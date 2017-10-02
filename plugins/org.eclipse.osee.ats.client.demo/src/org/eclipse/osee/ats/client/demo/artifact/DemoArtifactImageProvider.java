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
package org.eclipse.osee.ats.client.demo.artifact;

import org.eclipse.osee.ats.client.demo.util.DemoImage;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class DemoArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init()  {
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoReqTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(DemoArtifactTypes.DemoTestTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
   }
}