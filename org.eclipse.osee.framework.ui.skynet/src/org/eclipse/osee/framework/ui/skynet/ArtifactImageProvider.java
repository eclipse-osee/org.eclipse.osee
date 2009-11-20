/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * This class allows plugins to provide the base images for artifact types by registering via ImageManger.registerImage.
 * It also provides the ability for programatic override of image creation by
 * ImageManager.registerImageOverrideProvider. Registering to be override provider will cause the appropriate setupImage
 * calls to be executed when the image is needed. All overlays and base images are then provided out of this provider.
 * 
 * @author Ryan D. Brooks
 */
public abstract class ArtifactImageProvider {
   /**
    * Providers can return null which will cause null to be returned from the associated getImage or getImageDescriptor
    * call. Alternatively, providers that wish to defer to the basic implementation should call return
    * super.setupImage()
    * 
    * @param artifact
    * @throws OseeCoreException
    */
   public String setupImage(Artifact artifact) throws OseeCoreException {
      return ImageManager.setupImageNoProviders(artifact);
   }

   public String setupImage(ArtifactType artifactType) throws OseeCoreException {
      return ImageManager.setupImage(BaseImage.getBaseImageEnum(artifactType));
   }

   /**
    * Provide image artifact type registration by ImageManager.register.* calls
    * 
    * @throws OseeCoreException
    */
   public abstract void init() throws OseeCoreException;

}