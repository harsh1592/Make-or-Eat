package sample;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelpAPI {
    OAuthService serviceOBJ;
    Token accessTokenOBJ;

    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.serviceOBJ = new ServiceBuilder().provider(YelpAPI2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessTokenOBJ = new Token(token, tokenSecret);
    }

    public String search(String foodterm, String pincode) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", foodterm);
        request.addQuerystringParameter("location", pincode);
        request.addQuerystringParameter("radius_filter", "20000");

        this.serviceOBJ.signRequest(this.accessTokenOBJ, request);
        Response response = request.send();
        return response.getBody();
    }
}