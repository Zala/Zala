
package si.ijs.mobis.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class BaseService{
        
        @PersistenceContext(unitName="si.ijs.mobis_amzsPrijave_war_1.0-SNAPSHOTPU")
        private EntityManager entityManager;
        
        
        public List<String> getGPList() {
                Query q = entityManager.createQuery("SELECT DISTINCT parent2_malf FROM "+ Error.class.getName());
                List<String> gpList = q.getResultList();
                return gpList;
        }  
        
        public List<String> getByGPList(String grandp) {
                Query q = entityManager.createQuery("SELECT DISTINCT parent_malf FROM "+ Error.class.getName()
                        + " WHERE parent2_malf = :grandp").setParameter("grandp", grandp);
                List<String> list = q.getResultList();
                return list;
        }
        
        public List<String> getByPList(String par) {
                Query q = entityManager.createQuery("SELECT DISTINCT malfunction FROM "+ si.ijs.mobis.service.Error.class.getName()
                        + " WHERE parent_malf = :par").setParameter("par", par);
                List<String> list = q.getResultList();
                return list;
        }
        
        public void save(Object obj) {
            entityManager.persist(obj);
        }
        
        public void saveData(Prijave pr) {
            java.util.Date date = new Date();
            pr.setDatum(date);
            save(pr);
        }
        
        public Prijave updateEntry(Prijave pr) {
           try{
                Query q = entityManager.createNativeQuery("DELETE FROM prijave ORDER BY ID DESC LIMIT 1");
                q.executeUpdate();
//                pr = entityManager.merge(pr);
//                saveData(pr);
                }
            
            catch (Exception e) {
                System.out.println(e);
            }
            return pr;
        }
                
        public List<Prijave> getData() {
                Query q = entityManager.createQuery("FROM " + Prijave.class.getName() +" ORDER BY ID DESC");
                List<Prijave> list = q.getResultList();
                return list;
        }
        
        public Prijave getLastEntry() {
                Query q = entityManager.createQuery("FROM " + Prijave.class.getName() +" ORDER BY ID DESC LIMIT 1");
                List<Prijave> list = q.getResultList();
                Prijave last = list.get(0);
                return last;
        }
        
}
