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
@Table(name = "eventos_instrutores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EventosInstrutores.findAll", query = "SELECT e FROM EventosInstrutores e")
    , @NamedQuery(name = "EventosInstrutores.findByIdeventosInstrutor", query = "SELECT e FROM EventosInstrutores e WHERE e.ideventosInstrutor = :ideventosInstrutor")})
public class EventosInstrutores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ideventos_instrutor")
    private Integer ideventosInstrutor;
    @JoinColumn(name = "idevento", referencedColumnName = "idevento")
    @ManyToOne(optional = false)
    private Evento idevento;
    @JoinColumn(name = "idinstrutor", referencedColumnName = "idinstrutor")
    @ManyToOne(optional = false)
    private Instrutor idinstrutor;

    public EventosInstrutores() {
    }

    public EventosInstrutores(Integer ideventosInstrutor) {
        this.ideventosInstrutor = ideventosInstrutor;
    }

    public Integer getIdeventosInstrutor() {
        return ideventosInstrutor;
    }

    public void setIdeventosInstrutor(Integer ideventosInstrutor) {
        this.ideventosInstrutor = ideventosInstrutor;
    }

    public Evento getIdevento() {
        return idevento;
    }

    public void setIdevento(Evento idevento) {
        this.idevento = idevento;
    }

    public Instrutor getIdinstrutor() {
        return idinstrutor;
    }

    public void setIdinstrutor(Instrutor idinstrutor) {
        this.idinstrutor = idinstrutor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ideventosInstrutor != null ? ideventosInstrutor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventosInstrutores)) {
            return false;
        }
        EventosInstrutores other = (EventosInstrutores) object;
        if ((this.ideventosInstrutor == null && other.ideventosInstrutor != null) || (this.ideventosInstrutor != null && !this.ideventosInstrutor.equals(other.ideventosInstrutor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.EventosInstrutores[ ideventosInstrutor=" + ideventosInstrutor + " ]";
    }
    
}
