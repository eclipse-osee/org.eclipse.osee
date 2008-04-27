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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQuery {

   /**
    * return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @return
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(guidOrHrid, branch).getArtifacts();
      return getSoleArtifact(artifacts, " with id \"" + guidOrHrid + "\" on branch \"" + branch + "\"");
   }

   /**
    * return exactly one artifact by one its id - otherwise throw an exception
    * 
    * @param artId the id of the desired artifact
    * @param branch
    * @return
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(artId, branch).getArtifacts();
      return getSoleArtifact(artifacts, " with id \"" + artId + "\" on branch \"" + branch + "\"");
   }

   /**
    * return exactly one artifact based on its type and name - otherwise throw an exception
    * 
    * @param artifactType
    * @param artifactName
    * @param branch
    * @return exactly one artifact based on its type and name - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndName(ArtifactSubtypeDescriptor artifactType, String artifactName, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      AttributeValueCriteria attributeCriteria = new AttributeValueCriteria("Name", artifactName);
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(artifactType, branch, attributeCriteria).getArtifacts();
      return getSoleArtifact(
            artifacts,
            " with type \"" + artifactType.getName() + " and name \"" + artifactName + "\" on branch \"" + branch + "\"");
   }

   public static Artifact getArtifactFromTypeAndName(String artifactTypeName, String artifactName, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactFromTypeAndName(ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
            artifactTypeName), artifactName, branch);
   }

   private static Artifact getSoleArtifact(Collection<Artifact> artifacts, String message) throws ArtifactDoesNotExist, MultipleArtifactsExist {
      if (artifacts.size() == 0) {
         throw new ArtifactDoesNotExist("No artifact found" + message);
      }
      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist(artifacts.size() + " artifacts found" + message);
      }
      return artifacts.iterator().next();
   }

   public static Collection<Artifact> getAtrifactsFromType(ArtifactSubtypeDescriptor artifactType, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(artifactType, branch).getArtifacts();
   }

   public static Collection<Artifact> getAtrifactsFromType(String artifactTypeName, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
            artifactTypeName), branch).getArtifacts();
   }
}