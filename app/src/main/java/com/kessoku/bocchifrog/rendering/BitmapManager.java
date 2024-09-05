package com.kessoku.bocchifrog.rendering;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

// singleton Bitmap Manager for easy access to textures in-game via id
public class BitmapManager {
    private static final HashMap<Integer, Bitmap> BITMAP_MAP = new HashMap<>();
    private static Resources resources = null;

    // Must initialize before using `getById`
    public static void useResources(Resources resources) {
        BitmapManager.resources = resources;
    }

    public static Bitmap getById(int id) {
        if (resources == null) {
            return null;
        }

        if (!BITMAP_MAP.containsKey(id)) {
            BITMAP_MAP.put(id, BitmapFactory.decodeResource(resources, id));
        }

        return BITMAP_MAP.get(id);
    }
}
