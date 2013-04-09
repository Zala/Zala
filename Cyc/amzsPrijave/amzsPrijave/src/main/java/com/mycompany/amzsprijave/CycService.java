
package com.mycompany.amzsprijave;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;


@Stateless
public class CycService{
    
    private static final Logger log = Logger.getLogger(CycService.class.getName());
    @Inject private PrijaveFacade facade;
   
    
    public String getIssue(){
                String amzsIssue = "AMZSIssue"+ facade.getPrijave().get(0).getId();
                return amzsIssue;
        }
    
    
    enum Position{IZVEN_CESTE_DO_20M, IZVEN_CESTE_NAD_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI};
    
    public String getEvent(){
                String event = "AMZSVehicleAccident"+facade.getPrijave().get(0).getId();
                return event;
    }
    
    public String accident() throws UnknownHostException, IOException{
        
                CycAccess _c = new CycAccess("aidemo", 3600);
                CycObject Mt = _c.getConstantByName("BaseKB");
                CycConstant VehicleAccident = _c.getConstantByName("VehicleAccident");
                CycConstant CycAccident = _c.makeCycConstant(getEvent());
                _c.assertIsa(CycAccident, VehicleAccident, Mt);
                String assertionEvent = "(isa "+CycAccident +" VehicleAccident)";

                CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+ getIssue() +" #$VehicleAccident)");
                _c.assertGaf(issueEventType, Mt);

                Position pos = Position.valueOf(toEnumCase(facade.getPrijave().get(0).getParent_malf()));
                CycConstant position;
                CycList posL = new CycList(); 
                switch(pos)
                {
                    case V_JARKU_OB_CESTI: 
                        position = _c.getConstantByName("Ditch");
                        posL = _c.makeCycList("(#$objectFoundInLocationType #$" +getIssue() + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                +getIssue() +") #$" +position +")");
                        _c.assertGaf(posL, Mt);
                        assertionEvent = assertionEvent + ", " + posL;
                        break;
                    case NA_CESTI: 
                        position = _c.getConstantByName("Roadway");
                        posL = _c.makeCycList("(#$objectFoundInLocationType #$" +getIssue() + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                +getIssue() +") #$" +position +")");
                        assertionEvent = assertionEvent + ", " + posL;
                        break;
                    case IZVEN_CESTE_DO_20M: break;
                    case IZVEN_CESTE_NAD_20M: break;
                    case POD_CESTO: break;
                    default: break;

                }
                _c.assertGaf(posL, Mt);
                
                
                return assertionEvent;
    }
    
    
    
    
    enum Orientation {NA_KOLESIH, NA_STREHI, NA_BOKU};
    
    public String orientation() throws UnknownHostException, IOException{
                CycAccess _c = new CycAccess("aidemo", 3600);
                CycObject Mt = _c.getConstantByName("BaseKB");
                CycConstant orientation;
                CycList orientL = new CycList(); 
                
                String id = String.valueOf(facade.getPrijave().get(0).getId());
                Orientation orient = Orientation.valueOf(toEnumCase(facade.getPrijave().get(0).getMalfunction()));
                String assertionEvent = accident();
                
                switch(orient)
                {
                    case NA_KOLESIH: 
                        orientation = _c.getConstantByName("TopSideUp");
                        orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                            + "#$" +getIssue() +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                        assertionEvent = assertionEvent + ", " + orientL;
                        break;
                    case NA_STREHI:
                        orientation = _c.getConstantByName("UpsideDown");
                        orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                            + "#$AMZSIssue" +id +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                        assertionEvent = assertionEvent + ", " + orientL;
                        break;
                    case NA_BOKU:
                        orientation = _c.getConstantByName("LeftSideUp");
                        orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                            + "#$AMZSIssue" +id +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                        assertionEvent = assertionEvent + ", " + orientL;
                        break;
                    default: break;
                }
                _c.assertGaf(orientL, Mt);
                return assertionEvent;
                }
    
    
    public HashMap exportFromCycCarBrandList(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    CycFort fort = _c.getKnownFortByName("AutomobileTypeByBrand");
                    CycList instances = _c.getAllInstances(fort);
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    HashMap<Object, Object> mapBr = new HashMap<Object, Object>();
                    
                    for (Iterator it = instances.iterator(); it.hasNext();) {
                        Object constantBr = it.next();
                        CycFort nameStrBr = _c.getKnownFortByName(String.valueOf(constantBr));
                        CycList nameStr = _c.getNameStrings(nameStrBr, English);
                        if (!nameStr.isEmpty()){
                            mapBr.put(nameStr.get(0), constantBr);
                        }
                    }
                    
                    return mapBr;
        }
    
    
        public CycList carBrandStrings() throws UnknownHostException, IOException, JSONException{
                    CycAccess c = new CycAccess("aidemo", 3600);
                    HashMap<Object, Object> map = exportFromCycCarBrandList(c);
                    CycList carBrands = new CycList();
                    for ( Map.Entry<Object, Object> entry :  map.entrySet()){
                        Object nameString = entry.getKey();
                        carBrands.add(nameString);
                    }
        return carBrands;
    }
        
        
        
        
        public HashMap getModelByBrand(CycAccess _c, String _inputBrand) throws JSONException, UnknownHostException, CycApiException, IOException {
                    
                    CycObject English = _c.getConstantByName("EnglishMt");
                    CycList specializations = new CycList();
                    HashMap<Object, Object> mapMod = new HashMap<Object, Object>();
                    HashMap<Object, Object> mapBr = exportFromCycCarBrandList(_c);
                    
//                    ne razumem. zakaj zasteka, kadar ne izberem nic???
                    Object mapBrand = mapBr.get(_inputBrand);
                    if(mapBrand != null){
                        String constantBr = String.valueOf(mapBrand);
                        CycFort fort = _c.getKnownFortByName(constantBr);
                        specializations = _c.getAllSpecs(fort, English);

                        for (Iterator it = specializations.iterator(); it.hasNext();) {
                            Object constantMod = it.next();
                            CycFort nameStrMod = _c.getKnownFortByName(String.valueOf(constantMod));
                            CycList nameStr = _c.getNameStrings(nameStrMod, English);
                            if (!nameStr.isEmpty()){
                                mapMod.put(nameStr.get(0), constantMod);
                            }
                        }
                    }
                    return mapMod;
                }
        
        public HashMap getModelByBrandAllNameStrings(CycAccess _c, String _inputBrand) throws JSONException, UnknownHostException, CycApiException, IOException {
                    
                    CycObject English = _c.getConstantByName("EnglishMt");
                    CycList specializations = new CycList();
                    HashMap<Object, Object> mapMod = new HashMap<Object, Object>();
                    HashMap<Object, Object> mapBr = exportFromCycCarBrandList(_c);
                    
//                    ne razumem. zakaj zasteka, kadar ne izberem nic???
                    Object mapBrand = mapBr.get(_inputBrand);
                    if(mapBrand != null){
                        String constantBr = String.valueOf(mapBrand);
                        CycFort fort = _c.getKnownFortByName(constantBr);
                        specializations = _c.getAllSpecs(fort, English);

                        for (Iterator it = specializations.iterator(); it.hasNext();) {
                            Object constantMod = it.next();
                            CycFort nameStrMod = _c.getKnownFortByName(String.valueOf(constantMod));
                            CycList nameStr = _c.getNameStrings(nameStrMod, English);
                            if (!nameStr.isEmpty()){
                                for (Iterator str = nameStr.iterator(); str.hasNext();){
                                    Object strNext = str.next();
                                    mapMod.put(strNext, constantMod);
                                }
                            }
                        }
                    }
                    return mapMod;
                }
        
        public CycList carModelStrings(String _inputBrand) throws UnknownHostException, IOException, JSONException{
                    CycAccess c = new CycAccess("aidemo", 3600);
                    HashMap<Object, Object> mapBr = getModelByBrand(c, _inputBrand);
                    CycList carModels = new CycList();
                    for ( Map.Entry<Object, Object> entry :  mapBr.entrySet()){
                        Object nameString = entry.getKey();
                        carModels.add(nameString);
                    }
        return carModels;
        }
        
        
        
        public HashMap exportFromCycCarTypeList(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
                    CycFort fort = _c.getKnownFortByName("AutomobileTypeByBodyStyle");
                    CycList instances = _c.getAllInstances(fort);
                    CycObject English = _c.getConstantByName("EnglishMt");
                    
                    HashMap<Object, Object> mapTy = new HashMap<Object, Object>();
                    
                    for (Iterator it = instances.iterator(); it.hasNext();) {
                        Object constantTy = it.next();
                        CycFort nameStrTy = _c.getKnownFortByName(String.valueOf(constantTy));
                        CycList nameStr = _c.getNameStrings(nameStrTy, English);
                        if (!nameStr.isEmpty()){
                            mapTy.put(nameStr.get(0), constantTy);
                        }
                    }
                                        
                    return mapTy;
        }
        
        public CycList carTypeStrings() throws UnknownHostException, IOException, JSONException{
                    CycAccess c = new CycAccess("aidemo", 3600);
                    HashMap<Object, Object> mapTy = exportFromCycCarTypeList(c);
                    CycList carTypes = new CycList();
                    for ( Map.Entry<Object, Object> entry :  mapTy.entrySet()){
                        Object nameString = entry.getKey();
                        carTypes.add(nameString);
                    }
        return carTypes;
        }
        
        
        
        
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
}
