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
package org.eclipse.osee.ote.ui.define;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class OteArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() throws OseeCoreException {
      ArtifactImageManager.registerBaseImage("Test Procedure", OteDefineImage.TEST_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage("Test Configuration", OteDefineImage.TEST_CONFIG, this);
      ArtifactImageManager.registerBaseImage("Test Run", OteDefineImage.TEST_RUN, this);
      ArtifactImageManager.registerBaseImage("Test Case", OteDefineImage.TEST_CASE, this);
      ArtifactImageManager.registerBaseImage("Test Support", OteDefineImage.TEST_SUPPORT, this);
   }
}