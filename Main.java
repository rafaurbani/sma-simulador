import java.util.PriorityQueue;

public class Main {
    private static double x = 12934;
    private static final int A = 33443;
    private static final int C = 324234;
    private static final long M = 327844324;

    private static double tempoGlobal = 0;

    protected static Fila fila = new Fila(3, 5, 2, 5, 3, 5);
    
    protected static Fila fila1 = new Fila(2, 3, 1, 4, 3, 4);
    protected static Fila fila2 = new Fila(3, 5, 0, 0, 2, 3);
    
    protected static final PriorityQueue<Evento> escalonador = new PriorityQueue<>();

    public static void main(String[] args) {
        // filaUnica();
        tandem();
    }

    public static void filaUnica() {
        int count = 100000;
        escalonador.add(new Evento(0, 2));

        while (count > 0) {
            Evento evento = escalonador.poll();

            if (evento.getTipo() == 0) {
                chegadaFilaUnica(evento);
            } else if (evento.getTipo() == 1) {
                saidaFilaUnica(evento);
            }

            count--;
        }

        for (int i = 0; i <= fila.servers(); i++) {
            System.out.println("Número de clientes na fila: " + i + " - Tempo médio: " + (fila.getTimes()[i] / tempoGlobal) * 100 + "%");
        }
    }

    public static void tandem() {
        int count = 100000;
        escalonador.add(new Evento(0, 1.5));

        while (count > 0) {
            Evento evento = escalonador.poll();

            switch (evento.getTipo()) {
                case 0 -> chegadaTandem(evento);
                case 1 -> saidaTandem(evento);
                case 2 -> passagemTandem(evento);
            }

            count--;
        }

        for (int i = 0; i <= fila1.servers(); i++) {
            System.out.println("Número de clientes na fila 1: " + i + " - Tempo médio: " + (fila1.getTimes()[i] / tempoGlobal) * 100 + "%");
        }

        System.out.println("--------------------------------------------------");

        for (int i = 0; i <= fila2.servers(); i++) {
            System.out.println("Número de clientes na fila 2: " + i + " - Tempo médio: " + (fila2.getTimes()[i] / tempoGlobal) * 100 + "%");
        }

        System.out.println("Perdas da fila 1: " + fila1.getLoss());
        System.out.println("Perdas da fila 2: " + fila2.getLoss());

        System.out.println("Tempo total: " + tempoGlobal);
    }



    public static double nextRandom() {
        x = (A * x + C) % M;
        return x / M;
    }

    public static double tempoRandom(int a, int b) {
        return tempoGlobal + (a + (b - a) * nextRandom());
    }

    public static void chegadaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);

        if (fila.status() < fila.capacity()) {
            fila.in();
            if (fila.status() <= fila.servers()) {
                escalonador.add(new Evento(1, tempoRandom(3, 5)));
            }
        } else {
            fila.loss();
        }

        escalonador.add(new Evento(0, tempoRandom(2, 5)));
    }

    public static void saidaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        fila.out();
        if (fila.status() >= fila.servers()) {
            escalonador.add(new Evento(1, tempoRandom(3, 5)));
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
                escalonador.add(new Evento(2, tempoRandom(3, 5)));
            }
        } else {
            fila1.loss();
        }

        escalonador.add(new Evento(0, tempoRandom(2, 5)));
    }

    public static void saidaTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila2.out();
        if (fila2.status() >= fila2.servers()) {
            escalonador.add(new Evento(1, tempoRandom(3, 5)));
        }
    }

    public static void passagemTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila1.out();
        if (fila1.status() >= fila1.servers()) {
            escalonador.add(new Evento(2, tempoRandom(5, 6)));
        }
        if (fila2.status() < fila2.capacity()) {
            fila2.in();
            if (fila2.status() <= fila2.servers()) {
                escalonador.add(new Evento(1, tempoRandom(2, 4)));
            }
        } else {
            fila2.loss();
        }
    }

    public static void acumulaTempoTandem(Evento evento) {
        fila1.addTimes(fila1.status(), evento.getTempo() - tempoGlobal);
        fila2.addTimes(fila2.status(), evento.getTempo() - tempoGlobal);

        tempoGlobal = evento.getTempo();
    }
}