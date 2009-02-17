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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDecorator {

   private Action showArtIds;
   private Action showArtType;
   private Action showArtVersion;
   private ShowAttributeAction attributesAction;
   private StructuredViewer viewer;
   private String preferenceKey;

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

   public void addActions(IMenuManager manager) {
      if (showArtType == null) {
         showArtType = new Action("Show Artifact Type") {
            @Override
            public void run() {
               setChecked(!isChecked());
               updateShowArtTypeText();
               viewer.refresh();
            }
         };
         showArtType.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
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
         showArtVersion.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      }

      updateShowArtVersionText();
      updateShowArtTypeText();

      manager.add(showArtVersion);
      manager.add(showArtType);

      try {
         if (AccessControlManager.isOseeAdmin()) {
            if (showArtIds == null) {
               showArtIds = new Action("Show Artifact Ids") {
                  @Override
                  public void run() {
                     setChecked(!isChecked());
                     updateShowArtIdText();
                     viewer.refresh();
                  }
               };
               showArtIds.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
            }
            updateShowArtIdText();
            manager.add(showArtIds);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      try {
         if (attributesAction == null) {
            attributesAction = new ShowAttributeAction(viewer, preferenceKey);
            attributesAction.setValidAttributeTypes(SkynetViews.loadAttrTypesFromPreferenceStore(preferenceKey,
                  BranchManager.getDefaultBranch()));
         }
         manager.add(attributesAction);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void updateShowArtIdText() {
      showArtIds.setText((showArtIds.isChecked() ? "Hide" : "Show") + " Artifact Ids");
   }

   private void updateShowArtTypeText() {
      showArtType.setText((showArtType.isChecked() ? "Hide" : "Show") + " Artifact Type");
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

   public boolean showArtVersion() {
      return showArtVersion != null && showArtVersion.isChecked();
   }
}
