/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.demo;

/**
 * @author Donald G. Dunne
 */
public enum DemoProgramAttributes {

   Crew_Interface_Requirement,
   Timing_Critical,
   Obsolescence_date,
   Complexity_Factor,
   //
   Weight,
   Content_URL,
   Paragraph_Number,
   Legacy_Id,
   Level_2_IPT,
   Level_3_IPT,
   //
   Support_IPT,
   System_Security_Requirement,
   Training_Effectivity,
   CSCI,
   Subsystem,
   //
   Qualification_Method,
   Execution_Date,
   Test_Status,
   Test_Pass,
   Test_Fail,
   Version,
   //
   Repository_Type,
   Modification_Flag,
   Test_Log,
   Safety_Criticality;

   @Override
   public String toString() {
      return name().replace("_", " ");
   }
}
