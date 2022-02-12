package com.oslerdiagnostics.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OslerUtils {

    /**
     * Creates Analysis File comparing the downloaded+existing lists and existing_device_updated_list
     *
     * @param oldList
     * @param newList
     * @param path
     */
    public static void createAnalysisFile(List<String[]> oldList, List<String[]> newList, String path) throws IOException {

        FileWriter writer = new FileWriter(path);

        Map<String, String[]> oldMap = new HashMap<>();
        oldList.stream().forEach(arr -> oldMap.put(arr[0], new String[] {arr[1],arr[2],arr[3]}));

        Map<String, String[]> newMap = new HashMap<>();
        newList.stream().forEach(arr -> newMap.put(arr[0], new String[] {arr[1],arr[2].split("\t")[0],arr[2].split("\t")[1]}));

        for(Map.Entry<String, String[]> oldItem : oldMap.entrySet()) {

            if(newMap.containsKey(oldItem.getKey())) {
                String[] newItemsArray = newMap.get(oldItem.getKey());
                writer.write(oldItem.getKey() + "\t" + oldItem.getValue()[0] + "\t" + oldItem.getValue()[1] + "\t" + oldItem.getValue()[2]);
                if(Arrays.equals(oldItem.getValue(), newItemsArray)) {
                    writer.write("\t" + "\t" + "\t" + "CORRECT");
                }else {
                    writer.write("\t" + "\t" + "\t" + "ERROR");
                }
            }
            writer.write("\n");
        }
        writer.close();

    }

    /**
     * Resolves the hexadecimal code in the list and returns the list wit corresponding status codes
     *
     * @param list
     * @return list
     */
    public static List<String[]> resolveHex(List<String[]> list) {

        for(String[] array : list) {

            Map<String,String> status = new HashMap<>();

            status = checkStatus(array);
            array[2] = status.get("Authorization") + status.get("Administration") + "\t" + status.get("Training");
        }

        return list;
    }

    /**
     * Removes the duplicated userIDs in the llist and accepts the last one in the list as the correct item
     *
     * @param list
     * @return lastList
     */
    public static List<String[]> singularUsers(List<String[]> list) {

        List<String[]>  lastList = new ArrayList<>();

        for(String[] arr : list) {

            lastList.removeIf(l -> l[0].equals(arr[0]));
            lastList.add(arr);

        }

        return lastList;
    }

    /**
     * Reads the .txt file in the given path and returns as a List of String Array
     *
     * @param filePath
     * @return fileList
     */
    public static List<String[]> readFile(String filePath) throws FileNotFoundException {

        File file = new File(filePath);

        @SuppressWarnings("resource")
        Scanner scan = new Scanner(file);
        List<String[]> fileList = new ArrayList<>();

        while (scan.hasNextLine()) {
            String[] array = scan.nextLine().split("\t");
            fileList.add(array);
        }

        return fileList;
    }

    /**
     * Selects the data that belongs to the Device of which th ID number is given as parameter
     *
     * @param fileList
     * @param deviceID
     * @return deviceDataList
     */
    public static List<String[]> getDeviceDataList(List<String[]> fileList, String deviceID) {

        List<String[]>  deviceDataList = new ArrayList<>();

        for(String[] arr : fileList) {
            if(arr[1].equals(deviceID)) {
                deviceDataList.add(arr);
            }
        }

        return deviceDataList;
    }

    /**
     * Creates an updated list using existing_device_list and downloaded_device_list
     *
     * @param existingDeviceList
     * @param downloadedDeviceList
     * @return updatedList
     */
    public static List<String[]> createUpdatedList(List<String[]> existingDeviceList, List<String[]> downloadedDeviceList) {

        List<String[]>  updatedList = new ArrayList<>();

        for(String[] each : existingDeviceList) {
            if(!downloadedDeviceList.stream().anyMatch(arr -> arr[0].equals(each[0]))) {
                updatedList.add(each);
            }
        }
        updatedList.addAll(downloadedDeviceList);

        return updatedList;
    }

    /**
     * Converts the given hexadecimal code into binary code and returns the binary code
     *
     * @param hex
     * @return binary
     */
    public static String hexToBin(String hex){
        String binary = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for(int i = 0; i < hex.length(); i++){
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binFragment = Integer.toBinaryString(iHex);

            while(binFragment.length() < 4){
                binFragment = "0" + binFragment;
            }
            binary += binFragment;
        }
        return binary;
    }

    /**
     * Receives one row of a list as an array. Converts the hex using hexToBin and resolves the binary code.
     * Returns the appropriate status expressions using the binary code
     *
     * @param array
     * @return statMap
     */
    public static Map<String,String> checkStatus(String[] array) {
        Map<String,String> statMap = new HashMap<>();

        String binar = hexToBin(array[2]);
        String authorizationStat = "INVALID AUTHORIZATION STATUS DATA";
        String trainingStat = "INVALID TRAINING STATUS DATA";
        String adminStat = "INVALID ADMIN STATUS DATA";

        if(("" + binar.charAt(0)).equals("1")) {
            authorizationStat = "Authorised";
        }else if (("" + binar.charAt(0)).equals("0")) {
            authorizationStat = "Disabled";
        }

        statMap.put("Authorization", authorizationStat);

        if(("" + binar.charAt(1)).equals("1")) {
            trainingStat = "Trained";
        }else if (("" + binar.charAt(1)).equals("0")) {
            trainingStat = "Untrained";
        }

        statMap.put("Training", trainingStat);

        if(("" + binar.charAt(2)).equals("1")) {
            adminStat = "Operator";
        }else if (("" + binar.charAt(2)).equals("0")) {
            adminStat = "Admin";
        }

        statMap.put("Administration", adminStat);

        return statMap;
    }


}
