package crawlers.impl;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import crawlers.CrawlingResult;
import org.bson.types.ObjectId;
import voxxrin2.domain.Room;

import java.util.List;

public class PartialDevoxxCFPCrawler extends DevoxxCFPCrawler {

    public PartialDevoxxCFPCrawler(String id, List<String> roles, String baseUrl) {
        super(id, roles, baseUrl);
    }

    @Override
    protected void crawlDay(CrawlingResult result, DevoxxCFPCrawler.CFPLink dayLink, DevoxxCFPCrawler.CFPDay cfpDay) {
        populateRooms(result, cfpDay);
        super.crawlDay(result, dayLink, cfpDay);
    }

    /**
     * Rooms are not ALWAYS properly provided by the Devoxx CFP API, so we need to build a rooms list based
     * on roomName attributes found into a slot
     */
    private void populateRooms(CrawlingResult result, CFPDay cfpDay) {

        for (final CFPSlot slot : cfpDay.slots) {
            Optional<Room> registeredRoom = Iterables.tryFind(result.getRooms(), new Predicate<Room>() {
                @Override
                public boolean apply(Room input) {
                    return input.getName().equals(slot.roomId);
                }
            });
            if (!registeredRoom.isPresent()) {
                result.getRooms().add(
                        (Room) new Room()
                                .setFullName(slot.roomName)
                                .setName(slot.roomId)
                                .setKey(new ObjectId().toString())
                );
            }
        }
    }
}
