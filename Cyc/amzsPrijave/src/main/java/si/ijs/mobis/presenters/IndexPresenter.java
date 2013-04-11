package si.ijs.mobis.presenters;


import si.ijs.mobis.service.BaseService;
import si.ijs.mobis.service.Error;
import si.ijs.mobis.service.Prijave;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import si.ijs.mobis.org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycList;
import si.ijs.mobis.service.CycService;

/**
 *
 * @author Zala
 */
@ManagedBean
@ViewScoped
public class IndexPresenter implements Serializable {
        
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
    
    private CycAccess c;
    
//    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private CycService cycService;
    @Inject private BaseService baseService;
    
    @PostConstruct
   public void postconstruct() throws UnknownHostException, IOException, JSONException{
            c = new CycAccess("aidemo", 3600);
            carBrand = cycService.carBrandStrings();

            parent2_malfL = baseService.getGPList();

            inputBrand = "";
    }    
    
    
    public CycList carBodyTypeStrings() throws UnknownHostException, IOException, JSONException{
            HashMap<Object, Object> map = cycService.exportFromCycCarTypeList(c);
            carBodyType = new CycList();
            for ( Map.Entry<Object, Object> entry :  map.entrySet()){
                Object nameString = entry.getKey();
                carBodyType.add(nameString);
            }
            return carBodyType;
    }
    
    public ArrayList<String> handleBrandChange() throws JSONException, UnknownHostException, CycApiException, IOException {  
            modelL = new ArrayList<String>();

            if(inputBrand != null) {
                HashMap<Object, Object> map = cycService.getModelByBrand(c, inputBrand);
                for (Map.Entry<Object, Object> entry :  map.entrySet()){
                    Object nameString = entry.getKey();
                    modelL.add(String.valueOf(nameString));
                }
            }  
            return modelL;
    }
    
    
    
    public void saveError(Error error) {
            baseService.save(error);
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
            return baseService.getByGPList(grandp); 
         
    }
    
    
    public void handleParentChange() {  
            if(parent !=null && !parent.equals("")) {
                malfunctionL = getByParent(parent);
            }  
    }
    
    
    public List<String> getByParent(String par) {
            return baseService.getByPList(par);
    }
    
    
    
    
    public String asserting() {
            prijava.setParent2_malf(getGrandparent());
            prijava.setParent_malf(getParent());
            prijava.setMalfunction(getMalfunction());
            baseService.saveData(prijava);
            return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
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
