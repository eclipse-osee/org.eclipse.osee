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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * Contains methods specific to artifact types. All artifact methods will eventually be moved from the
 * ConfigurationPersistenceManager to here.
 * 
 * @author Donald G. Dunne
 */
public class ArtifactTypeManager {

   /**
    * Get a new instance of type artifactTypeName
    * 
    * @param artifactTypeName
    * @param branch
    * @return
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch) throws SQLException, OseeCoreException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
            branch);
   }

   /**
    * Get a new instance of type artifactTypeName and set it's name.
    * 
    * @param artifactTypeName
    * @param branch
    * @param name
    * @return
    * @throws SQLException
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String name) throws SQLException {
      Artifact artifact;
      try {
         artifact = addArtifact(artifactTypeName, branch);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
      artifact.setDescriptiveName(name);
      return artifact;
   }

   /**
    * Get a new instance of the type of artifact. This is just a convenience method that calls makeNewArtifact on the
    * known factory with this descriptor for the descriptor parameter, and the supplied branch.
    * 
    * @param branch branch on which artifact will be created
    * @return Return artifact reference
    * @throws SQLException
    * @throws OseeCoreException
    * @see ArtifactFactory#makeNewArtifact(Branch, ArtifactType, String, String, ArtifactProcessor)
    */
   public static Artifact addArtifact(String artifactTypeName, Branch branch, String guid, String humandReadableId) throws SQLException, OseeCoreException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
            branch, guid, humandReadableId);
   }

   public static ArtifactType getType(int artifactTypeId) throws SQLException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeId);
   }

   public static ArtifactType getType(String artifactTypeName) throws SQLException {
      return ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName);
   }
}