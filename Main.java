public class Main {
    private static final double X = 12934;
    private static final int A = 33443;
    private static final int C = 324234;
    private static final long M = 327844324;

    public static void main(String[] args) {
        Config cfg = Config.load("config.txt");
        RandomNumber rand = new RandomNumber(X, A, C, M);
        switch (cfg.tipoFila) {
            case 1 -> {
                System.out.println("Simulando fila simples");
                new FilaSimples(cfg, rand).run();
            }
            case 2 -> {
                System.out.println("Simulando fila em tandem");
                new FilaTandem(cfg, rand).run();
            }
            case 3 -> {
                System.out.println("Simulando fila em redes");
                Roteamento rt = new Roteamento(cfg.p12, cfg.p13, cfg.p21, cfg.p22, cfg.p2out, cfg.p33, cfg.p3out);
                new FilaRedes(cfg, rand, rt).run();
            }
            default -> System.out.println("Tipo de fila inválido. Usando M/M/1 por padrão.");
        }
    }
}