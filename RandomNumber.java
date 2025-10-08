public class RandomNumber {
    private double x;
    private final int A;
    private final int C;
    private final long M;

    public RandomNumber(double seed, int A, int C, long M) {
        this.x = seed; this.A = A; this.C = C; this.M = M;
    }

    public double next() {
        x = (A * x + C) % M;
        return x / M;
    }

    public double nextInRange(double a, double b) {
        return a + (b - a) * next();
    }
}
