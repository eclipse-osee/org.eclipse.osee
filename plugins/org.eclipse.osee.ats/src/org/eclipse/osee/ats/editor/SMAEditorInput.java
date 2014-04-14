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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class SMAEditorInput extends BaseArtifactEditorInput implements IPersistableElement {

   private final boolean pend;
   private String guid;
   private String title;

   public SMAEditorInput(Artifact artifact) {
      this(artifact, false);
   }

   public SMAEditorInput(Artifact artifact, boolean pend) {
      super(artifact);
      this.pend = pend;
   }

   public SMAEditorInput(String guid, String title) {
      this(null);
      this.guid = guid;
      this.title = title;
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof SMAEditorInput) {
         String thisGuid = null;
         if (this.isReload()) {
            thisGuid = guid;
         } else {
            thisGuid = getArtifact().getGuid();
         }
         SMAEditorInput input = (SMAEditorInput) obj;
         String objGuid = null;
         if (input.isReload()) {
            objGuid = input.getGuid();
         } else {
            objGuid = input.getArtifact().getGuid();
         }
         result = thisGuid.equals(objGuid);
      }
      return result;
   }

   @Override
   public int hashCode() {
      if (isReload()) {
         return guid.hashCode();
      } else {
         return getArtifact().hashCode();
      }
   }

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
      WEEditorInputFactory.saveState(memento, this);
   }

   @Override
   public String getFactoryId() {
      return WEEditorInputFactory.ID;
   }

   public String getGuid() {
      return guid;
   }

   public String getTitle() {
      return title;
   }

   @Override
   public String getName() {
      String name = title;
      if (getArtifact() != null && !getArtifact().isDeleted()) {
         name = ((AbstractWorkflowArtifact) getArtifact()).getEditorTitle();
      }
      return name;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_WORKFLOW);
   }

}
