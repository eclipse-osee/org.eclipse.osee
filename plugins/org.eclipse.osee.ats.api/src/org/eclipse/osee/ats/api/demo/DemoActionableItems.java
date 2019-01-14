/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo;

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
