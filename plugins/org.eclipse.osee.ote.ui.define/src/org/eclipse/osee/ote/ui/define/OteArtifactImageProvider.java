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

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class OteArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init()  {
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestProcedure, OteDefineImage.TEST_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestRun, OteDefineImage.TEST_RUN, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestCase, OteDefineImage.TEST_CASE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestSupport, OteDefineImage.TEST_SUPPORT, this);
   }
}