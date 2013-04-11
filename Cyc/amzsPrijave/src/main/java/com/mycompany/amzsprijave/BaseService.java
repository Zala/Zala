//
//package com.mycompany.amzsprijave;
//
//import java.util.List;
//import javax.ejb.Stateless;
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//
//@Stateless
//public class BaseService{
//        
//        private EntityManager entityManager;
//        
//        
//        public List<String> getGPList(){
//            Query q = entityManager.createQuery("SELECT DISTINCT parent2_malf FROM "+ Error.class.getName());
//            List<String> gpList = q.getResultList();
//            return gpList;
//        }  
//        
//        
//}
