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

package org.eclipse.osee.ats.ide.demo.artifact;

import org.eclipse.osee.ats.api.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.ide.demo.util.DemoImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class DemoArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() {
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoReqTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoTestTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
   }
}