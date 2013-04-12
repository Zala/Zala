
package si.ijs.mobis.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import si.ijs.mobis.org.json.JSONException;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycObject;
import org.opencyc.inference.DefaultInferenceParameters;
import org.opencyc.inference.DefaultInferenceWorkerSynch;
import org.opencyc.inference.InferenceResultSet;
import org.opencyc.inference.InferenceWorkerSynch;


@Stateless
public class CycService{
    
    private static final Logger LOGGER  = Logger.getLogger(CycService.class.getName());
    
    @Inject private BaseService baseService;
   
    enum EventType{NESRECA, RESEVANJE_VOZILA, VZIG, MOTOR, PRENOS_MOCI, ELEKTRIKA, PODVOZJE, PNEVMATIKE, GORIVO, KLJUCAVNICA, OSTALO};
    enum StuckIn{OSTAL_V_BLATU, OSTAL_NA_KOLICKU, OSTAL_V_SNEGU, OSTAL_NA_PREVISU, OSTAL_NA_ZIDU_SKARPI, OSTALO};
    enum Position{IZVEN_CESTE_DO_20M, IZVEN_CESTE_NAD_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI};
    enum Orientation {NA_KOLESIH, NA_STREHI, NA_BOKU};

    public String getIssue() {
                String amzsIssue = "AMZSIssue"+ baseService.getData().get(0).getId();
                return amzsIssue;
        }
    
    public String getEvent() {
                String event = "";
                String pm2 = baseService.getData().get(0).getParent2_malf();
                Integer id = baseService.getData().get(0).getId();
                if(pm2 != null){
                    EventType e = EventType.valueOf(toEnumCase(pm2));
                    switch(e)
                    {
                        case NESRECA: 
                            event = "AMZSVehicleAccident"+baseService.getData().get(0).getId();
                            break;
                        case RESEVANJE_VOZILA:
                            String pm = baseService.getData().get(0).getParent_malf();
                            StuckIn s = StuckIn.valueOf(toEnumCase(pm));
                            switch(s)
                            {
                                case OSTAL_V_BLATU:
                                    event = "AMZSStuckInMud"+id;
                                    break;
                                case OSTAL_NA_KOLICKU:
                                    event = "AMZSStuckOnAPole"+id;
                                    break;
                                case OSTAL_V_SNEGU:
                                    event = "AMZSStuckInSnow"+id;
                                    break;
                                case OSTAL_NA_PREVISU:
                                    event = "AMZSStuckOnACliff"+id;
                                    break;
                                case OSTAL_NA_ZIDU_SKARPI:
                                    event = "AMZSStuckOnAWall"+id;
                                    break;
                                default: 
                                    event = "(#$StuckOrConfinedVehicleSituationFn #$" + getIssue() +")";
                                    break;                                
                            }
                            break;
                        default: 
                            event = "AMZSVehicleMalfunction" +id;
                    }
                }
                return event;
    }
    
    public String rescuing(CycAccess _c) throws UnknownHostException, IOException {
                CycObject Mt = _c.getConstantByName("BaseKB");
                
                StuckIn s = StuckIn.valueOf(toEnumCase(baseService.getData().get(0).getParent_malf()));
                CycConstant StuckOrConfinedVehicleSituation = null;
                CycConstant iet = _c.getConstantByName("issueEventType");
                CycList issueET = new CycList();
                        switch(s)
                        {
                            case OSTAL_V_BLATU: 
                                StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInMudSituation");
                                issueET = _c.makeCycList("(#$"+ iet +" #$"+ getIssue() +" #$" +StuckOrConfinedVehicleSituation  +")");
                                _c.assertGaf(issueET, Mt);
                                break;
                            case OSTAL_NA_KOLICKU: 
                                StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAPoleSituation");
                                issueET = _c.makeCycList("(#$"+ iet +" #$"+ getIssue() +" #$" +StuckOrConfinedVehicleSituation  +")");
                                _c.assertGaf(issueET, Mt);
                                break;
                            case OSTAL_V_SNEGU: 
                                StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleInSnowSituation");
                                issueET = _c.makeCycList("(#$"+ iet +" #$"+ getIssue() +" #$" +StuckOrConfinedVehicleSituation  +")");
                                _c.assertGaf(issueET, Mt);
                                break;
                            case OSTAL_NA_PREVISU: 
                                StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnACliffSituation");
                                issueET = _c.makeCycList("(#$"+ iet +" #$"+ getIssue() +" #$" +StuckOrConfinedVehicleSituation  +")");
                                _c.assertGaf(issueET, Mt);
                                break;
                            case OSTAL_NA_ZIDU_SKARPI:
                                StuckOrConfinedVehicleSituation = _c.getConstantByName("StuckOrConfinedVehicleOnAWallSituation");
                                issueET = _c.makeCycList("(#$"+ iet +" #$"+ getIssue() +" #$" +StuckOrConfinedVehicleSituation  +")");
                                _c.assertGaf(issueET, Mt);
                                break;
                            default: break;                                
                        }
                return String.valueOf(StuckOrConfinedVehicleSituation);
    }
    
    public String accident(CycAccess _c) throws UnknownHostException, IOException {
                CycObject Mt = _c.getConstantByName("BaseKB");
                CycConstant VehicleAccident = _c.getConstantByName("VehicleAccident");
                CycConstant CycAccident = _c.makeCycConstant(getEvent());
                _c.assertIsa(CycAccident, VehicleAccident, Mt);
                String assertionEvent = "(isa "+CycAccident +" VehicleAccident)";

                CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+ getIssue() +" #$VehicleAccident)");
                _c.assertGaf(issueEventType, Mt);

                Position pos = Position.valueOf(toEnumCase(baseService.getData().get(0).getParent_malf()));
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
                        _c.assertGaf(posL, Mt);
                        break;
                    case NA_CESTI: 
                        position = _c.getConstantByName("Roadway");
                        posL = _c.makeCycList("(#$objectFoundInLocationType #$" +getIssue() + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                +getIssue() +") #$" +position +")");
                        assertionEvent = assertionEvent + ", " + posL;
                        _c.assertGaf(posL, Mt);
                        break;
                    case IZVEN_CESTE_DO_20M: break;
                    case IZVEN_CESTE_NAD_20M: break;
                    case POD_CESTO: break;
                    default: break;

                }
//                _c.assertGaf(posL, Mt); ko bojo vsi dokoncani
                
                
                return assertionEvent;
    }
        
    public String orientation(CycAccess _c) throws UnknownHostException, IOException {
                CycObject Mt = _c.getConstantByName("BaseKB");
                CycConstant orientation;
                CycList orientL = new CycList(); 
                
                String id = String.valueOf(baseService.getData().get(0).getId());
                Orientation orient = Orientation.valueOf(toEnumCase(baseService.getData().get(0).getMalfunction()));
                String assertionEvent = accident(_c);
                
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
    
    
    /**
     * Accesses Cyc and returns hashmap of car brand names and appropriate Cyc 
     * Constants
     * 
     * @param _c
     * @return
     * @throws JSONException
     * @throws UnknownHostException
     * @throws CycApiException
     * @throws IOException 
     */
    public HashMap exportFromCycCarBrandList(CycAccess _c) throws JSONException, UnknownHostException, CycApiException, IOException {
        DefaultInferenceParameters defaultP = new DefaultInferenceParameters(_c);
        InferenceWorkerSynch worker;
        InferenceResultSet rs;
        String query =  "(#$and \n" +
                        "  (#$isa ?X #$AutomobileTypeByBrand) \n" +
                        "  (#$nameString ?X ?NAME))";
        
        worker = new DefaultInferenceWorkerSynch(query, 
                                            _c.makeELMt(CycAccess.inferencePSC),
                                            defaultP, _c, 500000);
        HashMap<String, CycConstant> mapBrandNames = new HashMap<String, CycConstant>();
        
        long startTime = System.nanoTime();
        LOGGER.log(Level.INFO, "Calling cyc with query: {0}", query);
         
        rs = worker.executeQuery();
        while (rs.next())
        {
            CycConstant carBrand = rs.getConstant("?X");
            String carBrandName = rs.getString("?NAME");
            mapBrandNames.put(carBrandName, carBrand);
	}
        rs.close();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        LOGGER.log(Level.INFO, "Call took: {0}", new Date(duration).toString());
        
        return mapBrandNames;
    }
    
    public Set<Object> carBrandStrings(HashMap hm) throws UnknownHostException, IOException, JSONException {
                Set<Object> carBrands = hm.keySet();
                return carBrands;
    }

    public HashMap getModelByBrand(CycAccess _c, String _inputBrand, HashMap<Object,Object> hm) throws JSONException, UnknownHostException, CycApiException, IOException {

                CycObject English = _c.getConstantByName("EnglishMt");
                CycList specializations = new CycList();
                HashMap<Object, Object> mapMod = new HashMap<Object, Object>();
                HashMap<Object, Object> mapBr = hm;

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

    public HashMap getModelByBrandAllNameStrings(CycAccess _c, String _inputBrand, HashMap<Object,Object> hm) throws JSONException, UnknownHostException, CycApiException, IOException {

                CycObject English = _c.getConstantByName("EnglishMt");
                CycList specializations = new CycList();
                HashMap<Object, Object> mapMod = new HashMap<Object, Object>();
                HashMap<Object, Object> mapBr = hm;

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
    
    public static String toEnumCase(String s) {
		String string = s;
		string = string.toUpperCase().replaceAll("\\s+", "_");    // zamenjaj enega ali vec preslednov z _
		string = string.toUpperCase().replaceAll("/", "_");
		return string;
	}

}
