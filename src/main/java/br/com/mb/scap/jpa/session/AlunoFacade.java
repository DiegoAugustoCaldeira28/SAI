/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jpa.session;

import br.com.mb.scap.jpa.entities.Aluno;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Matricula;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author matheus
 */
@Stateless
public class AlunoFacade extends AbstractFacade<Aluno> {

    @PersistenceContext(unitName = "br.com.mb_SCAP_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlunoFacade() {
        super(Aluno.class);
    }

    public Aluno findIdByRa(String RA) {
         List<Aluno> aluno = em.createNamedQuery("Aluno.findByRa").setParameter("ra", RA).getResultList();
        if (aluno.isEmpty()) {
            return null;
        }
        return aluno.get(0);
    }

    public Aluno findCPF(String cpf) {//Exemplo
        List<Aluno> aluno = em.createNamedQuery("Aluno.findByCpf").setParameter("cpf", cpf).getResultList();
        if (aluno.isEmpty()) {
            return null;
        }
        return aluno.get(0);
    }

    public List<Matricula> findAluno(Aluno aluno) {
        return em.createNamedQuery("Matricula.findByAluno").setParameter("aluno", aluno).getResultList();
    }

    public List<DataEvento> findEventoSai() {
        return em.createNamedQuery("DataEvento.findByEventosSai").getResultList();
    }
    
    public List<Aluno> listAllOrdenado() {
        return em.createNamedQuery("Aluno.listAllOrdenado").getResultList();
    }
}
