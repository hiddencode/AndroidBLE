package com.example.androidble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
class SampleGattAttributes {
    private static HashMap attributes = new HashMap();                                   // hash map for processing string
    static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";       // some value for heart rate
    static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"; // some value for client characteristic
    static String CLIENT_NAME = "Faker";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "Manufacturer Name String");
    }

    static String lookup(String uuid, String defaultName) {
        String name = (String) attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
