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

import java.util.Arrays;
import java.util.List;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * Provides the factory for the loading of the XYZ demo artifact types.
 * 
 * @author Donald G. Dunne
 */
public class DemoArtifactFactory extends ArtifactFactory {
	   private static List<String> WORD_ARTIFACTS =
	         Arrays.asList(Requirements.SYSTEM_REQUIREMENT, Requirements.SUBSYSTEM_REQUIREMENT,
	               Requirements.TEST_PROCEDURE, Requirements.TEST_PROCEDURE, "Test Procedure WML",
	               Requirements.HARDWARE_REQUIREMENT, Requirements.SOFTWARE_DESIGN, Requirements.SOFTWARE_REQUIREMENT,
	               Requirements.INDIRECT_SOFTWARE_REQUIREMENT, Requirements.SYSTEM_DESIGN, Requirements.SUBSYSTEM_DESIGN,
	               Requirements.INTERFACE_REQUIREMENT, Requirements.SUBSYSTEM_FUNCTION, Requirements.SYSTEM_FUNCTION);
	   
   public DemoArtifactFactory() {
      super(Collections.setUnion(WORD_ARTIFACTS, Arrays.asList(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME, DemoTestTeamWorkflowArtifact.ARTIFACT_NAME,
            DemoReqTeamWorkflowArtifact.ARTIFACT_NAME)));
   }

   @Override
   public Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.getName().equals(DemoCodeTeamWorkflowArtifact.ARTIFACT_NAME)) {
         return new DemoCodeTeamWorkflowArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.getName().equals(DemoTestTeamWorkflowArtifact.ARTIFACT_NAME)) {
         return new DemoTestTeamWorkflowArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.getName().equals(DemoReqTeamWorkflowArtifact.ARTIFACT_NAME)) {
         return new DemoReqTeamWorkflowArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (WORD_ARTIFACTS.contains(artifactType.getName())) {
          return new WordArtifact(this, guid, humandReadableId, branch, artifactType);
       }
      throw new IllegalArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }
}