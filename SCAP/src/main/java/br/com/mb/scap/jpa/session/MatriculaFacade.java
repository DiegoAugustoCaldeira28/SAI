/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Matricula;
import br.com.mb.scap.jpa.entities.Aluno;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author matheus
 */
@Stateless
public class MatriculaFacade extends AbstractFacade<Matricula> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MatriculaFacade() {
        super(Matricula.class);
    }
    
    public List<Matricula> findByAlunoNPago(Aluno aluno) {
        return em.createNamedQuery("Matricula.findByAlunoNPago").setParameter("aluno", aluno).getResultList();
    }
    
    public List<Matricula> findByAlunoPago(Aluno aluno) {
        return em.createNamedQuery("Matricula.findByAlunoPago").setParameter("aluno", aluno).getResultList();
    }
    
    public Long countPosicao(Matricula matricula) {
        System.out.println("Chegou Facade");
        return (Long) em.createNamedQuery("Matricula.contPosicao")
                .setParameter("evento", matricula.getIdevento())
                .getSingleResult();
    }
    
    public List<Aluno> listAllOrdenado() {
        return em.createNamedQuery("Aluno.listAllOrdenado").getResultList();
    }
    
    
}
