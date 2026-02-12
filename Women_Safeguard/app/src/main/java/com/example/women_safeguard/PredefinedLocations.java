package com.example.women_safeguard;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredefinedLocations {
    private static final Map<String, LatLng> locationMap = new HashMap<>();

    static {
        // Initialize with common locations in your target area
        // City centers
        locationMap.put("Panchavati", new LatLng(20.0074939,73.7826181));
        locationMap.put("CBS", new LatLng(20.0013961,73.7728209));
        locationMap.put("Canada Corner", new LatLng(20.003082, 73.770393));
        locationMap.put("Shahid", new LatLng(20.014543, 73.756265));
        locationMap.put("CCM", new LatLng(19.991025, 73.763483));
        locationMap.put("Deola", new LatLng(20.458398, 74.182562));
        locationMap.put("Dindori", new LatLng(20.201377, 73.830341));

        locationMap.put("Yashwantrao Chavan Maharashtra Open University", new LatLng(20.0026669,73.7482342));
        locationMap.put("SMRK-BK-AK Mahila Mahavidyalaya", new LatLng(20.0060969,73.7558241));
        locationMap.put("BYK College of Commerce", new LatLng(20.0054999,73.7597089));
        locationMap.put("KTHM College", new LatLng(20.0075858,73.7741197));
        locationMap.put("Rangubai Junnare English Medium School", new LatLng(19.9924033,73.8004054));
        locationMap.put("St. Francis High School", new LatLng(19.9956291,73.7981817));
        locationMap.put("Rachana Vidyalaya", new LatLng(19.9986463,73.7679298));
        locationMap.put("Fravashi Academy", new LatLng(19.9940209,73.7587975));
        locationMap.put("Wisdom High International School", new LatLng(20.0101267,73.7371044));
        locationMap.put("New Era English School", new LatLng(19.983142,73.7792485));

        locationMap.put("HPT Arts & RYK Science College", new LatLng(20.0066628,73.7574908));
        locationMap.put("Bhonsala Military School", new LatLng(20.0055704,73.7488882));
        locationMap.put("Sacred Heart Convent High School", new LatLng(19.9794138,73.7920926));
        locationMap.put("Ashoka Universal School", new LatLng(19.9800311,73.7937196));
        locationMap.put("Vidya Prabodhini Prashala", new LatLng(20.0083074,73.7514323));

        locationMap.put("SMBT College of Pharmacy", new LatLng(19.7589665,73.76651));

        locationMap.put("K. V. N. Naik College", new LatLng(20.004366,73.7700124));
        locationMap.put("S. V. K. T. Arts, Science and Commerce College", new LatLng(19.925453,73.8312261));
        locationMap.put("St. Xavier's High School", new LatLng(19.96577,73.8165538));
        locationMap.put("Dr. Moonje Institute of Management", new LatLng(20.0111937,73.7503537));


        locationMap.put("JDC Bytco Institute of Management Studies and Research", new LatLng(20.004662,73.7602121));
        locationMap.put("Nashik Cambridge School", new LatLng(20.0711013,73.8035309));
        locationMap.put("New English School", new LatLng(19.93658,73.7316965));
        locationMap.put("Nirmala Convent", new LatLng(20.0192301,73.758667));


        locationMap.put("K. K. Wagh Polytechnic", new LatLng(20.0124905,73.816453));
        locationMap.put("MET Institute of Polytechnic", new LatLng(20.0419127,73.8475708));
        locationMap.put("Sandip Polytechnic", new LatLng(19.9620923,73.6683723));
        locationMap.put("Nashik Institute of Technology (NIT) Nashik", new LatLng(20.0016947,73.7347989));
        locationMap.put("Guru Gobind Singh Polytechnic", new LatLng(19.9572924,73.7736587));
        locationMap.put("Maratha Vidya Prasarak Samaj's Rajarshi Shahu Maharaj Polytechnic RSM ", new LatLng(20.0147266,73.7612953));
        locationMap.put("Shree Kapildhara Polytechnic", new LatLng(19.78744,73.4968657));
        locationMap.put("SND Polytechnic College", new LatLng(20.0666995,74.4548875));
        locationMap.put("Nashik Gramin Shikshan Prasarak Mandal's Polytechnic", new LatLng(19.9504618,73.575086));
        locationMap.put("Pune Vidyarthi Griha's Polytechnic", new LatLng(20.2392774,73.4573013));


        // kukure
        locationMap.put("K. K. Wagh Institute of Engineering Education & Research", new LatLng(20.0135914,73.8196865));
        locationMap.put("MET Bhujbal Knowledge City", new LatLng(20.0414904,73.8476378));
        locationMap.put("Sandip Institute of Technology & Research Centre", new LatLng(19.9658487,73.6655731));
        locationMap.put("Nashik District Maratha Vidya Prasarak Samaj's KBT COE", new LatLng(20.0147266,73.7612953));
        locationMap.put("Guru Gobind Singh College of Engineering & Research Centre", new LatLng(19.9587277,73.7753732));
        locationMap.put("Brahma Valley College of Engineering & Research Institute", new LatLng(19.9500163,73.5733513));
        locationMap.put("Gokhale Education Society's R. H. Sapat College of Engineering", new LatLng(20.004483,73.7563581));
        locationMap.put("Amrutvahini College of Engineering", new LatLng(19.614018,74.1828763));
        locationMap.put("SND College of Engineering & Research Centre", new LatLng(20.0656602,74.4536785));
        locationMap.put("MVPS's Karmaveer Adv. Baburao Ganpatrao Thakare College of Engineering", new LatLng(20.0147266,73.7612953));
        locationMap.put("SNJB's Late Sau. Kantabai Bhavarlalji Jain College of Engineering", new LatLng(20.3354593,74.2341524));
        locationMap.put("R. H. Sapat College of Engineering, Management Studies & Research", new LatLng(20.004483,73.7563581));
        locationMap.put("KK Wagh College of Agricultural Engineering & Technology", new LatLng(20.0260503,73.8295248));
        locationMap.put("Shatabdi Institute of Engineering & Research", new LatLng(19.8155535,73.8281847));
        locationMap.put("Matoshri College of Engineering & Research Centre", new LatLng(19.9904543,73.9080251));
        locationMap.put("Nashik Gramin Shikshan Prasarak Mandal's College of Engineering", new LatLng(19.9504618,73.575086));
        //
        // gayake
        locationMap.put("Sahyadri Valley College of Engineering & Technology", new LatLng(19.1570221,74.1082205));
        locationMap.put("Pune Vidyarthi Griha's College of Engineering & Technology", new LatLng(20.0330547,73.7860015));
        locationMap.put("Jawahar Education Society's, INSTITUTE OF TECHNOLOGY JIT ", new LatLng(20.0322795,73.6871427));
        locationMap.put("Dr. Vasantrao Pawar Medical College, Hospital & Research Centre", new LatLng(20.0356147,73.8493349));
        locationMap.put("SMBT Institute of Medical Sciences & Research Centre", new LatLng(19.7589665,73.76651));
        locationMap.put("MVP Samaj's Dr. Vasantrao Pawar Medical College", new LatLng(20.0356147,73.8493349));
        locationMap.put("Nashik Gramin Shikshan Prasarak Mandal's Medical College", new LatLng(19.9504617,73.57279));
        locationMap.put("Hirabai Haridas Vidyanagari Trust's Loknete Rajarambapu Patil Medical College", new LatLng(16.8325552,74.2746828));
        locationMap.put("Ashwin Rural Ayurveda Medical College", new LatLng(19.5588363,74.3599665));
        locationMap.put("SND College of Medical Sciences & Research Centre", new LatLng(19.8231691,74.1345222));
        locationMap.put("Matoshri Institute of Medical Sciences & Research Centre", new LatLng(19.9921635,73.8849199));
        locationMap.put("Gangamai Institute of Medical Sciences & Research Centre", new LatLng(20.9625991,74.7651742));
        locationMap.put("Dr. Moonje Institute of Medical Sciences", new LatLng(20.0113206,73.7504507));
        locationMap.put("Rural Medical College of Pravara Medical Trust", new LatLng(19.9989992,72.5660706));
        locationMap.put("SMBT Dental College", new LatLng(19.6052748,74.1826574));
        locationMap.put("K.B.H. Dental College and Hospital", new LatLng(20.0045138,73.7324685));


        //chobe
        locationMap.put("Sula Vineyards", new LatLng(20.005388, 73.688474));
        locationMap.put("Trimbakeshwar Temple", new LatLng(19.932184, 73.530786));
        locationMap.put("Pandav Leni Caves", new LatLng(19.941211, 73.748555));
        locationMap.put("Muktidham Temple", new LatLng(19.951645, 73.836745));
        locationMap.put("Someshwar Waterfall", new LatLng(20.029238, 73.723373));
        locationMap.put("Ramkund", new LatLng(20.008568, 73.792502));
        locationMap.put("Anjneri Hill", new LatLng(19.929983 ,73.577166));
        locationMap.put("Saptashrungi Temple", new LatLng(20.006364, 73.799343));
        locationMap.put("Dugarwadi Waterfall", new LatLng(19.940747, 73.468322));
        locationMap.put("Nandur Madhmeshwar Bird Sanctuary", new LatLng(20.008430, 74.107636));
        locationMap.put("Gangapur Dam", new LatLng(20.048166, 73.679835));
        locationMap.put("York Winery", new LatLng(20.011686, 73.674594));
        locationMap.put("Harihar Fort", new LatLng(19.906570, 73.473191));
        locationMap.put("Coin Museum", new LatLng(19.958246, 73.611358));
        locationMap.put("Kalaram Temple", new LatLng(20.007178, 73.795243));
        locationMap.put("Brahmagiri Hill", new LatLng(19.917215, 73.524150));
        locationMap.put("Dadasaheb Phalke Smarak", new LatLng(19.943849 ,73.750389));
        locationMap.put("Gargoti Museum", new LatLng(19.871938, 73.972132));
        locationMap.put("Kapaleshwar Temple", new LatLng(19.888093, 73.987841));
        locationMap.put("Jain Mandir Vilhouli", new LatLng(19.930252, 73.719918));
        locationMap.put("Vihigaon Waterfall", new LatLng(19.711593, 73.474714));
        locationMap.put("Sinnar Phata Gondeshwar Temple", new LatLng(19.851417, 74.002013));
        locationMap.put("Navshya Ganpati Temple", new LatLng(20.016434, 73.743246));
        locationMap.put("Chandwad Fort", new LatLng(20.336287, 74.259299));
        locationMap.put("Salher Fort", new LatLng(20.723726, 73.944963));
        locationMap.put("Saputara Hill Station", new LatLng(20.357590, 73.855868));
        locationMap.put("Vaitarna Dam", new LatLng(19.824278, 73.510442));
        locationMap.put("Igatpuri Hills", new LatLng(19.723390, 73.669028));
        locationMap.put("Ramsej Fort", new LatLng(20.112416, 73.767404));
        locationMap.put("Ashoka Waterfall", new LatLng(19.711703, 73.474734));
        locationMap.put("Balaji Temple", new LatLng(20.029221,73.722331));
        locationMap.put("Dabhosa Waterfall", new LatLng(20.005939,73.208085));
        locationMap.put("Bhaskargad Fort (Basgad)", new LatLng(19.904439, 73.431978));
        locationMap.put("Ahivant Fort", new LatLng(20.413832,73.850086));

        //  tanmay
        locationMap.put("The Gateway Hotel Ambad", new LatLng(19.957542, 73.754461));
        locationMap.put("Express Inn", new LatLng(19.952608, 73.755265));
        locationMap.put("Hotel Grand Rio", new LatLng(19.972034, 73.775103));
        locationMap.put("IBIS Nashik", new LatLng(19.990864, 73.738745));
        locationMap.put("The Source at Sula", new LatLng(20.005504, 73.688403));
        locationMap.put("Hotel Panchavati Yatri", new LatLng(20.005730, 73.785530));
        locationMap.put("Hotel Royale Heritage", new LatLng(19.998065, 73.786018));
        locationMap.put("Hotel City Palace", new LatLng(20.000231, 73.782075));
        locationMap.put("Hi 5 Hotel", new LatLng(19.955004, 73.755580));
        locationMap.put("Hotel Sai Saya", new LatLng(19.977310, 73.778368));

        locationMap.put("Cafe Bliss", new LatLng(20.013336, 73.756207));
        locationMap.put("Al Arabian Express", new LatLng(20.003502, 73.764959));
        locationMap.put("The Chocolate Room", new LatLng(20.039258, 73.795857));
        locationMap.put("Koreean By Baristo", new LatLng(20.013409, 73.738732));
        locationMap.put("The Royale Café ", new LatLng(19.759314, 73.771229));
        locationMap.put("Darling’s Cafe", new LatLng(20.011716, 73.756798));
        locationMap.put("Modern Café Nashik", new LatLng(20.007661, 73.763498));

        locationMap.put("Indira Nagar", new LatLng(19.972341, 73.781729));
        locationMap.put("Govind Nagar", new LatLng(19.9861731, 73.7751661));
        locationMap.put("Canada Corner", new LatLng(20.007376, 73.767365));
        locationMap.put("Panchavati", new LatLng(20.0095, 73.7919));
        locationMap.put("College Road", new LatLng(20.004101, 73.769978));
        locationMap.put("Ashok Nagar", new LatLng(19.986865, 73.719220));
        locationMap.put("Mahatma Nagar", new LatLng(19.997221, 73.753306));
        locationMap.put("Satpur", new LatLng(20.00728, 73.731091));
        locationMap.put("CIDCO", new LatLng(19.984090, 73.755331));
        locationMap.put("Adgaon", new LatLng(20.033427, 73.863537));
        locationMap.put("Pathardi Phata", new LatLng(19.944122, 73.774041));
        locationMap.put("Ambad", new LatLng(19.961953, 73.744467));
        locationMap.put("Sharanpur", new LatLng(19.998160, 73.772798));
        locationMap.put("Jail Road", new LatLng(19.975320, 73.841170));
        locationMap.put("Nashik Road", new LatLng(19.972881, 73.822267));
        locationMap.put("Trimbak Road", new LatLng(19.985282, 73.722582));
        locationMap.put("Deolali Camp", new LatLng(19.897002, 73.823413));
        locationMap.put("Mhasrul", new LatLng(20.046585, 73.807185));
        locationMap.put("Dwarka", new LatLng(19.993082, 73.803702));

    }

    public static List<String> getLocationNames() {
        return new ArrayList<>(locationMap.keySet());
    }

    /**
     * Get the LatLng for a specific location name
     * @param locationName The name of the location
     * @return LatLng coordinates or null if not found
     */
    public static LatLng getLocation(String locationName) {
        return locationMap.get(locationName);
    }

    /**
     * Find the closest matching location name for partial matches
     * @param input Partial or complete location name
     * @return Best matching location name or null if no match
     */
    public static String getClosestMatchingLocation(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        // Try exact match first
        for (String locationName : locationMap.keySet()) {
            if (locationName.equalsIgnoreCase(input.trim())) {
                return locationName;
            }
        }

        // Try partial match - case insensitive contains
        String inputLower = input.toLowerCase().trim();
        for (String locationName : locationMap.keySet()) {
            if (locationName.toLowerCase().contains(inputLower)) {
                return locationName;
            }
        }

        // Try partial match - location contains input
        for (String locationName : locationMap.keySet()) {
            if (inputLower.contains(locationName.toLowerCase())) {
                return locationName;
            }
        }

        return null;
    }


    public static List<LatLng> getDefaultCrimeSpots() {
        List<LatLng> spots = new ArrayList<>();
        spots.add(new LatLng(20.458398, 74.182562)); // Deola
        spots.add(new LatLng(20.201377, 73.830341)); // Dindori
        spots.add(new LatLng(28.6481936,77.3112788));//Isbt Anand Vihar
        spots.add(new LatLng(28.6447986,77.2108619));//Paharganj
        spots.add(new LatLng(28.6289016,77.2049872));// Connaught Place
        spots.add(new LatLng(19.0203304,72.8384882));//dadar station
        spots.add(new LatLng(19.1284994,72.9227067));//Lal Bahadur Shastri Marg
        spots.add(new LatLng(18.9651306,72.8232256));//Kamathipura
        spots.add(new LatLng(12.9769632,77.5720644));//Majestic Bus Stand
        spots.add(new LatLng(12.8411803,77.6614491));//Electronic City
        spots.add(new LatLng(22.5875224,88.350524));//Sonagachi
        spots.add(new LatLng(22.5683155,88.3687133));//Seldah Station
        spots.add(new LatLng(17.4340752,78.4990297));//Secunderabad Railway Station
        spots.add(new LatLng(17.3850642,78.4715796));//Koti Market Area
        spots.add(new LatLng(26.9236885,75.7984707));//Sindhi Camp Bus Stand
        spots.add(new LatLng(26.9643199,75.8441458));//Amer Road
        spots.add(new LatLng(26.8457784,80.9248475));//Aminabad Market
        spots.add(new LatLng(18.5175004,73.8390027));//Fergusson College Road
        spots.add(new LatLng(21.1436412,79.0822408));//Sitabuldi Market
        spots.add(new LatLng(21.1640837,79.0627833));//Sadar
        spots.add(new LatLng(19.2821648,72.8559369));//Mira Road
        spots.add(new LatLng(19.2800981,72.9277809));//Ghodbunder Road
        spots.add(new LatLng(19.994106,73.797085));//Ghodbunder Road
        spots.add(new LatLng(20.015324,73.822212));//Ghodbunder Road
        spots.add(new LatLng( 19.919154,73.902746));//Ghodbunder Roadv
        spots.add(new LatLng(20.046543,73.876788));//Ghodbunder Road
        spots.add(new LatLng( 20.007229,73.771376));//Ghodbunder Road
        spots.add(new LatLng(20.0010044,73.776848));//Ghodbunder Road


        return spots;
    }

    /**
     * Add a new location to the map (for runtime additions)
     * @param name Location name
     * @param latLng LatLng coordinates
     */
    public static void addLocation(String name, LatLng latLng) {
        locationMap.put(name, latLng);
    }

    /**
     * Find the closest predefined location to given coordinates
     * @param latLng Current coordinates
     * @return Name of the closest location
     */
    public static String findClosestLocation(LatLng latLng) {
        double minDistance = Double.MAX_VALUE;
        String closestLocation = null;

        for (Map.Entry<String, LatLng> entry : locationMap.entrySet()) {
            double distance = calculateDistance(latLng, entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                closestLocation = entry.getKey();
            }
        }

        return closestLocation;
    }


    private static double calculateDistance(LatLng point1, LatLng point2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(point2.latitude - point1.latitude);
        double lonDistance = Math.toRadians(point2.longitude - point1.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}