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
package org.eclipse.osee.ats.hyper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;

public class ActionHyperItem extends HyperViewItem {

   private final IATSArtifact artifact;

   public ActionHyperItem(IATSArtifact artifact) {
      super(artifact.getName());
      this.artifact = artifact;
      setGuid(artifact.getGuid());
   }

   @Override
   public String toString() {
      return artifact.getArtifactTypeName() + " - " + getTitle();
   }

   public void handleDoubleClick(HyperViewItem hyperViewItem) {
      // provided for subclass implementation
   }

   public void calculateCurrent(Artifact currentArtifact) {
      setCurrent(currentArtifact.equals(getArtifact()));
      for (ActionHyperItem childHyperItem : getChildren()) {
         childHyperItem.calculateCurrent(currentArtifact);
      }
   }

   @SuppressWarnings("unused")
   @Override
   public Image getImage() throws OseeCoreException {
      if (artifact.isDeleted()) {
         return null;
      }
      return ArtifactImageManager.getImage((Artifact) artifact);
   }

   @Override
   public String getTitle() {
      if (artifact.isDeleted()) {
         return "Deleted";
      }
      try {
         if (artifact instanceof TeamWorkFlowArtifact) {
            return ((TeamWorkFlowArtifact) artifact).getEditorTitle();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return artifact.getName();
   }

   @Override
   public String getToolTip() {
      if (artifact.isDeleted()) {
         return "";
      }
      StringBuilder builder = new StringBuilder();
      builder.append("Name: " + getTitle());
      builder.append("\nType: " + artifact.getArtifactTypeName());
      if (artifact instanceof AbstractWorkflowArtifact) {
         builder.append("\nState: " + ((AbstractWorkflowArtifact) artifact).getStateMgr().getCurrentStateName());
         builder.append("\nAssignee: " + getAssignee());
         builder.append("\nVersion: " + getTargetedVersion());
      }
      return builder.toString();
   }

   public String getAssignee() {
      try {
         if (artifact instanceof AbstractWorkflowArtifact) {
            return Artifacts.toString("; ", ((AbstractWorkflowArtifact) artifact).getStateMgr().getAssignees());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   private String getTargetedVersion() {
      try {
         if (artifact instanceof TeamWorkFlowArtifact) {
            String str = ((TeamWorkFlowArtifact) artifact).getWorldViewTargetedVersionStr();
            return str.isEmpty() ? "" : str;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   @Override
   public Image getMarkImage() {
      if (artifact.isDeleted()) {
         return null;
      }
      try {
         if (artifact instanceof AbstractWorkflowArtifact) {
            return ((AbstractWorkflowArtifact) artifact).getAssigneeImage();
         } else if (artifact instanceof ActionArtifact) {
            return ((ActionArtifact) artifact).getAssigneeImage();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getMarkImage();
   }

   public List<ActionHyperItem> getChildren() {
      List<ActionHyperItem> children = new ArrayList<ActionHyperItem>();
      for (HyperViewItem item : getBottom()) {
         if (item instanceof ActionHyperItem) {
            children.add((ActionHyperItem) item);
         }
      }
      for (HyperViewItem item : getTop()) {
         if (item instanceof ActionHyperItem) {
            children.add((ActionHyperItem) item);
         }
      }
      for (HyperViewItem item : getRight()) {
         if (item instanceof ActionHyperItem) {
            children.add((ActionHyperItem) item);
         }
      }
      for (HyperViewItem item : getLeft()) {
         if (item instanceof ActionHyperItem) {
            children.add((ActionHyperItem) item);
         }
      }
      return children;
   }

   public Artifact getArtifact() {
      return (Artifact) artifact;
   }

}
