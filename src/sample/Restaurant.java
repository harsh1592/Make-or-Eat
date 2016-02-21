package sample;

/**
 * Created by patil on 2/19/2016.
 */
public class Restaurant {
    String name;
    String address;
    String phone;
    String city;
    String star_url;
    String snippet_text;
    String img_url;

    public Restaurant(String name, String address, String phone, String city, String star_url, String snippet_text, String img_url) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.star_url = star_url;
        this.img_url = img_url;
        this.snippet_text = snippet_text;
    }
}
