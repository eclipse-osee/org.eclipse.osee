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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDecorator {

   private Action showArtIds;
   private Action showArtType;
   private Action showArtVersion;
   private Action showArtBranch;
   private ShowAttributeAction attributesAction;
   private StructuredViewer viewer;
   private final String preferenceKey;

   public ArtifactDecorator(StructuredViewer viewer, String preferenceKey) {
      this.viewer = viewer;
      this.preferenceKey = preferenceKey;
   }

   public ArtifactDecorator(String preferenceKey) {
      this.viewer = null;
      this.preferenceKey = preferenceKey;
   }

   public void setViewer(StructuredViewer viewer) {
      this.viewer = viewer;
      if (attributesAction != null) {
         attributesAction.setViewer(viewer);
      }
   }

   private void checkActionsCreated(IBranchProvider branchProvider) {
      if (showArtType == null) {
         showArtType = new Action("Show Artifact Type") {
            @Override
            public void run() {
               setChecked(!isChecked());
               updateShowArtTypeText();
               viewer.refresh();
            }
         };
         showArtType.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
      }
      if (showArtBranch == null) {
         showArtBranch = new Action("Show Artifact Branch") {
            @Override
            public void run() {
               setChecked(!isChecked());
               updateShowArtBranchText();
               viewer.refresh();
            }
         };
         showArtBranch.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
      }
      if (showArtVersion == null) {
         showArtVersion = new Action("Show Artifact Version") {
            @Override
            public void run() {
               setChecked(!isChecked());
               updateShowArtVersionText();
               viewer.refresh();
            }
         };
         showArtVersion.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
      }

      if (attributesAction == null && branchProvider != null) {
         attributesAction = new ShowAttributeAction(branchProvider, viewer, preferenceKey);
      }

      if (showArtIds == null && isAdmin()) {
         showArtIds = new Action("Show Artifact Ids") {
            @Override
            public void run() {
               setChecked(!isChecked());
               updateShowArtIdText();
               viewer.refresh();
            }
         };
         showArtIds.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
      }
   }

   private boolean isAdmin() {
      boolean result = false;
      try {
         if (AccessControlManager.isOseeAdmin()) {
            result = true;
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         result = false;
      }
      return result;
   }

   public void addActions(IMenuManager manager, IBranchProvider provider) {
      checkActionsCreated(provider);

      if (manager != null) {
         manager.add(showArtVersion);
         manager.add(showArtType);
         manager.add(showArtBranch);
      }
      updateShowArtTypeText();
      updateShowArtVersionText();
      updateShowArtBranchText();

      if (showArtIds != null && isAdmin()) {
         if (manager != null) {
            manager.add(showArtIds);
         }
         updateShowArtIdText();
      }
      if (manager != null) {
         manager.add(attributesAction);
      }
   }

   private void updateShowArtIdText() {
      showArtIds.setText((showArtIds.isChecked() ? "Hide" : "Show") + " Artifact Ids");
   }

   private void updateShowArtTypeText() {
      showArtType.setText((showArtType.isChecked() ? "Hide" : "Show") + " Artifact Type");
   }

   private void updateShowArtBranchText() {
      showArtBranch.setText((showArtBranch.isChecked() ? "Hide" : "Show") + " Artifact Branch");
   }

   private void updateShowArtVersionText() {
      showArtVersion.setText((showArtVersion.isChecked() ? "Hide" : "Show") + " Artifact Version");
   }

   public String getSelectedAttributeData(Artifact artifact) throws Exception {
      return attributesAction != null ? attributesAction.getSelectedAttributeData(artifact) : "";
   }

   public boolean showArtIds() {
      return showArtIds != null && showArtIds.isChecked();
   }

   public boolean showArtType() {
      return showArtType != null && showArtType.isChecked();
   }

   public boolean showArtBranch() {
      return showArtBranch != null && showArtBranch.isChecked();
   }

   public boolean showArtVersion() {
      return showArtVersion != null && showArtVersion.isChecked();
   }

   public void setShowArtType(boolean set) {
      if (showArtType != null) {
         showArtType.setChecked(set);
      }
   }

   public void setShowArtBranch(boolean set) {
      if (showArtBranch != null) {
         showArtBranch.setChecked(set);
      }
   }

   public void setShowArtIds(boolean set) {
      if (showArtIds != null) {
         showArtIds.setChecked(set);
      }
   }

   public void setShowArtVersion(boolean set) {
      if (showArtVersion != null) {
         showArtVersion.setChecked(set);
      }
   }
}
