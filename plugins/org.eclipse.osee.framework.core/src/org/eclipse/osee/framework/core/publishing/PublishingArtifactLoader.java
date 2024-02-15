/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.publishing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DoubleEnumMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.EnumMapHashMap;
import org.eclipse.osee.framework.jdk.core.util.ListMap;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

//@formatter:off
/**
 * Class for loading and caching {@link PublishingArtifact}s for a single publish.
 * <p>
 * Artifact applicability is determined as follows:
 * <pre>
 *                                    +---------------+-----------------+---------------------+
 *                                    | Artifact      | Artifact Branch | Artifact Reload     |
 *                                    | Applicability | Has View        | Without View Filter |
 *    +-------------------------------+---------------+-----------------+---------------------+
 *    | Artifact Loaded               | YES           | YES             | NO                  |
 *    | With View Filter              |               |                 |                     |
 *    +-------------------------------+---------------+-----------------+---------------------+
 *    | Artifact Loaded               | UNKOWN        | NO              | NO                  |
 *    | Without View Filter           |               |                 |                     |
 *    +-------------------------------+---------------+-----------------+---------------------+
 *    | Loaded Artifact               | YES/NO        | YES/NO          | NO                  |
 *    | Tested For Applicability      |               |                 |                     |
 *    +-------------------------------+---------------+-----------------+---------------------+
 *    | Artifact Not Found Marker     | NO            | YES             | NO                  |
 *    | Not Found With View Filter    |               |                 |                     |
 *    +-------------------------------+---------------+-----------------+---------------------+
 *    | Artifact Not Found Marker     | NO            | NO              | YES                 |
 *    | Not Found Without View Filter |               |                 |                     |
 *    +-------------------------------+---------------+-----------------+---------------------+
 * </pre>
 *
 * @author Loren K. Ashley
 */
//@formatter:on

public class PublishingArtifactLoader implements ToMessage {

   /**
    * A functional interface for a method that loads the artifacts that have been changed under one of the ATS Team work
    * flows that are associated with the artifact specified by <code>artifactSpecification</code> and that are
    * hierarchical descendants of one of the artifacts in <code>headerArtifacts</code>.
    */

   @FunctionalInterface
   public interface AtsTeamWorkflowLoader {

      void load(Collection<? extends PublishingArtifact> headerArtifacts, ArtifactId artifactIdentifier,
         PublishingArtifactLoader publishingArtifactLoader, BranchSpecification branchSpecification,
         Map<ArtifactId, PublishingArtifact> loadMap);
   }

   /**
    * Enumeration used to indicated whether an operation is to be performed on the product line branch or the publishing
    * branch. When the publishing branch is a product line branch both indicators will reference the publishing branch.
    * When the publishing branch is a merge branch the indicator {@link BranchIndicator#PRODUCT_LINE_BRANCH} references
    * the parent branch of the publishing branch.
    */

   public enum BranchIndicator {

      /**
       * When the publishing branch is a merge branch, indicates the operation is to be performed on the product line
       * branch; otherwise, the operation will be performed on the publishing branch.
       */

      PRODUCT_LINE_BRANCH,

      /**
       * Indicates the operation is to be performed on the publishing branch.
       */

      PUBLISHING_BRANCH;

      /**
       * Predicate to test if the enumeration member is {@link BranchIndicator#PRODUCT_LINE_BRANCH}.
       *
       * @return <code>true</code> when the member is {@link BranchIndicator#PRODUCT_LINE_BRANCH}; otherwise
       * <code>false</code>.
       */

      boolean isProductLineBranch() {
         return this == PRODUCT_LINE_BRANCH;
      }

      /**
       * Predicate to test if the enumeration member is {@link BranchIndicator#PUBLISHING_BRANCH}.
       *
       * @return <code>true</code> when the member is {@link BranchIndicator#PUBLISHING_BRANCH}; otherwise
       * <code>false</code>.
       */

      boolean isPublishingBranch() {
         return this == PUBLISHING_BRANCH;
      }
   }

   /**
    * Enumeration used to specify the cache loading behavior when requesting artifacts.
    */

   public enum CacheReadMode {

      /**
       * Only cached artifact will be returned.
       */

      CACHE_ONLY,

      /**
       * Requested artifacts that are not found in the cache will be loaded from the database.
       */

      LOAD_FROM_DATABASE;

      /**
       * Predicate to test if the enumeration member is {@link CacheReadMode#CACHE_ONLY}.
       *
       * @return <code>true</code> when the member is {@link CacheReadMode#CACHE_ONLY}; otherwise <code>false</code>.
       */

      boolean isCacheOnly() {
         return this == CACHE_ONLY;
      }

      /**
       * Predicate to test if the enumeration member is {@link CacheReadMode#LOAD_FROM_DATABASE}.
       *
       * @return <code>true</code> when the member is {@link CacheReadMode#LOAD_FROM_DATABASE}; otherwise
       * <code>false</code>.
       */

      boolean isLoadFromDatabase() {
         return this == LOAD_FROM_DATABASE;
      }
   }

   /**
    * Enumerations of {@link PublishingArtifact} cache results.
    */

   private enum CacheResult {

      /**
       * The {@link Result} contains a {@link DataAccessException} for something other than an unfound artifact.
       */

      ERROR,

      /**
       * The artifact was not found in the cache, it needs to be loaded.
       */

      LOAD,

      /*
       * A not found marker was found in the cache, don't try to load it again.
       */

      NO_LOAD,

      /**
       * The artifact was found in the cache, use it.
       */

      USE;

      /**
       * Predicate to test if the enumeration member is {@link CacheResult#USE}.
       *
       * @return <code>true</code> when the member is {@link CacheResult#USE}; otherwise <code>false</code>.
       */

      boolean isUse() {
         return this == USE;
      }
   };

   /**
    * A {@link Comparator} implementation for hierarchically sorting a {@link List} of {@link PublishingArtifacts}.
    */

   private class HierarchyComparator implements Comparator<PublishingArtifact> {

      /**
       * Creates a new {@link HierarchyComparator} instance.
       *
       * @param publishingErrorLog
       */

      public HierarchyComparator() {
      }

      /**
       * Compares the hierarchical position of two artifacts.
       *
       * @param lhsArtifact the left hand side artifact to compare.
       * @param rhsArtifact the right hand side artifact to compare.
       * @return -1 when the RHS artifact comes before the LHS artifact, 0 when the RHS artifact has the same position
       * as the LHS artifact, and 1 when the RHS artifact comes after the LHS artifact.
       */

      @Override
      public int compare(PublishingArtifact lhsArtifact, PublishingArtifact rhsArtifact) {

         var lhsHierarchyPosition = lhsArtifact.getHierarchyPosition();
         var lhsSize = lhsHierarchyPosition.size();
         var rhsHierarchyPosition = rhsArtifact.getHierarchyPosition();
         var rhsSize = rhsHierarchyPosition.size();

         for (var i = 0; i < lhsSize; i++) {

            if (i >= rhsSize) {
               return 1;
            }

            var lhsPosition = lhsHierarchyPosition.get(i);
            var rhsPosition = rhsHierarchyPosition.get(i);

            if (rhsPosition < lhsPosition) {
               return 1;
            }

            if (rhsPosition > lhsPosition) {
               return -1;
            }
         }

         if (rhsSize > lhsSize) {
            return -1;
         }

         return 0;
      }

   }

   /**
    * A functional interface for a factory method that creates a {@link PublishingArtifact} implementation from an
    * {@link ArtifactReadable} implementation.
    *
    * @implNote The class {@link PublishingArtifactLoader} is implemented to be usable by both client and server code.
    * The server and client code requires a different {@link PublishingArtifact} implementation.
    */

   @FunctionalInterface
   public interface PublishingArtifactFactoryWithoutView {

      /**
       * Factory method to create a {@link PublishingArtifact} implementation from an {@link ArtifactReadable}
       * implementation (server) or an {@link Artifact} (client) when the artifact was loaded without a view
       * applicability filter.
       *
       * @param artifact the {@link ArtifactReadable} or {@link Artifact}.
       * @return the created {@link PublishingArtifact} implementation.
       */

      PublishingArtifact apply(Object artifact);
   }

   /**
    * A functional interface for a factory method that creates a {@link PublishingArtifact} implementation from an
    * {@link ArtifactReadable} implementation when the artifact was loaded with a view applicability filter.
    *
    * @implNote The class {@link PublishingArtifactLoader} is implemented to be usable by both client and server code.
    * The server and client code requires a different {@link PublishingArtifact} implementation.
    */

   @FunctionalInterface
   public interface PublishingArtifactFactoryWithView {

      /**
       * Factory method to create a {@link PublishingArtifact} implementation from an {@link ArtifactReadable}
       * implementation (server) or an {@link Artifact} (client) when the artifact was loaded with a view applicability
       * filter.
       *
       * @param artifact the {@link ArtifactReadable} or {@link Artifact}.
       * @param branchSpecification the {@link BranchSpecification} containing the view the artifact was loaded with.
       * @return the created {@link PublishingArtifact} implementation.
       */

      PublishingArtifact apply(Object artifact, BranchSpecification branchSpecification);
   }

   /**
    * Enumeration used to specify the contents for a {@link Result} when an artifact is not found.
    */

   public enum WhenNotFound {

      /**
       * Return an empty {@link Result}.
       */

      EMPTY,

      /**
       * Return a {@link Result} with an error object.
       */

      ERROR,

      /**
       * Return a {@link Result} with a {@link PublishingArtifact.PublishingArtifactNotFound}.
       */

      NOT_FOUND,

      /**
       * Return a {@link Result} with a {@link PublishingArtifact.PublishingArtifactSentinel}.
       */

      SENTINEL;

      /**
       * Predicate to test if the enumeration member is {@link WhenNotFound#EMPTY}.
       *
       * @return <code>true</code> when the member is {@link WhenNotFound#EMPTY}; otherwise <code>false</code>.
       */

      boolean empty() {
         return this == EMPTY;
      }

      /**
       * Predicate to test if the enumeration member is {@link WhenNotFound#ERROR}.
       *
       * @return <code>true</code> when the member is {@link WhenNotFound#ERROR}; otherwise <code>false</code>.
       */

      boolean error() {
         return this == ERROR;
      }

      /**
       * Predicate to test if the enumeration member is {@link WhenNotFound#NOT_FOUND}.
       *
       * @return <code>true</code> when the member is {@link WhenNotFound#NOT_FOUND}; otherwise <code>false</code>.
       */

      boolean notFound() {
         return this == NOT_FOUND;
      }

      /**
       * Predicate to test if the enumeration member is {@link WhenNotFound#SENTINEL}.
       *
       * @return <code>true</code> when the member is {@link WhenNotFound#SENTINEL}; otherwise <code>false</code>.
       */

      boolean sentinel() {
         return this == SENTINEL;
      }
   }

   /**
    * Caches whether the {@link ApplicabilityId} is valid for the branch.
    */

   private DoubleMap<BranchIndicator, ApplicabilityId, Boolean> applicabilityMap;

   /**
    * Saves the {@link AtsTeamWorkflowLoader} implementation.
    */

   AtsTeamWorkflowLoader atsTeamWorkflowLoader;

   /**
    * Saves the publishing branch and view.
    */

   private BranchSpecification branchSpecification;

   /**
    * Organizes the {@link BranchSpecification}s by {@link BranchIndicator} and {@link FilterForView}.
    */

   private DoubleEnumMap<BranchIndicator, FilterForView, BranchSpecification> branchSpecificationMap;

   /**
    * When the publishing branch is a product line branch the {@link BranchSpecification} is the same as
    * {@link #branchSpecification}; otherwise, it is the parent branch of {@link #branchSpecification}.
    */

   private BranchSpecification branchSpecificationProductLine;

   /**
    * When the publishing branch is a product line branch this {@link BranchSpecification} is the same as
    * {@link #branchSpecificationProductLineWithoutView}; otherwise, it is the parent branch of
    * {@link #branchSpecificationProductLineWithoutView}.
    */

   private BranchSpecification branchSpecificationProductLineWithoutView;

   /**
    * Saves the publishing branch without view.
    */

   private BranchSpecification branchSpecificationWithoutView;

   /**
    * Counts the number of times the branch view is cleared from a cached artifact.
    */

   private int cacheClearBranchView;

   /**
    * Counts successful cache requests.
    */

   private int cacheHit;

   /**
    * Counts unsuccessful cache requests.
    */

   private int cacheMiss;

   /**
    * Counts the number of times a cached artifact is set applicable by the cache.
    */

   private int cacheSetApplicable;

   /**
    * Counts the number of times a cache request finds an artifact of unknown applicability that must be tested for
    * applicability before being returned.
    */

   private int cacheTest;

   /**
    * Flag to indicated that changed artifacts have been loaded by {@link #loadByTransactionComment} or by
    * {@link #loadByAtsTeamworkflow}. Changed artifacts should only be loaded once.
    */

   private boolean changedArtifactsLoaded;

   /**
    * Caches the changed artifacts from the publishing branch by {@link ArtifactId}.
    */

   private ListMap<ArtifactId, PublishingArtifact> changedArtifactsMap;

   /**
    * Counts the number of successful child cache requests.
    */

   private int childCacheHit;

   /**
    * Counts unsuccessful child cache requests.
    */

   private int childCacheMiss;

   /**
    * Cache of children artifact lists. The map is keyed by {@link BranchIndicator}, {@link ArtifactId}, and
    * {@link FilterForView}.
    */

   private RankMap<List<PublishingArtifact>> childrenCacheMap;

   /**
    * Saves a handle to the {@link DataAccessOperations} for database queries.
    */

   private final DataAccessOperations dataAccessOperations;

   /**
    * Counts the number of attempts to cache an artifact that is already cached.
    */

   private int dropCount;

   /**
    * Counts the number of get child requests.
    */

   private int getChildrenCount;

   /**
    * Counts the number of get parent requests.
    */

   private int getParentCount;

   /**
    * Counts the number of times a load artifacts by identifiers call skips an artifact because it was found in the
    * cache.
    */

   private int loadByIdCacheHitCount;

   /**
    * The maximum number of times a single load artifacts by identifiers call skips an artifact because it was not found
    * in the cache.
    */

   private int maxLoadByIdCacheHitCount;

   /**
    * The maximum number of artifacts loaded in a single database request.
    */

   private int maxLoaded;

   /**
    * The maximum number of artifacts requested in a single database request.
    */

   private int maxLoadRequest;

   /**
    * Caches {@link PublishingArtifact}s by {@link ArtifactId} for each branch.
    */

   private DoubleMap<BranchIndicator, ArtifactId, PublishingArtifact> publishingArtifactByArtifactIdentifierMap;

   /**
    * Caches {@link PublishingArtifact}s by GUID for each branch.
    */

   private DoubleMap<BranchIndicator, String, PublishingArtifact> publishingArtifactByGuidMap;

   /**
    * Saves a function implementation that produces a {@link PublishingArtifact} from an {@link ArtifactReadable}
    * implementation.
    */

   private final PublishingArtifactFactoryWithoutView publishingArtifactFactoryWithoutView;

   /**
    * Saves a bi-function implementation that produces a {@link PublishingArtifact} from an {@link ArtifactReadable}
    * implementation with the {@link BranchSpecification} it was loaded with.
    */

   private final PublishingArtifactFactoryWithView publishingArtifactFactoryWithView;

   /**
    * Saves a handle to the {@link PublishingErrorLog} for recording errors.
    */

   private final PublishingErrorLog publishingErrorLog;

   /**
    * Creates a new {@link PublishingArtifactLoader} with publisher supplied factory and service implementations. The
    * method {@link #configure} must be called after creation an before any other methods are invoked.
    *
    * @param dataAccessOperations an implementation of the {@link DataAccessOperation} interface for loading artifacts
    * from the database.
    * @param publishingErrorLog the {@link PublishingErrorLog} to report artifact specific errors to.
    * @param publishingArtifactFactoryWithoutView the {@link PublishingArtifactFactoryWithoutView} implementation to be
    * used.
    * @param publishingArtifactFactoryWithView the {@link PublishingArtifactFactoryWithView} implementation to be used
    * when the artifact was loaded with a view filter.
    * @param atsTeamWorkflowLoader the {@link AtsTeamWorkflowLoader} implementation to be used.
    */

   public PublishingArtifactLoader(DataAccessOperations dataAccessOperations, PublishingErrorLog publishingErrorLog,
      PublishingArtifactFactoryWithoutView publishingArtifactFactoryWithoutView,
      PublishingArtifactFactoryWithView publishingArtifactFactoryWithView,
      AtsTeamWorkflowLoader atsTeamWorkflowLoader) {
      this.dataAccessOperations = Objects.requireNonNull(dataAccessOperations);
      this.publishingErrorLog = Objects.requireNonNull(publishingErrorLog);
      this.publishingArtifactFactoryWithoutView = Objects.requireNonNull(publishingArtifactFactoryWithoutView);
      this.publishingArtifactFactoryWithView = Objects.requireNonNull(publishingArtifactFactoryWithView);
      this.changedArtifactsLoaded = false;
      this.atsTeamWorkflowLoader = atsTeamWorkflowLoader;
      this.cacheClearBranchView = 0;
      this.cacheHit = 0;
      this.cacheMiss = 0;
      this.cacheSetApplicable = 0;
      this.cacheTest = 0;
      this.childCacheHit = 0;
      this.childCacheMiss = 0;
      this.dropCount = 0;
      this.getChildrenCount = 0;
      this.getParentCount = 0;
      this.loadByIdCacheHitCount = 0;
      this.maxLoaded = 0;
      this.maxLoadRequest = 0;
      this.maxLoadByIdCacheHitCount = 0;
   }

   /**
    * Adds or updates a {@link PublishingArtifact} in the cache according to the following table:
    *
    * <pre>
    * +--------+-----------------+------------------+-----------------+-----------------+-----------------------------------+
    * | State  | Cached Artifact | Cached Artifact | Loaded Artifact  | Loaded Artifact | Action                            |
    * |        | Branch Has View | Is Found        |                  |                 |                                   |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     16 | null        (0) | null        (0) |  Id &  GUID (16) | no view     (0) | Add to ArtifactId and GUID caches |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     21 | has view    (1) | is found    (4) |  Id &  GUID (16) | no view     (0) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     25 | has view    (1) | not found   (8) |  Id &  GUID (16) | no view     (0) | Add to ArtifactId and GUID caches |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     22 | no view     (2) | is found    (4) |  Id &  GUID (16) | no view     (0) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     26 | no view     (2) | not found   (8) |  Id &  GUID (16) | no view     (0) | Add to ArtifactId and GUID caches |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     32 | null        (0) | null        (0) | !Id &  GUID (32) | no view     (0) | Add to GUID cache                 |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     37 | has view    (1) | is found    (4) | !Id &  GUID (32) | no view     (0) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     41 | has view    (1) | not found   (8) | !Id &  GUID (32) | no view     (0) | Remove view from cached artifact  |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     38 | no view     (2) | is found    (4) | !Id &  GUID (32) | no view     (0) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     42 | no view     (2) | not found   (8) | !Id &  GUID (32) | no view     (0) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     64 | null        (0) | null        (0) |  Id & !GUID (64) | no view     (0) | Add to ArtifactId cache           |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     69 | has view    (1) | is found    (4) |  Id & !GUID (64) | no view     (0) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     73 | has view    (1) | not found   (8) |  Id & !GUID (64) | no view     (0) | Remove view from cached artifact  |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     70 | no view     (2) | is found    (4) |  Id & !GUID (64) | no view     (0) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |     74 | no view     (2) | not found   (8) |  Id & !GUID (64) | no view     (0) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    144 | null        (0) | null        (0) |  Id &  GUID (16) | has view  (128) | Add to ArtifactId and GUID caches |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    149 | has view    (1) | is found    (4) |  Id &  GUID (16) | has view  (128) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    153 | has view    (1) | not found   (8) |  Id &  GUID (16) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    150 | no view     (2) | is found    (4) |  Id &  GUID (16) | has view  (128) | Set cached artifact as applicable |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    154 | no view     (2) | not found   (8) |  Id &  GUID (16) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    160 | null        (0) | null        (0) | !Id &  GUID (32) | has view  (128) | Add to GUID cache                 |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    165 | has view    (1) | is found    (4) | !Id &  GUID (32) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    169 | has view    (1) | not found   (8) | !Id &  GUID (32) | has view  (128) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    166 | no view     (2) | is found    (4) | !Id &  GUID (32) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    170 | no view     (2) | not found   (8) | !Id &  GUID (32) | has view  (128) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    192 | null        (0) | null        (0) |  Id & !GUID (64) | has view  (128) | Add to ArtifactId cache           |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    197 | has view    (1) | is found    (4) |  Id & !GUID (64) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    201 | has view    (1) | not found   (8) |  Id & !GUID (64) | has view  (128) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    198 | no view     (2) | is found    (4) |  Id & !GUID (64) | has view  (128) | Exception                         |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * |    202 | no view     (2) | not found   (8) |  Id & !GUID (64) | has view  (128) | drop - already cached             |
    * +--------+-----------------+-----------------+------------------+-----------------+-----------------------------------+
    * </pre>
    *
    * @param branchIndicator indicates if the <code>loadedArtifact</code> is from the
    * {@link BranchIndicator#PUBLISHING_BRANCH} or the {@link BranchIndicator#PRODUCT_LINE_BRANCH}.
    * @param artifactIdentifier the {@link ArtifactId} of the artifact to be cached or {@link ArtifactId#SENTINEL} when
    * a not found artifact is to be cached by GUID.
    * @param loadedArtifact the {@link PublishingArtifact} to be added to the cache.
    * @throws AssertionError when one of the following assertions fails:
    * <dl>
    * <dt>Parameter Checks:</dt>
    * <dd>
    * <ul>
    * <li>The <code>branchIndicator</code> is <code>null</code>.</li>
    * <li>The <code>artifactIdentifier</code> is <code>null</code>.</li>
    * <li>The <code>loadedArtifact</code> is <code>null</code>.</li>
    * <li>The <code>loadedArtifact</code> is valid or <code>loadedArtifact</code> is an instance of
    * {@link PublishingArtifact.PublishingArtifactNotFound}.</li>
    * <li>The <code>loadedArtifact</code>'s identifier is valid and does not match <code>artifactIdentifier</code>.</li>
    * <li>The <code>loadedArtifact</code>'s identifier is invalid and the GUID is invalid or blank.</li>
    * <li>The <code>loadedArtifact</code>'s artifact identifier and GUID are valid and the <code>loadedArtifact</code>
    * is not found.</li>
    * <li>The <code>loadedArtifact</code>'s artifact identifier or the GUID are invalid and the
    * <code>loadedArtifact</code> is found.</li>
    * <li>The <code>loadedArtifact</code> is valid and the <code>loadedArtifact</code> is not from the branch specified
    * by <code>branchIndicator</code>.
    * </ul>
    * </dd>
    * <dt>Cache Consistency Checks:</dt>
    * <dd>
    * <ul>
    * <li>When the <code>loadedArtifact</code>'s identifier and GUID are both valid and the cache entry by artifact
    * identifier does not equal the cache entry by GUID.</li>
    * <li>When a cached artifact is found by either the <code>artifactIdentifier</code> or the
    * <code>loadedArtifact</code>'s GUID and the branch of the cached artifact does not match the specified by
    * <code>branchIndicator</code>.</li>
    * </ul>
    * </dd>
    * <dt>State Checks:</dt>
    * <dd>
    * <dl>
    * <dt>Add To ArtifactId And GUID Caches (16,25,26,144):</dt>
    * <dd>
    * <ul>
    * <li>The <code>loadedArtifact</code> has an invalid artifact identifier.</li>
    * <li>The <code>loadedArtifact</code> has an invalid GUID.</li>
    * <li>The <code>loadedArtifact</code> is a not found marker.</li>
    * </ul>
    * </dd>
    * <dt>Add To ArtifactId Cache (64,192):</dt>
    * <dd>
    * <ul>
    * <li>The <code>loadedArtifact</code> has an invalid artifact identifier.</li>
    * <li>The <code>loadedArtifact</code> has a valid GUID.</li>
    * <li>The <code>loadedArtifact</code> is not a not found marker.</li>
    * </ul>
    * </dd>
    * <dt>Add To GUID Cache (32,160):</dt>
    * <dd>
    * <ul>
    * <li>The <code>loadedArtifact</code> has valid artifact identifier.</li>
    * <li>The <code>loadedArtifact</code> has an invalid GUID.</li>
    * <li>The <code>loadedArtifact</code> is not a not found marker.</li>
    * </ul>
    * </dd>
    * <dt>Remove View From Cached Artifact (41,73):</dt>
    * <dd>
    * <ul>
    * <li>The cached artifact is <code>null</code>.</li>
    * <li>The cached artifact has a valid artifact identifier and a valid GUID.</li>
    * <li>The cached artifact is not a not found marker.</li>
    * </ul>
    * </dd>
    * <dt>Set Cached Artifact As Applicable (150):</dt>
    * <dd>
    * <ul>
    * <li>The cached artifact is <code>null</code>.</li>
    * <li>The cached artifact has an invalid artifact identifier.</li>
    * <li>The cached artifact has an invalid GUID.</li>
    * <li>The cached artifact is a not found marker.</li>
    * <li>The cached artifact's identifier does not match the <code>loadedArtifact</code>'s identifier.</li>
    * <li>The cached artifact's GUID does not match the <code>loadArtifact</code>'s GUID.</li>
    * </ul>
    * </dd>
    * <dt>Drop (21,22,42,74,149,169,170,201,202):</dt>
    * <dd>
    * <ul>
    * <li>The cached artifact is <code>null</code>.</li>
    * <li>The cached artifact identifier does not match the loaded <code>loadedArtifact</code>'s identifier.</li>
    * <li>The cached artifact GUID does not match the loaded <code>loadedArtifact</code>'s GUID.</li>
    * <li>The cached artifacts is found flag does not match the <code>loadedArtifact</code>'s is found flag.</li>
    * <li>The cached artifact has a view, the cached artifact is found, <code>loadedArtifact</code> is found, and the
    * <code>loadedArtifact</code> has a view.</li>
    * </ul>
    * </dd>
    * </dl>
    * </dd>
    * </dl>
    * @throws IllegalStateException when an unexpected state was encountered. This exception is expected to never be
    * thrown.
    * @implNote {@link PublishingArtifact} implementations being cached must support the following methods:
    * <ul>
    * <li>{@link PublishingArtifact#branchHasView},</li>
    * <li>{@link PublishingArtifact#clearBranchView},</li>
    * <li>{@link PublishingArtifact#getId},</li>
    * <li>{@link PublishingArtifact#getGuid},</li>
    * <li>{@link PublishingArtifact#isBookmarked},</li>
    * <li>{@link PublishingArtifact#isFound},</li>
    * <li>{@link PublishingArtifact#isInvalid},</li>
    * <li>{@link PublishingArtifact#isValid}, and</li>
    * <li>{@link PublishingArtifact#setApplicable}.</li>
    * </ul>
    */

   private void cacheIt(BranchIndicator branchIndicator, ArtifactId artifactIdentifier,
      PublishingArtifact loadedArtifact) {

      /*
       * Parameter Checks
       */
      //@formatter:off
      assert
              Objects.nonNull( branchIndicator )
           && Objects.nonNull( artifactIdentifier )
           && Objects.nonNull( loadedArtifact     )
           && ( loadedArtifact.isValid() || (loadedArtifact instanceof PublishingArtifact.PublishingArtifactNotFound) )
           && ( loadedArtifact.isValid()
                   ? ( loadedArtifact.getId().equals( artifactIdentifier.getId() ) )
                   : Strings.isValidAndNonBlank( loadedArtifact.getGuid() ) )
           && ( loadedArtifact.getArtifactId().isValid() && Strings.isValidAndNonBlank( loadedArtifact.getGuid() )
                 ? loadedArtifact.isFound()
                 : !loadedArtifact.isFound() )
           && (    loadedArtifact.isInvalid()
                || loadedArtifact.getBranch().isSameBranch( this.getBranchSpecification( branchIndicator, FilterForView.NO ).getBranchId() ) )
         : new Message()
                  .title( "PublishingArtifactLoader::cacheIt, invalid input parameters." )
                  .indentInc()
                  .segment( "Artifact Identifier",      artifactIdentifier )
                  .segment( "Loaded Artifact",          loadedArtifact     )
                  .segment( "Loaded Artifact Is Found", loadedArtifact.isFound() )
                  .segment( "Loaded Artiact Has View",  loadedArtifact.branchHasView() )
                  .toString();

      var guid = loadedArtifact.getGuid();

      var artifactCachedByArtifactIdentifier = this.publishingArtifactByArtifactIdentifierMap.get( branchIndicator, artifactIdentifier ).orElse( null );
      var artifactCachedByGuid = this.publishingArtifactByGuidMap.get( branchIndicator, guid ).orElse( null );

      /*
       * Cache Consistency Checks
       */

      assert
              Objects.isNull( artifactCachedByArtifactIdentifier )
           || Objects.isNull( artifactCachedByGuid )
           || !artifactCachedByArtifactIdentifier.isFound()
           || !artifactCachedByGuid.isFound()
           || ( artifactCachedByArtifactIdentifier == artifactCachedByGuid )
         : new Message()
                  .title( "PublishingArtifactLoader::cacheIt, artifact identifier and GUID cache entries dont' match." )
                  .indentInc()
                  .segment( "Branch Indicator",    branchIndicator    )
                  .segment( "Artifact Identifier", artifactIdentifier )
                  .segment( "Artifact GUID",       guid               )
                  .segmentToMessage( "Artifact Identifier Cache", artifactCachedByArtifactIdentifier )
                  .segmentToMessage( "GUID Cache",                artifactCachedByGuid               )
                  .segmentToMessage( "Loaded Artifact",           loadedArtifact                     )
                  .toString();

      var cachedArtifact =
         artifactIdentifier.isValid()
            ? artifactCachedByArtifactIdentifier
            : artifactCachedByGuid;

      assert
              Objects.isNull( cachedArtifact )
           || cachedArtifact.getBranch().isSameBranch( this.getBranchSpecification( branchIndicator, FilterForView.NO ).getBranchId() )
         : new Message()
                  .title( "PublishingArtifactLoader::cacheIt, cached artifact branch does not match the parameter \"branchIdentifier." )
                  .indentInc()
                  .segment( "Branch Indicator",                  branchIndicator                    )
                  .segment( "Cached Artifact Branch Identifier", cachedArtifact.getBranchIdString() )
                  .toString();

      var state =
           ( Objects.nonNull( cachedArtifact ) &&  cachedArtifact.branchHasView() ?   1 : 0 )
         + ( Objects.nonNull( cachedArtifact ) && !cachedArtifact.branchHasView() ?   2 : 0 )
         + ( Objects.nonNull( cachedArtifact ) &&  cachedArtifact.isFound()       ?   4 : 0 )
         + ( Objects.nonNull( cachedArtifact ) && !cachedArtifact.isFound()       ?   8 : 0 )
         + ( loadedArtifact.isValid()   && Strings.isValidAndNonBlank( guid )     ?  16 : 0 )
         + ( loadedArtifact.isInvalid() && Strings.isValidAndNonBlank( guid )     ?  32 : 0 )
         + ( loadedArtifact.isValid()   && Strings.isInvalidOrBlank( guid )       ?  64 : 0 )
         + ( loadedArtifact.branchHasView()                                       ? 128 : 0 )
         ;

      switch (state) {
       /*
        * Action -> Add to ArtifactId and GUID caches
        */
         case  16:
         case  25:
         case  26:
         case 144:
         {
            assert
                    ( Objects.nonNull( loadedArtifact.getArtifactId() ) && loadedArtifact.getArtifactId().isValid() )
                 && Strings.isValidAndNonBlank( loadedArtifact.getGuid() )
                 && loadedArtifact.isFound()
               : this.cacheItErrorMessage
                    (
                       "artifact to be cached by ArtifactId and GUID has unexpected state.",
                       "Add To ArtifactId And GUID Caches",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            this.publishingArtifactByArtifactIdentifierMap.put( branchIndicator, artifactIdentifier, loadedArtifact);

            this.publishingArtifactByGuidMap.put( branchIndicator, guid, loadedArtifact);

            if( loadedArtifact.isOfType( CoreArtifactTypes.HeadingMsWord ) ) {

               this.publishingArtifactByArtifactIdentifierMap.put( branchIndicator, artifactIdentifier, loadedArtifact );

               this.publishingArtifactByGuidMap.put( branchIndicator, guid, loadedArtifact );
            }

            return;
         }
         /*
          * Action -> Add to ArtifactId cache
          */
         case  64:
         case 192:
         {
            assert
                    ( Objects.nonNull( loadedArtifact.getArtifactId() ) && loadedArtifact.getArtifactId().isValid() )
                 && Strings.isInvalidOrBlank( loadedArtifact.getGuid() )
                 && !loadedArtifact.isFound()
               : this.cacheItErrorMessage
                    (
                       "artifact to be cached by ArtifactId only has unexpected state.",
                       "Add To ArtifactId Cache",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            this.publishingArtifactByArtifactIdentifierMap.put( branchIndicator, artifactIdentifier, loadedArtifact);

            return;
         }
         /*
          * Action -> Add to GUID cache
          */
         case  32:
         case 160:
         {
            assert
                    ( Objects.isNull( loadedArtifact.getArtifactId() ) || loadedArtifact.getArtifactId().isInvalid() )
                 && Strings.isValidAndNonBlank( loadedArtifact.getGuid() )
                 && !loadedArtifact.isFound()
               : this.cacheItErrorMessage
                    (
                       "artifact to be cached by GUID only has unexpected state.",
                       "Add To GUID Cache",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            this.publishingArtifactByGuidMap.put( branchIndicator, guid, loadedArtifact );

            return;
         }
         /*
          * Action -> Remove view from cached artifact
          */
         case  41:
         case  73:
         {
            assert
                    Objects.nonNull( cachedArtifact )
                 && ( cachedArtifact.isInvalid() || Strings.isInvalidOrBlank( cachedArtifact.getGuid() ) )
                 && !cachedArtifact.isFound()
               : this.cacheItErrorMessage
                    (
                       "cached artifact is not a not found marker.",
                       "Remove View From Cached Artifact",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            cachedArtifact.clearBranchView();
            this.cacheClearBranchView++;
            return;
         }
         /*
          * Action -> Set cached artifact as applicable
          */
         case 150:
         {
            assert
                    Objects.nonNull( cachedArtifact )
                 && cachedArtifact.getArtifactId().isValid()
                 && Strings.isValidAndNonBlank( cachedArtifact.getGuid() )
                 && cachedArtifact.isFound()
                 && ( cachedArtifact.getArtifactId().getId().equals( loadedArtifact.getArtifactId().getId() ) )
                 && ( cachedArtifact.getGuid().equals( loadedArtifact.getGuid() ) )
               : this.cacheItErrorMessage
                    (
                       "cached artifact to be set applicable is in an unexpected state.",
                       "Set Cached Artifact As Applicable",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            cachedArtifact.setApplicable();
            this.cacheSetApplicable++;
            return;
         }
         /*
          * Action -> Drop
          */
         case  21:
         case  22:
         case  42:
         case  74:
         case 149:
         case 169:
         case 170:
         case 201:
         case 202:
         {
            assert
                    Objects.nonNull( cachedArtifact )
                 && ( cachedArtifact.getId().equals( loadedArtifact.getId() ) )
                 && ( cachedArtifact.getGuid().equals( loadedArtifact.getGuid() ) )
                 && ( cachedArtifact.isFound() == loadedArtifact.isFound() )
                 && (  !cachedArtifact.branchHasView() || cachedArtifact.isFound() || loadedArtifact.isFound() || loadedArtifact.branchHasView() )

               : this.cacheItErrorMessage
                    (
                       "dropped artifact vs cached artifact has unexpected differences.",
                       "Drop",
                       state,
                       branchIndicator,
                       cachedArtifact,
                       loadedArtifact
                    );

            this.dropCount++;
            return;
         }
         /*
          * Exception
          */
         case  37:
         case  38:
         case  69:
         case  70:
         case 153:
         case 154:
         case 165:
         case 166:
         case 197:
         case 198:
         {
            throw
               new OseeCoreException
                      (
                         this.cacheItErrorMessage
                            (
                               "loaded artifact conflicts with cached artifact.",
                               "Exception",
                               state,
                               branchIndicator,
                               cachedArtifact,
                               loadedArtifact
                            )
                      );
         }
         /*
          * Unexpected State
          */
         default: {

            throw
               new IllegalStateException
                      (
                         this.cacheItErrorMessage
                         (
                            "invalid state.",
                            "Unexpected State",
                            state,
                            branchIndicator,
                            cachedArtifact,
                            loadedArtifact
                         )
                      );
         }

      }
      //@formatter:on
   }

   /**
    * Generates error message strings for the method {@link PublishingArtifactLoader#cacheIt}.
    *
    * @param title the title summary for the error.
    * @param action the cache action where the error occurred.
    * @param state the cache state.
    * @param branchIndicator indicates if the <code>loadedArtifact</code> is from the
    * {@link BranchIndicator#PUBLISHING_BRANCH} or the {@link BranchIndicator#PRODUCT_LINE_BRANCH}.
    * @param cachedArtifact the cached artifact. This parameter may be <code>null</code>.
    * @param loadedArtifact the artifact to be cached. This parameter may be <code>null</code>.
    * @return a string containing the generate error message.
    */

   String cacheItErrorMessage(String title, String action, int state, BranchIndicator branchIndicator,
      PublishingArtifact cachedArtifact, PublishingArtifact loadedArtifact) {
      //@formatter:off
      var message =
         new Message()
            .title( "PublishingArtifactLoader::cacheIt, " )
            .append( title )
            .indentInc()
            .segment( "Action",           action          )
            .segment( "State",            state           )
            .segment( "Branch Indicator", branchIndicator )
            .title( "Cached Artifact" )
            .indentInc()
            ;

      if( Objects.nonNull( cachedArtifact ) ) {
         message
            .segment( "Artifact Identifier", cachedArtifact.getIdString()   )
            .segment( "Artifact GUID",       cachedArtifact.getGuid()       )
            .segment( "If Found",            cachedArtifact.isFound()       )
            .segment( "branchHasView",       cachedArtifact.branchHasView() )
            .segment( "Is Applicable",       cachedArtifact.isApplicable()  )
            ;
      } else {
         message
            .title( "(null)" )
            ;
      }

      message
            .indentDec()
            .title( "Loaded Artifact" )
            .indentInc()
            ;

      if( Objects.nonNull( loadedArtifact ) ) {
         message
            .segment( "Artifact Identifier", loadedArtifact.getIdString()   )
            .segment( "Artifact GUID",       loadedArtifact.getGuid()       )
            .segment( "If Found",            loadedArtifact.isFound()       )
            .segment( "branchHasView",       loadedArtifact.branchHasView() )
            .segment( "Is Applicable",       loadedArtifact.isApplicable()  )
            ;
      } else {
         message
            .title( "(null)" )
            ;
      }

      //@formatter:on
      return message.toString();
   }

   /**
    * Checks the applicability of an artifact to the publishing branch.
    *
    * @param artifact the {@link ArtifactReadable} to check for applicability.
    * @return <code>true</code> when the artifact is applicable to the publishing view or there is no publishing view;
    * otherwise, <code>false</code>.
    * @implNote Current method relies on a map between applicability identifiers and a true false to the set branch. If
    * the applicability has been processed, the map is relied upon. If not, it is checked to see whether or not it
    * contains a valid view.
    * @throws OseeCoreException when getting the applicable views for the publishing branch fails.
    */

   private boolean checkIsArtifactApplicable(BranchIndicator branchIndicator, ArtifactReadable artifact) {

      //@formatter:off
      assert
           this.branchSpecification.hasView()
         : "PublishingArtifactLoader::checkIsArtifactApplicable, expected configured branch specification to have a view.";
      //@formatter:on

      var applicabilityId = artifact.getApplicability();

      //@formatter:off
      return
         this.applicabilityMap.get( branchIndicator, applicabilityId )
            .or
               (
                  () ->
                  {
                     return
                        this.dataAccessOperations
                           .getBranchViewsForApplicability( this.branchSpecification, applicabilityId )
                           .mapValue( ( validViews ) -> validViews.contains( this.branchSpecification.getViewId() ) )
                           .peekValue( ( isIncluded ) -> this.applicabilityMap.put( branchIndicator, applicabilityId, isIncluded ) )
                           .getAsOptionalOrElseThrow
                              (
                                 ( exception ) ->
                                 {
                                    throw
                                       new OseeCoreException
                                              (
                                                 new Message()
                                                        .title( "WordTemplateProcessorServer::checkIsArtifactApplicable, failed to get applicable views for branch." )
                                                        .indentInc()
                                                        .reasonFollows( exception )
                                                        .segment( "Branch Identifier",      this.branchSpecification.getBranchId() )
                                                        .segment( "Branch View",            this.branchSpecification.getViewId()   )
                                                        .segment( "Artifact Identifier",    artifact.getIdString()            )
                                                        .segment( "Artifact Applicability", applicabilityId                   )
                                                        .toString(),
                                                 exception
                                              );
                                 }
                              );
                  }
               )
            .get();
      //@formatter:on
   }

   /**
    * Clears the is processed flag for all {@link PublishingArtifact}s in the cache.
    */

   public void clearProcessedFlags() {
      //@formatter:off
      this.publishingArtifactByArtifactIdentifierMap
         .values()
         .stream()
         .filter( PublishingArtifact::isFound )
         .forEach( PublishingArtifact::clearProcessed );
      //@formatter:on
   }

   /**
    * Sets the branch with optional view that {@link PublishingArtifact}s will be loaded from. The
    * {@link PublishingArtifactLoader} must be configured before any artifacts can be loaded.
    *
    * @param branchSpecification the branch and optional view the publish is being made from.
    * @param estimateArtifactCount an estimate of the number of artifacts in the publish.
    * @return the {@link PublishingArtifactLoader}.
    * @throws NullPointerException when <code>branchSpecification<code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>branchSpecification</code> is invalid or <code>initialCapacity</code>
    * is less than one.
    * @throws IllegalStateException when the {@link PublishingArtifactLoader} has already been configured.
    * @throws OseeCoreException when determination of the product line branch fails.
    */

   public PublishingArtifactLoader configure(BranchSpecification branchSpecification, int estimatedArtifactCount) {

      /*
       * this.branchSpecification is used as a sentinel to determine if the {@link PublishingArtifactLoader} has already
       * been configured.
       */

      if (Objects.nonNull(this.branchSpecification)) {
         throw new IllegalStateException("PublishingArtifactLoader::configure, already configured.");
      }

      Objects.requireNonNull(branchSpecification,
         "PublishingArtifactLoader::configure, the parameter \"branchSpecification\" cannot be null.");

      if (branchSpecification.getBranchId().isInvalid()) {
         throw new IllegalArgumentException(
            "PublishingArtifactLoader::configure, the parameter \"branchSpecification\" is not valid.");
      }

      if (estimatedArtifactCount < 1) {
         throw new IllegalArgumentException(
            "PublishingArtifactLoader::configure, the parameter \"initialCapacity\" must be greater than zero.");
      }

      var initialCapacity = estimatedArtifactCount * 8;

      this.applicabilityMap = new EnumMapHashMap<>(BranchIndicator.class, initialCapacity, 0.75f);

      this.branchSpecification = branchSpecification;

      //@formatter:off
      this.branchSpecificationWithoutView =
         this.branchSpecification.hasView()
            ? new BranchSpecification( BranchId.valueOf( branchSpecification.getBranchId().getId() ) )
            : this.branchSpecification;
      //@formatter:on

      //@formatter:off
      this.branchSpecificationProductLineWithoutView =
         this
            .getProductLineBranch()
            .orElseThrow
               (
                  ( dataAccessException ) -> new DataAccessException
                                                    (
                                                       new Message()
                                                              .title( "PublishingArtifactLoader::configure, failed to determine the product line branch." )
                                                              .indentInc()
                                                              .segment( "Branch Specification", branchSpecification )
                                                              .reasonFollows( dataAccessException )
                                                              .toString(),
                                                       dataAccessException.getPublishingUtilCause()
                                                    )
               );
      //@formatter:on

      //@formatter:off
      this.branchSpecificationProductLine =
         new BranchSpecification( this.branchSpecificationProductLineWithoutView.getBranchId(), this.branchSpecification.getViewId() );

      this.branchSpecificationMap = new DoubleEnumMap<>(BranchIndicator.class, FilterForView.class);

      this.branchSpecificationMap.put( BranchIndicator.PRODUCT_LINE_BRANCH, FilterForView.YES, this.branchSpecificationProductLine            );
      this.branchSpecificationMap.put( BranchIndicator.PRODUCT_LINE_BRANCH, FilterForView.NO,  this.branchSpecificationProductLineWithoutView );
      this.branchSpecificationMap.put( BranchIndicator.PUBLISHING_BRANCH,   FilterForView.YES, this.branchSpecification                       );
      this.branchSpecificationMap.put( BranchIndicator.PUBLISHING_BRANCH,   FilterForView.NO,  this.branchSpecificationWithoutView            );
      //@formatter:on

      this.changedArtifactsMap = new ListMap<>();

      //@formatter:off
      @SuppressWarnings("unchecked")
      Predicate<Object>[] predicates =
         new Predicate[]
         {
            ( key ) -> key instanceof BranchIndicator,
            ( key ) -> key instanceof ArtifactId,
            ( key ) -> key instanceof FilterForView
         };

      this.childrenCacheMap =
         new RankHashMap<>
                (
                   "Children Cache Map",
                   3,
                   initialCapacity,
                   0.75f,
                   predicates
                );
      //@formatter:on

      this.publishingArtifactByArtifactIdentifierMap =
         new EnumMapHashMap<>(BranchIndicator.class, initialCapacity, 0.75f);

      this.publishingArtifactByGuidMap = new EnumMapHashMap<>(BranchIndicator.class, initialCapacity, 0.75f);

      return this;
   }

   /**
    * Finds the descendants of an artifact with the specified artifact type and an attribute with a specified value.
    *
    * @param rootArtifactIdentifier the {@link ArtifactId} of the artifact to get the descendants of.
    * @param childArtifactTypeToken only descendants of this artifact type will be included.
    * @param childAttributeTypeId only descendants with an attribute of this type and <code>attributeValue</code> will
    * be included.
    * @param attributeValue only descendants with an attribute type of <code>childAttributeTypeId</code> with this value
    * will be included.
    * @param filterForView when {@link FilterForView#YES} only artifact that are applicable will be included.
    * @param processRecursively when {@link ProcessRecursively#YES} all hierarchical descendants meeting the selection
    * criteria will be included; otherwise, only immediate children will be included.
    * @return on success a {@link Result} containing a {@link List} of the {@link ArtifactId}s of the descendants
    * meeting the selection criteria; otherwise, a {@link Result} with a {@link DataAccessException}.
    */

   private Result<List<ArtifactId>, DataAccessException> dataAccessLoadChildrenArtifactIdentifiers(
      ArtifactId rootArtifactIdentifier, ArtifactTypeToken childArtifactTypeToken, AttributeTypeId childAttributeTypeId,
      String attributeValue, FilterForView filterForView, ProcessRecursively processRecursively) {

      this.maxLoadRequest = (this.maxLoadRequest <= 0) ? 1 : this.maxLoadRequest;

      var branchSpecification = this.getBranchSpecification(BranchIndicator.PUBLISHING_BRANCH, filterForView);

      //@formatter:off
      var result =
         this.dataAccessOperations
            .getChildrenArtifactIdentifiers
               (
                  branchSpecification,
                  rootArtifactIdentifier,
                  childArtifactTypeToken,
                  childAttributeTypeId,
                  attributeValue,
                  processRecursively
               );
      //@formatter:on

      if (result.isPresentValue()) {
         var loaded = result.getValue().size();
         this.maxLoaded = (this.maxLoaded < loaded) ? loaded : this.maxLoaded;
      }

      return result;
   }

   /**
    * Loads the publishing {@link Branch} and when the publishing branch is a merge branch the product line
    * {@link Branch} is also loaded.
    *
    * @return when the publishing branch is a merge branch a {@link Result} with the product line {@link Branch}, when
    * the publishing branch is not a merge branch a {@link Result} with the publishing {@link Branch}; otherwise, a
    * {@link Result} with a {@link DataAccessException}.
    */

   private Result<BranchId, DataAccessException> dataAccessLoadProductLineBranch() {

      //@formatter:off
      var result =
         this.dataAccessOperations
            .getBranchByIdentifier( this.branchSpecificationWithoutView.getBranchId() )
            .mapValue
               (
                  ( branch )            -> branch.getBranchType().equals( BranchType.MERGE )
                                              ? branch.getParentBranch()
                                              : branch,
                  ( branch, throwable ) -> new DataAccessException
                                                  (
                                                     new Message()
                                                            .title( "PublishingArtifactLoader::dataAccessLoadProductLineBranch failed to determine the product line branch." )
                                                            .indentInc()
                                                            .segment( "Publishing Branch", this.branchSpecification )
                                                            .reasonFollows( throwable )
                                                            .toString(),
                                                     ( (DataAccessException) throwable ).getPublishingUtilCause()
                                                  )
               );
      //@formatter:on
      return result;
   }

   /**
    * When the publishing branch is a merge branch the applicability named view map is loaded from the product line
    * branch; otherwise, the applicability named view map is loaded from the publishing branch.
    *
    * @return on error a {@link Result} with a {@link DataAccessException}; otherwise a {@link Result} with a
    * {@link Triplet} containing the following:
    * <dl>
    * <dt>First:</dt>
    * <dd>When the publishing branch is a merge branch the {@link BranchId} of the product line branch; otherwise, the
    * {@link BranchId} of the publishing branch.</dd>
    * <dt>Second:</dt>
    * <dd>The {@link ArtifactToken} for the publishing view from the branch who's identifier is contained in
    * "First".</dd>
    * <dt>Third:</dt>
    * <dd>Product Line View Applicabilities Map</dd>
    * </dl>
    */

   private Result<Triplet<BranchId, ArtifactToken, Map<String, List<String>>>, DataAccessException> dataAccessLoadProductLineViewApplicabilitiesMap() {

      //@formatter:off
      var result =
         this
            .getProductLineBranch()
            .mapValue
               (
                  ( productLineBranchSpecification ) ->
                  {
                     if( !productLineBranchSpecification.hasView() ) {
                        return
                           new Triplet<>
                                  (
                                     productLineBranchSpecification.getBranchId(),
                                     ArtifactToken.SENTINEL,
                                     (Map<String,List<String>>) null
                                  );
                     }

                     var view =
                        this.dataAccessOperations
                           .getArtifactTokens
                              (
                                 productLineBranchSpecification,
                                 List.of( this.branchSpecification.getViewId() ),
                                 ArtifactTypeToken.SENTINEL,
                                 RelationTypeSide.SENTINEL
                              )
                           .orElseGet( List.of( ArtifactToken.SENTINEL ) )
                           .get( 0 );

                     var viewApplicabilitesMap =
                        view.isValid()
                           ? this.dataAccessOperations
                                .getApplicabilityNamedViewMap( productLineBranchSpecification )
                                .orElseGet( new HashMap<>() )
                           : new HashMap<String,List<String>>();

                     return new Triplet<>( productLineBranchSpecification.getBranchId(), view, viewApplicabilitesMap );
                  },
                  ( productLineBranchSpecification, throwable ) ->
                  {
                     if( throwable instanceof DataAccessException ) {
                        return (DataAccessException) throwable;
                     }

                     return
                        new DataAccessException
                               (
                                  new Message()
                                         .title( "PublishingArtifactLoader::dataAccessLoadProductLineViewApplicabilityMap, failed to load map." )
                                         .segment( "Product Line Branch", productLineBranchSpecification )
                                         .indentInc()
                                         .reasonFollows( throwable )
                                         .toString(),
                                  Cause.ERROR,
                                  throwable
                               );
                  }
            );
      //@formatter:on
      return result;
   }

   /**
    * Loads the artifacts from the specified branch with filters for artifact identifier, guid, and transaction from the
    * database into a {@link List} of {@link PublishingArtifact}s.
    * <p>
    * The applicability of the returned {@link PublishingArtifact} will be set as follows:
    *
    * <pre>
    *    +---------------+------------------------+---------------+
    *    | filterForView | branchSpecification    | Artifact      |
    *    |               | from branchIndicator & | Applicability |
    *    |               | filterForView has a    |               |
    *    |               | view                   |               |
    *    +---------------+------------------------+---------------+
    *    | YES           | YES                    | YES           |
    *    +---------------+------------------------+---------------+
    *    | NO            | YES                    | *A            |
    *    +---------------+------------------------+---------------+
    *    | YES           | NO                     | UNKNOWN       |
    *    +---------------+------------------------+---------------+
    *    | NO            | NO                     | UNKNOWN       |
    *    +---------------+------------------------+---------------+
    * </pre>
    *
    * Note A: This combination is not applicable. When <code>filterForView</code> is {@link FilterForView#NO} the
    * {@link BranchSpecification} selected will never contain a view.
    * <p>
    *
    * @param branchIndicator indicates whether to load the artifact from the publishing or product line branch.
    * @param artifactIdentifiers a {@link List} of the {@link ArtifactId}s of the artifact to be loaded.
    * @param artifactGuids a {@link List} of the GUIDs of the artifact to be loaded.
    * @param filterForView when {@link FilterForView#YES} the results will be filtered for view applicability.
    * @param transactionId
    * @param includeDeleted indicates whether or not to also load deleted artifacts.
    * @return when the database load was successful a {@link Result} containing a {@link List} of the loaded artifacts;
    * otherwise, a {@link Result} containing a {@link DataAccessException}.
    */

   private Result<? extends List<PublishingArtifact>, DataAccessException> dataAccessLoadPublishingArtifacts(
      BranchIndicator branchIndicator, Collection<ArtifactId> artifactIdentifiers, Collection<String> guids,
      FilterForView filterForView, ArtifactTypeToken artifactTypeToken, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      this.maxLoadRequest =
         (this.maxLoadRequest < artifactIdentifiers.size()) ? artifactIdentifiers.size() : this.maxLoadRequest;

      var branchSpecification = this.getBranchSpecification(branchIndicator, filterForView);

      //@formatter:off
      var result =
         this.dataAccessOperations
            .getArtifactReadables
               (
                  branchSpecification,
                  artifactIdentifiers,
                  guids,
                  Strings.EMPTY_STRING,
                  artifactTypeToken,
                  transactionId,
                  includeDeleted
               )
            .mapValue
               (
                  ( artifactReadables ) -> artifactReadables
                                              .stream()
                                              .map
                                                 (
                                                    ( artifact ) -> branchSpecification.hasView()
                                                                       ? this.publishingArtifactFactoryWithView.apply( artifact, branchSpecification )
                                                                       : this.publishingArtifactFactoryWithoutView.apply( artifact )
                                                 )
                                              .collect
                                                 (
                                                    Collectors.toList()
                                                 ),
                  ( artifactReadables, throwable ) ->
                  {
                     if( throwable instanceof DataAccessException ) {
                        return (DataAccessException) throwable;
                     }

                     return
                        new DataAccessException
                               (
                                  new Message()
                                         .title( "PublishingArtifactLoader::dataAccessLoadPublishingArtifacts, failed to load artifacts." )
                                         .indentInc()
                                         .segment( "Branch Indicator", branchIndicator )
                                         .segmentIndexed( "Artifact Identifiers", artifactIdentifiers, (t) -> t, 20 )
                                         .segmentIndexed( "Artifact GUIDs", guids, (t) -> t, 20 )
                                         .segment("Filter For View", filterForView )
                                         .segmentIfNot( "Artifact Type Token", artifactTypeToken, ArtifactTypeToken.SENTINEL )
                                         .segmentIfNot( "Transaction Identifier", transactionId, TransactionId.SENTINEL )
                                         .segment( "Include Deleted", includeDeleted )
                                         .reasonFollows( throwable )
                                         .toString(),
                                  Cause.ERROR,
                                  throwable
                               );
                  }
               );

      if( result.isPresentValue() ) {
         var loaded = result.getValue().size();
         this.maxLoaded = ( this.maxLoaded < loaded ) ? loaded : this.maxLoaded;
      }

      return result;
      //@formatter:on
   }

   /**
    * Determines if the provided <code>branchId</code> is for the publishing branch or the product line branch.
    *
    * @param branchId the <code>BranchId</code> to be tested.
    * @return {@link BranchIndicator#PUBLISHING_BRANCH} when the <code>branchId</code> is for the publishing branch and
    * {@link BranchIndicator#PRODUCT_LINE_BRANCH} when the <code>branchId</code> is for the product line branch.
    * @throws OseeCoreException when the <code>branchId</code> isn't for either the publishing or product line branches.
    */

   public BranchIndicator determineBranchIndicator(BranchId branchId) {

      if (this.branchSpecification.getBranchId().isSameBranch(branchId)) {
         return BranchIndicator.PUBLISHING_BRANCH;
      }

      if (this.branchSpecificationProductLine.getBranchId().isSameBranch(branchId)) {
         return BranchIndicator.PRODUCT_LINE_BRANCH;
      }

      //@formatter:off
      throw
         new OseeCoreException
                (
                   new Message()
                          .title( "PublishingArtifactLoader::determineBranchIndicator, artifact is from an invalid branch." )
                          .indentInc()
                          .segment( "BranchId",            branchId                            )
                          .segment( "Publishing Branch",   this.branchSpecification            )
                          .segment( "Product Line Branch", this.branchSpecificationProductLine )
                          .toString()
                );
      //@formatter:on
   }

   /**
    * Determines if an artifact was loaded from the publishing branch or the product line branch.
    *
    * @param artifact the artifact to be tested.
    * @return when the artifact was loaded from the publishing branch {@link BranchIndicator#PUBLISHING_BRANCH};
    * otherwise, {@link BranchIndicator#PRODUCT_LINE_BRANCH}.
    * @throws OseeCoreException when the artifact is not from the publishing branch or the product line branch.
    */

   private @NonNull BranchIndicator determineBranchIndicator(@NonNull PublishingArtifact artifact) {

      if (this.branchSpecification.getBranchId().isSameBranch(artifact.getBranch())) {
         return BranchIndicator.PUBLISHING_BRANCH;
      }

      if (this.branchSpecificationProductLine.getBranchId().isSameBranch(artifact.getBranch())) {
         return BranchIndicator.PRODUCT_LINE_BRANCH;
      }

      //@formatter:off
      throw
         new OseeCoreException
                (
                   new Message()
                          .title( "PublishingArtifactLoader::determineBranchIndicator, artifact is from an invalid branch." )
                          .indentInc()
                          .segment( "Artifact",            artifact                            )
                          .segment( "Publishing Branch",   this.branchSpecification            )
                          .segment( "Product Line Branch", this.branchSpecificationProductLine )
                          .toString()
                );
      //@formatter:on
   }

   /**
    * Determines if the publishing branch is filtered for the specified view as follows:
    *
    * <pre>
    * +-------------------+----------------+-------------+-----------------+
    * | Publishing Branch | viewIdentifier | Views Are   | Filter For View |
    * | View Identifier   |                |             |                 |
    * +-------------------+----------------+-------------+-----------------+
    * | Is Valid       (1)| Is Valid    (2)| Equal    (4)| YES          (7)|
    * +-------------------+----------------+-------------+-----------------+
    * | Is Invalid        | Is Invalid     | Equal    (4)| NO           (4)|
    * +-------------------+----------------+-------------+-----------------+
    * | Is Valid       (1)| Is Valid    (2)| Not Equal   | EXCEPTION    (3)|
    * +-------------------+----------------+-------------+-----------------+
    * | Is Invalid        | Is Valid    (2)| Not Equal   | EXCEPTION    (2)|
    * +-------------------+----------------+-------------+-----------------+
    * | Is Valid       (1)| Is Invalid     | Not Equal   | NO           (1)|
    * +-------------------+----------------+-------------+-----------------+
    * </pre>
    *
    * @param viewIdentifier the view identifier to be tested.
    * @return the following:
    * <dl>
    * <dt>{@link FilterForView#YES}</dt>
    * <dd>when the <code>viewIdentifier</code> is valid and matches the view identifier of the publishing branch;
    * otherwise, <code>false</code>.</dd>
    * <dt>{@link FilterForView#No}</dt>
    * <dd>when the <code>viewIdentifier</code> and the publishing branch view identifier are both invalid.</dd>
    * <dt>{@link FilterForView#No}</dt>
    * <dd>when the <code>viewIdentifier</code> is invalid and the publishing branch view identifier is valid.</dd>
    * </dl>
    * @throws OseeCoreException when
    * <ul>
    * <li>The publishing branch view identifier is invalid and the <code>viewIdentifier</code> is valid.</li>
    * <li>The publishing branch view identifier is valid, the <code>viewIdentifier</code> is valid, and the view
    * identifiers are not equal.</li>
    * </ul>
    */

   public FilterForView determineFilterForView(ArtifactId viewIdentifier) {

      //@formatter:off
      var state =
           ( this.branchSpecification.getViewId().isValid()                                ? 1 : 0 )
         + ( viewIdentifier.isValid()                                                      ? 2 : 0 )
         + ( this.branchSpecification.getViewId().getId().equals( viewIdentifier.getId() ) ? 4 : 0 );
      //@formatter:on

      switch (state) {
         case 7:
            return FilterForView.YES;
         case 1:
         case 4:
            return FilterForView.NO;
         default:
            //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "PublishingArtifactLoader::determineFilterForView, view identifier is invalid." )
                                .indentInc()
                                .segment( "View Identifier", viewIdentifier )
                                .segment( "Branch View", this.branchSpecification.getViewId().getIdString() )
                                .toString()
                      );
            //@formatter:on
      }
   }

   /**
    * Loads and caches artifacts.
    *
    * @param branchIndicator indicates to load and cache for the publishing or product line branch.
    * @param identifierTypeIndicator indicates if the provided identifiers are {@link ArtifactId}s or GUIDs.
    * @param unloadedArtifactIdList the list of identifiers to load artifacts for. All artifact identifiers on the
    * unloadedArtifactIdList must be of the same type.
    * @param filterForView indicates if only artifact applicable to the specified branch should be loaded.
    * @param transactionId when valid, artifacts will be loaded from the specified transaction only.
    * @param includeDeleted indicates if deleted artifacts should also be loaded.
    * @return on success an empty {@link Optional}; otherwise, an {@link Optional} containing a
    * {@link DataAccessException} describing the error.
    */

   private Optional<DataAccessException> getAndCacheUnloadedArtifacts(BranchIndicator branchIndicator,
      IdentifierTypeIndicator identifierTypeIndicator, List<?> unloadedArtifactIdList, FilterForView filterForView,
      TransactionId transactionId, IncludeDeleted includeDeleted) {

      var loadTracker = new HashSet<>(unloadedArtifactIdList);

      /*
       * Get and cache uncached artifacts
       */

      //@formatter:off
      @SuppressWarnings("unchecked")
      var result =
         ( identifierTypeIndicator.isArtifactId()
              ? this.dataAccessLoadPublishingArtifacts
                   (
                      branchIndicator,
                      (List<ArtifactId>) unloadedArtifactIdList,
                      List.of(),
                      filterForView,
                      ArtifactTypeToken.SENTINEL,
                      transactionId,
                      includeDeleted
                   )
              : this.dataAccessLoadPublishingArtifacts
                   (
                      branchIndicator,
                      List.of(),
                      (List<String>) unloadedArtifactIdList,
                      filterForView,
                      ArtifactTypeToken.SENTINEL,
                      transactionId,
                      includeDeleted
                   ) )
         .peekValue
            (
               ( artifacts ) ->
               {
                  artifacts.forEach
                     (
                        ( artifact ) ->
                        {
                           this.cacheIt
                              (
                                 branchIndicator,
                                 ArtifactId.valueOf( artifact.getId() ),
                                 artifact
                              );

                           loadTracker.remove
                              (
                                 identifierTypeIndicator.isArtifactId()
                                    ? artifact.getArtifactId()
                                    : artifact.getGuid()
                              );
                        }
                     );

                  var branchSpecification = this.getBranchSpecification(branchIndicator, filterForView);

                  loadTracker.forEach
                     (
                        ( artifactIdentifier ) ->
                        {
                           this.cacheIt
                              (
                                 branchIndicator,
                                 identifierTypeIndicator.isArtifactId()
                                    ? (ArtifactId) artifactIdentifier
                                    : ArtifactId.SENTINEL,
                                 identifierTypeIndicator.isArtifactId()
                                    ? new PublishingArtifact.PublishingArtifactNotFound( branchSpecification, (ArtifactId) artifactIdentifier )
                                    : new PublishingArtifact.PublishingArtifactNotFound( branchSpecification, (String)     artifactIdentifier )
                           );
                        }
                     );
               }
            );

      return
         result.isPresentError()
            ? Optional.of( result.getError() )
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Get's the {@link ArtifactId} of the artifact specified by the <code>ambiguousIdentifier</code> from the publishing
    * branch. The specified artifact is loaded from the publishing branch without a view and cached when not already
    * cached.
    *
    * @param ambiguousIdentifier an {@link ArtifactId} or GUID.
    * @return when an artifact is found on the publishing branch with the specified <code>ambiguousIdentifier</code> an
    * {@link Optional} containing the found artifact's {@link ArtifactId}; otherwise, an empty {@link Optional}.
    * @throws OseeCoreException when an error occurs loading an artifact from the publishing branch.
    */

   public Optional<ArtifactId> getArtifactIdFromAmbiguousIdentifier(Object ambiguousIdentifier) {

      //@formatter:off
      var result =
         this
            .getPublishingArtifactByAmbiguousIdentifier
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  ambiguousIdentifier,
                  FilterForView.NO,
                  WhenNotFound.EMPTY,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .mapValue( PublishingArtifact::getArtifactId )
            .getAsOptionalOrElseThrow
               (
                  ( dataAccessException ) -> new OseeCoreException
                                                    (
                                                       new Message()
                                                              .title( "PublishingArtifactLoader::getArtifactIdFromAmbiguousIdentifier, unable to determine ArtifactId of artifact." )
                                                              .indentInc()
                                                              .segment( "Ambiguous Identifier", ambiguousIdentifier.toString() )
                                                              .reasonFollows( dataAccessException )
                                                              .toString(),
                                                       dataAccessException
                                                    )
               );
      //@formatter:on
      return result;
   }

   /**
    * Gets the {@link BranchSpecification} for the specified <code>branchIndicator</code> and
    * <code>filterForView</code>.
    *
    * @param branchIndicator indicates whether the {@link BranchSpecification} is for the publishing branch or the
    * product line branch.
    * @param filterForView indicates whether the {@link BranchSpecification} contains a view or not.
    * @return the {@link BranchSpecification} for the <code>branchIndicator</code> and <code>filterForView</code>.
    */

   public BranchSpecification getBranchSpecification(BranchIndicator branchIndicator, FilterForView filterForView) {
      return this.branchSpecificationMap.get(branchIndicator, filterForView).orElseThrow();
   }

   //@formatter:off
   /**
    * OK
    * <pre>
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * | State  | Cached Artifact | Cached Artifact | Filter   | WhenNotFound    | Action                                                                           |
    * |        | Branch Has View | Is Found        |          |                 |                                                                                  |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     80 | null        (0) | null        (0) | NO  (16) | error      (64) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     85 | has view    (1) | is found    (4) | NO  (16) | error      (64) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     89 | has view    (1) | not found   (8) | NO  (16) | error      (64) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     86 | no view     (2) | is found    (4) | NO  (16) | error      (64) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     90 | no view     (2) | not found   (8) | NO  (16) | error      (64) | Return: NoLoad,Result.ofError(NotFound)                                          |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |     96 | null        (0) | null        (0) | YES (32) | error      (64) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    101 | has view    (1) | is found    (4) | YES (32) | error      (64) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    105 | has view    (1) | not found   (8) | YES (32) | error      (64) | Return: NoLoad,Result.ofError(NotFound)                                          |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    102 | no view     (2) | is found    (4) | YES (32) | error      (64) | Return: Test Applicability ? Use,Result.of(A) : NoLoad,Result.ofError(NotFound ) |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    106 | no view     (2) | not found   (8) | YES (32) | error      (64) | Return: NoLoad,Result.ofError(NotFound)                                          |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    144 | null        (0) | null        (0) | NO  (16) | empty     (128) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    149 | has view    (1) | is found    (4) | NO  (16) | empty     (128) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    153 | has view    (1) | not found   (8) | NO  (16) | empty     (128) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    150 | no view     (2) | is found    (4) | NO  (16) | empty     (128) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    154 | no view     (2) | not found   (8) | NO  (16) | empty     (128) | Return: NoLoad,Result.empty()                                                    |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    160 | null        (0) | null        (0) | YES (32) | empty     (128) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    165 | has view    (1) | is found    (4) | YES (32) | empty     (128) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    169 | has view    (1) | not found   (8) | YES (32) | empty     (128) | Return: NoLoad,Result.empty()                                                    |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    166 | no view     (2) | is found    (4) | YES (32) | empty     (128) | Return: Test Applicability ? Use,Result.of(A) : NoLoad,Result.ofError(NotFound ) |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    170 | no view     (2) | not found   (8) | YES (32) | empty     (128) | Return: NoLoad,Result.empty()                                                    |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    272 | null        (0) | null        (0) | NO  (16) | not found (256) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    277 | has view    (1) | is found    (4) | NO  (16) | not found (256) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    281 | has view    (1) | not found   (8) | NO  (16) | not found (256) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    278 | no view     (2) | is found    (4) | NO  (16) | not found (256) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    282 | no view     (2) | not found   (8) | NO  (16) | not found (256) | Return: NoLoad,Result.of(A)                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    288 | null        (0) | null        (0) | YES (32) | not found (256) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    293 | has view    (1) | is found    (4) | YES (32) | not found (256) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    297 | has view    (1) | not found   (8) | YES (32) | not found (256) | Return: NoLoad,Result.of(A)                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    294 | no view     (2) | is found    (4) | YES (32) | not found (256) | Return: Test Applicability ? Use,Result.of(A) : NoLoad,Result.ofError(NotFound ) |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    298 | no view     (2) | not found   (8) | YES (32) | not found (256) | Return: NoLoad,Result.of(A)                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    528 | null        (0) | null        (0) | NO  (16) | sentinel  (512) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    533 | has view    (1) | is found    (4) | NO  (16) | sentinel  (512) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    537 | has view    (1) | not found   (8) | NO  (16) | sentinel  (512) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    534 | no view     (2) | is found    (4) | NO  (16) | sentinel  (512) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    538 | no view     (2) | not found   (8) | NO  (16) | sentinel  (512) | Return: NoLoad,Result.of(Sentinel)                                               |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    544 | null        (0) | null        (0) | YES (32) | sentinel  (512) | Return: Load,Result.empty()                                                      |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    549 | has view    (1) | is found    (4) | YES (32) | sentinel  (512) | Return: Use,Result.of(A)                                                         |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    553 | has view    (1) | not found   (8) | YES (32) | sentinel  (512) | Return: NoLoad,Result.of(Sentinel)                                               |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    550 | no view     (2) | is found    (4) | YES (32) | sentinel  (512) | Return: Test Applicability ? Use,Result.of(A) : NoLoad,Result.ofError(NotFound ) |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * |    554 | no view     (2) | not found   (8) | YES (32) | sentinel  (512) | Return: NoLoad,Result.of(Sentinel)                                               |
    * +--------+-----------------+-----------------+----------+-----------------+----------------------------------------------------------------------------------+
    * </pre>
    *
    * @param branchIndicator indicates whether to look for a cached artifact from the publishing branch or the product line branch.
    * @param identifierTypeIndicator
    * @param artifactIdentifier
    * @param filterForView
    * @param whenNotFound
    * @return
    */
   //@formatter:on

   private Triplet<CacheResult, Result<PublishingArtifact, DataAccessException>, IdentifierTypeIndicator> getCachedArtifact(
      BranchIndicator branchIndicator, IdentifierTypeIndicator identifierTypeIndicator, Object artifactIdentifier,
      FilterForView filterForView, WhenNotFound whenNotFound) {

      //@formatter:off
      var resolvedIdentifierTypeIndicator =
         identifierTypeIndicator.isAmbiguous()
            ? IdentifierTypeIndicator.determine( artifactIdentifier )
            : identifierTypeIndicator;
      //@formatter:on

      if (resolvedIdentifierTypeIndicator.isUnknown()) {
         //@formatter:off
         return
            new Triplet<>
                   (
                      CacheResult.ERROR,
                      Result.ofError
                         (
                            new DataAccessException
                                   (
                                      new Message()
                                             .title( "PublishingArtifactLoader::getCachedArtifact, cannot determine artifact identifier type." )
                                             .indentInc()
                                             .segment( "BranchIndicator",           branchIndicator               )
                                             .segment( "Identifier Type Indicator", identifierTypeIndicator       )
                                             .segment( "Artifact Identifier",       artifactIdentifier.toString() )
                                             .segment( "Filter For View",           filterForView                 )
                                             .segment( "When Not Found",            whenNotFound                  )
                                             .toString(),
                                      Cause.ARTIFACT_IDENTIFIER_TYPE_ERROR
                                   )
                         ),
                      resolvedIdentifierTypeIndicator
                   );
      }

      PublishingArtifact cachedArtifact;

      try {

         /*
          * Look for the artifact by artifact identifier or GUID in the cache. The identifierTypeIndicator will now be
          * resolved to a known type or set to unknown. If the passed artifactIdentifier does not match the expected
          * type a class cast exception will be thrown.
          */

         switch (resolvedIdentifierTypeIndicator) {

            case ARTIFACT_ID:
               //@formatter:off
               cachedArtifact =
                  this.publishingArtifactByArtifactIdentifierMap
                     .get
                        (
                           branchIndicator,
                           (ArtifactId) artifactIdentifier
                        )
                     .orElse( null );
               //@formatter:on
               break;

            case ARTIFACT_ID_STRING:
               //@formatter:off
               cachedArtifact =
                  this.publishingArtifactByArtifactIdentifierMap
                     .get
                        (
                           branchIndicator,
                           ArtifactId.valueOf( (String) artifactIdentifier )
                        )
                     .orElse( null );
               //@formatter:on
               break;

            case GUID:
               //@formatter:off
               cachedArtifact =
                  this.publishingArtifactByGuidMap
                     .get( branchIndicator, (String) artifactIdentifier )
                     .orElse( null );
               //@formatter:on
               break;

            default:

               //@formatter:off
               return
                  new Triplet<>
                         (
                            CacheResult.ERROR,
                            Result.ofError
                               (
                                  new DataAccessException
                                         (
                                            new Message()
                                                   .title( "PublishingArtifactLoader::getCachedArtifact, unexpected resolved artifact identifier." )
                                                   .indentInc()
                                                   .segment( "Branch Indicator",                   branchIndicator                 )
                                                   .segment( "Identifier Type Indicator",          identifierTypeIndicator         )
                                                   .segment( "Resolved Identifier Type Indicator", resolvedIdentifierTypeIndicator )
                                                   .segment( "Artifact Identifier",                artifactIdentifier.toString()   )
                                                   .segment( "Filter For View",                    filterForView                   )
                                                   .segment( "When Not Found",                     whenNotFound                    )
                                                   .toString(),
                                            Cause.ARTIFACT_IDENTIFIER_TYPE_ERROR
                                         )
                               ),
                            resolvedIdentifierTypeIndicator
                         );
               //@formatter:on
         }

      } catch (ClassCastException e) {

         //@formatter:off
         return
            new Triplet<>
                   (
                      CacheResult.ERROR,
                      Result.ofError
                         (
                            new DataAccessException
                                   (
                                      new Message()
                                             .title( "PublishingArtifactLoader::getCachedArtifact, artifact identifier type does not match indicator." )
                                             .indentInc()
                                             .segment( "Branch Indicator",                   branchIndicator                 )
                                             .segment( "Identifier Type Indicator",          identifierTypeIndicator         )
                                             .segment( "Resolved Identifier Type Indicator", resolvedIdentifierTypeIndicator )
                                             .segment( "Artifact Identifier",                artifactIdentifier.toString()   )
                                             .segment( "Filter For View",                    filterForView                   )
                                             .segment( "When Not Found",                     whenNotFound                    )
                                             .toString(),
                                      Cause.ARTIFACT_IDENTIFIER_TYPE_ERROR,
                                      e
                                   )
                         ),
                      resolvedIdentifierTypeIndicator
                   );
         //@formatter:on
      }

      var branchHasView = this.getBranchSpecification(branchIndicator, filterForView).hasView();

      //@formatter:off
      var state =
           ( Objects.nonNull( cachedArtifact ) &&  cachedArtifact.branchHasView() ?   1 : 0 )
         + ( Objects.nonNull( cachedArtifact ) && !cachedArtifact.branchHasView() ?   2 : 0 )
         + ( Objects.nonNull( cachedArtifact ) &&  cachedArtifact.isFound()       ?   4 : 0 )
         + ( Objects.nonNull( cachedArtifact ) && !cachedArtifact.isFound()       ?   8 : 0 )
         + ( filterForView.no()  || !branchHasView                                ?  16 : 0 )
         + ( filterForView.yes() &&  branchHasView                                ?  32 : 0 )
         + ( whenNotFound.error()                                                 ?  64 : 0 )
         + ( whenNotFound.empty()                                                 ? 128 : 0 )
         + ( whenNotFound.notFound()                                              ? 256 : 0 )
         + ( whenNotFound.sentinel()                                              ? 512 : 0 )
         ;
      //@formatter:on

      return this.getCachedArtifactProcessState(state, branchIndicator, resolvedIdentifierTypeIndicator, cachedArtifact,
         filterForView);
   }

   private Triplet<CacheResult, Result<PublishingArtifact, DataAccessException>, IdentifierTypeIndicator> getCachedArtifactProcessState(
      int state, BranchIndicator branchIndicator, IdentifierTypeIndicator identifierTypeIndicator,
      PublishingArtifact cachedArtifact, FilterForView filterForView) {
      //@formatter:off
      switch( state ) {
         /*
          * Load,Result.empty()
          */
         case  80:
         case  89:
         case  96:
         case 144:
         case 153:
         case 160:
         case 272:
         case 281:
         case 288:
         case 528:
         case 537:
         case 544:
         {
            this.cacheMiss++;
            return new Triplet<>(CacheResult.LOAD,Result.empty(),identifierTypeIndicator);
         }
         /*
          * Use,Result.of(A)
          */
         case  85:
         case  86:
         case 101:
         case 149:
         case 150:
         case 165:
         case 277:
         case 278:
         case 293:
         case 533:
         case 534:
         case 549:
         {
            this.cacheHit++;
            return new Triplet<>(CacheResult.USE,Result.ofValue(cachedArtifact),identifierTypeIndicator);
         }
         /*
          * Test,Result.of(A)
          */
         case 102:
         case 166:
         case 294:
         case 550:
         {
            this.cacheTest++;

            if( this.checkIsArtifactApplicable( branchIndicator, cachedArtifact) ) {
               cachedArtifact.setBranchView(this.getBranchSpecification(branchIndicator, filterForView));
               cachedArtifact.setApplicable();
               this.cacheHit++;
               return new Triplet<>(CacheResult.USE,Result.ofValue(cachedArtifact),identifierTypeIndicator);
            }

            cachedArtifact.clearApplicable();
            state += 3;
            return this.getCachedArtifactProcessState(state, branchIndicator, identifierTypeIndicator, cachedArtifact, filterForView);
         }
         /*
          * NoLoad,Result.ofError(NotFound)
          */
         case  90:
         case 105:
         case 106:
         {
            return
               new Triplet<>
                      (
                         CacheResult.NO_LOAD,
                         Result.ofError
                            (
                               new DataAccessException
                                      (
                                         new Message()
                                                .title( "Artifact is not found." )
                                                .indentInc()
                                                .segment( "Artifact Identifier", cachedArtifact.getIdString() )
                                                .segment( "Artifact GUID",       cachedArtifact.getGuid()     )
                                                .toString(),
                                         Cause.NOT_FOUND
                                      )
                            ),
                         identifierTypeIndicator
                      );
         }
         /*
          * NoLoad,Result.empty()
          */
         case 154:
         case 169:
         case 170:
         {
            return new Triplet<>(CacheResult.NO_LOAD,Result.empty(),identifierTypeIndicator);
         }
         /*
          * NoLoad,Result(A)
          */
         case 282:
         case 297:
         case 298:
         {
            return new Triplet<>(CacheResult.NO_LOAD,Result.ofValue(cachedArtifact),identifierTypeIndicator);
         }
         /*
          * NoLoad,Result.of(sentinel)
          */
         case 538:
         case 553:
         case 554:
         {
            return new Triplet<>(CacheResult.NO_LOAD,Result.ofValue(PublishingArtifact.SENTINEL),identifierTypeIndicator);
         }
         /*
          * Should never fall into default case
          */
         default:
         {
            throw
               new IllegalStateException
                      (
                         new Message()
                                .title( "PublishingArtifactLoader::getCachedArtifact, unexpected state." )
                                .indentInc()
                                .segment( "State", state )
                                .toString()
                      );
         }
      }
      //@formatter:on
   }

   /**
    * Gets an unmodifiable list view of the changed artifacts from the publishing branch for the publish.
    *
    * @return a {@link List} of the changed {@link PublishingArtifact}s on the publishing branch.
    */

   public List<PublishingArtifact> getChangedArtifactsList() {
      return Collections.unmodifiableList(this.changedArtifactsMap.listView());
   }

   /**
    * Gets the hierarchical children of <code>artifact</code>.
    *
    * @param artifact the artifact to get the children of.
    * @param filterForView indicates whether or not to include non-applicable artifacts.
    * @param cachedArtifactsOnly when {@link CachedArtifactsOnly#ALWAYS} only cached artifacts are returned and a database
    * load won't be performed.
    * @return on success a {@link Result} with the hierarchically sorted {@link List} of the <code>artifact</code>'s
    * immediate children; otherwise, a {@link Result} with a {@link DataAccessException}.
    * @implNote When the children of <code>artifact</code> have already been found, the cached list of children are
    * returned. The cache contains a hierarchically sorted list of all the children as well as a hierarchically sorted
    * list of just the applicable children. When the children are not cached, all of the children are loaded and tested
    * for applicability. A list of all children and a list of just the applicability children are cached.
    */

   public Result<List<PublishingArtifact>, DataAccessException> getChildren(@NonNull PublishingArtifact artifact,
      @NonNull FilterForView filterForView, @NonNull CacheReadMode cacheReadMode) {

      final var safeArtifact = Conditions.requireNonNull(artifact, "artifact");
      final var safeFilterForView = Conditions.requireNonNull(filterForView, "filterForView");

      final var validateResult = this.validateCached(artifact);

      if (validateResult.isPresent()) {
         return Result.ofError(validateResult.get());
      }

      this.getChildrenCount++;

      final var branchIndicator = this.determineBranchIndicator(safeArtifact);
      final var artifactIdentifier = ArtifactId.valueOf(safeArtifact.getId());

      final var branchSpecificationWithView = this.branchSpecificationMap.get(branchIndicator, FilterForView.YES).get();

      //@formatter:off
      final var cacheLoadFilterForView =
         branchSpecificationWithView.hasView()
            ? safeFilterForView
            : FilterForView.NO;
      //@formatter:on

      var childrenOptional = this.childrenCacheMap.get(branchIndicator, artifactIdentifier, cacheLoadFilterForView);

      if (childrenOptional.isPresent()) {
         this.cacheHit++;
         this.childCacheHit++;
         var children = new ArrayList<>(childrenOptional.get());
         return Result.ofValue(children);
      }

      if (cacheReadMode.isCacheOnly()) {
         return Result.ofValue(new ArrayList<>());
      }

      this.cacheMiss++;
      this.childCacheMiss++;

      /*
       * ArtifactReadOnlyImpl.getChildren() returns children in hierarchical order.
       * ArtifactReadOnlyImpl.getChildrenIds() does not.
       */

      /*
       * Load children via the artifact, cache them, and build a hierarchically ordered list of children identifiers.
       */

      var children = new LinkedList<PublishingArtifact>();

      try {

         var artifactChildren = artifact.getChildrenAsPublishingArtifacts();

         for (var child : artifactChildren) {

            child = this.switchIfCached(child);

            if (branchSpecificationWithView.hasView()) {

               child.setBranchView(branchSpecificationWithView);

               if (this.checkIsArtifactApplicable(branchIndicator, child)) {

                  child.setApplicable();

               } else {

                  child.clearApplicable();

               }

            } else {

               child.clearApplicable();

            }

            var childIdentifier = ArtifactId.valueOf(child.getId());

            this.cacheIt(branchIndicator, childIdentifier, child);

            children.add(child);
         }

      } catch (Throwable t) {
         //@formatter:off
            return
               Result.ofError
                  (
                     new DataAccessException
                            (
                               new Message()
                                      .title( "PublishingArtifactLoader::getChildren, failed to get children identifiers of artifact." )
                                      .indentInc()
                                      .segment( "Artifact Identifier", artifact.getIdString() )
                                      .reasonFollows( t )
                                      .toString(),
                               Cause.ERROR,
                               t
                            )
                  );
            //@formatter:on
      }

      if (children.isEmpty()) {

         /*
          * No children, cache empty lists and return an empty list
          */

         this.childrenCacheMap.associate(List.of(), branchIndicator, artifactIdentifier, FilterForView.YES);
         this.childrenCacheMap.associate(List.of(), branchIndicator, artifactIdentifier, FilterForView.NO);

         return Result.ofValue(new ArrayList<>());

      }

      /*
       * Make list of just the applicable children
       */

      var applicableChildren = new LinkedList<PublishingArtifact>();

      //@formatter:off
      children
         .forEach
            (
               ( child ) ->
               {
                  assert
                     !child.isApplicable().unknown()
                     : new Message()
                              .title( "PublishingArtifactLoader::getChildren, artifact has unknown applicability when unexpected." )
                              .indentInc()
                              .segmentToMessage( "Publishing Artifact", child )
                              .toString();

               if (child.isApplicable().yes()) {
                  applicableChildren.add(child);
               }
            });

      this.childrenCacheMap.associate
         (
            Collections.unmodifiableList( applicableChildren ),
            branchIndicator,
            artifactIdentifier,
            FilterForView.YES
         );

      this.childrenCacheMap.associate
         (
            Collections.unmodifiableList( children ),
            branchIndicator,
            artifactIdentifier,
            FilterForView.NO
         );

      safeArtifact.setChildrenLoadedAndCached();

      return
         cacheLoadFilterForView.yes()
            ? Result.ofValue( new ArrayList<>( applicableChildren ) )
            : Result.ofValue( new ArrayList<>( children           ) );
      //@formatter:on
   }

   public Result<List<PublishingArtifact>, DataAccessException> getChildrenPublishingArtifacts(ArtifactId rootArtifact,
      ArtifactTypeToken childArtifactTypeToken, AttributeTypeId childAttributeTypeId, String attributeValue,
      FilterForView filterForView, ProcessRecursively processRecursively) {

      //@formatter:off
      var result =
         this
            .dataAccessLoadChildrenArtifactIdentifiers
               (
                  rootArtifact,
                  childArtifactTypeToken,
                  childAttributeTypeId,
                  attributeValue,
                  filterForView,
                  processRecursively
               )
            .flatMapValue
               (
                  ( artifactIdentifiers )            -> this.getPublishingArtifactsByArtifactIdentifiers
                                                           (
                                                              BranchIndicator.PUBLISHING_BRANCH,
                                                              artifactIdentifiers,
                                                              filterForView,
                                                              WhenNotFound.EMPTY,
                                                              TransactionId.SENTINEL,
                                                              IncludeDeleted.NO
                                                           ),
                  ( artifactIdentifiers, throwable ) -> (DataAccessException) throwable
               );
      //@formatter:on
      return result;
   }

   /**
    * @return
    * @implNote For next gen publisher only.
    */

   public Collection<PublishingArtifact> getHyperLinkArtifactIdentifiers() {
      //@formatter:off
      return
         this.publishingArtifactByArtifactIdentifierMap
            .values(BranchIndicator.PUBLISHING_BRANCH)
            .orElse( Set.of() );
      //@formatter:on
   }

   /**
    * Determines the hierarchical index of the <code>cursor</code> amongst its siblings and pushes the hierarchical
    * index onto the <code>artifact</core>'s hierarchical position list. If the parent of the <code>cursor</code>
    * artifact has its hierarchy position set, it will be used to complete the <code>artifact</code>'s hierarchy
    * position list and the <code>artifact</code> will be set to hierarchy
    *
    * @param artifact the artifact the hierarchical position is being determined for.
    * @param cursor the parent of the <code>artifact</code> at the hierarchical level being determined.
    * @return the parent of the <code>cursor</code> artifact.
    */

   private PublishingArtifact getLocalHierarchyPosition(PublishingArtifact artifact, PublishingArtifact cursor) {

      //@formatter:off
      var parent =
         this
            .getParent
               (
                  cursor,
                  FilterForView.NO,
                  WhenNotFound.ERROR
               )
            .peekError
               (
                  ( dataAccessException ) ->
                  {
                     this.publishingErrorLog.error
                        (
                           cursor,
                           new Message()
                                  .title( "Failed to obtain parent of artifact during hierarchical sort." )
                                  .indentInc()
                                  .segment( "Artifact Identifier", cursor )
                                  .reasonFollows( dataAccessException )
                                  .toString()
                        );
                  }
               )
            .orElseGet
               (
                  PublishingArtifact.SENTINEL
               );
      //@formatter:on

      if (!parent.isFound()) {
         artifact.pushHierarchyPosition(-1);
         artifact.setHierarchyPosition();
         return parent;
      }

      //@formatter:off
      var siblings =
         this
            .getChildren
               (
                  parent,
                  FilterForView.NO,
                  CacheReadMode.LOAD_FROM_DATABASE
               )
            .peekError
               (
                  ( dataAccessException ) ->
                  {
                     this.publishingErrorLog.error
                        (
                           cursor,
                           new Message()
                                  .title( "Failed to sibling artifacts during hierarchical sort." )
                                  .indentInc()
                                  .segment( "Artifact Identifier",        cursor )
                                  .segment( "Parent Artifact Identifier", parent )
                                  .reasonFollows( dataAccessException )
                                  .toString()
                        );
                  }
               )
            .orElseGet
               (
                  List.of()
               );
      //@formatter:on

      var siblingPosition = siblings.indexOf(cursor);

      artifact.pushHierarchyPosition(siblingPosition);

      if (parent.isHierarchyPositionSet()) {
         artifact.setHierarchyPosition(parent.getHierarchyPosition());
      }

      return parent;
   }

   /**
    * Gets the hierarchical parent of <code>artifact</code>.
    *
    * @param artifact the {@link PublishingArtifact} to get the parent of.
    * @param filterForView specifies whether the parent artifact should or should not be returned when non-applicable.
    * @param whenNotFound set according to the following for the desired result when a parent artifact cannot be found:
    * <dl>
    * <dt>{@link WhenNotFound#ERROR}</dt>
    * <dd>for a {@link Result} containing a {@link DataAccessException}.</dd>
    * <dt>{@link WhenNotFound#EMPTY}</dt>
    * <dd>for an empty {@link Result}.</dd>
    * <dt>{@link WhenNotFound#SENTINEL}</dt>
    * <dd>for a {@link Result} containing a {@link PublishingArtifact.SENTINEL}.</dd>
    * <dt>{@link WhenNotFound#NOT_FOUND}</dt>
    * <dd>for a {@link Result} containing a {@link PublishingArtifact.PublishingArtifactNotFound}. The not found marker
    * will not contain an {@link ArtifactId} when the parent identifier cannot be determined from the
    * <code>artifact</code>. The not found marker will contain an {@link ArtifactId} when the identifier of the parent
    * to <code>artifact</code> is found but an artifact with that identifier cannot be loaded. A parent will not be
    * found when it is not applicable to the view and <code>filterForView</code> is set to
    * {@link FilterForView#YES}.</dd>
    * </dl>
    * @return when the parent artifact is found a {@link Result} containing the parent {@link PublishingArtifact}, when
    * an error occurs a {@link Result} containing a {@link DataAccessException}, and when the parent artifact is not
    * found a {@link Result} set according to the setting of <code>whenNotFound</code>.
    */

   public Result<PublishingArtifact, DataAccessException> getParent(PublishingArtifact artifact,
      FilterForView filterForView, WhenNotFound whenNotFound) {

      this.getParentCount++;

      var branchIndicator = this.determineBranchIndicator(artifact);

      List<ArtifactId> parentIdentifierList;

      try {
         parentIdentifierList = artifact.getRelatedIds(CoreRelationTypes.DefaultHierarchical_Parent);
      } catch (Exception e) {
         //@formatter:off
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "PublishingArtifactLoader::getParent, PublishingArtifact::getRelatedIds failed." )
                                   .indentInc()
                                   .segmentToMessage( "Artifact", artifact )
                                   .reasonFollows( e )
                                   .toString(),
                            Cause.ERROR,
                            e
                         )
               );
         //@formatter:on
      }

      /*
       * Should never be more than one artifact identifier
       */

      if (parentIdentifierList.size() > 1) {
         //@formatter:off
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "PublishingArtifactLoader::getParent, PublishingArtifact::getRelatedIds returned more than one parent identifier." )
                                   .indentInc()
                                   .segment( "Artifact", artifact.getIdString() )
                                   .segmentIndexed( "Parent Identifiers", parentIdentifierList )
                                   .toString(),
                            Cause.ERROR
                         )
               );
         //@formatter:on
      }

      /*
       * If artifact is not hierarchically connected
       */

      if (parentIdentifierList.isEmpty()) {
         switch (whenNotFound) {
            case ERROR:
               //@formatter:off
               return
                  Result.ofError
                     (
                        new DataAccessException
                               (
                                  new Message()
                                         .title( "PublishingArtifactLoader::getParent, artifact is not hierarchically connected." )
                                         .indentInc()
                                         .segment( "Artifact", artifact )
                                         .toString(),
                                  Cause.NOT_FOUND
                               )
                     );
               //@formatter:on
            case EMPTY:
               return Result.empty();
            case SENTINEL:
               return Result.ofValue(PublishingArtifact.SENTINEL);
            case NOT_FOUND:
               return Result.ofValue(new PublishingArtifact.PublishingArtifactNotFound(
                  this.getBranchSpecification(branchIndicator, filterForView)));
            default:
               throw new IllegalStateException();
         }
      }

      var parentIdentifier = ArtifactId.valueOf(parentIdentifierList.get(0).getId());

      //@formatter:off
      var result =
         this
            .getPublishingArtifactInternal
               (
                  branchIndicator,
                  IdentifierTypeIndicator.ARTIFACT_ID,
                  parentIdentifier,
                  filterForView,
                  whenNotFound,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               );

      return result;
      //@formatter:on
   }

   public Result<BranchSpecification, DataAccessException> getProductLineBranch() {
      //@formatter:off
      var result =
         Objects.nonNull( this.branchSpecificationProductLineWithoutView )
            ? Result.<BranchSpecification,DataAccessException>ofValue( this.branchSpecificationProductLineWithoutView )
            : this.dataAccessLoadProductLineBranch().mapValue( BranchSpecification::new );
      //@formatter:on
      return result;
   }

   public Result<Triplet<BranchId, ArtifactToken, Map<String, List<String>>>, DataAccessException> getProductLineViewApplicabilitesMap() {
      return this.dataAccessLoadProductLineViewApplicabilitiesMap();
   }

   /**
    * KO
    *
    * @param ambiguousIdentifierString
    * @param filterForView
    * @param whenNotFound
    * @return
    */

   public Result<PublishingArtifact, DataAccessException> getPublishingArtifactByAmbiguousIdentifier(
      BranchIndicator branchIndicator, Object ambiguousIdentifier, FilterForView filterForView,
      WhenNotFound whenNotFound, TransactionId transactionIdentifier, IncludeDeleted includeDeleted) {

      //@formatter:off
      var result =
         this.getPublishingArtifactInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.AMBIGUOUS,
               ambiguousIdentifier,
               filterForView,
               whenNotFound,
               transactionIdentifier,
               includeDeleted
            );
      //@formatter:on
      return result;
   }

   /**
    * KO
    *
    * @param artifactIdentifier
    * @param filter
    * @param whenNotFound
    * @return
    */

   public Result<PublishingArtifact, DataAccessException> getPublishingArtifactByArtifactIdentifier(
      BranchIndicator branchIndicator, ArtifactId artifactIdentifier, FilterForView filter, WhenNotFound whenNotFound,
      TransactionId transactionIdentifier, IncludeDeleted includeDeleted) {

      //@formatter:off
      var result =
         this.getPublishingArtifactInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.ARTIFACT_ID,
               artifactIdentifier,
               filter,
               whenNotFound,
               transactionIdentifier,
               includeDeleted
            );
      //@formatter:on
      return result;
   }

   /**
    * KO
    *
    * @param guid
    * @param filter
    * @param whenNotFound
    * @return
    */

   public Result<PublishingArtifact, DataAccessException> getPublishingArtifactByGuid(BranchIndicator branchIndicator,
      String guid, FilterForView filter, WhenNotFound whenNotFound, TransactionId transactionIdentifier,
      IncludeDeleted includeDeleted) {

      //@formatter:off
      var result =
         this.getPublishingArtifactInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.GUID,
               guid,
               filter,
               whenNotFound,
               transactionIdentifier,
               includeDeleted
            );
      //@formatter:on
      return result;
   }

   /**
    * KO
    *
    * @param ambiguousIdentifier
    * @param filterForView
    * @return
    */

   //@formatter:off
   private Result<PublishingArtifact, DataAccessException>
      getPublishingArtifactInternal
         (
            BranchIndicator         branchIndicator,
            IdentifierTypeIndicator identifierTypeIndicator,
            Object                  ambiguousIdentifier,
            FilterForView           filterForView,
            WhenNotFound            whenNotFound,
            TransactionId           transactionId,
            IncludeDeleted          includeDeleted
         ) {
      //@formatter:on

      //@formatter:off
      var triplet =
         this.getCachedArtifact
            (
               branchIndicator,
               identifierTypeIndicator,
               ambiguousIdentifier,
               filterForView,
               whenNotFound
            );
      //@formatter:on

      var cacheResult = triplet.getFirst();
      var resultFromCache = triplet.getSecond();
      var resolvedIdentifierTypeIndicator = triplet.getThird();

      switch (cacheResult) {
         case ERROR:
         case NO_LOAD:
         case USE: {
            return resultFromCache;
         }
         case LOAD: {
            //@formatter:off
            Supplier<Result<? extends List<PublishingArtifact>,DataAccessException>> artifactLoader =
               resolvedIdentifierTypeIndicator.isArtifactId()
                  ? () -> this
                             .dataAccessLoadPublishingArtifacts
                                (
                                   branchIndicator,
                                   List.of( (ArtifactId) ambiguousIdentifier ),
                                   List.of(),
                                   filterForView,
                                   ArtifactTypeToken.SENTINEL,
                                   transactionId,
                                   includeDeleted
                                )
                  : () -> this.dataAccessLoadPublishingArtifacts
                             (
                                branchIndicator,
                                List.of(),
                                List.of( (String) ambiguousIdentifier ),
                                filterForView,
                                ArtifactTypeToken.SENTINEL,
                                transactionId,
                                includeDeleted
                             );

            var result =
               artifactLoader
                  .get()
                  .ifEmptyThrow
                     (
                        () -> new OseeCoreException
                                     (
                                        "PublishingArtifactLoader::getPublishingArtifactInternal, the artifact loader result was empty."
                                     )
                     )
                  .mapValue
                     (
                        ( loadedArtifacts )           -> loadedArtifacts.get(0),
                        ( loadedArtifact, throwable ) -> new DataAccessException
                                                                (
                                                                   new Message()
                                                                          .title( "PublishingArtifactLoader::getPublishingArtifactInternal, artifact not found." )
                                                                          .indentInc()
                                                                          .segment( "Ambiguous Artifact Identifier", ambiguousIdentifier )
                                                                          .toString(),
                                                                   Cause.NOT_FOUND
                                                                )
                     )
                  .peekValue
                     (
                        ( loadedArtifact ) -> this.cacheIt
                                                 (
                                                    branchIndicator,
                                                    ArtifactId.create( loadedArtifact  ),
                                                    loadedArtifact
                                                 )
                     )
                  .flatMapError
                     (
                        ( dataAccessException ) ->
                        {
                           if( dataAccessException.isNotFound() )
                           {
                              var notFoundArtifact =
                                 resolvedIdentifierTypeIndicator.isArtifactId()
                                    ? new PublishingArtifact.PublishingArtifactNotFound
                                             (
                                                this.getBranchSpecification(branchIndicator, filterForView),
                                                (ArtifactId) ambiguousIdentifier
                                             )
                                    : new PublishingArtifact.PublishingArtifactNotFound
                                             (
                                                this.getBranchSpecification(branchIndicator, filterForView),
                                                (String) ambiguousIdentifier
                                             );
                              this.cacheIt
                                 (
                                    branchIndicator,
                                    notFoundArtifact.getArtifactId(), /* Is SENTINEL for artifact load by GUID */
                                    notFoundArtifact
                                 );

                              switch( whenNotFound ) {
                                 case ERROR:
                                    return Result.ofError( dataAccessException );
                                 case EMPTY:
                                    return Result.empty();
                                 case SENTINEL:
                                    return Result.ofValue( PublishingArtifact.SENTINEL );
                                 case NOT_FOUND:
                                    return Result.ofValue( notFoundArtifact );
                              }
                           }

                           return Result.ofError( dataAccessException );
                        }
                     );
            //@formatter:off
            return result;
         }
         default:
            throw new IllegalStateException();
      }


   }

   //@formatter:off
   public Result<List<PublishingArtifact>, DataAccessException>
      getPublishingArtifacts
         (
            BranchIndicator branchIndicator,
            FilterForView   filterForView,
            WhenNotFound    whenNotFound,
            AttributeTypeId attributeTypeId,
            String          attributeValue,
            TransactionId   transactionId,
            IncludeDeleted  includeDeleted
         ) {

      var result =
         this.dataAccessOperations
            .getArtifactIdentifiers
               (
                  this.branchSpecificationMap.get(branchIndicator, filterForView).get(),
                  attributeTypeId,
                  attributeValue,
                  transactionId,
                  includeDeleted
               )
            .flatMapValue
               (
                  ( artifactIdentifiers ) -> this.getPublishingArtifactsInternal
                                                (
                                                   branchIndicator,
                                                   IdentifierTypeIndicator.ARTIFACT_ID,
                                                   artifactIdentifiers,
                                                   filterForView,
                                                   whenNotFound,
                                                   transactionId,
                                                   includeDeleted
                                                ),
                  ( artifactIdentifier, dataAccessException ) -> (DataAccessException) dataAccessException
               );

      return result;
   }
   //@formatter:on

   public Result<List<PublishingArtifact>, DataAccessException> getPublishingArtifactsByAmbiguousIdentifiers(
      BranchIndicator branchIndicator, Collection<?> ambiguousIdentifiers, FilterForView filterForView,
      WhenNotFound whenNotFound, TransactionId transactionId, IncludeDeleted includeDeleted) {

      //@formatter:off
      var result =
         this.getPublishingArtifactsInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.AMBIGUOUS,
               ambiguousIdentifiers,
               filterForView,
               whenNotFound,
               transactionId,
               includeDeleted
            );
      //@formatter:on
      return result;
   }

   public Result<List<PublishingArtifact>, DataAccessException> getPublishingArtifactsByArtifactIdentifiers(
      BranchIndicator branchIndicator, Collection<? extends ArtifactId> artifactIdentifiers,
      FilterForView filterForView, WhenNotFound whenNotFound, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      //@formatter:off
      var result =
         this.getPublishingArtifactsInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.ARTIFACT_ID,
               artifactIdentifiers,
               filterForView,
               whenNotFound,
               transactionId,
               includeDeleted
            );
      //@formatter:on
      return result;
   }

   //@formatter:off
   public Result<List<PublishingArtifact>, DataAccessException>
      getPublishingArtifactsByGuids
         (
            BranchIndicator    branchIndicator,
            Collection<String> guids,
            FilterForView      filterForView,
            WhenNotFound       whenNotFound,
            TransactionId      transactionId,
            IncludeDeleted     includeDeleted
         ) {

      var result =
         this.getPublishingArtifactsInternal
            (
               branchIndicator,
               IdentifierTypeIndicator.GUID,
               guids,
               filterForView,
               whenNotFound,
               transactionId,
               includeDeleted
            );

      return result;
   }
   //@formatter:on

   //@formatter:off
   @SuppressWarnings("incomplete-switch")
   private Result<List<PublishingArtifact>, DataAccessException>
      getPublishingArtifactsInternal
         (
            BranchIndicator         branchIndicator,
            IdentifierTypeIndicator identifierTypeIndicator,
            Collection<?>           ambiguousIdentifiers,
            FilterForView           filterForView,
            WhenNotFound            whenNotFound,
            TransactionId           transactionId,
            IncludeDeleted          includeDeleted
         ) {
      //@formatter:on

      if (Objects.isNull(ambiguousIdentifiers)) {
         //@formatter:off
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            "PublishingArtifactLoader::getPublishingArtifactsByArtifactIdentifiers, parameter \"ambiguousIdentifiers\" cannot be null.",
                            Cause.ERROR
                         )
               );
         //@formatter:on
      }

      //@formatter:off
      assert
           !ambiguousIdentifiers.stream().anyMatch( ( ambiguousIdentifier ) -> IdentifierTypeIndicator.determine( ambiguousIdentifier ).isUnknown() )
         : new Message()
                  .title( "PublishingArtifactLoader::getPublishingArtifactsByArtifactIdentifiers, parameter \"artifactIdentifiers\" cannot contain null or sentinel artifact identifiers." )
                  .indentInc()
                  .segmentIndexed( "Artifact Identifiers", ambiguousIdentifiers.stream().collect( Collectors.toList() ) )
                  .toString();
      //@formatter:on

      if (ambiguousIdentifiers.isEmpty()) {
         return Result.empty();
      }

      /*
       * Get cached artifacts
       */

      var loadSize = ambiguousIdentifiers.size();
      var callLoadByIdCacheHitCount = 0;
      var loadedArtifacts = new ArrayList<PublishingArtifact>(loadSize);
      var unloadedArtifactIdList = new ArrayList<ArtifactId>(loadSize);
      var unloadedGuidList = new ArrayList<String>(loadSize);

      for (var ambiguousIdentifier : ambiguousIdentifiers) {

         //@formatter:off
         var triplet =
            this.getCachedArtifact
               (
                  branchIndicator,
                  identifierTypeIndicator,
                  ambiguousIdentifier,
                  filterForView,
                  WhenNotFound.NOT_FOUND
               );
         //@formatter:on

         var cacheResult = triplet.getFirst();
         var resultFromCache = triplet.getSecond();
         var resolvedIdentifierTypeIndicator = triplet.getThird();

         if (resultFromCache.isPresentError()) {
            return Result.ofError(resultFromCache);
         }

         //@formatter:off
         resultFromCache
            .ifValueActionElseAction
               (
                  loadedArtifacts::add,
                  () ->
                  {
                     switch( resolvedIdentifierTypeIndicator ) {
                        case ARTIFACT_ID:
                           loadedArtifacts.add
                              (
                                 new PublishingArtifact.PublishingArtifactNotFound
                                        (
                                           branchSpecification,
                                           (ArtifactId) ambiguousIdentifier
                                        )
                              );
                           break;
                        case ARTIFACT_ID_STRING:
                           loadedArtifacts.add
                              (
                                 new PublishingArtifact.PublishingArtifactNotFound
                                        (
                                           branchSpecification,
                                           ArtifactId.valueOf( (String) ambiguousIdentifier )
                                        )
                              );
                           break;
                        case GUID:
                           loadedArtifacts.add
                              (
                                 new PublishingArtifact.PublishingArtifactNotFound
                                        (
                                           branchSpecification,
                                           (String) ambiguousIdentifier
                                        )
                              );
                           break;
                     }
                  }
               );
         //@formatter:on

         switch (cacheResult) {
            case USE: {
               callLoadByIdCacheHitCount++;
               this.loadByIdCacheHitCount++;
               break;
            }
            case LOAD: {
               switch (resolvedIdentifierTypeIndicator) {
                  case ARTIFACT_ID:
                     unloadedArtifactIdList.add((ArtifactId) ambiguousIdentifier);
                     break;
                  case ARTIFACT_ID_STRING:
                     unloadedArtifactIdList.add(ArtifactId.valueOf((String) ambiguousIdentifier));
                     break;
                  case GUID:
                     unloadedGuidList.add((String) ambiguousIdentifier);
                     break;
               }
               break;
            }
         }
      }

      //@formatter:off
      this.maxLoadByIdCacheHitCount =
         callLoadByIdCacheHitCount > this.maxLoadByIdCacheHitCount
            ? callLoadByIdCacheHitCount
            : this.maxLoadByIdCacheHitCount;
      //@formatter:on

      /*
       * Get and cache unloaded artifacts
       */

      if (unloadedArtifactIdList.size() > 0) {
         //@formatter:off
         var dataAccessExceptionOptional =
            this.getAndCacheUnloadedArtifacts
               (
                  branchIndicator,
                  IdentifierTypeIndicator.ARTIFACT_ID,
                  unloadedArtifactIdList,
                  filterForView,
                  transactionId,
                  includeDeleted
               );
         //@formatter:on
         if (dataAccessExceptionOptional.isPresent()) {
            return Result.ofError(dataAccessExceptionOptional.get());
         }
      }

      if (unloadedGuidList.size() > 0) {
         //@formatter:off
         var dataAccessExceptionOptional =
            this.getAndCacheUnloadedArtifacts
               (
                  branchIndicator,
                  IdentifierTypeIndicator.GUID,
                  unloadedGuidList,
                  filterForView,
                  transactionId,
                  includeDeleted
               );
         //@formatter:on
         if (dataAccessExceptionOptional.isPresent()) {
            return Result.ofError(dataAccessExceptionOptional.get());
         }
      }

      /*
       * Everything found is now cached. Replace not found tokens in loadedArtifact with the loaded artifact if one was
       * found.
       */

      var unloadedArtifacts =
         this.mergeJustLoadedArtifacts(branchIndicator, loadedArtifacts, filterForView, WhenNotFound.EMPTY);

      if (whenNotFound.empty() && unloadedArtifacts.isPresent()) {
         loadedArtifacts.removeIf(Predicate.not(PublishingArtifact::isFound));
      }

      return Result.ofValue(loadedArtifacts);
   }

   public Result<List<PublishingArtifact>, DataAccessException> getRelated(PublishingArtifact publishingArtifact,
      RelationTypeSide relationTypeSide, FilterForView filterForView) {

      var relatedArtifactIdentifiers = publishingArtifact.getRelatedIds(relationTypeSide);

      if (relatedArtifactIdentifiers.isEmpty()) {
         return Result.ofValue(new ArrayList<>());
      }

      //@formatter:off
      var result =
         this.getPublishingArtifactsByArtifactIdentifiers
            (
               BranchIndicator.PUBLISHING_BRANCH,
               relatedArtifactIdentifiers,
               filterForView,
               WhenNotFound.EMPTY,
               TransactionId.SENTINEL,
               IncludeDeleted.NO
            );
      //@formatter:on
      return result;
   }

   public Result<List<PublishingArtifact>, DataAccessException> getSiblings(PublishingArtifact publishingArtifact,
      FilterForView filterForView) {

      var branchSpecification = this.getBranchSpecification(BranchIndicator.PUBLISHING_BRANCH, filterForView);

      //@formatter:off
      return
         this.dataAccessOperations
            .getParentArtifactIdentifiers( branchSpecification, publishingArtifact, ProcessRecursively.NO )
            .flatMapValue
               (
                  ( parentArtifactIdentifiers )       -> this.dataAccessOperations
                                                            .getChildrenArtifactIdentifiers
                                                               (
                                                                  branchSpecification,
                                                                  parentArtifactIdentifiers.get(0),
                                                                  ArtifactTypeToken.SENTINEL,
                                                                  AttributeTypeId.SENTINEL,
                                                                  Strings.EMPTY_STRING,
                                                                  ProcessRecursively.NO
                                                               ),
                  ( artifactId, dataAccessException ) -> (DataAccessException) dataAccessException

               )
            .mapValue
               (
                  ( artifactIdentifiers ) -> this.getPublishingArtifactsByArtifactIdentifiers
                                                (
                                                   BranchIndicator.PUBLISHING_BRANCH,
                                                   artifactIdentifiers,
                                                   filterForView,
                                                   WhenNotFound.EMPTY,
                                                   TransactionId.SENTINEL,
                                                   IncludeDeleted.NO
                                                )
               )
            .orElseThrow();
      //@formatter:on
   }

   public Result<Set<String>, DataAccessException> getViewApplicabilityConfigurationGroups() {

      //@formatter:off
      var result =
         this
            .getProductLineBranch()
            .flatMapValue
               (
                  ( productLineBranchSpecification )            -> this.dataAccessOperations.getArtifactTokens
                                                                      (
                                                                         productLineBranchSpecification,
                                                                         List.of(),
                                                                         CoreArtifactTypes.GroupArtifact,
                                                                         CoreRelationTypes.PlConfigurationGroup_Group
                                                                      ),
                  ( productLineBranchSpecification, throwable ) -> (DataAccessException) throwable
               )
            .mapValue
               (
                  ( artifactTokenList ) ->
                  {
                     Set<String> set = new HashSet<>( artifactTokenList.size() * 8 );

                     artifactTokenList
                        .forEach
                           (
                              ( artifactToken ) -> set.add( artifactToken.getName().toUpperCase() )
                           );

                     return set;
                  }
               );
      //@formatter:on
      return result;
   }

   public Result<Set<String>, DataAccessException> getViewApplicabilityConfigurations() {

      //@formatter:off
      var result =
         this
            .getProductLineBranch()
            .flatMapValue
               (
                  ( productLineBranchSpecification )            -> this.dataAccessOperations.getArtifactTokens
                                                                      (
                                                                         productLineBranchSpecification,
                                                                         List.of(),
                                                                         CoreArtifactTypes.BranchView,
                                                                         RelationTypeSide.SENTINEL
                                                                      ),
                  ( productLineBranchSpecification, throwable ) -> (DataAccessException) throwable
               )
            .mapValue
               (
                  ( artifactTokenList ) ->
                  {
                     Set<String> set = new HashSet<>( artifactTokenList.size() * 8 );

                     artifactTokenList
                        .forEach
                           (
                              ( artifactToken ) -> set.add( artifactToken.getName().toUpperCase() )
                           );

                     return set;
                  }
               );
      //@formatter:on
      return result;
   }

   /**
    * Predicate to determine if an artifact for the publish has been changed.
    *
    * @param artifactId the identifier of the artifact to check.
    * @return <code>true</code>, when the artifact has been changed; otherwise, <code>false</code>.
    */

   public boolean isChangedArtifact(ArtifactId artifactId) {

      return this.changedArtifactsMap.mapView().containsKey(artifactId);
   }

   public boolean isHeading(ArtifactId artifactIdentifier) {
      //@formatter:off
      return
         this
            .getPublishingArtifactByArtifactIdentifier
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  artifactIdentifier,
                  FilterForView.NO,
                  WhenNotFound.EMPTY,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .mapValue( ( artifact ) -> artifact.isOfType( CoreArtifactTypes.HeadingMsWord ) )
            .orElseGet( false );
      //@formatter:on

   }

   public boolean isProductLineBranchPublishingBranch() {
      return this.branchSpecification.getBranchId().isSameBranch(this.branchSpecificationProductLine.getBranchId());
   }

   /**
    * Predicate to determine if the <code>artifact</code> is a hierarchical descendant of an artifact in
    * <code>goodParents</code>. When the <code>artifact</code> is found to be a hierarchical descendant, the artifact
    * and it's hierarchical parents that are not in <code>goodParents</code> are added.
    *
    * @param goodParents the set of artifact's to test if <code>artifact</code> is a hierarchical descendant of one.
    * @param artifact the artifact to test.
    * @return <code>true</code>, when <code>artifact</code> is a hierarchical descendant of an artifact in
    * <code>goodParents</code>; otherwise, <code>false</code>.
    */

   public boolean isRecursivelyRelated(HashSet<PublishingArtifact> goodParents, PublishingArtifact artifact,
      FilterForView filterForView) {

      if (goodParents.contains(artifact)) {
         return true;
      }

      var maybeParents = new LinkedList<PublishingArtifact>();

      maybeParents.add(artifact);

      //@formatter:off
      for( var ancestor = this.getParent( artifact, filterForView, WhenNotFound.ERROR ).orElseThrow();
               Objects.nonNull( ancestor );
               ancestor = this.getParent( ancestor, filterForView, WhenNotFound.ERROR ).orElseThrow() ) {

         maybeParents.add( ancestor );

         if( goodParents.contains( ancestor ) ) {
            goodParents.addAll(maybeParents);
            return true;
         }
      }

      return false;
      //@formatter:on
   }

   public Result<List<PublishingArtifact>, DataAccessException> loadAncestors(PublishingArtifact publishingArtifact,
      FilterForView filterForView) {

      //@formatter:off
      return
         this.dataAccessOperations
            .getParentArtifactIdentifiers
               (
                  this.getBranchSpecification( BranchIndicator.PUBLISHING_BRANCH, filterForView ),
                  publishingArtifact,
                  ProcessRecursively.YES
               )
            .flatMapValue
               (
                  ( artifactIdentifiers )            -> this.getPublishingArtifactsByArtifactIdentifiers
                                                           (
                                                              BranchIndicator.PUBLISHING_BRANCH,
                                                              artifactIdentifiers,
                                                              FilterForView.YES,
                                                              WhenNotFound.EMPTY,
                                                              TransactionId.SENTINEL,
                                                              IncludeDeleted.NO
                                                           ),
                  ( artifactIdentifiers, throwable ) -> (DataAccessException) throwable
               );

      //@formatter:on
   }

   private void loadAndCacheChildren(PublishingArtifact publishingArtifact) {

      if (!publishingArtifact.areChildrenCached()) {
         this.getChildren(publishingArtifact, FilterForView.NO, CacheReadMode.LOAD_FROM_DATABASE);
      }

   }

   /**
    * Finds the artifacts that have been changed under one of the ATS Team Workflows that are associated with the
    * artifact specified by <code>artifactSpecification</code> and that are hierarchical descendants of one of the
    * artifacts in <code>headerArtifacts</code>.
    * <p>
    * Call only one of the methods {@link #loadByAtsTeamworkflow} or {@link #loadByTransactionComment} only once per
    * instantiation of the {@link ChangedArtifactsTracker} class.
    * <p>
    * If the parameter <code>headerArtifacts</code> is an empty {@link Collection}, no changed artifacts will be loaded.
    *
    * @param headerArtifacts find changed artifacts that are hierarchical descendants of these artifacts.
    * @param artifactSpecification ATS Team Workflows related to this artifact are used to find the changed artifacts.
    * @throws NullPointerException when <code>headerArtifacts</code> or <code>artifactSpecification</code> are
    * <code>null</code>.
    * @implNote This method is for taking a Goal artifact and gathering the ArtifactIds of all changed artifacts
    * associated with the workflows under the goal. There is a future plan to publish off of working branches, once
    * implemented, the transaction query will need to be modified. Also, if a working branch is selected, it is okay for
    * that branch to not be committed. If publishing off of a baseline branch, any uncommitted workflows will be logged
    * and changes will not be included in the publish. Will move to MSWordTemplatePublisher when completed.
    */

   public void loadByAtsTeamWorkflow(Collection<? extends PublishingArtifact> headerArtifacts,
      ArtifactId artifactIdentifier) {

      if (this.changedArtifactsLoaded) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      "PublishingArtifactLoader::loadByAtsTeamWorkflow, changed artifacts already loaded."
                   );
         //@formatter:on
      }

      this.atsTeamWorkflowLoader.load(headerArtifacts, artifactIdentifier, this, this.branchSpecification,
         this.changedArtifactsMap.mapView());

      this.changedArtifactsLoaded = true;
   }

   /**
    * Loads all artifacts from a branch with a transaction comment that indicates the artifact has been modified on the
    * branch.
    *
    * @param branchSpecification the branch to load modified artifacts from.
    * @throws NullPointerException when the parameter <code>branchSpecification</code> is <code>null</code>.
    */

   public void loadByTransactionComment(BranchSpecification branchSpecification) {

      if (this.changedArtifactsLoaded) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      "PublishingArtifactLoader::loadByAtsTeamWorkflow, changed artifacts already loaded."
                   );
         //@formatter:on
      }

      Objects.requireNonNull(branchSpecification,
         "ChangedArtifactsTracker::loadByTransactionComment, parameter \"branchSpecification\" cannot be null.");

      //@formatter:off
      this.streamChangedArtifactReadables( branchSpecification )
         .collect
            (
               Collectors.toMap
                  (
                     ArtifactId::create,
                     Function.identity(),
                     ( a, b ) -> a,
                     () -> this.changedArtifactsMap.mapView()
                  )
            );
      //@formatter:on
      this.changedArtifactsLoaded = true;
   }

   private Optional<List<PublishingArtifact>> mergeJustLoadedArtifacts(BranchIndicator branchIndicator,
      ArrayList<PublishingArtifact> artifacts, FilterForView filterForView, WhenNotFound whenNotFound) {

      List<PublishingArtifact> notFoundArtifacts = null;

      for (var i = 0; i < artifacts.size(); i++) {

         var artifact = artifacts.get(i);

         if (artifact.isFound()) {
            continue;
         }

         Object artifactIdentifier;
         IdentifierTypeIndicator identifierTypeIndicator;

         var artifactId = artifact.getArtifactId();

         if (artifactId.isValid()) {
            artifactIdentifier = artifactId;
            identifierTypeIndicator = IdentifierTypeIndicator.ARTIFACT_ID;
         } else {
            artifactIdentifier = artifact.getGuid();
            identifierTypeIndicator = IdentifierTypeIndicator.GUID;
         }

         //@formatter:off
         var triplet =
            this.getCachedArtifact
               (
                  branchIndicator,
                  identifierTypeIndicator,
                  artifactIdentifier,
                  filterForView,
                  WhenNotFound.NOT_FOUND
               );
         //@formatter:on

         var cacheResult = triplet.getFirst();
         var artifactFromCache = triplet.getSecond().getValue();

         if (cacheResult.isUse()) {
            artifacts.set(i, artifactFromCache);
            continue;
         }

         //@formatter:off
         notFoundArtifacts =
            Objects.isNull( notFoundArtifacts )
               ? new LinkedList<>()
               : notFoundArtifacts;
         //@formatter:on

         notFoundArtifacts.add(artifactFromCache);

         switch (whenNotFound) {
            case EMPTY:
            case ERROR:
               artifacts.remove(i);
               i--;
               break;

            case SENTINEL:
               artifacts.set(i, PublishingArtifact.SENTINEL);
               break;
         }

      }

      return Optional.ofNullable(notFoundArtifacts);
   }

   /**
    * Determines the hierarchical position of the artifact.
    *
    * @param artifact the artifact to determine the position of.
    * @implNote If the hierarchical position has already been determined this method will just return. When a
    * hierarchical parent is encountered that has it's hierarchical position determined, ascension up the hierarchy tree
    * will stop and that parents hierarchical position will be used. When the hierarchical position is determined for an
    * artifact, that information will be used to set the hierarchical position of all of its parents.
    */

   private void setHierarchyPosition(PublishingArtifact artifact) {

      var cursor = artifact;

      //@formatter:off
      while(    !artifact.isHierarchyPositionSet()
             && cursor.isFound()
             && !cursor.getId().equals( CoreArtifactTokens.DefaultHierarchyRoot.getId() ) ) {
         cursor = this.getLocalHierarchyPosition( artifact, cursor );
      }

      artifact.setHierarchyPosition();

      PublishingArtifact prior;

      for( prior  = artifact,
           cursor = this.getParent( artifact, FilterForView.NO, WhenNotFound.EMPTY ).orElseGet( (PublishingArtifact) null );

              Objects.nonNull( cursor )
           && !cursor.isHierarchyPositionSet()
           && !cursor.getId().equals( CoreArtifactTokens.DefaultHierarchyRoot.getId() );

           prior  = cursor,
           cursor = this.getParent( cursor, FilterForView.NO, WhenNotFound.EMPTY ).orElseGet( (PublishingArtifact) null ) ) {

          var priorHierarchyPosition = prior.getHierarchyPosition();

          assert priorHierarchyPosition.size() >= 2 : "bad index";

          cursor.setHierarchyPosition( priorHierarchyPosition.subList( 0, priorHierarchyPosition.size() - 1) );
      }
      //@formatter:on
   }

   /**
    * Sorts the list of artifacts in hierarchical order.
    *
    * @param artifacts the list of artifacts to be sorted. The list must be modifiable.
    */

   public void sort(@NonNull List<@NonNull PublishingArtifact> artifacts) {

      //@formatter:off
      Conditions.require
         (
            artifacts,
            Conditions.ValueType.PARAMETER,
            "artifacts",
            "cannot be null or contian null elements",
            Conditions.or
               (
                  Objects::isNull,
                  Conditions::collectionContainsNull
               ),
            NullPointerException::new
         );

      artifacts
         .stream()
         .peek( this::loadAndCacheChildren )
         .forEach(this::setHierarchyPosition);

      artifacts.sort(new HierarchyComparator());
      //@formatter:on
   }

   /**
    * Streams all artifacts on the <code>branchSpecification</code> branch that have a transaction comment that
    * indicates the artifact was modified on the branch.
    *
    * @param branchSpecification the branch to stream changed artifacts from.
    * @return a {@link Stream} of the changed artifacts as {@link ArtifactReadable} objects.
    */

   private Stream<PublishingArtifact> streamChangedArtifactReadables(BranchSpecification branchSpecification) {
      //@formatter:off
      return
         this.dataAccessOperations
            .getArtifactIdentifiersFilterByTxCommentForChange(branchSpecification)
            .flatMapValue
               (
                  ( artifactIdentifiers )                      -> this.getPublishingArtifactsByArtifactIdentifiers
                                                                     (
                                                                        BranchIndicator.PUBLISHING_BRANCH,
                                                                        artifactIdentifiers,
                                                                        FilterForView.NO,
                                                                        WhenNotFound.EMPTY,
                                                                        TransactionId.SENTINEL,
                                                                        IncludeDeleted.NO
                                                                     ),
                  ( artifactIdentifiers, dataAccessException ) -> (DataAccessException) dataAccessException
               )
            .orElseGet
               (
                  ( dataAccessException ) ->  Result.ofValue( List.of() ),
                  List::of
               )
            .stream()
            .filter( PublishingArtifact::isFound )
            .peek( PublishingArtifact::setChanged );
      //@formatter:on
   }

   /**
    * Generates a {@link Stream} of {@link PublishingArtifact} {@link Pair}s where:
    * <dl>
    * <dt>First:</dt>
    * <dd>is the linked from {@link PublishingArtifact}.</dd>
    * <dt>Second:</dt>
    * <dd>is the linked to unbookmarked {@link PublishingArtifact}.</dd>
    * </dl>
    *
    * @return a {@link Stream} of {@link PublishingArtifact} {@link Pair}s.
    */

   public Stream<Pair<PublishingArtifact, PublishingArtifact>> streamUnbookmarkedHyperLinkPairs() {
      //@formatter:off
      return
         this.publishingArtifactByArtifactIdentifierMap
            .values( BranchIndicator.PUBLISHING_BRANCH )
            .orElseThrow()
            .stream()
            .filter( PublishingArtifact::isFound)
            .filter( Predicate.not( PublishingArtifact::isBookmarked ) )
            .flatMap
               (
                  ( hyperlinkToUnbookmarkedArtifact ) -> hyperlinkToUnbookmarkedArtifact
                                                            .streamHyperlinkedFrom()
                                                            .map
                                                               (
                                                                  ( hyperlinkFromArtifact ) -> Pair.createNonNull
                                                                                                  (
                                                                                                     hyperlinkFromArtifact,
                                                                                                     hyperlinkToUnbookmarkedArtifact
                                                                                                  )
                                                               )
               );
      //@formatter:on
   }

   private @NonNull PublishingArtifact switchIfCached(@NonNull PublishingArtifact artifact) {

      final var branchIndicator = this.determineBranchIndicator(artifact);
      final var artifactIdentifier = ArtifactId.valueOf(artifact.getId());

      //@formatter:off
      final var triplet =
         getCachedArtifact
            (
               branchIndicator,
               IdentifierTypeIndicator.ARTIFACT_ID,
               artifactIdentifier,
               FilterForView.NO,
               WhenNotFound.EMPTY
            );
      //@formatter:on

      final var result = triplet.getSecondNonNull();

      if (result.isEmpty()) {
         return artifact;
      }

      final var cachedArtifact = result.getValue();

      return cachedArtifact;

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Publishing Artifact Loader" )
         .indentInc()
         .segment( "Cached By ArtifactId",                      this.publishingArtifactByArtifactIdentifierMap.size() )
         .segment( "Cached By GUID",                            this.publishingArtifactByGuidMap.size()               )
         .segment( "Cache Hit Count",                           this.cacheHit                                         )
         .segment( "Cache Miss Count",                          this.cacheMiss                                        )
         .segment( "Child Cache Hit Count",                     this.childCacheHit                                    )
         .segment( "Child Cache Miss COunt",                    this.childCacheMiss                                   )
         .segment( "Get Parent Requests",                       this.getParentCount                                   )
         .segment( "Get Children Requests",                     this.getChildrenCount                                 )
         .segment( "Cache Applicibility Test Count",            this.cacheTest                                        )
         .segment( "Clear Branch View Count",                   this.cacheClearBranchView                             )
         .segment( "Drop Count",                                this.dropCount                                        )
         .segment( "Set Applicable Count",                      this.cacheSetApplicable                               )
         .segment( "Load By Id Cache Hit Count",                this.loadByIdCacheHitCount                            )
         .segment( "Maximum Single Load By Id Cache Hit Count", this.maxLoadByIdCacheHitCount                         )
         .segment( "Maximum Load Request",                      this.maxLoadRequest                                   )
         .segment( "Maximum Loaded",                            this.maxLoaded                                        )
         .indentDec();
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

   private Optional<DataAccessException> validateCached(@NonNull PublishingArtifact artifact) {

      final var branchIndicator = this.determineBranchIndicator(artifact);
      final var artifactIdentifier = ArtifactId.valueOf(artifact.getId());

      //@formatter:off
      final var triplet =
         getCachedArtifact
            (
               branchIndicator,
               IdentifierTypeIndicator.ARTIFACT_ID,
               artifactIdentifier,
               FilterForView.NO,
               WhenNotFound.EMPTY
            );
      //@formatter:on

      final var result = triplet.getSecondNonNull();

      if (result.isEmpty()) {
         return Optional.of(new DataAccessException("artifact is not in cache", Cause.NOT_FOUND));
      }

      final var cachedArtifact = result.getValue();

      if (cachedArtifact != artifact) {
         return Optional.of(new DataAccessException("artifact is not the cached artifact", Cause.ERROR));
      }

      return Optional.empty();
   }

}
