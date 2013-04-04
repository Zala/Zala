/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amzsprijave;

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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.ELMt;
import org.opencyc.inference.DefaultInferenceParameters;
import org.opencyc.inference.DefaultInferenceWorkerSynch;
import org.opencyc.inference.InferenceResultSet;
import org.opencyc.inference.InferenceWorkerSynch;
import org.opencyc.cycobject.CycObject;

@Named
@RequestScoped
public class amzsIssue {
        
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
        
	public static void main(String[] args) {
		try {
			//set Cyc Connection
			//CycAccess c = new CycAccess("localhost", 3600);
                        CycAccess c = new CycAccess("aidemo", 3600);

			//Set inference parameters
			DefaultInferenceParameters defaultP = new DefaultInferenceParameters(
					c);
			defaultP.put(new CycSymbol(":NEW-TERMS-ALLOWED?"), CycObjectFactory.nil);
			defaultP.put(new CycSymbol(":INFERENCE-MODE"), new CycSymbol(":SHALLOW"));
			
			//importIntoCyc(c);
			queryCyc(c, defaultP);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CycApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //*catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}

	}
        
            @Inject private PrijaveFacade facade;
            
            @PostConstruct
            private void postconstruct(){        
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
            }

	public String importIntoCycIssue(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant AMZSReport = _c.getConstantByName("AMZSReport");
			CycConstant CycIssue = _c.makeCycConstant("AMZSIssue"+id);
			_c.assertIsa(CycIssue, AMZSReport);
                        amzsIssue = CycIssue.toString();
                        
                        assertionIssue = "(isa " +CycIssue +" " +AMZSReport + ")";
                        return assertionIssue; 				
	}
        
         public String importIntoCycSender(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
//                        String User = "(AMZSUserFn \"" +id +"\")";
//                        CycConstant Issue = _c.getConstantByName("AMZSIssue"+id);
                                                
                        CycList SenderOfInfo = _c.makeCycList("(#$senderOfInfo #$"+amzsIssue +" (#$AMZSUserFn \"" +id +"\"))");
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        _c.assertGaf(SenderOfInfo, Mt);
                        
                        
                        CycObject English = _c.getConstantByName("EnglishMt");
                        CycList NameString = _c.makeCycList("(#$nameString  (#$AMZSUserFn \"" +id +"\") \"" +name +" " +surname +"\")");
                        _c.assertGaf(NameString, English);
			assertionSender = SenderOfInfo + ", " + NameString +".";
                        return assertionSender; 				
	}
        
         
         public String printName(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (name.isEmpty()){
                        name = " / ";
                    }
                    return name;
         }
         
         public String printSurname(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (surname.isEmpty()){
                        surname = " / ";
                    }
                    return surname;
         }
         
         
         
        public String importIntoCycMember(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
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
             
        public String printMemberNo(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (member_no.isEmpty()){
                        member_no = " / ";
                    }
                    return member_no;
         }
        
        
        enum Orientation {NA_KOLESIH, NA_STREHI, NA_BOKU};
        enum Position{IZVEN_CESTE_DO_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI};
        public String importIntoCycEvent(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			event = 0;
                        CycObject Mt = _c.getConstantByName("BaseKB");
                        
                        if("nesreca".equals(parent2_malf)){ 
                                CycConstant VehicleAccident = _c.getConstantByName("VehicleAccident");
                                CycConstant CycAccident = _c.makeCycConstant("AMZSVehicleAccident"+id);
                                _c.assertIsa(CycAccident, VehicleAccident, Mt);
                                amzsEvent = CycAccident.toString();
                                assertionEvent = "(isa "+CycAccident +" VehicleAccident)";
//                                Position pos = Position.valueOf(toEnumCase(parent_malf));
//                                switch(pos)
//                                {
//                                    case V_JARKU_OB_CESTI:
//                                        
//                                        
//                                }
                                
                                Orientation orient = Orientation.valueOf(toEnumCase(malfunction));
                                switch(orient)
                                {
                                    case NA_KOLESIH: 
                                        CycConstant topSideUp = _c.getConstantByName("TopSideUp");
                                        CycList orient1 = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                            + "#$AMZSIssue" +id +") #$" +topSideUp +" #$AMZSVehicleAccident" +id +")");
                                        _c.assertGaf(orient1, Mt);
                                        assertionEvent = assertionEvent + ", " + orient1;
                                        break;
                                    case NA_STREHI:
                                        CycConstant upsideDown = _c.getConstantByName("UpsideDown");
                                        CycList orient2 = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                            + "#$AMZSIssue" +id +") #$" +upsideDown +" #$AMZSVehicleAccident" +id +")");
                                        _c.assertGaf(orient2, Mt);
                                        break;
                                    case NA_BOKU:
                                        CycConstant leftSideUp = _c.getConstantByName("LeftSideUp");
                                        CycList orient3 = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                            + "#$AMZSIssue" +id +") #$" +leftSideUp +" #$AMZSVehicleAccident" +id +")");
                                        _c.assertGaf(orient3, Mt);
                                        break;
                                
                                }
                                
//                                if ("na kolesih".equals(malfunction)){
//                                    CycConstant orientationVector = _c.getConstantByName("TopSideUp");
//                                    CycList orientation = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
//                                            + "#$AMZSIssue" +id +") #$" +orientationVector +" #$AMZSVehicleAccident" +id +")");
//                                    _c.assertGaf(orientation, Mt);
//                                }
                            }
                                                
                        else if("resevanje vozila".equals(parent2_malf)){
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
                            CycConstant RoadVehicleMalfunction = _c.getConstantByName("RoadVehicleMalfunction");
                            CycConstant AMZSVehicleMalfunction = _c.makeCycConstant("AMZSVehicleMalfunction"+id);
                            _c.assertIsa(AMZSVehicleMalfunction, RoadVehicleMalfunction);
                            amzsEvent = AMZSVehicleMalfunction.toString();
                        }
                        
                        return amzsEvent;			
	}
       
        public String printEvent(){
            return assertionEvent;
        }
        public String printMalfunction(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
            if (malfunction == null){
                        malfunction = " / ";
                    }
                    return malfunction;
         }
        
        
        public String printGrandparent() throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (parent2_malf == null){
                        parent2_malf = " / ";
                    }
                    return parent2_malf;
         }
        
        
        public String printParent() throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (parent_malf == null){
                        parent_malf = " / ";
                    }
                    return parent_malf;
         }
        
             
        public HashMap exportFromCycCarTypeList(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    CycFort fort = _c.getKnownFortByName("AutomobileTypeByBodyStyle");
                    CycList instances = _c.getAllInstances(fort);
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    mapTy = new HashMap<Object, Object>();
                    nameStringsTy = new CycList();
                    
                    for (Iterator it = instances.iterator(); it.hasNext();) {
                        Object constantTy = it.next();
                        CycFort nameStrTy = _c.getKnownFortByName(String.valueOf(constantTy));
                        CycList nameStr = _c.getNameStrings(nameStrTy, English);
                        if (!nameStr.isEmpty()){
                            mapTy.put(nameStr.get(0), constantTy);
                            nameStringsTy.add(nameStr.get(0)) ; 
                        }
                    }
                                        
                    return mapTy;
        }
        
        
        public CycList exportFromCycCarBrandList(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    CycFort fort = _c.getKnownFortByName("AutomobileTypeByBrand");
                    CycList instances = _c.getAllInstances(fort);
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    mapBr = new HashMap<Object, Object>();
                    nameStringsBr = new CycList();
                    
                    for (Iterator it = instances.iterator(); it.hasNext();) {
                        Object constantBr = it.next();
                        CycFort nameStrBr = _c.getKnownFortByName(String.valueOf(constantBr));
                        CycList nameStr = _c.getNameStrings(nameStrBr, English);
                        if (!nameStr.isEmpty()){
                            mapBr.put(nameStr.get(0), constantBr);
                            nameStringsBr.add(nameStr.get(0)) ; 
                        }
                    }
                    
                    return nameStringsBr;
        }
        
        
        
        public ArrayList<String> handleBrandChange() throws JSONException, UnknownHostException, CycApiException, IOException {  
                    CycAccess c = new CycAccess("aidemo", 3600);
                    modelL = new ArrayList<String>();
                    
                    if(!(inputBrand == null) && !inputBrand.equals("")) {
                        HashMap<Object, Object> map = getModelByBrand(c, inputBrand);
                        for (Map.Entry<Object, Object> entry :  map.entrySet()){
                            Object nameString = entry.getKey();
                            modelL.add(String.valueOf(nameString));
                        }
                    }  
                    return modelL;
    }
        
        public HashMap getModelByBrand(CycAccess _c, String _brand) throws JSONException, UnknownHostException, CycApiException, IOException {
                    
                    CycObject English = _c.getConstantByName("EnglishMt");
                    CycList specializations = new CycList();
                    mapMod = new HashMap<Object, Object>();
                    nameStringsMod = new CycList();
                    
                    if(!(_brand == null) && !_brand.equals("")){
                        Object mapBrand = mapBr.get(_brand);
                        String constantBr = String.valueOf(mapBrand);
                        CycFort fort = _c.getKnownFortByName(constantBr);
                        specializations = _c.getAllSpecs(fort, English);
                     }
                    

                    for (Iterator it = specializations.iterator(); it.hasNext();) {
                        Object constantMod = it.next();
                        CycFort nameStrMod = _c.getKnownFortByName(String.valueOf(constantMod));
                        CycList nameStr = _c.getNameStrings(nameStrMod, English);
                        if (!nameStr.isEmpty()){
                            mapMod.put(nameStr.get(0), constantMod);
                            nameStringsMod.add(String.valueOf(nameStr.get(0)));
                        }
                    }
                       
                    return mapMod;
                }
        

        
        public String importIntoCycVehicle(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    CycObject Mt = _c.getConstantByName("BaseKB");
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    mapTy = exportFromCycCarTypeList(_c);
                    if (!(type == null)){
                        String typeConst = String.valueOf(mapTy.get(type));
                        CycList Type = _c.makeCycList("(#$roadVehicleBodyStyle (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+typeConst +")");
                        _c.assertGaf(Type, Mt);
                        assertType = String.valueOf(Type);
                    }

                    mapMod = getModelByBrand(_c, inputBrand);
                    if (!(model == null)){
                        String modelConst = String.valueOf(mapMod.get(model));
                        CycList Model = _c.makeCycList("(#$isa (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+modelConst +")");
                        _c.assertGaf(Model, Mt);
                        assertModel = String.valueOf(Model);
                    }
                    
                    CycList Registrska = _c.makeCycList("(#$nameString  (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"" +registration +"\")");
                    _c.assertGaf(Registrska, English);

                    if(!"".equals(amzsEvent)){
                        CycList ObjectActedOn;
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
        
        public String printRegistration(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    if (registration.isEmpty()){
                        registration = " / ";
                    }
                    return registration;
         }
        
       
        public String importIntoCycTopic(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
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

        
        public String importIntoCycOccursAt(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
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
        
        public String importIntoCycDate(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
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

    public String getInputBrand() {
        return inputBrand;
    }

    public void setInputBrand(String inputBrand) {
        this.inputBrand = inputBrand;
    }

    public HashMap<Object, Object> getMapTy() {
        return mapTy;
    }

    public void setMapTy(HashMap<Object, Object> mapTy) {
        this.mapTy = mapTy;
    }

    public HashMap<Object, Object> getMapBr() {
        return mapBr;
    }

    public void setMapBr(HashMap<Object, Object> mapBr) {
        this.mapBr = mapBr;
    }

    public HashMap<Object, Object> getMapMod() {
        return mapMod;
    }

    public void setMapMod(HashMap<Object, Object> mapMod) {
        this.mapMod = mapMod;
    }
    
    
}
