public class Fila {
    private final int servers;
    private final int capacity;
    private final double minArrival;
    private final double maxArrival;
    private final double minService;
    private final double maxService;
    private int customers;
    private int loss;
    private final double[] times;

    public Fila(int servers, int capacity, double minArrival, double maxArrival, double minService, double maxService) {
        this.servers = servers;
        this.capacity = capacity;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
        this.customers = 0;
        this.loss = 0;
        this.times = new double[capacity + 1];
    }

    public int status() {
        return customers;
    }

    public int capacity() {
        return capacity;
    }

    public int servers() {
        return servers;
    }

    public int loss() {
        return loss++;
    }

    public void in() {
        customers++;
    }

    public void out() {
        customers--;
    }

    public void addTimes(int status, double time) {
        times[status] += time;
    }

    public double[] getTimes() {
        return times;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getMinArrival() {
        return minArrival;
    }

    public double getMaxArrival() {
        return maxArrival;
    }

    public double getMinService() {
        return minService;
    }

    public double getMaxService() {
        return maxService;
    }

    public int getLoss() {
        return loss;
    }
}
