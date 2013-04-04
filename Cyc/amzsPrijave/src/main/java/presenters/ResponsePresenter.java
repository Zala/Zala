/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenters;

import com.mycompany.amzsprijave.ErrorFacade;
import com.mycompany.amzsprijave.Prijave;
import com.mycompany.amzsprijave.PrijaveFacade;
import com.mycompany.amzsprijave.amzsIssue;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.cycobject.CycList;

/**
 *
 * @author Zala
 */
@ManagedBean
@Named
@RequestScoped
public class ResponsePresenter {
    private String name;
    private String surname;
    private String model;
    private String type;
    private String registration;
    private String member;
    private String member_no;
    private String parent2_malf;
    private String parent_malf;
    private String malfunction;
    private Date date;
    
    private List<String> parent2_malfL;
    private List<Prijave> podatki;

    private CycList carBodyType;
    private CycList carBrand;
    private CycList carModel;

    
    int n;
    
    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private ErrorFacade errorFacade;
    @Inject private amzsIssue issue;
    
    
    @PostConstruct
    public void postconstruct() throws ParseException, UnknownHostException, IOException, JSONException{
          
        parent2_malfL = errorFacade.getByParent2();
        
        podatki = facade.getPrijave();
        n = podatki.size()-1; 
        
         if (podatki.isEmpty()){
            return;
        }
         
      
        name = podatki.get(0).getIme();
        surname = podatki.get(0).getPriimek();
        model = podatki.get(0).getZnamka();
        type = podatki.get(0).getTip();
        registration = podatki.get(0).getRegistrska();
        member = podatki.get(0).getClan();
        member_no = podatki.get(0).getClanska_st();
        date = podatki.get(0).getDatum();
        parent2_malf = podatki.get(0).getParent2_malf();
        parent_malf = podatki.get(0).getParent_malf();
        malfunction = podatki.get(0).getMalfunction();
        
        
        CycAccess c = new CycAccess("aidemo", 3600);
        carBrand = issue.exportFromCycCarBrandList(c);
        
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
    
//    Zakaj to ne dela???
//    public String asserting() {
//        prijava.setParent2_malf(errorFacade.getGrandparent());
//        prijava.setParent_malf(errorFacade.getParent());
//        prijava.setMalfunction(errorFacade.getMalfunction());
//        facade.shraniPrijavo(prijava);
//        return "asserting.xhtml?faces-redirect=true";
//    }

    
    
    
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMember_no() {
        return member_no;
    }

    public void setMember_no(String member_no) {
        this.member_no = member_no;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public List<Prijave> getPodatki() {
        return podatki;
    }
    
    public void setPodatki(List<Prijave> podatki) {
        this.podatki = podatki;
    }

    public List<String> getParent2_malfL() {
        return parent2_malfL;
    }

    public void setParent2_malfL(List<String> parent2_malfL) {
        this.parent2_malfL = parent2_malfL;
    }

    public String getParent2_malf() {
        return parent2_malf;
    }

    public void setParent2_malf(String parent2_malf) {
        this.parent2_malf = parent2_malf;
    }

    public String getParent_malf() {
        return parent_malf;
    }

    public void setParent_malf(String parent_malf) {
        this.parent_malf = parent_malf;
    }

    public String getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(String malfunction) {
        this.malfunction = malfunction;
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

    
    public ErrorFacade getErrorFacade() {
        return errorFacade;
    }

    public void setErrorFacade(ErrorFacade errorFacade) {
        this.errorFacade = errorFacade;
    }
           

       

}