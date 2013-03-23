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
    private String assertionVehicle;
    private String assertionSender;
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
        assertionVehicle = issue.importIntoCycVehicle(c);
//        assertionSender = issue.importIntoCycSender(c);
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

    public String getAssertionVehicle() {
        return assertionVehicle;
    }

    public void setAssertionVehicle(String assertionVehicle) {
        this.assertionVehicle = assertionVehicle;
    }
    
    public String getAssertionSender() {
        return assertionSender;
    }

    public void setAssertionSender(String assertionSender) {
        this.assertionSender = assertionSender;
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
