/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "historico")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historico.findAll", query = "SELECT h FROM Historico h")
    , @NamedQuery(name = "Historico.findByIdhistorico", query = "SELECT h FROM Historico h WHERE h.idhistorico = :idhistorico")
    , @NamedQuery(name = "Historico.findByTempo", query = "SELECT h FROM Historico h WHERE h.tempo = :tempo")})
public class Historico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idhistorico")
    private Integer idhistorico;
    @Column(name = "tempo")
    private Integer tempo;
    @JoinColumn(name = "idaluno", referencedColumnName = "idaluno")
    @ManyToOne(optional = false)
    private Aluno idaluno;
    @JoinColumn(name = "idevento", referencedColumnName = "idevento")
    @ManyToOne(optional = false)
    private Evento idevento;

    public Historico() {
    }

    public Historico(Integer idhistorico) {
        this.idhistorico = idhistorico;
    }

    public Integer getIdhistorico() {
        return idhistorico;
    }

    public void setIdhistorico(Integer idhistorico) {
        this.idhistorico = idhistorico;
    }

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public Aluno getIdaluno() {
        return idaluno;
    }

    public void setIdaluno(Aluno idaluno) {
        this.idaluno = idaluno;
    }

    public Evento getIdevento() {
        return idevento;
    }

    public void setIdevento(Evento idevento) {
        this.idevento = idevento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idhistorico != null ? idhistorico.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Historico)) {
            return false;
        }
        Historico other = (Historico) object;
        if ((this.idhistorico == null && other.idhistorico != null) || (this.idhistorico != null && !this.idhistorico.equals(other.idhistorico))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.Historico[ idhistorico=" + idhistorico + " ]";
    }
    
}
