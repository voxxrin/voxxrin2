package voxxrin2.crawlers.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import voxxrin2.crawlers.AbstractHttpCrawler;
import voxxrin2.crawlers.CrawlingResult;
import voxxrin2.crawlers.HttpDataFiller;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Voxxrin1Crawler extends AbstractHttpCrawler {

    // 2015-10-16 09:00:00.0
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.S");
    private static final Function<Speaker, Reference<Speaker>> TO_SPEAKER_REF_FUNCTION = new Function<Speaker, Reference<Speaker>>() {
        @Override
        public Reference<Speaker> apply(Speaker speaker) {
            return Reference.of(Type.speaker, speaker.getKey());
        }
    };

    /**
     * Configure this area
     */
    private static final String EVENT_ID = "bdxio15";
    private static final String EVENT_LOCATION = "Talence - ENSEIRB-MATMECA";
    private static final String EVENT_IMAGE_URL = "http://www.bdx.io/images/./2ea70105.logo_header.png";
    private static final String DESTINATION_API_URL = "http://localhost:8080/api";

    /**
     * Static params
     */
    private static final String BASE_URL = "http://app.voxxr.in/r";
    private static final String EVENT_URL = BASE_URL + "/events/" + EVENT_ID;
    private static final String SPEAKERS_URL = EVENT_URL + "/speakers";
    private static final String DAYS_URL = EVENT_URL + "/day";

    public static void main(String[] args) throws IOException {
        CrawlingResult result = new Voxxrin1Crawler().crawl();
        new HttpDataFiller(DESTINATION_API_URL).fill(result);
    }

    @Override
    protected CrawlingResult crawl() throws IOException {

        VoxxrinEvent voxxrinEvent = MAPPER.readValue(HttpRequest.get(EVENT_URL).body(), VoxxrinEvent.class);
        Event stdEvent = voxxrinEvent.toStdEvent();
        CrawlingResult crawlingResult = new CrawlingResult(stdEvent);

        Map<String, Room> rooms = new HashMap<>();
        Map<String, Speaker> speakers = new HashMap<>();

        for (VoxxrinDay voxxrinDay : voxxrinEvent.days) {
            Day stdDay = voxxrinDay.toStdDay(stdEvent);
            VoxxrinDaySchedules schedules = MAPPER.readValue(HttpRequest.get(DAYS_URL + "/" + voxxrinDay.id).body(), VoxxrinDaySchedules.class);
            for (VoxxrinPresentation presentation : schedules.schedules) {
                List<Speaker> stdSpeakers = registerSpeakers(speakers, presentation);
                Room stdRoom = registerRoom(rooms, presentation);
                crawlingResult.getPresentations().add(presentation.toStdPresentation(stdEvent, stdDay, stdRoom, toSpeakerRefs(stdSpeakers)));
            }
            crawlingResult.getDays().add(stdDay);
        }

        for (Room room : rooms.values()) {
            crawlingResult.getRooms().add(room);
        }

        for (Speaker speaker : speakers.values()) {
            crawlingResult.getSpeakers().add(speaker);
        }

        return crawlingResult;
    }

    private ImmutableList<Reference<Speaker>> toSpeakerRefs(List<Speaker> stdSpeakers) {
        return ImmutableList.copyOf(Iterables.transform(stdSpeakers, TO_SPEAKER_REF_FUNCTION));
    }

    private Room registerRoom(Map<String, Room> rooms, VoxxrinPresentation presentation) {
        if (!rooms.containsKey(presentation.room.id)) {
            rooms.put(presentation.room.id, presentation.room.toStdRoom());
        }
        return rooms.get(presentation.room.id);
    }

    private List<Speaker> registerSpeakers(Map<String, Speaker> speakers, VoxxrinPresentation presentation) throws IOException {

        List<Speaker> stdSpeakers = new ArrayList<>();

        for (VoxxrinSpeaker speaker : presentation.speakers) {
            Speaker cachedSpeaker = speakers.get(speaker.id);
            if (cachedSpeaker == null) {
                VoxxrinSpeaker fullSpeaker = MAPPER.readValue(HttpRequest.get(SPEAKERS_URL + "/" + speaker.id).body(), VoxxrinSpeaker.class);
                Speaker stdSpeaker = fullSpeaker.toStdSpeaker();
                stdSpeakers.add(stdSpeaker);
                speakers.put(speaker.id, stdSpeaker);
            } else {
                stdSpeakers.add(cachedSpeaker);
            }
        }

        return stdSpeakers;
    }

    private static class VoxxrinEvent {

        public String id;
        public DateTime from;
        public DateTime to;
        public String title;
        public String description;
        public List<VoxxrinDay> days;

        public Event toStdEvent() {
            return (Event) new Event()
                    .setName(title)
                    .setLocation(EVENT_LOCATION)
                    .setImageUrl(EVENT_IMAGE_URL)
                    .setDescription(description)
                    .setTo(to)
                    .setFrom(from)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class VoxxrinDay {

        public String id;

        public String name;

        public Day toStdDay(Event event) {
            Reference<Event> eventRef = Reference.of(Type.event, event.getKey());
            return (Day) new Day()
                    .setEvent(eventRef)
                    .setName(name)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class VoxxrinDaySchedules {

        @JsonProperty("schedule")
        public List<VoxxrinPresentation> schedules;

    }

    private static class VoxxrinPresentation {

        public String summary;
        public String fromTime;
        public String toTime;
        public String type;
        public String kind;
        public String title;
        public String id;

        public List<VoxxrinSpeaker> speakers;
        public VoxxrinRoom room;

        public Presentation toStdPresentation(Event event, Day day, Room room, List<Reference<Speaker>> speakers) {
            return (Presentation) new Presentation()
                    .setTitle(title)
                    .setExternalId(id)
                    .setSummary(summary)
                    .setDay(Reference.<Day>of(Type.day, day.getKey()))
                    .setEvent(Reference.<Event>of(Type.event, event.getKey()))
                    .setLocation(Reference.<Room>of(Type.room, room.getKey()))
                    .setSpeakers(speakers)
                    .setFrom(DATE_TIME_FORMATTER.parseDateTime(fromTime))
                    .setTo(DATE_TIME_FORMATTER.parseDateTime(toTime))
                    .setKind(type)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class VoxxrinSpeaker {

        public String id;
        public String pictureURI;
        public String bio;
        public String name;
        @JsonProperty("__lastName")
        public String lastName;
        @JsonProperty("__firstName")
        public String firstName;
        @JsonProperty("__company")
        public String company;
        @JsonProperty("__twitter")
        public String twitterId;

        public Speaker toStdSpeaker() {
            return (Speaker) new Speaker()
                    .setAvatarUrl(BASE_URL + pictureURI)
                    .setBio(bio)
                    .setCompany(company)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setTwitterId(twitterId)
                    .setKey(new ObjectId().toString());
        }
    }

    private static class VoxxrinRoom {

        public String id;

        public String name;

        public Room toStdRoom() {
            return (Room) new Room()
                    .setName(name)
                    .setFullName(name)
                    .setKey(new ObjectId().toString());
        }

    }
}
