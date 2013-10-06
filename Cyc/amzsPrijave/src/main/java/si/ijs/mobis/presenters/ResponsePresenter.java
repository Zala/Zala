package si.ijs.mobis.presenters;


import si.ijs.mobis.service.BaseService;
import si.ijs.mobis.service.Error;
import si.ijs.mobis.service.Prijave;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

/**
 *
 * @author Zala
 */
@ManagedBean
@ViewScoped
public class ResponsePresenter implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(ResponsePresenter.class.getName());

    private Integer id;
    private String grandparent;
    private String parent;
    private String malfunction;
    private String inputBrand;
    private List<String> parent_malfL;
    private List<String> parent2_malfL;
    private List<String> malfunctionL;
    private List<String> eventDescription;
        
    private CycList carBodyType;
    private List<String> carBrand;
    private CycList carModel;
    private ArrayList<String> modelL;
    private HashMap hmBrands;
    private HashMap<Object, Object> mapTy;
    private HashMap<Object, Object> mapMod;
    private HashMap<Object, Object> mapBr;
    
    private CycAccess c;
    private CycObject UniversalMt;
    private CycObject AMZSMt;
    private CycObject EnglishMt;    
    private Integer event;
    private String amzsEvent;
    
//    @Inject private PrijaveFacade facade;
    private Prijave prijava = new Prijave();
    @Inject private CycService cycService;
    @Inject private BaseService baseService;
    
    @PostConstruct
   public void postconstruct() throws UnknownHostException, IOException, JSONException{
            c = new CycAccess("aidemo", 3600);
            UniversalMt = c.getConstantByName("UniversalVocabularyMt");
            EnglishMt = c.getConstantByName("EnglishMt");
            AMZSMt = c.getConstantByName("AMZSMt");
            
            id = baseService.getData().get(0).getId() + 1;
//            amzsEvent = cycService.getEvent();
            event = 0;
            mapBr = cycService.exportFromCycCarBrandList(c);
            
            
            hmBrands = cycService.exportFromCycCarBrandList(c);
            carBrand = cycService.carBrandStrings(hmBrands);
            parent2_malfL = baseService.getGPList();
            inputBrand = "";
    }    
    

    public void importIntoCycSender(){
                    try {
                        long startTime = System.currentTimeMillis();
                        String name = prijava.getIme();
//                        CycList SenderOfInfo = _c.makeCycList("(#$senderOfInfo #$"+amzsIssue +" (#$AMZSUserFn \"" +id +"\"))");
                        
                        CycList SenderOfInfo = c.makeCycList("(#$amzsServiceClient #$AMZSIssue"+id +" (#$AMZSUserFn \"" +id +"\"))");
                        c.assertGaf(SenderOfInfo, AMZSMt);
                        
                        if ("".equals(prijava.getIme())) {
                             name = "AMZS user";
                        }
                            
                        CycList NameString2 = c.makeCycList("(#$nameString (#$AMZSUserFn \"" +id +"\") \"" +name +"\")");
                        c.assertGaf(NameString2, EnglishMt);
                            
                        
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Importing into Cyc SENDER took: {0}", duration);
                    
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    
        
    public void importIntoCycMember() {
                                                
                    try {
                        long startTime = System.currentTimeMillis();
                        
                        if (!prijava.getClanska_st().isEmpty()){
                            
//                            cycService.assertMember(prijava.getClan(),prijava.getClanska_st());
                            CycList Member = c.makeCycList("(#$memberWithIDInIssue #$AMZSIssue"+id + " (#$AMZSUserFn \"" +id +"\") \""+prijava.getClanska_st() +"\")");
                            c.assertGaf(Member, AMZSMt);
                        }
                        
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Importing into Cyc MEMBER took: {0}", duration);
                    
                    } catch (UnknownHostException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
    
    
    public void registrationVehicle() {             
                try {
                    long startTime = System.currentTimeMillis();
                    
                    String registration = prijava.getRegistrska();
                    CycList Reg = c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"Vehicle with registration number " +registration +"\")");

//                            sploh ni teba if-else, ker itak ne bo assertu, ce ne vpises
//                            Reg = c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"Vehicle involved\")");

                    c.assertGaf(Reg, EnglishMt);

//                    switch (event){
//                                case 1:
//                                    CycList ObjectActedOn1 = c.makeCycList("(#$objectActedOn " + amzsEvent +" (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +"))");
//                                    c.assertGaf(ObjectActedOn1, AMZSMt);
//                                    break;
//                                default:
//                                    CycList ObjectActedOn2 = c.makeCycList("(#$objectActedOn #$"+amzsEvent +" (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +"))");
//                                    c.assertGaf(ObjectActedOn2, AMZSMt);
//                                    break;
//                    }
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Importing into Cyc VEHICLE took: {0}", duration);

                } catch (UnknownHostException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }                        
            }
    
    
    public void importIntoCycType() {
                        try {                           
                            long startTime = System.currentTimeMillis();
                            
                            String type = prijava.getTip();
                            mapTy = cycService.exportFromCycCarTypeList(c);
                            String typeConst = String.valueOf(mapTy.get(type));
                            CycList Type = c.makeCycList("(#$roadVehicleBodyStyle (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+typeConst +")");
                            c.assertGaf(Type, AMZSMt);
                                
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            LOGGER.log(Level.INFO, "Importing into Cyc TYPE took: {0}", duration);
                            
                        } catch (JSONException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } catch (UnknownHostException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } catch (CycApiException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
            }
    
    
    public CycList carBodyTypeStrings() throws UnknownHostException, IOException, JSONException{
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
                            
                            mapMod = cycService.getModelByBrand(c, inputBrand, mapBr);
                            String modelConst = String.valueOf(mapMod.get(prijava.getZnamka()));

                            CycList Brand = c.makeCycList("(#$isa (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+ mapBr.get(inputBrand) +")");
                            c.assertGaf(Brand, AMZSMt);
                            
                            CycList Model = c.makeCycList("(#$roadVehicleModel (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+modelConst +")");
                            c.assertGaf(Model, AMZSMt);
                            
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            LOGGER.log(Level.INFO, "Importing into Cyc MODEL took: {0}", duration);
                    
                    } catch (JSONException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (UnknownHostException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
        }
    
    
    public void importIntoCycEvent() {
            
                        if (LOGGER.isLoggable(Level.FINE))
                            {LOGGER.fine("Importing into cyc event");}
                        
                        long startTime = System.currentTimeMillis();
                        
                            amzsEvent = cycService.getEvent();

                            if("nesreca".equals(grandparent)) {
                                
                                    cycService.accident(c, AMZSMt);

                                    if(malfunction != null) {
                                        cycService.orientation(c, AMZSMt);
                                    }                          
                            }

                            else if("resevanje vozila".equals(grandparent)) {
                                    event = 1;
                                    cycService.rescuing(c, AMZSMt);
                            }

                            else if("ostalo".equals(grandparent)){
                                    cycService.unknown(c,UniversalMt);
                            }

                            else{
                                    cycService.malfunction(c, AMZSMt);
                            }
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Importing into Cyc EVENT took: {0}", duration);
                }
        
        
    public void importIntoCycTopic() {
                    long startTime = System.currentTimeMillis();
                    try {
                        CycList Topic = new CycList();
                        amzsEvent = cycService.getEvent();
                        switch(event){
                            case 1:
                                Topic = c.makeCycList("(#$topicOfInfoTransfer #$" +cycService.getIssue() + " (#$StuckOrConfinedVehicleSituationFn #$"+cycService.getIssue() +"))");
                                c.assertGaf(Topic, AMZSMt);
                                break;
                            
                            default:
                                long stAssertTopic = System.currentTimeMillis();
                                
                                Topic = c.makeCycList("(#$topicOfInfoTransfer #$" + cycService.getIssue() + " #$" + amzsEvent +")");
                                c.assertGaf(Topic, AMZSMt);
                                
                                long etAssertTopic = System.currentTimeMillis();
                                long durationTopic = etAssertTopic - stAssertTopic;
                                LOGGER.log(Level.INFO, "Asserting TOPIC took: {0}", durationTopic);
                                break;
                        }
                        
                    } catch (UnknownHostException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Importing into Cyc TOPIC took: {0}", duration);
                    
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
            else{}
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
            importIntoCycEvent();
            importIntoCycTopic();
            return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
    }
                 
    public String assertingEdit() {
            prijava.setParent2_malf(getGrandparent());
            prijava.setParent_malf(getParent());
            prijava.setMalfunction(getMalfunction());
            baseService.updateEntry(prijava);
            return "asserting.xhtml?ib=" + inputBrand + "&faces-redirect=true";
    }
    
    
//    public List<String> complete(String query) {  
//            String[] names = {"Bob", "George", "Henry", "Declan", "Peter", "Steven"};
//            List<String> keywords = new ArrayList<String>();
//            keywords.addAll(Arrays.asList(names));
//            List<String> suggestions = new ArrayList<String>(); 
//
//            for (String name : keywords) { 
//               if (name.toLowerCase().trim().startsWith(query.toLowerCase().trim())) 
//               { 
//                  suggestions.add(name.toUpperCase()); 
//               } 
//            } 
//            return suggestions; 
//    }  
    
    
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

       
    
    
}
