
package si.ijs.mobis.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class CycService {
    
    private static final Logger LOGGER  = Logger.getLogger(CycService.class.getName());
    @Inject private BaseService baseService;
      
    enum EventType{NESRECA, RESEVANJE_VOZILA, VZIG, MOTOR, PRENOS_MOCI, ELEKTRIKA, PODVOZJE, PNEVMATIKE, GORIVO, KLJUCAVNICA, OSTALO};
    enum StuckIn{OSTAL_V_BLATU, OSTAL_NA_KOLICKU, OSTAL_V_SNEGU, OSTAL_NA_PREVISU, OSTAL_NA_ZIDU_SKARPI, OSTALO};
    enum Position{IZVEN_CESTE_DO_20M, IZVEN_CESTE_NAD_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI};
    enum Orientation {NA_KOLESIH, NA_STREHI, NA_BOKU};
    enum Malfunction {VZIG, MOTOR, PRENOS_MOCI, ELEKTRIKA, PODVOZJE, PNEVMATIKE, GORIVO, KLJUCAVNICA, OSTALO};
    enum Ignition {VRTI, NE_VRTI, PRIZGAN_PORABNIK, OSTALO};
    enum IgnitionMalf {GORI_LUCKA_NA_ARMATURI, GORIJO_VSE_LUCKE_NA_ARMATURI, LUCI_GORIJO, LUCI_NE_GORIJO, AKUMULATOR};
    enum Engine {UGASNIL_MED_VOZNJO, IZGUBLJA_MOC_NE_VLECE, PREGREVA, GLAVNI_JERMEN, OSTALO};
    enum EngineMalf {ROPOTA, BEL_DIM, CRN_DIM, LUC_ZA_OLJE_SVETI, UTRIPA_RUMENA_ZA_MOTOR, ZAKUHAL, BREZ_HLADILNE, SVETI_RDECA_ZA_HLADILNO_TEKOCINO, OSTALO};
    enum Transmission {MENJALNIK, SKLOPKA, KARDAN, PEDAL_ZA_PLIN, OSTALO};
    enum TransmissionMalf {NE_PRESTAVLJA, OSTAL_V_PRESTAVI, AVTOMATSKI_NE_DELA, ZICA_SKLOPKE, PEDAL_SKLOPKE_OSTAL_NOTRI, PEDAL_SKLOPKE_TRD, PADEL_DOL, DELA_NE_GRE_V_OBRATE, SE_NE_ODZIVA, OSTALO};
    
    
    public String getIssue() {
                String amzsIssue = "AMZSIssue"+ baseService.getLastEntry().getId();
                return amzsIssue;
        }
    
    public String getEvent() {
                String event = "";
                String pm2 = baseService.getLastEntry().getParent2_malf();
                Integer id = baseService.getLastEntry().getId();
                if(pm2 != null){
                    EventType e = EventType.valueOf(toEnumCase(pm2));
                    StuckIn s = StuckIn.valueOf("OSTALO");
                    switch(e)
                    {
                        case NESRECA: 
                            event = "AMZSVehicleAccident"+baseService.getLastEntry().getId();
                            break;
                        case RESEVANJE_VOZILA:
                            String pm = baseService.getLastEntry().getParent_malf();
                            try {
                                s = StuckIn.valueOf(toEnumCase(pm));
                            } catch (NullPointerException n){
                                System.out.println("You have to choose a value for a type of stuck situation"); 
                            }
                            switch(s)
                            {
                                case OSTAL_V_BLATU:
//                                    event = "AMZSStuckInMud"+id;
//                                    break;
                                case OSTAL_NA_KOLICKU:
//                                    event = "AMZSStuckOnAPole"+id;
//                                    break;
                                case OSTAL_V_SNEGU:
//                                    event = "AMZSStuckInSnow"+id;
//                                    break;
                                case OSTAL_NA_PREVISU:
//                                    event = "AMZSStuckOnACliff"+id;
//                                    break;
                                case OSTAL_NA_ZIDU_SKARPI:
//                                    event = "AMZSStuckOnAWall"+id;
//                                    break;
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
                CycObject Mt = _c.getConstantByName("AMZSMt");
                
                StuckIn s = StuckIn.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
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
    
    public String accident(CycAccess _c) {
                String assertionEvent = "";
                
                try {
                    CycObject Mt = _c.getConstantByName("BaseKB");
                    CycConstant VehicleAccident = _c.getConstantByName("VehicleAccident");
                    CycConstant CycAccident = _c.makeCycConstant(getEvent());
                    _c.assertIsa(CycAccident, VehicleAccident, Mt);
                    assertionEvent = "(isa "+CycAccident +" VehicleAccident)";

                    CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+ getIssue() +" #$VehicleAccident)");
                    _c.assertGaf(issueEventType, Mt);

                    Position pos = Position.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
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

                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }

                return assertionEvent;
                }
        
    public String malfunction(CycAccess _c) {
            String assertionEvent = "";
            try {
                CycObject Mt = _c.getConstantByName("AMZSMt");
                CycService.Malfunction malf = CycService.Malfunction.valueOf(toEnumCase(baseService.getLastEntry().getParent2_malf()));

                switch(malf)
                {
                    case VZIG: 
                        assertionEvent = ignition(_c, Mt);
                        break;
                    case MOTOR:
                        assertionEvent = engine(_c, Mt);
                    case PRENOS_MOCI:
                        assertionEvent = transmission(_c, Mt);
                    default: break;
                }

            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }

            return assertionEvent;
                }   
    
    public String ignition(CycAccess _c, CycObject Mt) {
                String assertionEvent = "";
                
                try {
                    CycList malfL = new CycList(); 

                    Ignition ignit = Ignition.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));

                    switch(ignit)
                    {
                        case VRTI: 
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Automobile #$AutoEngineTurnOver)");
                            assertionEvent = String.valueOf(malfL);

                            IgnitionMalf turns = IgnitionMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
                            _c.assertGaf(malfL, Mt);
                            switch(turns)
                            {
                                case GORI_LUCKA_NA_ARMATURI:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$PrototypicalIndicatorLight #$Device-On)");
                                    assertionEvent = assertionEvent + String.valueOf(malfL);  
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case GORIJO_VSE_LUCKE_NA_ARMATURI:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$VehicleIndicatorLight #$Device-On)");
                                    assertionEvent = assertionEvent + String.valueOf(malfL);
                                    _c.assertGaf(malfL, Mt);     
                                    break;
                            }
                            break;
                            
                        case NE_VRTI: 
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Automobile #$EngineDoesntTurnOver)");
                            assertionEvent = String.valueOf(malfL);

                            IgnitionMalf noTurn = IgnitionMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
                            _c.assertGaf(malfL, Mt);
                            switch(noTurn)
                            {
                                case LUCI_GORIJO:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutomobileHeadLight #$Device-On)");
                                    assertionEvent = assertionEvent + String.valueOf(malfL);  
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case LUCI_NE_GORIJO:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutomobileHeadLight #$Device-FunctionallyDefective)");
                                    assertionEvent = assertionEvent + String.valueOf(malfL);  
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case AKUMULATOR:
                                    break;
                            }
                            break;
                            
                        case PRIZGAN_PORABNIK: 
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$ConsumerElectronicDevice #$Device-On)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                            break;
                        default: break;
                            
                    }
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return assertionEvent;
                }
    
    public String engine(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            Engine eng = Engine.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
            try{
                switch(eng)
                {
                    case UGASNIL_MED_VOZNJO:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$AutoEngineStalls)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case IZGUBLJA_MOC_NE_VLECE:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$AutoEngineLoosingPower)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREGREVA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$AutoEngineOverheating)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case GLAVNI_JERMEN:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$EngineBelt #$EngineBeltMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL) + enMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return assertionEvent;
            }

    public String enMalf(CycAccess _c, CycObject Mt) {
        
            String assertionEvent = "";
            
            try {

                CycList malfL = new CycList();
                EngineMalf engPow = EngineMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));

                switch(engPow)
                {
                    case ROPOTA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Noise-Unusual #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case BEL_DIM:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-White #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case CRN_DIM:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-Black #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case LUC_ZA_OLJE_SVETI:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$OilLevelIndicatorLight #$Device-On)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                        break;
                    case UTRIPA_RUMENA_ZA_MOTOR:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$CheckEngineIndicatorLight #$Device-On)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                        break;
                    case ZAKUHAL:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$AutoEngineOverheated)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                        break;
                    case BREZ_HLADILNE:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$CoolantTank #$CoolantTank-Empty)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                        break;
                    case SVETI_RDECA_ZA_HLADILNO_TEKOCINO:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #LowCoolantIndicatorLight #$Device-On)");
                            assertionEvent = String.valueOf(malfL);
                            _c.assertGaf(malfL, Mt);
                        break;                        
                    default:
                        break;
                }

            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }

            return assertionEvent;
            }
    
    
    
    public String transmission(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            Transmission trans = Transmission.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
            try{
                switch(trans)
                {
                    case MENJALNIK:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Gearbox #$GearboxMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SKLOPKA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Clutch #$ClutchMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case KARDAN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$PTOShaft #$PTOMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$GasPedal #$GasPedalMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                }
                assertionEvent = String.valueOf(malfL) + transMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }


    public String transMalf(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            TransmissionMalf trans = TransmissionMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
            try{
                switch(trans)
                {
                    case NE_PRESTAVLJA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Gearbox #$DoesntShiftGears)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_V_PRESTAVI:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Gearbox #$StuckInGear)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AVTOMATSKI_NE_DELA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Gearbox #$AutomaticDoesntShift)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZICA_SKLOPKE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$ClutchWire #$ClutchWireMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_OSTAL_NOTRI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Clutch #$ClutchPedal-StuckDown)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_TRD:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Clutch #$ClutchPedal-Stiff)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PADEL_DOL:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$GasPedal #$GasPedal-StuckDown)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case DELA_NE_GRE_V_OBRATE:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$GasPedal #$GasPedal-DoesntAccelerate)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SE_NE_ODZIVA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$GasPedal #$GasPedal-DoesntRespond)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL) + transMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
    
    
    
    public String orientation(CycAccess _c) {
                String assertionEvent = "";
                try {
                    CycObject Mt = _c.getConstantByName("BaseKB");
                    CycConstant orientation;
                    CycList orientL = new CycList(); 
                    assertionEvent = accident(_c);

                    String id = String.valueOf(baseService.getLastEntry().getId());
                    Orientation orient = Orientation.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));

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
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
    
    public List<String> carBrandStrings(HashMap hm) throws UnknownHostException, IOException, JSONException {
                Set<Object> carBr = hm.keySet();
                List<String> carBrands = Arrays.asList(carBr.toArray( new String[0] ) );
                Collections.sort(carBrands);
                return carBrands;
    }

    public HashMap getModelByBrand(CycAccess _c, String _inputBrand, HashMap<Object,Object> hm) throws JSONException, UnknownHostException, CycApiException, IOException {
                HashMap<String, CycConstant> mapModelNames = new HashMap<String, CycConstant>();    
                if (!"null".equals(_inputBrand)) {
                    Object brand = hm.get(_inputBrand);
                    DefaultInferenceParameters defaultP = new DefaultInferenceParameters(_c);
                    InferenceWorkerSynch worker;
                    InferenceResultSet rs;
                    String query =  "(#$and \n" +
                                    "  (#$genls ?X #$"+ brand +") \n" +
                                    "  (#$preferredNameString ?X ?NAME))";
                    worker = new DefaultInferenceWorkerSynch(query, 
                                                        _c.makeELMt(CycAccess.inferencePSC),
                                                        defaultP, _c, 500000);

                    long startTime = System.nanoTime();
                    LOGGER.log(Level.INFO, "Calling cyc with query: {0}", query);


                    rs = worker.executeQuery();
                    while (rs.next())
                    {
                        CycConstant carModel = rs.getConstant("?X");
                        String carModelName = rs.getString("?NAME");
                        mapModelNames.put(carModelName, carModel);
                    }
                    rs.close();
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Call took: {0}", new Date(duration).toString());
                    }
                return mapModelNames;
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
                string = string.toUpperCase().replaceAll("-", "_");
		return string;
	}
    
    public static String toConstCase(String s) {
		String string = s;
		string = string.replaceAll("#\\$", ""); 
		return string;
	}
}
