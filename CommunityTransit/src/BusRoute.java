import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusRoute {
    public static final String breakline = "+++++++++++++++++++++++++++++++++++";
    public static String URLText = "";

    public static void main(String[] args) {
        URLText = PullURLText("https://www.communitytransit.org/busservice/schedules/");
        String destination = AskForInput("Please enter a letter that your destinations start with: ");
        // String destinationHeader = String.format("<hr id=\"(%s.*?)\" />", destination.toLowerCase());
        String destinationName = "<h3>(.*?)</h3>";
        // String destinationFirstOnly = String.format("<hr id=\"(%s.*?)\" />(.*?)</div>(\r\n|\\s|\t)+</div>", destination.toLowerCase());
        String destinationAll = String.format("<hr id=\"(%s.*?)\" />(.*?)</h3>(\r\n|\\s|\t)+(<div class=\"row Community\">(\r\n|\\s|\t)+<div(.*?)</div>(\r\n|\\s|\t)+</div>(\r\n|\\s|\t)+)+", destination.toLowerCase());
        String routeNumber = "<div(.*?)<div(.*?)a href=\"/schedules/route/(.*?)\"";
        String routeDestination = "<div class=\"col-xs-9 col-xs-offset-1\">(.*?)$";
        ArrayList<String> results = PatternMatch(destinationAll, 0, URLText);

        ArrayList<String> resultRoute = new ArrayList<String>();
        ArrayList<String> resultNumber = new ArrayList<String>();
        for(int i=0; i < results.size(); i++) {
            ArrayList<String> resultCity = PatternMatch(destinationName, 1, results.get(i));
            ArrayList<String> tempResultNumber = (PatternMatch(routeNumber, 3, results.get(i)));
            resultRoute = PatternMatch(routeDestination, 1, results.get(i));
            DisplayRouteResults(resultCity.get(0), tempResultNumber);
            resultNumber.addAll(tempResultNumber);
        }

        String route = AskForInput("Please enter a route ID as a string: ");
        DisplayRoutePath(route.trim(), resultNumber);
    }

    public static String PullURLText(String URL) {
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

    public static String AskForInput(String query) {
        System.out.print(query);
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        return input;
    }

    public static void DisplayRoutePath(String routeNumber, ArrayList<String> possibleRoutes) {
        boolean foundRoute = false;
        for (String route : possibleRoutes) {
            if (route.equals(routeNumber)){
                foundRoute = true;
                String routeURL = String.format("https://www.communitytransit.org/busservice/schedules/route/%s", routeNumber);
                System.out.println();
                System.out.println(String.format("The link for your route is: %s", routeURL));
                System.out.println();

                String routeURLText = PullURLText(routeURL);
                String allStopRegex = "<h2>Weekday<small>(.*?)</tr>(\r\n|\\s|\t)+<tr>(.*?)(<p>(.*?)</p>)*?(.*?)</tr>";
                String singleStopRegex = "<p>(.*?)</p>";
                String stopDirectionRegex = "<small>(.*?)</small>";
                ArrayList<String> stopList = PatternMatch(allStopRegex, 0, routeURLText);
                for (String s : stopList) {
                    ArrayList<String> stopOneDirection = PatternMatch(singleStopRegex, 1, s);
                    ArrayList<String> stopOneDirectionName = PatternMatch(stopDirectionRegex, 1, s);
                    // System.out.println(s);
                    for (String direction : stopOneDirectionName) {
                        System.out.println(direction);
                        for (int i = 0; i < stopOneDirection.size(); i++) {
                            String stopResult = String.format("Stop number %1$s: %2$s", (i+1), stopOneDirection.get(i).replace("&amp;", "&"));
                            System.out.println(stopResult);
                        }

                        System.out.println(breakline);
                    }
                }
            }
        }

        if (!foundRoute) {
            System.out.println(String.format("Failed to find route %s in search results.", routeNumber));
        }
    }

    public static void DisplayRouteResults(String city, ArrayList<String> routeNumbers) {
        System.out.println(String.format("Destination: %s", city));
        for (String number : routeNumbers) {
            System.out.println(String.format("Bus Number: %s", number));
        }

        System.out.println(breakline);
    }

    public static ArrayList<String> PatternMatch(String regexPattern, int grouping, String searchText) {
        ArrayList<String> results = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regexPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(searchText);
        while (matcher.find()) {
            String found = matcher.group(grouping);
            results.add(found);
            // System.out.println(found);
        }

        return results;
    }

    // Destination Header  <hr id="(B.*?)/><h3>(.*?)</h3>
    // Route Number <div(.*?)<div(.*?)a href="/schedules/route/(.*?)".*?</div>
    // Route Destination <div class="col-xs-9 col-xs-offset-1">(.*?)</div></div>

        /*<hr id="bellevue" />
            <h3>Bellevue</h3>
                <div class="row Community">
                    <div class="col-xs-2">
                        <strong><a href="/schedules/route/532-535" class=&quot;text-success&quot;>532/535</a></strong>
                    </div>
                    <div class="col-xs-9 col-xs-offset-1">Lynnwood to Bellevue</div>
                </div>
       */

}
