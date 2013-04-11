/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenters;

import com.mycompany.amzsprijave.AmzsIssue;
import com.mycompany.amzsprijave.CycService;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;

/**
 *
 * @author Zala
 */
@Named
@RequestScoped
public class AssertingPresenter {
    
    private static final Logger log = Logger.getLogger(AssertingPresenter.class.getName());

//    private String assertionIssue;
//    private String assertionEvent;
//    private String printEvent;
//    private String printMalfunction;
//    private String printParent;
//    private String printGrandparent;
    private String assertionVehicle;
//    private String printModel;
//    private String printRegistration;
//    private String exportStringType;
    private String inputBrand;
//    private String exportStringBrand;
//    private String assertionSender;
//    private String printName;
//    private String printSurname;
//    private String assertionMember;
//    private String printMemberNo;
//    private String assertionTopic;
//    private String assertionOccursAt;
//    private String assertionDate;
//    private String assertionHlid;
    
    @Inject private AmzsIssue issue;
//    @Inject private CycService cycService;

    @PostConstruct
    private void postconstruct() throws UnknownHostException, IOException, JSONException {
        
//        if (log.isLoggable(Level.FINE))
//            log.fine("initializing assertingPresenter");
//        
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, String> parameterMap = (Map<String, String>) context.getRequestParameterMap();
        if (!parameterMap.containsKey("ib")) {
            if (log.isLoggable(Level.WARNING))
                log.warning("Parameter ib missing!");
            throw new IllegalArgumentException("Parameter ib missing!");
        }
        
        inputBrand = parameterMap.get("ib");
            
//        CycAccess c = new CycAccess("aidemo", 3600);
//        assertionIssue = issue.importIntoCycIssue();
//        assertionEvent = issue.importIntoCycEvent(c);
//        printEvent = issue.printEvent();
//        printMalfunction = issue.printMalfunction();
//        printParent = issue.printParent();        
//        printGrandparent = issue.printGrandparent();
        assertionVehicle = issue.importIntoCycModel(inputBrand);
//        printModel = issue.getModel();
//                String.valueOf(cycService.getModelByBrand(c, inputBrand).get(inputBrand));
//        printRegistration = issue.printRegistration();
//        exportStringType = issue.getType();
//        assertionSender = issue.importIntoCycSender();
//        printName = issue.printName();       
//        printSurname = issue.printSurname();       
//        assertionMember = issue.importIntoCycMember();
//        printMemberNo = issue.printMemberNo(c);
//        assertionTopic = issue.importIntoCycTopic();
//        assertionOccursAt = issue.importIntoCycOccursAt();
//        assertionDate = issue.importIntoCycDate();
    }
    
    
   
//    public String getAssertionIssue() {
//        return assertionIssue;
//    }
//
//    public void setAssertionIssue(String assertionIssue) {
//        this.assertionIssue = assertionIssue;
//    }
//
//    public String getAssertionEvent() {
//        return assertionEvent;
//    }
//
//    public void setAssertionEvent(String assertionEvent) {
//        this.assertionEvent = assertionEvent;
//    }
//
//    public String getPrintEvent() {
//        return printEvent;
//    }
//
//    public void setPrintEvent(String printEvent) {
//        this.printEvent = printEvent;
//    }
//
//    public String getPrintMalfunction() {
//        return printMalfunction;
//    }
//
//    public void setPrintMalfunction(String printMalfunction) {
//        this.printMalfunction = printMalfunction;
//    }
//
//    public String getPrintParent() {
//        return printParent;
//    }
//
//    public void setPrintParent(String printParent) {
//        this.printParent = printParent;
//    }
//
//    public String getPrintGrandparent() {
//        return printGrandparent;
//    }
//
//    public void setPrintGrandparent(String printGrandparent) {
//        this.printGrandparent = printGrandparent;
//    }
//
    public String getAssertionVehicle() {
        return assertionVehicle;
    }

    public void setAssertionVehicle(String assertionVehicle) {
        this.assertionVehicle = assertionVehicle;
    }
//
//    public String getPrintRegistration() {
//        return printRegistration;
//    }
//
//    public void setPrintRegistration(String printRegistration) {
//        this.printRegistration = printRegistration;
//    }
//
//    public String getExportStringBrand() {
//        return exportStringBrand;
//    }
//
//    public void setExportStringBrand(String exportStringBrand) {
//        this.exportStringBrand = exportStringBrand;
//    }
//
//    public String getExportStringType() {
//        return exportStringType;
//    }
//
//    public void setExportStringType(String exportStringType) {
//        this.exportStringType = exportStringType;
//    }
//    
//    public String getAssertionSender() {
//        return assertionSender;
//    }
//
//    public void setAssertionSender(String assertionSender) {
//        this.assertionSender = assertionSender;
//    }
//
//    public String getPrintName() {
//        return printName;
//    }
//
//    public void setPrintName(String printName) {
//        this.printName = printName;
//    }
//
//    public String getPrintSurname() {
//        return printSurname;
//    }
//
//    public void setPrintSurname(String printSurname) {
//        this.printSurname = printSurname;
//    }
//
//    public String getAssertionMember() {
//        return assertionMember;
//    }
//
//    public void setAssertionMember(String assertionMember) {
//        this.assertionMember = assertionMember;
//    }
//
//    public String getPrintMemberNo() {
//        return printMemberNo;
//    }
//
//    public void setPrintMemberNo(String printMemberNo) {
//        this.printMemberNo = printMemberNo;
//    }
//    
//    public String getAssertionTopic() {
//        return assertionTopic;
//    }
//
//    public void setAssertionTopic(String assertionTopic) {
//        this.assertionTopic = assertionTopic;
//    }
//
//    public String getAssertionOccursAt() {
//        return assertionOccursAt;
//    }
//
//    public void setAssertionOccursAt(String assertionOccursAt) {
//        this.assertionOccursAt = assertionOccursAt;
//    }
//
//    public String getAssertionDate() {
//        return assertionDate;
//    }
//
//    public void setAssertionDate(String assertionDate) {
//        this.assertionDate = assertionDate;
//    }
//
//    public String getAssertionHlid() {
//        return assertionHlid;
//    }
//
//    public void setAssertionHlid(String assertionHlid) {
//        this.assertionHlid = assertionHlid;
//    }
//
    public String getInputBrand() {
        return inputBrand;
    }

    public void setInputBrand(String inputBrand) {
        this.inputBrand = inputBrand;
    }
//
//    public String getPrintModel() {
//        return printModel;
//    }
//
//    public void setPrintModel(String printModel) {
//        this.printModel = printModel;
//    }

    
       
    
    public String action2() {
        return "index.xhtml?faces-redirect=true";
        // mora unassertat
    }
    
//    public String cure() throws JSONException, UnknownHostException, CycApiException, IOException {
//        CycAccess c = new CycAccess("aidemo", 3600);
//        assertionHlid = issue.importIntoCycHlid(c);
//        return "http://aidemo:3603/cure/edit.jsp?conceptid=" +assertionHlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
//    }
}
