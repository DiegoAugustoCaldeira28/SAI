/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jsf.util;

import java.util.ArrayList;

/**
 *
 * @author Diego Caldeira
 */
public class Curso {
    private String nome;
    private float cargaHoraria;
    private ArrayList<Relatorio> relatorios;
    
    public Curso() {
    }

    public Curso(String nome, float cargaHoraria) {
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the cargaHoraria
     */
    public float getCargaHoraria() {
        return cargaHoraria;
    }

    /**
     * @param cargaHoraria the cargaHoraria to set
     */
    public void setCargaHoraria(float cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * @return the relatorios
     */
    public ArrayList<Relatorio> getRelatorios() {
        return relatorios;
    }

    /**
     * @param relatorios the relatorios to set
     */
    public void setRelatorios(ArrayList<Relatorio> relatorios) {
        this.relatorios = relatorios;
    }
}
