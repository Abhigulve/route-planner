public class Test {

    public static void main(String[] args) {
        double lat = 18.5559569;
        double lon = 73.8918204;
        //Earth’s radius, sphere
        long R = 6378137;
        //offsets in meters
        int dn = 100;
        int de = 100;
        //Coordinate offsets in radians
        double dLat = dn / R;
        double dLon = de / (R * Math.cos(Math.PI * lat / 180));

        //OffsetPosition, decimal degrees
        double latO = lat + dLat * 180 / Math.PI;

        System.out.println(lat + dLat * 180 / Math.PI);
        System.out.println(lon + dLon * 180 / Math.PI);

        System.out.println(getDirection(18.554092, 73.890782, 18.554883, 73.895428));

    }

    private static double getDirection(double lat1, double lng1, double lat2, double lng2) {

        double PI = Math.PI;
        double dTeta = Math.log(Math.tan((lat2 / 2) + (PI / 4)) / Math.tan((lat1 / 2) + (PI / 4)));
        double dLon = Math.abs(lng1 - lng2);
        double teta = Math.atan2(dLon, dTeta);
        double direction = Math.round(Math.toDegrees(teta));
        return direction; //direction in degree

    }
}
