import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusRouteManager {
    public static final String breakline = "+++++++++++++++++++++++++++++++++++";
    public static String URLText = "";
    public static BusRouteQuery queryManager;

    public static void main(String[] args) {
        queryManager = new BusRouteQuery();
        while(BusRoute()) {}
        System.out.println("Exiting...");
    }

    public static boolean BusRoute() {
        boolean keepRunning = false;

        System.out.println();
        String destination = AskForInput("Please enter a letter that your destinations start with: ");
        ArrayList<String> results = queryManager.QueryForDestination(destination);
        ArrayList<String> resultNumber = new ArrayList<String>();
        for(int i=0; i < results.size(); i++) {
            ArrayList<String> resultCity = queryManager.QueryForCity(results.get(i));
            ArrayList<String> tempResultNumber = queryManager.QueryForRouteNumber(results.get(i));
            DisplayRouteResults(resultCity.get(0), tempResultNumber);
            resultNumber.addAll(tempResultNumber);
        }

        if (results.size() > 0) {
            String route = AskForInput("Please enter a route ID as a string: ");
            DisplayRoutePath(route.trim(), resultNumber);
        }
        else {
            System.out.println(String.format("Failed to find a route to a city that starts with ", destination));
        }


        System.out.println();
        String again = AskForInput("Would you like to find another route?(Y/N): ");
        again = again.trim().toLowerCase();
        if (again.equals("y")) {
            keepRunning = true;
        }
        return keepRunning;
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
                String routeURL = queryManager.GenerateRouteURL(routeNumber);
                System.out.println();
                System.out.println(String.format("The link for your route is: %s", routeURL));
                System.out.println();

                ArrayList<String> stopList = queryManager.QueryForListOfStops(routeNumber);
                for (String stop : stopList) {
                    ArrayList<String> stopOneDirection = queryManager.QueryForStops(stop);
                    ArrayList<String> stopOneDirectionName = queryManager.QueryForStopName(stop);
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
}
