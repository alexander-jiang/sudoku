package main.util;

import java.util.ArrayList;
import java.util.List;

public class SubsetHelper {
    public static <T> List<List<T>> listAllSubsets(List<T> sourceList) {
        // binary count
        int subsetIndex = 0B1;
        int maxIndex = 1 << sourceList.size();
        List<List<T>> subsets = new ArrayList<>();
        // empty set is always a subset
        subsets.add(new ArrayList<T>());

        while (subsetIndex < maxIndex) {
            List<T> newSubset = new ArrayList<>();
            // parse out the subset from the binary string representation of subsetIndex
            int listIndex = 0;
            int binaryRemainder = subsetIndex;
            while (binaryRemainder > 0) {
                if (binaryRemainder % 2 == 1) {
                    newSubset.add(sourceList.get(listIndex));
                }
                listIndex++;
                binaryRemainder = binaryRemainder / 2;
            }
            subsets.add(newSubset);
            subsetIndex++;
        }
        return subsets;
    }

    public static <T> List<List<T>> listAllSubsetsOfSize(List<T> list, int subsetSize) {
        return listAllSubsets(list).stream().filter(subset -> subset.size() == subsetSize).toList();
    }
}
