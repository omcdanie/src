package utils;

import java.awt.Color;
import java.util.HashMap;

public class ColorMaker {
    
    private static final HashMap<String, Color> colors = new HashMap<>();
    
    static {
        colors.put("BLUE", Color.BLUE);
        colors.put("CYAN", Color.CYAN);
        colors.put("GRAY", Color.GRAY);
        colors.put("GREEN", Color.GREEN);
        colors.put("MAGENTA", Color.MAGENTA);
        colors.put("ORANGE", Color.ORANGE);
        colors.put("PINK", Color.PINK);
        colors.put("RED", Color.RED);
        colors.put("WHITE", Color.WHITE);
        colors.put("YELLOW", Color.YELLOW);
        
        colors.put("MAROON", new Color(128, 0, 0));
        colors.put("OLIVE", new Color(128, 128, 0));
        colors.put("PURPLE", new Color(128, 0, 128));
        colors.put("TEAL", new Color(0, 128, 128));
        colors.put("GOLD", new Color(255, 215, 0));
        colors.put("SKYBLUE", new Color(135, 206, 250));
        colors.put("VIOLET", new Color(75, 0, 130));
        colors.put("BROWN", new Color(139, 69, 19));
        colors.put("TAN", new Color(210, 180, 140));
    }
    
   public static String getColorName(Color c) {
   for (String name : colors.keySet()) {
      if (colors.get(name).equals(c))
         return name;
     }
     return "UNKNOWN";
}
    
    public static Color makeColor(String name) {
        if (colors.containsKey(name))
            return colors.get(name);
        
        return Color.GRAY;
    }
    
    public static String randomColorName() {
        String[] c = colors.keySet().toArray(new String[colors.size()]);
        return c[(int) (Math.random() * c.length)];
    }
    
    public static String[] getSupportedColors() {
        String[] c = colors.keySet().toArray(new String[colors.size()]);
        return c;
    }
}
