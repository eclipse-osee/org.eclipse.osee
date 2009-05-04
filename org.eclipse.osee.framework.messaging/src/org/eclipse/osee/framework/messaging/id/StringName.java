package org.eclipse.osee.framework.messaging.id;

import java.io.Serializable;

public class StringName implements Name, Serializable {
   private static final long serialVersionUID = -7215226960243262972L;
   private String name;
   
   public StringName(String name){
      this.name = name;
   }

   @Override
   public boolean equals(Object arg0) {
      if(arg0 instanceof StringName){
         return name.equals(((StringName)arg0).name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return name.hashCode();
   }
   
   
}
