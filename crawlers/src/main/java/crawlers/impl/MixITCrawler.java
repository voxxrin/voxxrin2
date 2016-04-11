package crawlers.impl;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.configuration.CrawlingConfiguration;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import restx.factory.Component;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MixITCrawler extends AbstractHttpCrawler {

    private static final String BASE_URL = "https://www.mix-it.fr/api";
    private static final String SESSIONS_URL = "/session/";
    private static final String SPEAKER_URL = "/member/";
    private static final DateTimeFormatter FMT = DateTimeFormat.forPattern("dd/MM/yyyy");

    public MixITCrawler() {
        super("mxt", ImmutableList.of("mxt-publisher"));
    }

    @Override
    public CrawlingResult crawl(CrawlingConfiguration configuration) throws IOException {

        CollectionType type = TypeFactory.defaultInstance().constructCollectionType(List.class, MixITSession.class);
        List<MixITSession> sessions = MAPPER.readValue(GET(BASE_URL + SESSIONS_URL).body(), type);

        final Map<String, Room> rooms = new HashMap<>();
        final Map<Integer, Speaker> speakers = new HashMap<>();
        final Map<String, Day> days = new HashMap<>();

        Event event = (Event) new Event()
                .setName(configuration.getEventName())
                .setEventId(configuration.getEventId())
                .setLocation(configuration.getLocation())
                .setImageUrl(configuration.getImageUrl())
                .setKey(new ObjectId().toString());

        Reference<Event> eventRef = Reference.of(Type.event, event.getKey());
        CrawlingResult result = new CrawlingResult(event);

        for (MixITSession session : sessions) {
            if (session.start == null) {
                continue;
            }
            for (MixITSpeaker speaker : session.speakers) {
                MixITSpeaker fullSpeaker = MAPPER.readValue(GET(BASE_URL + SPEAKER_URL + speaker.idMember).body(), MixITSpeaker.class);
                if (!speakers.containsKey(fullSpeaker.idMember)) {
                    speakers.put(fullSpeaker.idMember, fullSpeaker.toStdSpeaker());
                }
            }
            if (!rooms.containsKey(session.room) && session.room != null) {
                rooms.put(session.room, toStdRoom(session));
            }
            String dayKey = dayKey(session);
            if (!days.containsKey(dayKey)) {
                days.put(dayKey, toStdDay(session).setEvent(eventRef));
            }
        }

        result.getRooms().addAll(rooms.values());
        result.getSpeakers().addAll(speakers.values());
        result.getDays().addAll(days.values());

        for (MixITSession session : sessions) {
            if (session.start == null) {
                continue;
            }
            Room room = rooms.get(session.room);
            Day day = days.get(dayKey(session));
            List<Reference<Speaker>> prezSpeakers = FluentIterable
                    .from(session.speakers)
                    .transform(new Function<MixITSpeaker, Reference<Speaker>>() {
                        @Override
                        public Reference<Speaker> apply(MixITSpeaker input) {
                            Speaker speaker = speakers.get(input.idMember);
                            return Reference.of(Type.speaker, speaker.getKey());
                        }
                    })
                    .toList();
            result.getPresentations().add(
                    session.toStdPresentation()
                            .setExternalId(configuration.getExternalEventRef())
                            .setEvent(eventRef)
                            .setDay(Reference.<Day>of(Type.day, day.getKey()))
                            .setSpeakers(prezSpeakers)
                            .setLocation(room != null ? Reference.<Room>of(Type.room, room.getKey()) : null)
            );
        }

        setEventTemporalLimits(result);

        return result;
    }

    private HttpRequest GET(String url) {
        return new HttpRequest(url, "GET").trustAllCerts().trustAllHosts();
    }

    private String dayKey(MixITSession session) {
        return FMT.print(session.start) + "-" + FMT.print(session.end);
    }

    private Day toStdDay(MixITSession session) {
        return (Day) new Day()
                .setName(FMT.print(session.start))
                .setDate(session.start.withTimeAtStartOfDay())
                .setKey(new ObjectId().toString());
    }

    private Room toStdRoom(MixITSession session) {
        return (Room) new Room()
                .setFullName(session.room)
                .setName(session.room)
                .setKey(new ObjectId().toString());
    }

    private static class MixITSession {
        public int id;
        public String lang;
        public String format;
        public String title;
        public String summary;
        public String description;
        public String room;
        public DateTime start;
        public DateTime end;
        public List<MixITSpeaker> speakers;

        private Presentation toStdPresentation() {
            return (Presentation) new Presentation()
                    .setKind(format)
                    .setExternalId(String.valueOf(id))
                    .setTitle(title)
                    .setSummary(description)
                    .setFrom(start)
                    .setTo(end)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class MixITSpeaker {
        public int idMember;
        public String firstname;
        public String lastname;
        public String longDescription;
        public String company;
        public String logo;
        public List<Link> userLinks;

        public Speaker toStdSpeaker() {
            Optional<Link> twitter = Iterables.tryFind(userLinks, new Predicate<Link>() {
                @Override
                public boolean apply(Link input) {
                    return "twitter".equalsIgnoreCase(input.key);
                }
            });
            String twitterId = twitter.isPresent() && twitter.get().value != null ?
                    twitter.get().value.replace("https://twitter.com/", "").replace("http://twitter.com/", "") : null;
            return (Speaker) new Speaker()
                    .setName(firstname + " " + lastname)
                    .setTwitterId(twitterId)
                    .setFirstName(firstname)
                    .setLastName(lastname)
                    .setAvatarUrl(logo)
                    .setBio(longDescription)
                    .setCompany(company)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class Link {
        public String key;
        public String value;
    }
}
