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
package org.eclipse.osee.ats.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.agile.AgileUtilClient;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class WfeInput extends ArtifactEditorInput implements IPersistableElement {

   private final boolean pend;
   private int artUuid;
   private String title;
   private BranchId branch;

   public WfeInput(Artifact artifact) {
      this(artifact, false);
   }

   public WfeInput(Artifact artifact, boolean pend) {
      super(artifact);
      this.pend = pend;
   }

   public WfeInput(BranchId branch, int artUuid, String title) {
      this(null);
      this.branch = branch;
      this.artUuid = artUuid;
      this.title = title;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + artUuid;
      result = prime * result + (int) (branch.getId() ^ branch.getId() >>> 32);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      WfeInput other = (WfeInput) obj;
      if (artUuid != other.artUuid) {
         return false;
      }
      if (!branch.equals(other.branch)) {
         return false;
      }
      return true;
   }

   @Override
   public boolean isReload() {
      return getArtifact() == null;
   }

   public boolean isPend() {
      return pend;
   }

   @Override
   public IPersistableElement getPersistable() {
      return this;
   }

   @Override
   public void saveState(IMemento memento) {
      WfeInputFactory.saveState(memento, this);
   }

   @Override
   public String getFactoryId() {
      return WfeInputFactory.ID;
   }

   public int getArtUuid() {
      return artUuid;
   }

   public BranchId getBranchId() {
      return branch;
   }

   public String getTitle() {
      return title;
   }

   @Override
   public String getName() {
      String name = title;
      if (getArtifact() != null && !getArtifact().isDeleted()) {
         if (isBacklog()) {
            name = "Backlog: " + getArtifact().getName();
         } else {
            name = ((AbstractWorkflowArtifact) getArtifact()).getEditorTitle();
         }
      }
      return name;
   }

   boolean isBacklog() {
      return AgileUtilClient.isBacklog(getArtifact());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      if (AgileUtilClient.isBacklog(getArtifact())) {
         return ImageManager.getImageDescriptor(AtsImage.AGILE_BACKLOG);
      }
      return ImageManager.getImageDescriptor(AtsImage.TEAM_WORKFLOW);
   }

}
