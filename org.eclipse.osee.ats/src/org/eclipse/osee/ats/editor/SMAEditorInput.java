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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class SMAEditorInput implements IEditorInput {

   protected Artifact artifact;

   public SMAEditorInput(Artifact artifact) {
      this.artifact = artifact;
   }

   public boolean equals(Object obj) {
      boolean equals = false;
      if (obj instanceof SMAEditorInput) {
         SMAEditorInput otherEdInput = (SMAEditorInput) obj;

         equals = (artifact == otherEdInput.artifact);
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
      return null;
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
      return artifact.getVersionedName();
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
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @param artifact the artifact to set
    */
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

}
