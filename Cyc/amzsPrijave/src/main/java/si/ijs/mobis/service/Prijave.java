/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.ijs.mobis.service;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Zala
 */
@Entity
@Table(name = "PRIJAVE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prijave.findAll", query = "SELECT p FROM Prijave p"),
    @NamedQuery(name = "Prijave.findById", query = "SELECT p FROM Prijave p WHERE p.id = :id"),
    @NamedQuery(name = "Prijave.findByIme", query = "SELECT p FROM Prijave p WHERE p.ime = :ime"),
    @NamedQuery(name = "Prijave.findByPriimek", query = "SELECT p FROM Prijave p WHERE p.priimek = :priimek"),
    @NamedQuery(name = "Prijave.findByDatum", query = "SELECT p FROM Prijave p WHERE p.datum = :datum"),
    @NamedQuery(name = "Prijave.findByZnamka", query = "SELECT p FROM Prijave p WHERE p.znamka = :znamka"),
    @NamedQuery(name = "Prijave.findByTip", query = "SELECT p FROM Prijave p WHERE p.tip = :tip"),
    @NamedQuery(name = "Prijave.findByRegistrska", query = "SELECT p FROM Prijave p WHERE p.registrska = :registrska"),
    @NamedQuery(name = "Prijave.findByClanska_st", query = "SELECT p FROM Prijave p WHERE p.clanska_st = :clanska_st"),
    @NamedQuery(name = "Prijave.findByMalfunction", query = "SELECT p FROM Prijave p WHERE p.malfunction = :malfunction"),
    @NamedQuery(name = "Prijave.findByParent_malf", query = "SELECT p FROM Prijave p WHERE p.parent_malf = :arent_malf"),
    @NamedQuery(name = "Prijave.findByParent2_malf", query = "SELECT p FROM Prijave p WHERE p.parent2_malf = :parent2_malf"),
    @NamedQuery(name = "Prijave.findByClan", query = "SELECT p FROM Prijave p WHERE p.clan = :clan")})

public class Prijave implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;
    @Size(max = 20)
    @Column(name = "IME", nullable = false)
    private String ime;
    @Size(max = 20)
    @Column(name = "PRIIMEK")
    private String priimek;
    @Column(name = "DATUM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Size(max = 20)
    @Column(name = "ZNAMKA")
    private String znamka;
    @Size(max = 20)
    @Column(name = "TIP")
    private String tip;
    @Size(max = 10)
    @Column(name = "REGISTRSKA")
    private String registrska;
    @Size(max = 2)
    @Column(name = "CLAN")
    private String clan;
    @Column(name = "CLANSKA_ST")
    private String clanska_st;
    @Column(name = "MALFUNCTION")
    private String malfunction;
    @Column(name = "PARENT_MALF")
    private String parent_malf;
    @Column(name = "PARENT2_MALF")
    private String parent2_malf;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPriimek() {
        return priimek;
    }

    public void setPriimek(String priimek) {
        this.priimek = priimek;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getZnamka() {
        return znamka;
    }

    public void setZnamka(String znamka) {
        this.znamka = znamka;
    }
    
    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
    
    public String getRegistrska() {
        return registrska;
    }

    public void setRegistrska(String registrska) {
        this.registrska = registrska;
    }
    
    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }
    
    public String getClanska_st() {
        return clanska_st;
    }

    public void setClanska_st(String clanska_st) {
        this.clanska_st = clanska_st;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prijave)) {
            return false;
        }
        Prijave other = (Prijave) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.amzsprijave.Prijave[ id=" + id + " ]";
    }
    
}
