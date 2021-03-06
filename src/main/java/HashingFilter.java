import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HashingFilter {
    Set<Integer> paragraphHashes = Collections.synchronizedSet(new HashSet<>());

    boolean isNew(String paragraph) {
        int paraHashCode = paragraph.hashCode();

        if (!paragraphHashes.contains(paraHashCode)) {
            paragraphHashes.add(paraHashCode);
            return true;
        } else {
            return false;
        }
    }

}
