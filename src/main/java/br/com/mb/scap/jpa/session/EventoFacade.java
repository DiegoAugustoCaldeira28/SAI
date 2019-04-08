/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Instrutor;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author matheus
 */
@Stateless
public class EventoFacade extends AbstractFacade<Evento> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFacade() {
        super(Evento.class);
    }

    public List<Evento> findCurso(String curso) {
        return em.createNamedQuery("Evento.findByCurso").setParameter("curso", curso).getResultList();
    }
    
    public Evento findbyIdEvento(String idEvento) {
         List<Evento> aluno = em.createNamedQuery("Evento.findByIdevento").setParameter("idevento", idEvento).getResultList();
        if (aluno.isEmpty()) {
            return null;
        }
        return aluno.get(0);
    }

    public int vagasFechadas(Evento evento) {
        Long valor = (Long) em.createNamedQuery("Matricula.countVagasPagas").setParameter("evento", evento).getSingleResult();
        return valor.intValue();
    }
    
    public List<Evento> findbyAll() {
        return em.createNamedQuery("Evento.findAll").getResultList();
    }
    
    public List<Evento> findbyInstrutor(Instrutor instrutor) {
        return em.createNamedQuery("Evento.findByInstrutor").setParameter("idinstrutor", instrutor.getIdinstrutor()).getResultList();
    }
    
    public List<Evento> findbyInstrutorMini(Instrutor instrutor) {
        return em.createNamedQuery("Evento.findByInstrutorMini").setParameter("idinstrutor", instrutor.getIdinstrutor()).getResultList();
    }
}
