/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "local")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Local.findAll", query = "SELECT l FROM Local l")
    , @NamedQuery(name = "Local.findByIdlocal", query = "SELECT l FROM Local l WHERE l.idlocal = :idlocal")
    , @NamedQuery(name = "Local.findByNomeLocal", query = "SELECT l FROM Local l WHERE l.nomeLocal = :nomeLocal")
    , @NamedQuery(name = "Local.findBySala", query = "SELECT l FROM Local l WHERE l.sala = :sala")})
public class Local implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idlocal")
    private Integer idlocal;
    @Size(max = 70)
    @Column(name = "nome_local")
    private String nomeLocal;
    @Size(max = 70)
    @Column(name = "sala")
    private String sala;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idlocal")
    private List<Evento> eventoList;

    public Local() {
    }

    public Local(Integer idlocal) {
        this.idlocal = idlocal;
    }

    public Integer getIdlocal() {
        return idlocal;
    }

    public void setIdlocal(Integer idlocal) {
        this.idlocal = idlocal;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public void setNomeLocal(String nomeLocal) {
        this.nomeLocal = nomeLocal;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    @XmlTransient
    public List<Evento> getEventoList() {
        return eventoList;
    }

    public void setEventoList(List<Evento> eventoList) {
        this.eventoList = eventoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idlocal != null ? idlocal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Local)) {
            return false;
        }
        Local other = (Local) object;
        if ((this.idlocal == null && other.idlocal != null) || (this.idlocal != null && !this.idlocal.equals(other.idlocal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Local: " + nomeLocal + " Sala: " + sala;
    }
    
}
