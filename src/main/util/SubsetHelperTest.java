package main.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SubsetHelperTest {

    @Test
    public void testListAllSubsetsExhaustive() throws Exception {
        List<String> sourceList = Arrays.asList("a", "b", "c");
        List<List<String>> subsets = SubsetHelper.listAllSubsets(sourceList);
        assertEquals(8, subsets.size());
        assertTrue(subsets.contains(Collections.EMPTY_LIST));
        assertTrue(subsets.contains(Collections.singletonList("a")));
        assertTrue(subsets.contains(Collections.singletonList("b")));
        assertTrue(subsets.contains(Collections.singletonList("c")));
        assertTrue(subsets.contains(Arrays.asList("a", "b")));
        assertTrue(subsets.contains(Arrays.asList("a", "c")));
        assertTrue(subsets.contains(Arrays.asList("b", "c")));
        assertTrue(subsets.contains(Arrays.asList("a", "b", "c")));
    }

    @Test
    public void testListAllSubsets() throws Exception {
        List<String> sourceList = Arrays.asList("a", "b", "c", "d", "e", "f");
        List<List<String>> subsets = SubsetHelper.listAllSubsets(sourceList);
        assertEquals(64, subsets.size());
        assertTrue(subsets.contains(Collections.singletonList("a")));
        assertTrue(subsets.contains(Arrays.asList("a", "b")));
        assertTrue(subsets.contains(Arrays.asList("a", "b", "c")));
        assertTrue(subsets.contains(Arrays.asList("a", "b", "c", "d")));
        assertTrue(subsets.contains(Arrays.asList("a", "b", "c", "d", "e")));
        assertTrue(subsets.contains(Arrays.asList("a", "b", "c", "d", "e", "f")));
    }

    @Test
    public void testListAllSubsetsOfSizeExhaustive() throws Exception {
        List<String> sourceList = Arrays.asList("a", "b", "c", "d");
        List<List<String>> subsets = SubsetHelper.listAllSubsets(sourceList);
        assertEquals(16, subsets.size());
        List<List<String>> pairs = SubsetHelper.listAllSubsetsOfSize(sourceList, 2);
        assertEquals(6, pairs.size());
        assertTrue(pairs.contains(Arrays.asList("a", "b")));
        assertTrue(pairs.contains(Arrays.asList("a", "c")));
        assertTrue(pairs.contains(Arrays.asList("a", "d")));
        assertTrue(pairs.contains(Arrays.asList("b", "c")));
        assertTrue(pairs.contains(Arrays.asList("b", "d")));
        assertTrue(pairs.contains(Arrays.asList("c", "d")));
    }
}