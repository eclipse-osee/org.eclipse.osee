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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorInput implements IEditorInput {
   private Artifact artifact;

   public ArtifactEditorInput(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public boolean exists() {
      return true;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EDITOR);
   }

   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.ARTIFACT_EDITOR);
   }

   @Override
   public String getName() {
      if (artifact == null) {
         return "No Artifact Input Provided";
      }
      //      return String.format("%s%s", artifact.getVersionedName(), artifact.isReadOnly() ? " (Read-Only)" : "");
      return artifact.getVersionedName();
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return getName();
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type != null && type.isAssignableFrom(Artifact.class)) {
         return (T) getArtifact();
      }
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public boolean isReadOnly() {
      return artifact == null || artifact.isReadOnly();
   }

   public void setArtifact(Artifact art) {
      this.artifact = art;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (artifact == null ? 0 : artifact.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      boolean equals = false;
      if (obj instanceof ArtifactEditorInput) {
         ArtifactEditorInput otherEdInput = (ArtifactEditorInput) obj;
         equals = artifact.equals(otherEdInput.artifact);
      }
      return equals;
   }

}
