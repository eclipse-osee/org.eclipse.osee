/*
 * Created on Apr 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.id;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public abstract class StringId implements Serializable {

   private static final long serialVersionUID = 80655792810954088L;
   private Namespace namespace;
   private Name name;
   
   public StringId(Namespace namespace, Name name){
      this.namespace = namespace;
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if(obj instanceof StringId){
         return namespace.equals(((StringId)obj).namespace) && 
         name.equals(((StringId)obj).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 31 * hash + namespace.hashCode();
      hash = 31 * hash + name.hashCode();
      return hash;
   }

   public Name getName() {
      return name;
   }

   public Namespace getNamespace() {
      return namespace;
   }
}
