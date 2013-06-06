
package si.ijs.mobis.service;

import java.io.IOException;
import java.net.UnknownHostException;
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
    enum Electricity {OSTAL_BREZ_MED_VOZNJO, OSTAL_BREZ_NA_PARKIRISCU, LUCI, BRISALCI, SIPE_RAZBITE, SIPA_SE_NE_ZAPRE, OSTALO};
    enum ElectricityMalf {ALTERNATOR, AKUMULATOR, VAROVALKE, PREDNJE, ZADNJE, DOLGE, KRATKE, VEC_LUCI_NE_DELA, STOP_LUC, SMERNIKI, MOTORCEK, PREDNJA, ZADNJA, STRANSKA, VOZNIKOVA, SOVOZNIKOVA, STRANSKA_ZADAJ, OSTALO};
    enum Chassis {KRMILNI_MEHANIZEM, VZMETENJE, ZAVORE, HIDRAVLIKA, OSTALO};
    enum ChassisMalf {VOLAN_TRD, ZGLOB, ROKA, KONCNIK, AMORTIZER, VZMET, ZABLOKIRANE, ODPOVEDALE, AVTO_NA_TLEH, AVTO_NI_NA_TLEH};    
    enum Tires{PRAZNA, PREDRTA, VEC_PRAZNIH, VEC_PREDRTIH, BREZ_1, BREZ_VEC, OSTALO};
    enum TiresMalf {IMA_REZERVO, NIMA_REZERVE, NI_KLJUCA, VARNOSTNI_VIJAK, NE_MORE_ODVITI_VIJAKA, UKRADENA, NI_UKRADENA, OSTALO};
    enum Fuel {BREZ, NAROBE_TOCIL, ZMRZNJENO, UMAZANO, OSTALO};
    enum FuelMalf {EURO_95, EURO_100, DIESEL, OSTALO};
    enum Lock {KLJUC_SE_NE_OBRNE, KLJUC_ZLOMLJEN, SE_VRTI_V_PRAZNO, ZAKLENJENI_KLJUCI_KARTICA, OSTALO};
    enum LockMalf {V_KLJUCAVNICI_VZIGA, V_VRATIH, V_PRTLJAZNIKU, NA_SEDEZU, V_TORBICI_PREDALU, V_KLJUCAVNICI_VZIGA_MOTOR_TECE,  V_KLJUCAVNICI_VZIGA_MOTOR_NE_TECE, OSTALO};
    
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
                    switch(e)
                    {
                        case NESRECA: 
                            event = "AMZSVehicleAccident"+id;
                            break;
                        case RESEVANJE_VOZILA:
                            event = "(#$StuckOrConfinedVehicleSituationFn #$" + getIssue() +")";
                            break;
                        default: 
                            event = "AMZSVehicleMalfunction" +id;
                    }
                }
                return event;
    }
    
    public String rescuing(CycAccess _c) {
                    String assertionEvent = "";
                    CycList loc = new CycList();
                    
                    try {
                        CycObject Mt = _c.getConstantByName("UniversalVocabularyMt");
                        CycList issueET = _c.makeCycList("(#$issueEventType #$"+ getIssue() +" #$StuckOrConfinedVehicleSituation)");
                        _c.assertGaf(issueET, Mt);
                        assertionEvent = String.valueOf(issueET);
                        
                        StuckIn s = StuckIn.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
                                switch(s)
                                {
                                    case OSTAL_V_BLATU: 
                                        loc = _c.makeCycList("(#$objectFoundInLocationType #$"+getIssue() +" (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$MuddyArea)");
                                        _c.assertGaf(loc, Mt);
                                        break;
                                    case OSTAL_NA_KOLICKU: 
                                        loc = _c.makeCycList("(#$objectFoundInLocationType #$"+getIssue() +" (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Pole-Rod)");
                                        _c.assertGaf(loc, Mt);
                                        break;
                                    case OSTAL_V_SNEGU: 
                                        loc = _c.makeCycList("(#$objectFoundInLocationType #$"+getIssue() +" (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$SnowCoveredArea)");
                                        _c.assertGaf(loc, Mt);
                                        break;
                                    case OSTAL_NA_PREVISU: 
                                        loc = _c.makeCycList("(#$objectFoundInLocationType #$"+getIssue() +" (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Cliff)");
                                        _c.assertGaf(loc, Mt);
                                        break;
                                    case OSTAL_NA_ZIDU_SKARPI:
                                        loc = _c.makeCycList("(#$objectFoundInLocationType #$"+getIssue() +" (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Wall-ExternalToStructure)");
                                        _c.assertGaf(loc, Mt);
                                        break;
                                    default: break;                                
                                }
                            
                        assertionEvent = assertionEvent +", " + String.valueOf(loc);
                            
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    return assertionEvent;
                }
    
    public String accident(CycAccess _c) {
                String assertionEvent = "";
                
                try {
                    CycObject Mt = _c.getConstantByName("UniversalVocabularyMt");
                    CycConstant CycAccident = _c.makeCycConstant(getEvent());
                    CycList event = _c.makeCycList("(#$isa #$"+CycAccident +" #$VehicleAccident)");
                    assertionEvent = String.valueOf(event);

                    CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+ getIssue() +" #$VehicleAccident)");
                    _c.assertGaf(issueEventType, Mt);
                    assertionEvent = assertionEvent +", "+ String.valueOf(issueEventType);

                    Position pos = Position.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
                    CycList posL = new CycList(); 
                    switch(pos)
                    {
                        case V_JARKU_OB_CESTI: 
                            posL = _c.makeCycList("(#$objectFoundInLocationType #$" +getIssue() + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$Ditch)");
                            _c.assertGaf(posL, Mt);
                            break;
                        case NA_CESTI: 
                            posL = _c.makeCycList("(#$objectFoundInLocationType #$" +getIssue() + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$Roadway)");
                            _c.assertGaf(posL, Mt);
                            break;
                        case IZVEN_CESTE_DO_20M: break;
                        case IZVEN_CESTE_NAD_20M: break;
                        case POD_CESTO: break;
                    }
                
                    assertionEvent = assertionEvent + ", " + posL;
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
    
    public String orientation(CycAccess _c) {
                String assertionEvent = "";
                try {
                    CycObject Mt = _c.getConstantByName("UniversalVocabularyMt");
                    CycConstant orientation;
                    CycList orientL = new CycList(); 

                    String id = String.valueOf(baseService.getLastEntry().getId());
                    Orientation orient = Orientation.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));

                    switch(orient)
                    {
                        case NA_KOLESIH: 
                            orientation = _c.getConstantByName("TopSideUp");
                            orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$" +getIssue() +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                            break;
                        case NA_STREHI:
                            orientation = _c.getConstantByName("UpsideDown");
                            orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$AMZSIssue" +id +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                            break;
                        case NA_BOKU:
                            orientation = _c.getConstantByName("LeftSideUp");
                            orientL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$AMZSIssue" +id +") #$" +orientation +" #$AMZSVehicleAccident" +id +")");
                            break;
                        default: break;
                    }
                    _c.assertGaf(orientL, Mt);
                    assertionEvent = String.valueOf(orientL);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                    return assertionEvent;
                }
        
    public String malfunction(CycAccess _c) {
            String assertionEvent = "";
            try {
                CycObject Mt = _c.getConstantByName("UniversalVocabularyMt");
                CycList issueEventType = _c.makeCycList("(#$issueEventType #$"+getIssue() +" #$TransportationDevice-VehicleMalfunction)");
                _c.assertGaf(issueEventType, Mt);
                
                
                CycConstant RoadVehicleMalfunction = _c.getConstantByName("RoadVehicleMalfunction");
                CycConstant AMZSVehicleMalfunction = _c.makeCycConstant("AMZSVehicleMalfunction"+baseService.getLastEntry().getId());
                _c.assertIsa(AMZSVehicleMalfunction, RoadVehicleMalfunction,Mt);
                
                CycService.Malfunction malf = CycService.Malfunction.valueOf(toEnumCase(baseService.getLastEntry().getParent2_malf()));

                switch(malf)
                {
                    case VZIG: 
                        assertionEvent = ignition(_c, Mt);
                        break;
                    case MOTOR:
                        assertionEvent = engine(_c, Mt);
                        break;
                    case PRENOS_MOCI:
                        assertionEvent = transmission(_c, Mt);
                        break;
                    case ELEKTRIKA: 
                        assertionEvent = electricity(_c, Mt);
                        break;
                    case PODVOZJE:
                        assertionEvent = chassis(_c, Mt);
                        break;
                    case PNEVMATIKE:
                        assertionEvent = tires(_c, Mt);
                        break;
                    case GORIVO:
                        assertionEvent = fuel(_c,Mt);
                        break;
                    case KLJUCAVNICA:
                        assertionEvent = lock(_c,Mt);
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
    
    public String ignition(CycAccess _c, CycObject Mt) {
                String assertionEvent = "";
                
                try {
                    CycList malfL = new CycList(); 

                    Ignition ignit = Ignition.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));

                    switch(ignit)
                    {
                        case VRTI: 
                            malfL = _c.makeCycList("(#$isa #$" +getEvent() +" #$AutoEngineTurnOver)");
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
                
                CycList engineMalf = _c.makeCycList("(#$isa #$" +getEvent() +" #$PerformanceDegradation-AutoEngine)");
                _c.assertGaf(engineMalf, Mt); 
                    
                switch(eng)
                {
                    case UGASNIL_MED_VOZNJO:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine #$AutoEngineStalls)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case IZGUBLJA_MOC_NE_VLECE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine #$AutoEngineLosingPower)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREGREVA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine #$AutoEngineOverheating)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case GLAVNI_JERMEN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$EngineBelt #$EngineBeltMalfunction)");
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
                        _c.assertGaf(malfL, Mt);
                        break;
                    case BEL_DIM:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-White #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case CRN_DIM:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-Black #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case LUC_ZA_OLJE_SVETI:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$OilLevelIndicatorLight #$Device-On)");
                            _c.assertGaf(malfL, Mt);
                        break;
                    case UTRIPA_RUMENA_ZA_MOTOR:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$CheckEngineIndicatorLight #$Device-On)");
                            _c.assertGaf(malfL, Mt);
                        break;
                    case ZAKUHAL:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$AutoEngineOverheated)");
                            _c.assertGaf(malfL, Mt);
                        break;
                    case BREZ_HLADILNE:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$CoolantTank #$CoolantTank-Empty)");
                            _c.assertGaf(malfL, Mt);
                        break;
                    case SVETI_RDECA_ZA_HLADILNO_TEKOCINO:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #LowCoolantIndicatorLight #$Device-On)");
                            _c.assertGaf(malfL, Mt);
                        break;                        
                    default: break;
                }
                assertionEvent = String.valueOf(malfL);

            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NullPointerException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, "You have to choose a value.", ex);
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
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$GasPedal #$GasPedalMalfunction)");
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

    public String transMalf(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            TransmissionMalf trans = TransmissionMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
            try{
                switch(trans)
                {
                    case NE_PRESTAVLJA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Gearbox #$DoesntShiftGears)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_V_PRESTAVI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Gearbox #$StuckInGear)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AVTOMATSKI_NE_DELA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Gearbox #$AutomaticDoesntShift)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZICA_SKLOPKE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$ClutchWire #$ClutchWireMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_OSTAL_NOTRI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Clutch #$ClutchPedal-StuckDown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_TRD:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Clutch #$ClutchPedal-Stiff)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PADEL_DOL:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$GasPedal #$GasPedal-StuckDown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case DELA_NE_GRE_V_OBRATE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$GasPedal #$GasPedal-DoesntAccelerate)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SE_NE_ODZIVA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$GasPedal #$GasPedal-DoesntRespond)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, "You have to choose a value.", ex);
            }
            
            return assertionEvent;
            }
    
    public String electricity(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            Electricity elec = Electricity.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
            try{
                switch(elec)
                {
                    case OSTAL_BREZ_MED_VOZNJO:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Automobile #$VehicleState-Drive)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_BREZ_NA_PARKIRISCU:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Automobile #$VehicleState-Parked)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case LUCI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomotiveLight #$AutomotiveLightMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    case BRISALCI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$RegularWindshieldWiper #$RegularWindshieldWiperMalfunction)");
                        assertionEvent = String.valueOf(malfL);
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL) + elecMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
    
    public String elecMalf(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            ElectricityMalf elecM = ElectricityMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
            try{
                switch(elecM)
                {
                    case ALTERNATOR:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$Alternator #$AlternatorMalfunction)");
                        break;
                    case AKUMULATOR:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileBattery #BatteryMalfunction)");
                        break;
                    case VAROVALKE:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoFuse #$AutoFuse-Blown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREDNJE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileHeadLight #$AutomobileHeadLightMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZADNJE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileTailLight #$TailLightMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case DOLGE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$HeadLight-HighBeam #$HighBeamMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case KRATKE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$HeadLight-LowBeam #$LowBeamMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case STOP_LUC:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileBrakeLight #$BrakeLightMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SMERNIKI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileTurnSignals #$TurnSignalsMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case MOTORCEK:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$WindshieldWiperMotor #$WindshieldWiperMotorMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREDNJA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Windshield #$AutomobileWindow-Broken)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZADNJA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$RearWindowWithDefroster #$AutomobileWindow-Broken)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case STRANSKA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$SideWindow #$AutomobileWindow-Broken)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case VOZNIKOVA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Window-DriverSide #$AutomobileWindow-WontClose)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SOVOZNIKOVA:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Window-PassengerSide #$AutomobileWindow-WontClose)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case STRANSKA_ZADAJ:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Window-SecondRow #$AutomobileWindow-WontClose)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
        
    public String chassis(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            Chassis chass = Chassis.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
            try{
                switch(chass)
                {
                    case KRMILNI_MEHANIZEM:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileSteeringSystem #$SteeringSystemMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case VZMETENJE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileSuspensionSystem #$SuspensionSystemMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZAVORE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleBrakeSystem #$BrakeSystemMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case HIDRAVLIKA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleHydraulics #$HydraulicsMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL) + chassMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
    
    public String chassMalf(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            ChassisMalf chass = ChassisMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
            try{
                switch(chass)
                {
                    case VOLAN_TRD:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$SteeringWheel #$SteeringWheelMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZGLOB:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$CVJoint #$CVJointMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case KONCNIK:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$TrackRodEnd #$TrackRodEndMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AMORTIZER:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$ShockAbsorber #$ShockAbsorberMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case VZMET:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutomobileSpring #$SpringMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ODPOVEDALE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleBrakeSystem #$VehicleBrakeFailure)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZABLOKIRANE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleBrakeSystem #$VehicleBrake-Blocked)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AVTO_NA_TLEH:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$RoadVehicle #$Vehicle-Low)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
    
    public String tires(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            CycList malfL1 = new CycList(); 
            String assertionEvent = "";
            
            Tires tires = Tires.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
            try{
                Mt = _c.getKnownConstantByName("UniversalVocabularyMt");
                switch(tires)
                {
                    case PRAZNA: 
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Tire #$FlatTire)");
                        malfL1 = _c.makeCycList("(#$numberOfObjectsInSit #$" +getEvent() +" #$FlatTire 1)");
                        _c.assertGaf(malfL, Mt);
                        _c.assertGaf(malfL1, Mt);
                        break;
                    case VEC_PRAZNIH:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Tire #$FlatTire)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREDRTA: 
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Tire #$PerforatedTire)");
                        malfL1 = _c.makeCycList("(#$numberOfObjectsInSit #$" +getEvent() +" #$PerforatedTire 1)");
                        _c.assertGaf(malfL, Mt);
                        _c.assertGaf(malfL1, Mt);
                        break;
                    case VEC_PREDRTIH:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$Tire #$PerforatedTire)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case BREZ_1:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$RoadVehicle #$VehicleMissingATire)");
                        malfL1 = _c.makeCycList("(#$numberOfItemsMissing #$" +getEvent() +" #$Tire 1)");
                        _c.assertGaf(malfL, Mt);   
                        _c.assertGaf(malfL1, Mt);
                        break;
                    case BREZ_VEC:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$RoadVehicle #$VehicleMissingATire)");
                        _c.assertGaf(malfL, Mt);   
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL) +" " +String.valueOf(malfL1) +" " +tiresMalf(_c, Mt);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }

    public String tiresMalf(CycAccess _c, CycObject Mt) {
        
            CycList malfL = new CycList(); 
            String assertionEvent = "";
            
            TiresMalf tires = TiresMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
            try{
                switch(tires)
                {
                    case IMA_REZERVO:
                        malfL = _c.makeCycList("(#$possessesTypeInSit #$" +getEvent() +" #$SpareTire)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case NIMA_REZERVE:
                        malfL = _c.makeCycList("(#$itemsMissing #$" +getEvent() +" #$SpareTire)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case NI_KLJUCA:
                        malfL = _c.makeCycList("(#$itemsMissing #$" +getEvent() +" #$WheelLockKey)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case NE_MORE_ODVITI_VIJAKA:
                        malfL = _c.makeCycList("(#$unableToUnscrew #$" +getEvent() +" #$WheelLockKey #$RoadVehicle)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case UKRADENA:
                        malfL = _c.makeCycList("(#$itemsStolen #$" +getEvent() +" (#$UniquePartFn (#$VehicleInvolvedInAMZSReportFn #$" +getIssue() +") #$Tire))");
                        _c.assertGaf(malfL, Mt);
                        break;
                    default: break;
                }
                assertionEvent = String.valueOf(malfL);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return assertionEvent;
            }
    
    public String fuel(CycAccess _c, CycObject Mt) {
                String assertionEvent = "";
                
                try {
                    CycList malfL = new CycList(); 
                    
                    Fuel fuel = Fuel.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
                    FuelMalf fuelM = FuelMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));
                                        
                    CycList fuelMalf = _c.makeCycList("(#$isa #$" +getEvent() +" #$FuelSystemMalfunction)");
                    _c.assertGaf(fuelMalf, Mt); 
                    
                    switch(fuel)
                    {
                        case BREZ: 
                            switch(fuelM)
                            {
                                case EURO_95:
                                    malfL = _c.makeCycList("(#$itemsMissing #$" +getEvent() +" #$GasolineFuel-95)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case EURO_100:
                                    malfL = _c.makeCycList("(#$itemsMissing #$" +getEvent() +" #$GasolineFuel-100)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case DIESEL:
                                    malfL = _c.makeCycList("(#$itemsMissing #$" +getEvent() +" #$DieselFuel)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                default: break; 
                            }
                            assertionEvent = String.valueOf(malfL);
                            break;
                            
                        case NAROBE_TOCIL: 
                            switch(fuelM)
                            {
                                case EURO_95:
                                    malfL = _c.makeCycList("(#$pumpingWrongFuel #$" +getEvent() +" #$DieselFuel #$GasolineFuel-95)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case EURO_100:
                                    malfL = _c.makeCycList("(#$pumpingWrongFuel #$" +getEvent() +" #$DieselFuel #$GasolineFuel-100)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case DIESEL:
                                    malfL = _c.makeCycList("(#$pumpingWrongFuel #$" +getEvent() +" #$GasolineFuel-95 #$DieselFuel)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                default: break;
                            }
                            assertionEvent = String.valueOf(malfL);
                            break;                            
                        case ZMRZNJENO: 
                            malfL = _c.makeCycList("(#$stateOfMatterInSit #$" +getEvent() +" #$DieselFuel #$DieselFuel-Frozen)");
                            _c.assertGaf(malfL, Mt);
                            assertionEvent = String.valueOf(malfL);
                            break;
                        case UMAZANO:
                            malfL = _c.makeCycList("(#$isa #$" +getEvent() +" #$ContaminationEvent)");
                            _c.assertGaf(malfL, Mt);
                            assertionEvent = String.valueOf(malfL);
                            switch(fuelM)
                            {
                                case EURO_95:
                                    malfL = _c.makeCycList("(#$objectContaminated #$" +getEvent() +" #$ThePrototypicalGasolineFuel-95)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case EURO_100:
                                    malfL = _c.makeCycList("(#$objectContaminated #$" +getEvent() +" #$ThePrototypicalGasolineFuel-100)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case DIESEL:
                                    malfL = _c.makeCycList("(#$objectContaminated #$" +getEvent() +" #$ThePrototypicalDieselFuel)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                default: break;                                     
                            }
                            assertionEvent = assertionEvent + String.valueOf(malfL);
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
    
    public String lock(CycAccess _c, CycObject Mt) {
                String assertionEvent = "";
                
                try {
                    CycList malfL = new CycList(); 
                    CycList malfL1 = new CycList(); 

                    Lock lock = Lock.valueOf(toEnumCase(baseService.getLastEntry().getParent_malf()));
                    LockMalf lockM = LockMalf.valueOf(toEnumCase(baseService.getLastEntry().getMalfunction()));

                    CycList lockMalf = _c.makeCycList("(#$isa #$" +getEvent() +" #$LockMalfunction)");
                    _c.assertGaf(lockMalf, Mt); 
                    
                    switch(lock)
                    {
                        case KLJUC_SE_NE_OBRNE: 
                            switch(lockM)
                            {
                                case V_KLJUCAVNICI_VZIGA:
                                    malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleIgnitionSwitch #$LockMalfunction-KeyWontTurn)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case V_VRATIH:
                                    malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoPowerDoorLock #$LockMalfunction-KeyWontTurn)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                default: break; 
                            }
                            assertionEvent = String.valueOf(malfL);
                            break;
                            
                        case KLJUC_ZLOMLJEN: 
                            switch(lockM)
                            {
                                case V_KLJUCAVNICI_VZIGA:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$VehicleIgnitionKey #$Torn)");
                                    malfL1 = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleIgnitionKey #$VehicleIgnitionSwitch)");
                                    _c.assertGaf(malfL, Mt); 
                                    _c.assertGaf(malfL1, Mt); 
                                    break;
                                case V_VRATIH:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$VehicleDoorKey #$Torn)");
                                    malfL1 = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleDoorKey #$AutoPowerDoorLock)");
                                    _c.assertGaf(malfL, Mt); 
                                    _c.assertGaf(malfL1, Mt); 
                                    break;
                                default: break;
                            }
                            assertionEvent = String.valueOf(malfL) + String.valueOf(malfL1);
                            break;                            
                        case SE_VRTI_V_PRAZNO: 
                            switch(lockM)
                            {
                                case V_KLJUCAVNICI_VZIGA:
                                    malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$VehicleIgnitionSwitch #$LockMalfunction-KeySpinning)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case V_VRATIH:
                                    malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +getEvent() +" #$AutoPowerDoorLock #$LockMalfunction-KeySpinning)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                default: break;
                            }
                            assertionEvent = String.valueOf(malfL);
                            break;
                        case ZAKLENJENI_KLJUCI_KARTICA:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$RoadVehicle #$Device-Locked)");
                            _c.assertGaf(malfL, Mt);
                            assertionEvent = String.valueOf(malfL);
                            switch(lockM)
                            {
                                case V_PRTLJAZNIKU:
                                    malfL = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleDoorKey #$TrunkOfCar)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case NA_SEDEZU:
                                    malfL = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleDoorKey #$TrunkOfCar)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case V_TORBICI_PREDALU:
                                    malfL = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleDoorKey #$VehicleFrontStorageBox)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;
                                case V_KLJUCAVNICI_VZIGA_MOTOR_TECE:
                                    malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +getEvent() +" #$AutoEngine #$Device-On)");
                                    malfL1 = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleIgnitionKey #$VehicleIgnitionSwitch)");
                                    _c.assertGaf(malfL, Mt); 
                                    _c.assertGaf(malfL1, Mt); 
                                    break;
                                case V_KLJUCAVNICI_VZIGA_MOTOR_NE_TECE:
                                    malfL = _c.makeCycList("(#$objectStuckInsideObject #$" +getEvent() +" #$VehicleIgnitionKey #$VehicleIgnitionSwitch)");
                                    _c.assertGaf(malfL, Mt); 
                                    break;                                    
                                default: break;                                     
                            }
                            assertionEvent = assertionEvent + String.valueOf(malfL);
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
