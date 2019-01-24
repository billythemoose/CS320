import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusRouteQuery {

    public final String destinationNameRegex = "<h3>(.*?)</h3>";
    public final String destinationAllRegex = "<hr id=\"(%s.*?)\" />(.*?)</h3>(\r\n|\\s|\t)+(<div class=\"row Community\">(\r\n|\\s|\t)+<div(.*?)</div>(\r\n|\\s|\t)+</div>(\r\n|\\s|\t)+)+";
    public final String routeNumberRegex = "<div(.*?)<div(.*?)a href=\"/schedules/route/(.*?)\"";
    public final String routeDestination = "<div class=\"col-xs-9 col-xs-offset-1\">(.*?)$";
    public final String allStopRegex = "<h2>Weekday<small>(.*?)</tr>(\r\n|\\s|\t)+<tr>(.*?)(<p>(.*?)</p>)*?(.*?)</tr>";

    private final String URLbase = "https://www.communitytransit.org/busservice/schedules/";
    private final String URLroute = "https://www.communitytransit.org/busservice/schedules/route/%s";

    private String URLText;

    public BusRouteQuery() {
        URLText = PullURLText(URLbase);
    }

    public String PullURLText(String URL) {
        String urlResponse = "";
        try {
            URLConnection connection = new URL(URL).openConnection();
            connection.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = "";
            while ((inputLine = input.readLine()) != null) {
                urlResponse += inputLine + "\n";
            }

            input.close();
        }
        catch (Exception e) {
            System.out.println("Failed to connect to URL");
            System.out.println(e.getMessage());
        }

        return urlResponse;
    }

    public ArrayList<String> QueryForDestination(String destination) {
        String formatted = String.format(destinationAllRegex, destination.toLowerCase());
        ArrayList<String> results = PatternMatch(formatted, 0, URLText);
        return results;
    }

    public ArrayList<String> QueryForCity(String cityInformation) {
        ArrayList<String> resultCity = PatternMatch(destinationNameRegex, 1, cityInformation);
        return resultCity;
    }

    public ArrayList<String> QueryForRouteNumber(String routeInformation) {
        ArrayList<String> resultNumber = PatternMatch(routeNumberRegex, 3, routeInformation);
        return resultNumber;
    }

    public ArrayList<String> QueryForRouteStopList(String stopInformation) {
        ArrayList<String> stopList = PatternMatch(allStopRegex, 0, stopInformation);
        return stopList;
    }



    public String GenerateRouteURL(String routeNumber) {
        return String.format(URLroute, routeNumber);
    }

    public ArrayList<String> QueryForListOfStops(String routeNumber) {
        String routeURL = this.GenerateRouteURL(routeNumber);
        String routeURLText = PullURLText(routeURL);
        ArrayList<String> stopList = PatternMatch(allStopRegex, 0, routeURLText);
        return stopList;
    }

    public ArrayList<String> QueryForStops(String stopList) {
        String singleStopRegex = "<p>(.*?)</p>";
        ArrayList<String> stopOneDirection = PatternMatch(singleStopRegex, 1, stopList);
        return stopOneDirection;
    }

    public ArrayList<String> QueryForStopName(String stopList) {
        String stopDirectionRegex = "<small>(.*?)</small>";
        ArrayList<String> stopOneDirectionName = PatternMatch(stopDirectionRegex, 1, stopList);
        return stopOneDirectionName;
    }

    public ArrayList<String> PatternMatch(String regexPattern, int grouping, String searchText) {
        ArrayList<String> results = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(searchText);
        while (matcher.find()) {
            String found = matcher.group(grouping);
            results.add(found);
        }

        return results;
    }
}
