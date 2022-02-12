package com.oslerdiagnostics.tests;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.oslerdiagnostics.utilities.OslerUtils.*;

public class VerifyDeviceUpdate {

    String portalUsersPath = "src/test/resources/PortalUserList.txt";
    String deviceUsersPath = "src/test/resources/DeviceUserList.txt";
    String updatedUsersList = "src/test/resources/DeviceUserList_updated.txt";
    String analysisFilePath = "src/test/resources/Analysis.txt";
    String deviceID = "47057";

    @Test
    public void deviceTest() throws IOException {

        List<String[]> portalUsersList = readFile(portalUsersPath);
        portalUsersList = getDeviceDataList(portalUsersList, deviceID);
        portalUsersList = singularUsers(portalUsersList);

        List<String[]> deviceUserList = readFile(deviceUsersPath);
        deviceUserList = singularUsers(deviceUserList);

        List<String[]> updatedDeviceList = createUpdatedList(deviceUserList, portalUsersList);
        updatedDeviceList = resolveHex(updatedDeviceList);

        List<String[]> deviceUpdatedList = readFile(updatedUsersList);

        createAnalysisFile(deviceUpdatedList, updatedDeviceList, analysisFilePath);

    }

}
