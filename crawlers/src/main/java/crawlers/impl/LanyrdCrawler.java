package crawlers.impl;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.configuration.CrawlingConfiguration;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import voxxrin2.domain.*;
import voxxrin2.domain.technical.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LanyrdCrawler extends AbstractHttpCrawler {

    private static final String BASE_URL = "http://lanyrd.com";
    private static final Pattern PROFILE_URL_PATTERN = Pattern.compile("^[/]profile[/](.+)[/]$");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss'+ZZ:ZZ'");
    private static final DateTimeFormatter DAY_NAME_FORMATTER = DateTimeFormat.forPattern("dd/MM/YYYY");

    public LanyrdCrawler(String id, List<String> roles) {
        super(id, roles);
    }

    @Override
    public CrawlingResult crawl(CrawlingConfiguration configuration) throws IOException {

        String eventUrl = BASE_URL + String.format("/%s/schedule/", configuration.getExternalEventRef());
        Document root = Jsoup.parse(HttpRequest.get(eventUrl).body(), Charsets.UTF_8.name());

        Event event = (Event) new Event()
                .setName(root.select(".url.summary").text())
                .setKey(new ObjectId().toString());

        CrawlingResult result = new CrawlingResult(event);

        Map<DateTime, Day> allDays = new HashMap<>();
        Map<String, Room> allRooms = new HashMap<>();
        Map<String, Speaker> allSpeakers = new HashMap<>();

        Elements items = root.select(".schedule-item");
        for (Element el : items) {

            Presentation presentation = buildPresentation(event, el);

            DateTime start = DATE_TIME_FORMATTER.parseDateTime(el.select(".schedule-meta .dtstart > span").attr("title"));
            DateTime end = DATE_TIME_FORMATTER.parseDateTime(el.select(".schedule-meta .dtend > span").attr("title"));
            presentation
                    .setFrom(start)
                    .setTo(end);

            extractDay(allDays, presentation, start, event);

            Elements scheduleMetas = el.select(".schedule-meta p");
            if (scheduleMetas.size() > 1) {
                extractRoom(allRooms, presentation, scheduleMetas.get(1).text());
            } else {
                extractRoom(allRooms, presentation, "-");
            }

            extractSpeakers(allSpeakers, presentation, el.select(".session-speakers > a"));

            result.getPresentations().add(presentation);
        }

        result.getSpeakers().addAll(allSpeakers.values());
        result.getRooms().addAll(allRooms.values());
        result.getDays().addAll(allDays.values());

        // Compute some extra data
        setEventTemporalLimits(result);

        return result;
    }

    protected Presentation buildPresentation(Event event, Element el) {
        return new Presentation()
                .setTitle(el.select("h2 > a").first().text())
                .setSummary(el.select(".desc").html())
                .setEvent(Reference.<Event>of(Type.event, event.getKey()))
                .setKind("Talk")
                .setExternalId(el.select("h2 > a").first().attr("href"));
    }

    private void extractDay(Map<DateTime, Day> allDays, Presentation presentation, DateTime start, Event event) {
        DateTime startOfDay = start.withTimeAtStartOfDay();
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

    private void extractRoom(Map<String, Room> allRooms, Presentation presentation, String roomName) {
        Room room = allRooms.get(roomName);
        if (room == null) {
            room = (Room) new Room().setName(roomName).setFullName(roomName).setKey(new ObjectId().toString());
            allRooms.put(roomName, room);
        }
        presentation.setLocation(Reference.<Room>of(Type.room, room.getKey()));
    }

    private void extractSpeakers(Map<String, Speaker> allSpeakers, Presentation presentation, Elements sessionSpeakers) {
        presentation.setSpeakers(new ArrayList<Reference<Speaker>>());
        for (Element sessionSpeaker : sessionSpeakers) {
            String href = sessionSpeaker.attr("href");
            Optional<Speaker> registeredSpeaker = findOrRegisterSpeaker(href, allSpeakers);
            if (registeredSpeaker.isPresent()) {
                presentation.getSpeakers().add(Reference.<Speaker>of(Type.speaker, registeredSpeaker.get().getKey()));
            }
        }
    }

    @Override
    public CrawlingResult setup(CrawlingResult result, CrawlingConfiguration configuration) {
        Event event = result.getEvent();
        event.setLocation(configuration.getLocation());
        event.setImageUrl(configuration.getImageUrl());
        return super.setup(result, configuration);
    }

    private Optional<Speaker> findOrRegisterSpeaker(String profileLink, Map<String, Speaker> allSpeakers) {

        Matcher matcher = PROFILE_URL_PATTERN.matcher(profileLink);
        if (!matcher.matches()) {
            return Optional.absent();
        }
        String username = matcher.group(1);

        Speaker speaker = allSpeakers.get(username);
        if (speaker == null) {

            // Download full resolution image (if exists)
            Document profilePage = Jsoup.parse(HttpRequest.get(BASE_URL + profileLink).body(), Charsets.UTF_8.name());
            String avatarUrl = profilePage.select(".avatar > a > img").attr("src");

            Document bioPage = Jsoup.parse(HttpRequest.get(BASE_URL + profileLink + "bio").body(), Charsets.UTF_8.name());
            String name = bioPage.select(".people .name").text();
            String bio = bioPage.select(".tagline").text();
            String twitter = bioPage.select(".people .handle").text();
            if (Strings.isNullOrEmpty(avatarUrl)) {
                avatarUrl = bioPage.select(".avatar img").attr("src");
            }

            Speaker newSpeaker = (Speaker) new Speaker()
                    .setName(name)
                    .setBio(bio)
                    .setTwitterId(twitter)
                    .setAvatarUrl(avatarUrl)
                    .setKey(new ObjectId().toString());

            allSpeakers.put(username, newSpeaker);

            return Optional.of(newSpeaker);
        } else {
            return Optional.of(speaker);
        }
    }
}
