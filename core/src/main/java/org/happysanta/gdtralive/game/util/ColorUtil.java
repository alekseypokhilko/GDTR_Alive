package org.happysanta.gdtralive.game.util;

import org.happysanta.gdtralive.game.api.model.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColorUtil {
    public static Map<String, Color> colors = new LinkedHashMap<>();
    public static String[] colorNames;

    static {
        //https://cssgenerator.org/rgba-and-hex-color-generator.html
        colors.put("lime", new Color(0, 255, 0));
        colors.put("blue", new Color(0, 0, 255));
        colors.put("red", new Color(255, 0, 0));
        colors.put("black", new Color(0, 0, 0));
        colors.put("gray", new Color(128, 128, 128));
        colors.put("white", new Color(255, 255, 255));
        colors.put("perspective", new Color(150, 150, 150));
        colors.put("325", new Color(100, 100, 255));
        colors.put("progress", new Color(41, 170, 39));
        colors.put("progressBG", new Color(196, 196, 196));
        colors.put("fork", new Color(228, 228, 228));
        colors.put("head", new Color(156, 0, 0));

        colors.put("maroon", new Color(128, 0, 0));
        colors.put("darkred", new Color(139, 0, 0));
        colors.put("brown", new Color(165, 42, 42));
        colors.put("firebrick", new Color(178, 34, 34));
        colors.put("crimson", new Color(220, 20, 60));
        colors.put("tomato", new Color(255, 99, 71));
        colors.put("coral", new Color(255, 127, 80));
        colors.put("indianred", new Color(205, 92, 92));
        colors.put("lightcoral", new Color(240, 128, 128));
        colors.put("darksalmon", new Color(233, 150, 122));
        colors.put("salmon", new Color(250, 128, 114));
        colors.put("lightsalmon", new Color(255, 160, 122));
        colors.put("orangered", new Color(255, 69, 0));
        colors.put("darkorange", new Color(255, 140, 0));
        colors.put("orange", new Color(255, 165, 0));
        colors.put("gold", new Color(255, 215, 0));
        colors.put("darkgoldenrod", new Color(184, 134, 11));
        colors.put("goldenrod", new Color(218, 165, 32));
        colors.put("palegoldenrod", new Color(238, 232, 170));
        colors.put("darkkhaki", new Color(189, 183, 107));
        colors.put("khaki", new Color(240, 230, 140));
        colors.put("olive", new Color(128, 128, 0));
        colors.put("yellow", new Color(255, 255, 0));
        colors.put("yellowgreen", new Color(154, 205, 50));
        colors.put("darkolivegreen", new Color(85, 107, 47));
        colors.put("olivedrab", new Color(107, 142, 35));
        colors.put("lawngreen", new Color(124, 252, 0));
        colors.put("chartreuse", new Color(127, 255, 0));
        colors.put("greenyellow", new Color(173, 255, 47));
        colors.put("darkgreen", new Color(0, 100, 0));
        colors.put("green", new Color(0, 128, 0));
        colors.put("forestgreen", new Color(34, 139, 34));
        colors.put("limegreen", new Color(50, 205, 50));
        colors.put("lightgreen", new Color(144, 238, 144));
        colors.put("palegreen", new Color(152, 251, 152));
        colors.put("darkseagreen", new Color(143, 188, 143));
        colors.put("mediumspringgreen", new Color(0, 250, 154));
        colors.put("springgreen", new Color(0, 255, 127));
        colors.put("seagreen", new Color(46, 139, 87));
        colors.put("mediumaquamarine", new Color(102, 205, 170));
        colors.put("mediumseagreen", new Color(60, 179, 113));
        colors.put("lightseagreen", new Color(32, 178, 170));
        colors.put("darkslate", new Color(47, 79, 79));
        colors.put("teal", new Color(0, 128, 128));
        colors.put("darkcyan", new Color(0, 139, 139));
        colors.put("aqua", new Color(0, 255, 255));
        colors.put("cyan", new Color(0, 255, 255));
        colors.put("lightcyan", new Color(224, 255, 255));
        colors.put("darkturquoise", new Color(0, 206, 209));
        colors.put("turquoise", new Color(64, 224, 208));
        colors.put("mediumturquoise", new Color(72, 209, 204));
        colors.put("paleturquoise", new Color(175, 238, 238));
        colors.put("aquamarine", new Color(127, 255, 212));
        colors.put("powderblue", new Color(176, 224, 230));
        colors.put("cadetblue", new Color(95, 158, 160));
        colors.put("steelblue", new Color(70, 130, 180));
        colors.put("cornflowerblue", new Color(100, 149, 237));
        colors.put("deepskyblue", new Color(0, 191, 255));
        colors.put("dodgerblue", new Color(30, 144, 255));
        colors.put("lightblue", new Color(173, 216, 230));
        colors.put("skyblue", new Color(135, 206, 235));
        colors.put("lightskyblue", new Color(135, 206, 250));
        colors.put("midnightblue", new Color(25, 25, 112));
        colors.put("navy", new Color(0, 0, 128));
        colors.put("darkblue", new Color(0, 0, 139));
        colors.put("mediumblue", new Color(0, 0, 205));
        colors.put("royalblue", new Color(65, 105, 225));
        colors.put("blueviolet", new Color(138, 43, 226));
        colors.put("indigo", new Color(75, 0, 130));
        colors.put("darkslateblue", new Color(72, 61, 139));
        colors.put("slateblue", new Color(106, 90, 205));
        colors.put("mediumslateblue", new Color(123, 104, 238));
        colors.put("mediumpurple", new Color(147, 112, 219));
        colors.put("darkmagenta", new Color(139, 0, 139));
        colors.put("darkviolet", new Color(148, 0, 211));
        colors.put("darkorchid", new Color(153, 50, 204));
        colors.put("mediumorchid", new Color(186, 85, 211));
        colors.put("purple", new Color(128, 0, 128));
        colors.put("thistle", new Color(216, 191, 216));
        colors.put("plum", new Color(221, 160, 221));
        colors.put("violet", new Color(238, 130, 238));
        colors.put("magenta", new Color(255, 0, 255));
        colors.put("orchid", new Color(218, 112, 214));
        colors.put("mediumvioletred", new Color(199, 21, 133));
        colors.put("palevioletred", new Color(219, 112, 147));
        colors.put("deeppink", new Color(255, 20, 147));
        colors.put("hotpink", new Color(255, 105, 180));
        colors.put("lightpink", new Color(255, 182, 193));
        colors.put("pink", new Color(255, 192, 203));
        colors.put("antiquewhite", new Color(250, 235, 215));
        colors.put("beige", new Color(245, 245, 220));
        colors.put("bisque", new Color(255, 228, 196));
        colors.put("blanchedalmond", new Color(255, 235, 205));
        colors.put("wheat", new Color(245, 222, 179));
        colors.put("cornsilk", new Color(255, 248, 220));
        colors.put("lemonchiffon", new Color(255, 250, 205));
        colors.put("lightgoldenrodyellow", new Color(250, 250, 210));
        colors.put("lightyellow", new Color(255, 255, 224));
        colors.put("saddlebrown", new Color(139, 69, 19));
        colors.put("sienna", new Color(160, 82, 45));
        colors.put("chocolate", new Color(210, 105, 30));
        colors.put("peru", new Color(205, 133, 63));
        colors.put("sandybrown", new Color(244, 164, 96));
        colors.put("burlywood", new Color(222, 184, 135));
        colors.put("tan", new Color(210, 180, 140));
        colors.put("rosybrown", new Color(188, 143, 143));
        colors.put("moccasin", new Color(255, 228, 181));
        colors.put("navajowhite", new Color(255, 222, 173));
        colors.put("peachpuff", new Color(255, 218, 185));
        colors.put("mistyrose", new Color(255, 228, 225));
        colors.put("lavenderblush", new Color(255, 240, 245));
        colors.put("linen", new Color(250, 240, 230));
        colors.put("oldlace", new Color(253, 245, 230));
        colors.put("papayawhip", new Color(255, 239, 213));
        colors.put("seashell", new Color(255, 245, 238));
        colors.put("mintcream", new Color(245, 255, 250));
        colors.put("slategray", new Color(112, 128, 144));
        colors.put("lightslategray", new Color(119, 136, 153));
        colors.put("lightsteelblue", new Color(176, 196, 222));
        colors.put("lavender", new Color(230, 230, 250));
        colors.put("floralwhite", new Color(255, 250, 240));
        colors.put("aliceblue", new Color(240, 248, 255));
        colors.put("ghostwhite", new Color(248, 248, 255));
        colors.put("honeydew", new Color(240, 255, 240));
        colors.put("ivory", new Color(255, 255, 240));
        colors.put("azure", new Color(240, 255, 255));
        colors.put("snow", new Color(255, 250, 250));
        colors.put("dimgray", new Color(105, 105, 105));
        colors.put("darkgray", new Color(169, 169, 169));
        colors.put("silver", new Color(192, 192, 192));
        colors.put("lightgray", new Color(211, 211, 211));
        colors.put("gainsboro", new Color(220, 220, 220));
        colors.put("whitesmoke", new Color(245, 245, 245));

        colorNames = colors.keySet().toArray(new String[0]);
    }

    public static int indexOf(Color color) {
        for (Map.Entry<String, Color> entry : colors.entrySet()) {
            if (entry.getValue().equals(color)) {
                for (int i = 0; i < colorNames.length; i++) {
                    if (colorNames[i].equals(entry.getKey())) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

}
