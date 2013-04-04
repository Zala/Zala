/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenters;

import com.mycompany.amzsprijave.amzsIssue;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;
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

    private String assertionIssue;
    private String assertionEvent;
    private String printEvent;
    private String printMalfunction;
    private String printParent;
    private String printGrandparent;
    private String assertionVehicle;
    private String printRegistration;
    private String exportStringType;
    private String exportStringBrand;
    private String assertionSender;
    private String printName;
    private String printSurname;
    private String assertionMember;
    private String printMemberNo;
    private String assertionTopic;
    private String assertionOccursAt;
    private String assertionDate;
    private String assertionHlid;
    
    @Inject
    private amzsIssue issue;

    @PostConstruct
    private void postconstruct() throws UnknownHostException, IOException, JSONException {
        CycAccess c = new CycAccess("aidemo", 3600);
        assertionIssue = issue.importIntoCycIssue(c);
        assertionEvent = issue.importIntoCycEvent(c);
        printEvent = issue.printEvent();
        printMalfunction = issue.printMalfunction(c);
        printParent = issue.printParent();        
        printGrandparent = issue.printGrandparent();
        assertionVehicle = issue.importIntoCycVehicle(c);
        printRegistration = issue.printRegistration(c);
        exportStringType = issue.getType();
        assertionSender = issue.importIntoCycSender(c);
        printName = issue.printName(c);       
        printSurname = issue.printSurname(c);       
        assertionMember = issue.importIntoCycMember(c);
        printMemberNo = issue.printMemberNo(c);
        assertionTopic = issue.importIntoCycTopic(c);
//        assertionOccursAt = issue.importIntoCycOccursAt(c);
//        assertionDate = issue.importIntoCycDate(c);
    }
    
    
    

    public String getAssertionIssue() {
        return assertionIssue;
    }

    public void setAssertionIssue(String assertionIssue) {
        this.assertionIssue = assertionIssue;
    }

    public String getAssertionEvent() {
        return assertionEvent;
    }

    public void setAssertionEvent(String assertionEvent) {
        this.assertionEvent = assertionEvent;
    }

    public String getPrintEvent() {
        return printEvent;
    }

    public void setPrintEvent(String printEvent) {
        this.printEvent = printEvent;
    }

    public String getPrintMalfunction() {
        return printMalfunction;
    }

    public void setPrintMalfunction(String printMalfunction) {
        this.printMalfunction = printMalfunction;
    }

    public String getPrintParent() {
        return printParent;
    }

    public void setPrintParent(String printParent) {
        this.printParent = printParent;
    }

    public String getPrintGrandparent() {
        return printGrandparent;
    }

    public void setPrintGrandparent(String printGrandparent) {
        this.printGrandparent = printGrandparent;
    }

    public String getAssertionVehicle() {
        return assertionVehicle;
    }

    public void setAssertionVehicle(String assertionVehicle) {
        this.assertionVehicle = assertionVehicle;
    }

    public String getPrintRegistration() {
        return printRegistration;
    }

    public void setPrintRegistration(String printRegistration) {
        this.printRegistration = printRegistration;
    }

    public String getExportStringBrand() {
        return exportStringBrand;
    }

    public void setExportStringBrand(String exportStringBrand) {
        this.exportStringBrand = exportStringBrand;
    }

    public String getExportStringType() {
        return exportStringType;
    }

    public void setExportStringType(String exportStringType) {
        this.exportStringType = exportStringType;
    }
    
    public String getAssertionSender() {
        return assertionSender;
    }

    public void setAssertionSender(String assertionSender) {
        this.assertionSender = assertionSender;
    }

    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String printName) {
        this.printName = printName;
    }

    public String getPrintSurname() {
        return printSurname;
    }

    public void setPrintSurname(String printSurname) {
        this.printSurname = printSurname;
    }

    public String getAssertionMember() {
        return assertionMember;
    }

    public void setAssertionMember(String assertionMember) {
        this.assertionMember = assertionMember;
    }

    public String getPrintMemberNo() {
        return printMemberNo;
    }

    public void setPrintMemberNo(String printMemberNo) {
        this.printMemberNo = printMemberNo;
    }
    
    public String getAssertionTopic() {
        return assertionTopic;
    }

    public void setAssertionTopic(String assertionTopic) {
        this.assertionTopic = assertionTopic;
    }

    public String getAssertionOccursAt() {
        return assertionOccursAt;
    }

    public void setAssertionOccursAt(String assertionOccursAt) {
        this.assertionOccursAt = assertionOccursAt;
    }

    public String getAssertionDate() {
        return assertionDate;
    }

    public void setAssertionDate(String assertionDate) {
        this.assertionDate = assertionDate;
    }

    public String getAssertionHlid() {
        return assertionHlid;
    }

    public void setAssertionHlid(String assertionHlid) {
        this.assertionHlid = assertionHlid;
    }

    
       
    
    public String action2() {
        return "index.xhtml?faces-redirect=true";
        // mora unassertat
    }
    
    public String cure() throws JSONException, UnknownHostException, CycApiException, IOException {
        CycAccess c = new CycAccess("aidemo", 3600);
        assertionHlid = issue.importIntoCycHlid(c);
        return "http://aidemo:3603/cure/edit.jsp?conceptid=" +assertionHlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
    }
}
