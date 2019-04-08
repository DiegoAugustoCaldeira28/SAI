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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "evento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Evento.findAll", query = "SELECT e FROM Evento e")
    , @NamedQuery(name = "Evento.findByIdevento", query = "SELECT e FROM Evento e WHERE e.idevento = :idevento")
    , @NamedQuery(name = "Evento.findByNome", query = "SELECT e FROM Evento e WHERE e.nome = :nome")
    , @NamedQuery(name = "Evento.findByTipo", query = "SELECT e FROM Evento e WHERE e.tipo = :tipo")
    , @NamedQuery(name = "Evento.findByVagasTotais", query = "SELECT e FROM Evento e WHERE e.vagasTotais = :vagasTotais")
    , @NamedQuery(name = "Evento.findByHoraInicio", query = "SELECT e FROM Evento e WHERE e.horaInicio = :horaInicio")
    , @NamedQuery(name = "Evento.findByHoraTermino", query = "SELECT e FROM Evento e WHERE e.horaTermino = :horaTermino")
    , @NamedQuery(name = "Evento.findByCurso", query = "SELECT e FROM Evento e WHERE e.curso = :curso")
    , @NamedQuery(name = "Evento.findByCusto", query = "SELECT e FROM Evento e WHERE e.custo = :custo")
    , @NamedQuery(name = "Evento.findByCargaHoraria", query = "SELECT e FROM Evento e WHERE e.cargaHoraria = :cargaHoraria")
    , @NamedQuery(name = "Evento.findByTempoMinimo", query = "SELECT e FROM Evento e WHERE e.tempoMinimo = :tempoMinimo")
    , @NamedQuery(name = "Evento.findByInstrutor", query = "SELECT e FROM Evento e, EventosInstrutores ei, Instrutor i WHERE e = ei.idevento AND ei.idinstrutor = i AND i.idinstrutor = :idinstrutor AND e.tipo != \"Minicurso\" AND e.tipo != \"Minicurso Computacional\" ORDER BY e.nome ASC")
    , @NamedQuery(name = "Evento.findByInstrutorMini", query = "SELECT e FROM Evento e, EventosInstrutores ei, Instrutor i WHERE e = ei.idevento AND ei.idinstrutor = i AND i.idinstrutor = :idinstrutor AND (e.tipo = \"Minicurso\" OR e.tipo = \"Minicurso Computacional\") ORDER BY e.nome ASC") })
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idevento")
    private Integer idevento;
    @Size(max = 200)
    @Column(name = "nome")
    private String nome;
    @Size(max = 60)
    @Column(name = "tipo")
    private String tipo;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "vagas_totais")
    private Integer vagasTotais;
    @Column(name = "hora_inicio")
    @Temporal(TemporalType.TIME)
    private Date horaInicio;
    @Column(name = "hora_termino")
    @Temporal(TemporalType.TIME)
    private Date horaTermino;
    @Size(max = 100)
    @Column(name = "curso")
    private String curso;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "custo")
    private Double custo;
    @Column(name = "carga_horaria")
    private Float cargaHoraria;
    @Column(name = "tempo_minimo")
    private Integer tempoMinimo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idevento")
    private List<CargaHoraria> cargaHorariaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idevento")
    private List<Historico> historicoList;
    @JoinColumn(name = "idlocal", referencedColumnName = "idlocal")
    @ManyToOne(optional = false)
    private Local idlocal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idevento")
    private List<EventosInstrutores> eventosInstrutoresList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idevento")
    private List<Matricula> matriculaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idevento")
    private List<DataEvento> dataEventoList;

    public Evento() {
    }

    public Evento(Integer idevento) {
        this.idevento = idevento;
    }

    public Integer getIdevento() {
        return idevento;
    }

    public void setIdevento(Integer idevento) {
        this.idevento = idevento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(Integer vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(Date horaTermino) {
        this.horaTermino = horaTermino;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Double getCusto() {
        return custo;
    }

    public void setCusto(Double custo) {
        this.custo = custo;
    }

    public Float getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Float cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Integer getTempoMinimo() {
        return tempoMinimo;
    }

    public void setTempoMinimo(Integer tempoMinimo) {
        this.tempoMinimo = tempoMinimo;
    }

    @XmlTransient
    public List<CargaHoraria> getCargaHorariaList() {
        return cargaHorariaList;
    }

    public void setCargaHorariaList(List<CargaHoraria> cargaHorariaList) {
        this.cargaHorariaList = cargaHorariaList;
    }

    @XmlTransient
    public List<Historico> getHistoricoList() {
        return historicoList;
    }

    public void setHistoricoList(List<Historico> historicoList) {
        this.historicoList = historicoList;
    }

    public Local getIdlocal() {
        return idlocal;
    }

    public void setIdlocal(Local idlocal) {
        this.idlocal = idlocal;
    }

    @XmlTransient
    public List<EventosInstrutores> getEventosInstrutoresList() {
        return eventosInstrutoresList;
    }

    public void setEventosInstrutoresList(List<EventosInstrutores> eventosInstrutoresList) {
        this.eventosInstrutoresList = eventosInstrutoresList;
    }

    @XmlTransient
    public List<Matricula> getMatriculaList() {
        return matriculaList;
    }

    public void setMatriculaList(List<Matricula> matriculaList) {
        this.matriculaList = matriculaList;
    }

    @XmlTransient
    public List<DataEvento> getDataEventoList() {
        return dataEventoList;
    }

    public void setDataEventoList(List<DataEvento> dataEventoList) {
        this.dataEventoList = dataEventoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idevento != null ? idevento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Evento)) {
            return false;
        }
        Evento other = (Evento) object;
        if ((this.idevento == null && other.idevento != null) || (this.idevento != null && !this.idevento.equals(other.idevento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
    
}
