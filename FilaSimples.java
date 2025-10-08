import java.util.PriorityQueue;

public class FilaSimples {
    private final Config config;
    private final RandomNumber rand;

    private final PriorityQueue<Evento> escalonador = new PriorityQueue<>();
    private double tempoGlobal = 0;

    private final Fila fila;

    public FilaSimples(Config config, RandomNumber rand) {
        this.config = config;
        this.rand = rand;
        fila = new Fila(config.servers1, config.capacity1, config.minArrival, config.maxArrival, config.minServiceQ1, config.maxServiceQ1);
    }

    public void run() {
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival), 1));
        int count = config.events;

        while (count-- > 0 && !escalonador.isEmpty()) {
            Evento evento = escalonador.poll();

            switch (evento.getTipo()) {
                case Evento.CHEGADA -> chegadaFilaUnica(evento);
                case Evento.SAIDA -> saidaFilaUnica(evento);
            }
        }

        printResultados();
    }

    public void chegadaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        if (fila.status() < fila.capacity()) {
            fila.in();
            if (fila.status() <= fila.servers()) {
                escalonador.add(new Evento(Evento.SAIDA, tempoRandom(config.minServiceQ1, config.maxServiceQ1)));
            }
        } else {
            fila.loss();
        }
        escalonador.add(new Evento(Evento.CHEGADA, tempoRandom(config.minArrival, config.maxArrival)));
    }

    public void saidaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        fila.out();
        if (fila.status() >= fila.servers()) {
            escalonador.add(new Evento(Evento.SAIDA, tempoRandom(config.minServiceQ1, config.maxServiceQ1)));
        }
    }

    public void acumulaTempoFilaUnica(Evento evento) {
        fila.addTimes(fila.status(), evento.getTempo() - tempoGlobal);
        tempoGlobal = evento.getTempo();
    }

    private double tempoRandom(int a, int b) {
        return tempoGlobal + rand.nextInRange(a, b);
    }

    private void printResultados() {
        for (int i = 0; i <= fila.getCapacity(); i++) {
            double perc = tempoGlobal > 0 ? (fila.getTimes()[i] / tempoGlobal) * 100.0 : 0.0;
            System.out.printf("Número de clientes na fila: %d - Tempo médio: %.4f%%%n", i, perc);
        }
        System.out.println("Tempo total: " + tempoGlobal);
    }

}