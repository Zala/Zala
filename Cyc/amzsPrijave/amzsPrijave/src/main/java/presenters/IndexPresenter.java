package presenters;


import com.mycompany.amzsprijave.Error;
import com.mycompany.amzsprijave.Prijave;
import com.mycompany.amzsprijave.PrijaveFacade;
import com.mycompany.amzsprijave.AmzsIssue;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycList;

/**
 *
 * @author Zala
 */
@ManagedBean
@ViewScoped
public class IndexPresenter implements Serializable {
    
    @PersistenceContext(unitName="com.mycompany_amzsPrijave_war_1.0-SNAPSHOTPU")
    private EntityManager entityManager;
    
    private String grandparent;
    private String parent;
    private String malfunction;
    private String inputBrand;
    private List<String> parent_malfL;
    private List<String> parent2_malfL;
    private List<String> malfunctionL;
        
    private CycList carBodyType;
    private CycList carBrand;
    private CycList carModel;
    private ArrayList<String> modelL;
    
    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private AmzsIssue issue;
    
    @PostConstruct
   public void postconstruct() throws UnknownHostException, IOException, JSONException{
        CycAccess c = new CycAccess("aidemo", 3600);
        carBrand = issue.exportFromCycCarBrandList(c);
        
        
        Query q = entityManager.createQuery("SELECT DISTINCT parent2_malf FROM "+ Error.class.getName());
        parent2_malfL = q.getResultList();
        
        inputBrand = "";
    }    
    
    
    public CycList carBodyTypeStrings() throws UnknownHostException, IOException, JSONException{
        CycAccess c = new CycAccess("aidemo", 3600);
        HashMap<Object, Object> map = issue.exportFromCycCarTypeList(c);
        carBodyType = new CycList();
        for ( Map.Entry<Object, Object> entry :  map.entrySet()){
            Object nameString = entry.getKey();
            carBodyType.add(nameString);
        }
        return carBodyType;
    }
    
    public ArrayList<String> handleBrandChange() throws JSONException, UnknownHostException, CycApiException, IOException {  
                    CycAccess c = new CycAccess("aidemo", 3600);
                    modelL = new ArrayList<String>();
                    
                    if(inputBrand != null) {
                        HashMap<Object, Object> map = issue.getModelByBrand(c, inputBrand);
                        for (Map.Entry<Object, Object> entry :  map.entrySet()){
                            Object nameString = entry.getKey();
                            modelL.add(String.valueOf(nameString));
                        }
                    }  
                    return modelL;
    }
    
    
    
    public void saveError(Error error) {
        entityManager.persist(error);
    }
    
    
    public List<String> getByParent2() {
        return parent2_malfL;
    }

    
    
    public void handleGrandparentChange() {  
        
        if(grandparent != null && !grandparent.isEmpty()) {
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
        Query q = entityManager.createQuery("SELECT DISTINCT malfunction FROM "+ com.mycompany.amzsprijave.Error.class.getName()
                + " WHERE parent_malf = :par").setParameter("par", par);
        List<String> list = q.getResultList();
        return list;
    }
    
    
    
    
    public String asserting() {
        prijava.setParent2_malf(getGrandparent());
        prijava.setParent_malf(getParent());
        prijava.setMalfunction(getMalfunction());
        facade.shraniPrijavo(prijava);
        return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
    }
                 
    
    
    
    
    
    
    public String response() {
        return "response.xhtml?faces-redirect=true";
    }
    
    public Prijave getPrijava() {
        return prijava;
    }

    public void setPrijava(Prijave prijava) {
        this.prijava = prijava;
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

    public String getInputBrand() {
        return inputBrand;
    }

    public void setInputBrand(String inputBrand) {
        this.inputBrand = inputBrand;
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

    public CycList getCarBodyType() {
        return carBodyType;
    }

    public void setCarBodyType(CycList carBodyType) {
        this.carBodyType = carBodyType;
    }

    public CycList getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(CycList carBrand) {
        this.carBrand = carBrand;
    }

    public CycList getCarModel() {
        return carModel;
    }

    public void setCarModel(CycList carModel) {
        this.carModel = carModel;
    }

    public ArrayList<String> getModelL() {
        return modelL;
    }

    public void setModelL(ArrayList<String> modelL) {
        this.modelL = modelL;
    }
    
}
