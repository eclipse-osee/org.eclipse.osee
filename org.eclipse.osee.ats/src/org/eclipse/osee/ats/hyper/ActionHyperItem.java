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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.graphics.Image;

public class ActionHyperItem extends HyperViewItem {

   private final IHyperArtifact iHyperartifact;

   public ActionHyperItem(IHyperArtifact iHyperartifact) {
      super(iHyperartifact.getHyperName());
      this.iHyperartifact = iHyperartifact;
      setGuid(iHyperartifact.getGuid());
   }

   public String toString() {
      return iHyperartifact.getHyperType() + " - " + iHyperartifact.getHyperName();
   }

   public void handleDoubleClick(HyperViewItem hyperViewItem) {
   }

   public void calculateCurrent(Artifact currentArtifact) {
      setCurrent(currentArtifact.equals(getArtifact()));
      for (ActionHyperItem childHyperItem : getChildren()) {
         childHyperItem.calculateCurrent(currentArtifact);
      }
   }

   public Image getImage() {
      if (iHyperartifact.isDeleted()) return null;
      return iHyperartifact.getHyperImage();
   }

   @Override
   public String getTitle() {
      if (iHyperartifact.isDeleted()) return "Deleted";
      return iHyperartifact.getHyperName();
   }

   @Override
   public String getToolTip() {
      if (iHyperartifact.isDeleted()) return "";
      StringBuilder builder = new StringBuilder();
      builder.append("Type: " + ((IHyperArtifact) iHyperartifact).getHyperType());
      if (iHyperartifact instanceof IHyperArtifact) {
         if (((IHyperArtifact) iHyperartifact).getHyperState() != null) builder.append("\nState: " + ((IHyperArtifact) iHyperartifact).getHyperState());
         if (((IHyperArtifact) iHyperartifact).getHyperAssignee() != null) builder.append("\nAssignee: " + ((IHyperArtifact) iHyperartifact).getHyperAssignee());
      }
      return builder.toString();
   }

   @Override
   public Image getMarkImage() {
      if (iHyperartifact.isDeleted()) return null;
      try {
         if (iHyperartifact instanceof IHyperArtifact) return ((IHyperArtifact) iHyperartifact).getHyperAssigneeImage();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return super.getMarkImage();
   }

   public ArrayList<ActionHyperItem> getChildren() {
      ArrayList<ActionHyperItem> children = new ArrayList<ActionHyperItem>();
      for (HyperViewItem item : getBottom()) {
         children.add((ActionHyperItem) item);
      }
      for (HyperViewItem item : getTop()) {
         children.add((ActionHyperItem) item);
      }
      for (HyperViewItem item : getRight()) {
         children.add((ActionHyperItem) item);
      }
      for (HyperViewItem item : getLeft()) {
         children.add((ActionHyperItem) item);
      }
      return children;
   }

   public IHyperArtifact getHyperArtifact() {
      return iHyperartifact;
   }

   public Artifact getArtifact() {
      return iHyperartifact.getHyperArtifact();
   }

}
