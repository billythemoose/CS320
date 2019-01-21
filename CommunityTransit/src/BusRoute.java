import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class BusRoute {
    public static void main(String[] args) {
        System.out.println("beans");
    }

    public static String PullURLText() {
        String urlText = "";
        try {
            URLConnection connection = new URL("https://www.communitytransit.org/busservice/schedules/").openConnection();
            connection.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = "";
            while ((inputLine = input.readLine()) != null) {
                urlText += inputLine + "\n";
            }

            input.close();
        }
        catch (Exception e) {
            System.out.println("Failed to connect to URL");
            System.out.println(e.getMessage());
        }

        return urlText;
    }
}
