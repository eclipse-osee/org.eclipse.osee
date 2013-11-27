package org.eclipse.osee.ote.rest.internal;

import java.io.File;
import java.util.Comparator;

public class TimeSort implements Comparator<File> {

   @Override
   public int compare(File o1, File o2) {
      long time1 = o1.lastModified();
      long time2 = o2.lastModified();
      if(time1 == time2){
         return 0;
      } else if ( time1 > time2) {
         return 1;
      } else {
         return -1;
      }
   }

}
