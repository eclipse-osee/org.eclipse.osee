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
