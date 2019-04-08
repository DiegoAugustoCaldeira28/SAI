/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Aluno;
import br.com.mb.scap.jpa.entities.ChamadaEvento;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Matricula;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author matheus
 */
@Stateless
public class ChamadaEventoFacade extends AbstractFacade<ChamadaEvento> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ChamadaEventoFacade() {
        super(ChamadaEvento.class);
    }
    
    public List<DataEvento> findAllEventos(){
        List <DataEvento> list = em.createNamedQuery("ChamadaEvento.findAllDataEvento").getResultList();
        return list;
    }
    
    public ChamadaEvento findChamadaEvento(ChamadaEvento current) {
        List<ChamadaEvento> list = em.createNamedQuery("ChamadaEvento.findByAlunoandDataEvento")
                .setParameter("aluno", current.getIdaluno())
                .setParameter("dataEvento", current.getIddataEvento())
                .getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public List<ChamadaEvento> carregaChamadaPalestra(DataEvento dataEvento) {
        return em.createNamedQuery("ChamadaEvento.findByIddataEvento").setParameter("iddataEvento", dataEvento).getResultList();
    }

    public Long TotalPalestra(Integer iddataEvento) {
        return (Long) em.createNamedQuery("ChamadaEvento.CountTotal").setParameter("dataEvento", iddataEvento).getSingleResult();
    }

    public List<DataEvento> buscaEventosAbertos() {
        return em.createNamedQuery("DataEvento.findEventosAberto").getResultList();
    }

    public ArrayList<Aluno> buscaAlunosDentro(DataEvento dataEvento) {
        ArrayList<Aluno> alunos = new ArrayList<>(em.createNamedQuery("ChamadaEvento.findByAlunoImparAndDataEvento")
                .setParameter("dataEvento", dataEvento).getResultList());
        ArrayList<Aluno> alunosDentro = new ArrayList<>();
        for (Aluno aluno : alunos) {
            Long qtde = (Long) em.createNamedQuery("ChamadaEvento.CountByAlunoDataEvento")
                    .setParameter("dataEvento", dataEvento)
                    .setParameter("aluno", aluno).getSingleResult();
            //System.out.println(aluno.getNome() + " "+ qtde);
            
                alunosDentro.add(aluno);
            
        }
        return alunosDentro;
    }

    public Matricula alunoPacoteSAIPago(Aluno a) {
        List<Matricula> m = em.createNamedQuery("Matricula.PacoteSaiPAgo")
                .setParameter("aluno", a)
                .setParameter("sai", 100).getResultList();
        if (m.isEmpty()) {
            return null;
        } else {
            return m.get(0);
        }
    }

    public Matricula alunoEventoPago(Aluno a, Evento e) {
        List<Matricula> m = em.createNamedQuery("Matricula.EventoPago")
                .setParameter("aluno", a)
                .setParameter("evento", e).getResultList();
        if (m.isEmpty()) {
            return null;
        } else {
            return m.get(0);
        }
    }

}
