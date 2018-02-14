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
package org.eclipse.osee.framework.ui.skynet.compare;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.action.WasIsCompareEditorAction;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Open external compare editor, if set in preferences. Open internal compare editor if external fails or preference is
 * not set.
 *
 * @author Jeff C. Phillips
 */
public class CompareHandler {
   private final CompareItem leftCompareItem;
   private final CompareItem rightCompareItem;
   private final CompareItem parentCompareItem;
   private String title;

   /**
    * The left string is the 'Was' content and the right is the 'Is" content
    */
   public CompareHandler(String left, String right) {
      this(null, new CompareItem("Was", left, System.currentTimeMillis(), null),
         new CompareItem("Is", right, System.currentTimeMillis(), null), null);
   }

   public CompareHandler(String title, CompareItem leftCompareItem, CompareItem rightCompareItem, CompareItem parentCompareItem) {
      this.title = title;
      this.leftCompareItem = leftCompareItem;
      this.rightCompareItem = rightCompareItem;
      this.parentCompareItem = parentCompareItem;
   }

   public CompareHandler(String title, String left, String right) {
      this(left, right);
      this.title = title;
   }

   public void compare() {

      boolean externalEditorSucceeded = false;
      if (EditorsPreferencePage.isUseExternalCompareEditorForText()) {
         String editor = EditorsPreferencePage.getExternalCompareEditorForText();
         if (Strings.isValid(editor)) {
            try {
               String diffFilename = getDiffFilename(leftCompareItem);
               File leftFile = OseeData.getFile(diffFilename);
               Lib.writeStringToFile(leftCompareItem.getStringContent(), leftFile);
               File rightFile = OseeData.getFile(getDiffFilename(rightCompareItem));
               Lib.writeStringToFile(rightCompareItem.getStringContent(), rightFile);
               String editorCmd = String.format(editor, leftFile.getAbsolutePath(), rightFile.getAbsolutePath());

               Runtime.getRuntime().exec(editorCmd);
               externalEditorSucceeded = true;
            } catch (Exception ex) {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "External Compare Editor Error", "Error opening compare editor.  See error log for details.");
               OseeLog.log(CompareHandler.class, Level.SEVERE, "Error opening compare editor.", ex);
            }
         } else {
            OseeLog.log(WasIsCompareEditorAction.class, Level.WARNING, "Configured External Compare Editor not set");
         }
      }

      if (!externalEditorSucceeded) {
         CompareConfiguration compareConfiguration = new CompareConfiguration();
         compareConfiguration.setLeftEditable(leftCompareItem.isEditable());
         compareConfiguration.setRightEditable(rightCompareItem.isEditable());

         CompareUI.openCompareEditorOnPage(
            new CompareInput(title, compareConfiguration, leftCompareItem, rightCompareItem, parentCompareItem),
            AWorkbench.getActivePage());
      }
   }

   private String getDiffFilename(CompareItem compareItem) {
      if (Strings.isValid(compareItem.getDiffFilename())) {
         return compareItem.getDiffFilename();
      }
      String postfix = "";
      if (compareItem.getName().toLowerCase().equals("was")) {
         postfix = "was";
      } else if (compareItem.getName().toLowerCase().equals("is")) {
         postfix = "is";
      }
      return String.format("compare_%d%s.txt", Lib.generateArtifactIdAsInt(), postfix);

   }

}
