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
package org.eclipse.osee.ote.ui.define.test.manager;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.ote.define.TestRunStorageKey;
import org.eclipse.osee.ote.ui.define.panels.GrayableBranchSelectionComposite;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class TestRunArtifactOptions implements IAdvancedPageContribution {

   private GrayableBranchSelectionComposite panel;

   public Control createControl(Composite parent) {
      this.panel = new GrayableBranchSelectionComposite(parent, SWT.NONE);
      this.panel.setFeatureEnabled(true);
      return this.panel;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.IAdvancedPageOptionsWidget#load(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public void load(IPropertyStore propertyStore) {
      boolean widgetEnabled = propertyStore.getBoolean(TestRunStorageKey.STORAGE_ENABLED);
      String lastBranchSelected = propertyStore.get(TestRunStorageKey.SELECTED_BRANCH_ID);
      String[] branchIds = propertyStore.getArray(TestRunStorageKey.BRANCH_IDS);

      this.panel.restoreWidgetValues(widgetEnabled, branchIds, lastBranchSelected);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.IAdvancedPageOptionsWidget#save(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(TestRunStorageKey.STORAGE_ENABLED, this.panel.isBranchSelectEnabled());

      propertyStore.put(TestRunStorageKey.BRANCH_IDS, this.panel.getBranchIds());

      Branch branch = this.panel.getSelectedBranch();
      int branchId = -1;
      if (branch != null) {
         branchId = branch.getBranchId();
      }
      propertyStore.put(TestRunStorageKey.SELECTED_BRANCH_ID, branchId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#areSettingsValidForRun()
    */
   @Override
   public boolean areSettingsValidForRun() {
      boolean result = true;
      if (this.panel.isBranchSelectEnabled() != false) {
         result &= this.panel.getSelectedBranch() != null;
      }
      return result;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#getErrorMessage()
    */
   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (areSettingsValidForRun() != true) {
         builder.append("Artifact Upload: Please select a branch to upload test runs into.");
      }
      return builder.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#getPriority()
    */
   public int getPriority() {
      return 2;
   }
}
