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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * This class implements publishing and rendering methods in a client/server agnostic way. This allows for the
 * consolidation of similar logic between the client and server publishing functions.
 *
 * @author Loren K. Ashley
 */

public class WordRenderUtil {

   /**
    * A functional interface for methods that test if an artifact is ok to include in the publish.
    */

   @FunctionalInterface
   public interface ArtifactAcceptor {

      /**
       * Returns an {@link ArtifactAcceptor} predicate that is the logical NOT of the <code>baseArtifactAcceptor</code>.
       *
       * @param baseArtifactAcceptor the {@link ArtifactAcceptor} to create an inverse of.
       * @return an {@link ArtifactAcceptor} that is the logical NOT of the <code>baseArtifactAcceptor</code>.
       */

      default ArtifactAcceptor isKo(ArtifactAcceptor baseArtifactAcceptor) {
         Objects.requireNonNull(baseArtifactAcceptor);
         return (t) -> !isOk(t);
      }

      /**
       * Predicate to determine if the artifact can be included in the publish.
       *
       * @param artifactReadable the {@link ArtifactReadable} to be tested.
       * @return <code>true</code>, when the artifact can be included; otherwise <code>false</code>.
       */

      boolean isOk(ArtifactReadable artifactReadable);
   }

   /**
    * A functional interface used to obtain the data rights for artifacts in the publish.
    */

   @FunctionalInterface
   public interface DataRightsProvider {

      /**
       * @param branchId the branch the publish artifacts are from.
       * @param overrideCalssification when non-<code>null</code> and non-blank, the data rights for each artifact are
       * overridden with this classification.
       * @param artifacts the identifiers of all the artifacts for the publish in publishing order.
       * @return a {@link DataRightResult} with the data rights configuration for the artifacts to be in the publish.
       */

      DataRightResult getDataRights(BranchId branchId, String overrideCalssification, List<ArtifactId> artifacts);
   }

   /**
    * Get the data rights for artifacts in the publish.
    *
    * @param artifacts the top level artifacts for the publish.
    * @param branchId the branch the artifacts for the publish are from.
    * @param recurse when <code>true</code>, the descendants of the top level artifacts will be included in the data
    * rights request.
    * @param notHistorical when <code>true</code> and <code>recurse</code> is <code>true</code>, descendants of
    * historical top level artifacts and historical descendants will be excluded.
    * @param overrideClassification when non-<code>null</code> and non-blank, the data rights for each artifact are
    * overridden with this classification.
    * @param descendantArtifactAcceptor a predicate used to accept or reject each descendant.
    * @param dataRightsProvider used to request the data rights of artifacts for the publish from the Data Rights
    * Manager. Client and Server implementations of this functional interface will be different.
    * @return a {@link DataRightContentBuilder} which can be used to obtain the data rights footer for each artifact in
    * the publish.
    * @throws OseeCoreException when a failure occurred obtaining the data rights.
    */

   public static Optional<DataRightContentBuilder> getDataRights(List<PublishingArtifact> artifacts, BranchId branchId, boolean recurse, boolean notHistorical, String overrideClassification, ArtifactAcceptor descendantArtifactAcceptor, DataRightsProvider dataRightsProvider) {

      //@formatter:off
      assert
           Objects.nonNull( branchId )
         : "WordRenderUtil::getDataRights, parameter \"branchId\" cannot be null.";

      assert
           Objects.nonNull( dataRightsProvider )
         : "WordRenderUtil::getDataRights, parameter \"dataRightsProvider\" cannot be null.";
      //@formatter:on

      try {

         if (Objects.isNull(artifacts) || artifacts.isEmpty()) {
            return Optional.empty();
         }

         var allArtifacts =
            WordRenderUtil.getPublishArtifacts(artifacts, recurse, notHistorical, descendantArtifactAcceptor);

         if (allArtifacts.isEmpty()) {
            return Optional.empty();
         }

         var dataRightResult = dataRightsProvider.getDataRights(branchId, overrideClassification,
            allArtifacts.stream().map(ArtifactId::create).collect(Collectors.toList()));

         var dataRightContentBuilder = new DataRightContentBuilder(dataRightResult);

         return Optional.of(dataRightContentBuilder);

      } catch (Exception e) {

         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "WordRenderUtil::getDataRights, failed to obtain data rights for publishing artifacts." )
                             .indentInc()
                             .segment( "Publishing Branch Identifier",   branchId                             )
                             .segment( "Recursive",                      recurse                              )
                             .segment( "Not Historical",                 notHistorical                        )
                             .segment( "Override Classification",        overrideClassification               )
                             .segment( "Top Level Publishing Artifacts", artifacts, PublishingArtifact::getId )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
         //@formatter:on
      }

   }

   /**
    * Gets the page orientation from the <code>artifact</code>'s {@link CoreAttributeTypes#pageOrientation} attribute.
    * The {@link WordCoreUtil.pageType#getDefault()} will be used if unable to read the artifact's attribute or if the
    * artifact is <code>null</code> or {@link Artifact#SENTINEL}.
    *
    * @param artifact the artifact to extract the page orientation from.
    * @return the page orientation.
    */

   public static WordCoreUtil.pageType getPageOrientation(PublishingArtifact artifact) {

      var defaultPageType = WordCoreUtil.pageType.getDefault();

      try {

         if (Objects.isNull(artifact) || artifact.isInvalid()) {
            return defaultPageType;
         }

         if (!artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
            return defaultPageType;
         }

         var pageTypeString =
            artifact.getSoleAttributeAsString(CoreAttributeTypes.PageOrientation, defaultPageType.name());

         return WordCoreUtil.pageType.fromString(pageTypeString);

      } catch (Exception e) {

         return defaultPageType;

      }
   }

   /**
    * Creates a new {@link List} containing the {@link PublishingArtifact} objects contained in the provided
    * {@link List} and optionally their hierarchical descendants when performing a publish.
    *
    * @param artifact the list or {@link PublishingArtifact}to copy or expand.
    * @param recurse when <code>true</code>, descendants will be included.
    * @param notHistorical when <code>true</code> and <code>recurse</code> is <code>true</code>, descendants of
    * historical artifacts on the <code>artifacts</code> will not have their descendants included and historical
    * descendants will also be excluded. Any historical artifacts on the <code>artifacts</code> list will be copied to
    * the output list.
    * @param descendantArtifactAcceptor descendant artifacts rejected by the {@link DescendantArtifactAcceptor} will
    * excluded from the output list.
    * @return a new {@link List} of the provided {@link ArtifactReadble} objects and possibly including their
    * descendants.
    */

   public static List<PublishingArtifact> getPublishArtifacts(List<PublishingArtifact> artifacts, boolean recursive, boolean notHistorical, ArtifactAcceptor descendantArtifactAcceptor) {

      if (Objects.isNull(artifacts) || artifacts.isEmpty()) {
         return null;
      }

      /*
       * Initialize start of section and end of section flags for top level artifacts
       */

      var artifactIterator = artifacts.iterator();

      var artifact = artifacts.iterator().next();

      artifact.setStartOfSection();
      artifact.clearEndOfSection();
      artifact.setOutlineLevel(0);

      while (artifactIterator.hasNext()) {
         artifact = artifactIterator.next();
         artifact.setStartOfSection();
         artifact.clearEndOfSection();
         artifact.setOutlineLevel(0);
      }

      artifact.setEndOfSection();

      var allArtifacts = new LinkedList<PublishingArtifact>();
      var checkSet = recursive ? new HashSet<ArtifactId>() : null;

      WordRenderUtil.loadChildrenRecursive(allArtifacts, checkSet, artifacts, 0, recursive, notHistorical,
         descendantArtifactAcceptor);

      return allArtifacts;
   }

   /**
    * Recursively loads the next level of artifacts for {@link #getPublishArtifacts}.
    *
    * @param allArtifacts level artifacts are appended to this list.
    * @param checkSet set used to skip artifacts that have already been seen.
    * @param levelArtifacts the artifacts on the level to be processed.
    * @param outlineLevel the outline level depth.
    * @param recurse when <code>true</code>, recursive processing of child artifacts is enabled.
    * @param notHistorical when <code>true</code> and <code>recurse</code> is <code>true</code>, descendants of
    * historical artifacts on the <code>artifacts</code> will not have their descendants included and historical
    * descendants will also be excluded. Any historical artifacts on the <code>artifacts</code> list will be copied to
    * the output list.
    * @param descendantArtifactAcceptor artifacts at outline levels greater than 0 rejected by the
    * {@link DescendantArtifactAcceptor} will excluded from the output list.
    */

   private static void loadChildrenRecursive(List<PublishingArtifact> allArtifacts, Set<ArtifactId> checkSet, List<PublishingArtifact> levelArtifacts, int outlineLevel, boolean recurse, boolean notHistorical, ArtifactAcceptor descendantArtifactAcceptor) {

      var artifactIterator = levelArtifacts.iterator();
      PublishingArtifact artifact = null;

      /*
       * Find first artifact of the level and set start of section flag
       */

      while (artifactIterator.hasNext()) {

         artifact = artifactIterator.next();

         //@formatter:off
         if (
                  Objects.isNull(artifact)
               || artifact.isInvalid()
               || ( Objects.nonNull( checkSet ) && checkSet.contains(artifact) )
            )
         //@formatter:on
         {
            continue;
         }

         //@formatter:off
         if (
                  ( outlineLevel > 0 )
               && Objects.nonNull( descendantArtifactAcceptor )
               && !descendantArtifactAcceptor.isOk(artifact)
            )
         //@formatter:on
         {
            checkSet.add(artifact);
            continue;
         }

         if (Objects.nonNull(checkSet)) {
            checkSet.add(artifact);
         }

         allArtifacts.add(artifact);

         if (recurse && (!notHistorical || !artifact.isHistorical())) {
            //@formatter:off
            WordRenderUtil.loadChildrenRecursive
               (
                  allArtifacts,
                  checkSet,
                  artifact.getChildrenAsPublishingArtifacts(),
                  outlineLevel + 1,
                  recurse,
                  notHistorical,
                  descendantArtifactAcceptor
               );
            //@formatter:on
         }
      }

   }

}

/* EOF */
