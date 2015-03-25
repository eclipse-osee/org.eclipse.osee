package org.eclipse.osee.ote.io;

public interface SystemOutput {
   public void addListener(SystemOutputListener listener);   
   public void removeListener(SystemOutputListener listener);   
   public void write(String input);
}
