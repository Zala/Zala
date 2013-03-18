/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amzsprijave;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Zala
 */
@Named 
// Zakaj brez tega ne dela???
@Stateless
// kaj ma veze ta stateless?
public class ErrorFacade {
    
    
    private String grandparent;
    private String parent;
    private String malfunction;
    private List<String> parent_malfL;
    private List<String> parent2_malfL;
    private List<String> malfunctionL;
    
   
    @PersistenceContext(unitName="com.mycompany_amzsPrijave_war_1.0-SNAPSHOTPU")
    private EntityManager entityManager;
    
    public void saveError(Error error) {
        
        entityManager.persist(error);
    
    }
    
//    public List<Error> getError() {
//        Query q = entityManager.createQuery("FROM " + Error.class.getName() +" ORDER BY ID DESC");
//        List<Error> list = q.getResultList();
//        return list;
//    }
    
    
    public List<String> getByParent2() {
        Query q = entityManager.createQuery("SELECT DISTINCT parent2_malf FROM "+ Error.class.getName());
        List<String> list = q.getResultList();
        return list;
    }

    
    
    public void handleGrandparentChange() {  
        
        if(grandparent !=null && !grandparent.equals("")) {
            parent_malfL = getByGrandparent(grandparent);
        }  
    }
    
    
    public List<String> getByGrandparent(String grandp) {
        Query q = entityManager.createQuery("SELECT DISTINCT parent_malf FROM "+ Error.class.getName()
                + " WHERE parent2_malf = :grandp").setParameter("grandp", grandp);
        List<String> list = q.getResultList();
        return list;
    }
    
    
    
    
    
    public void handleParentChange() {  
        if(parent !=null && !parent.equals("")) {
            malfunctionL = getByParent(parent);
        }  
    }
    
    
    public List<String> getByParent(String par) {
        Query q = entityManager.createQuery("SELECT DISTINCT malfunction FROM "+ Error.class.getName()
                + " WHERE parent_malf = :par").setParameter("par", par);
        List<String> list = q.getResultList();
        return list;
    }
    
    
    
    
    
    
    public String getGrandparent() {
        return grandparent;
    }

    public void setGrandparent(String grandparent) {
        this.grandparent = grandparent;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(String malfunction) {
        this.malfunction = malfunction;
    }

    public List<String> getParent2_malfL() {
        return parent2_malfL;
    }

    public void setParent2_malfL(List<String> parent2_malfL) {
        this.parent2_malfL = parent2_malfL;
    }

    public List<String> getParent_malfL() {
        return parent_malfL;
    }

    public void setParent_malfL(List<String> parent_malfL) {
        this.parent_malfL = parent_malfL;
    }

    public List<String> getMalfunctionL() {
        return malfunctionL;
    }

    public void setMalfunctionL(List<String> malfunctionL) {
        this.malfunctionL = malfunctionL;
    }
}

