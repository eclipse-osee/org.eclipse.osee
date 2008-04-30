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
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQuery {

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(guidOrHrid, branch).getArtifacts();
      return getSoleArtifact(artifacts, " with id \"" + guidOrHrid + "\" on branch \"" + branch + "\"");
   }

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @param allowDeleted
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch, boolean allowDeleted) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(guidOrHrid, branch, allowDeleted).getArtifacts();
      return getSoleArtifact(artifacts, " with id \"" + guidOrHrid + "\" on branch \"" + branch + "\"");
   }

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    * 
    * @param artId the id of the desired artifact
    * @param branch
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return getArtifactFromId(artId, branch, false);
   }

   public static Collection<Artifact> getArtifactsFromIds(List<Integer> artifactIds, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(artifactIds, branch, false).getArtifacts();
   }

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    * 
    * @param artId the id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch, boolean allowDeleted) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts = new ArtifactQueryBuilder(artId, branch, allowDeleted).getArtifacts();
      return getSoleArtifact(artifacts, " with id \"" + artId + "\" on branch \"" + branch + "\"");
   }

   /**
    * search for exactly one artifact based on its type and name - otherwise throw an exception
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

   /**
    * search for artifacts of the given type on a particular branch with the various criteria
    * 
    * @param artifactTypeName
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws SQLException
    */
   public static Collection<Artifact> getAtrifactsFromTypeAnd(String artifactTypeName, Branch branch, AbstractArtifactSearchCriteria... criteria) throws SQLException {
      return new ArtifactQueryBuilder(ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
            artifactTypeName), branch, criteria).getArtifacts();
   }

   /**
    * search for artifacts of the given type with an attribute of the given type and value
    * 
    * @param artifactTypeName
    * @param attributeTypeName
    * @param attributeValue
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws SQLException
    */
   public static Collection<Artifact> getAtrifactsFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws SQLException {
      ArtifactSubtypeDescriptor artifactType =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName);
      return new ArtifactQueryBuilder(artifactType, branch, new AttributeValueCriteria(attributeTypeName,
            attributeValue)).getArtifacts();
   }

   public static Collection<Artifact> getAtrifactsFromHistoricalAttributeValue(String attributeValue) throws SQLException {
      return new ArtifactQueryBuilder(new AttributeValueCriteria((DynamicAttributeDescriptor) null, attributeValue,
            true)).getArtifacts();
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    * 
    * @param artifactTypeName
    * @param attributeTypeName
    * @param attributeValue
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getAtrifactFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Collection<Artifact> artifacts =
            getAtrifactsFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch);
      return getSoleArtifact(
            artifacts,
            " with type \"" + artifactTypeName + " and \"" + attributeTypeName + " = \"" + attributeValue + "\" on branch \"" + branch + "\"");
   }
}