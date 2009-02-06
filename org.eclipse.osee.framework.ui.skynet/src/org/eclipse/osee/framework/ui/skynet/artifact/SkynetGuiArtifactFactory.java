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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.GlobalPreferences;
import org.eclipse.osee.framework.ui.skynet.blam.BlamWorkflow;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerCustomizationArtifact;

/**
 * @author Ryan D. Brooks
 */
public class SkynetGuiArtifactFactory extends ArtifactFactory {

   public static Collection<String> BASIC_ARTIFACTS =
         Arrays.asList(WorkFlowDefinition.ARTIFACT_NAME, WorkPageDefinition.ARTIFACT_NAME,
               WorkWidgetDefinition.ARTIFACT_NAME, WorkRuleDefinition.ARTIFACT_NAME);

   public static Collection<String> OTHER_ARTIFACTS =
         Arrays.asList(BlamWorkflow.ARTIFACT_NAME, GlobalPreferences.ARTIFACT_NAME,
               XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME);

   @SuppressWarnings("unchecked")
   public SkynetGuiArtifactFactory() {
      super(Collections.setUnion(BASIC_ARTIFACTS, OTHER_ARTIFACTS));
   }

   public @Override
   Artifact getArtifactInstance(String guid, String humandReadableId, Branch branch, ArtifactType artifactType) throws OseeCoreException {
      if (artifactType.getName().equals(XViewerCustomizationArtifact.ARTIFACT_TYPE_NAME)) {
         return new XViewerCustomizationArtifact(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.getName().equals(BlamWorkflow.ARTIFACT_NAME)) {
         return new BlamWorkflow(this, guid, humandReadableId, branch, artifactType);
      }
      if (artifactType.getName().equals(GlobalPreferences.ARTIFACT_NAME)) {
         return new GlobalPreferences(this, guid, humandReadableId, branch, artifactType);
      }
      if (BASIC_ARTIFACTS.contains(artifactType.getName())) {
         return new Artifact(this, guid, humandReadableId, branch, artifactType);
      }
      throw new OseeArgumentException("did not recognize the artifact type: " + artifactType.getName());
   }
}