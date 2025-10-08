import java.util.PriorityQueue;

public class FilaTandem {
    private final Config config;
    private final RandomNumber rand;

    private final PriorityQueue<Evento> escalonador = new PriorityQueue<>();
    private double tempoGlobal = 0;

    private final Fila fila1;
    private final Fila fila2;

    public FilaTandem(Config config, RandomNumber rand) {
        this.config = config;
        this.rand = rand;
        fila1 = new Fila(config.servers1, config.capacity1, config.minArrival, config.maxArrival, config.minServiceQ1, config.maxServiceQ1);
        fila2 = new Fila(config.servers2, config.capacity2, config.minArrival, config.maxArrival, config.minServiceQ2, config.maxServiceQ2);
    }

    public void run() {
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival), 1));
        int count = config.events;

        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento evento = escalonador.poll();

            switch (evento.getTipo()) {
                case Evento.CHEGADA -> chegada(evento);
                case Evento.SAIDA -> saida(evento);
                case Evento.PASSAGEM -> passagem(evento);
            }
        }

        printResultados();
    }

    public void chegada(Evento evento) {
        if (fila1.status() < fila1.capacity()) {
            fila1.in();
            if (fila1.status() <= fila1.servers()) {
                escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(config.minServiceQ1, config.maxServiceQ1)));
            }
        } else {
            fila1.loss();
        }
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival)));
    }

    public void saida(Evento evento) {
        acumulaTempo(evento);
        fila2.out();
        if (fila2.status() >= fila2.servers()) {
            escalonador.add(new Evento(Evento.SAIDA, tempoRandom(config.minServiceQ2, config.maxServiceQ2)));
        }
    }

    public void passagem(Evento evento) {
        acumulaTempo(evento);
        fila1.out();
        if (fila1.status() >= fila1.servers()) {
            escalonador.add(new Evento(Evento.PASSAGEM, tempoRandom(config.minServiceQ1, config.maxServiceQ1)));
        }
        if (fila2.status() < fila2.capacity()) {
            fila2.in();
            if (fila2.status() <= fila2.servers()) {
                escalonador.add(new Evento(Evento.SAIDA, tempoRandom(config.minServiceQ2, config.maxServiceQ2)));
            }
        } else {
            fila2.loss();
        }
    }

    public void acumulaTempo(Evento evento) {
        double delta = evento.getTempo() - tempoGlobal;
        if (delta < 0)
            return;
        fila1.addTimes(fila1.status(), delta);
        fila2.addTimes(fila2.status(), delta);
        tempoGlobal = evento.getTempo();
    }

    private double tempoRandom(int a, int b) {
        return tempoGlobal + rand.nextInRange(a, b);
    }

    private void printResultados() {
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

}