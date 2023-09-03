/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mackayirb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    public static HashMap<String, String> attributes = new HashMap();
    public static ArrayList<String> subscribed_UUIDs;
    public static ArrayList<String> input_UUIDs;

    static {

        // ================================================================================
        // attributes

        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");

        // C1-C6
        attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "Service: C1-C6");
        attributes.put("0000fff1-0000-1000-8000-00805f9b34fb", "C1");
        attributes.put("0000fff2-0000-1000-8000-00805f9b34fb", "C2");
        attributes.put("0000fff3-0000-1000-8000-00805f9b34fb", "C3");
        attributes.put("0000fff4-0000-1000-8000-00805f9b34fb", "C4");
        attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "C5");
        attributes.put("0000fff6-0000-1000-8000-00805f9b34fb", "C6");

        attributes.put("6E400000-B5A3-F393-E0A9-E50E24DCCA9E", "Service: R1-R6");
        attributes.put("6E400002-B5A3-F393-E0A9-E50E24DCCA9E", "R2");
        attributes.put("6E400003-B5A3-F393-E0A9-E50E24DCCA9E", "R3");

        // ================================================================================
        // subscribed
//        input_UUIDs = getUUIDsByReference(new String[]{"C3"});
//        subscribed_UUIDs = getUUIDsByReference(new String[]{"C6"});

        input_UUIDs = getUUIDsByReference(new String[]{"R2"});
        subscribed_UUIDs = getUUIDsByReference(new String[]{"R3"});

//        input_UUIDs = getUUIDsByReference(new String[]{"C1","C2","C3","C4","C5","C6"});
//        subscribed_UUIDs = getUUIDsByReference(new String[]{"C1","C2","C3","C4","C5","C6"});

    }

    public static ArrayList<String> getUUIDsByReference(String[] reference) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Map.Entry<String, String> entry:attributes.entrySet()) {
            for (String s:reference) {
                if(s.equals(entry.getValue())) {
                    arrayList.add(entry.getKey());
                }
            }
        }
        return arrayList;
    }

    public static String lookup(UUID uuid, String defaultName) {
        return lookup(uuid.toString(), defaultName);
    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static boolean isIncluded(int type, String UUID) {
        ArrayList<String> list;
        switch(type){
            case 0:
                list = subscribed_UUIDs;
                break;
            case 1:
                list = input_UUIDs;
                break;
            default:
                list = new ArrayList<>();
        }
        for (String u:list) {
            if(u.equalsIgnoreCase(UUID)) {
                return true;
            }
        }
        return false;
    }
    public static boolean checkSubscribed(String UUID) {
        return isIncluded(0, UUID);
    }
    public static boolean checkInput(String UUID) {
        return isIncluded(1, UUID);
    }
}
