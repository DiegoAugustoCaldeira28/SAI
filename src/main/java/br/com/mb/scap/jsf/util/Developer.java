/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jsf.util;

import java.math.BigDecimal;
import java.util.ArrayList;
/**
 *
 * @author Diego Caldeira
 */
public class Developer {
    private String nomeParticipante;
    private String emailParticipante;
    private String documentoParticipante;
    private String nomeEvento;
    private String nomeDepartamento;
    private String nomeCampus;
    private String periodoEvento;
    private String nomeCoordenador;
    private String ementaTXT;
    private ArrayList<Curso> cursos;
    private float soma;
    
    public Developer() {
    }

    /**
     * @return the nomeParticipante
     */
    public String getNomeParticipante() {
        return nomeParticipante;
    }

    /**
     * @param nomeParticipante the nomeParticipante to set
     */
    public void setNomeParticipante(String nomeParticipante) {
        this.nomeParticipante = nomeParticipante;
    }

    /**
     * @return the emailParticipante
     */
    public String getEmailParticipante() {
        return emailParticipante;
    }

    /**
     * @param emailParticipante the emailParticipante to set
     */
    public void setEmailParticipante(String emailParticipante) {
        this.emailParticipante = emailParticipante;
    }

    /**
     * @return the documentoParticipante
     */
    public String getDocumentoParticipante() {
        return documentoParticipante;
    }

    /**
     * @param documentoParticipante the documentoParticipante to set
     */
    public void setDocumentoParticipante(String documentoParticipante) {
        this.documentoParticipante = documentoParticipante;
    }

    /**
     * @return the nomeEvento
     */
    public String getNomeEvento() {
        return nomeEvento;
    }

    /**
     * @param nomeEvento the nomeEvento to set
     */
    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    /**
     * @return the nomeDepartamento
     */
    public String getNomeDepartamento() {
        return nomeDepartamento;
    }

    /**
     * @param nomeDepartamento the nomeDepartamento to set
     */
    public void setNomeDepartamento(String nomeDepartamento) {
        this.nomeDepartamento = nomeDepartamento;
    }

    /**
     * @return the nomeCampus
     */
    public String getNomeCampus() {
        return nomeCampus;
    }

    /**
     * @param nomeCampus the nomeCampus to set
     */
    public void setNomeCampus(String nomeCampus) {
        this.nomeCampus = nomeCampus;
    }

    /**
     * @return the periodoEvento
     */
    public String getPeriodoEvento() {
        return periodoEvento;
    }

    /**
     * @param periodoEvento the periodoEvento to set
     */
    public void setPeriodoEvento(String periodoEvento) {
        this.periodoEvento = periodoEvento;
    }

    /**
     * @return the nomeCoordenador
     */
    public String getNomeCoordenador() {
        return nomeCoordenador;
    }

    /**
     * @param nomeCoordenador the nomeCoordenador to set
     */
    public void setNomeCoordenador(String nomeCoordenador) {
        this.nomeCoordenador = nomeCoordenador;
    }

    /**
     * @return the ementaTXT
     */
    public String getEmentaTXT() {
        return ementaTXT;
    }

    /**
     * @param ementaTXT the ementaTXT to set
     */
    public void setEmentaTXT(String ementaTXT) {
        this.ementaTXT = ementaTXT;
    }

    /**
     * @return the cursos
     */
    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    /**
     * @param cursos the cursos to set
     */
    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    /**
     * @return the soma
     */
    public float getSoma() {
        return soma;
    }

    /**
     * @param soma the soma to set
     */
    public void setSoma(float soma) {
        this.soma = soma;
    }
    
    
}
