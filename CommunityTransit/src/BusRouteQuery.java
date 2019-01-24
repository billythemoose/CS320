import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class that handles querying of route data for the Community Transit
public class BusRouteQuery {
    // Main text from the community transit site
    private String URLText;

    // Constructor
    public BusRouteQuery() {
        String URLbase = "https://www.communitytransit.org/busservice/schedules/";
        URLText = PullURLText(URLbase);
    }

    // Returns the HTML text from a given URL
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

    // Returns possible destinations that start with a given letter
    public ArrayList<String> QueryForDestination(String destination) {
        String destinationAllRegex = "<hr id=\"(%s.*?)\" />(.*?)</h3>(\r\n|\\s|\t)+(<div class=\"row Community\">(\r\n|\\s|\t)+<div(.*?)</div>(\r\n|\\s|\t)+</div>(\r\n|\\s|\t)+)+";
        String formatted = String.format(destinationAllRegex, destination.toLowerCase());
        ArrayList<String> results = PatternMatch(formatted, 0, URLText);
        return results;
    }

    // Returns a specific city name pulled from unorganized city information
    public ArrayList<String> QueryForCity(String cityInformation) {
        String destinationNameRegex = "<h3>(.*?)</h3>";
        ArrayList<String> resultCity = PatternMatch(destinationNameRegex, 1, cityInformation);
        return resultCity;
    }

    // Returns a single route number from unorganized city information
    public ArrayList<String> QueryForRouteNumber(String routeInformation) {
        String routeNumberRegex = "<div(.*?)<div(.*?)a href=\"/schedules/route/(.*?)\"";
        ArrayList<String> resultNumber = PatternMatch(routeNumberRegex, 3, routeInformation);
        return resultNumber;
    }

    public ArrayList<String> QueryForRouteStopList(String stopInformation) {
        String allStopRegex = "<h2>Weekday<small>(.*?)</tr>(\r\n|\\s|\t)+<tr>(.*?)(<p>(.*?)</p>)*?(.*?)</tr>";
        ArrayList<String> stopList = PatternMatch(allStopRegex, 0, stopInformation);
        return stopList;
    }

    // Returns URL for a specific route given a route number
    public String GenerateRouteURL(String routeNumber) {
        String URLroute = "https://www.communitytransit.org/busservice/schedules/route/%s";
        return String.format(URLroute, routeNumber);
    }

    // Returns a list of unorganized route stop information
    public ArrayList<String> QueryForListOfStops(String routeNumber) {
        String allStopRegex = "<h2>Weekday<small>(.*?)</tr>(\r\n|\\s|\t)+<tr>(.*?)(<p>(.*?)</p>)*?(.*?)</tr>";
        String routeURL = this.GenerateRouteURL(routeNumber);
        String routeURLText = PullURLText(routeURL);
        ArrayList<String> stopList = PatternMatch(allStopRegex, 0, routeURLText);
        return stopList;
    }

    // Returns a list of stops in one direction
    public ArrayList<String> QueryForStops(String stopList) {
        String singleStopRegex = "<p>(.*?)</p>";
        ArrayList<String> stopOneDirection = PatternMatch(singleStopRegex, 1, stopList);
        return stopOneDirection;
    }

    // Returns the name of a single stop
    public ArrayList<String> QueryForStopName(String stopList) {
        String stopDirectionRegex = "<small>(.*?)</small>";
        ArrayList<String> stopOneDirectionName = PatternMatch(stopDirectionRegex, 1, stopList);
        return stopOneDirectionName;
    }

    // Returns an ArrayList of results from a provided string and regex expression
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
