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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

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
   public static Artifact getArtifactFromId(int artId, IOseeBranch branch) throws OseeCoreException {
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
   public static Artifact getArtifactFromId(int artId, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return getOrCheckArtifactFromId(artId, branch, allowDeleted, QueryType.GET);
   }

   private static Artifact getOrCheckArtifactFromId(int artId, IOseeBranch branch, boolean allowDeleted, QueryType queryType) throws OseeCoreException {
      Artifact artifact = ArtifactCache.getActive(artId, branch);
      if (artifact != null) {
         return artifact;
      }
      return new ArtifactQueryBuilder(artId, branch, allowDeleted, FULL).getOrCheckArtifact(queryType);
   }

   /**
    * Checks for existence of an artifact by id
    * 
    * @param artifactId the id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return one artifact by one its id if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(int artifactId, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return getOrCheckArtifactFromId(artifactId, branch, allowDeleted, QueryType.CHECK);
   }

   /**
    * Checks for existence of an artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return one artifact by one its id if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(String guidOrHrid, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return getOrCheckArtifactFromId(guidOrHrid, branch, allowDeleted, QueryType.CHECK);
   }

   /**
    * Checks for existence of an artifact by one its guid or human readable id - otherwise throw an exception
    * 
    * @param guidOrHrid either the guid or human readable id of the desired artifact
    * @param branch
    * @return one artifact by one its guid or human readable id if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(String guidOrHrid, IOseeBranch branch) throws OseeCoreException {
      return getOrCheckArtifactFromId(guidOrHrid, branch, false, QueryType.CHECK);
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
   public static Artifact getArtifactFromId(String guidOrHrid, IOseeBranch branch) throws OseeCoreException {
      return getOrCheckArtifactFromId(guidOrHrid, branch, false, QueryType.GET);
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
   public static Artifact getArtifactFromId(String guidOrHrid, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return getOrCheckArtifactFromId(guidOrHrid, branch, allowDeleted, QueryType.GET);
   }

   private static Artifact getOrCheckArtifactFromId(String guidOrHrid, IOseeBranch branch, boolean allowDeleted, QueryType queryType) throws OseeCoreException {
      Artifact artifact = ArtifactCache.getActive(guidOrHrid, branch);
      if (artifact != null) {
         return artifact;
      }
      return new ArtifactQueryBuilder(guidOrHrid, branch, allowDeleted, FULL).getOrCheckArtifact(queryType);
   }

   /**
    * search for exactly one artifact based on its type and name - otherwise throw an exception
    * 
    * @return exactly one artifact based on its type and name - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndName(String artifactTypeName, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch).getOrCheckArtifact(QueryType.GET);
   }

   public static Artifact getArtifactFromTypeAndName(IArtifactType artifactType, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(ArtifactTypeManager.getType(artifactType), "Name", artifactName, branch).getOrCheckArtifact(
            QueryType.GET);
   }

   public static List<Integer> selectArtifactIdsFromTypeAndName(String artifactTypeName, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch).selectArtifacts(2);
   }

   /**
    * Checks for existence of an artifact based on its type and name
    * 
    * @return one artifact based on its type and name if it exists, otherwise null
    */
   public static Artifact checkArtifactFromTypeAndName(IArtifactType artifactTypeToken, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(ArtifactTypeManager.getType(artifactTypeToken), "Name", artifactName, branch).getOrCheckArtifact(
            QueryType.CHECK);
   }

   /**
    * search for artifacts with any of the given artifact ids
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromIds(Collection<Integer> artifactIds, IOseeBranch branch) throws OseeCoreException {
      return ArtifactLoader.loadArtifacts(artifactIds, branch, ArtifactLoad.FULL, false);
   }

   /**
    * search for artifacts with any of the given artifact hrids or guids
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromIds(List<String> guidOrHrids, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, branch, FULL).getArtifacts(30, null);
   }

   public static List<Artifact> getArtifactListFromIds(List<String> guidOrHrids, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, branch, allowDeleted, FULL).getArtifacts(30, null);
   }

   public static List<Artifact> getHistoricalArtifactListFromIds(List<String> guidOrHrids, TransactionRecord transactionId, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(guidOrHrids, transactionId, allowDeleted, FULL).getArtifacts(30, null);
   }

   public static Artifact getHistoricalArtifactFromId(int artifactId, TransactionRecord transactionId, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactId, transactionId, allowDeleted, FULL).getOrCheckArtifact(QueryType.GET);
   }

   public static Artifact getHistoricalArtifactFromId(String guidOrHrid, TransactionRecord transactionId, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(Arrays.asList(guidOrHrid), transactionId, allowDeleted, FULL).getOrCheckArtifact(QueryType.GET);
   }

   public static List<Artifact> getArtifactListFromName(String artifactName, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted, new AttributeCriteria("Name", artifactName)).getArtifacts(
            30, null);
   }

   public static List<Artifact> getArtifactListFromTypeAndName(String artifactTypeName, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return getArtifactListFromTypeAndAttribute(artifactTypeName, "Name", artifactName, branch);
   }

   public static List<Artifact> getArtifactListFromTypeAndName(IArtifactType artifactType, String artifactName, IOseeBranch branch) throws OseeCoreException {
      return getArtifactListFromTypeAndAttribute(artifactType, "Name", artifactName, branch);
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch).getOrCheckArtifact(
            QueryType.GET);
   }

   public static Artifact getArtifactFromTypeAndAttribute(IArtifactType artifactType, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactType, attributeTypeName, attributeValue, branch).getOrCheckArtifact(
            QueryType.GET);
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromAttribute(String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getOrCheckArtifact(QueryType.GET);
   }

   public static List<Artifact> getArtifactListFromType(IArtifactType artifactTypeToken, boolean allowDeleted) throws OseeCoreException {
      return getArtifactListFromType(artifactTypeToken, null, allowDeleted);
   }

   public static List<Artifact> getArtifactListFromType(IArtifactType artifactTypeToken, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(ArtifactTypeManager.getType(artifactTypeToken), branch, FULL, allowDeleted).getArtifacts(
            1000, null);
   }

   public static List<Artifact> getArtifactListFromType(IArtifactType artifactTypeToken, IOseeBranch branch) throws OseeCoreException {
      return getArtifactListFromType(artifactTypeToken, branch, false);
   }

   public static List<Artifact> getArtifactListFromBranch(IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Integer> selectArtifactListFromBranch(IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).selectArtifacts(10000);
   }

   public static List<Artifact> getArtifactListFromBranch(IOseeBranch branch, ArtifactLoad loadLevel, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, loadLevel, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Artifact> reloadArtifactListFromBranch(IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, allowDeleted).reloadArtifacts(10000);
   }

   public static List<Artifact> getArtifactListFromType(String artifactTypeName, IOseeBranch branch) throws OseeCoreException {
      return getArtifactListFromType(ArtifactTypeManager.getType(artifactTypeName), branch);
   }

   public static List<Artifact> getArtifactListFromArtifactTypes(Collection<? extends IArtifactType> artifactTypes, IOseeBranch branch, boolean allowDeleted) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactTypes, branch, FULL, allowDeleted).getArtifacts(1000, null);
   }

   /**
    * search for artifacts of the given type on a particular branch that satisfy the given criteria
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromTypeAnd(IArtifactType artifactType, IOseeBranch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactType, branch, FULL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromCriteria(IOseeBranch branch, int artifactCountEstimate, List<AbstractArtifactSearchCriteria> criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromCriteria(IOseeBranch branch, int artifactCountEstimate, AbstractArtifactSearchCriteria... criteria) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts related
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getRelatedArtifactList(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifact.getBranch(), FULL, false, new RelationCriteria(artifact.getArtId(),
            relationType, relationSide)).getArtifacts(1000, null);
   }

   /**
    * search for artifacts by relation
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromRelation(RelationType relationType, RelationSide relationSide, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new RelationCriteria(relationType, relationSide)).getArtifacts(
            1000, null);
   }

   /**
    * search for artifacts of the given type with an attribute of the given type and value
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, attributeTypeName, attributeValue, branch).getArtifacts(100,
            null);
   }

   /**
    * search for artifacts of the given type with an attribute of the given type and value
    * 
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromTypeAndAttribute(IArtifactType artifactType, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactType, attributeTypeName, attributeValue, branch).getArtifacts(100, null);
   }

   public static List<Artifact> getArtifactListFromTypeAndAttribute(String artifactTypeName, IAttributeType attributeType, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(artifactTypeName, attributeType.getName(), attributeValue, branch).getArtifacts(
            100, null);
   }

   public static List<Artifact> getArtifactListFromAttribute(String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName, attributeValue)).getArtifacts(
            300, null);
   }

   public static List<Artifact> getArtifactListFromAttribute(AttributeType attributeType, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeType, attributeValue)).getArtifacts(
            300, null);
   }

   /**
    * Return all artifacts that have one or more attributes of given type regardless of the value
    */
   public static List<Artifact> getArtifactListFromAttributeType(String attributeTypeName, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeTypeName)).getArtifacts(300,
            null);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(String artifactTypeName, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(ArtifactTypeManager.getType(artifactTypeName), attributeTypeName,
            attributeValue, branch);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(IArtifactType artifactType, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return queryFromTypeAndAttribute(ArtifactTypeManager.getType(artifactType), attributeTypeName, attributeValue,
            branch);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(ArtifactType artifactType, String attributeTypeName, String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactType, branch, FULL, new AttributeCriteria(attributeTypeName,
            attributeValue));
   }

   public static List<Artifact> getArtifactListFromHistoricalAttributeValue(String attributeValue, IOseeBranch branch) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, true, new AttributeCriteria(null, attributeValue, true)).getArtifacts(
            30, null);
   }

   public static List<Artifact> getArtifactListFromTypeAndAttribute(IArtifactType artifactType, IAttributeType attributeType, Collection<String> attributeValues, IOseeBranch branch, int artifactCountEstimate) throws OseeCoreException {
      return new ArtifactQueryBuilder(artifactType, branch, FULL, new AttributeCriteria(attributeType, attributeValues)).getArtifacts(
            artifactCountEstimate, null);
   }

   public static List<Artifact> getArtifactListFromAttributeValues(IAttributeType attributeType, Collection<String> attributeValues, IOseeBranch branch, int artifactCountEstimate) throws OseeCoreException {
      return new ArtifactQueryBuilder(branch, FULL, false, new AttributeCriteria(attributeType, attributeValues)).getArtifacts(
            artifactCountEstimate, null);
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
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromAttributeKeywords(IOseeBranch branch, String queryString, boolean matchWordOrder, boolean allowDeleted, boolean isCaseSensitive, String... attributeTypes) throws OseeCoreException {
      return new HttpArtifactQuery(branch, queryString, matchWordOrder, allowDeleted, isCaseSensitive, attributeTypes).getArtifacts(
            FULL, null, false, false, allowDeleted);
   }

   /**
    * Searches for keywords in attributes and returning match location information such as artifact where match was
    * found, attribute containing the match and match location in attribute data.
    * 
    * @see #getArtifactsFromAttributeWithKeywords
    * @param findAllMatchLocations when set to <b>true</b> returns all match locations instead of just returning the
    *           first one. When returning all match locations, search performance may be slow.
    */
   public static List<ArtifactMatch> getArtifactMatchesFromAttributeKeywords(IOseeBranch branch, String queryString, boolean matchWordOrder, boolean allowDeleted, boolean findAllMatchLocations, boolean isCaseSensitive, String... attributeTypes) throws OseeCoreException {
      return new HttpArtifactQuery(branch, queryString, matchWordOrder, allowDeleted, isCaseSensitive, attributeTypes).getArtifactsWithMatches(
            FULL, null, false, false, allowDeleted, findAllMatchLocations);
   }

   public static Artifact reloadArtifactFromId(int artId, IOseeBranch branch) throws OseeCoreException {
      Artifact artifact = new ArtifactQueryBuilder(artId, branch, true, FULL).reloadArtifact();
      OseeEventManager.kickArtifactReloadEvent(new ArtifactQuery(), Collections.singleton(artifact));
      return artifact;
   }

   public static Collection<Artifact> reloadArtifacts(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Set<Integer> artIds = new HashSet<Integer>();
      IOseeBranch branch = null;
      for (Artifact artifact : artifacts) {
         if (branch == null) {
            branch = artifact.getBranch();
         } else if (!branch.equals(artifact.getBranch())) {
            throw new OseeArgumentException("Reloading artifacts of different branches not supported");
         }
         artIds.add(artifact.getArtId());
      }

      Collection<Artifact> reloadedArts =
            new ArtifactQueryBuilder(artIds, branch, true, FULL).reloadArtifacts(artifacts.size());
      OseeEventManager.kickArtifactReloadEvent(new ArtifactQuery(), artifacts);
      return reloadedArts;
   }
}