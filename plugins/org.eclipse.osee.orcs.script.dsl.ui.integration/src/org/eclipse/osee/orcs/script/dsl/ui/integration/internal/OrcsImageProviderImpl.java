/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui.integration.internal;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsImageProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class OrcsImageProviderImpl implements IOrcsImageProvider {

   @Override
   public Image getBranchImage() {
      return ImageManager.getImage(FrameworkImage.BRANCH);
   }

   @Override
   public Image getTxImage() {
      return ImageManager.getImage(FrameworkImage.VERSION);
   }

   @Override
   public Image getAttributeImage() {
      return ImageManager.getImage(FrameworkImage.ATTRIBUTE_MOLECULE);
   }

   @Override
   public Image getRelationImage() {
      return ImageManager.getImage(FrameworkImage.RELATION);
   }

   @Override
   public Image getArtifactImage() {
      ArtifactTypeToken artType = CoreArtifactTypes.Artifact;
      return ArtifactImageManager.getImage(artType);
   }

   @Override
   public Image getArtifactTypeImage(Id type) {
      ArtifactTypeToken artType = (ArtifactTypeToken) type;
      return ArtifactImageManager.getImage(artType);
   }

   @Override
   public Image getAttributeTypeImage(Id type) {
      return getAttributeImage();
   }

   @Override
   public Image getRelationTypeImage(Id type) {
      return getRelationImage();
   }

}