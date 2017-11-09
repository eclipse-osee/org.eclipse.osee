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
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class WfeInput extends ArtifactEditorInput {

   public WfeInput(Artifact artifact) {
      super(artifact);
   }

   public WfeInput(BranchId branch, ArtifactId artId, String title) {
      super(branch, artId, title);
   }

   @Override
   public boolean isReload() {
      return getArtifact() == null;
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

   @Override
   public String getName() {
      String name = getSavedTitle();
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
      return AtsClientService.get().getAgileService().isBacklog(getArtifact());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      if (AtsClientService.get().getAgileService().isBacklog(getArtifact())) {
         return ImageManager.getImageDescriptor(AtsImage.AGILE_BACKLOG);
      }
      return ImageManager.getImageDescriptor(AtsImage.TEAM_WORKFLOW);
   }

   @Override
   public boolean equals(Object obj) {
      boolean equals = super.equals(obj);
      if (equals && getClass() != obj.getClass()) {
         equals = false;
      }
      return equals;
   }
}