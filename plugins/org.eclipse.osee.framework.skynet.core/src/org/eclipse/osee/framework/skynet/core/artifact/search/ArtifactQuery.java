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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.LoadLevel.ALL;
import static org.eclipse.osee.framework.skynet.core.artifact.LoadType.INCLUDE_CACHE;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMatch;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchParameters;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQuery {
   private static Map<Long, String> uuidToGuid;

   public static <T extends ArtifactId & HasBranch> Artifact getArtifactFromToken(T artifactToken, DeletionFlag deletionFlag) {
      return getOrCheckArtifactFromId(artifactToken, artifactToken.getBranch(), deletionFlag, QueryType.GET);
   }

   public static <T extends ArtifactId & HasBranch> Artifact getArtifactOrNull(T artifactToken, DeletionFlag deletionFlag) {
      return getOrCheckArtifactFromId(artifactToken, artifactToken.getBranch(), deletionFlag, QueryType.CHECK);
   }

   public static <T extends ArtifactId & HasBranch> Artifact getArtifactFromToken(T artifactToken) {
      return getOrCheckArtifactFromId(artifactToken, artifactToken.getBranch(), EXCLUDE_DELETED, QueryType.GET);
   }

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    *
    * @param artId the id of the desired artifact
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    */
   public static Artifact getArtifactFromId(long artId, BranchId branch) {
      return getOrCheckArtifactFromId(ArtifactId.valueOf(artId), branch, EXCLUDE_DELETED, QueryType.GET);
   }

   public static Artifact getArtifactFromId(ArtifactId artId, BranchId branch) {
      return getOrCheckArtifactFromId(artId, branch, EXCLUDE_DELETED, QueryType.GET);
   }

   /**
    * search for exactly one artifact by one its id - otherwise throw an exception
    *
    * @param artifactId the id of the desired artifact
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    */
   public static Artifact getArtifactFromId(ArtifactId artifactId, BranchId branch, DeletionFlag allowDeleted) {
      return getOrCheckArtifactFromId(artifactId, branch, allowDeleted, QueryType.GET);
   }

   public static Artifact getArtifactOrNull(ArtifactId artifactId, BranchId branch, DeletionFlag deletionFlag) {
      return getOrCheckArtifactFromId(artifactId, branch, deletionFlag, QueryType.CHECK);
   }

   private static Artifact getOrCheckArtifactFromId(ArtifactId artifactId, BranchId branch, DeletionFlag allowDeleted, QueryType queryType) {
      ArtifactToken artifactToken;
      if (artifactId instanceof ArtifactToken) {
         artifactToken = ArtifactToken.valueOf((ArtifactToken) artifactId, branch);
      } else {
         artifactToken = ArtifactToken.valueOf(artifactId, branch);
      }
      Artifact artifact = ArtifactCache.getActive(artifactToken);
      if (artifact != null) {
         if (artifact.isDeleted() && allowDeleted == EXCLUDE_DELETED) {
            if (queryType == QueryType.CHECK) {
               artifact = null;
            } else {
               throw new ArtifactDoesNotExist("Deleted artifact unexpectedly returned");
            }
         }
      } else {
         artifact = new ArtifactQueryBuilder(artifactId, branch, allowDeleted, ALL).getOrCheckArtifact(queryType);
      }
      return artifact;
   }

   /**
    * Checks for existence of an artifact by id
    *
    * @param artifactId the id of the desired artifact
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return one artifact by one its id if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(ArtifactId artifactId, BranchId branch, DeletionFlag allowDeleted) {
      return getOrCheckArtifactFromId(artifactId, branch, allowDeleted, QueryType.CHECK);
   }

   /**
    * Checks for existence of an artifact by one its guid - otherwise throw an exception
    *
    * @param guid either the guid of the desired artifact
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return one artifact by one its id if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(String guid, BranchId branch, DeletionFlag allowDeleted) {
      return getOrCheckArtifactFromId(guid, branch, allowDeleted, QueryType.CHECK);
   }

   /**
    * Checks for existence of an artifact by one its guid or human readable id - otherwise throw an exception
    *
    * @param artifactId of the desired artifact
    * @return one artifact by its guid if it exists, otherwise null
    */
   public static Artifact checkArtifactFromId(ArtifactId artifactId, BranchId branch) {
      return getOrCheckArtifactFromId(artifactId, branch, EXCLUDE_DELETED, QueryType.CHECK);
   }

   /**
    * search for exactly one artifact by one its guid - otherwise throw an exception
    *
    * @param guid of the desired artifact
    * @return exactly one artifact by one its guid - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guid, BranchId branch) {
      return getOrCheckArtifactFromId(guid, branch, EXCLUDE_DELETED, QueryType.GET);
   }

   /**
    * search for exactly one artifact by one its guid or human readable id - otherwise throw an exception
    *
    * @param the guid of the desired artifact
    * @param allowDeleted whether to return the artifact even if it has been deleted
    * @return exactly one artifact by one its guid or human readable id - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromId(String guid, BranchId branch, DeletionFlag allowDeleted) {
      return getOrCheckArtifactFromId(guid, branch, allowDeleted, QueryType.GET);
   }

   private static Artifact getOrCheckArtifactFromId(String guid, BranchId branch, DeletionFlag allowDeleted, QueryType queryType) {
      Artifact artifact = ArtifactCache.getActive(guid, branch);
      if (artifact != null) {
         if (artifact.isDeleted() && allowDeleted == EXCLUDE_DELETED) {
            if (queryType == QueryType.CHECK) {
               return null;
            } else {
               throw new ArtifactDoesNotExist("Deleted artifact unexpectedly returned");
            }
         }
         return artifact;
      }
      return new ArtifactQueryBuilder(guid, branch, allowDeleted, ALL).getOrCheckArtifact(queryType);
   }

   /**
    * search for exactly one artifact based on its type and name - otherwise throw an exception
    *
    * @return exactly one artifact based on its type and name - otherwise throw an exception
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndName(ArtifactTypeId artifactType, String artifactName, BranchId branch) {
      return getArtifactFromTypeAndAttribute(artifactType, CoreAttributeTypes.Name, artifactName, branch,
         QueryType.GET);
   }

   /**
    * Checks for existence of an artifact based on its type and name
    *
    * @return one artifact based on its type and name if it exists, otherwise null
    */
   public static Artifact checkArtifactFromTypeAndName(ArtifactTypeId artifactType, String artifactName, BranchId branch) {
      return getArtifactFromTypeAndAttribute(artifactType, CoreAttributeTypes.Name, artifactName, branch,
         QueryType.CHECK);
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      return getArtifactFromTypeAndAttribute(artifactType, attributeType, attributeValue, branch, QueryType.GET);
   }

   public static List<ArtifactId> selectArtifactIdsFromTypeAndName(ArtifactTypeId artifactType, String artifactName, BranchId branch, QueryOption... options) {
      return queryFromTypeAndAttribute(artifactType, CoreAttributeTypes.Name, artifactName, branch,
         options).selectArtifacts(2);
   }

   private static Artifact getArtifactFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch, QueryType queryType) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL,
         new AttributeCriteria(attributeType, attributeValue)).getOrCheckArtifact(queryType);
   }

   public static List<Artifact> getArtifactListFromTypeAndName(ArtifactTypeId artifactType, String artifactName, BranchId branch) {
      return getArtifactListFromTypeAndAttribute(artifactType, CoreAttributeTypes.Name, artifactName, branch);
   }

   public static List<Artifact> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL,
         new AttributeCriteria(attributeType, attributeValue)).getArtifacts(100, null);
   }

   /**
    * search for artifacts of the given type with an attribute of the given type and value
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch, QueryOption... options) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL,
         new AttributeCriteria(attributeType, attributeValue, options)).getArtifacts(100, null);
   }

   /**
    * search for un-deleted artifacts with any of the given artifact ids
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFrom(Collection<? extends ArtifactId> artifactIds, BranchId branch) {
      return ArtifactLoader.loadArtifacts(artifactIds, branch, LoadLevel.ALL, INCLUDE_CACHE, INCLUDE_DELETED);
   }

   public static List<Artifact> getArtifactListFrom(Collection<? extends ArtifactId> artifactIds, BranchId branch, DeletionFlag allowDeleted) {
      return ArtifactLoader.loadArtifacts(artifactIds, branch, LoadLevel.ALL, INCLUDE_CACHE, allowDeleted);
   }

   /**
    * search for artifacts with any of the given artifact guids
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromIds(List<String> guids, BranchId branch) {
      return new ArtifactQueryBuilder(guids, branch, ALL).getArtifacts(30, null);
   }

   public static List<Artifact> getArtifactListFromIds(List<String> guids, BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(guids, branch, allowDeleted, ALL).getArtifacts(30, null);
   }

   public static List<Artifact> getHistoricalArtifactListFromIds(List<String> guids, TransactionToken transactionId, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(guids, transactionId, allowDeleted, ALL).getArtifacts(30, null);
   }

   public static List<Artifact> getHistoricalArtifactListFromIds(Collection<ArtifactId> artifactIds, TransactionToken transactionId, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactIds, transactionId, allowDeleted, ALL).getArtifacts(30, null);
   }

   public static Artifact getHistoricalArtifactFromId(ArtifactId artifactId, TransactionToken transactionId, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactId, transactionId, allowDeleted, ALL).getOrCheckArtifact(QueryType.GET);
   }

   public static Artifact getHistoricalArtifactOrNull(ArtifactId artifactId, TransactionToken transactionId, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactId, transactionId, allowDeleted, ALL).getOrCheckArtifact(QueryType.CHECK);
   }

   public static Artifact getHistoricalArtifactFromId(String guid, TransactionToken transactionId, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(Arrays.asList(guid), transactionId, allowDeleted, ALL).getOrCheckArtifact(
         QueryType.GET);
   }

   public static List<Artifact> getArtifactListFromName(String artifactName, BranchId branch, DeletionFlag allowDeleted, QueryOption[] options) {
      return new ArtifactQueryBuilder(branch, ALL, allowDeleted,
         new AttributeCriteria(CoreAttributeTypes.Name, artifactName, options)).getArtifacts(30, null);
   }

   public static List<Artifact> getArtifactListFromName(String artifactName, BranchId branch) {
      return getArtifactListFromTypeAndAttribute(ArtifactTypeId.SENTINEL, CoreAttributeTypes.Name, artifactName,
         branch);
   }

   public static List<Artifact> getArtifactListFromTypeAndName(ArtifactTypeId artifactType, String artifactName, BranchId branch, QueryOption[] options) {
      return getArtifactListFromTypeAndAttribute(artifactType, CoreAttributeTypes.Name, artifactName, branch, options);
   }

   /**
    * search for exactly one artifact based on its type and an attribute of a given type and value - otherwise throw an
    * exception
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    * @throws ArtifactDoesNotExist if no artifacts are found
    * @throws MultipleArtifactsExist if more than one artifact is found
    */
   public static Artifact getArtifactFromAttribute(AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED,
         new AttributeCriteria(attributeType, attributeValue)).getOrCheckArtifact(QueryType.GET);
   }

   public static List<Artifact> getArtifactListFromType(ArtifactTypeId artifactTypeToken, BranchId branch) {
      return getArtifactListFromType(artifactTypeToken, branch, EXCLUDE_DELETED);
   }

   /**
    * Does not return any inherited artifacts. Use getArtifactListFromTypeWithInheritence instead.
    */
   public static List<Artifact> getArtifactListFromType(ArtifactTypeId artifactType, BranchId branch, DeletionFlag allowDeleted) {
      return getArtifactListFrom(getArtifactEndpoint(branch).getArtifactIdsByType(artifactType), branch, allowDeleted);
   }

   public static List<Artifact> getArtifactListFromType(List<? extends ArtifactTypeId> artifactTypeTokens, BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactTypeTokens, branch, ALL, allowDeleted).getArtifacts(1000, null);
   }

   public static List<Artifact> getArtifactListFromBranch(BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(branch, ALL, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Artifact> getArtifactListFromBranch(BranchId branch, LoadLevel loadLevel, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(branch, loadLevel, allowDeleted).getArtifacts(10000, null);
   }

   public static List<Artifact> reloadArtifactListFromBranch(BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(branch, ALL, allowDeleted).reloadArtifacts(10000);
   }

   /**
    * do not use this method if searching for a super type and its descendants, instead use getArtifactListFromTypeAnd
    */
   public static List<Artifact> getArtifactListFromTypes(Collection<? extends ArtifactTypeId> artifactTypes, BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactTypes, branch, ALL, allowDeleted).getArtifacts(1000, null);
   }

   public static List<Artifact> getArtifactListFromTypeWithInheritence(ArtifactTypeId artifactType, BranchId branch, DeletionFlag allowDeleted) {
      ArtifactType artifactTypeFull = ArtifactTypeManager.getType(artifactType);
      Collection<ArtifactType> artifactTypes = artifactTypeFull.getAllDescendantTypes();
      artifactTypes.add(artifactTypeFull);
      return getArtifactListFromTypes(artifactTypes, branch, allowDeleted);
   }

   public static int getArtifactCountFromTypeWithInheritence(ArtifactTypeId artifactType, BranchId branch, DeletionFlag allowDeleted) {
      ArtifactType artifactTypeFull = ArtifactTypeManager.getType(artifactType);
      Collection<ArtifactType> artifactTypes = artifactTypeFull.getAllDescendantTypes();
      artifactTypes.add(artifactTypeFull);
      return getArtifactCountFromTypes(artifactTypes, branch, allowDeleted);
   }

   public static int getArtifactCountFromTypes(Collection<? extends ArtifactTypeId> artifactTypes, BranchId branch, DeletionFlag allowDeleted) {
      return new ArtifactQueryBuilder(artifactTypes, branch, ALL, allowDeleted).countArtifacts();
   }

   /**
    * search for artifacts of the given type on a particular branch that satisfy the given criteria
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromTypeAnd(ArtifactTypeId artifactType, BranchId branch, int artifactCountEstimate, List<ArtifactSearchCriteria> criteria) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromCriteria(BranchId branch, int artifactCountEstimate, List<ArtifactSearchCriteria> criteria) {
      return new ArtifactQueryBuilder(branch, ALL, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts on a particular branch that satisfy the given criteria
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromCriteria(BranchId branch, int artifactCountEstimate, ArtifactSearchCriteria... criteria) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED, criteria).getArtifacts(artifactCountEstimate, null);
   }

   /**
    * search for artifacts related
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getRelatedArtifactList(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      return new ArtifactQueryBuilder(artifact.getBranch(), ALL, EXCLUDE_DELETED,
         new RelationCriteria(artifact, relationType, relationSide)).getArtifacts(1000, null);
   }

   /**
    * search for artifacts by relation
    *
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromRelation(IRelationType relationType, RelationSide relationSide, BranchId branch) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED,
         new RelationCriteria(relationType, relationSide)).getArtifacts(1000, null);
   }

   public static List<Artifact> getArtifactListFromAttribute(AttributeTypeId attributeType, String attributeValue, BranchId branch, QueryOption... options) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED,
         new AttributeCriteria(attributeType, attributeValue, options)).getArtifacts(300, null);
   }

   public static List<Artifact> getArtifactListFromAttribute(AttributeTypeId attributeType, String attributeValue, BranchId branch) {
      return getArtifactListFromTypeAndAttribute(ArtifactTypeId.SENTINEL, attributeType, attributeValue, branch);
   }

   /**
    * Return all artifacts that have one or more attributes of given type regardless of the value
    */
   public static List<Artifact> getArtifactListFromAttributeType(AttributeTypeId attributeType, BranchId branch) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED, new AttributeCriteria(attributeType)).getArtifacts(
         300, null);
   }

   private static ArtifactQueryBuilder queryFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, String attributeValue, BranchId branch, QueryOption... options) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL,
         new AttributeCriteria(attributeType, attributeValue, options));
   }

   public static List<Artifact> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, Collection<ArtifactId> attributeValues, BranchId branch) {
      Collection<String> idStrings =
         org.eclipse.osee.framework.jdk.core.util.Collections.transform(attributeValues, String::valueOf);
      return getArtifactListFromTypeAndAttribute(artifactType, attributeType, idStrings, branch, 10);
   }

   public static List<Artifact> getArtifactListFromTypeAndAttribute(ArtifactTypeId artifactType, AttributeTypeId attributeType, Collection<String> attributeValues, BranchId branch, int artifactCountEstimate) {
      return new ArtifactQueryBuilder(artifactType, branch, ALL,
         new AttributeCriteria(attributeType, attributeValues)).getArtifacts(artifactCountEstimate, null);
   }

   public static List<Artifact> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<String> attributeValues, BranchId branch, int artifactCountEstimate) {
      return new ArtifactQueryBuilder(branch, ALL, EXCLUDE_DELETED,
         new AttributeCriteria(attributeType, attributeValues)).getArtifacts(artifactCountEstimate, null);
   }

   public static List<Artifact> getArtifactListFromAttributeValues(AttributeTypeId attributeType, Collection<ArtifactId> attributeValues, BranchId branch) {
      Collection<String> idStrings =
         org.eclipse.osee.framework.jdk.core.util.Collections.transform(attributeValues, ArtifactId::getIdString);
      return getArtifactListFromAttributeValues(attributeType, idStrings, branch, 10);
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
    * order
    * @param nameOnly <b>true</b> searches in name attributes only; <b>false</b> includes all tagged attribute types
    * @param allowDeleted <b>true</b> includes deleted artifacts in results; <b>false</b> omits deleted artifacts
    * @return a collection of the artifacts found or an empty collection if none are found
    */
   public static List<Artifact> getArtifactListFromAttributeKeywords(BranchId branch, String queryString, boolean isMatchWordOrder, DeletionFlag deletionFlag, boolean isCaseSensitive, AttributeTypeId... attributeTypes) {
      QueryBuilderArtifact queryBuilder = createQueryBuilder(branch);
      queryBuilder.includeDeleted(deletionFlag.areDeletedAllowed());
      QueryOption matchCase = QueryOption.getCaseType(isCaseSensitive);
      QueryOption matchWordOrder = QueryOption.getTokenOrderType(isMatchWordOrder);
      Collection<AttributeTypeId> typesToSearch = attributeTypes.length == 0 ? Collections.singleton(
         QueryBuilder.ANY_ATTRIBUTE_TYPE) : Arrays.asList(attributeTypes);
      queryBuilder.and(typesToSearch, queryString, matchCase, matchWordOrder);
      List<Artifact> toReturn = new LinkedList<>();
      for (Artifact art : queryBuilder.getResults()) {
         toReturn.add(art);
      }
      return toReturn;
   }

   /**
    * Searches for keywords in attributes and returning match location information such as artifact where match was
    * found, attribute containing the match and match location in attribute data.
    *
    * @see #getArtifactsFromAttributeWithKeywords
    * @param findAllMatchLocations when set to <b>true</b> returns all match locations instead of just returning the
    * first one. When returning all match locations, search performance may be slow.
    */
   public static Iterable<ArtifactMatch> getArtifactMatchesFromAttributeKeywords(SearchRequest searchRequest) {
      QueryBuilderArtifact queryBuilder = createQueryBuilder(searchRequest.getBranch());
      SearchOptions options = searchRequest.getOptions();
      queryBuilder.includeDeleted(options.getDeletionFlag().areDeletedAllowed());
      QueryOption matchCase = QueryOption.getCaseType(options.isCaseSensitive());
      QueryOption matchWordOrder = QueryOption.getTokenOrderType(options.isMatchWordOrder());
      QueryOption matchExact = QueryOption.TOKEN_DELIMITER__ANY;
      if (options.isExactMatch()) {
         matchCase = QueryOption.CASE__MATCH;
         matchWordOrder = QueryOption.TOKEN_MATCH_ORDER__MATCH;
         matchExact = QueryOption.TOKEN_DELIMITER__EXACT;
      }

      Collection<AttributeTypeId> typesToSearch = Conditions.hasValues(
         options.getAttributeTypeFilter()) ? options.getAttributeTypeFilter() : Collections.singleton(
            QueryBuilder.ANY_ATTRIBUTE_TYPE);
      queryBuilder.and(typesToSearch, searchRequest.getRawSearch(), matchCase, matchWordOrder, matchExact);

      if (Conditions.hasValues(options.getArtifactTypeFilter())) {
         queryBuilder.andIsOfType(options.getArtifactTypeFilter());
      }

      return queryBuilder.getMatches();
   }

   public static Artifact reloadArtifactFromId(ArtifactId artId, BranchId branch) {
      ArtifactQueryBuilder query = new ArtifactQueryBuilder(artId, branch, INCLUDE_DELETED, ALL);
      Artifact artifact = query.reloadArtifact();
      OseeEventManager.kickLocalArtifactReloadEvent(query, Collections.singleton(artifact));
      return artifact;
   }

   public static Collection<? extends Artifact> reloadArtifacts(Collection<? extends ArtifactToken> artifacts) {
      Collection<Artifact> reloadedArts = new ArrayList<>(artifacts.size());
      HashCollection<BranchId, ArtifactToken> branchMap = new HashCollection<>();
      if (artifacts.isEmpty()) {
         return reloadedArts;
      }
      for (ArtifactToken artifact : artifacts) {
         // separate/group artifacts by branch since ArtifactQueryBuilder only supports a single branch
         branchMap.put(artifact.getBranch(), artifact);
      }
      Set<ArtifactId> artIds = new HashSet<>();
      for (Entry<BranchId, List<ArtifactToken>> entrySet : branchMap.entrySet()) {
         for (ArtifactToken artifact : entrySet.getValue()) {
            artIds.add(artifact);
         }

         ArtifactQueryBuilder query = new ArtifactQueryBuilder(artIds, entrySet.getKey(), INCLUDE_DELETED, ALL);

         reloadedArts.addAll(query.reloadArtifacts(artIds.size()));
         OseeEventManager.kickLocalArtifactReloadEvent(query, reloadedArts);
         artIds.clear();
      }
      return reloadedArts;
   }

   public static Artifact getOrCreate(String guid, ArtifactTypeId type, BranchId branch) {
      Artifact artifact = ArtifactQuery.checkArtifactFromId(guid, branch, EXCLUDE_DELETED);

      if (artifact == null) {
         artifact = ArtifactTypeManager.addArtifact(type, branch, null, guid);
      }
      if (artifact == null) {
         throw new ArtifactDoesNotExist("Artifact of type [%s] does not exist on branch [%s]", type, branch);
      }
      return artifact;
   }

   public static QueryBuilderArtifact createQueryBuilder(BranchId branch) {
      OseeClient client = ServiceUtil.getOseeClient();
      QueryBuilder builder = client.createQueryBuilder(branch);

      QueryBuilderProxy handler = new QueryBuilderProxy(builder);

      Class<?>[] types = new Class<?>[] {QueryBuilderArtifact.class};
      QueryBuilderArtifact query =
         (QueryBuilderArtifact) Proxy.newProxyInstance(QueryBuilderArtifact.class.getClassLoader(), types, handler);

      return query;
   }

   private static final class QueryBuilderProxy implements InvocationHandler {

      private final QueryBuilder proxied;

      public QueryBuilderProxy(QueryBuilder proxied) {
         super();
         this.proxied = proxied;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         Object toReturn = null;
         Method localMethod = getMethodFor(this.getClass(), method);
         try {
            if (localMethod != null) {
               toReturn = localMethod.invoke(this, args);
            } else {
               toReturn = invokeOnDelegate(proxied, method, args);
            }
         } catch (InvocationTargetException e) {
            throw e.getCause();
         }
         return toReturn;
      }

      protected Object invokeOnDelegate(Object target, Method method, Object[] args) throws Throwable {
         return method.invoke(target, args);
      }

      private Method getMethodFor(Class<?> clazz, Method method) {
         Method toReturn = null;
         try {
            toReturn = clazz.getMethod(method.getName(), method.getParameterTypes());
         } catch (Exception ex) {
            // Do Nothing;
         }
         return toReturn;
      }

      // this method is called from invoke in the localMethod case
      @SuppressWarnings("unused")
      public ResultSet<Artifact> getResults() {
         SearchResult result = proxied.getSearchResult(RequestType.IDS);
         SearchParameters searchParameters = result.getSearchParameters();

         BranchId branch = searchParameters.getBranch();

         TransactionId tx = TransactionId.SENTINEL;
         if (searchParameters.getFromTx() > 0) {
            tx = TransactionId.valueOf(searchParameters.getFromTx());
         }
         DeletionFlag deletionFlag =
            searchParameters.isIncludeDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED;

         List<ArtifactId> ids = result.getIds();
         ResultSet<Artifact> toReturn;
         if (ids != null && !ids.isEmpty()) {
            List<Artifact> loadedArtifacts =
               ArtifactLoader.loadArtifacts(ids, branch, LoadLevel.ALL, INCLUDE_CACHE, deletionFlag, tx);
            toReturn = ResultSets.newResultSet(loadedArtifacts);
         } else {
            toReturn = ResultSets.emptyResultSet();
         }
         return toReturn;
      }

      // this method is called from invoke in the localMethod case
      @SuppressWarnings("unused")
      public ResultSet<ArtifactMatch> getMatches() {
         SearchResult result = proxied.getSearchResult(RequestType.MATCHES);
         SearchParameters searchParameters = result.getSearchParameters();

         BranchId branch = searchParameters.getBranch();

         TransactionId tx = TransactionId.SENTINEL;
         if (searchParameters.getFromTx() > 0) {
            tx = TransactionId.valueOf(searchParameters.getFromTx());
         }
         DeletionFlag deletionFlag =
            searchParameters.isIncludeDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED;

         Map<Long, Artifact> artIdToArtifact = new HashMap<>();

         List<Artifact> loadedArtifacts =
            ArtifactLoader.loadArtifacts(result.getIds(), branch, LoadLevel.ALL, INCLUDE_CACHE, deletionFlag, tx);

         for (Artifact art : loadedArtifacts) {
            artIdToArtifact.put(art.getId(), art);
         }

         Map<Artifact, ArtifactMatch> matches = new HashMap<>();
         for (SearchMatch match : result.getSearchMatches()) {
            ArtifactId artId = match.getArtId();
            Artifact art = artIdToArtifact.get(artId.getId());

            if (art != null) {
               ArtifactMatch toAddTo = matches.get(art);
               if (toAddTo == null) {
                  toAddTo = new ArtifactMatch(art);
                  matches.put(art, toAddTo);
               }
               toAddTo.addMatchData(match.getAttrId(), match.getLocations());
            }
         }

         return ResultSets.newResultSet(matches.values());
      }
   }

   public static String getGuidFromUuid(long uuid, BranchId branch) {
      return getGuidFromId(uuid, branch);
   }

   public static String getGuidFromId(long uuid, BranchId branch) {
      if (uuidToGuid == null) {
         uuidToGuid = new HashMap<>(200);
      }
      String result = null;
      if (uuidToGuid.containsKey(uuid)) {
         result = uuidToGuid.get(uuid);
      } else {
         Artifact art = getArtifactFromId(uuid, branch);
         if (art != null) {
            result = art.getGuid();
            uuidToGuid.put(uuid, result);
         }
      }
      return result;
   }

   /**
    * The following methods are in support of poor running queries that are known about in 0.24.0. Significant query
    * improvements will be done for Product Line in 0.25.0. This code should be removed and it's uses move to the better
    * performing queries.
    */

   public static Collection<ArtifactToken> getArtifactTokenListFromTypeAndActive(ArtifactTypeId artifactType, AttributeTypeId activeAttrType, BranchId branch) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(getTokenQuery(Active.Active, activeAttrType), artifactType.getId(), branch.getId(),
            branch.getId(), branch.getId());
         List<ArtifactToken> tokens = extractTokensFromQuery(chStmt, branch);
         return tokens;
      } finally {
         chStmt.close();
      }
   }

   public static Collection<ArtifactToken> getArtifactTokenListFromType(ArtifactTypeId artifactType, BranchId branch) {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(getTokenQuery(Active.Both, null), artifactType.getId(), branch.getId(),
            branch.getId());
         List<ArtifactToken> tokens = extractTokensFromQuery(chStmt, branch);
         return tokens;
      } finally {
         chStmt.close();
      }
   }

   private static String getTokenQuery(Active active, AttributeTypeId activeAttrType) {
      if (active == Active.Active) {
         return tokenQuery + activeTokenQueryAdendum.replaceFirst("PUT_ACTIVE_ATTR_TYPE_HERE",
            activeAttrType.getId().toString());
      } else if (active == Active.Both) {
         return tokenQuery;
      } else {
         throw new UnsupportedOperationException("Unhandled Active case " + active);
      }
   }

   private static List<ArtifactToken> extractTokensFromQuery(JdbcStatement chStmt, BranchId branch) {
      List<ArtifactToken> tokens = new LinkedList<>();
      while (chStmt.next()) {
         Long artId = chStmt.getLong("art_id");
         ArtifactTypeId artTypeId = ArtifactTypeId.valueOf(chStmt.getLong("art_type_id"));
         String name = chStmt.getString("value");
         String guid = chStmt.getString("guid");
         ArtifactToken token = ArtifactToken.valueOf(artId, guid, name, branch, artTypeId);
         tokens.add(token);
      }
      return tokens;
   }

   private static String tokenQuery = "select art.art_id, art.art_type_id, art.guid, attr.value " + //
      "from osee_txs txsArt, osee_txs txsAttr, osee_artifact art, osee_attribute attr where art.art_type_id = ? " + //
      "and txsArt.BRANCH_ID = ? and art.GAMMA_ID = txsArt.GAMMA_ID and txsArt.TX_CURRENT = 1 " + //
      "and txsAttr.BRANCH_ID = ? and attr.GAMMA_ID = txsAttr.GAMMA_ID and txsAttr.TX_CURRENT = 1 " + //
      "and art.ART_ID = attr.art_id and attr.ATTR_TYPE_ID = " + CoreAttributeTypes.Name.getId() + " ";

   private static String activeTokenQueryAdendum =
      "and not exists (select 1 from osee_attribute attr, osee_txs txs where txs.BRANCH_ID = ? " + //
         "and txs.GAMMA_ID = attr.GAMMA_ID and attr.art_id = art.art_id " + //
         "and txs.TX_CURRENT = 1 and  attr.ATTR_TYPE_ID = PUT_ACTIVE_ATTR_TYPE_HERE and value = 'false')";

   private static String attributeTokenQuery = "select art.art_id, art.art_type_id, art.guid, attr.value " + //
      "from osee_txs txsArt, osee_txs txsAttr, osee_artifact art, osee_attribute attr where art.art_type_id in ( ART_IDS_HERE ) " + //
      "and txsArt.BRANCH_ID = ? and art.GAMMA_ID = txsArt.GAMMA_ID and txsArt.TX_CURRENT = 1 " + //
      "and txsAttr.BRANCH_ID = ? and attr.GAMMA_ID = txsAttr.GAMMA_ID and txsAttr.TX_CURRENT = 1 " + //
      "and art.ART_ID = attr.art_id and attr.ATTR_TYPE_ID = ? and value = ? ";

   private static String artifactTokenByGuidQuery =
      "select ART.ART_ID, attr.VALUE, art.art_type_id, art.guid from OSEE_ATTRIBUTE attr, OSEE_ARTIFACT art, OSEE_TXS txs where " //
         + "txs.BRANCH_ID = ? and art.GUID in ( ART_GUIDS_HERE ) and txs.TX_CURRENT = 1 and attr.ATTR_TYPE_ID = 1152921504606847088 " //
         + "and attr.ART_ID = art.ART_ID and txs.GAMMA_ID = ATTR.GAMMA_ID";

   private static String artifactTokenByArtIdQuery =
      "select ART.ART_ID, attr.VALUE, art.art_type_id, art.guid from OSEE_ATTRIBUTE attr, OSEE_ARTIFACT art, OSEE_TXS txs where " //
         + "txs.BRANCH_ID = ? and art.art_id in ( ART_IDS_HERE ) and txs.TX_CURRENT = 1 and attr.ATTR_TYPE_ID = 1152921504606847088 " //
         + "and attr.ART_ID = art.ART_ID and txs.GAMMA_ID = ATTR.GAMMA_ID";

   public static List<ArtifactToken> getArtifactTokenListFromSoleAttributeInherited(ArtifactTypeId artifactType, AttributeTypeId attributetype, String value, BranchId branch) {

      ArtifactType artifactTypeFull = ArtifactTypeManager.getType(artifactType);
      List<Long> artTypeIds = new LinkedList<>();
      String ids = "";
      for (ArtifactType artType : artifactTypeFull.getAllDescendantTypes()) {
         artTypeIds.add(artType.getId());
         ids += artType.getIdString() + ",";
      }
      artTypeIds.add(artifactTypeFull.getId());
      ids = ids.replaceFirst(",$", "");

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         String query = attributeTokenQuery.replaceFirst("ART_IDS_HERE", ids);
         chStmt.runPreparedQuery(query, branch, branch, attributetype, value);
         List<ArtifactToken> tokens = extractTokensFromQuery(chStmt, branch);
         return tokens;
      } finally {
         chStmt.close();
      }
   }

   public static HashCollection<ArtifactId, ArtifactToken> getArtifactTokenListFromRelated(BranchId branch, Collection<ArtifactId> artifacts, ArtifactTypeId artifactType, RelationTypeSide relationType) {
      List<Long> artIds = new LinkedList<>();
      String ids = "";
      for (ArtifactId art : artifacts) {
         artIds.add(art.getId());
         ids += art.getId().toString() + ",";
      }
      ids = ids.replaceFirst(",$", "");

      Map<Long, Long> artBIdToArtAId = new HashMap<>();
      Map<Long, Long> artAIdToArtBId = new HashMap<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      boolean isSideA = relationType.getSide().isSideA();
      try {
         String query = OseeSql.ARTIFACT_TO_RELATED_B_ARTIFACT_ID.getSql().replaceFirst("ART_IDS_HERE", ids);
         query = query.replaceAll("REL_SIDE_HERE", isSideA ? "b_art_id" : "a_art_id");
         query = query.replaceAll("REL_TYPE_LINKE_ID_HERE", relationType.getGuid().toString());
         query = query.replaceAll("BRANCH_ID_HERE", branch.getId().toString());
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            Long aArtId = chStmt.getLong("a_art_id");
            Long bArtId = chStmt.getLong("b_art_id");
            artBIdToArtAId.put(bArtId, aArtId);
            artAIdToArtBId.put(aArtId, bArtId);
         }
      } finally {
         chStmt.close();
      }

      chStmt = ConnectionHandler.getStatement();
      HashCollection<ArtifactId, ArtifactToken> artToRelatedTokens = new HashCollection<>();
      try {
         String query = OseeSql.ARTIFACT_TOKENS_RELATED_TO_ARTIFACT_QUERY.getSql().replaceFirst("ART_IDS_HERE", ids);
         query = query.replaceAll("OPPOSITE_REL_SIDE_HERE", isSideA ? "a_art_id" : "b_art_id");
         query = query.replaceAll("REL_SIDE_HERE", isSideA ? "b_art_id" : "a_art_id");
         query = query.replaceAll("REL_TYPE_LINKE_ID_HERE", relationType.getGuid().toString());
         query = query.replaceAll("BRANCH_ID_HERE", branch.getId().toString());
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            Long artId = chStmt.getLong("art_id");
            ArtifactTypeId artTypeId = ArtifactTypeId.valueOf(chStmt.getLong("art_type_id"));
            String name = chStmt.getString("value");
            ArtifactToken token = ArtifactToken.valueOf(artId, name, branch, artTypeId);
            Long artIdLong = isSideA ? artAIdToArtBId.get(artId) : artBIdToArtAId.get(artId);
            ArtifactId aArtId = ArtifactId.valueOf(artIdLong);
            artToRelatedTokens.put(aArtId, token);
         }
      } finally {
         chStmt.close();
      }
      return artToRelatedTokens;
   }

   public static Map<String, ArtifactToken> getArtifactTokensFromGuids(BranchId branch, List<String> guids) {
      Map<String, ArtifactToken> guidToToken = new HashMap<>();
      if (!guids.isEmpty()) {
         JdbcStatement chStmt = ConnectionHandler.getStatement();
         StringBuilder sb = new StringBuilder();
         for (String guid : guids) {
            sb.append("'");
            sb.append(guid);
            sb.append("',");
         }
         try {
            String query =
               artifactTokenByGuidQuery.replaceFirst("ART_GUIDS_HERE", sb.toString().replaceFirst(",$", ""));
            chStmt.runPreparedQuery(query, branch);
            for (ArtifactToken token : extractTokensFromQuery(chStmt, branch)) {
               guidToToken.put(token.getGuid(), token);
            }
         } finally {
            chStmt.close();
         }
      }
      return guidToToken;
   }

   public static ArtifactToken getArtifactTokenFromId(BranchId branch, ArtifactId artifactId) {
      return getArtifactEndpoint(branch).getArtifactToken(artifactId);
   }

   public static Map<String, ArtifactToken> getArtifactTokensFromIds(BranchId branch, Collection<String> artIds) {
      Map<String, ArtifactToken> guidToToken = new HashMap<>();
      if (!artIds.isEmpty()) {
         JdbcStatement chStmt = ConnectionHandler.getStatement();
         try {
            String query = artifactTokenByArtIdQuery.replaceFirst("ART_IDS_HERE",
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", artIds));
            chStmt.runPreparedQuery(query, branch);
            for (ArtifactToken token : extractTokensFromQuery(chStmt, branch)) {
               guidToToken.put(token.getIdString(), token);
            }
         } finally {
            chStmt.close();
         }
      }
      return guidToToken;
   }

   /**
    * Quick way to determine if artifact has changed. This first compares the number of current attributes against db,
    * if not equal, return true. Then compares number of current relations against db, if not equal, true.<br/>
    * <br/>
    * NOTE: This is a fast check, but could be wrong if attribute/relation was added and another deleted. Use
    * isArtifactChangedViaTransaction if you want to ensure accuracy.
    */
   public static boolean isArtifactChangedViaEntries(Artifact artifact) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      int attrCount = jdbcClient.fetch(-1,
         "select count(*) from OSEE_ATTRIBUTE attr, osee_txs txs where attr.art_id = ? and " //
            + "txs.GAMMA_ID = ATTR.GAMMA_ID and txs.BRANCH_ID = ? and txs.TX_CURRENT = 1",
         artifact, artifact.getBranch());
      if (artifact.getAttributes().size() != attrCount) {
         return true;
      }
      int relCount = jdbcClient.fetch(-1,
         "select count(*) from OSEE_RELATION_LINK rel, osee_txs txs where (rel.A_ART_ID = ? or rel.B_ART_ID = ?) " //
            + "and txs.GAMMA_ID = rel.GAMMA_ID and txs.BRANCH_ID = ? and txs.TX_CURRENT = 1",
         artifact, artifact, artifact.getBranch());
      if (!artifact.isHistorical() && artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED).size() != relCount) {
         return true;
      }
      return false;
   }

   /**
    * Long way to determine if artifact has changed. Return true if loaded transaction is different than database
    * transaction. This is 100% accurate, but may take longer to compute. Use isArtifactChangedViaEntries if just quick
    * check is desired.
    */
   public static boolean isArtifactChangedViaTransaction(Artifact artifact) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      String query =
         "select distinct transaction_id from (select txs.transaction_id from osee_artifact art, OSEE_RELATION_LINK rel, " + //
            "osee_txs txs, osee_tx_details txd where art.art_id = ? and  " + //
            // check for relation transactions
            "(art.art_id = rel.A_ART_ID or art.art_id = rel.B_ART_ID) and rel.GAMMA_ID = txs.gamma_id and txs.TRANSACTION_ID = txd.TRANSACTION_ID " + //
            "and txs.BRANCH_ID = ? " + //
            "union all " + //
            // union attribute transactions
            "select txs.transaction_id from osee_artifact art, osee_attribute attr, " + //
            "osee_txs txs, osee_tx_details txd where art.art_id = ? and  " + //
            "art.art_id = attr.art_id and attr.GAMMA_ID = txs.gamma_id and txs.TRANSACTION_ID = txd.TRANSACTION_ID " + //
            "and txs.BRANCH_ID = ?) " //
            // order by latest transaction
            + "order by transaction_id desc ";
      long transactionId = jdbcClient.fetch(-1L, query, artifact, artifact.getBranch(), artifact, artifact.getBranch());
      return !artifact.getTransaction().getId().equals(transactionId);
   }

   private static ArtifactEndpoint getArtifactEndpoint(BranchId branch) {
      return ServiceUtil.getOseeClient().getArtifactEndpoint(branch);
   }
}