import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;

public class LruCacheTest {

    private static final String MONTHS[] = new String[] {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "Novenber", "December"};
    private static final int CAPACITY = 4;

    @Test
    public void putAndGetMoreThenCapacity() throws Exception {
        LruCache<String, Integer> monthsCache = new LruCache<>(CAPACITY);

        for (int i = 0; i < MONTHS.length; i++) {
            monthsCache.put(MONTHS[i], i + 1);
        }

        for (int i = 0; i < MONTHS.length - CAPACITY; i++) {
            Assert.that(monthsCache.get(MONTHS[i]) == null, "Old elements should not present in cache!");
        }

        for (int i = MONTHS.length - CAPACITY; i < MONTHS.length; i++) {
            Assert.that(monthsCache.get(MONTHS[i]) == i+1, "New elements should present in cache!");
        }
    }

    @Test
    public void putAndGetEqualCapacity() throws Exception {
        LruCache<String, Integer> monthsCache = new LruCache<>(CAPACITY);

        for (int i = 0; i < CAPACITY; i++) {
            monthsCache.put(MONTHS[i], i + 1);
        }

        for (int i = 0; i < CAPACITY; i++) {
            Assert.that(monthsCache.get(MONTHS[i]) == i+1, "New elements should present in cache!");
        }
    }

    @Test
    public void putAndChangeElements() throws Exception {
        LruCache<String, Integer> monthsCache = new LruCache<>(CAPACITY);

        for (int i = 0; i < CAPACITY; i++) {
            monthsCache.put(MONTHS[i], i + 1);
        }

        int changedIndex = 2;
        monthsCache.put(MONTHS[changedIndex], 5); // Now March is 5
        Assert.that(monthsCache.get(MONTHS[changedIndex]).equals(5), "Value is not updated!");

        for (int i = 0; i < CAPACITY; i++) {
            Assert.that(i == changedIndex || monthsCache.get(MONTHS[i]) == i+1,
                    "Other elements should stay the same!");
        }
    }

    @Test
    public void emptyCache() throws Exception {
        LruCache<String, Integer> monthsCache = new LruCache<>(CAPACITY);

        for (int i = 0; i < CAPACITY; i++) {
            Assert.that(monthsCache.get(MONTHS[i]) == null, "Elements should not present in cache!");
        }
    }

}