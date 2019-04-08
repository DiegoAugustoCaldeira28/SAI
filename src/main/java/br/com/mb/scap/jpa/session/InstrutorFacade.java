/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jpa.entities.Usuario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author matheus
 */
@Stateless
public class InstrutorFacade extends AbstractFacade<Instrutor> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public Instrutor findByCPF(String cpf){
        return (Instrutor) em.createNamedQuery("Instrutor.findByCpf").setParameter("cpf", cpf).getSingleResult();
    }
    
    public Instrutor findByNome(String nome){
        return (Instrutor) em.createNamedQuery("Instrutor.findByNome").setParameter("nome", nome).getSingleResult();
    }
    
    public InstrutorFacade() {
        super(Instrutor.class);
    }
    
}
