import java.util.PriorityQueue;

public class FilaRedes {
    private final Config config;
    private final RandomNumber rand;
    private final Roteamento rt;

    private final PriorityQueue<Evento> escalonador = new PriorityQueue<>();
    private double tempoGlobal = 0;

    private final Fila f1;
    private final Fila f2;
    private final Fila f3;

    public FilaRedes(Config config, RandomNumber rand, Roteamento rt) {
        this.config = config;
        this.rand = rand;
        this.rt = rt;
        f1 = new Fila(config.servers1, config.capacity1, config.minArrival, config.maxArrival, config.minServiceQ1, config.maxServiceQ1);
        f2 = new Fila(config.servers2, config.capacity2, 0, 0, config.minServiceQ2, config.maxServiceQ2);
        f3 = new Fila(config.servers3, config.capacity3, 0, 0, config.minServiceQ3, config.maxServiceQ3);
    }

    public void run() {
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival), 1));
        int count = config.events;
        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento e = escalonador.poll();
            switch (e.getTipo()) {
                case Evento.CHEGADA -> chegada(e);
                case Evento.PASSAGEM -> passagem(e);
                case Evento.SAIDA -> acumulaTempo(e); // unused; keep for future
            }
        }
        printResultados();
    }

    private void chegada(Evento e) {
        acumulaTempo(e);
        Fila f = fila(e.getFilaId());
        if (f.status() < f.capacity()) {
            f.in();
            if (f.status() <= f.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(f), serviceMax(f)), e.getFilaId()));
            }
        } else {
            f.loss();
        }
        if (e.getFilaId() == 1) {
            escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival), 1));
        }
    }

    private void passagem(Evento e) {
        acumulaTempo(e);
        Fila origem = fila(e.getFilaId());
        origem.out();
        if (origem.status() >= origem.servers()) {
            escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(origem), serviceMax(origem)), e.getFilaId()));
        }
        int dest = rt.nextFrom(e.getFilaId(), rand.next());
        if (dest == 0)
            return; // out
        Fila d = fila(dest);
        if (d.status() < d.capacity()) {
            d.in();
            if (d.status() <= d.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(serviceMin(d), serviceMax(d)), dest));
            }
        } else
            d.loss();
    }

    private void acumulaTempo(Evento e) {
        double delta = e.getTempo() - tempoGlobal;
        f1.addTimes(f1.status(), delta);
        f2.addTimes(f2.status(), delta);
        f3.addTimes(f3.status(), delta);
        tempoGlobal = e.getTempo();
    }

    private Fila fila(int id) {
        return switch (id) {
            case 1 -> f1;
            case 2 -> f2;
            case 3 -> f3;
            default -> f1;
        };
    }

    private int serviceMin(Fila f) {
        if (f == f1)
            return config.minServiceQ1;
        if (f == f2)
            return config.minServiceQ2;
        return config.minServiceQ3;
    }

    private int serviceMax(Fila f) {
        if (f == f1)
            return config.maxServiceQ1;
        if (f == f2)
            return config.maxServiceQ2;
        return config.maxServiceQ3;
    }

    private double tempoRandom(int a, int b) {
        return tempoGlobal + rand.nextInRange(a, b);
    }

    private void printResultados() {
        Fila[] fs = { f1, f2, f3 };
        for (int i = 0; i < fs.length; i++) {
            System.out.println("---------------- Fila " + (i + 1) + " ----------------");
            Fila f = fs[i];
            for (int n = 0; n <= f.getCapacity(); n++) {
                double perc = tempoGlobal > 0 ? (f.getTimes()[n] / tempoGlobal) * 100.0 : 0.0;
                System.out.printf("N=%d -> %.4f%%%n", n, perc);
            }
            System.out.println("Perdas: " + f.getLoss());
        }
        System.out.println("Tempo total: " + tempoGlobal);
    }
}
