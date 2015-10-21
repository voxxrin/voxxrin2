package crawlers.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.HttpDataFiller;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DevoxxCFPCrawler extends AbstractHttpCrawler {

    private static final Logger logger = getLogger(DevoxxCFPCrawler.class);

    /**
     * Configure this area
     */
    private static final String EVENT_CODE = "DevoxxFR2015";
    private static final String BASE_URL = "http://cfp.devoxx.fr/api/conferences/" + EVENT_CODE;
    private static final String EVENT_LOGO_URL = "http://www.devoxx.fr/wp-content/uploads/2014/02/devoxx_france_150px.png";
    private static final String DESTINATION_API_URL = "http://localhost:8080/api";

    private static final String ROOMS_URL = BASE_URL + "/rooms";
    private static final String SPEAKERS_URL = BASE_URL + "/speakers";
    private static final String DAYS_URL = BASE_URL + "/schedules";

    public static void main(String[] args) throws IOException {
        CrawlingResult result = new DevoxxCFPCrawler().crawl();
        new HttpDataFiller(DESTINATION_API_URL).fill(result);
    }

    @Override
    public CrawlingResult crawl() throws IOException {

        CFPEvent cfpEvent = MAPPER.readValue(HttpRequest.get(BASE_URL).body(), CFPEvent.class);
        CFPRooms cfpRooms = MAPPER.readValue(HttpRequest.get(ROOMS_URL).body(), CFPRooms.class);
        List<CFPLinks> cfpSpeakerLinks = MAPPER.readValue(HttpRequest.get(SPEAKERS_URL).body(), buildCollectionType(CFPLinks.class));
        CFPLinks cfpDayLinks = MAPPER.readValue(HttpRequest.get(DAYS_URL).body(), CFPLinks.class);

        CrawlingResult crawlingResult = new CrawlingResult(cfpEvent.toStdEvent());
        // Rooms
        crawlRooms(cfpRooms, crawlingResult);
        // Speakers
        crawlSpeakers(cfpSpeakerLinks, crawlingResult);
        // Schedules
        crawlSchedules(cfpDayLinks, crawlingResult);

        // Compute some extra data
        setEventTemporalLimits(crawlingResult);

        return crawlingResult;
    }

    private void crawlRooms(CFPRooms cfpRooms, CrawlingResult crawlingResult) {
        for (CFPRoom room : cfpRooms.rooms) {
            crawlingResult.getRooms().add(room.toStdRoom());
        }
    }

    private void crawlSpeakers(List<CFPLinks> cfpSpeakerLinks, CrawlingResult crawlingResult) throws IOException {
        for (CFPLinks speakerLink : cfpSpeakerLinks) {
            CFPLink link = Iterables.getFirst(speakerLink.links, null);
            if (link == null) {
                logger.error("link not found");
            } else {
                CFPSpeaker cfpSpeaker = MAPPER.readValue(HttpRequest.get(link.href).body(), CFPSpeaker.class);
                crawlingResult.getSpeakers().add(cfpSpeaker.toStdSpeaker());
            }
        }
    }

    private void crawlSchedules(CFPLinks cfpDayLink, CrawlingResult result) throws IOException {
        for (CFPLink dayLink : cfpDayLink.links) {
            CFPDay cfpDay = MAPPER.readValue(HttpRequest.get(dayLink.href).body(), CFPDay.class);
            Day stdDay = cfpDay.toStdDay(result.getEvent());
            if (cfpDay.slots == null || cfpDay.slots.isEmpty()) {
                logger.error("empty slots here {}", dayLink.href);
            } else {
                crawlSlots(cfpDay.slots, stdDay, result);
                result.getDays().add(stdDay);
            }
        }
    }

    private void crawlSlots(List<CFPSlot> slots, Day currentDay, CrawlingResult result) {
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
                    if (speaker.isPresent()) {
                        speakers.add(Reference.<Speaker>of(Type.speaker, speaker.get().getKey()));
                    }
                }
            }
            result.getPresentations().add(slot.toStdPresentation(currentDay, result.getEvent(), room.get(), speakers));
        }
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

    private void setEventTemporalLimits(CrawlingResult crawlingResult) {

        DateTime from = null;
        DateTime to = null;

        for (Presentation presentation : crawlingResult.getPresentations()) {

            if (from == null) {
                from = presentation.getFrom();
            }
            if (to == null) {
                to = presentation.getTo();
            }
            if (presentation.getFrom().isBefore(from)) {
                from = presentation.getFrom();
            }
            if (presentation.getTo().isAfter(from)) {
                to = presentation.getTo();
            }
        }

        crawlingResult.getEvent()
                .setFrom(from)
                .setTo(to);
    }

    private static class CFPLink {
        public String href;
    }

    private static class CFPLinks {
        public List<CFPLink> links;
    }

    private static class CFPEvent {
        public String label;
        public String localisation;
        public String eventCode;

        public Event toStdEvent() {
            return (Event) new Event()
                    .setImageUrl(EVENT_LOGO_URL)
                    .setDescription(label)
                    .setLocation(localisation)
                    .setName(eventCode)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class CFPRooms {
        public List<CFPRoom> rooms;
    }

    private static class CFPRoom {
        public String id;
        public String name;

        public Room toStdRoom() {
            return (Room) new Room()
                    .setFullName(name)
                    .setName(id)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class CFPSpeaker {
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

    private static class CFPDay {
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

    private static class CFPSlot {
        public String roomId;
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

    private static class CFPTalk {
        public String title;
        public String summary;
        public String talkType;
        public String id;
        public List<CFPTalkSpeaker> speakers;
    }

    private static class CFPBreak {
        public String nameEN;
        public String nameFR;
    }

    private static class CFPTalkSpeaker {
        public CFPLink link;
    }
}
