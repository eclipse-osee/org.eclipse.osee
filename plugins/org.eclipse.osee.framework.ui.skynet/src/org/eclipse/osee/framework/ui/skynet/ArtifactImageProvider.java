/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.OseeUser;
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
    */
   public String setupImage(Artifact artifact) {
      return ArtifactImageManager.setupImageNoProviders(artifact);
   }

   public String setupImage(OseeUser user) {
      return ArtifactImageManager.setupImageNoProviders(user);
   }

   public String setupImage(ArtifactTypeToken artifactType) {
      return ArtifactImageManager.setupImage(BaseImage.getBaseImageEnum(artifactType));
   }

   /**
    * Provide image artifact type registration by ImageManager.register.* calls
    */
   public void init() {
      // do nothing
   }

}