/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amzsprijave;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.cycobject.CycSymbol;
import org.opencyc.cycobject.ELMt;
import org.opencyc.inference.DefaultInferenceParameters;
import org.opencyc.inference.DefaultInferenceWorkerSynch;
import org.opencyc.inference.InferenceResultSet;
import org.opencyc.inference.InferenceWorkerSynch;

@Named
@RequestScoped
public class amzsIssue {
        
        private Integer id;
        private String name;
        private String surname;
        private String brand;
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
        private String assertionVehicle;
        private String assertionSender;
        private String assertionTopic;
        private String assertionObject;
        private String assertionOccursAt;
        private String assertionDate;
        
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
                name = String.valueOf(podatki.get(0).getIme());
                surname = String.valueOf(podatki.get(0).getPriimek());
                brand = String.valueOf(podatki.get(0).getZnamka());
                type = String.valueOf(podatki.get(0).getTip());
                registration = String.valueOf(podatki.get(0).getRegistrska());
                member = String.valueOf(podatki.get(0).getClan());
                member_no = String.valueOf(podatki.get(0).getClanska_st());
                date = podatki.get(0).getDatum();
                parent2_malf = String.valueOf(podatki.get(0).getParent2_malf());
                parent_malf = String.valueOf(podatki.get(0).getParent_malf());
                malfunction = String.valueOf(podatki.get(0).getMalfunction());
            }

	public String importIntoCycIssue(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant AMZSReport = _c.getConstantByName("AMZSReport");
			CycConstant CycIssue = _c.makeCycConstant("AMZSIssue"+id);
			_c.assertIsa(CycIssue, AMZSReport);
                        assertionIssue = "(isa " +CycIssue +" " +AMZSReport + ")";
                        return assertionIssue; 				
	}
        
         public String importIntoCycSender(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                        String User = "(AMZSUserFn \"" +id +"\")";
                        CycConstant Issue = _c.getConstantByName("AMZSIssue"+id);
                                                
                        CycList SenderOfInfo = _c.makeCycList("(#$senderOfInfo #$AMZSIssue"+id +" (#$AMZSUserFn \"" +id +"\"))");
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        _c.assertGaf(SenderOfInfo, Mt);
                        
                        if ("Da".equals(member)){ 
                            if (member_no.isEmpty()){
                                member_no = "Not known";
                            }
                            CycList Member = _c.makeCycList("(#$groupMemberWithMembershipID (#$AMZSUserFn \"" +id +"\") #$AMZSKlub \""+member_no +"\")");
                            _c.assertGaf(Member, Mt);
                        }
                        
                        if ("null".equals(member)){
                            member = "No information";
                        }
                        
                        CycList NameString = _c.makeCycList("(#$nameString  (#$AMZSUserFn \"" +id +"\") \"" +name +" " +surname +"\")");
                        _c.assertGaf(NameString, Mt);
			assertionSender = "(senderOfInfo " +Issue +" " +User +"), User named: " +name +" " +surname +", oseba je clan AMZS kluba: "+member +"." ;
                        return assertionSender; 				
	}
         
        
        public String importIntoCycEvent(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			 
                        if ("nesreca".equals(parent2_malf)){
                            CycConstant VehicleAccident = _c.getConstantByName("VehicleAccident");
                            CycConstant CycAccident = _c.makeCycConstant("AMZSVehicleAccident"+id);
                            _c.assertIsa(CycAccident, VehicleAccident);
                            amzsEvent = CycAccident.toString();
                        } 
                        
                        if("resevanje vozila".equals(parent2_malf)){
                            if("ostal v blatu".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInMudSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckInMud"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            if("ostal v snegu".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInSnowSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckInSnow"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            if("ostal na kolicku".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAPoleSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnAPole"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            if("ostal na previsu".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnACliffSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnACliff"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            if("ostal na zidu/skarpi".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAWallSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckOnAWall"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                            if("ostalo".equals(parent_malf)){ 
                                    CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleSituation");
                                    CycConstant AMZSStuckSituation = _c.makeCycConstant("AMZSStuckSituation"+id);
                                    _c.assertIsa(AMZSStuckSituation, StuckOrConfinedVehicleSituation);
                                    amzsEvent = AMZSStuckSituation.toString();
                            }
                        }
                        
                        else {
                            CycConstant StuckOrConfinedVehicleSituation = _c.getConstantByName("RoadVehicleMalfunction");
                            CycConstant AMZSVehicleMalfunction = _c.makeCycConstant("AMZSVehicleMalfunction"+id);
                            _c.assertIsa(AMZSVehicleMalfunction, StuckOrConfinedVehicleSituation);
                            amzsEvent = AMZSVehicleMalfunction.toString();
                        }
                             
                        return amzsEvent;			
	}
       
        public String importIntoCycVehicle(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			CycObject Mt = _c.getConstantByName("AMZSMt");
                        
                        CycList ObjectActedOn = _c.makeCycList("(#$objectActedOn  #$"+amzsEvent +" ( #$AMZSRoadVehicleFn \"" +name +" " +surname +"\"))");
                        _c.assertGaf(ObjectActedOn, Mt);
                        
                        CycList Registrska = _c.makeCycList("(#$nameString  (#$AMZSRoadVehicleFn \"" +name +" " +surname +"\") \"" +registration +"\")");
                        _c.assertGaf(Registrska, Mt);
                        
                        if (type.isEmpty() == false){
                            CycList Type = _c.makeCycList("(#$isa (#$AMZSRoadVehicleFn \"" +name +" " +surname +"\") #$"+type +")");
                            _c.assertGaf(Type, Mt);
                        }
                        
                        if (brand.isEmpty() == false){
                            CycList Model = _c.makeCycList("(#$isa (#$AMZSRoadVehicleFn \"" +name +" " +surname +"\") #$"+brand +")");
                            _c.assertGaf(Model, Mt);
                        }

                        if("zakuhal".equals(malfunction)){
                            CycList Overheated = _c.makeCycList("(#$isa (#$AMZSRoadVehicleFn \"" +name +" " +surname +"\") #$VehicleDevice-Overheated)");
                            _c.assertGaf(Overheated, Mt);
                        }
			assertionVehicle = "(objectActedOn #$"+amzsEvent+" (#$AMZSRoadVehicleFn \"" +name +" " +surname +"\")), car of type "+brand +" " +type +", with registration number: "+registration;
                        return assertionVehicle; 				
	}

         
        public String importIntoCycTopic(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant Event = _c.getConstantByName(amzsEvent);
                        CycConstant Issue = _c.getConstantByName("AMZSIssue"+id);
                        CycConstant Predicate = _c.getConstantByName("topicOfInfoTransfer");
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        _c.assertGaf(Mt, Predicate, Issue, Event);
			assertionTopic = "(topicOfInfoTransfer " +Issue +" " +Event +")";
                        return assertionTopic; 				
	}

        
        public String importIntoCycOccursAt(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
			CycConstant Event = _c.getConstantByName(amzsEvent);
                        CycConstant LocationFn = _c.getConstantByName("StreetNamedFn");
                        String Street = "\"Trzaska cesta\"";
                        String City = "CityOfLjubljanaSlovenia";
                        
                        String Place = "(#$" +LocationFn +" " +Street +" #$" +City +")";
                        CycObject Mt = _c.getConstantByName("AMZSMt");
                        CycConstant Predicate = _c.getConstantByName("eventOccursAt");
                        String StringEvent = "(#$eventOccursAt #$" +Event +" " +Place +")";
                        CycList EventL = _c.makeCycList(StringEvent);
                        _c.assertGaf(EventL, Mt);
			assertionOccursAt = "(" +Predicate +" " +Event +" ("+LocationFn +" "+Street +" "+City +"))";
                        return assertionOccursAt; 				
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
                        
                        assertionDate = "Date: " +Year + "-" + Month +"-" +Day;
                                              
                        return assertionDate;
                        
        }
        
        public String importIntoCycHlid(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                        CycConstant Event = _c.getConstantByName(amzsEvent);
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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
    
}
