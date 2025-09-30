package com.g18.simulador;

public class Evento implements Comparable<Evento> {
    public static final int CHEGADA = 0;
    public static final int SAIDA = 1;
    public static final int PASSAGEM = 2;

    private final int tipo;
    private final double tempo;

    public Evento(int tipo, double tempo) {
        this.tipo = tipo;
        this.tempo = tempo;
    }

    @Override
    public int compareTo(Evento outroEvento) {
        return Double.compare(this.tempo, outroEvento.tempo);
    }

    public int getTipo() {
        return tipo;
    }

    public double getTempo() {
        return tempo;
    }

}
