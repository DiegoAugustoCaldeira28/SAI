/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "data_evento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataEvento.findAll", query = "SELECT d FROM DataEvento d")
    , @NamedQuery(name = "DataEvento.findByIddataEvento", query = "SELECT d FROM DataEvento d WHERE d.iddataEvento = :iddataEvento")
    , @NamedQuery(name = "DataEvento.findByData", query = "SELECT d FROM DataEvento d WHERE d.data = :data")
    , @NamedQuery(name = "DataEvento.findByAberto", query = "SELECT d FROM DataEvento d WHERE d.aberto = :aberto")
    , @NamedQuery(name = "DataEvento.findByHoraAbertura", query = "SELECT d FROM DataEvento d WHERE d.horaAbertura = :horaAbertura")
    , @NamedQuery(name = "DataEvento.findByHoraFechamento", query = "SELECT d FROM DataEvento d WHERE d.horaFechamento = :horaFechamento")
    , @NamedQuery(name = "DataEvento.findByEvento", query = "SELECT d FROM DataEvento d INNER JOIN Evento e ON d.idevento = e WHERE d.idevento.idevento = :idevento")
    , @NamedQuery(name = "DataEvento.findByEventosSai", query = "SELECT d FROM DataEvento d INNER JOIN Evento e ON d.idevento = e WHERE e.curso = \"SAI\"")
    , @NamedQuery(name = "DataEvento.findByEventoCurso", query = "SELECT de FROM DataEvento de INNER JOIN Evento e ON de.idevento = e WHERE e.curso = :curso OR e.curso=\"DEPARTAMENTO DE ENGENHARIA ELETRONICA/ELETRICA\" OR e.curso=\"DEPARTAMENTO DE INFORMATICA\"")
    , @NamedQuery(name = "DataEvento.findEventosAberto", query = "SELECT d FROM DataEvento d INNER JOIN Evento e ON d.idevento = e WHERE e.tipo = \"Palestra\" OR e.tipo = \"Workshop\" ORDER BY e.nome ASC")})
public class DataEvento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddata_evento")
    private Integer iddataEvento;
    @Column(name = "data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Column(name = "aberto")
    private Boolean aberto;
    @Column(name = "hora_abertura")
    @Temporal(TemporalType.TIME)
    private Date horaAbertura;
    @Column(name = "hora_fechamento")
    @Temporal(TemporalType.TIME)
    private Date horaFechamento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iddataEvento")
    private List<Chamada> chamadaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iddataEvento")
    private List<ChamadaEvento> chamadaEventoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iddataEvento")
    private List<ChamadaAula> chamadaAulaList;
    @JoinColumn(name = "idevento", referencedColumnName = "idevento")
    @ManyToOne(optional = false)
    private Evento idevento;

    public DataEvento() {
    }

    public DataEvento(Integer iddataEvento) {
        this.iddataEvento = iddataEvento;
    }

    public Integer getIddataEvento() {
        return iddataEvento;
    }

    public void setIddataEvento(Integer iddataEvento) {
        this.iddataEvento = iddataEvento;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Boolean getAberto() {
        return aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public Date getHoraAbertura() {
        return horaAbertura;
    }

    public void setHoraAbertura(Date horaAbertura) {
        this.horaAbertura = horaAbertura;
    }

    public Date getHoraFechamento() {
        return horaFechamento;
    }

    public void setHoraFechamento(Date horaFechamento) {
        this.horaFechamento = horaFechamento;
    }

    @XmlTransient
    public List<Chamada> getChamadaList() {
        return chamadaList;
    }

    public void setChamadaList(List<Chamada> chamadaList) {
        this.chamadaList = chamadaList;
    }

    @XmlTransient
    public List<ChamadaEvento> getChamadaEventoList() {
        return chamadaEventoList;
    }

    public void setChamadaEventoList(List<ChamadaEvento> chamadaEventoList) {
        this.chamadaEventoList = chamadaEventoList;
    }

    @XmlTransient
    public List<ChamadaAula> getChamadaAulaList() {
        return chamadaAulaList;
    }

    public void setChamadaAulaList(List<ChamadaAula> chamadaAulaList) {
        this.chamadaAulaList = chamadaAulaList;
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
        hash += (iddataEvento != null ? iddataEvento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataEvento)) {
            return false;
        }
        DataEvento other = (DataEvento) object;
        if ((this.iddataEvento == null && other.iddataEvento != null) || (this.iddataEvento != null && !this.iddataEvento.equals(other.iddataEvento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.mb.scap.jpa.entities.DataEvento[ iddataEvento=" + iddataEvento + " ]";
    }

}
