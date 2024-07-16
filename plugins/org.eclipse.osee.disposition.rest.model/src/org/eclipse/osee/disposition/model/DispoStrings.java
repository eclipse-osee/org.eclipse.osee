/*********************************************************************
 * Copyright (c) 2013 Boeing
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
   public static final String Item_Modify = "MODIFY";
   public static final String Item_Incomplete = "INCOMPLETE";
   public static final String Item_Analysis = "ANALYSIS-COMPLETE";

   public static final String Operation_Import = "Import";

   public static final String CLEAN_ANNOTATIONS = "Clean_Annotations";

   public static final String DeletedDiscrepancy = "Deleted Discrepancy";

   public static final String Dispo_Config_Art = "Dispo_Config";

   public static final String Test_Unit_Resolution = "Test_Script";
   public static final String Exception_Handling_Resolution = "Exception_Handling";

   public static final String ANALYSIS = "Analysis";

   public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
   public static final String BATCH_RERUN_LIST =
      XML_HEADER + "<TestManagerConfig>\n" + "\t<ScriptPageConfig>\n" + "\t\t<ServicesSettings/>\n";
   public static final String BATCH_RERUN_LIST_END = "\t</ScriptPageConfig>\n" + "</TestManagerConfig>";
   public static final String SCRIPT_ENTRY = "\t\t<ScriptEntry>\n";
   public static final String SCRIPT_ENTRY_END = "\t\t</ScriptEntry>\n";
   public static final String IS_RUNNABLE = "\t\t\t<IsRunnable>true</IsRunnable>\n";
   public static final String SCRIPT_NAME = "\t\t\t<Name>%s</Name>\n";
   public static final String SCRIPT_PATH = "\t\t\t<Path>%s</Path>\n";
   public static final String MODIFY = "Modify_";

   public static final String MODIFY_CODE = "Modify_Code";
   public static final String MODIFY_TEST = "Modify_Test";
   public static final String MODIFY_REQT = "Modify_Reqt";
   public static final String MODIFY_TOOL = "Modify_Tooling";
   public static final String MODIFY_WORK_PRODUCT = "Modify_Work_Product";

   public static final String CODE_COVERAGE = "codeCoverage";

   public static final String STATE_ALL = "All";
   public static final String STATE_NONE = "None";
   public static final String STATE_NO_CHANGE = "No Change";
   public static final String STATE_OK = "OK";
   public static final String STATE_WARNING = "Warnings";
   public static final String STATE_FAIL = "Failed";

}
