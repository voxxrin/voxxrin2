package crawlers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import crawlers.configuration.CrawlingConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import voxxrin2.domain.Speaker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BreizhCampCrawler extends CfpIOCrawler {
    
    public BreizhCampCrawler() {
        super("breizhcamp", ImmutableList.of("breizhcamp-publisher"));
    }

    @Override
    public List<Schedule> completeSchedules(List<Schedule> schedules) throws IOException {
        final String rawScheduleJSONResult = HttpRequest.get("http://www.breizhcamp.org/json/others.json")
                .body();
        final List<Schedule> othersSchedules = MAPPER.readValue(rawScheduleJSONResult, new TypeReference<List<Schedule>>(){});
        othersSchedules.addAll(schedules);
        return othersSchedules;
    }

    @Override
    protected List<Speaker> completeSpeakers(Map<String, Speaker> speakers) {
        Document root = Jsoup.parse(HttpRequest.get("http://www.breizhcamp.org/conference/speakers/").body(), Charsets.UTF_8.name());
        Elements items = root.select(".speaker-box");

        List completedSpeakers = new ArrayList();

        for (Element el : items) {
            final String avatarUrl = el.select(".speaker img.img-circle").attr("src");
            final String name = el.select(".speaker .media-body .media-heading").text();
            final String trimmedSpeaker = name.trim().toLowerCase();
            final String bio = el.select(".speaker > div:nth-child(2)").text();

            Speaker speaker = speakers.get(trimmedSpeaker);
            if (speaker != null) {
                speaker.setName(name);
                speaker.setAvatarUrl(avatarUrl);
                speaker.setBio(bio);
                completedSpeakers.add(speaker);
            }
        }

        return completedSpeakers;
    }
}
