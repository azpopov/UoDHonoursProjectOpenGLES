package arturpopov.basicprojectopengles.androidtemplate.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arturpopov.basicprojectopengles.R;

/**
 * Just dummy content. Nothing special.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class DummyContent {

    /**
     * An array of sample items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample items. Key: sample ID; Value: Item.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<>(6);

    static {
        addItem(new DummyItem("1", R.drawable.p1, "Grey Classic", "Grey Classic", "\n\nIn this scene, a manually tuned normal map is used alongside the traditional colours of smoke"));
        addItem(new DummyItem("2", R.drawable.p2, "Addition of Bias", "Addition of Bias","\nA demonstration of the effect particles exhibit when a variable direction bias is added to the particle simulation and a set of textures at an attempt to simulate the stencil shadow effect. "));
        addItem(new DummyItem("3", R.drawable.p3, "Uniform White Smoke", "Uniform White Smoke", "\nA departure from the usual colour scheme to make a more complex visual scene. The colour depth texture has been replace with a white filled RGB to produce an effect close to McGuire's"));
        addItem(new DummyItem("4", R.drawable.p4, "Yellow Generated Map", "Yellow Generated Map","\nAttempts to create a complex colour scheme with yellow and red as the colours of choice. Additionally, the unmodified generated normal map and the same colour depth texture as in option three was used."));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class DummyItem {
        public final String id;
        public final int photoId;
        public final String title;
        public final String author;
        public final String content;

        public DummyItem(String id, int photoId, String title, String author, String content) {
            this.id = id;
            this.photoId = photoId;
            this.title = title;
            this.author = author;
            this.content = content;
        }
    }
}
