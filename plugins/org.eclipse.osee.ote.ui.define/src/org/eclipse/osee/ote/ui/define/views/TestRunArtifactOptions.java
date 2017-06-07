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
package org.eclipse.osee.ote.ui.define.views;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TestRunStorageKey;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.ui.plugin.widgets.IPropertyStoreBasedControl;
import org.eclipse.osee.ote.ui.define.panels.GrayableBranchSelectionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class TestRunArtifactOptions implements IPropertyStoreBasedControl {

   private GrayableBranchSelectionComposite panel;

   @Override
   public Control createControl(Composite parent) {
      this.panel = new GrayableBranchSelectionComposite(parent, SWT.NONE);
      this.panel.setFeatureEnabled(true);
      return this.panel;
   }

   @Override
   public void load(IPropertyStore propertyStore) {
      boolean widgetEnabled = propertyStore.getBoolean(TestRunStorageKey.STORAGE_ENABLED);
      String lastBranchSelected = propertyStore.get(TestRunStorageKey.SELECTED_BRANCH_ID);
      String[] branchUuids = propertyStore.getArray(TestRunStorageKey.BRANCH_IDS);

      this.panel.restoreWidgetValues(widgetEnabled, branchUuids, lastBranchSelected);
   }

   @Override
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(TestRunStorageKey.STORAGE_ENABLED, this.panel.isBranchSelectEnabled());
      propertyStore.put(TestRunStorageKey.BRANCH_IDS, this.panel.getBranchIds());

      BranchId branch = this.panel.getSelectedBranch();
      propertyStore.put(TestRunStorageKey.SELECTED_BRANCH_ID, branch.getId());
   }

   @Override
   public boolean areSettingsValid() {
      boolean result = true;
      if (this.panel.isBranchSelectEnabled() != false) {
         result &= this.panel.getSelectedBranch().isValid();
      }
      return result;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (areSettingsValid() != true) {
         builder.append("Artifact Upload: Please select a branch to upload test runs into.");
      }
      return builder.toString();
   }

   @Override
   public int getPriority() {
      return 2;
   }
}
