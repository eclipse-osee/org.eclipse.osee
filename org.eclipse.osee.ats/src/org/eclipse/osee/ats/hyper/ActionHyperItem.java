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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.graphics.Image;

public class ActionHyperItem extends HyperViewItem {

   private final IHyperArtifact artifact;

   public ActionHyperItem(IHyperArtifact artifact) {
      super(artifact.getHyperName());
      this.artifact = artifact;
      setGuid(artifact.getGuid());
   }

   public Image getImage() {
      if (artifact.isDeleted()) return null;
      return artifact.getHyperImage();
   }

   @Override
   public String getTitle() {
      if (artifact.isDeleted()) return "Deleted";
      return artifact.getHyperName();
   }

   @Override
   public String getToolTip() {
      if (artifact.isDeleted()) return "";
      StringBuilder builder = new StringBuilder();
      builder.append("Type: " + ((IHyperArtifact) artifact).getHyperType());
      if (artifact instanceof IHyperArtifact) {
         builder.append("\nState: " + ((IHyperArtifact) artifact).getHyperState());
         builder.append("\nAssignee: " + ((IHyperArtifact) artifact).getHyperAssignee());
      }
      return builder.toString();
   }

   @Override
   public Image getMarkImage() {
      if (artifact.isDeleted()) return null;
      if (artifact instanceof IHyperArtifact) return ((IHyperArtifact) artifact).getHyperAssigneeImage();
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
      return artifact;
   }

   public Artifact getArtifact() {
      return artifact.getHyperArtifact();
   }

}
