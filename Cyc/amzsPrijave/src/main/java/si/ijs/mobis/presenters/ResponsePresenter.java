package si.ijs.mobis.presenters;


import si.ijs.mobis.service.BaseService;
import si.ijs.mobis.service.Error;
import si.ijs.mobis.service.Prijave;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import si.ijs.mobis.org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import si.ijs.mobis.service.CycService;
import si.ijs.mobis.service.MalfunctionClassification;

/**
 *
 * @author Zala
 */
@ManagedBean
@ViewScoped
public class ResponsePresenter implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(ResponsePresenter.class.getName());
    
    enum Malfunction {VZIG, MOTOR, PRENOS_MOCI, ELEKTRIKA, PODVOZJE, PNEVMATIKE, GORIVO, KLJUCAVNICA, OSTALO, BATTERY_WARNING, BATTERY_DEAD, 
            ENGINE_NOISES, ENGINE_STALLS, DOES_NOT_START, ROPOTA, UGASNIL, DIM, LUCKE, CUKA, OLJE, ALTERNATOR, TIP_MOTORJA, NESRECA, 
            RESEVANJE_VOZILA, SIPE, LUCI};
    
    private Integer id;
    private String grandparent;
    private String parent;
    private String malfunction;
    private String inputBrand;
    private String notes;
//    private List<String> parent_malfL;
//    private List<String> parent2_malfL;
//    private List<String> malfunctionL;
    private List<String> eventDescription;
        
    private CycList carBodyType;
    private List<String> carBrand;
    private CycList carModel;
    private ArrayList<String> modelL;
    private HashMap hmBrands;
//    private HashMap<Object, Object> mapTy;
//    private HashMap<Object, Object> mapMod;
//    private HashMap<Object, Object> mapBr;
    
    private CycAccess c;
//    private CycObject UniversalMt;
    private CycObject AMZSMt;
    private CycObject EnglishMt;    
    private Integer event;
//    private String amzsEvent;
    
//    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private CycService cycService;
    @Inject private BaseService baseService;
    @Inject private MalfunctionClassification malfunctionClassification;
    
    @PostConstruct
   public void postconstruct() throws UnknownHostException, IOException, JSONException{
            c = new CycAccess("aidemo", 3600);
//            UniversalMt = c.getConstantByName("UniversalVocabularyMt");
            EnglishMt = c.getConstantByName("EnglishMt");
            AMZSMt = c.getConstantByName("AMZSMt");
            
            id = baseService.getData().get(0).getId() + 1;
//            amzsEvent = cycService.getEvent();
            event = 0;
//            mapBr = cycService.exportFromCycCarBrandList(c);
            
            
            hmBrands = cycService.exportFromCycCarBrandList(c);
            carBrand = cycService.carBrandStrings(hmBrands);
//            parent2_malfL = baseService.getGPList();
            inputBrand = "";
            grandparent = "";
    }    
    
    public String printIssue(){
            String Issue = "Issue No. "+id;
            return Issue;
    }
       
    public void importIntoCycEvent() {
            
                        if (LOGGER.isLoggable(Level.FINE))
                            {LOGGER.fine("Importing into cyc event");}
                        
                        long startTime = System.currentTimeMillis();
                        

                            if("nesreca".equals(grandparent)) {
                                    cycService.accident(c, AMZSMt);
                            }

                            else if("resevanje vozila".equals(grandparent)) {
                                    event = 1;
                                    cycService.rescuing(c, AMZSMt);
                            }

                            else{
                                    cycService.malfunction(c, AMZSMt);
                            }
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Importing into Cyc EVENT took: {0}", duration);
                }
    
    public List<String> complete(String query) {  
        
            Set<String> keywords = malfunctionClassification.mapMalfunctions().keySet();
            List<String> suggestions = new ArrayList<String>(); 

            for (String tag : keywords) { 
        	
        	int n=0;
        	String newTag = tag;
        	
        	while (n > -1) {
                    n = newTag.indexOf(" ");    		
                    if (newTag.startsWith(query)) { 
                        suggestions.add(tag); 
                    } 
                    newTag = newTag.substring(newTag.indexOf(" ")+1);
        	}
            } 
            return suggestions; 
    }  
    
    public void handleDescription() {  
            if (LOGGER.isLoggable(Level.FINE))
                {LOGGER.fine("Importing into cyc description");}

            long startTime = System.currentTimeMillis();
            
            
            if (eventDescription != null) {
                parent = eventDescription.get(0);
                
                if (eventDescription.size() > 1){
                     malfunction = eventDescription.get(1);
                }
            }
            
            try {                   
                for (String keyword : eventDescription) {
                            
                Malfunction malfType = Malfunction.valueOf(CycService.toEnumCase(classifyKeyword(keyword)));
                
                    switch(malfType)
                    {
                        case ROPOTA:
                            cycService.noises(c, AMZSMt);
                            break;
                        case VZIG: 
                            cycService.ignition(c, AMZSMt, keyword);
                            break;
                        case UGASNIL:
                            cycService.stalls(c, AMZSMt);
                            break;
                        case LUCKE:
                            cycService.indicatorLights(c, AMZSMt);
                            break;
                        case CUKA:
                            cycService.tugging(c, AMZSMt);
                            break;
                        case OLJE:
                            cycService.oil(c, AMZSMt);
                            break;
                        case ALTERNATOR:
                            cycService.alternator(c, AMZSMt);
                            break;
                        case TIP_MOTORJA:
                            cycService.engineType(c, AMZSMt);
                            break;
                        case NESRECA:
                            cycService.accidentDetails(c, AMZSMt, keyword);
                            break;
                        case MOTOR:
                            cycService.engine(c, AMZSMt, keyword);
                            break;
                        case PRENOS_MOCI:
                            cycService.transmission(c, AMZSMt, keyword);
                            break;
                        case ELEKTRIKA: 
                            cycService.electricity(c, AMZSMt, keyword);
                            break;
                        case LUCI:
                            break;
                        case PODVOZJE:
                            cycService.chassis(c, AMZSMt, keyword);
                            break;
                        case PNEVMATIKE:
                            cycService.tires(c, AMZSMt, keyword);
                            break;
                        case GORIVO:
                            cycService.fuel(c, AMZSMt, keyword);
                            break;
                        case KLJUCAVNICA:
                            cycService.keys(c, AMZSMt, keyword);
                            break;
                        case RESEVANJE_VOZILA:
                            cycService.stuckDetails(c, AMZSMt, keyword);
                        default: break; 
//                        case BATTERY_DEAD:
//                            cycService.batteryDead(c, AMZSMt);
//                            break;
//                        case BATTERY_WARNING:
//                            cycService.batteryWarning(c, AMZSMt);
//                            break;
//                        case ENGINE_NOISES:
//                            cycService.engineNoises(c, AMZSMt);
//                            break;
                    }
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                LOGGER.log(Level.INFO, "Importing into Cyc DESCRIPTION of event took: {0}", duration);
                        
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, "You have to choose a value!", ex);
            }
    }
    
    public String classifyKeyword(String keyword) {
            String malfType = malfunctionClassification.mapMalfunctions().get(keyword);
            return malfType;
    }
    
    public void importIntoCycSender(){
                    try {
                        long startTime = System.currentTimeMillis();
                        String name = prijava.getIme();
                        
                        cycService.assertSender(c, AMZSMt, id, name);                            
                        
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Importing into Cyc SENDER took: {0}", duration);
                    
                    } catch (CycApiException ex) {
                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    
        
    public void importIntoCycMember() {
                                                
                    try {
                        long startTime = System.currentTimeMillis();
                        
                        if (!prijava.getClanska_st().isEmpty()){
                            
                            cycService.assertMember(c, AMZSMt, id, prijava.getClanska_st());
                        }
                        
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Importing into Cyc MEMBER took: {0}", duration);
                    
                    } catch (CycApiException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
    
    
    public void registrationVehicle() {             
                try {
                    long startTime = System.currentTimeMillis();
                    
                    cycService.assertRegistration(c, EnglishMt, id, prijava.getRegistrska());
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Importing into Cyc VEHICLE took: {0}", duration);

                } catch (CycApiException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }                        
            }
    
    
    public void importIntoCycType() {
                        try {                           
                            long startTime = System.currentTimeMillis();
                            
                            cycService.assertType(c, AMZSMt, id, prijava.getTip());
                                
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            LOGGER.log(Level.INFO, "Importing into Cyc TYPE took: {0}", duration);
                            
                        } catch (CycApiException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } 
            }
    
    
    public CycList carBodyTypeStrings() {
            HashMap<Object, Object> map = cycService.exportFromCycCarTypeList(c);
            carBodyType = new CycList();
            for ( Map.Entry<Object, Object> entry :  map.entrySet()){
                Object nameString = entry.getKey();
                carBodyType.add(nameString);
            }
            Collections.sort(carBodyType);
            return carBodyType;
    }
    
    
    public ArrayList<String> handleBrandChange() throws JSONException, UnknownHostException, CycApiException, IOException {  
            modelL = new ArrayList<String>();

            if(inputBrand != null) {
                HashMap<Object, Object> map = cycService.getModelByBrand(c, inputBrand, hmBrands);
                for (Map.Entry<Object, Object> entry :  map.entrySet()){
                    Object nameString = entry.getKey();
                    modelL.add(String.valueOf(nameString));
                }
            }  
            Collections.sort(modelL);
            return modelL;
    }
    
    
    public void importIntoCycModel() {
                    try {
                            long startTime = System.currentTimeMillis();
                            
                            cycService.assertModel(c, AMZSMt, id, prijava.getZnamka(), inputBrand);
                            
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            LOGGER.log(Level.INFO, "Importing into Cyc MODEL took: {0}", duration);
                    
                    } catch (CycApiException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } 
        }
           
    
    public void saveError(Error error) {
            baseService.save(error);
    }
    
        
    public String asserting() {
            prijava.setParent2_malf(getGrandparent());
            prijava.setParent_malf(getParent());
            prijava.setMalfunction(getMalfunction());
            baseService.saveData(prijava);
//            importIntoCycEvent();
//            importIntoCycTopic();
            return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
    }
                 
    public String assertingEdit() {
            prijava.setParent2_malf(getGrandparent());
            prijava.setParent_malf(getParent());
            prijava.setMalfunction(getMalfunction());
            baseService.updateEntry(prijava);
            return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
    }
    
    public String cure() {
                    String Hlid = "";
                    try
                    {
                        CycConstant Event = c.getConstantByName(cycService.getIssue());
                        Hlid = CycConstant.toCompactExternalId(Event, c);
                        
                    } catch(CycApiException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, "Doesn't work for StuckOrConfinedVehicleSituationFn yet. Else, event not found in Cyc.", e);
                    } catch(UnknownHostException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
                    } catch(IOException e) {
                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
                    }
                    return "http://aidemo:3603/cure/edit.jsp?conceptid=" +Hlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
        }
    
    public String googleMaps() {
                    return "https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&libraries=places";
    }
    
    public String googleMapsTest() {
                    return "https://google-developers.appspot.com/maps/documentation/javascript/examples/full/places-searchbox";
    }
    
//    public String cureSecond() {
//                    String Hlid = "";    
//                    try {
//                        String amzsEvent = cycService.getEvent();
//                        if (grandparent.equals("resevanje vozila")){
//                            event = 1;
//                        }
//
//                        switch(event){
//                            case 1:
//                                Hlid = CycConstant.toCompactExternalId(c.getHLCycTerm(amzsEvent), c);
//                                break;
//
//                            default:
//                                if (!"".equals(amzsEvent)) {
//                                    Hlid = CycConstant.toCompactExternalId(c.getConstantByName("InconvenientTrafficEvent" +id), c);
//                                }
//                                break;
//                        }
//                        
//                    } catch(CycApiException e) {
//                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, "Doesn't work for StuckOrConfinedVehicleSituationFn yet. Else, event not found in Cyc.", e);
//                    } catch(UnknownHostException e) {
//                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
//                    } catch(IOException e) {
//                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
//                    }
//                    
//                    return "http://aidemo:3603/cure/edit.jsp?conceptid=" +Hlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
//        }
    
       
    
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
//
//    public List<String> getParent2_malfL() {
//        return parent2_malfL;
//    }
//
//    public void setParent2_malfL(List<String> parent2_malfL) {
//        this.parent2_malfL = parent2_malfL;
//    }
//
//    public List<String> getParent_malfL() {
//        return parent_malfL;
//    }
//
//    public void setParent_malfL(List<String> parent_malfL) {
//        this.parent_malfL = parent_malfL;
//    }
//
//    public List<String> getMalfunctionL() {
//        return malfunctionL;
//    }
//
//    public void setMalfunctionL(List<String> malfunctionL) {
//        this.malfunctionL = malfunctionL;
//    }

    public CycList getCarBodyType() {
        return carBodyType;
    }

    public void setCarBodyType(CycList carBodyType) {
        this.carBodyType = carBodyType;
    }

    public List<String> getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(List<String> carBrand) {
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

    public List<String> getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(List<String> eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    

       
    
    
}
