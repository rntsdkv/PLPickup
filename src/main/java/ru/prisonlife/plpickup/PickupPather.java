package ru.prisonlife.plpickup;

/**
 * @author prot_cn
 * @project PLPickup
 */

public class PickupPather {

    public static String getWorldConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".world";
    }

    public static String getXCoordinateConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".x";
    }

    public static String getYCoordinateConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".y";
    }

    public static String getZCoordinateConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".z";
    }

    public static String getIdConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".id";
    }

    public static String getUuidConfigPath(String pickupName) {
        return getPickupConfigPath(pickupName) + ".uuid";
    }

    public static String getPickupConfigPath(String pickupName) {
        return "pickups." + pickupName;
    }
}
