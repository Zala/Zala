/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.ijs.mobis.presenters;

import si.ijs.mobis.service.Prijave;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import si.ijs.mobis.org.json.JSONException;
import si.ijs.mobis.service.BaseService;

/**
 *
 * @author Zala
 */
@ManagedBean
@Named
@RequestScoped
public class IndexPresenter {
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
    
    private List<Prijave> podatki;

    
    int n;
    
    @Inject private BaseService baseService;
    
    
    @PostConstruct
    public void postconstruct() throws ParseException, UnknownHostException, IOException, JSONException{
                  
        podatki = baseService.getData();
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
                
    }
    
    
    public String response() {
        return "response.xhtml?faces-redirect=true";
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


}