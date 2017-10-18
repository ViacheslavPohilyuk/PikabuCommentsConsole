import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mac on 18.10.17.
 */
public class ConnectionWebsiteTest {
    @Test
    public void checkConnectionToPikabu() {
        String geocodeUrl = "https://pikabu.ru";
        String geocodeErrorMessage = "Error creating HTTP connection to the Pikabu.ru";
        assert (assertHttpConn(geocodeUrl, geocodeErrorMessage));
    }

    private boolean assertHttpConn(String strUrl, String errorMessage) {
        boolean isOkConn = false;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(strUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            isOkConn = HttpURLConnection.HTTP_OK == urlConn.getResponseCode();
        } catch (IOException e) {
            System.err.println(errorMessage);
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
        return isOkConn;
    }
}
