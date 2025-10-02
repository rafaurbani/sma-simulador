import java.util.PriorityQueue;

public class Main {
    private static long x = 12934;
    private static final long A = 33443;
    private static final long C = 324234;
    private static final long M = 327844324;

    private static int minArrival;
    private static int maxArrival;
    private static int minService;
    private static int maxService;
    private static int minServiceQ2;
    private static int maxServiceQ2;
    private static int minServiceQ3;
    private static int maxServiceQ3;

    private static double p12, p13;
    private static double p21, p22, p2out;
    private static double p33, p3out;

    private static double tempoGlobal = 0;

    protected static Fila fila;
    protected static Fila fila1;
    protected static Fila fila2;
    protected static Fila fila3;

    protected static final PriorityQueue<Evento> escalonador = new PriorityQueue<>();

    public static void main(String[] args) {
        var config = ConfigReader.lerConfigGenerico("config.txt");

        minArrival = ConfigReader.getInt(config, "minArrival");
        maxArrival = ConfigReader.getInt(config, "maxArrival");
        minService = ConfigReader.getInt(config, "minService");
        maxService = ConfigReader.getInt(config, "maxService");
        minServiceQ2 = ConfigReader.getInt(config, "minServiceQ2");
        maxServiceQ2 = ConfigReader.getInt(config, "maxServiceQ2");
        minServiceQ3 = ConfigReader.getInt(config, "minServiceQ3");
        maxServiceQ3 = ConfigReader.getInt(config, "maxServiceQ3");

        p12 = ConfigReader.getDouble(config, "p12");
        p13 = ConfigReader.getDouble(config, "p13");
        p21 = ConfigReader.getDouble(config, "p21");
        p22 = ConfigReader.getDouble(config, "p22");
        p2out = ConfigReader.getDouble(config, "p2out");
        p33 = ConfigReader.getDouble(config, "p33");
        p3out = ConfigReader.getDouble(config, "p3out");

        fila = new Fila(3, 5, minArrival, maxArrival, minService, maxService);
        fila1 = new Fila(ConfigReader.getInt(config, "serversFila1"), ConfigReader.getInt(config, "capacityFila1"), minArrival, maxArrival, minService, maxService);
        fila2 = new Fila(ConfigReader.getInt(config, "serversFila2"), ConfigReader.getInt(config, "capacityFila2"), 0, 0, minServiceQ2, maxServiceQ2);
        fila3 = new Fila(ConfigReader.getInt(config, "serversFila3"), ConfigReader.getInt(config, "capacityFila3"), 0, 0, minServiceQ3, maxServiceQ3);

        // filaUnica();
        // tandem();
        redeTresFilas();
    }

    public static void filaUnica() {
        escalonador.clear();
        tempoGlobal = 0;
        int count = 100000;
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(minArrival, maxArrival)));

        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento evento = escalonador.poll();
            
            switch (evento.getTipo()) {
                case Evento.CHEGADA -> chegadaFilaUnica(evento);
                case Evento.SAIDA -> saidaFilaUnica(evento);
            }
        }

        for (int i = 0; i <= fila.getCapacity(); i++) {
            double perc = tempoGlobal > 0 ? (fila.getTimes()[i] / tempoGlobal) * 100.0 : 0.0;
            System.out.printf("Número de clientes na fila: %d - Tempo médio: %.4f%%%n", i, perc);
        }
        System.out.println("Tempo total: " + tempoGlobal);
    }

    public static void tandem() {
        escalonador.clear();
        tempoGlobal = 0;
        
        int count = 100000;
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(minArrival, maxArrival)));

        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento evento = escalonador.poll();

            switch (evento.getTipo()) {
                case Evento.CHEGADA -> chegadaTandem(evento);
                case Evento.SAIDA -> saidaTandem(evento);
                case Evento.PASSAGEM -> passagemTandem(evento);
            }
        }
        
        imprimeResultadosTandem();
    }

    private static void imprimeResultadosTandem() {
        System.out.println("---------------- Fila 1 ----------------");
        for (int i = 0; i <= fila1.getCapacity(); i++) {
             double perc = tempoGlobal > 0 ? (fila1.getTimes()[i] / tempoGlobal) * 100.0 : 0;
             System.out.printf("N=%d -> %.4f%%%n", i, perc);
        }
        System.out.println("Perdas: " + fila1.getLoss());

        System.out.println("---------------- Fila 2 ----------------");
        for (int i = 0; i <= fila2.getCapacity(); i++) {
             double perc = tempoGlobal > 0 ? (fila2.getTimes()[i] / tempoGlobal) * 100.0 : 0;
             System.out.printf("N=%d -> %.4f%%%n", i, perc);
        }
        System.out.println("Perdas: " + fila2.getLoss());
        System.out.println("Tempo total: " + tempoGlobal);
    }

    public static void redeTresFilas() {
        int count = 100000;
        escalonador.clear();
        tempoGlobal = 0;

        escalonador.add(new Evento(Evento.CHEGADA, 2, 1));

        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento e = escalonador.poll();
            switch (e.getTipo()) {
                case Evento.CHEGADA -> chegadaRede(e);
                case Evento.SAIDA -> saidaRede(e);
                case Evento.PASSAGEM -> passagemRede(e);
            }
        }
        imprimeResultadosRede();
    }

    private static void imprimeResultadosRede() {
        Fila[] filas = { fila1, fila2, fila3 };
        for (int idx = 0; idx < filas.length; idx++) {
            Fila f = filas[idx];
            System.out.println("---------------- Fila " + (idx + 1) + " ----------------");
            for (int i = 0; i <= f.getCapacity(); i++) {
                double perc = tempoGlobal > 0 ? (f.getTimes()[i] / tempoGlobal) * 100.0 : 0;
                System.out.printf("N=%d -> %.4f%%%n", i, perc);
            }
            System.out.println("Perdas: " + f.getLoss());
        }
        System.out.println("Tempo total: " + tempoGlobal);
    }

    private static void chegadaRede(Evento e) {
        acumulaTempoRede(e);
        Fila f = filaPorId(e.getFilaId());
        if (f.status() < f.capacity()) {
            f.in();
            if (f.status() <= f.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(f), serviceMax(f)), e.getFilaId()));
            }
        } else {
            f.loss();
        }
        if (e.getFilaId() == 1) {
            escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(minArrival, maxArrival), 1));
        }
    }

    private static void passagemRede(Evento e) {
        acumulaTempoRede(e);
        Fila origem = filaPorId(e.getFilaId());
        origem.out();
        if (origem.status() >= origem.servers()) {
            escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(origem), serviceMax(origem)), e.getFilaId()));
        }

        int dest = roteamento(e.getFilaId());
        if (dest == 0) return;

        Fila destino = filaPorId(dest);
        if (destino.status() < destino.capacity()) {
            destino.in();
            if (destino.status() <= destino.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(destino), serviceMax(destino)), dest));
            }
        } else {
            destino.loss();
        }
    }

    private static void saidaRede(Evento e) {
        acumulaTempoRede(e);
    }

    private static int roteamento(int origemId) {
        double u = nextRandom();
        return switch (origemId) {
            case 1 -> u < p12 ? 2 : 3; 
            case 2 -> {
                if (u < p21) yield 1;
                if (u < p21 + p22) yield 3;
                yield 0; 
            }
            case 3 -> u < p33 ? 1 : 0;
            default -> 0;
        };
    }

    private static Fila filaPorId(int id) {
        return switch (id) {
            case 1 -> fila1;
            case 2 -> fila2;
            case 3 -> fila3;
            default -> throw new IllegalArgumentException("Fila inexistente: " + id);
        };
    }

    private static int serviceMin(Fila f) {
        if (f == fila1) return minService;
        if (f == fila2) return minServiceQ2;
        return minServiceQ3;
    }

    private static int serviceMax(Fila f) {
        if (f == fila1) return maxService;
        if (f == fila2) return maxServiceQ2;
        return maxServiceQ3;
    }

    public static double nextRandom() {
        x = (A * x + C) % M;
        return (double) x / M;
    }

    public static double tempoRandom(int a, int b) {
        return tempoGlobal + (a + (b - a) * nextRandom());
    }
    
    public static void chegadaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        if (fila.status() < fila.capacity()) {
            fila.in();
            if (fila.status() <= fila.servers()) {
                escalonador.add(new Evento(Evento.SAIDA, tempoRandom(minService, maxService)));
            }
        } else {
            fila.loss();
        }
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(minArrival, maxArrival)));
    }

    public static void saidaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        fila.out();
        if (fila.status() >= fila.servers()) {
            escalonador.add(new Evento(Evento.SAIDA, tempoRandom(minService, maxService)));
        }
    }

    public static void acumulaTempoFilaUnica(Evento evento) {
        fila.addTimes(fila.status(), evento.getTempo() - tempoGlobal);
        tempoGlobal = evento.getTempo();
    }

    public static void chegadaTandem(Evento evento) {
        acumulaTempoTandem(evento);
        if (fila1.status() < fila1.capacity()) {
            fila1.in();
            if (fila1.status() <= fila1.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(minService, maxService)));
            }
        } else {
            fila1.loss();
        }
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(minArrival, maxArrival)));
    }

    public static void saidaTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila2.out();
        if (fila2.status() >= fila2.servers()) {
            escalonador.add(new Evento(Evento.SAIDA, tempoRandom(minServiceQ2, maxServiceQ2)));
        }
    }

    public static void passagemTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila1.out();
        if (fila1.status() >= fila1.servers()) {
            escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(minService, maxService)));
        }
        if (fila2.status() < fila2.capacity()) {
            fila2.in();
            if (fila2.status() <= fila2.servers()) {
                escalonador.add(new Evento(Evento.SAIDA, tempoRandom(minServiceQ2, maxServiceQ2)));
            }
        } else {
            fila2.loss();
        }
    }
    
    private static void acumulaTempoRede(Evento e) {
        double delta = e.getTempo() - tempoGlobal;
        if (delta < 0) return;
        fila1.addTimes(fila1.status(), delta);
        fila2.addTimes(fila2.status(), delta);
        fila3.addTimes(fila3.status(), delta);
        tempoGlobal = e.getTempo();
    }

    public static void acumulaTempoTandem(Evento evento) {
        double delta = evento.getTempo() - tempoGlobal;
        if (delta < 0) return;
        fila1.addTimes(fila1.status(), delta);
        fila2.addTimes(fila2.status(), delta);
        tempoGlobal = evento.getTempo();
    }
}