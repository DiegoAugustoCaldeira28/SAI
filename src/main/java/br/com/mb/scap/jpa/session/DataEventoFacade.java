/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.ChamadaEvento;
import br.com.mb.scap.jpa.entities.DataEvento;
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
public class DataEventoFacade extends AbstractFacade<DataEvento> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DataEventoFacade() {
        super(DataEvento.class);
    }

    public List<DataEvento> findCurso(String curso) {
        return em.createNamedQuery("DataEvento.findByEventoCurso").setParameter("curso", curso).getResultList();
    }

    public int vagasFechadas(Evento evento) {
        Long valor = (Long) em.createNamedQuery("Matricula.countVagasPagas").setParameter("evento", evento).getSingleResult();
        return valor.intValue();
    }
    
    public List<DataEvento> allDataEvento(int id) {
        return em.createNamedQuery("DataEvento.findByEvento").setParameter("idevento", id).getResultList();
    }
    
    public DataEvento uniqueDataEvento(int id) {
        return (DataEvento) em.createNamedQuery("DataEvento.findByEvento").setParameter("idevento", id).getSingleResult();
    }
    
    public List<ChamadaAula> carregaChamadaAula(DataEvento dataEvento) {
        return em.createNamedQuery("ChamadaAula.findByIddataEvento").setParameter("iddataEvento", dataEvento).getResultList();
    }
    
    public List<Chamada> carregaChamada(DataEvento dataEvento) {
        return em.createNamedQuery("Chamada.findByIddataEvento").setParameter("iddataEvento", dataEvento).getResultList();
    }
    
    public List<ChamadaEvento> carregaChamadaEventoImpar(DataEvento dataEvento) {
        return em.createNamedQuery("ChamadaEvento.findByIddataEvento").setParameter("iddataEvento", dataEvento).getResultList();
    }
    
    public List<DataEvento> controleChamada(){
        return em.createNamedQuery("DataEvento.findTipo").getResultList();
    }
    
    public List<DataEvento> listAllNotMiniCurso(){
        return em.createNamedQuery("DataEvento.allNotMinicurso").getResultList();
    }
    
    public List<DataEvento> controleChamadaAula(){
        return em.createNamedQuery("DataEvento.findTipoMinicurso").getResultList();
    }
        
}
