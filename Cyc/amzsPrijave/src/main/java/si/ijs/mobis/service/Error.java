/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package si.ijs.mobis.service;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author Zala
 */

@Entity
@Table(name = "ERROR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Error.findAll", query = "SELECT p FROM Error p"),
    @NamedQuery(name = "Error.findById", query = "SELECT p FROM Error p WHERE p.id = :id"),
    @NamedQuery(name = "Error.findByMalfunction", query = "SELECT p FROM Error p WHERE p.malfunction = :malfunction"),
    @NamedQuery(name = "Error.findByParent_malf", query = "SELECT p FROM Error p WHERE p.parent_malf = :parent_malf"),
    @NamedQuery(name = "Error.findByParent2_malf", query = "SELECT p FROM Error p WHERE p.parent2_malf = :parent2_malf")})
    
public class Error implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;
    @Size(max = 20)
    @Column(name = "malfunction", nullable = false)
    private String malfunction;
    @Size(max = 20)
    @Column(name = "parent_malf")
    private String parent_malf;
    @Column(name = "parent2_malf")
    private String parent2_malf;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if (!(object instanceof Error)) {
            return false;
        }
        Error other = (Error) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.amzsprijave.Error[ id=" + id + " ]";
    }
    
    
}
