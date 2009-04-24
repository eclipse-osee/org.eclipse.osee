package org.eclipse.osee.framework.messaging;

import java.io.Serializable;

public class StringNamespace implements Namespace, Serializable {
   private static final long serialVersionUID = -8903438134102328929L;
   private String namespace;
   
   public StringNamespace(String namespace){
      this.namespace = namespace;
   }

   @Override
   public boolean equals(Object arg0) {
      if(arg0 instanceof StringNamespace){
         return namespace.equals(((StringNamespace)arg0).namespace);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return namespace.hashCode();
   }
}
