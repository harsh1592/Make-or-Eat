package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextArea search_Result;
    @FXML
    private TextField cuisine_txt, city_txt;
    @FXML
    private TextField recipe_txt;
    @FXML
    private Button recipe_btn, restaurant_btn;
    @FXML
    private ListView result_list, ingri_list, restaurant_listview;
    @FXML
    private Label source_lbl, label_lbl, rest_lbl, rest_add, snippet_lbl, phone_lbl;
    @FXML
    private Hyperlink url_lbl;
    @FXML
    private ImageView img_view, rest_img, star_view;
    @FXML
    private WebView map_view;

    private ArrayList<Recipe> recipeList;
    private ArrayList<Restaurant> restaurantList;
    private final ObservableList<String> data = FXCollections.observableArrayList();
    private final ObservableList<String> ingridata = FXCollections.observableArrayList();
    private final ObservableList<String> restaurantdata = FXCollections.observableArrayList();

    public void OnReviewButton(ActionEvent event) {

        List<Integer> selectedItemsCopy = new ArrayList<>(restaurant_listview.getSelectionModel().getSelectedItems());
        restaurant_listview.getItems().removeAll(selectedItemsCopy);
        restaurant_listview.getItems().clear();

        String consumerKey = "3fG6tlhJLNzOzA_lJ8dUVQ";
        String consumerSecret = "GPuXzEdP7kOrUleW3moc8-1F00M";
        String token = "ND4AmId2XY2UB0vazFYvow2WmIjzU2au";
        String tokenSecret = "DFDVROK8TehvKvFtPvRnGPtehys";

        YelpAPI yelp = new YelpAPI(consumerKey, consumerSecret, token, tokenSecret);
        String term = label_lbl.getText();
        term = term.replaceAll("", "+");
        String citytxt = city_txt.getText();
        String response = yelp.search(term, citytxt);

        System.out.println(response);

        JSONObject root = new JSONObject(response);
        JSONArray business = root.getJSONArray("businesses");

        Iterator<?> iterator = business.iterator();
        JSONObject a;
        restaurantList = new ArrayList<Restaurant>();
        while (iterator.hasNext()) {
            a = (JSONObject) iterator.next();
            String name = a.getString("name");
            String phone = a.getString("phone");
            String snippet = a.getString("snippet_text");
            String img_url = a.getString("image_url");
            String star_url = a.getString("rating_img_url_large");

            JSONObject location = a.getJSONObject("location");
            String city = location.getString("city");

            JSONArray addressArray = location.getJSONArray("display_address");

            ArrayList<String> addressLines = new ArrayList<>();
            String restAddress = "";
            for (Object item : addressArray) {
                restAddress += (String) item + ",";
            }
            restAddress.substring(0, restAddress.length() - 2);
            restaurantList.add(new Restaurant(name, restAddress, phone, city, star_url, snippet, img_url));
        }
        for (Restaurant b : restaurantList) {
            restaurantdata.add(b.name);
            System.out.println(b.name);
        }
        restaurant_listview.setItems(restaurantdata);
    }

    public void OnSearchButton(ActionEvent event) {
        String query = recipe_txt.getText();
        query = query.replaceAll(" ", "+");
        URL url;
        String str = null;

        List<Integer> selectedItemsCopy = new ArrayList<>(result_list.getSelectionModel().getSelectedItems());
        result_list.getItems().removeAll(selectedItemsCopy);
        result_list.getItems().clear();
        try {
            url = new URL("https://api.edamam.com/search?q=" + query +
                    "&from=0&to=20&app_id=66c92071&app_key=e47f38e53a08c75fe2caf9f6eb7dfe9f");
            //System.out.println(url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();

            while ((str = bufferReader.readLine()) != null) {
                stringBuffer.append(str);
                stringBuffer.append("\n");
            }
            str = stringBuffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(str);
        JSONObject root = new JSONObject(str);
        JSONArray itemsArray = root.getJSONArray("hits");

        Iterator<?> iterator = itemsArray.iterator();
        JSONObject a;
        recipeList = new ArrayList<Recipe>();
        while (iterator.hasNext()) {
            a = (JSONObject) iterator.next();
            JSONObject recipe = a.getJSONObject("recipe");
            String label = recipe.getString("label");
            String source = "By: " + recipe.getString("source");
            String recipeURL = recipe.getString("url");
            String image = recipe.getString("image");
            JSONArray ingredientLines = recipe.getJSONArray("ingredientLines");

            ArrayList<String> ingredientList = new ArrayList<>();
            for (Object item : ingredientLines) {
                ingredientList.add((String) item);
            }
            recipeList.add(new Recipe(label, source, recipeURL, image, ingredientList));
        }
        for (Recipe b : recipeList) {
            data.add(b.label);
        }
        result_list.setItems(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result_list.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                        cleanAll();
                        int index = result_list.getSelectionModel().getSelectedIndex();
                        List<Integer> selectedItemsCopy = new ArrayList<>(ingri_list.getSelectionModel().getSelectedItems());
                        ingri_list.getItems().removeAll(selectedItemsCopy);
                        ingri_list.getItems().clear();
                        label_lbl.setWrapText(true);
                        label_lbl.setText(recipeList.get(index).label);
                        source_lbl.setText(recipeList.get(index).source);
                        img_view.setImage(new Image(recipeList.get(index).image));
                        url_lbl.setWrapText(true);
                        url_lbl.setText(recipeList.get(index).url);

                        for (String b : recipeList.get(index).ingridientLines) {
                            ingridata.add(b);
                        }
                        ingri_list.setItems(ingridata);
                    }
                });

        restaurant_listview.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                        cleanAllRestData();
                        int index = restaurant_listview.getSelectionModel().getSelectedIndex();
                        rest_lbl.setText(restaurantList.get(index).name);
                        star_view.setImage(new Image(restaurantList.get(index).star_url));
                        rest_img.setImage(new Image(restaurantList.get(index).img_url));
                        rest_add.setWrapText(true);
                        rest_add.setText(restaurantList.get(index).address);
                        phone_lbl.setText(restaurantList.get(index).phone);
                        snippet_lbl.setWrapText(true);
                        snippet_lbl.setText(restaurantList.get(index).snippet_text);
                        callMapAPIP();
                    }
                });
    }

    private void callMapAPIP() {
        WebEngine webEngine = map_view.getEngine();
        int index = restaurant_listview.getSelectionModel().getSelectedIndex();
        String address = restaurantList.get(index).name;
        address = address + restaurantList.get(index).city;
        address = address.replaceAll(" ", "+");
        System.out.println(address);
        webEngine.loadContent("<iframe\n" +
                "  width=\"329\"\n" +
                "  height=\"271\"\n" +
                "  frameborder=\"0\" style=\"border:0\"\n" +
                "  src=\"https://www.google.com/maps/embed/v1/place?key=AIzaSyDTpl7uJnJYX6W02RvvIGzcVSoPddrW3po\n" +
                "    &q=" + address + "\" allowfullscreen>\n" +
                "</iframe>");
    }

    private void cleanAllRestData() {
        rest_lbl.setText("");
        phone_lbl.setText("");
        rest_add.setText("");
        snippet_lbl.setText("");
    }

    private void cleanAll() {
        label_lbl.setText("");
        source_lbl.setText("");
        url_lbl.setText("");
    }
}
