/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.disposition.model;

/**
 * @author Angel Avila
 */

public class DispoStrings {

   private DispoStrings() {
      //
   }

   // Messages
   public static final String Program_NoneFound = "There are currently no disposition sets available on this branch";
   public static final String Program_NotFound = "Dispositon Program was not found";

   public static final String Set_NoneFound = "There are currently no disposition sets available on this branch";
   public static final String Set_ConflictingNames = "Can't create sets with the same name";
   public static final String Set_ErrorCreating = "Could not create set";
   public static final String Set_EmptyNameOrPath = "The Set must have a name and import path";
   public static final String Set_NotFound = "Dispositon Set was not found";

   public static final String Item_EmptyName = "The Item must have a name";
   public static final String Item_ConflictingNames = "Can't create items with the same name";
   public static final String Item_NoneFound = "There are currently no disposition items available under this set";
   public static final String Item_NotFound = "Dispositonable Item was not found";

   public static final String Annotation_EmptyLocRef = "The Annotation must have a valid location reference";
   public static final String Annotation_NoneFound = "There are currently no annotations available under this item";
   public static final String Annotation_NotFound = "Annotation was not found";

   public static final String Item_Pass = "PASS";
   public static final String Item_Complete = "COMPLETE";
   public static final String Item_InComplete = "INCOMPLETE";

   public static final String Operation_Import = "Import";
   public static final String Operation_MassSendStatus = "MassSendDispoItemStatus";

   public static final String DeletedDiscrepancy = "Deleted Discrepancy";

   public static final String Dispo_Config_Art = "Dispo_Config";

   public static final String Test_Unit_Resolution = "Test_Script";
   public static final String Exception_Handling_Resolution = "Exception_Handling";

}
