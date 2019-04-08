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
@Table(name = "chamada_aula")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ChamadaAula.findAll", query = "SELECT c FROM ChamadaAula c")
    , @NamedQuery(name = "ChamadaAula.findByIdchamadaAula", query = "SELECT c FROM ChamadaAula c WHERE c.idchamadaAula = :idchamadaAula")
    , @NamedQuery(name = "ChamadaAula.findByFaltas", query = "SELECT c FROM ChamadaAula c WHERE c.faltas = :faltas")})
public class ChamadaAula implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idchamada_aula")
    private Integer idchamadaAula;
    @Column(name = "faltas")
    private Integer faltas;
    @JoinColumn(name = "idaluno", referencedColumnName = "idaluno")
    @ManyToOne(optional = false)
    private Aluno idaluno;
    @JoinColumn(name = "iddata_evento", referencedColumnName = "iddata_evento")
    @ManyToOne(optional = false)
    private DataEvento iddataEvento;

    public ChamadaAula() {
    }

    public ChamadaAula(Integer idchamadaAula) {
        this.idchamadaAula = idchamadaAula;
    }

    public Integer getIdchamadaAula() {
        return idchamadaAula;
    }

    public void setIdchamadaAula(Integer idchamadaAula) {
        this.idchamadaAula = idchamadaAula;
    }

    public Integer getFaltas() {
        return faltas;
    }

    public void setFaltas(Integer faltas) {
        this.faltas = faltas;
    }

    public Aluno getIdaluno() {
        return idaluno;
    }

    public void setIdaluno(Aluno idaluno) {
        this.idaluno = idaluno;
    }

    public DataEvento getIddataEvento() {
        return iddataEvento;
    }

    public void setIddataEvento(DataEvento iddataEvento) {
        this.iddataEvento = iddataEvento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idchamadaAula != null ? idchamadaAula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChamadaAula)) {
            return false;
        }
        ChamadaAula other = (ChamadaAula) object;
        if ((this.idchamadaAula == null && other.idchamadaAula != null) || (this.idchamadaAula != null && !this.idchamadaAula.equals(other.idchamadaAula))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.ChamadaAula[ idchamadaAula=" + idchamadaAula + " ]";
    }
    
}
