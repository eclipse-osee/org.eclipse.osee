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
package org.eclipse.osee.ats.ide.demo.artifact;

import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.ide.demo.util.DemoImage;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageProvider;

/**
 * @author Donald G. Dunne
 */
public class DemoArtifactImageProvider extends ArtifactImageProvider {

   @Override
   public void init() {
      ArtifactImageManager.registerBaseImage(AtsDemoOseeTypes.DemoCodeTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(AtsDemoOseeTypes.DemoReqTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
      ArtifactImageManager.registerBaseImage(AtsDemoOseeTypes.DemoTestTeamWorkflow, DemoImage.DEMO_WORKFLOW, this);
   }
}