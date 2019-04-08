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
@Table(name = "matricula")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Matricula.findAll", query = "SELECT m FROM Matricula m")
    , @NamedQuery(name = "Matricula.findByIdmatricula", query = "SELECT m FROM Matricula m WHERE m.idmatricula = :idmatricula")
    , @NamedQuery(name = "Matricula.findByPago", query = "SELECT m FROM Matricula m WHERE m.pago = :pago")
    , @NamedQuery(name = "Matricula.findByAlunoNPago", query = "SELECT m FROM Matricula m INNER JOIN Evento e ON m.idevento = e WHERE m.idaluno = :aluno AND m.pago = false ORDER BY e.tipo ASC, e.nome ASC")
    , @NamedQuery(name = "Matricula.findByAlunoPago", query = "SELECT m FROM Matricula m INNER JOIN Evento e ON m.idevento = e WHERE m.idaluno = :aluno AND m.pago = true ORDER BY e.tipo ASC, e.nome ASC")
    , @NamedQuery(name = "Matricula.countVagas", query = "SELECT COUNT(m.idaluno) FROM Matricula m WHERE m.idevento = :evento")
    , @NamedQuery(name = "Matricula.findByIdusuario", query = "SELECT m FROM Matricula m WHERE m.idusuario = :idusuario")
    , @NamedQuery(name = "Matricula.contPosicao", query = "SELECT COUNT(m.idmatricula) FROM Matricula m WHERE m.idevento = :evento AND m.pago = true")
    , @NamedQuery(name = "Matricula.findByAluno", query = "SELECT m FROM Matricula m WHERE m.idaluno = :aluno")
    , @NamedQuery(name = "Matricula.countVagasPagas", query = "SELECT COUNT(m.idaluno) FROM Matricula m WHERE m.idevento = :evento AND m.pago = true")
    , @NamedQuery(name = "Matricula.PacoteSaiPAgo", query = "SELECT m FROM Matricula m WHERE m.idaluno = :aluno AND m.idevento.idevento = :sai")
    , @NamedQuery(name = "Matricula.EventoPago", query = "SELECT m FROM Matricula m WHERE m.idaluno = :aluno AND m.idevento = :evento")})
public class Matricula implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idmatricula")
    private Integer idmatricula;
    @Column(name = "pago")
    private Boolean pago;
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = true)
    private Usuario idusuario;
    @JoinColumn(name = "idaluno", referencedColumnName = "idaluno")
    @ManyToOne(optional = false)
    private Aluno idaluno;
    @JoinColumn(name = "idevento", referencedColumnName = "idevento")
    @ManyToOne(optional = false)
    private Evento idevento;

    public Matricula() {
    }

    public Matricula(Integer idmatricula) {
        this.idmatricula = idmatricula;
    }

    public Integer getIdmatricula() {
        return idmatricula;
    }

    public void setIdmatricula(Integer idmatricula) {
        this.idmatricula = idmatricula;
    }

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public Usuario getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Usuario idusuario) {
        this.idusuario = idusuario;
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
        hash += (idmatricula != null ? idmatricula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Matricula)) {
            return false;
        }
        Matricula other = (Matricula) object;
        return !((this.idmatricula == null && other.idmatricula != null) || (this.idmatricula != null && !this.idmatricula.equals(other.idmatricula)));
    }

    @Override
    public String toString() {
        return "" + idevento;
    }

}
