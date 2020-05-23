/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryEntryCheckDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class WasIsCompareEditorWithReplaceAction extends WasIsCompareEditorAction {

   private static final String WAS_IS_SEARCH_PROPERTY = "history.was.is.search.regex";
   private static final String WAS_IS_REPLACE_PROPERTY = "history.was.is.replace.regex";
   private String currentSearchStr = "", currentReplaceStr = "";

   public WasIsCompareEditorWithReplaceAction() {
      super("View Was/Is Comparison With Replace");
   }

   @Override
   public void run() {
      try {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               String searchStr = UserManager.getSetting(WAS_IS_SEARCH_PROPERTY);
               String replaceStr = UserManager.getSetting(WAS_IS_REPLACE_PROPERTY);
               EntryEntryCheckDialog dialog = new EntryEntryCheckDialog("Set Was/Is Search/Replace Strings",
                  "Run comparison with search/replace below applied to both values.\n\n", "Search RegEx",
                  "Replace String (leave blank for newline)", "Save Search/Replace as Default");
               dialog.setEntry(searchStr);
               dialog.setEntry2(replaceStr);
               if (dialog.open() == Window.OK) {
                  boolean save = dialog.isChecked();
                  currentSearchStr = dialog.getEntry();
                  if (save && !currentSearchStr.equals(searchStr)) {
                     UserManager.setSetting(WAS_IS_SEARCH_PROPERTY, currentSearchStr);
                  }
                  currentReplaceStr = dialog.getEntry2();
                  if (save && !currentReplaceStr.equals(replaceStr)) {
                     UserManager.setSetting(WAS_IS_REPLACE_PROPERTY, currentReplaceStr);
                  }
               }
            }
         }, true);
         super.run();
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected String performanStringManipulation(String str) {
      if (Strings.isValid(currentSearchStr)) {
         str = str.replaceAll(currentSearchStr, Strings.isValid(currentReplaceStr) ? currentReplaceStr : "\n");
      }
      return str;
   }
}
