import java.util.Map;

public class Config {
    public final int tipoFila;

    public final int servers1, capacity1;
    public final int servers2, capacity2;
    public final int servers3, capacity3;

    public final int minArrival, maxArrival;
    public final int minServiceQ1, maxServiceQ1;
    public final int minServiceQ2, maxServiceQ2;
    public final int minServiceQ3, maxServiceQ3;

    public final double p12, p13;
    public final double p21, p22, p2out;
    public final double p33, p3out;

    public final int events;

    private Config(
            int tipoFila,
            int servers1, int capacity1,
            int servers2, int capacity2,
            int servers3, int capacity3,
            int minArrival, int maxArrival,
            int minServiceQ1, int maxServiceQ1,
            int minServiceQ2, int maxServiceQ2,
            int minServiceQ3, int maxServiceQ3,
            double p12, double p13,
            double p21, double p22, double p2out,
            double p33, double p3out,
            int events) {
        this.tipoFila = tipoFila;
        this.servers1 = servers1;
        this.capacity1 = capacity1;
        this.servers2 = servers2;
        this.capacity2 = capacity2;
        this.servers3 = servers3;
        this.capacity3 = capacity3;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minServiceQ1 = minServiceQ1;
        this.maxServiceQ1 = maxServiceQ1;
        this.minServiceQ2 = minServiceQ2;
        this.maxServiceQ2 = maxServiceQ2;
        this.minServiceQ3 = minServiceQ3;
        this.maxServiceQ3 = maxServiceQ3;
        this.p12 = p12;
        this.p13 = p13;
        this.p21 = p21;
        this.p22 = p22;
        this.p2out = p2out;
        this.p33 = p33;
        this.p3out = p3out;
        this.events = events;
    }

    public static Config load(String path) {
        Map<String, String> cfg = ConfigReader.lerConfig(path);
        int tipoFila = getInt(cfg, "tipoFila");

        int servers1 = getInt(cfg, "serversFila1");
        int capacity1 = getInt(cfg, "capacityFila1");
        int servers2 = getInt(cfg, "serversFila2");
        int capacity2 = getInt(cfg, "capacityFila2");
        int servers3 = getInt(cfg, "serversFila3");
        int capacity3 = getInt(cfg, "capacityFila3");

        int minArrival = getInt(cfg, "minArrival");
        int maxArrival = getInt(cfg, "maxArrival");
        int minServiceQ1 = getInt(cfg, "minServiceQ1");
        int maxServiceQ1 = getInt(cfg, "maxServiceQ1");
        int minServiceQ2 = getInt(cfg, "minServiceQ2");
        int maxServiceQ2 = getInt(cfg, "maxServiceQ2");
        int minServiceQ3 = getInt(cfg, "minServiceQ3");
        int maxServiceQ3 = getInt(cfg, "maxServiceQ3");

        double p12 = getDouble(cfg, "p12");
        double p13 = getDouble(cfg, "p13");
        double p21 = getDouble(cfg, "p21");
        double p22 = getDouble(cfg, "p22");
        double p2out = getDouble(cfg, "p2out");
        double p33 = getDouble(cfg, "p33");
        double p3out = getDouble(cfg, "p3out");

        int events = 100000;

        return new Config( tipoFila,
                servers1, capacity1,
                servers2, capacity2,
                servers3, capacity3,
                minArrival, maxArrival,
                minServiceQ1, maxServiceQ1,
                minServiceQ2, maxServiceQ2,
                minServiceQ3, maxServiceQ3,
                p12, p13, p21, p22, p2out, p33, p3out,
                events);
    }

    private static int getInt(Map<String, String> m, String k) {
        return Integer.parseInt(m.get(k));
    }

    private static double getDouble(Map<String, String> m, String k) {
        return Double.parseDouble(m.get(k));
    }
}
