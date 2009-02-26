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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

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
    * @throws ArtifactDoesNotExist if no artifacts are found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch) throws OseeCoreException {
      return getArtifactFromId(artId, branch, false);
   }

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    * 
    * @param artId the id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    */
   public static Artifact getArtifactFromId(int artId, Branch branch, boolean allowDeleted) throws OseeCoreException {
      try {
         Artifact artifact = ArtifactCache.getActive(artId, branch.getBranchId());
         if (artifact != null) {
            return artifact;
         }
         return new ArtifactQueryBuilder(artId, branch, allowDeleted, FULL).getArtifact();
      } catch (MultipleArtifactsExist ex) {
         // it is not possible to have two artifacts with the same artifact id
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
         return null;
      }
   }

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch) throws OseeCoreException {
      return getArtifactFromId(guidOrHrid, branch, false);
   }

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guidOrHrid, Branch branch, boolean allowDeleted) throws OseeCoreException {
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
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndName(String artifactTypeName, String artifactName, Branch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch).getArtifact();
   }

   /**
    * search for artifacts with any of the given artifact ids
    * 
    * @param artifactIds
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactsFromIds(Collection<Integer> artifactIds, Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactIds, branch, allowDeleted, FULL).getArtifacts(50, null);
   }

   /**
    * search for artifacts with any of the given artifact hrids or guids
    * 
    * @param artifactIds
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactsFromIds(List<String> guidOrHrids, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, branch, FULL).getArtifacts(30, null);
   }

   public static List<Artifact> getArtifactsFromIds(List<String> guidOrHrids, Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, branch, allowDeleted, FULL).getArtifacts(30, null);
   }

   public static List<Artifact> getHistoricalArtifactsFromIds(List<String> guidOrHrids, TransactionId transactionId, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, transactionId, allowDeleted, FULL).getArtifacts(30, null);
   }

   public static Artifact getHistoricalArtifactFromId(String guidOrHrid, TransactionId transactionId, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(Arrays.asList(guidOrHrid), transactionId, allowDeleted, FULL).getArtifact();
   }

   public static List<Artifact> getArtifactsFromName(String artifactName, Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted, new AttributeCriteria("Name", artifactName)).getArtifacts(
            30, null);
   }

   public static List<Artifact> getArtifactsFromTypeAndName(String artifactTypeName, String artifactName, Branch branch) throws OseeCoreException {
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
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws OseeCoreException {
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
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromAttribute(String attributeTypeName, String attributeValue, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getArtifact();
   }

   public static List<Artifact> getArtifactsFromType(ArtifactType artifactType, boolean allowDeleted) throws OseeCoreException {
      return getArtifactsFromType(artifactType, null, allowDeleted);
   }

   public static List<Artifact> getArtifactsFromType(ArtifactType artifactType, Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactType, branch, FULL, allowDeleted).getArtifacts(1000, null);
   }

   public static List<Artifact> getArtifactsFromType(ArtifactType artifactType, Branch branch) throws OseeCoreException {
      return getArtifactsFromType(artifactType, branch, false);
   }

   public static List<Artifact> getArtifactsFromBranch(Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Artifact> reloadArtifactsFromBranch(Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).reloadArtifacts(10000);
   }

   public static List<Artifact> getArtifactsFromType(String artifactTypeName, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL, false).getArtifacts(
            1000, null);
   }

   public static List<Artifact> getArtifactsFromTypes(Collection<String> artifactTypeNames, Branch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getTypes(artifactTypeNames), branch, FULL, allowDeleted).getArtifacts(
            1000, null);
   }

   /**
    * search for artifacts of the given type on a particular branch that satisfy the given criteria
    * 
    * @param artifactTypeName
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactsFromTypeAnd(String artifactTypeName, Branch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL, criteria).getArtifacts(
            artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactsFromCriteria(Branch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactsFromCriteria(Branch branch, int artifactCountEstimate, AbstractArtifactSearchCriteria... criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts related
    * 
    * @param artifactId
    * @param branch
    * @param criteria
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
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
    */
   public static List<Artifact> getArtifactsFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch).getArtifacts(100,
            null);
   }

   public static List<Artifact> getArtifactsFromAttribute(String attributeTypeName, String attributeValue, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getArtifacts(
            300, null);
   }

   public static List<Artifact> getArtifactsFromAttribute(AttributeType attributeType, String attributeValue, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeType, attributeValue)).getArtifacts(
            300, null);
   }

   /**
    * Return all artifacts that have one or more attributes of given type regardless of the value
    * 
    * @param attributeTypeName
    * @param branch
    * @return artifacts
    */
   public static List<Artifact> getArtifactsFromAttributeType(String attributeTypeName, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName)).getArtifacts(300,
            null);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL,
            new AttributeCriteria(attributeTypeName, attributeValue));
   }

   public static List<Artifact> getArtifactsFromHistoricalAttributeValue(String attributeValue, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, true, new AttributeCriteria(null, attributeValue, true)).getArtifacts(
            30, null);
   }

   public static List<Artifact> getArtifactsFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, Collection<String> attributeValues, Branch branch, int artifactCountEstimate) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeName), branch, FULL,
            new AttributeCriteria(attributeTypeName, attributeValues)).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * Searches for artifacts having attributes which contain matching keywords entered in the query string.
    * <p>
    * Special characters such as (<b><code>' '</code>, <code>!</code>, <code>"</code>, <code>#</code>, <code>$</code>,
    * <code>%</code>, <code>(</code>, <code>)</code>, <code>*</code>, <code>+</code>, <code>,</code>, <code>-</code>,
    * <code>.</code>, <code>/</code>, <code>:</code>, <code>;</code>, <code>&lt;</code>, <code>&gt;</code>,
    * <code>?</code>, <code>@</code>, <code>[</code>, <code>\</code>, <code>]</code>, <code>^</code>, <code>{</code>,
    * <code>|</code>, <code>}</code>, <code>~</code>, <code>_</code></b>) are assumed to be word separators.
    * </p>
    * <p>
    * For example:
    * <ul>
    * <li><b>'<code>hello.world</code>'</b> will be translated to <b>'<code>hello</code>'</b> and <b>'<code>world</code>
    * '</b>. The search will match attributes with <b>'<code>hello</code>'</b> and <b>'<code>world</code>'</b> keywords.
    * </li>
    * </ul>
    * </p>
    * 
    * @param queryString keywords to match
    * @param matchWordOrder <b>true</b> ensures the query string words exist in order; <b>false</b> matches words in any
    *           order
    * @param nameOnly <b>true</b> searches in name attributes only; <b>false</b> includes all tagged attribute types
    * @param allowDeleted <b>true</b> includes deleted artifacts in results; <b>false</b> omits deleted artifacts
    * @param branch
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws Exception
    */
   public static List<Artifact> getArtifactsFromAttributeWithKeywords(Branch branch, String queryString, boolean matchWordOrder, boolean allowDeleted, String... attributeTypes) throws OseeCoreException {
      return new HttpArtifactQuery(branch, queryString, matchWordOrder, allowDeleted, attributeTypes).getArtifacts(
            FULL, null, false, false, allowDeleted);
   }

   /**
    * Searches for keywords in attributes and returning match location information such as artifact where match was
    * found, attribute containing the match and match location in attribute data.
    * 
    * @see #getArtifactsFromAttributeWithKeywords
    * @param branch
    * @param queryString
    * @param matchWordOrder
    * @param allowDeleted
    * @param findAllMatchLocations when set to <b>true</b> returns all match locations instead of just returning the
    *           first one. When returning all match locations, search performance may be slow.
    * @param attributeTypes
    * @return artifact matches
    * @throws OseeCoreException
    */
   public static List<ArtifactMatch> getArtifactMatchesFromAttributeWithKeywords(Branch branch, String queryString, boolean matchWordOrder, boolean allowDeleted, boolean findAllMatchLocations, String... attributeTypes) throws OseeCoreException {
      return new HttpArtifactQuery(branch, queryString, matchWordOrder, allowDeleted, attributeTypes).getArtifactsWithMatches(
            FULL, null, false, false, allowDeleted, findAllMatchLocations);
   }

   /**
    * @param artId
    * @return reloaded artifacts
    * @throws OseeDataStoreException
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    */
   public static Artifact reloadArtifactFromId(int artId, Branch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(artId, branch, true, FULL).reloadArtifact();
   }
}