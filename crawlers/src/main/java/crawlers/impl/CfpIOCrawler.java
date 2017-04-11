package crawlers.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import voxxrin2.domain.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kevinsawicki.http.HttpRequest;

import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.configuration.CrawlingConfiguration;
import voxxrin2.domain.technical.Reference;

public abstract class CfpIOCrawler extends AbstractHttpCrawler {
    
    public static final String API_URL = "https://api.cfp.io/api";
    private static final DateTimeFormatter DAY_NAME_FORMATTER = DateTimeFormat.forPattern("dd/MM/YYYY");

    public CfpIOCrawler(String id, List<String> roles) {
        super(id, roles);
    }

    protected abstract List<Schedule> completeSchedules(List<Schedule> schedules) throws IOException;

    protected abstract List<Speaker> completeSpeakers(Map<String, Speaker> speakers);

    protected abstract String getEventName(final CrawlingConfiguration configuration);

    @Override
    public CrawlingResult crawl(final CrawlingConfiguration configuration) throws IOException {

        final List<Schedule> schedules = getSchedules(configuration.getExternalEventRef());
        
        final Event event = (Event) new Event()
                .setName(getEventName(configuration))
                .setKey(new ObjectId().toString());

        final CrawlingResult result = new CrawlingResult(event);

        Map<DateTime, Day> allDays = new HashMap<>();
        Map<String, Room> allRooms = new HashMap<>();
        Map<String, Speaker> allSpeakers = new HashMap<>();

        for (Schedule schedule : schedules) {

            Presentation presentation = new Presentation()
                    .setTitle(schedule.name)
                    .setSummary(schedule.description)
                    .setEvent(Reference.<Event>of(Type.event, event.getKey()))
                    .setKind(schedule.format)
                    .setFrom(schedule.eventStart)
                    .setTo(schedule.eventEnd);

            extractSpeakers(allSpeakers, presentation, schedule.speakers);
            extractRooms(allRooms, presentation, schedule.venueId, schedule.venue);
            extractDay(allDays, presentation, schedule.eventStart, event);

            result.getPresentations().add(presentation);
        }
        
        List<Speaker> speakers = completeSpeakers(allSpeakers);

        result.getRooms().addAll(allRooms.values());
        result.getSpeakers().addAll(speakers);
        result.getDays().addAll(allDays.values());
    
        return result;
    }

    private List<Schedule> getSchedules(final String externalEventRef) throws IOException {
        final String scheduleUrl = API_URL + "/schedule";
        final String rawScheduleJSONResult = HttpRequest.get(scheduleUrl)
                                                    .header("X-Tenant-Id", externalEventRef)
                                                    .body();
        final List<Schedule> apiSchedules = MAPPER.readValue(rawScheduleJSONResult, new TypeReference<List<Schedule>>(){});
        return completeSchedules(ImmutableList.copyOf(apiSchedules));
    }

    private void extractDay(Map<DateTime, Day> allDays, Presentation presentation, DateTime eventStart, Event event) {
        DateTime startOfDay = eventStart.withTimeAtStartOfDay();
        Day day = allDays.get(startOfDay);
        if (day == null) {
            day = (Day) new Day().setDate(startOfDay)
                    .setEvent(Reference.<Event>of(Type.event, event.getKey()))
                    .setName(DAY_NAME_FORMATTER.print(startOfDay))
                    .setKey(new ObjectId().toString());
            allDays.put(startOfDay, day);
        }
        presentation.setDay(Reference.<Day>of(Type.day, day.getKey()));
    }

    private void extractRooms(Map<String, Room> allRooms, Presentation presentation, String venueId, String venue) {
        Room room = allRooms.get(venueId);
        if (room == null) {
            room = (Room) new Room().setName(venue).setFullName(venue).setKey(new ObjectId().toString());
            allRooms.put(venueId, room);
        }
        presentation.setLocation(Reference.<Room>of(Type.room, room.getKey()));
    }

    private void extractSpeakers(Map<String, Speaker> allSpeakers, Presentation presentation, String speakers) {
        if (speakers == null)
            return;

        presentation.setSpeakers(new ArrayList<Reference<Speaker>>());
        List<String> s = Arrays.asList(speakers.split(","));
        for (String speaker : s) {
            String trimmedSpeaker = speaker.trim().toLowerCase();
            if ("".equals(trimmedSpeaker))
                break;

            if (!allSpeakers.containsKey(trimmedSpeaker)) {

                Speaker stdSpeaker = (Speaker) new Speaker()
                        .setName(trimmedSpeaker)
                        .setKey(new ObjectId().toString());

                presentation.getSpeakers().add(Reference.<Speaker>of(Type.speaker, stdSpeaker.getKey()));

                allSpeakers.put(trimmedSpeaker, stdSpeaker);
            } else {
                presentation.getSpeakers().add(Reference.<Speaker>of(Type.speaker, trimmedSpeaker));
            }
        }

    }

    public static class Schedule {
        public String active;
        public String name;
        @JsonProperty("event_start")
        public DateTime eventStart;
        @JsonProperty("event_end")
        public DateTime eventEnd;
        public String eventType;
        public String format;
        public String venue;
        @JsonProperty("venue_id")
        public String venueId;
        public String speakers;
        public String description;
    }

}
