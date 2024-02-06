/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.publishing.DataRightAnchor;
import org.eclipse.osee.framework.core.publishing.DataRightResult;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * An implementation of the {@link DataRightsOperations} interface.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class DataRightsOperationsImpl implements DataRightsOperations {

   /**
    * Saves the single instance of the {@link DataRightsOperationsImpl}.
    */

   private static DataRightsOperationsImpl dataRightsOperationsImpl = null;

   /**
    * Gets or creates the single instance of the {@link DataRightsOperationsImpl}.
    *
    * @param orcsApi a handle to the {@link OrcsApi}.
    * @return this single {@link DataRightsOperationsImpl} object.
    * @throws NullPointerException when the single instance must be created and the <code>orcsApi</code> or it's
    * {@link QueryFactory} are <code>null</code>.
    */

   public synchronized static DataRightsOperationsImpl create(OrcsApi orcsApi) {

      //@formatter:off
      return
         Objects.isNull( DataRightsOperationsImpl.dataRightsOperationsImpl )
            ? (
                DataRightsOperationsImpl.dataRightsOperationsImpl =
                   new DataRightsOperationsImpl
                          (
                             Objects.requireNonNull
                                (
                                    Objects.requireNonNull( orcsApi ).getQueryFactory()
                                )
                          )
              )
            : DataRightsOperationsImpl.dataRightsOperationsImpl;
      //@formatter:on
   }

   /**
    * Frees the instances of the {@link DataRightsOperations} implementations.
    */

   public synchronized static void free() {

      DataRightsOperationsImpl.dataRightsOperationsImpl = null;

   }

   /**
    * Caches the {@link DataRightClassificationMap}.
    */

   private DataRightClassificationMap dataRightClassificationMap;

   /**
    * Saves a handle to the {@link QueryFactory} from the {@link OrcsApi}.
    */

   private final QueryFactory queryFactory;

   /**
    * Private constructor creates the single instance of the {@link DataRightsOperationsImpl}.
    *
    * @param queryFactory a handle to the {@link QueryFactory} from the {@Link OrcsApi}.
    */

   private DataRightsOperationsImpl(QueryFactory queryFactory) {
      this.queryFactory = queryFactory;
      this.dataRightClassificationMap = null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {

      synchronized (DataRightsOperationsImpl.dataRightsOperationsImpl) {
         this.dataRightClassificationMap = null;
      }
   }

   /**
    * Creates a map of {@link DataRightAnchor} objects by {@link ArtifactId} for each artifact represented on the
    * <code>dataRightEntryList</code> with the flags <code>newFooter</code> and <code>isContinuous</code> set as
    * specified in the table:
    *
    * <pre>
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * | First Artifact | Classification !=       | Orientation !=       || Current Artifact | Current Artifact  | Previous Artifact |
    * |                | Previous Classification | Previous Orientation || newFooter        | isContinuous      | isContinuous      |
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * | true           | N/A                     | N/A                  || true             | true              | N/A               |
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * | false          | true                    | N/A                  || true             | N/A               | false             |
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * | false          | false                   | false                || false            | N/A               | true              |
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * | false          | false                   | true                 || false            | N/A               | false             |
    * +----------------+-------------------------+----------------------++------------------+-------------------+-------------------+
    * </pre>
    *
    * @param dataRightEntryList an ordered list of the data right classifications, possibly overridden, of the artifact
    * sequence to analyze.
    * @param sourceDataRightClassificationMap a mapping of the data right footer Word ML for each data right
    * classification.
    * @return a closed {@link DataRightAnchors} map.
    */

   private DataRightResult findSequences(DataRightEntryList dataRightEntryList) {

      var dataRightClassificationMap = this.getDataRightsClassificationMap();

      var dataRightAnchors = new DataRightAnchors();

      try (var dataRightAnchorsAutoClose = dataRightAnchors;) {

         DataRightEntry previousArtifact = null;
         DataRightAnchor previousDataRightAnchor = null;
         DataRightAnchor currentDataRightAnchor = null;

         for (var currentArtifact : dataRightEntryList) {

            var dataRight = dataRightClassificationMap.get(currentArtifact.getClassification());

            var newFooter = this.isNewFooter(currentArtifact, previousArtifact);

            currentDataRightAnchor = dataRightAnchors.add(currentArtifact.getId(), dataRight, newFooter);

            /*
             * if not first artifact in the sequence
             */

            if (Objects.nonNull(previousArtifact) && Objects.nonNull(previousDataRightAnchor)) {

               var isContinuousForPrevious = this.isContinuousForPrevious(currentArtifact, previousArtifact);

               previousDataRightAnchor.setIsContinuous(isContinuousForPrevious);
            }

            previousArtifact = currentArtifact;
            previousDataRightAnchor = currentDataRightAnchor;
         }

         /*
          * if sequence was not empty, set the last artifact
          */

         if (Objects.nonNull(currentDataRightAnchor)) {
            currentDataRightAnchor.setIsContinuous(false);
         }

      }

      var dataRightAnchorsResult = new DataRightResult(dataRightAnchors.stream());

      return dataRightAnchorsResult;

   }

   /**
    * {@inheritDoc}
    *
    * @see {@link DataRightsOperations}.
    * @implNote This method is for REST API calls and Operations calls.
    */

   @Override
   public DataRightResult getDataRights(BranchId branchIdentifier, List<ArtifactId> artifactIdentifiers) {
      return getDataRights(branchIdentifier, "", artifactIdentifiers);
   }

   /**
    * {@inheritDoc}
    *
    * @see {@link DataRightsOperations}.
    * @implNote This method is for REST API calls and Operations calls.
    */

   @Override
   public DataRightResult getDataRights(BranchId branchIdentifier, String overrideClassification, List<ArtifactId> artifactIdentifiers) {

      Message message = null;

      //@formatter:off
      message =
         Conditions.require
            (
               message,
               branchIdentifier,
               ValueType.PARAMETER,
               "DataRightsOperationsImpl",
               "getDataRights",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() < 0L
            );

      message =
         Conditions.requireNonNull
            (
               message,
               overrideClassification,
               "DataRightsOperationsImpl",
               "getDataRights",
               "overrideClassification"
            );

      message =
         Conditions.require
            (
               message,
               artifactIdentifiers,
               ValueType.PARAMETER,
               "DataRightsOperationsImpl",
               "getDataRights",
               "artifactIdentifiers",
               "cannot be null",
               Objects::isNull,
               "artifact identifiers list is not empty, does not contain a null element, and does not contain a negative artifact identifier",
               Conditions.<List<ArtifactId>>predicate( List::isEmpty )
                  .or( Conditions.collectionContainsNull )
                  .or( Conditions.collectionElementPredicate( ( p ) -> p.getId() < 0L ) )
            );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Conditions.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "getDataRights",
                            message
                         )
                   );
      }
      //@formatter:on

      /*
       * Load the publishing artifacts
       */

      var artifactMap = this.loadArtifactMap(branchIdentifier, artifactIdentifiers);

      /*
       * Create a list of records containing the artifactId, data right enum, and page orientation.
       */

      var dataRightEntryList = this.populateRequest(artifactIdentifiers, artifactMap, overrideClassification);

      /*
       * Determine runs of artifacts with the same data rights.
       */

      var dataRightAnchorsResult = this.findSequences(dataRightEntryList);

      return dataRightAnchorsResult;
   }

   /**
    * {@inheritDoc}
    *
    * @see {@link DataRightsOperations}.
    * @implNote This method is for Operations calls only.
    */

   @Override
   public DataRightResult getDataRights(List<ArtifactId> artifactIdentifiers, Map<ArtifactId, ArtifactReadable> artifactMap, String overrideClassification) {

      Message message = null;

      //@formatter:off
      message =
         Conditions.require
            (
               message,
               artifactIdentifiers,
               ValueType.PARAMETER,
               "DataRightsOperationsImpl",
               "getDataRights",
               "artifactIdentifiers",
               "cannot be null",
               Objects::isNull,
               "artifact identifier list is not empty, does not contain a null element, and does not contain a negative artifact identifier",
               Conditions.<List<ArtifactId>>predicate( List::isEmpty )
                  .or( Conditions.collectionContainsNull )
                  .or( Conditions.collectionElementPredicate( ( p ) -> p.getId() < 0l ) )
            );

      message =
         Conditions.requireNonNull
            (
               message,
               artifactIdentifiers,
               "DataRightsOperationsImpl",
               "getDataRights",
               "artifacts"
            );

      message =
         Conditions.requireNonNull
            (
               message,
               overrideClassification,
               "DataRightsOperationsImpl",
               "getDataRights",
               "overrideClassification"
            );

      message =
         Conditions.require
            (
               message,
               artifactMap,
               ValueType.PARAMETER,
               "DataRightsOperationsImpl",
               "getDataRights",
               "artifactMap",
               "cannot be null",
               Objects::isNull,
               "",
               Conditions.mapContainsNullKey
                  .or( Conditions.mapContainsNullValue )
            );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Conditions.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "getDataRights",
                            message
                         )
                   );
      }
      //@formatter:on

      /*
       * Create a list of records containing the artifactId, data right enum, and page orientation.
       */

      var dataRightEntryList = this.populateRequest(artifactIdentifiers, artifactMap, overrideClassification);

      /*
       * Determine runs of artifacts with the same data rights.
       */

      var dataRightAnchorsResult = this.findSequences(dataRightEntryList);

      return dataRightAnchorsResult;
   }

   /**
    * Read and parse the data rights footer contents from the common branch data rights mapping artifact, if not already
    * cached.
    *
    * @return a map of the data right footers by data right classification name.
    */

   private DataRightClassificationMap getDataRightsClassificationMap() {

      DataRightClassificationMap dataRightClassificationMap;

      synchronized (DataRightsOperationsImpl.dataRightsOperationsImpl) {
         if (Objects.isNull(this.dataRightClassificationMap)) {
            this.dataRightClassificationMap =
               DataRightClassificationMap.create(this.queryFactory.fromBranch(CoreBranches.COMMON));
         }
         dataRightClassificationMap = this.dataRightClassificationMap;
      }

      return dataRightClassificationMap;

   }

   /**
    * Determines if the previous artifact is not the last artifact in a sequence of artifacts with the same Word ML
    * footer. A change in data rights classification or a change in page orientation will require new Word ML.
    *
    * <pre>
    * +-------------------------+---------------------------++-----------------------+
    * | current classification  | current page orientation  || not the last artifact |
    * |         !=              |         !=                || in the sequence       |
    * | previous classification | previous page orientation || (isContinuous)        |
    * +-------------------------+---------------------------++-----------------------+
    * | true                    | true                      || false                 |
    * +-------------------------+---------------------------++-----------------------+
    * | true                    | false                     || false                 |
    * +-------------------------+---------------------------++-----------------------+
    * | false                   | true                      || false                 |
    * +-------------------------+---------------------------++-----------------------+
    * | false                   | false                     || true                  |
    * +-------------------------+---------------------------++-----------------------+
    * </pre>
    *
    * @param current {@link DataRightEntry} for the current artifact in the sequence.
    * @param previous {@link DataRightEntry} for the previous artifact in the sequence.
    * @return the flag value.
    */

   private boolean isContinuousForPrevious(DataRightEntry current, DataRightEntry previous) {

      //@formatter:off
      return
            current.getClassification().equals(previous.getClassification())
         && current.getOrientation().equals(previous.getOrientation());
      //@formatter:on
   }

   /**
    * Determines if the current artifact is the first artifact in a sequence thus requiring new data rights Word ML.
    * footer.
    *
    * <pre>
    * +----------------+-------------------------++----------------------+
    * | first artifact | current classification  || Need new data rights |
    * | in sequence    |         !=              || Word ML              |
    * |                | previous classification || (isNewFooter)        |
    * +----------------+-------------------------++----------------------+
    * | true           | N/A                     || true                 |
    * +----------------+-------------------------++----------------------+
    * | false          | true                    || true                 |
    * +----------------+-------------------------++----------------------+
    * | false          | false                   || false                |
    * +----------------+-------------------------++----------------------+
    * </pre>
    *
    * @param current {@link DataRightEntry} for the current artifact in the sequence.
    * @param previous {@link DataRightEntry} for the previous artifact in the sequence. This parameter maybe
    * <code>null</code>.
    * @return the flag value.
    */

   private boolean isNewFooter(DataRightEntry current, DataRightEntry previous) {

      //@formatter:off
      return
            Objects.isNull( previous )
         || !current.getClassification().equals( previous.getClassification() );
      //@formatter:on
   }

   /**
    * Loads the {@link ArtifactReadable} objects for the publish artifacts to get the data rights for.
    *
    * @param branchIdentifier the branch and applicability view to get the publishing artifacts from.
    * @param artifactIdentifiers a list of the identifiers for the artifacts to be published.
    * @return the publishing artifacts.
    * @throws OseeCoreException when a failure occurs loading the publishing artifacts from the database.
    */

   private Map<ArtifactId, ArtifactReadable> loadArtifactMap(BranchId branchIdentifier, List<ArtifactId> artifactIdentifiers) {

      try {
         return this.queryFactory.fromBranch(branchIdentifier).andIds(artifactIdentifiers).asArtifactMap();
      } catch (Exception e) {
         throw new OseeCoreException(
            "DataRightsOperationsImpl::loadArtifactMap, failed to obtain artifacts from the database.", e);
      }

   }

   /**
    * Loads the artifacts specified by the <code>artifactIdentifier</code> list and extracts the data rights
    * classification and page orientation from each artifact's attributes. The data is saved in an ordered list of
    * {@link DataRightEntry} objects matching the the order of the provided <code>artifactIdentifiers</code>. The
    * {@link ArtifactReadable} objects obtained from the data base are not retained.
    *
    * @param artifactIdentifiers the list of artifacts to get data rights for.
    * @param branchIdentifier the branch to get the artifacts from.
    * @param overrideClassification when specified, this will be used as each artifact's data right classification
    * instead of the classification specified by the artifacts data rights attributes.
    * @return a {@link DataRightEntryList}.
    */

   private DataRightEntryList populateRequest(List<ArtifactId> artifactIdentifiers, Map<ArtifactId, ArtifactReadable> artifactMap, String overrideClassification) {

      try (var dataRightEntryList = new DataRightEntryList(overrideClassification)) {

         /*
          * The requested sequence order of artifacts might not match the hierarchical order of artifacts read from the
          * database. Artifacts are requested as a map and then selected based on the requested artifact order.
          */

         for (ArtifactId artifactId : artifactIdentifiers) {

            var artifactReadable = artifactMap.getOrDefault(artifactId, ArtifactReadable.SENTINEL);

            dataRightEntryList.add(artifactId, artifactReadable);
         }

         return dataRightEntryList;
      }
   }

}

/* EOF */
