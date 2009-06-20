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
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseArtifactEditorInput implements IEditorInput {
   private final Artifact artifact;

   public BaseArtifactEditorInput(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public boolean equals(Object obj) {
      boolean equals = false;
      if (obj instanceof BaseArtifactEditorInput) {
         BaseArtifactEditorInput otherEdInput = (BaseArtifactEditorInput) obj;
         equals = artifact == otherEdInput.artifact;
      }
      return equals;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(artifact);
   }

   public Image getImage() {
      return ImageManager.getImage(artifact);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   public String getName() {
      if (artifact == null) {
         return "No Artifact Input Provided";
      }
      return String.format("%s%s", artifact.getVersionedName(), artifact.isReadOnly() ? " (Read-Only)" : "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return getName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (Artifact.class.equals(adapter)) {
         return getArtifact();
      }
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public boolean isReadOnly() {
      return artifact == null || artifact.isReadOnly();
   }
}
