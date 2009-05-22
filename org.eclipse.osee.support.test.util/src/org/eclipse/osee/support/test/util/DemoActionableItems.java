/*
 * Created on May 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.support.test.util;

/**
 * @author Donald G. Dunne
 */
public enum DemoActionableItems {

   Actionable_Items,
   SAW_CSCI,
   SAW_Code,
   Test_Page,
   SAW_Test,
   RunLists,
   SAW_Requirements,
   SAW_HW,
   Adapter,
   Manual,
   Screen,
   Case,
   SAW_SW_Design,
   CIS_CSCI,
   CIS_Code,
   CIS_Test,
   CIS_Requirements,
   CIS_SW_Design,
   Tools,
   Website,
   Reader,
   Timesheet,
   Results_Reporter,
   Processes,
   Coding_Standards,
   Config_Mgmt,
   Reviews,
   New_Employee_Manual,
   Facilities,
   Network,
   Vending_Machines,
   Computers,
   Break_Room,
   Backups;

   public String getName() {
      return name().replaceAll("_", " ");
   }
}
