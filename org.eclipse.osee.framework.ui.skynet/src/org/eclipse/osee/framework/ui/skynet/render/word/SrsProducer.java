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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Jeff C. Phillips
 */
public class SrsProducer implements IWordMlProducer {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   public BlamVariableMap process(BlamVariableMap variableMap) throws SQLException, IllegalStateException, IOException {
      if (variableMap == null) throw new IllegalArgumentException("variableMap must not be null");

      String name = variableMap.getString("Name");
      Branch branch = variableMap.getBranch("Branch");
      Artifact root = artifactManager.getDefaultHierarchyRootArtifact(branch);
      Artifact softwareRequirement = root.getChild("Software Requirements");
      Artifact crewInterface = softwareRequirement.getChild("Crew Interface");
      Artifact subsystemManagement = softwareRequirement.getChild("Subsystem Management");
      Artifact appendices = softwareRequirement.getChild("SRS Appendices");

      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(500);

      process(crewInterface, artifacts, name);
      process(subsystemManagement, artifacts, name);
      process(appendices, artifacts, name);

      variableMap.setValue("srsProducer.objects", artifacts);
      return variableMap;
   }

   private void process(Artifact parent, ArrayList<Artifact> artifacts, String name) throws IllegalStateException, IOException, SQLException {
      try {
         Artifact child = parent.getChild(name);
         artifacts.addAll(child.getDescendants());

      } catch (IllegalArgumentException ex) {
         //We expect there to be times where the child does not exists and will throw an exception.
      }
   }
}
