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
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.validator.constraints.br.CPF;

/**
 *
 * @author matheus
 */
@Entity
@Table(name = "aluno")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Aluno.findAll", query = "SELECT a FROM Aluno a")
    , @NamedQuery(name = "Aluno.findByIdaluno", query = "SELECT a FROM Aluno a WHERE a.idaluno = :idaluno")
    , @NamedQuery(name = "Aluno.findByNome", query = "SELECT a FROM Aluno a WHERE a.nome = :nome")
    , @NamedQuery(name = "Aluno.findByRa", query = "SELECT a FROM Aluno a WHERE a.ra = :ra")
    , @NamedQuery(name = "Aluno.findByCpf", query = "SELECT a FROM Aluno a WHERE a.cpf = :cpf")
    , @NamedQuery(name = "Aluno.findByRg", query = "SELECT a FROM Aluno a WHERE a.rg = :rg")
    , @NamedQuery(name = "Aluno.findByOrgaoExpeditor", query = "SELECT a FROM Aluno a WHERE a.orgaoExpeditor = :orgaoExpeditor")
    , @NamedQuery(name = "Aluno.findByExterno", query = "SELECT a FROM Aluno a WHERE a.externo = :externo")
    , @NamedQuery(name = "Aluno.findByCurso", query = "SELECT a FROM Aluno a WHERE a.curso = :curso")
    , @NamedQuery(name = "Aluno.findByInstituicao", query = "SELECT a FROM Aluno a WHERE a.instituicao = :instituicao")
    , @NamedQuery(name = "Aluno.findByEmail", query = "SELECT a FROM Aluno a WHERE a.email = :email")
    , @NamedQuery(name = "Aluno.listAllOrdenado", query = "SELECT a FROM Aluno a ORDER BY a.nome ASC")})
public class Aluno implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idaluno")
    private Integer idaluno;
    @Size(max = 100)
    @Column(name = "nome")
    private String nome;
    @Size(max = 20)
    @Column(name = "ra")
    private String ra;
    @CPF
    @Size(max = 15)
    @Column(name = "CPF")
    private String cpf;
    @Size(max = 45)
    @Column(name = "RG")
    private String rg;
    @Size(max = 20)
    @Column(name = "orgao_expeditor")
    private String orgaoExpeditor;
    @Column(name = "externo")
    private Boolean externo;
    @Size(max = 60)
    @Column(name = "curso")
    private String curso;
    @Size(max = 70)
    @Column(name = "instituicao")
    private String instituicao;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="E-mail inválido")//if the field contains email address consider using this annotation to enforce field validation
    @Email
    @Size(max = 45)
    @Column(name = "email")
    private String email;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<CargaHoraria> cargaHorariaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<Chamada> chamadaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<Historico> historicoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<ChamadaEvento> chamadaEventoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<Matricula> matriculaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idaluno")
    private List<ChamadaAula> chamadaAulaList;

    public Aluno() {
    }

    public Aluno(Integer idaluno) {
        this.idaluno = idaluno;
    }

    public Integer getIdaluno() {
        return idaluno;
    }

    public void setIdaluno(Integer idaluno) {
        this.idaluno = idaluno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getOrgaoExpeditor() {
        return orgaoExpeditor;
    }

    public void setOrgaoExpeditor(String orgaoExpeditor) {
        this.orgaoExpeditor = orgaoExpeditor;
    }

    public Boolean getExterno() {
        return externo;
    }

    public void setExterno(Boolean externo) {
        this.externo = externo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlTransient
    public List<CargaHoraria> getCargaHorariaList() {
        return cargaHorariaList;
    }

    public void setCargaHorariaList(List<CargaHoraria> cargaHorariaList) {
        this.cargaHorariaList = cargaHorariaList;
    }

    @XmlTransient
    public List<Chamada> getChamadaList() {
        return chamadaList;
    }

    public void setChamadaList(List<Chamada> chamadaList) {
        this.chamadaList = chamadaList;
    }

    @XmlTransient
    public List<Historico> getHistoricoList() {
        return historicoList;
    }

    public void setHistoricoList(List<Historico> historicoList) {
        this.historicoList = historicoList;
    }

    @XmlTransient
    public List<ChamadaEvento> getChamadaEventoList() {
        return chamadaEventoList;
    }

    public void setChamadaEventoList(List<ChamadaEvento> chamadaEventoList) {
        this.chamadaEventoList = chamadaEventoList;
    }

    @XmlTransient
    public List<Matricula> getMatriculaList() {
        return matriculaList;
    }

    public void setMatriculaList(List<Matricula> matriculaList) {
        this.matriculaList = matriculaList;
    }

    @XmlTransient
    public List<ChamadaAula> getChamadaAulaList() {
        return chamadaAulaList;
    }

    public void setChamadaAulaList(List<ChamadaAula> chamadaAulaList) {
        this.chamadaAulaList = chamadaAulaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idaluno != null ? idaluno.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Aluno)) {
            return false;
        }
        Aluno other = (Aluno) object;
        if ((this.idaluno == null && other.idaluno != null) || (this.idaluno != null && !this.idaluno.equals(other.idaluno))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
    
}
