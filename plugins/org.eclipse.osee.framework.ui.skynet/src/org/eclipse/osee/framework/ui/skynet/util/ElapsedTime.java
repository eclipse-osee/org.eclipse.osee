/*
 * Created on Nov 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Date;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class ElapsedTime {

   Date startDate;
   Date endDate;
   private String name;

   public ElapsedTime(String name) {
      start(name);
   }

   public void start(String name) {
      this.name = name;
      startDate = new Date();
      System.out.println("\n" + name + " - start " + XDate.getTimeStamp());
   }

   public void end() {
      endDate = new Date();
      long diff = endDate.getTime() - startDate.getTime();
      String str =
            String.format("%s - elapsed %d sec - start %s - end %s", name, (diff / 1000), XDate.getDateStr(startDate,
                  XDate.HHMMSSSS), XDate.getDateStr(endDate, XDate.HHMMSSSS));
      System.out.println(str);
   }
}
