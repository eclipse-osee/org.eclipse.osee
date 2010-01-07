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
package org.eclipse.osee.coverage.util.widget;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.util.dialog.CoverageMethodListDialog;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelCoverageMethodSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelCoverageMethodSelection.class.getSimpleName();
   Collection<CoverageOption> selectedCoverageMethods = new HashSet<CoverageOption>();
   CoverageMethodListDialog dialog = null;
   CoverageOptionManager coverageOptionManager;

   public XHyperlabelCoverageMethodSelection() {
      super("Coverage Methods", true, 80);
   }

   public XHyperlabelCoverageMethodSelection(String label, CoverageOptionManager coverageOptionManager) {
      super(label, true, 80);
      this.coverageOptionManager = coverageOptionManager;
   }

   public Collection<CoverageOption> getSelectedCoverageMethods() {
      return selectedCoverageMethods;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(selectedCoverageMethods, ", ");
   }

   public void setSelectedCoverageMethods(Collection<CoverageOption> selectedCoverageMethods) {
      this.selectedCoverageMethods = selectedCoverageMethods;
      notifyXModifiedListeners();
      refresh();
   }

   @Override
   public boolean handleClear() {
      selectedCoverageMethods.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         dialog = new CoverageMethodListDialog(coverageOptionManager.getEnabled(), selectedCoverageMethods);
         int result = dialog.open();
         if (result == 0) {
            selectedCoverageMethods.clear();
            for (Object obj : dialog.getSelected()) {
               selectedCoverageMethods.add((CoverageOption) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public CoverageOptionManager getCoverageOptionManager() {
      return coverageOptionManager;
   }

   public void setCoverageOptionManager(CoverageOptionManager coverageOptionManager) {
      this.coverageOptionManager = coverageOptionManager;
   }

}
