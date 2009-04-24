/*
 * Created on Apr 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.io.Serializable;

/**
 * @author b1528444
 *
 */
public class StringCommandId implements MessageId, Serializable {

   private static final long serialVersionUID = 2236967568467058971L;
   private Namespace namespace;
   private Name name;
   
   public StringCommandId(Namespace namespace, Name name){
      this.namespace = namespace;
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      if(obj instanceof StringCommandId){
         return namespace.equals(((StringCommandId)obj).namespace) && 
         name.equals(((StringCommandId)obj).name);
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
