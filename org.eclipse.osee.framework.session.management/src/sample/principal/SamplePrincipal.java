/*
 * Created on Oct 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package sample.principal;

import java.security.Principal;

/**
 * <p>
 * This class implements the <code>Principal</code> interface and represents a Sample user.
 * <p>
 * Principals such as this <code>SamplePrincipal</code> may be associated with a particular <code>Subject</code> to
 * augment that <code>Subject</code> with an additional identity. Refer to the <code>Subject</code> class for more
 * information on how to achieve this. Authorization decisions can then be based upon the Principals associated with a
 * <code>Subject</code>.
 * 
 * @version 1.4, 01/11/00
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class SamplePrincipal implements Principal, java.io.Serializable {

   private static final long serialVersionUID = 2814367183630479808L;

   /**
    * @serial
    */
   private String name;

   /**
    * Create a SamplePrincipal with a Sample username.
    * <p>
    * 
    * @param name the Sample username for this user.
    * @exception NullPointerException if the <code>name</code> is <code>null</code>.
    */
   public SamplePrincipal(String name) {
      if (name == null) throw new NullPointerException("illegal null input");

      this.name = name;
   }

   /**
    * Return the Sample username for this <code>SamplePrincipal</code>.
    * <p>
    * 
    * @return the Sample username for this <code>SamplePrincipal</code>
    */
   public String getName() {
      return name;
   }

   /**
    * Return a string representation of this <code>SamplePrincipal</code>.
    * <p>
    * 
    * @return a string representation of this <code>SamplePrincipal</code>.
    */
   public String toString() {
      return ("SamplePrincipal:  " + name);
   }

   /**
    * Compares the specified Object with this <code>SamplePrincipal</code> for equality. Returns true if the given
    * object is also a <code>SamplePrincipal</code> and the two SamplePrincipals have the same username.
    * <p>
    * 
    * @param o Object to be compared for equality with this <code>SamplePrincipal</code>.
    * @return true if the specified Object is equal equal to this <code>SamplePrincipal</code>.
    */
   public boolean equals(Object o) {
      if (o == null) return false;

      if (this == o) return true;

      if (!(o instanceof SamplePrincipal)) return false;
      SamplePrincipal that = (SamplePrincipal) o;

      if (this.getName().equals(that.getName())) return true;
      return false;
   }

   /**
    * Return a hash code for this <code>SamplePrincipal</code>.
    * <p>
    * 
    * @return a hash code for this <code>SamplePrincipal</code>.
    */
   public int hashCode() {
      return name.hashCode();
   }
}
