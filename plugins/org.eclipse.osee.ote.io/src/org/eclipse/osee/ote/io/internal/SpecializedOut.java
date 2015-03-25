package org.eclipse.osee.ote.io.internal;

import java.io.PrintStream;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SpecializedOut extends PrintStream {

   private SpecializedOutputStream specialOut;

   public SpecializedOut(SpecializedOutputStream out) {
      super(out);
      this.specialOut = out;
   }
   
   public void addListener(SystemOutputListener listener){
      specialOut.add(listener);
   }
   
   public void removeListener(SystemOutputListener listener){
      specialOut.remove(listener);
   }
}
