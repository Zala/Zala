
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
import si.ijs.mobis.presenters.ResponsePresenter;


@Stateless
public class CycService {
    
    private static final Logger LOGGER  = Logger.getLogger(CycService.class.getName());
    @Inject private BaseService baseService;
      
    
//    private CycAccess _c;
        
    enum StuckIn{OSTAL_V_BLATU, OSTAL_NA_KOLICKU, OSTAL_V_SNEGU, OSTAL_NA_PREVISU, OSTAL_NA_ZIDU_SKARPI, ZAKOPAL_V_BLATU, ZAKOPAL_V_SNEGU};
    enum Accident {NA_KOLESIH, NA_STREHI, NA_BOKU, IZVEN_CESTE_DO_20M, IZVEN_CESTE_NAD_20M, POD_CESTO, V_JARKU_OB_CESTI, NA_CESTI, 
            AVTO_SE_JE_PREVRNIL};
    enum Noises {ROPOTA, CUDNI_ZVOKI, CVILI};
    enum Ignition {STARTER, ZAGANJAC, VERGLA, OBRNE, NE_OBRNE, VRTI, NE_VRTI, NE_ZAVERGLA, NE_ZALAUFA, NE_ZAZENE, 
            NE_VZGE, VZIG, AKUMULATOR};
    enum Engine {MOTOR_ROPOTA, JERMEN, AVTO_NE_VLECE, HLADILNA_TEKOCINA, OLJNA_CRPALKA, MOTOR, BEL_DIM_IZ_MOTORJA, CRN_DIM_IZ_MOTORJA,
            UGASNIL_MED_VOZNJO, CRKNIL, IZGUBLJA_MOC, PREGREVA, ZAKUHAL, BREZ_HLADILNE, GLAVNI_JERMEN};
    enum Transmission {PRENOS_MOCI, MENJALNIK, MENJALNIK_NE_DELA, NE_GRE_V_PRESTAVO, NE_PRESTAVLJA, OSTAL_V_PRESTAVI, AVTOMATSKI_MENJALNIK_NE_DELA,
            ZICA_SKLOPKE, PEDAL_SKLOPKE_OSTAL_NOTRI, PEDAL_SKLOPKE_TRD, PEDAL_ZA_PLIN_PADEL_DOL, PEDAL_ZA_PLIN_SE_NE_ODZIVA,
            PEDAL_ZA_PLIN_DELA_NE_GRE_V_OBRATE, SKLOPKA_NE_DELA, PEDAL_ZA_PLIN_NE_DELA, GAS_NE_DELA, KARDAN};
    enum Electricity {ELEKTRIKA, ELEKTRIKA_ODPOVEDALA, RACUNALNIK_CRKNIL, ALTERNATOR, AKUMULATOR, VAROVALKE, OSTAL_BREZ_ELEKTRIKE_MED_VOZNJO,
            OSTAL_BREZ_ELEKTRIKE_NA_PARKIRISCU, SMERNIKI_NE_DELAJO, BRISALCI, MOTORCEK_PRI_BRISALCIH}; 
    enum Chassis {PODVOZJE, ZAVORNA_TEKOCINA_POD_NORMALNO, AVTO_NA_TLEH, KOLO_ZABLOKIRA, ZAVORE_ZABLOKIRALE, KRMILNI_MEHANIZEM, VOLAN_TRD, ZGLOB, 
            ROKA, KONCNIK, AMORTIZER, VZMETENJE, VZMET, ZAVORE, ZAVORE_ODPOVEDALE, HIDRAVLIKA};  
    enum Tires {CENTRIRAT_PNEVMATIKE, SPUSCENA_ZRACNICA, SPUSCENA_PNEVMATIKA, DVE_PRAZNI_PNEVMATIKI, DVE_PREDRTI_PNEVMATIKI,
            VEC_PRAZNIH_PNEVMATIK, VEC_PREDRTIH_PNEVMATIK, IMA_REZERVNO_PNEVMATIKO, NIMA_REZERVNEGA_KOLESA, ZVITA_FELTNA, ZRACNICA_POCASI_SPUSTILA, 
            ZRACNICA_SE_SPRAZNILA, PRAZNA_ZRACNICA, PRESEKANA_ZRACNICA, POCENA_GUMA, NI_KLJUCA_ZA_ZRACNICO, VARNOSTNI_VIJAK, 
            NE_MORE_DVITI_VIJAKA_NA_PNEVMATIKI, PREDRTA_ZRACNICA, UKRADENA_ZRACNICA, BREZ_ZRACNICE,  BREZ_VEC_ZRACNIC, VEC_UKRADENIH_PNEVMATIK};
    enum TiresMalf {IMA_REZERVO, NIMA_REZERVE, NI_KLJUCA, VARNOSTNI_VIJAK, NE_MORE_ODVITI_VIJAKA, UKRADENA, NI_UKRADENA, OSTALO};
    enum Fuel {BREZ_GORIVA, ZMANJKALO_GORIVA, NI_DOVODA_GORIVA, NAFTA_NAMESTO_BENCINA, BENCIN_NAMEST_DIESLA, NAPACNO_GORIVO, NAROBE_TOCIL, 
            SENZOR_ZA_BENCIN_NE_DELA, ZMRZNJENO_GORIVO, NAROBE_TANKAL, UMAZANO_GORIVO, DIESEL_NAMESTO_BENCINA, BREZ_EURO_95, BREZ_EURO_100,
            BREZ_DIESLA};
    enum Lock {ZAKLENIL_KLJUCE_V_AVTO, ZAKLENJENI_KLJUCI, ZAKLENJENA_KARTICA, KLJUCAVNICA_NE_PREPOZNA_KLJUCA, KLJUC_NE_ODPIRA, ODKLEPANJE,
            NE_MORE_ODKLENIT, ELEKTRONSKO_SE_NE_DA_ODKLENIT, KLJUC_NE_GRE_V_KLJUCAVNICO, NAVADNI_NITI_REZERVNI_KLJUC_NE_DELA, IZGUBLJENI_KLJUCI,
            KLJUC_SE_NE_OBRNE, KLJUC_ZLOMLJEN, KLJUC_SE_VRTI_V_PRAZNO, NE_MORE_OBRNIT_KLJUCA_V_KLJUCAVNICI_VZIGA, KLJUC_ZLOMLJEN_V_VRATIH,
            KLJUC_ZLOMLJEN_V_KLJUCAVNICI_VZIGA, KLJUC_SE_VRTI_V_PRAZNO_V_KLJUCAVNICI_VZIGA, KLJUC_SE_VRTI_V_PRAZNO_V_VRATIH};
    
    public Integer getId() {
                return baseService.getLastEntry().getId() + 1;
        }
    
    public Integer getCurrentId() {
                return baseService.getLastEntry().getId();
    }
    
    public String getIssue() {
                return "AMZSIssue"+ getId();
        }
    
    public String getCurrentIssue() {
                return "AMZSIssue"+getCurrentId();
    }
        
    public void rescuing(CycAccess _c, CycObject Mt) {
                    CycList loc = new CycList();
                    
                    try {
                        
                        long startTime = System.currentTimeMillis();
                        _c.assertGaf(_c.makeCycList("(#$nameString (#$StuckOrConfinedVehicleSituationFn #$" + getIssue() +") \"Stuck situation "+getId() +"\")"), _c.getConstantByName("EnglishMt"));
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Asserting nameString: {0}", duration);
                        
                        startTime = System.currentTimeMillis();
                        _c.assertGaf(_c.makeCycList("(#$issueTopic #$" +getIssue() + " (#$StuckOrConfinedVehicleSituationFn #$"+getIssue() +"))"), Mt);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Asserting issueTopic: {0}", duration);
                        
                        startTime = System.currentTimeMillis();
                        _c.assertGaf(_c.makeCycList("(#$vehicleAffected (#$StuckOrConfinedVehicleSituationFn #$"+getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +getId() +"))"), Mt);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Asserting vehicleAffected: {0}", duration);
                        
                            
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }
    
    public void stuckDetails(CycAccess _c, CycObject Mt, String keyword) {
                try {
                    long startTime = System.currentTimeMillis();

                    StuckIn s = StuckIn.valueOf(toEnumCase(keyword));
                    CycList loc = new CycList();

                    switch(s)
                    {
                        case OSTAL_V_BLATU: case ZAKOPAL_V_BLATU:
                            loc = _c.makeCycList("(#$objectFoundInLocationType (#$StuckOrConfinedVehicleSituationFn #$"+ getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$MuddyArea)");
                            _c.assertGaf(loc, Mt);
                            break;
                        case OSTAL_NA_KOLICKU: 
                            loc = _c.makeCycList("(#$objectFoundInLocationType (#$StuckOrConfinedVehicleSituationFn #$"+ getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Pole-Rod)");
                            _c.assertGaf(loc, Mt);
                            break;
                        case OSTAL_V_SNEGU: case ZAKOPAL_V_SNEGU:
                            loc = _c.makeCycList("(#$objectFoundInLocationType (#$StuckOrConfinedVehicleSituationFn #$"+ getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$SnowCoveredArea)");
                            _c.assertGaf(loc, Mt);
                            break;
                        case OSTAL_NA_PREVISU: 
                            loc = _c.makeCycList("(#$objectFoundInLocationType (#$StuckOrConfinedVehicleSituationFn #$"+ getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Cliff)");
                            _c.assertGaf(loc, Mt);
                            break;
                        case OSTAL_NA_ZIDU_SKARPI:
                            loc = _c.makeCycList("(#$objectFoundInLocationType (#$StuckOrConfinedVehicleSituationFn #$"+ getIssue() +") (#$VehicleInvolvedInAMZSReportFn #$"+getIssue() +") #$Wall-ExternalToStructure)");
                            _c.assertGaf(loc, Mt);
                            break;
                        default: break;                                
                    }

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Asserting STUCK DETAILS: {0}", duration);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void accident(CycAccess _c, CycObject Mt) {
                try {
                    
                    long startTime = System.currentTimeMillis();
                    CycConstant CycAccident = _c.makeCycConstant("AMZSVehicleAccident"+getId());
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Creating AMZSVehicleAccident: {0}", duration);
                
                    startTime = System.currentTimeMillis();
                    _c.assertGaf(_c.makeCycList("(#$nameString #$"+CycAccident +" \"Vehicle accident "+getId() +"\")"),Mt);
                    endTime = System.currentTimeMillis();
                    duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "asserting accident nameString: {0} ", duration);
                                        
                    startTime = System.currentTimeMillis();
                    _c.assertGaf(_c.makeCycList("(#$issueTopic #$" + getIssue() + " #$" + CycAccident +")"), Mt);
                    endTime = System.currentTimeMillis();
                    duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Asserting accident issueTopic: {0} ", duration);
                    
                    startTime = System.currentTimeMillis();
                    _c.assertGaf(_c.makeCycList("(#$vehicleAffected #$" +CycAccident +" (#$VehicleInvolvedInAMZSReportFn #$" +getIssue() +"))"), Mt);
                    endTime = System.currentTimeMillis();
                    duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Asserting accident vehicleAffected: {0} ", duration);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void accidentDetails(CycAccess _c, CycObject Mt, String keyword) {
                try {
                    CycConstant orientation;
                    CycList accL = new CycList(); 

                    Accident acc = Accident.valueOf(toEnumCase(keyword));
                    long startTime = System.currentTimeMillis();
                    
                    switch(acc)
                    {
                        case NA_KOLESIH: 
                            accL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$" +getIssue() +") #$TopSideUp #$AMZSVehicleAccident" +getId() +")");
                            _c.assertGaf(accL, Mt);
                            break;
                        case NA_STREHI:
                            accL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$AMZSIssue" +getId() +") #$UpsideDown #$AMZSVehicleAccident" +getId() +")");
                            _c.assertGaf(accL, Mt);
                            break;
                        case NA_BOKU:
                            accL = _c.makeCycList("(#$roadVehicleOrientationAfterAccident (#$VehicleInvolvedInAMZSReportFn "
                                + "#$AMZSIssue" +getId() +") #$LeftSideUp #$AMZSVehicleAccident" +getId() +")");
                            _c.assertGaf(accL, Mt);
                            break;
                        case V_JARKU_OB_CESTI: 
                            accL = _c.makeCycList("(#$objectFoundInLocationType #$" +baseService.getEvent(1) + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$Ditch)");
                            _c.assertGaf(accL, Mt);
                            break;
                        case NA_CESTI: 
                            accL = _c.makeCycList("(#$objectFoundInLocationType #$" +baseService.getEvent(1) + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$Roadway)");
                            _c.assertGaf(accL, Mt);
                            break;
                        case IZVEN_CESTE_DO_20M: 
                            accL = _c.makeCycList("(#$objectFoundInLocationType #$" +baseService.getEvent(1) + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$OffRoad)");
                            _c.assertGaf(accL, Mt);
                            break;
                        case IZVEN_CESTE_NAD_20M: 
                            accL = _c.makeCycList("(#$objectFoundInLocationType #$" +baseService.getEvent(1) + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$OffRoad)");
                            _c.assertGaf(accL, Mt);
                            break;
                        case POD_CESTO:
                            accL = _c.makeCycList("(#$objectFoundInLocationType #$" +baseService.getEvent(1) + " (#$VehicleInvolvedInAMZSReportFn #$" 
                                    +getIssue() +") #$UnderRoadway)");
                            _c.assertGaf(accL, Mt);
                            break;
                        case AVTO_SE_JE_PREVRNIL:
//                            kako je orientiran?
                            break;
                        default: break;
                    }
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Asserting ACCIDENT DETAILS: {0}", duration);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
    public void malfunction(CycAccess _c, CycObject Mt) {
            try {                
                
                CycConstant RoadVehicleMalfunction = _c.getConstantByName("RoadVehicleMalfunction");
                CycConstant AMZSVehicleMalfunction = _c.makeCycConstant("AMZSVehicleMalfunction" +getId());
                _c.assertIsa(AMZSVehicleMalfunction, RoadVehicleMalfunction,_c.getConstantByName("UniversalVocabularyMt"));
                _c.assertGaf(_c.makeCycList("(#$issueTopic #$" + getIssue() + " #$AMZSVehicleMalfunction" + getId() +")"), _c.getConstantByName("UniversalVocabularyMt"));
              
                _c.assertGaf(_c.makeCycList("(#$vehicleAffected #$"+AMZSVehicleMalfunction +" (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +getId() +"))"), Mt);
                               
                
//                _c.assertGaf(_c.makeCycList("(#$nameString #$"+AMZSVehicleMalfunction +" \"Vehicle malfunction of " + getIssue()
//                        +" " +getId() +"\")"), _c.getConstantByName("EnglishMt"));
                                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, "You have to choose a value!", ex);
            }
    }   
    
    public void noises(CycAccess _c, CycObject Mt) {
                try {
                    CycList malf = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$AMZSVehicleMalfunction"+getId()+" #$RoadVehicle #$Device-Rattling)");
                    _c.assertGaf(malf, Mt);
//                    where do rattling noisees come from?
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void stalls(CycAccess _c, CycObject Mt) {
                try {
                    CycList malf = _c.makeCycList("(#$malfunctionTypeAffectsSit #$AMZSVehicleMalfunction" +getId() +" #$AutoEngine #$AutoEngineStalls)");
                    _c.assertGaf(malf, Mt);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void indicatorLights(CycAccess _c, CycObject Mt) {
//                koliko luck? katere lucke? kaksne barve?
    }
    
    public void tugging(CycAccess _c, CycObject Mt) {
//                ravno tankal? narobe, umazano?
    }
    
    public void oil(CycAccess _c, CycObject Mt) {
                
    }
    
    public void alternator(CycAccess _c, CycObject Mt) {
                
    }
    
    public void engineType(CycAccess _c, CycObject Mt) {
//                diesel, bencin
    }
    
    public void ignition(CycAccess _c, CycObject Mt, String keyword) {                
                try {
                    CycList malf = _c.makeCycList("(#$malfunctionTypeAffectsSit #$AMZSVehicleMalfunction"+getId()+" #$RoadVehicle #$VehicleIgnitionMalfunction)");
                    _c.assertGaf(malf, Mt);
                    
                    Ignition ignition = Ignition.valueOf(toEnumCase(keyword));
                    
                    switch(ignition) {
                        case STARTER: case ZAGANJAC: 
                            break;
                        case VERGLA: case VRTI: case OBRNE:
                            _c.assertGaf(_c.makeCycList("(#$isa #$" +baseService.getEvent(1) +" #$AutoEngineTurnOver)"), Mt);
                            break;
                        case NE_VRTI: case NE_ZAVERGLA: case NE_OBRNE: case NE_ZALAUFA: case NE_ZAZENE:
                            _c.assertGaf(_c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Automobile #$EngineDoesntTurnOver)"), Mt);
                            break;
                        case AKUMULATOR:
                            break;   
                        case NE_VZGE: case VZIG:
                            break;   
                    }
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } 
        }
    
    public void engine(CycAccess _c, CycObject Mt, String keyword) {
            try {
                long startTime = System.currentTimeMillis();
    //                CycList engineMalf = _c.makeCycList("(#$isa #$" +getEvent() +" #$PerformanceDegradation-AutoEngine)");
    //                _c.assertGaf(engineMalf, Mt); 
                _c.assertGaf(_c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$RoadVehicle #$PerformanceDegradation-AutoEngine)"), Mt);   
                
                CycList malfL = new CycList();
                Engine eng = Engine.valueOf(toEnumCase(keyword));
                
                switch(eng) {
                    case MOTOR_ROPOTA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine #$Noise-Unusual)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case JERMEN: case GLAVNI_JERMEN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$EngineBelt #$EngineBeltMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AVTO_NE_VLECE: case IZGUBLJA_MOC:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine #$AutoEngineLosingPower)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case HLADILNA_TEKOCINA: case BREZ_HLADILNE:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$CoolantTank #$CoolantTank-Empty)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OLJNA_CRPALKA:
                        break;
                    case MOTOR:
                        break;
                    case BEL_DIM_IZ_MOTORJA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-White #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case CRN_DIM_IZ_MOTORJA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine (#$LevelOfSubstanceTypeInSysTypeFn "
                                + "#$Smoke-Black #$AutoEngine (#$PositiveAmountFn #$LevelOfSubstanceInSystem)))");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case UGASNIL_MED_VOZNJO: case CRKNIL:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine #$AutoEngineStalls)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PREGREVA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoEngine #$AutoEngineOverheating)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZAKUHAL:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$AutoEngine #$AutoEngineOverheated)");
                        _c.assertGaf(malfL, Mt);
                        break;
                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                LOGGER.log(Level.INFO, "Asserting ENGINE malfunction: {0}", duration);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void transmission(CycAccess _c, CycObject Mt, String keyword) {
            try {   
                long startTime = System.currentTimeMillis();
                
                _c.assertGaf(_c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$RoadVehicle #$VehicleTransmissionMalfunction)"), Mt);
                
                CycList malfL = new CycList();
                Transmission trans = Transmission.valueOf(toEnumCase(keyword));
                
                switch(trans) {
                    case MENJALNIK: case MENJALNIK_NE_DELA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Gearbox #$GearboxMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case NE_GRE_V_PRESTAVO: case NE_PRESTAVLJA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Gearbox #$DoesntShiftGears)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_V_PRESTAVI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Gearbox #$StuckInGear)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AVTOMATSKI_MENJALNIK_NE_DELA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Gearbox #$AutomaticDoesntShift)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case ZICA_SKLOPKE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$ClutchWire #$ClutchWireMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_OSTAL_NOTRI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Clutch #$ClutchPedal-StuckDown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_SKLOPKE_TRD:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Clutch #$ClutchPedal-Stiff)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN_NE_DELA: case GAS_NE_DELA: 
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$GasPedal #$GasPedalMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN_PADEL_DOL:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$GasPedal #$GasPedal-StuckDown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN_SE_NE_ODZIVA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$GasPedal #$GasPedal-DoesntRespond)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PEDAL_ZA_PLIN_DELA_NE_GRE_V_OBRATE:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$GasPedal #$GasPedal-DoesntAccelerate)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SKLOPKA_NE_DELA:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Clutch #$ClutchMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case KARDAN:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$PTOShaft #$PTOMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case PRENOS_MOCI:
                        break;
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                LOGGER.log(Level.INFO, "Asserting TRANSMISSION malfunction: {0}", duration);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void electricity(CycAccess _c, CycObject Mt, String keyword) { 
            try {
                long startTime = System.currentTimeMillis();
                
                _c.assertGaf(_c.makeCycList("(#$malfunctionTypeAffectsSit #$"+baseService.getEvent(1) +" #$RoadVehicle #$VehicleElectricityMalfunction)"), Mt);
                
                CycList malfL = new CycList();
                Electricity elec = Electricity.valueOf(toEnumCase(keyword));
                
                switch(elec) {
                    case ELEKTRIKA: case ELEKTRIKA_ODPOVEDALA: case RACUNALNIK_CRKNIL:
                        break;
                    case ALTERNATOR:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$Alternator #$AlternatorMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case AKUMULATOR:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutomobileBattery #$BatteryMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case VAROVALKE:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$AutoFuse #$AutoFuse-Blown)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_BREZ_ELEKTRIKE_MED_VOZNJO:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Automobile #$VehicleState-Drive)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case OSTAL_BREZ_ELEKTRIKE_NA_PARKIRISCU:
                        malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Automobile #$VehicleState-Parked)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case SMERNIKI_NE_DELAJO:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutomobileTurnSignals #$TurnSignalsMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case BRISALCI:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$RegularWindshieldWiper #$WindshieldWiperMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                    case MOTORCEK_PRI_BRISALCIH:
                        malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$WindshieldWiperMotor #$WindshieldWiperMotorMalfunction)");
                        _c.assertGaf(malfL, Mt);
                        break;
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                LOGGER.log(Level.INFO, "Asserting ELECTRICITY malfunction: {0}", duration);
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CycApiException ex) {
                Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void chassis(CycAccess _c, CycObject Mt, String keyword) {
                try {
                    long startTime = System.currentTimeMillis();

                    _c.assertGaf(_c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$RoadVehicle #$ChassisMalfunction)"), Mt);    
                    
                    Chassis chassis = Chassis.valueOf(toEnumCase(keyword));
                    CycList malfL = new CycList();
                    
                    switch(chassis) {
                        case ZAVORNA_TEKOCINA_POD_NORMALNO:
                            break;
                        case AVTO_NA_TLEH:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$RoadVehicle #$Vehicle-Low)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case KOLO_ZABLOKIRA:
                            break;
                        case ZAVORE_ZABLOKIRALE:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleBrakeSystem #$VehicleBrake-Blocked)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case KRMILNI_MEHANIZEM:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutomobileSteeringSystem #$SteeringSystemMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case VOLAN_TRD:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$SteeringWheel #$SteeringWheelMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case ZGLOB:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$CVJoint #$CVJointMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case ROKA:
                            break;
                        case KONCNIK:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$TrackRodEnd #$TrackRodEndMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case AMORTIZER:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$ShockAbsorber #$ShockAbsorberMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case VZMETENJE:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutomobileSuspensionSystem #$SuspensionSystemMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case VZMET:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutomobileSpring #$SpringMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case ZAVORE:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleBrakeSystem #$BrakeSystemMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case ZAVORE_ODPOVEDALE:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleBrakeSystem #$VehicleBrakeFailure)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case HIDRAVLIKA:
                            malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleHydraulics #$HydraulicsMalfunction)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case PODVOZJE:
                            break;     
                    }

                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;    
                    LOGGER.log(Level.INFO, "Asserting CHASSIS malfucntion: {0}", duration);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void tires(CycAccess _c, CycObject Mt, String keyword) {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    CycList malfL = new CycList(); 
                    CycList malfL1 = new CycList(); 

                    Tires tires = Tires.valueOf(toEnumCase(keyword));

                    switch(tires) {
                        case CENTRIRAT_PNEVMATIKE:
                            break;
                        case SPUSCENA_PNEVMATIKA: case SPUSCENA_ZRACNICA: case ZRACNICA_POCASI_SPUSTILA: case ZRACNICA_SE_SPRAZNILA: 
                        case PRAZNA_ZRACNICA:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Tire #$FlatTire)");
                            malfL1 = _c.makeCycList("(#$numberOfObjectsInSit #$" +baseService.getEvent(1) +" #$FlatTire 1)");
                            _c.assertGaf(malfL, Mt);
                            _c.assertGaf(malfL1, Mt);
                            break;
                        case DVE_PRAZNI_PNEVMATIKI: case VEC_PRAZNIH_PNEVMATIK: case VEC_UKRADENIH_PNEVMATIK: 
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Tire #$FlatTire)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case BREZ_VEC_ZRACNIC:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$RoadVehicle #$VehicleMissingATire)");
                            _c.assertGaf(malfL, Mt);   
                        case DVE_PREDRTI_PNEVMATIKI: case VEC_PREDRTIH_PNEVMATIK: 
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Tire #$PerforatedTire)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case IMA_REZERVNO_PNEVMATIKO:
                            malfL = _c.makeCycList("(#$possessesTypeInSit #$" +baseService.getEvent(1) +" #$SpareTire)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case NIMA_REZERVNEGA_KOLESA:
                            malfL = _c.makeCycList("(#$itemsMissing #$" +baseService.getEvent(1) +" #$SpareTire)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case ZVITA_FELTNA:
                            break;
                        case PRESEKANA_ZRACNICA: 
                            break;
                        case UKRADENA_ZRACNICA: 
                            malfL = _c.makeCycList("(#$itemsStolen #$" +baseService.getEvent(1) +" (#$UniquePartFn (#$VehicleInvolvedInAMZSReportFn #$" +getIssue() +") #$Tire))");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case BREZ_ZRACNICE:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$RoadVehicle #$VehicleMissingATire)");
                            malfL1 = _c.makeCycList("(#$numberOfItemsMissing #$" +baseService.getEvent(1) +" #$Tire 1)");
                            _c.assertGaf(malfL, Mt);   
                            _c.assertGaf(malfL1, Mt);
                            break;
                        case POCENA_GUMA: case PREDRTA_ZRACNICA:
                            malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$Tire #$PerforatedTire)");
                            malfL1 = _c.makeCycList("(#$numberOfObjectsInSit #$" +baseService.getEvent(1) +" #$PerforatedTire 1)");
                            _c.assertGaf(malfL, Mt);
                            _c.assertGaf(malfL1, Mt);
                            break;
                        case NI_KLJUCA_ZA_ZRACNICO:
                            malfL = _c.makeCycList("(#$itemsMissing #$" +baseService.getEvent(1) +" #$WheelLockKey)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case VARNOSTNI_VIJAK:
                            break;
                        case NE_MORE_DVITI_VIJAKA_NA_PNEVMATIKI:
                            malfL = _c.makeCycList("(#$unableToUnscrew #$" +baseService.getEvent(1) +" #$WheelLockKey #$RoadVehicle)");
                            _c.assertGaf(malfL, Mt);
                            break;
                    }
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;    
                    LOGGER.log(Level.INFO, "Asserting TIRES malfunction: {0}", duration);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    
    public void fuel(CycAccess _c, CycObject Mt, String keyword) {
                try {
                    long startTime = System.currentTimeMillis();

                    _c.assertGaf(_c.makeCycList("(#$isa #$" +baseService.getEvent(1) +" #$FuelSystemMalfunction)"), Mt);
                    
                    Fuel fuel = Fuel.valueOf(toEnumCase(keyword));
                    CycList malfL = new CycList();
                    
                    switch(fuel) {
                        case ZMANJKALO_GORIVA: case BREZ_GORIVA:
                            break;
                        case NI_DOVODA_GORIVA:
                            break;
                        case NAFTA_NAMESTO_BENCINA: case DIESEL_NAMESTO_BENCINA: 
                            malfL = _c.makeCycList("(#$pumpingWrongFuel #$" +baseService.getEvent(1) +" #$DieselFuel #$GasolineFuel-95)");
                            _c.assertGaf(malfL, Mt);                            
                            break;
                        case BENCIN_NAMEST_DIESLA:
                            malfL = _c.makeCycList("(#$pumpingWrongFuel #$" +baseService.getEvent(1) +" #$GasolineFuel-95 #$DieselFuel)");
                            _c.assertGaf(malfL, Mt); 
                            break;
                        case NAPACNO_GORIVO: case NAROBE_TANKAL: case NAROBE_TOCIL:  
                            break;
                        case SENZOR_ZA_BENCIN_NE_DELA:
                            break;
                        case ZMRZNJENO_GORIVO:
                            malfL = _c.makeCycList("(#$stateOfMatterInSit #$" +baseService.getEvent(1) +" #$DieselFuel #$DieselFuel-Frozen)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case UMAZANO_GORIVO:
//                            rethink that!!!
                            malfL = _c.makeCycList("(#$isa #$" +baseService.getEvent(1) +" #$ContaminationEvent)");
                            _c.assertGaf(malfL, Mt);
                            break;
                        case BREZ_EURO_95:
                            malfL = _c.makeCycList("(#$itemsMissing #$" +baseService.getEvent(1) +" #$GasolineFuel-95)");
                            _c.assertGaf(malfL, Mt); 
                            break;
                        case BREZ_EURO_100:
                            malfL = _c.makeCycList("(#$itemsMissing #$" +baseService.getEvent(1) +" #$GasolineFuel-100)");
                            _c.assertGaf(malfL, Mt); 
                            break;
                        case BREZ_DIESLA:
                            malfL = _c.makeCycList("(#$itemsMissing #$" +baseService.getEvent(1) +" #$DieselFuel)");
                            _c.assertGaf(malfL, Mt); 
                            break; 
                        
                    }


                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;    
                    LOGGER.log(Level.INFO, "Asserting FUEL malfunction: {0}", duration);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void keys(CycAccess _c, CycObject Mt, String keyword) {
                    try {
                        long startTime = System.currentTimeMillis();

                        _c.assertGaf(_c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$RoadVehicle #$LockMalfunction)"), Mt);

                        Lock lock = Lock.valueOf(toEnumCase(keyword));
                        CycList malfL = new CycList();
                        
                        switch(lock) {
                            case ZAKLENIL_KLJUCE_V_AVTO: case ZAKLENJENA_KARTICA: case ZAKLENJENI_KLJUCI:
                                malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$RoadVehicle #$Device-Locked)");
                                _c.assertGaf(malfL, Mt);
                                break;
//                                bi dodal se kje so zaklenjeni?
                            case IZGUBLJENI_KLJUCI:
                                break;
                            case KLJUCAVNICA_NE_PREPOZNA_KLJUCA:
                                break;
                            case NE_MORE_ODKLENIT: case KLJUC_NE_ODPIRA: case ODKLEPANJE:
                                malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoPowerDoorLock #$LockMalfunction-KeyWontTurn)");
                                _c.assertGaf(malfL, Mt); 
                                break;
                            case NE_MORE_OBRNIT_KLJUCA_V_KLJUCAVNICI_VZIGA:
                                malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleIgnitionSwitch #$LockMalfunction-KeyWontTurn)");
                                _c.assertGaf(malfL, Mt); 
                                break;
                            case ELEKTRONSKO_SE_NE_DA_ODKLENIT:
                                break;
                            case KLJUC_NE_GRE_V_KLJUCAVNICO:
                                break;
                            case NAVADNI_NITI_REZERVNI_KLJUC_NE_DELA:
                                break;
                            case KLJUC_ZLOMLJEN:
//                                kje?
                                malfL = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$VehicleKey #$Torn)");
                                _c.assertGaf(malfL, Mt);
                                break;
                            case KLJUC_ZLOMLJEN_V_KLJUCAVNICI_VZIGA:
                                _c.assertGaf(_c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$VehicleIgnitionKey #$Torn)"), Mt); 
                                _c.assertGaf(_c.makeCycList("(#$objectStuckInsideObject #$" +baseService.getEvent(1) +" #$VehicleIgnitionKey #$VehicleIgnitionSwitch)"), Mt); 
                                break;
                            case KLJUC_ZLOMLJEN_V_VRATIH:
                                _c.assertGaf(_c.makeCycList("(#$stateOfDeviceTypeInSituation #$" +baseService.getEvent(1) +" #$VehicleDoorKey #$Torn)"), Mt); 
                                _c.assertGaf(_c.makeCycList("(#$objectStuckInsideObject #$" +baseService.getEvent(1) +" #$VehicleDoorKey #$AutoPowerDoorLock)"), Mt); 
                                break;
                            case KLJUC_SE_VRTI_V_PRAZNO:
                                break;
                            case KLJUC_SE_VRTI_V_PRAZNO_V_KLJUCAVNICI_VZIGA:
                                malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$VehicleIgnitionSwitch #$LockMalfunction-KeySpinning)");
                                _c.assertGaf(malfL, Mt); 
                                break;
                            case KLJUC_SE_VRTI_V_PRAZNO_V_VRATIH:
//                                preveri, ce je ok!!
                                malfL = _c.makeCycList("(#$malfunctionTypeAffectsSit #$" +baseService.getEvent(1) +" #$AutoPowerDoorLock #$LockMalfunction-KeySpinning)");
                                _c.assertGaf(malfL, Mt); 
                                break;
                        }

                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Asserting LOCK malfunction: {0}", duration);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }

    }
    
//    public void batteryDead(CycAccess _c, CycObject Mt) {
//                try{
//                    CycList malf = _c.makeCycList("(#$malfunctionTypeAffectsSit #$AMZSVehicleMalfunction"+getId()+" #$AutomobileBattery #$BatteryMalfunction)");
//                    _c.assertGaf(malf, Mt);
//                    
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                }
//    }
//    
//    public void batteryWarning(CycAccess _c, CycObject Mt) {
//                try{
//                    CycList malf = _c.makeCycList("(#$stateOfDeviceTypeInSituation #$AMZSVehicleMalfunction"+getId()+" #$ChargingSystemIndicatorLight #$Device-On)");
//                    _c.assertGaf(malf, Mt);
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                }
//    }
//        
//    public void engineNoises(CycAccess _c, CycObject Mt) {
//                try {
//                    CycList malf = _c.makeCycList("(#$malfunctionTypeAffectsSit #$AMZSVehicleMalfunction"+getId()+" #$AutoEngine #$Noise-Unusual)");
//                    _c.assertGaf(malf, Mt);
//                    
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (CycApiException ex) {
//                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
//                }
//    }
        
    public void assertSender(CycAccess c, CycObject Mt, Integer id, String ime) {
                try {
                    CycList amzsServiceClient = c.makeCycList("(#$amzsServiceClient #$AMZSIssue"+id +" (#$AMZSUserFn \"" +id +"\"))");
                    c.assertGaf(amzsServiceClient, Mt);

                    if ("".equals(ime)) {
                         ime = "AMZS user";
                    }

                    CycList NameString2 = c.makeCycList("(#$nameString (#$AMZSUserFn \"" +id +"\") \"" +ime +"\")");
                    c.assertGaf(NameString2, c.getConstantByName("EnglishMt"));
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void assertMember(CycAccess c, CycObject Mt, Integer id, String clanska) {
                try {
                    CycList Member = c.makeCycList("(#$memberWithIDInIssue #$AMZSIssue"+id + " (#$AMZSUserFn \"" +id +"\") \""+clanska +"\")");
                    c.assertGaf(Member, Mt);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CycApiException ex) {
                    Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                }          
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
                        "  (#$preferredNameString ?X ?NAME))";
        
        worker = new DefaultInferenceWorkerSynch(query, 
                                            _c.makeELMt(CycAccess.inferencePSC),
                                            defaultP, _c, 500000);
        HashMap<String, CycConstant> mapBrandNames = new HashMap<String, CycConstant>();
        
        long startTime = System.currentTimeMillis();
//        long startTime = System.nanoTime();
        LOGGER.log(Level.INFO, "Calling cyc with query: {0}", query);
         
        rs = worker.executeQuery();
        while (rs.next())
        {
            CycConstant carBrand = rs.getConstant("?X");
            String carBrandName = rs.getString("?NAME");
            mapBrandNames.put(carBrandName, carBrand);
	}
        rs.close();
        long endTime = System.currentTimeMillis();
//        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        LOGGER.log(Level.INFO, "Call took: {0} miliseconds", duration);
//        LOGGER.log(Level.INFO, "Call took: {0}", new Date(duration).toString());
        
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

                    long startTime = System.currentTimeMillis();
                    LOGGER.log(Level.INFO, "Calling cyc with query: {0}", query);


                    rs = worker.executeQuery();
                    while (rs.next())
                    {
                        CycConstant carModel = rs.getConstant("?X");
                        String carModelName = rs.getString("?NAME");
                        mapModelNames.put(carModelName, carModel);
                    }
                    rs.close();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    LOGGER.log(Level.INFO, "Call took: {0}", duration);
                    }
                return mapModelNames;
    } 
    
    public HashMap exportFromCycCarTypeList(CycAccess _c) {
                    HashMap<Object, Object> mapTy = new HashMap<Object, Object>();
                        
                    try {
                        //CycFort fort = _c.getKnownFortByName("AutomobileTypeByBodyStyle");
                           CycFort fort = _c.getKnownFortByName("RoadVehicleTypeByBodyStyle-AmzsPurposes");
                           CycList instances = _c.getAllInstances(fort);
                           CycObject English = _c.getConstantByName("EnglishMt");


                           for (Iterator it = instances.iterator(); it.hasNext();) {
                               Object constantTy = it.next();
                               CycFort nameStrTy = _c.getKnownFortByName(String.valueOf(constantTy));
                               CycList nameStr = _c.getNameStrings(nameStrTy, English);
                               if (!nameStr.isEmpty()){
                                   mapTy.put(nameStr.get(0), constantTy);
                               }
                           }

                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        return mapTy;
    }
    
    public void assertRegistration(CycAccess c, CycObject Mt, Integer id, String reg) {
                    try {
                            CycList Reg = c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"Vehicle with registration number " +reg +"\")");

                //                            sploh ni teba if-else, ker itak ne bo assertu, ce ne vpises
                //                            Reg = c.makeCycList("(#$nameString (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") \"Vehicle involved\")");

                            c.assertGaf(Reg, Mt);
                            
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }
    
    public void assertType(CycAccess c, CycObject Mt, Integer id, String type) {
                    try {
                            HashMap mapTy = exportFromCycCarTypeList(c);
                            String typeConst = String.valueOf(mapTy.get(type));
                            CycList Type = c.makeCycList("(#$roadVehicleBodyStyle (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+typeConst +")");
                            c.assertGaf(Type, Mt);
                            
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }
    
    public void assertModel(CycAccess c, CycObject Mt, Integer id, String brand, String inputBrand) {
                    try {
                            HashMap mapBr = exportFromCycCarBrandList(c); 
                            HashMap mapMod = getModelByBrand(c, inputBrand, mapBr);
                            String modelConst = String.valueOf(mapMod.get(brand));

                            CycList Brand = c.makeCycList("(#$isa (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+ mapBr.get(inputBrand) +")");
                            c.assertGaf(Brand, Mt);

                            CycList Model = c.makeCycList("(#$roadVehicleModel (#$VehicleInvolvedInAMZSReportFn #$AMZSIssue" +id +") #$"+modelConst +")");
                            c.assertGaf(Model, Mt);
                            
                    } catch (JSONException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, null, ex);
                    }
    }
    
     public void newIssue(CycAccess _c) {
//    creating a concept of new AMZSIssue when opening new application
                    try {
                        long startTime = System.currentTimeMillis();
                        
//                        Integer newIssueId = getId() + 1;
                        
                        CycConstant AMZSReport = _c.getConstantByName("AMZSReport");
                        CycConstant CycIssue = _c.makeCycConstant("AMZSIssue"+ getId());
                        _c.assertIsa(CycIssue, AMZSReport, _c.getConstantByName("BaseKB"));
                        CycList NameString = _c.makeCycList("(#$nameString #$AMZSIssue"+getId() +" \"Issue "+getId() +"\")");
                        _c.assertGaf(NameString, _c.getConstantByName("EnglishMt"));
                        
                        
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        LOGGER.log(Level.INFO, "Importing into Cyc ISSUE took: {0}", duration);

                    } catch (UnknownHostException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                    } catch (CycApiException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                    }
    }
    
//    public void newInconvenientEvent(CycAccess _c) {
//                    try {
//                        long startTime = System.currentTimeMillis();
//                        
////                        Integer newIssueId = getId() + 1;
//                        
//                        CycConstant AMZSReport = _c.getConstantByName("InconvenientTrafficEvent");
//                        CycConstant Event = _c.makeCycConstant("InconvenientTrafficEvent"+ getId());
//                        _c.assertIsa(Event, AMZSReport, _c.getConstantByName("BaseKB"));
//                        CycList NameString = _c.makeCycList("(#$nameString #$InconvenientTrafficEvent"+getId() +" \"Traffic event "+getId() +"\")");
//                        _c.assertGaf(NameString, _c.getConstantByName("EnglishMt"));
//                        
//                        
//                        long endTime = System.currentTimeMillis();
//                        long duration = endTime - startTime;
//                        LOGGER.log(Level.INFO, "Importing into Cyc Traffic EVENT took: {0}", duration);
//                    } catch (UnknownHostException ex) {
//                        LOGGER.log(Level.SEVERE, null, ex);
//                    } catch (IOException ex) {
//                            LOGGER.log(Level.SEVERE, null, ex);
//                    } catch (CycApiException ex) {
//                            LOGGER.log(Level.SEVERE, null, ex);
//                    }
//    }
    
    public static String toEnumCase(String s) {
                String string = s;
                try{
                        string = string.toUpperCase().replaceAll("\\s+", "_");    // zamenjaj enega ali vec preslednov z _
                        string = string.toUpperCase().replaceAll("/", "_");
                        string = string.toUpperCase().replaceAll("-", "_");

                } catch (NullPointerException ex) {
                        Logger.getLogger(CycService.class.getName()).log(Level.SEVERE, "You have to choose a value!", ex);
                }
                return string;
    }
    
    public static String toConstCase(String s) {
		String string = s;
		string = string.replaceAll("#\\$", ""); 
		return string;
	}
    }
