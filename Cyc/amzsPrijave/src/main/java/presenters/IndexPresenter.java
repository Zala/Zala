package presenters;


import com.mycompany.amzsprijave.ErrorFacade;
import com.mycompany.amzsprijave.Prijave;
import com.mycompany.amzsprijave.PrijaveFacade;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zala
 */
@Named
@RequestScoped
public class IndexPresenter {

    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private ErrorFacade errorFacade;
    
       
    
    public Prijave getPrijava() {
        return prijava;
    }

    public void setPrijava(Prijave prijava) {
        this.prijava = prijava;
    }

    public ErrorFacade getErrorFacade() {
        return errorFacade;
    }

    public void setErrorFacade(ErrorFacade errorFacade) {
        this.errorFacade = errorFacade;
    }
           
    /**
     * Vnos ocene v bazo s pomočjo SLSB
     */
    public String asserting() {
        prijava.setParent2_malf(errorFacade.getGrandparent());
        prijava.setParent_malf(errorFacade.getParent());
        prijava.setMalfunction(errorFacade.getMalfunction());
        facade.shraniPrijavo(prijava);
        return "asserting.xhtml?faces-redirect=true";
    }
                 
    public String response() {
        return "response.xhtml?faces-redirect=true";
    }
}
