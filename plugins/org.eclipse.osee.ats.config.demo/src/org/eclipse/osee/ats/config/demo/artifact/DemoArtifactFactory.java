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
package org.eclipse.osee.ats.config.demo.artifact;

import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.support.test.util.DemoArtifactTypes;

/**
 * Provides the factory for the loading of the XYZ demo artifact types.
 * 
 * @author Donald G. Dunne
 */
public class DemoArtifactFactory extends ArtifactFactory {
   public DemoArtifactFactory() {
      super(DemoArtifactTypes.DemoCodeTeamWorkflow, DemoArtifactTypes.DemoTestTeamWorkflow,
         DemoArtifactTypes.DemoReqTeamWorkflow);
   }

   @Override
   protected Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      return new TeamWorkFlowArtifact(this, guid, humandReadableId, branch, artifactType);
   }
}