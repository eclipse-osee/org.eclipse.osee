/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

/**
 * @author Donald G. Dunne
 */
public enum DemoDbAIs {
   Computers,
   Network,
   Config_Mgmt,
   Reviews,
   Timesheet,
   Website,
   Reader,
   CIS_Code,
   CIS_Test,
   CIS_Requirements,
   CIS_SW_Design,
   SAW_Code,
   SAW_Test,
   SAW_Requirements,
   SAW_SW_Design,
   Adapter;

   public String getAIName() {
      return name().replaceAll("_", " ");
   }
}
