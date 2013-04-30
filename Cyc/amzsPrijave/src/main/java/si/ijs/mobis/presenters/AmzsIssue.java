
package si.ijs.mobis.presenters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.el.ELException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import si.ijs.mobis.org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.ELMt;
import org.opencyc.inference.DefaultInferenceParameters;
import org.opencyc.inference.DefaultInferenceWorkerSynch;
import org.opencyc.inference.InferenceResultSet;
import org.opencyc.inference.InferenceWorkerSynch;
import si.ijs.mobis.service.BaseService;
import si.ijs.mobis.service.CycService;
import si.ijs.mobis.service.Prijave;


@ManagedBean
@ViewScoped
public class AmzsIssue {
        
        private static final Logger LOGGER = Logger.getLogger(AmzsIssue.class.getName());
        @Inject private BaseService baseService;
        @Inject private CycService cycService;
        
        private Integer id;
        private String name;
        private String surname;
        private String model;
        private String type;
        private String registration;
        private String member;
        private String member_no;
        private Date date;
        private String malfunction;
        private String parent_malf;
        private String parent2_malf;
        private List<Prijave> podatki;
        private CycAccess _c;
        private String assertionIssue;
        private String amzsEvent;
        private Integer event;
        private String amzsIssue;
        private String assertType;
        private String inputBrand;
        private String assertModel;
        private HashMap<Object, Object> mapTy;
        private HashMap<Object, Object> mapBr;
        private HashMap<Object, Object> mapMod;
        private ArrayList<String> modelL;
        private String assertionEvent;
        private String assertionVehicle;
        private String assertionSender;
        private String assertionTopic;        
 
        
            
            @PostConstruct
            private void postconstruct() throws UnknownHostException, IOException, JSONException {
                if (LOGGER.isLoggable(Level.FINE))
                    {LOGGER.fine("initiaizing AmzsIssue");}
                
                podatki = baseService.getData();
                                
                id = podatki.get(0).getId();
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
                
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                Map<String, String> parameterMap = (Map<String, String>) context.getRequestParameterMap();
                if (!parameterMap.containsKey("ib")) {
                    if (LOGGER.isLoggable(Level.WARNING))
                        {LOGGER.warning("Parameter ib missing!");}
                    throw new IllegalArgumentException("Parameter ib missing!");
                }

                inputBrand = parameterMap.get("ib");
                _c = new CycAccess("aidemo", 3600);
                amzsIssue = cycService.getIssue();
                event = 0;
                mapBr = cycService.exportFromCycCarBrandList(_c);
                               
            }
           
        
        public String printName() {
                    String n = name;
                    if (name.isEmpty()) {
                        n = " / ";
                    }
                    return n;
         }
         
        public String printSurname() {
                    String s = surname;
                    if (surname.isEmpty()){
                        s = " / ";
                    }
                    return s;
         }    
           
        public String printMember() {
                  String m = member;
                  if (member == null){
                      m = " / ";
                  }
                  return m;
        }
          
        public String printMemberNo() {
                    String m = member_no;
                    if (member_no.isEmpty()){
                        m = " / ";
                    }
                    return m;
         }
        
        public String printMalfunction() {
                    String malf = malfunction;
                    if (malfunction == null){
                        malf = " / ";
                    }
                    return malf;
         }
        
        public String printGrandparent() {
                    String par2 = parent2_malf;
                    if (parent2_malf == null){
                        par2 = " / ";
                    }
                    return par2;
         }
        
        public String printParent() {
                    String par = parent_malf;
                    if (parent_malf == null){
                        par = " / ";
                    }
                    return par;
         }
        
        public String printRegistration() {
                    String reg = registration;
                    if (registration.isEmpty()) {
                        reg = " / ";
                    }
                    return reg;
         }
        
        public String printModel() {
                    String m = model;
                    if (model == null){
                        m = " / ";
                    }
                    return m;
        }
        
        public String printType() {
                    String t = type;
                    if (type == null){
                        t = " / ";
                    }
                    return t;
        }
        
        public String importIntoCycIssue() throws JSONException, UnknownHostException, CycApiException, IOException {
                        CycConstant AMZSReport = _c.getConstantByName("AMZSReport");
                        CycConstant CycIssue = _c.makeCycConstant(amzsIssue);
                        _c.assertIsa(CycIssue, AMZSReport);
                        
                        assertionIssue = "(isa " +CycIssue +" " +AMZSReport + ")";
                        return assertionIssue;
        }
        
        public String importIntoCycSender() throws JSONException, UnknownHostException, CycApiException, IOException {
                                                
                        CycList SenderOfInfo = _c.makeCycList("(#$senderOfInfo #$"+amzsIssue +" (#$AMZSUserFn \"" +id +"\"))");
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        _c.assertGaf(SenderOfInfo, Mt);
                        
                        CycObject English = _c.getConstantByName("EnglishMt");
                        CycList NameString = _c.makeCycList("(#$nameString (#$AMZSUserFn \"" +id +"\") \"" +name +" " +surname +"\")");
                        _c.assertGaf(NameString, English);
                        assertionSender = SenderOfInfo + ", " + NameString +".";
                        return assertionSender;
}
        
        public String importIntoCycMember() throws JSONException, UnknownHostException, CycApiException, IOException {
                    
                    _c = new CycAccess("aidemo", 3600);
                    CycObject Mt = _c.getConstantByName("AMZSMt");
                    
                    CycList Member = new CycList();
                    if ("Da".equals(member) && !member_no.isEmpty()){
                        Member = _c.makeCycList("(#$groupMemberWithMembershipID (#$AMZSUserFn \"" +id +"\") #$AMZSKlub \""+member_no +"\")");
                        _c.assertGaf(Member, Mt);
                    }

//                    else if (member == null){
//                        member = " / ";
//                    }

                    return String.valueOf(Member);
        }
        
        public String importIntoCycEvent() {
                        if (LOGGER.isLoggable(Level.FINE))
                            {LOGGER.fine("Importing itno cyc event");}
                        
                        try{
                            CycObject Mt = _c.getConstantByName("BaseKB");
                            amzsEvent = "";

                            if("nesreca".equals(parent2_malf)){
                                try {assertionEvent = cycService.accident(_c);}
                                catch(EJBException n) {
                                    System.out.println("You have to choose location of the object");
                                }
                                amzsEvent = cycService.getEvent();

                                if(malfunction != null){
                                    assertionEvent = assertionEvent + cycService.orientation(_c);
                                }
                            }

                            else if("resevanje vozila".equals(parent2_malf)){
                                event = 1;
                                CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+amzsIssue +" #$StuckOrConfinedVehicleSituation)");
                                _c.assertGaf(issueEventType, Mt);

                                assertionEvent = cycService.rescuing(_c);
                                amzsEvent = cycService.getEvent();
                            }

                            else if(parent2_malf == null){
                                amzsEvent = "";
                            }

                            else{
                                CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+amzsIssue +" #$TransportationDevice-VehicleMalfunction)");
                                _c.assertGaf(issueEventType, Mt);

                                CycConstant RoadVehicleMalfunction = _c.getConstantByName("RoadVehicleMalfunction");
                                CycConstant AMZSVehicleMalfunction = _c.makeCycConstant("AMZSVehicleMalfunction"+id);
                                _c.assertIsa(AMZSVehicleMalfunction, RoadVehicleMalfunction);
                                amzsEvent = cycService.getEvent();
                            }
                        } catch(IOException e){
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);                        
                        }
                        return amzsEvent;	
}
       
        public String importIntoCycType() throws JSONException, UnknownHostException, CycApiException, IOException {
                    amzsEvent = cycService.getEvent();
                    CycObject Mt = _c.getConstantByName("AMZSMt");
                    assertType = "";
                    
                    mapTy = cycService.exportFromCycCarTypeList(_c);
                    if (type != null){
                        String typeConst = String.valueOf(mapTy.get(type));
                        CycList Type = _c.makeCycList("(#$roadVehicleBodyStyle (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+typeConst +")");
                        _c.assertGaf(Type, Mt);
                        assertType = String.valueOf(Type);
                    }
                    return assertType;
        }
        
        public String importIntoCycModel() {
                    try {CycObject Mt = _c.getConstantByName("AMZSMt");
                        mapMod = cycService.getModelByBrand(_c, inputBrand, mapBr);
                        String modelConst = String.valueOf(mapMod.get(model));

                        if (!"null".equals(modelConst)){
                            CycList Model = _c.makeCycList("(#$isa (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+modelConst +")");
                            _c.assertGaf(Model, Mt);
                            assertModel = String.valueOf(Model);
                        }
                    }
                    catch (UnknownHostException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    } catch (CycApiException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                    return assertModel;
        }
        
        public String importIntoCycVehicle() throws JSONException, UnknownHostException, CycApiException, IOException {
                    amzsEvent = importIntoCycEvent();
                    if (LOGGER.isLoggable(Level.FINE))
                        {LOGGER.fine("asserting vehicle");}
                
                    CycObject Mt = _c.getConstantByName("AMZSMt");
                    CycObject English = _c.getConstantByName("EnglishMt");
                     
                    CycList Registrska = new CycList();
                    if (!registration.equals("")) {
                        Registrska = _c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"" +registration +"\")");
                    }
                    else {
                        Registrska = _c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"Vehicle involved\")");
                    }
                    _c.assertGaf(Registrska, English);
                    
                    
                    if(!"".equals(amzsEvent) ){
                        CycList ObjectActedOn = new CycList();
                        switch (event){
                            case 1:
                                ObjectActedOn = _c.makeCycList("(#$objectActedOn " + amzsEvent +" (#$VehicleInvolvedInAMZSReportFn #$" +amzsIssue +"))");
                                _c.assertGaf(ObjectActedOn, Mt);
                                break;
                            default:
                                ObjectActedOn = _c.makeCycList("(#$objectActedOn #$"+amzsEvent +" (#$VehicleInvolvedInAMZSReportFn #$" +amzsIssue +"))");
                                _c.assertGaf(ObjectActedOn, Mt);
                                break;
                        }
                         // Ne dela v AMZSMt!
                         if("zakuhal".equals(malfunction)){
                            CycList Overheated = _c.makeCycList("(#$stateOfDevice (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$VehicleDevice-Overheated)");
                            _c.assertGaf(Overheated, Mt);
                        }
                        assertionVehicle = ObjectActedOn +", " +Registrska;
                    }
                    
                    else {assertionVehicle = "";}
                    
                    return assertionVehicle;
                        
}
        
        public String importIntoCycTopic() throws JSONException, UnknownHostException, CycApiException, IOException {
                    amzsEvent = cycService.getEvent();
                    CycObject Mt = _c.getConstantByName("BaseKB");
                    if(!"".equals(amzsEvent)){
                        switch(event){
                            case 1:
                                CycList Topic = _c.makeCycList("(#$topicOfInfoTransfer #$" +amzsIssue + " (#$StuckOrConfinedVehicleSituationFn #$"+amzsIssue +"))");
                                _c.assertGaf(Topic, Mt);
                                assertionTopic = String.valueOf(Topic);
                                break;
                            default:
                                CycConstant Event = _c.getConstantByName(amzsEvent);
                                CycConstant Issue = _c.getConstantByName(amzsIssue);
                                CycConstant Predicate = _c.getConstantByName("topicOfInfoTransfer");
                                _c.assertGaf(Mt, Predicate, Issue, Event);
                                assertionTopic = "("+Predicate +" " +Issue +" " +Event +")";
                                break;
                        }
                    }
                    return assertionTopic;
        }

        public String importIntoCycDate() throws JSONException, UnknownHostException, CycApiException, IOException {
                        CycConstant Event = _c.getConstantByName(amzsEvent);
                        
                        long timestamp = date.getTime();
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(timestamp);
                        String Year = String.valueOf(cal.get(Calendar.YEAR));
                        String Month = getMonth(cal.get(Calendar.MONTH)+1);
                        String Day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        String StringDate = "(#$dateOfEvent #$" +Event +" (#$DayFn " +Day +" (#$MonthFn #$" +Month +" (#$YearFn " +Year +"))))";
                        CycList EventL = _c.makeCycList(StringDate);
                        _c.assertGaf(EventL, Mt);
                        
                        // assertionDate = "Date: " +Year + "-" + Month +"-" +Day;
                                              
                        return StringDate;
                        
        }
        
        public String importIntoCycHlidIssue(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                        CycConstant Event = _c.getConstantByName(amzsIssue);
                        String Hlid = CycConstant.toCompactExternalId(Event, _c);
                        return Hlid;
        }
        
        public String importIntoCycHlidEvent(CycAccess _c) throws JSONException {
                        String Hlid = "";            
                        try{
                            switch(event){
                                case 1:
                                    Hlid = CycConstant.toCompactExternalId(_c.getHLCycTerm(amzsEvent), _c);
                                    break;
                                    
                                default:
                                    if (!"".equals(amzsEvent)) {
                                        Hlid = CycConstant.toCompactExternalId(_c.getConstantByName(amzsEvent), _c);
                                    }
                                    break;
                            }
                        }
                        catch(CycApiException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, "Doesn't work for StuckOrConfinedVehicleSituationFn yet. Else, event not found in Cyc.", e);
                        }
                        catch(ELException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, "Doesn't work for StuckOrConfinedVehicleSituationFn yet.", e);
                        }
                        catch(UnknownHostException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
                        }
                        catch(IOException e) {
                            Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
                        }
                        return Hlid;
        }
        
        public String getMonth(int month) {
                        return new DateFormatSymbols(Locale.ENGLISH).getMonths()[month-1];
        }
        
        
        public String cure() throws JSONException, UnknownHostException, CycApiException, IOException {
                    String Hlid = importIntoCycHlidIssue(_c);
                    return "http://aidemo:3603/cure/edit.jsp?conceptid=" +Hlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
        }
        
        public String cureSecond() throws JSONException, UnknownHostException {
            String Hlid = new String();
            try {
                Hlid = importIntoCycHlidEvent(_c);
            } catch(CycApiException e){
                Logger.getLogger(AmzsIssue.class.getName()).log(Level.SEVERE, null, e);
            }
            
            return "http://aidemo:3603/cure/edit.jsp?conceptid=" +Hlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
        }
        
        
        public String edit() throws UnknownHostException, IOException {
            CycConstant issue = _c.getConstantByName(amzsIssue);
            _c.kill(issue);
            baseService.updateEntry(baseService.getLastEntry());
            return "response.xhtml?faces-redirect=true";
        }
        
        public String newApp() {
            return "response.xhtml?faces-redirect=true";
        }
        
        private static void queryCyc(CycAccess ca, DefaultInferenceParameters _inferenceParameters) throws CycApiException, IOException {

            ELMt CCMt = ca.makeELMt(ca.makeCycConstant("#$InferencePSC"));

            String OurQueryString = "(#$and (#$durationOfDelay ?EVENT ?TIME) (#$lengthOfQueue ?EVENT ?LENGTH) (#$isa ?EVENT #$TrafficIrregularEvent) (#$nearestIsa ?EVENT ?TYPE) (#$greaterThan ?LENGTH (#$Meter 200)))";

            InferenceWorkerSynch cycWorker = new DefaultInferenceWorkerSynch(
            OurQueryString, CCMt, _inferenceParameters, ca,
            500000);

            InferenceResultSet rs = cycWorker.executeQuery();
            while (rs.next()) {
            int index = rs.findColumn("?EVENT");
            System.out.println(rs.getCycObject(index));

            }
            rs.close();
        }

                // /*static String toCamelCase(String s){
                // String[] parts = s.split(" ");
                // String camelCaseString = "";
                // for (String part : parts){
                // camelCaseString = camelCaseString + toProperCase(part);
                // }
                // return camelCaseString;
                // }*/

                // /*static String toProperCase(String s) {
                // return s.substring(0, 1).toUpperCase() +
                // s.substring(1).toLowerCase();
                // }*/
        
//                static String toEnumCase(String s){
//                    String[] parts = s.split(" ");
//                    String enumCaseString = "";
//                    for (String part : parts){
//                        enumCaseString = enumCaseString + part.toUpperCase()+"_" ;
//                    }
//                    int n = enumCaseString.length();
//                    enumCaseString = enumCaseString.substring(0,n-1);
//                    return enumCaseString;
//                }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(String malfunction) {
        this.malfunction = malfunction;
    }

    public String getParent_malf() {
        return parent_malf;
    }

    public void setParent_malf(String parent_malf) {
        this.parent_malf = parent_malf;
    }

    public String getParent2_malf() {
        return parent2_malf;
    }

    public void setParent2_malf(String parent2_malf) {
        this.parent2_malf = parent2_malf;
    }
   
    public List<Prijave> getPodatki() {
        return podatki;
    }
    
    public void setPodatki(List<Prijave> podatki) {
        this.podatki = podatki;
    }

    public List<String> getModelL() {
        return modelL;
    }

    public void setModelL(ArrayList<String> modelL) {
        this.modelL = modelL;
    }

    public HashMap<Object, Object> getMapTy() {
        return mapTy;
    }

    public void setMapTy(HashMap<Object, Object> mapTy) {
        this.mapTy = mapTy;
    }

    public String getInputBrand() {
        return inputBrand;
    }

    public void setInputBrand(String inputBrand) {
        this.inputBrand = inputBrand;
    }

    public HashMap<Object, Object> getMapMod() {
        return mapMod;
    }

    public void setMapMod(HashMap<Object, Object> mapMod) {
        this.mapMod = mapMod;
    }
    
    
}
