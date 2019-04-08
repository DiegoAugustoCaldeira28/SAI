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
@Table(name = "carga_horaria")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CargaHoraria.findAll", query = "SELECT c FROM CargaHoraria c")
    , @NamedQuery(name = "CargaHoraria.findByIdcargaHoraria", query = "SELECT c FROM CargaHoraria c WHERE c.idcargaHoraria = :idcargaHoraria")
    , @NamedQuery(name = "CargaHoraria.findByAluno", query = "SELECT c FROM CargaHoraria c GROUP BY c.idaluno")
    , @NamedQuery(name = "CargaHoraria.findByCursoAluno", query = "SELECT c FROM CargaHoraria c WHERE c.idaluno.idaluno = :idaluno")})
public class CargaHoraria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcarga_horaria")
    private Integer idcargaHoraria;
    @JoinColumn(name = "idaluno", referencedColumnName = "idaluno")
    @ManyToOne(optional = false)
    private Aluno idaluno;
    @JoinColumn(name = "idevento", referencedColumnName = "idevento")
    @ManyToOne(optional = false)
    private Evento idevento;

    public CargaHoraria() {
    }

    public CargaHoraria(Integer idcargaHoraria) {
        this.idcargaHoraria = idcargaHoraria;
    }

    public Integer getIdcargaHoraria() {
        return idcargaHoraria;
    }

    public void setIdcargaHoraria(Integer idcargaHoraria) {
        this.idcargaHoraria = idcargaHoraria;
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
        hash += (idcargaHoraria != null ? idcargaHoraria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CargaHoraria)) {
            return false;
        }
        CargaHoraria other = (CargaHoraria) object;
        if ((this.idcargaHoraria == null && other.idcargaHoraria != null) || (this.idcargaHoraria != null && !this.idcargaHoraria.equals(other.idcargaHoraria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.CargaHoraria[ idcargaHoraria=" + idcargaHoraria + " ]";
    }
    
}
