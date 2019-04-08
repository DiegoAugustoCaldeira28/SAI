/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.ChamadaEvento;
import br.com.mb.scap.jpa.entities.DataEvento;
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
    
    public List<DataEvento> buscaEventosAbertos(){
        return em.createNamedQuery("DataEvento.findEventosAberto").getResultList();
    }

}
