/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "chamada_evento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ChamadaEvento.findAll", query = "SELECT c FROM ChamadaEvento c")
    , @NamedQuery(name = "ChamadaEvento.findByIdchamada", query = "SELECT c FROM ChamadaEvento c WHERE c.idchamada = :idchamada")
    , @NamedQuery(name = "ChamadaEvento.findByHora", query = "SELECT c FROM ChamadaEvento c WHERE c.hora = :hora")
    , @NamedQuery(name = "ChamadaEvento.findBySituacao", query = "SELECT c FROM ChamadaEvento c WHERE c.situacao = :situacao")
    , @NamedQuery(name = "ChamadaEvento.findByIddataEvento", query = "SELECT c FROM ChamadaEvento c WHERE c.iddataEvento = :iddataEvento ORDER BY c.idchamada DESC")
    , @NamedQuery(name = "ChamadaEvento.CountTotal", query = "SELECT count(DISTINCT c.idaluno.idaluno) FROM ChamadaEvento c WHERE c.iddataEvento.iddataEvento = :dataEvento")
    , @NamedQuery(name = "ChamadaEvento.findByAlunoandDataEvento", query = "SELECT c FROM ChamadaEvento c WHERE c.idaluno = :aluno AND c.iddataEvento = :dataEvento ORDER BY c.idchamada")
    , @NamedQuery(name = "ChamadaEvento.findByAlunoImparAndDataEvento", query = "SELECT c.idaluno FROM ChamadaEvento c WHERE c.iddataEvento = :dataEvento GROUP BY c.idaluno")
    , @NamedQuery(name = "ChamadaEvento.CountByAlunoDataEvento", query = "SELECT COUNT(c.idchamada) FROM ChamadaEvento c WHERE c.iddataEvento = :dataEvento AND c.idaluno = :aluno")
    , @NamedQuery(name = "ChamadaEvento.findAllDataEvento", query = "SELECT c FROM ChamadaEvento c GROUP BY c.iddataEvento")})

public class ChamadaEvento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idchamada")
    private Integer idchamada;
    @Column(name = "hora")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Column(name = "situacao")
    private Boolean situacao;
    @JoinColumn(name = "idaluno", referencedColumnName = "idaluno")
    @ManyToOne(optional = false)
    private Aluno idaluno;
    @JoinColumn(name = "iddata_evento", referencedColumnName = "iddata_evento")
    @ManyToOne(optional = false)
    private DataEvento iddataEvento;

    public ChamadaEvento() {
    }

    public ChamadaEvento(Integer idchamada) {
        this.idchamada = idchamada;
    }

    public Integer getIdchamada() {
        return idchamada;
    }

    public void setIdchamada(Integer idchamada) {
        this.idchamada = idchamada;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Boolean getSituacao() {
        return situacao;
    }

    public void setSituacao(Boolean situacao) {
        this.situacao = situacao;
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
        hash += (idchamada != null ? idchamada.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChamadaEvento)) {
            return false;
        }
        ChamadaEvento other = (ChamadaEvento) object;
        if ((this.idchamada == null && other.idchamada != null) || (this.idchamada != null && !this.idchamada.equals(other.idchamada))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.ChamadaEvento[ idchamada=" + idchamada + " ]";
    }

}
