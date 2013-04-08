/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amzsprijave;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
/**
 *
 * @author Zala
 */
@Stateless
public class PrijaveFacade {
    
    @PersistenceContext(unitName="com.mycompany_amzsPrijave_war_1.0-SNAPSHOTPU")
    private EntityManager entityManager;
    
    public void shraniPrijavo(Prijave pr) {
        java.util.Date date = new Date();
        
        pr.setDatum(date);
        
        entityManager.persist(pr);
    }
    



    public List<Prijave> getPrijave() {
        Query q = entityManager.createQuery("FROM " + Prijave.class.getName() +" ORDER BY ID DESC");
        List<Prijave> list = q.getResultList();
        return list;
    }
    
//    public Prijave getById(int id) {
//        Query q = entityManager.createQuery("from " + Prijave.class.getName() + " where id = :id")
//                                .setParameter("id", id);
//        return (Prijave) q.getSingleResult();
//    }

    
    
}

