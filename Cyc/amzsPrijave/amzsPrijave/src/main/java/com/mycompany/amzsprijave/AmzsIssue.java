/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amzsprijave;

import com.mycompany.amzsprijave.Prijave;
import com.mycompany.amzsprijave.PrijaveFacade;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.ELMt;
import org.opencyc.inference.DefaultInferenceParameters;
import org.opencyc.inference.DefaultInferenceWorkerSynch;
import org.opencyc.inference.InferenceResultSet;
import org.opencyc.inference.InferenceWorkerSynch;
import presenters.IndexPresenter;


@ManagedBean
@ViewScoped
public class AmzsIssue {
        
        private static final Logger log = Logger.getLogger(AmzsIssue.class.getName());
        
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
//        a je to kul ce tukaj definiram CycAccess??
        private CycAccess _c;
        private String assertionIssue;
        private String amzsEvent;
        private Integer event;
        private String amzsIssue;
        private String assertType;
        private String assertModel;
        private String _inputBrand;
        private HashMap<Object, Object> mapTy;
//        private HashMap<Object, Object> mapBr;
        private HashMap<Object, Object> mapMod;
        private CycList nameStringsTy;
        private CycList nameStringsBr;
        private CycList nameStringsMod;
        private ArrayList<String> modelL;
        private String assertionEvent;
        private String assertionVehicle;
        private String assertionSender;
        private String assertionTopic;
//        private String assertionObject;
//        private String assertionOccursAt;
//        private String assertionDate;
        
//	public static void main(String[] args) {
//		try {
//			//set Cyc Connection
//			//CycAccess c = new CycAccess("localhost", 3600);
//                        CycAccess c = new CycAccess("aidemo", 3600);
//
//			//Set inference parameters
//			DefaultInferenceParameters defaultP = new DefaultInferenceParameters(
//					c);
//			defaultP.put(new CycSymbol(":NEW-TERMS-ALLOWED?"), CycObjectFactory.nil);
//			defaultP.put(new CycSymbol(":INFERENCE-MODE"), new CycSymbol(":SHALLOW"));
//			
//			//importIntoCyc(c);
//			queryCyc(c, defaultP);
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CycApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} //*catch (JSONException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		//}
//
//	}
        
            @Inject private PrijaveFacade facade;
            @Inject private CycService cycService;
            
            @PostConstruct
            private void postconstruct() throws UnknownHostException, IOException{  
                if (log.isLoggable(Level.FINE))
                    log.fine("initiaizing AmzsIssue");
                
                podatki = facade.getPrijave();
                int n = podatki.size()-1; 
                                
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
                
                _c = new CycAccess("aidemo", 3600);
//                mapBr = new HashMap<Object,Object>();
                amzsIssue = cycService.getIssue();
                amzsEvent = cycService.getEvent();
                event = 0;
            }
            
//        public String getIssue(){
//                amzsIssue = "AMZSIssue"+id;
//                return amzsIssue;
//        }
            
	public String importIntoCycIssue() throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant AMZSReport = _c.getConstantByName("AMZSReport");
			CycConstant CycIssue = _c.makeCycConstant(amzsIssue);
			_c.assertIsa(CycIssue, AMZSReport);
//                        amzsIssue = CycIssue.toString();
                        
                        assertionIssue = "(isa " +CycIssue +" " +AMZSReport + ")";
                        return assertionIssue; 				
	}
        
        
        public String importIntoCycSender() throws JSONException, UnknownHostException, CycApiException, IOException {
                                                
                        CycList SenderOfInfo = _c.makeCycList("(#$senderOfInfo #$"+amzsIssue +" (#$AMZSUserFn \"" +id +"\"))");
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        _c.assertGaf(SenderOfInfo, Mt);                        
                        
                        CycObject English = _c.getConstantByName("EnglishMt");
                        CycList NameString = _c.makeCycList("(#$nameString  (#$AMZSUserFn \"" +id +"\") \"" +name +" " +surname +"\")");
                        _c.assertGaf(NameString, English);
			assertionSender = SenderOfInfo + ", " + NameString +".";
                        return assertionSender; 				
	}
        
         
        public String printName(){
                    if (name.isEmpty()){
                        name = " / ";
                    }
                    return name;
         }
         
        public String printSurname(){
                    if (surname.isEmpty()){
                        surname = " / ";
                    }
                    return surname;
         }
          
        public String importIntoCycMember() throws JSONException, UnknownHostException, CycApiException, IOException {
                    
                    CycAccess _c = new CycAccess("aidemo", 3600);
                    CycObject Mt = _c.getConstantByName("AMZSMt"); 
                    if ("Da".equals(member) && !member_no.isEmpty()){ 
                        CycList Member = _c.makeCycList("(#$groupMemberWithMembershipID (#$AMZSUserFn \"" +id +"\") #$AMZSKlub \""+member_no +"\")");
                        _c.assertGaf(Member, Mt);
                    }

                    else if (member == null){
                        member = " / ";
                    }

                    return member;
        }
             
        public String printMemberNo(){
                    if (member_no.isEmpty()){
                        member_no = " / ";
                    }
                    return member_no;
         }
        
        
//        enum Orientation {NA_KOLESIH, NA_STREHI, NA_BOKU};
//        enum Position{IZVEN_CESTE_DO_20M, IZVEN_CESTE_NAD_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI};
        public String importIntoCycEvent() throws JSONException, UnknownHostException, CycApiException, IOException {
                        if (log.isLoggable(Level.FINE))
                            log.fine("Importing itno cyc event");
                        
                        CycObject Mt = _c.getConstantByName("BaseKB");
                        amzsEvent = "";
                        
                        if("nesreca".equals(parent2_malf)){ 
                            assertionEvent = cycService.accident();
                            amzsEvent = cycService.getEvent();

                            if(malfunction != null){
                                assertionEvent = assertionEvent + cycService.orientation();
                            }
                        }
                                                
                        else if("resevanje vozila".equals(parent2_malf)){
                            CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+amzsIssue +" #$StuckOrConfinedVehicleSituation)");
                            _c.assertGaf(issueEventType, Mt);
                                
                            if("ostal v blatu".equals(parent_malf)){ 
                                CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInMudSituation");
                                CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckInMud"+id);
                                _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                amzsEvent = AMZSStuckSituation.toString();
                            }
                            else if("ostal v snegu".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInSnowSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckInSnow"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            else if("ostal na kolicku".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAPoleSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnAPole"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            else if("ostal na previsu".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnACliffSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnACliff"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            else if("ostal na zidu/skarpi".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAWallSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnAWall"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            else if("ostalo".equals(parent_malf) || parent_malf == null){ 
//                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleSituation");
//                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckSituation"+id);
                                    CycList AMZSStuckSituation = _c.makeCycList("(#$StuckOrConfinedVehicleSituationFn #$" + amzsIssue +")");
//                                    _c.assertGaf(AMZSStuckSituation, StuckOrConfinedVehicleSituation, Mt);
                                    amzsEvent = "Constant: (StuckOrConfinedVehicleSituationFn " + amzsIssue +")";
                                    event = 1;
                            }
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
                            amzsEvent = AMZSVehicleMalfunction.toString();
                        }
                        
                        return amzsEvent;			
	}
       
//        public String printEvent() throws JSONException, UnknownHostException, CycApiException, IOException{
//            assertionEvent = importIntoCycEvent();
//            return assertionEvent;
//        }
        
        public String printMalfunction(){
                    String malf = malfunction;
                    if (malfunction == null){
                        malf = " / ";
                    }
                    return malf;
         }
        
        
        public String printGrandparent(){
                    String par2 = parent2_malf;
                    if (parent2_malf == null){
                        par2 = " / ";
                    }
                    return par2;
         }
        
        
        public String printParent(){
                    String par = parent_malf;
                    if (parent_malf == null){
                        par = " / ";
                    }
                    return par;
         }
        
          
        public String importIntoCycVehicle(String _inputBrand) throws JSONException, UnknownHostException, CycApiException, IOException {
                    amzsEvent = importIntoCycEvent();
                    if (log.isLoggable(Level.FINE))
                        log.fine("asserting vehicle");
                
//                    _inputBrand = indexPres.getInputBrand();
                    CycObject Mt = _c.getConstantByName("BaseKB");
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    mapTy = cycService.exportFromCycCarTypeList(_c);
                    if (type != null){
                        String typeConst = String.valueOf(mapTy.get(type));
                        CycList Type = _c.makeCycList("(#$roadVehicleBodyStyle (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+typeConst +")");
                        _c.assertGaf(Type, Mt);
                        assertType = String.valueOf(Type);
                    }

//                    prestavi if() vrstico nizje oz resi problem s seznami!
                    mapMod = cycService.getModelByBrandAllNameStrings(_c, _inputBrand);
                    String modelConst = String.valueOf(mapMod.get(model));
                    if (!"null".equals(modelConst)){
                        CycList Model = _c.makeCycList("(#$isa (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+modelConst +")");
                        _c.assertGaf(Model, Mt);
                        assertModel = String.valueOf(Model);
                    }
                    
                    CycList Registrska = _c.makeCycList("(#$nameString  (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"" +registration +"\")");
                    _c.assertGaf(Registrska, English);
                    
                    if(!"".equals(amzsEvent) ){
                        CycList ObjectActedOn = new CycList();
                        switch (event){
                            case 1: 
                                ObjectActedOn = _c.makeCycList("(#$objectActedOn (#$StuckOrConfinedVehicleSituationFn #$" + amzsIssue +") (#$VehicleInvolvedInAMZSReportFn #$" +amzsIssue +"))");
                                _c.assertGaf(ObjectActedOn, Mt);
                                break;
                            default: 
                                ObjectActedOn = _c.makeCycList("(#$objectActedOn  #$"+amzsEvent +" (#$VehicleInvolvedInAMZSReportFn #$" +amzsIssue +"))");
                                _c.assertGaf(ObjectActedOn, Mt);
                                break;   
                        }
                        
                        
//                        Ne dela v AMZSMt!
                         if("zakuhal".equals(malfunction)){
                            CycList Overheated = _c.makeCycList("(#$stateOfDevice (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$VehicleDevice-Overheated)");
                            _c.assertGaf(Overheated, Mt);
                        }
                         
                        assertionVehicle = ObjectActedOn +", " +Registrska +", "; 
                    }
                    else {assertionVehicle = "";}
//                        model in overheated v assertionVehicle!
                    return assertionVehicle + assertType + ", " +assertModel; 	
                        
	}
        
//        public String printVehicle() throws JSONException, CycApiException, UnknownHostException, IOException{
//                    return importIntoCycVehicle(_inputBrand);
//        }
        
//        a bi tele ubistvu lahko bli static???
        public String printRegistration(){
                    String reg = registration;
                    if (registration.isEmpty()){
                        reg = " / ";
                    }
                    return reg;
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
                                CycConstant Issue = _c.getConstantByName("AMZSIssue"+id);
                                CycConstant Predicate = _c.getConstantByName("topicOfInfoTransfer");
                                _c.assertGaf(Mt, Predicate, Issue, Event);
                                assertionTopic = "("+Predicate +" " +Issue +" " +Event +")";
                                break;
                        }
                    }
                    return assertionTopic; 				
	}

        
        public String importIntoCycOccursAt() throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant Event = _c.getConstantByName(amzsEvent);
                        CycConstant LocationFn = _c.getConstantByName("StreetNamedFn");
                        String Street = "\"Trzaska cesta\"";
                        String City = "CityOfLjubljanaSlovenia";
                        
                        String Place = "(#$" +LocationFn +" " +Street +" #$" +City +")";
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        String StringEvent = "(#$eventOccursAt #$" +Event +" " +Place +")";
                        CycList EventL = _c.makeCycList(StringEvent);
                        _c.assertGaf(EventL, Mt);
//			assertionOccursAt = EventL;
                        return StringEvent; 				
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
                        
//                        assertionDate = "Date: " +Year + "-" + Month +"-" +Day;
                                              
                        return StringDate;
                        
        }
        
        public String importIntoCycHlid(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                        CycConstant Event = _c.getConstantByName(amzsIssue);
                        String Hlid = CycConstant.toCompactExternalId(Event, _c);
                        return Hlid;
        }
        
        public String getMonth(int month) {
                        return new DateFormatSymbols(Locale.ENGLISH).getMonths()[month-1];
        }
        
        
        public String cure() throws JSONException, UnknownHostException, CycApiException, IOException {
                    String Hlid = importIntoCycHlid(_c);
                    return "http://aidemo:3603/cure/edit.jsp?conceptid=" +Hlid +"&cycHost=aidemo&cycPort=3600&userName=AMZSAdministrator";
        }
        
		private static void queryCyc(CycAccess ca,	DefaultInferenceParameters _inferenceParameters)
				throws CycApiException, IOException {

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
	
                //	/*static String toCamelCase(String s){
                //		   String[] parts = s.split(" ");
                //		   String camelCaseString = "";
                //		   for (String part : parts){
                //		      camelCaseString = camelCaseString + toProperCase(part);
                //		   }
                //		   return camelCaseString;
                //		}*/
	
                //	/*static String toProperCase(String s) {
                //	    return s.substring(0, 1).toUpperCase() +
                //	               s.substring(1).toLowerCase();
                //	}*/
        
                static String toEnumCase(String s){
                    String[] parts = s.split(" ");
                    String enumCaseString = "";
                    for (String part : parts){
                        enumCaseString = enumCaseString + part.toUpperCase()+"_" ;
                    }
                    int n = enumCaseString.length();
                    enumCaseString = enumCaseString.substring(0,n-1);
                    return enumCaseString;
                }


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

//    public HashMap<Object, Object> getMapBr() {
//        return mapBr;
//    }
//
//    public void setMapBr(HashMap<Object, Object> mapBr) {
//        this.mapBr = mapBr;
//    }

    public HashMap<Object, Object> getMapMod() {
        return mapMod;
    }

    public void setMapMod(HashMap<Object, Object> mapMod) {
        this.mapMod = mapMod;
    }
    
    
}
