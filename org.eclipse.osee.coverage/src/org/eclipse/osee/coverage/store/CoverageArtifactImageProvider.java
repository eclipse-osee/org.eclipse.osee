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
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Ryan D. Brooks
 */
public class CoverageArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ImageManager.registerBaseImage(OseeCoveragePackageStore.ARTIFACT_NAME, CoverageImage.COVERAGE_PACKAGE, this);
      ImageManager.registerBaseImage(OseeCoverageUnitStore.ARTIFACT_NAME, CoverageImage.COVERAGE, this);
      ImageManager.registerBaseImage(OseeCoverageUnitStore.ARTIFACT_FOLDER_NAME, FrameworkImage.FOLDER, this);
   }

}