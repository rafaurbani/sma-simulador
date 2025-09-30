package com.g18.simulador;

import java.util.PriorityQueue;

public class Escalonador {
    private static double x = 12934;
    private static final int A = 33443;
    private static final int C = 324234;
    private static final long M = 327844324;

    private static int minArrival = 1;
    private static int maxArrival = 4;
    private static int minService = 3;
    private static int maxService = 4;
    private static int minServiceQ2 = 2;
    private static int maxServiceQ2 = 3;

    private static double tempoGlobal = 0;

    protected static Fila fila = new Fila(3, 5, minArrival, maxArrival, minService, maxService);
    
    protected static Fila fila1 = new Fila(2, 3, minArrival, maxArrival, minService, maxService);
    protected static Fila fila2 = new Fila(1, 5, 0, 0, minServiceQ2, maxServiceQ2);
    
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

        for (int i = 0; i <= fila.getCapacity(); i++) {
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

        for (int i = 0; i <= fila1.getCapacity(); i++) {
            System.out.println("Número de clientes na fila 1: " + i + " - Tempo médio: " + (fila1.getTimes()[i] / tempoGlobal) * 100 + "%");
        }

        System.out.println("--------------------------------------------------");

        for (int i = 0; i <= fila2.getCapacity(); i++) {
            System.out.println("Número de clientes na fila 2: " + i + " - Tempo médio: " + (fila2.getTimes()[i] / tempoGlobal) * 100 + "%");
        }


        System.out.println("--------------------------------------------------");
        
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
                escalonador.add(new Evento(1, tempoRandom(minService, maxService)));
            }
        } else {
            fila.loss();
        }

        escalonador.add(new Evento(0, tempoRandom(minArrival, maxArrival)));
    }

    public static void saidaFilaUnica(Evento evento) {
        acumulaTempoFilaUnica(evento);
        fila.out();
        if (fila.status() >= fila.servers()) {
            escalonador.add(new Evento(1, tempoRandom(minService, maxService)));
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
                escalonador.add(new Evento(2, tempoRandom(minService, maxService)));
            }
        } else {
            fila1.loss();
        }

        escalonador.add(new Evento(0, tempoRandom(minArrival, maxArrival)));
    }

    public static void saidaTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila2.out();
        if (fila2.status() >= fila2.servers()) {
            escalonador.add(new Evento(1, tempoRandom(minServiceQ2, maxServiceQ2)));
        }
    }

    public static void passagemTandem(Evento evento) {
        acumulaTempoTandem(evento);
        fila1.out();
        if (fila1.status() >= fila1.servers()) {
            escalonador.add(new Evento(2, tempoRandom(minService, maxService)));
        }
        if (fila2.status() < fila2.capacity()) {
            fila2.in();
            if (fila2.status() <= fila2.servers()) {
                escalonador.add(new Evento(1, tempoRandom(minServiceQ2, maxServiceQ2)));
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
