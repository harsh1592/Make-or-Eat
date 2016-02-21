package sample;

import java.util.ArrayList;

/**
 * Created by patil on 2/19/2016.
 */
public class Recipe {
    public String label;
    public String source;
    public String url;
    public String image;
    public ArrayList<String> ingridientLines;

    public Recipe(String label, String source, String url, String image, ArrayList<String> ingridientLines) {
        this.label = label;
        this.source = source;
        this.url = url;
        this.image = image;
        this.ingridientLines = ingridientLines;
    }

}
