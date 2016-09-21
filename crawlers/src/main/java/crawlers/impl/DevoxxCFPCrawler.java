package crawlers.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.configuration.CrawlingConfiguration;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class DevoxxCFPCrawler extends AbstractHttpCrawler {

    private static final Logger logger = getLogger(DevoxxCFPCrawler.class);
    private final String baseUrl;

    public DevoxxCFPCrawler(String id, List<String> roles, String baseUrl) {
        super(id, roles);
        this.baseUrl = baseUrl;
    }

    @Override
    public CrawlingResult crawl(CrawlingConfiguration configuration) throws IOException {

        String eventUrl = baseUrl + configuration.getExternalEventRef();
        String roomsUrl = eventUrl + "/rooms";
        String speakersUrl = eventUrl + "/speakers";
        String daysUrl = eventUrl + "/schedules";

        CFPEvent cfpEvent = MAPPER.readValue(httpGet(eventUrl, configuration).body(), CFPEvent.class);
        List<CFPLinks> cfpSpeakerLinks = MAPPER.readValue(httpGet(speakersUrl, configuration).body(), buildCollectionType(CFPLinks.class));
        CFPLinks cfpDayLinks = MAPPER.readValue(httpGet(daysUrl, configuration).body(), CFPLinks.class);

        CFPRooms cfpRooms = new CFPRooms();
        try {
            cfpRooms = MAPPER.readValue(httpGet(roomsUrl, configuration).body(), CFPRooms.class);
        } catch (JsonParseException e) {
            logger.warn("Unable to retrieve rooms");
        }

        CrawlingResult crawlingResult = new CrawlingResult(cfpEvent.toStdEvent());
        // Rooms
        crawlRooms(cfpRooms, crawlingResult);
        // Speakers
        crawlSpeakers(cfpSpeakerLinks, crawlingResult, configuration);
        // Schedules
        crawlSchedules(cfpDayLinks, crawlingResult, configuration);

        // Compute some extra data
        setEventTemporalLimits(crawlingResult);

        return crawlingResult;
    }

    private HttpRequest httpGet(String url, CrawlingConfiguration configuration) {
        return HttpRequest.get(configuration.isForceHttps() && !url.contains("https://") ? url.replaceAll("http", "https") : url)
                .trustAllCerts()
                .trustAllHosts();
    }

    @Override
    public CrawlingResult setup(CrawlingResult result, CrawlingConfiguration configuration) {
        Event event = result.getEvent();
        if (configuration.getLocation() != null) {
            event.setLocation(configuration.getLocation());
        }
        if (configuration.getImageUrl() != null) {
            event.setImageUrl(configuration.getImageUrl());
        }
        return super.setup(result, configuration);
    }

    protected void crawlRooms(CFPRooms cfpRooms, CrawlingResult crawlingResult) {
        for (CFPRoom room : cfpRooms.rooms) {
            crawlingResult.getRooms().add(room.toStdRoom());
        }
    }

    protected void crawlSpeakers(List<CFPLinks> cfpSpeakerLinks, CrawlingResult crawlingResult, CrawlingConfiguration configuration) throws IOException {
        for (CFPLinks speakerLink : cfpSpeakerLinks) {
            CFPLink link = Iterables.getFirst(speakerLink.links, null);
            if (link == null) {
                logger.error("link not found");
            } else {
                CFPSpeaker cfpSpeaker = MAPPER.readValue(httpGet(link.href, configuration).body(), CFPSpeaker.class);
                crawlingResult.getSpeakers().add(cfpSpeaker.toStdSpeaker());
            }
        }
    }

    protected void crawlSchedules(CFPLinks cfpDayLink, CrawlingResult result, CrawlingConfiguration configuration) throws IOException {
        for (CFPLink dayLink : cfpDayLink.links) {
            try {
                CFPDay cfpDay = MAPPER.readValue(httpGet(dayLink.href, configuration).body(), CFPDay.class);
                crawlDay(result, dayLink, cfpDay, configuration);
            } catch (Exception e) {
                logger.warn("Unable to retrieve info about day from {}", dayLink.href);
            }
        }
    }

    protected void crawlDay(CrawlingResult result, CFPLink dayLink, CFPDay cfpDay, CrawlingConfiguration configuration) {
        Day stdDay = cfpDay.toStdDay(result.getEvent());
        if (cfpDay.slots == null || cfpDay.slots.isEmpty()) {
            logger.error("empty slots here {}", dayLink.href);
        } else {
            crawlSlots(cfpDay.slots, stdDay, result, configuration);
            result.getDays().add(stdDay);
        }
    }

    protected void crawlSlots(List<CFPSlot> slots, Day currentDay, CrawlingResult result, CrawlingConfiguration configuration) {
        for (final CFPSlot slot : slots) {
            Optional<Room> room = findRoom(result, slot);
            if (!room.isPresent()) {
                logger.error("No room found {}", slot.roomId);
                continue;
            }
            if (slot.justABreak == null && slot.talk == null) {
                continue;
            }
            List<Reference<Speaker>> speakers = new ArrayList<>();
            if (slot.talk != null && slot.talk.speakers != null) {
                for (CFPTalkSpeaker cfpSpeaker : slot.talk.speakers) {
                    String href = cfpSpeaker.link.href;
                    final String uuid = href.substring(href.lastIndexOf("/") + 1);
                    Optional<Speaker> speaker = findSpeaker(result, uuid);
                    if (!speaker.isPresent()) {
                        // Sometime, speaker has not been registered into CFP speakers repository
                        // and are referenced ONLY from a slot => handling this case by retrieving "manually"
                        // the speaker following HREF
                        Speaker registeredSpeaker = addUnregisteredSpeaker(result, cfpSpeaker, href, configuration);
                        if (registeredSpeaker != null) {
                            speakers.add(Reference.<Speaker>of(Type.speaker, registeredSpeaker.getKey()));
                        }
                    } else {
                        speakers.add(Reference.<Speaker>of(Type.speaker, speaker.get().getKey()));
                    }
                }
            }
            result.getPresentations().add(slot.toStdPresentation(currentDay, result.getEvent(), room.get(), speakers));
        }
    }

    private Speaker addUnregisteredSpeaker(CrawlingResult result, CFPTalkSpeaker cfpSpeaker, String href, CrawlingConfiguration configuration) {
        logger.warn("Speaker {} not registered, trying to follow the link.", cfpSpeaker.link.href);
        try {
            CFPSpeaker speakerToRegister = MAPPER.readValue(httpGet(href, configuration).body(), CFPSpeaker.class);
            Speaker stdSpeaker = speakerToRegister.toStdSpeaker();
            result.getSpeakers().add(stdSpeaker);
            return stdSpeaker;
        } catch (IOException e) {
            logger.warn("Definitely the speaker {} has not been found... aborting.", cfpSpeaker.link.href);
        }
        return null;
    }

    private Optional<Speaker> findSpeaker(CrawlingResult result, final String uuid) {
        return Iterables.tryFind(result.getSpeakers(), new Predicate<Speaker>() {
            @Override
            public boolean apply(Speaker input) {
                return uuid.equals(input.getUuid());
            }
        });
    }

    private Optional<Room> findRoom(CrawlingResult result, final CFPSlot slot) {
        return Iterables.tryFind(result.getRooms(), new Predicate<Room>() {
            @Override
            public boolean apply(Room input) {
                return input.getName().equals(slot.roomId);
            }
        });
    }

    protected static class CFPLink {
        public String href;
    }

    protected static class CFPLinks {
        public List<CFPLink> links;
    }

    protected static class CFPEvent {
        public String label;
        public String localisation;
        public String eventCode;

        public Event toStdEvent() {
            return (Event) new Event()
                    .setDescription(label)
                    .setLocation(localisation)
                    .setName(eventCode)
                    .setKey(new ObjectId().toString());
        }
    }

    protected static class CFPRooms {
        public List<CFPRoom> rooms = new ArrayList<>();
    }

    protected static class CFPRoom {
        public String id;
        public String name;

        public Room toStdRoom() {
            return (Room) new Room()
                    .setFullName(name)
                    .setName(id)
                    .setKey(new ObjectId().toString());
        }
    }

    protected static class CFPSpeaker {
        public String uuid;
        public String firstName;
        public String lastName;
        public String twitter;
        public String avatarURL;
        public String company;
        public String bio;

        public Speaker toStdSpeaker() {
            return (Speaker) new Speaker()
                    .setAvatarUrl(avatarURL)
                    .setBio(bio)
                    .setCompany(company)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setName(firstName + " " + lastName)
                    .setTwitterId(twitter)
                    .setUuid(uuid)
                    .setKey(new ObjectId().toString());
        }
    }

    protected static class CFPDay {
        public List<CFPSlot> slots;

        public Day toStdDay(Event event) {
            Reference<Event> eventRef = Reference.of(Type.event, event.getKey());
            CFPSlot slot = Iterables.getFirst(slots, null);
            String name = "";
            if (slot != null) {
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
                name = fmt.print(new DateTime(slot.fromTimeMillis));
            }
            return (Day) new Day().setEvent(eventRef).setName(name).setKey(new ObjectId().toString());
        }
    }

    protected static class CFPSlot {
        public String roomId;
        public String roomName;
        public long fromTimeMillis;
        public long toTimeMillis;
        public CFPTalk talk;
        @JsonProperty("break")
        public CFPBreak justABreak;

        public Presentation toStdPresentation(Day day, Event event, Room room, List<Reference<Speaker>> speakers) {
            return new Presentation()
                    .setDay(Reference.<Day>of(Type.day, day.getKey()))
                    .setSpeakers(speakers)
                    .setEvent(Reference.<Event>of(Type.event, event.getKey()))
                    .setLocation(Reference.<Room>of(Type.room, room.getKey()))
                    .setFrom(new DateTime(fromTimeMillis))
                    .setTo(new DateTime(toTimeMillis))
                    .setExternalId(talk != null ? talk.id : null)
                    .setKind(talk != null ? talk.talkType : null)
                    .setSummary(talk != null ? talk.summary : null)
                    .setTitle(talk != null ? talk.title : justABreak.nameFR);
        }
    }

    protected static class CFPTalk {
        public String title;
        public String summary;
        public String talkType;
        public String id;
        public List<CFPTalkSpeaker> speakers;
    }

    protected static class CFPBreak {
        public String nameEN;
        public String nameFR;
    }

    protected static class CFPTalkSpeaker {
        public CFPLink link;
    }
}
