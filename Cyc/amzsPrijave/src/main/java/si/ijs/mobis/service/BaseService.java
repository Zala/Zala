
package si.ijs.mobis.service;

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
        
        public List<Prijave> getData() {
                Query q = entityManager.createQuery("FROM " + Prijave.class.getName() +" ORDER BY ID DESC");
                List<Prijave> list = q.getResultList();
                return list;
        }
        
}
