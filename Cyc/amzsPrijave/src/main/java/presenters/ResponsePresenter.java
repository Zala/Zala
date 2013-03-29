/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenters;

import com.mycompany.amzsprijave.ErrorFacade;
import com.mycompany.amzsprijave.Prijave;
import com.mycompany.amzsprijave.PrijaveFacade;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
    private String brand;
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
    
    int n;
    
    @Inject private PrijaveFacade facade;
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
         
      
        name = String.valueOf(podatki.get(0).getIme());
        surname = String.valueOf(podatki.get(0).getPriimek());
        brand = String.valueOf(podatki.get(0).getZnamka());
        type = String.valueOf(podatki.get(0).getTip());
        registration = String.valueOf(podatki.get(0).getRegistrska());
        member = String.valueOf(podatki.get(0).getClan());
        member_no = String.valueOf(podatki.get(0).getClanska_st());
        date = podatki.get(0).getDatum();
        parent2_malf = String.valueOf(podatki.get(0).getParent2_malf());
        parent_malf = String.valueOf(podatki.get(0).getParent_malf());
        malfunction = String.valueOf(podatki.get(0).getMalfunction());
        
        
        CycAccess c = new CycAccess("aidemo", 3600);
        carBodyType = issue.exportFromCycCarTypeList(c);
        
    }
    
    
   
    
    
       
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    

       

}