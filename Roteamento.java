public class Roteamento {
    public final double p12, p13;
    public final double p21, p22, p2out;
    public final double p33, p3out;

    public Roteamento(double p12, double p13, double p21, double p22, double p2out, double p33, double p3out) {
        this.p12 = p12;
        this.p13 = p13;
        this.p21 = p21;
        this.p22 = p22;
        this.p2out = p2out;
        this.p33 = p33;
        this.p3out = p3out;
    }

    public int nextFrom(int origem, double u) {
        return switch (origem) {
            case 1 -> (u < p12) ? 2 : 3;
            case 2 -> {
                if (u < p21)
                    yield 1;
                if (u < p21 + p22)
                    yield 2;
                yield 0;
            }
            case 3 -> (u < p33) ? 3 : 0;
            default -> 0;
        };
    }
}
