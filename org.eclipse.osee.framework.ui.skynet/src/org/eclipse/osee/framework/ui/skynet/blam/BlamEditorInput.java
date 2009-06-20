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
package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class BlamEditorInput extends BaseArtifactEditorInput {

   public BlamEditorInput(BlamOperation blamOperation) throws OseeCoreException {
      this(BlamWorkflow.getOrCreateBlamWorkflow(blamOperation));
   }

   public BlamEditorInput(Artifact artifact) {
      super(artifact);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BlamEditorInput) {
         return super.equals(obj);
      }
      return false;
   }

   @Override
   public String getName() {
      if (getArtifact() == null) {
         return "No Artifact Input Provided";
      }
      return getArtifact().getDescriptiveName() + " BLAM";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput#getImage()
    */
   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.BLAM);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput#getImageDescriptor()
    */
   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BLAM);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (Artifact.class.equals(adapter) || BlamWorkflow.class.equals(adapter)) {
         return getArtifact();
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput#getArtifact()
    */
   @Override
   public BlamWorkflow getArtifact() {
      return (BlamWorkflow) super.getArtifact();
   }

}
