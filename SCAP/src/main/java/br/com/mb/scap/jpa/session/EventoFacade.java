/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Evento;
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

    public int vagasFechadas(Evento evento) {
        Long valor = (Long) em.createNamedQuery("Matricula.countVagasPagas").setParameter("evento", evento).getSingleResult();
        return valor.intValue();
    }
}
