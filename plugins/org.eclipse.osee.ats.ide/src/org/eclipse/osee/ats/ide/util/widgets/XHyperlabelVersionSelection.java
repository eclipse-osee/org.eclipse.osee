/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * Single version selection dialog. No persist and no clear button. Must be implemented to provide needed inforamtion
 * and handle method.
 *
 * @author Donald G. Dunne
 */
public class XHyperlabelVersionSelection extends XHyperlinkLabelValueSelection {

   Version selectedVersion = null;
   Collection<IAtsVersion> selectableVersions;
   VersionListDialog dialog = null;
   protected AtsApi atsApi;

   public XHyperlabelVersionSelection() {
      this("Version");
   }

   public XHyperlabelVersionSelection(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   public Version getSelectedVersion() {
      return selectedVersion;
   }

   @Override
   public Object getData() {
      return getSelectedVersion();
   }

   @Override
   public String getCurrentValue() {
      if (selectedVersion == null) {
         return Widgets.NOT_SET;
      }
      return selectedVersion.getName();
   }

   public boolean handleClear() {
      selectedVersion = null;
      notifyXModifiedListeners();
      return true;
   }

   public void setSelectedVersion(Version selectedVersion) {
      this.selectedVersion = selectedVersion;
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleSelection() {
      try {
         if (selectableVersions == null) {
            dialog = new VersionListDialog("Select Version", "Select Version", getSelectableVersions());
         } else {
            dialog = new VersionListDialog("Select Version", "Select Version", selectableVersions);
         }
         dialog.setRemoveAllAllowed(false);
         int result = dialog.open();
         if (result == 0) {
            Version version = dialog.getSelectedFirst();
            this.selectedVersion = version;
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return Status.OK_STATUS;
   }

   public Collection<IAtsVersion> getSelectableVersions() {
      return selectableVersions;
   }

   public void setSelectableVersions(Collection<IAtsVersion> versions) {
      this.selectableVersions = versions;
   }

   @Override
   public boolean isEmpty() {
      return selectedVersion == null;
   }

   public void setEnableHyperLink() {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.setEnabled(true);
      }
   }

   public void setDisableHyperLink() {
      if (Widgets.isAccessible(labelHyperlink)) {
         labelHyperlink.setEnabled(false);
      }
   }

}
