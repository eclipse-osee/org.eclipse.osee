/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.define;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.OteArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class OteArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() {
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestProcedure, OteDefineImage.TEST_PROCEDURE, this);
      ArtifactImageManager.registerBaseImage(OteArtifactTypes.TestRun, OteDefineImage.TEST_RUN, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestCase, OteDefineImage.TEST_CASE, this);
      ArtifactImageManager.registerBaseImage(CoreArtifactTypes.TestSupport, OteDefineImage.TEST_SUPPORT, this);
   }
}