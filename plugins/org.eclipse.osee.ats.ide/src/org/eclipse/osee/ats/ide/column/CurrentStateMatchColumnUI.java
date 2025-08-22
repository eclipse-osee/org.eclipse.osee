/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.IXViewerDynamicColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class CurrentStateMatchColumnUI extends XViewerAtsCoreCodeXColumn implements IXViewerDynamicColumn {

   public String matchStr = "";
   public Boolean changed = false;
   public static CurrentStateMatchColumnUI instance = new CurrentStateMatchColumnUI();

   public static CurrentStateMatchColumnUI getInstance() {
      return instance;
   }

   private CurrentStateMatchColumnUI() {
      super(AtsColumnTokensDefault.CurrentStateMatchColumn, AtsApiService.get());
      setDescription("Dynamic column - Alt-Left-Click to set");
   }

   @Override
   public boolean performUI() {
      changed = false;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            matchStr = "";
            EntryDialog entry = new EntryDialog(getDisplayName(), "Enter State Match String");
            if (entry.open() == Window.OK) {
               matchStr = entry.getEntry();
               changed = true;
            }
         }
      }, true);
      return changed;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      CurrentStateMatchColumnUI newXCol = new CurrentStateMatchColumnUI();
      super.copy(this, newXCol);
      newXCol.setMatchStr(getMatchStr());
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (Strings.isInvalid(matchStr)) {
         return "Alt-Left-Click to Set";
      }
      if (element instanceof IAtsWorkItem) {
         return ((IAtsWorkItem) element).getCurrentStateName().contains(matchStr) ? "Match" : "No-Match";
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public String getMatchStr() {
      return matchStr;
   }

   public void setMatchStr(String matchStr) {
      this.matchStr = matchStr;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      boolean changed = performUI();
      return changed;
   }

   @Override
   public boolean refreshColumnOnChange() {
      return true;
   }

}
