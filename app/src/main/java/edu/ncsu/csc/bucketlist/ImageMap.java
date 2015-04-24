package edu.ncsu.csc.bucketlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A hashmap of <image tags, <associated images>> for buckets
 */
public class ImageMap {
    private HashMap<String, ArrayList<Integer>> hashMap;

    public ImageMap() {
        hashMap = new HashMap<String, ArrayList<Integer>>();
        // add pairs of tag, images
        hashMap.put("art", new ArrayList<Integer>(Arrays.asList(R.drawable.art_tiny, R.drawable.art)));
        hashMap.put("entertainment", new ArrayList<Integer>(Arrays.asList(R.drawable.entertainment_tiny, R.drawable.entertainment)));
        hashMap.put("food", new ArrayList<Integer>(Arrays.asList(R.drawable.food_tiny, R.drawable.food)));
        hashMap.put("kid", new ArrayList<Integer>(Arrays.asList(R.drawable.kid_tiny, R.drawable.kid)));
        hashMap.put("parks", new ArrayList<Integer>(Arrays.asList(R.drawable.parks_tiny, R.drawable.parks)));
        hashMap.put("shopping", new ArrayList<Integer>(Arrays.asList(R.drawable.shopping_tiny, R.drawable.shopping)));
        hashMap.put("sports", new ArrayList<Integer>(Arrays.asList(R.drawable.sports_tiny, R.drawable.sports)));
        hashMap.put("standard", new ArrayList<Integer>(Arrays.asList(R.drawable.standard_tiny, R.drawable.standard)));
    }

    public HashMap<String, ArrayList<Integer>> getHashMap() {
        return hashMap;
    }

}
