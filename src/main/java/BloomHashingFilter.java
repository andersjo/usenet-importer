import orestes.bloomfilter.BloomFilter;
import orestes.bloomfilter.FilterBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BloomHashingFilter extends HashingFilter {
    BloomFilter<String> paragraphBloomFilter;
    public BloomHashingFilter() {
        // With an expected 100M elements we allow a false positive rate of 0.1 %
        paragraphBloomFilter = new FilterBuilder(100_000_000, 0.001).buildBloomFilter();
        System.out.println("Bloom filter has size " + paragraphBloomFilter.getSize()
                + " with " + paragraphBloomFilter.getHashes() + " hash functions");
    }

    @Override
    boolean isNew(String paragraph) {
        if (paragraphBloomFilter.contains(paragraph)) {
            return false;
        } else {
            paragraphBloomFilter.add(paragraph);
            return true;
        }
    }
}
