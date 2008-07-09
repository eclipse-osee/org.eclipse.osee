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

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQuery {

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    * 
    * @param artId the id of the desired artifact
    * @param branch
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch) throws SQLException, ArtifactDoesNotExist {
      return getArtifactFromId(artId, branch, false);
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
    */
   public static Artifact getArtifactFromId(int artId, Branch branch, boolean allowDeleted) throws SQLException, ArtifactDoesNotExist {
      try {
         Artifact artifact = ArtifactCache.getActive(artId, branch.getBranchId());
         if (artifact != null) {
            return artifact;
         }
         return new ArtifactQueryBuilder(artId, branch, allowDeleted, FULL).getArtifact();
      } catch (MultipleArtifactsExist ex) {
         // it is not possible to have two artifacts with the same artifact id
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return null;
      }
   }

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
      return getArtifactFromId(guidOrHrid, branch, false);
   }

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch, boolean allowDeleted) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Artifact artifact = ArtifactCache.getActive(guidOrHrid, branch.getBranchId());
      if (artifact != null) {
         return artifact;
      }
      return new ArtifactQueryBuilder(guidOrHrid, branch, allowDeleted, FULL).getArtifact();
   }

   /**
    * search for exactly one artifact based on its type and name - otherwise throw an exception
    * 
    * @param artifactTypeName
    * @param artifactName
    * @param branch
    * @return exactly one artifact based on its type and name - otherwise throw an exception
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndName(String artifactTypeName, String artifactName, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return queryFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch).getArtifact();
   }

   /**
    * search for artifacts with any of the given artifact ids
    * 
    * @param artifactIds
    * @param branch
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifactsFromIds(Collection<Integer> artifactIds, Branch branch, boolean allowDeleted) throws SQLException {
      return new ArtifactQueryBuilder(artifactIds, branch, allowDeleted, FULL).getArtifacts(50, null);
   }

   /**
    * search for artifacts with any of the given artifact hrids or guids
    * 
    * @param artifactIds
    * @param branch
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifactsFromIds(List<String> guidOrHrids, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(guidOrHrids, branch, FULL).getArtifacts(30, null);
   }

   public static List<Artifact> getArtifactsFromName(String artifactName, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria("Name", artifactName)).getArtifacts(
            30, null);
   }

   public static List<Artifact> getArtifactsFromTypeAndName(String artifactTypeName, String artifactName, Branch branch) throws SQLException {
      return getArtifactsFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch);
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
   public static Artifact getArtifactFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return queryFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch).getArtifact();
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    * 
    * @param attributeTypeName
    * @param attributeValue
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws SQLException
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromAttribute(String attributeTypeName, String attributeValue, Branch branch) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getArtifact();
   }

   public static List<Artifact> getArtifactsFromType(ArtifactType artifactType, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(artifactType, branch, FULL).getArtifacts(1000, null);
   }

   public static List<Artifact> getArtifactsFromBranch(Branch branch, boolean allowDeleted) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Artifact> getArtifactsFromType(String artifactTypeName, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL).getArtifacts(1000,
            null);
   }

   public static List<Artifact> getArtifactsFromTypes(Collection<String> artifactTypeNames, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getTypes(artifactTypeNames), branch, FULL).getArtifacts(1000,
            null);
   }

   /**
    * search for artifacts of the given type on a particular branch that satisfy the given criteria
    * 
    * @param artifactTypeName
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws SQLException
    */
   public static List<Artifact> getArtifactsFromTypeAnd(String artifactTypeName, Branch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws SQLException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL, criteria).getArtifacts(
            artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @param branch
    * @param criteria
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifactsFromCriteria(Branch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @param branch
    * @param criteria
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifactsFromCriteria(Branch branch, int artifactCountEstimate, AbstractArtifactSearchCriteria... criteria) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, false, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts related
    * 
    * @param artifactId
    * @param branch
    * @param criteria
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide) throws SQLException {
      return new ArtifactQueryBuilder(artifact.getBranch(), FULL, false, new RelationCriteria(artifact.getArtId(),
            relationType, relationSide)).getArtifacts(1000, null);
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
   public static List<Artifact> getArtifactsFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws SQLException {
      return queryFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch).getArtifacts(100,
            null);
   }

   public static List<Artifact> getArtifactsFromAttribute(String attributeTypeName, String attributeValue, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getArtifacts(
            300, null);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL,
            new AttributeCriteria(attributeTypeName, attributeValue));
   }

   public static List<Artifact> getArtifactsFromHistoricalAttributeValue(String attributeValue, Branch branch) throws SQLException {
      return new ArtifactQueryBuilder(branch, FULL, true, new AttributeCriteria(null, attributeValue, true)).getArtifacts(
            30, null);
   }

   /**
    * @param artifactCountEstimate
    * @param confirmer
    * @param artifact_name
    * @param string
    * @param actionItemNames
    * @param atsBranch
    * @return
    */
   public static List<Artifact> getArtifactsFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, Collection<String> attributeValues, Branch branch, int artifactCountEstimate) throws SQLException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL,
            new AttributeCriteria(attributeTypeName, attributeValues)).getArtifacts(artifactCountEstimate, null);
   }
}