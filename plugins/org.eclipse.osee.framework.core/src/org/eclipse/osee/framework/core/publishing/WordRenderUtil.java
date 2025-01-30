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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;
import org.eclipse.osee.framework.core.publishing.relation.table.HtmlRelationTableAppender;
import org.eclipse.osee.framework.core.publishing.relation.table.RelationTableAppender;
import org.eclipse.osee.framework.core.publishing.relation.table.RelationTableBuilder;
import org.eclipse.osee.framework.core.publishing.relation.table.RelationTableOptions;
import org.eclipse.osee.framework.core.publishing.relation.table.WordRelationTableAppender;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class implements publishing and rendering methods in a client/server agnostic way. This allows for the
 * consolidation of similar logic between the client and server publishing functions.
 *
 * @author Loren K. Ashley
 */

public class WordRenderUtil {

   @FunctionalInterface
   public interface AttributeTypeTokenAcceptor {

      boolean isOk(AttributeTypeToken attributeTypeToken);
   }

   /**
    * A functional interface for a method to be called once it has been determined that an attribute will be rendered.
    */

   @FunctionalInterface
   public interface AttributeProcessor {

      /**
       * The {@link #process} method of the {@link AttributeProcessor} implementation that was passed to the
       * {@link WordRenderUtil#processAttributes} method is called when it is determined that an attribute will be
       * rendered.
       *
       * @param publishingAppender the {@link PublishingAppender} attributes will be rendered to and with.
       * @param attributeOptions the {@link AttributeOptions} to render the attribute with.
       * @param attributeType the type of attribute to be rendered.
       * @param allAttributes the render all attributes flag.
       * @param includeBookmark specifies whether to include a book mark in the rendered artifact
       */

      void process(PublishingArtifact publishingArtifact, PublishingAppender publishingAppender,
         AttributeOptions attributeOptions, AttributeTypeToken attributeType, boolean allAttributes,
         PresentationType presentationType, boolean publishInLine, String footer, IncludeBookmark includeBookmark);
   }

   @FunctionalInterface
   interface InternalAttributeProcessor {

      /**
       * The {@link #process} method of the {@link AttributeProcessor} implementation that was passed to the
       * {@link WordRenderUtil#processAttributes} method is called when it is determined that an attribute will be
       * rendered.
       *
       * @param publishingAppender the {@link PublishingAppender} attributes will be rendered to and with.
       * @param attributeOptions the {@link AttributeOptions} to render the attribute with.
       * @param attributeType the type of attribute to be rendered.
       * @param allAttributes the render all attributes flag.
       */

      void process(PublishingAppender publishingAppender, AttributeOptions attributeOptions,
         AttributeTypeToken attributeType, boolean allAttributes);
   }

   /**
    * A functional interface for a method to lookup the {@link AttributeTypeToken} for an attribute by the attribute's
    * name. The implementation of this interface is called by {@link WordRenderUtil#processAttributes} to obtain
    * {@link AttributeTypeToken}s.
    */

   @FunctionalInterface
   public interface AttributeTypeFunction extends Function<String, AttributeTypeToken> {
      //methods are inherited.
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

   @FunctionalInterface
   public interface ExceptionHandler extends Consumer<Throwable> {
      //methods are inherited.
   }

   /**
    * A functional interface for a supplier for a sequence of {@link AttributeTypeToken}s for an artifact in rendering
    * order.
    */

   @FunctionalInterface
   public interface OrderedAttributeTypeSupplier extends Supplier<Iterable<AttributeTypeToken>> {
      //methods are inherited.
   }

   /**
    * A functional interface to get the rendered relation order table for an artifact.
    */

   @FunctionalInterface
   public interface RelationOrderFunction extends Function<ArtifactReadable, String> {
      //methods are inherited.
   }

   @FunctionalInterface
   public interface RenderWordTemplateContentOperation extends Function<WordTemplateContentData, Pair<String, Set<String>>> {
      //methods are inherited.
   }

   /**
    * An allowed value for a metadata "attribute" name.
    */

   private static final String APPLICABILITY = "Applicability";

   /**
    * An allowed value for a metadata "attribute" name.
    */

   private static final String ARTIFACT_ID = "Artifact Id";

   /**
    * An allowed value for a metadata "attribute" name.
    */

   private static final String ARTIFACT_TYPE = "Artifact Type";

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

   public static Optional<DataRightContentBuilder> getDataRights(List<PublishingArtifact> artifacts, BranchId branchId,
      boolean recurse, boolean notHistorical, String overrideClassification,
      ArtifactAcceptor descendantArtifactAcceptor, DataRightsProvider dataRightsProvider) {

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
                             .segmentIndexed( "Top Level Publishing Artifacts", artifacts, PublishingArtifact::getId )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
         //@formatter:on
      }

   }

   /**
    * Gets an override paragraph number string from the <code>artifact</code>'s
    * {@link CoreAttributeTypes#ParagraphNumber} attribute. If the attribute does not contains a value or the attribute
    * is not defined for the <code>artifact</code> an outlining number string will be created from the
    * <code>artifact</code>'s hierarchical position.
    *
    * @param artifact the {@link PublishingArtifact} to obtain an override paragraph number from.
    * @param outlineNUmber the {@link OutlineNumber} tracking object for the document, used to test validity of the
    * override outline number.
    * @return when a valid override paragraph number is obtained, an {@link Optional} containing the override paragraph
    * number; otherwise, and empty {@link Optional}.
    */

   private static Optional<String> getOverrideSectionNumber(@NonNull PublishingArtifact artifact,
      @NonNull OutlineNumber outlineNumber) {

      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
         return Optional.empty();
      }

      var paragraphNumber = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, Strings.EMPTY_STRING);

      if (outlineNumber.isValidOutlineNumber(paragraphNumber)) {
         return Optional.of(paragraphNumber);
      }

      final var comparator = new ArtifactHierarchyComparator();

      paragraphNumber = comparator.getHierarchyPosition(artifact);

      if (outlineNumber.isValidOutlineNumber(paragraphNumber)) {
         return Optional.of(paragraphNumber);
      }

      return Optional.empty();
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

   public static List<PublishingArtifact> getPublishArtifacts(List<PublishingArtifact> artifacts, boolean recursive,
      boolean notHistorical, ArtifactAcceptor descendantArtifactAcceptor) {

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
    * Determines the starting paragraph number as follows:
    * <dl>
    * <dt>The <code>artifact</code> or <code>publishingTemplate</code> are <code>null</code>:</dt>
    * <dd>"1"</dd>
    * <dt>The <code>publishingTemplate</code> does not contain an insert artifact here token:</dt>
    * <dd>"1"</dd>
    * <dt>The contents of the {@link CoreAttributeTypes#ParagraphNumber} attribute of the <code>artifact</code> is
    * invalid:</dt>
    * <dd>"1"</dd>
    * <dt>Otherwise:</dt>
    * <dd>The contents of the {@link CoreAttributeTypes#ParagraphNumber} attribute.</dd>
    * </dl>
    *
    * @param artifact the first artifact selected for the publish. Can be null if the list of artifacts is empty.
    * @param publishingTemplate the {@link PublishingTemplate} for the publish.
    * @return the starting paragraph number.
    */

   public static String getStartingParagraphNumber(ArtifactReadable artifact, PublishingTemplate publishingTemplate) {

      var startParagraphNumber = "1";

      //@formatter:off
      if(    Objects.isNull(publishingTemplate)
          || Objects.isNull(artifact)
          || artifact.isInvalid()
          || publishingTemplate.test(WordCoreUtil::isNotArtifactPublishingTemplateInsertToken) /* is nested template? */
          || !artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber) ) {
         return startParagraphNumber;
      }
      //@formatter:on

      var paragraphNumber = artifact.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber, "");

      if (Strings.isInvalidOrBlank(paragraphNumber)) {
         return startParagraphNumber;
      }

      return paragraphNumber;
   }

   /**
    * Predicate to determine if an artifact is to be excluded from the publish based on its artifact type. When the
    * collection of excluded artifact types is <code>null</code> or empty, the artifact will always be included.
    *
    * @param artifact the artifact to be tested.
    * @param excludeArtifactTypes a {@link Collection} of {@link ArtifactTypeToken}s for the artifact types to be
    * excluded.
    * @return <code>true</code> when the artifact is to be excluded; otherwise <code>false</code>.
    */

   //@formatter:off
   public static ArtifactAcceptor getExcludedArtifactTypeArtifactAcceptor(
      @Nullable Collection<ArtifactTypeToken> excludeArtifactTypes) {

      if ((excludeArtifactTypes == null) || excludeArtifactTypes.isEmpty()) {
         return ArtifactAcceptor.ok();
      }

      final var safeExcludeArtifactTypes =
         excludeArtifactTypes
            .stream()
            .filter( Objects::nonNull )
            .filter( ArtifactTypeToken::isValid )
            .collect( org.eclipse.osee.framework.jdk.core.util.Collectors.toArray( (s) -> new ArtifactTypeToken[s] ) );

      return
         new ArtifactAcceptor() {

            @Override
            public boolean isOk(ArtifactReadable artifact) {
               return !artifact.isOfType( safeExcludeArtifactTypes );
            }

            @Override() public String toString() {
               return
                  new Message()
                         .title( "Excluded Artifact Type Artifact Acceptor" )
                         .indentInc()
                         .segmentIndexed( "Artifact Types", safeExcludeArtifactTypes )
                         .toString();
            }

      };
   }
   //@formatter:on

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

   private static void loadChildrenRecursive(List<PublishingArtifact> allArtifacts, Set<ArtifactId> checkSet,
      List<PublishingArtifact> levelArtifacts, int outlineLevel, boolean recurse, boolean notHistorical,
      ArtifactAcceptor descendantArtifactAcceptor) {

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

   /**
    * Starts the recursive heading tree analysis for each artifact on the list of <code>artifacts</code>.
    *
    * @see {@link AllowedOutlineTypes} for how an artifact is determined to be a heading or not.
    * @see {@link IncludeHeadings} for how heading artifacts are determined to be empty or not.
    * @param artifacts a list of the root artifacts.
    * @param includeHeadings specifies the requirements for a heading to not be considered empty. A <code>null</code>
    * value will be treated the same as {@link IncludeHeadings#ALWAYS}.
    * @param allowedOutlineTypes specifies what type of artifacts can be headings. A <code>null</code> value will be
    * treated the same as {@link AllowedOutlineTypes#ANYTHING}.
    * @param headingArtifactTypeToken when <code>allowedOutlineTypes</code> is {@link AllowedOutlineTypes#RESTRICTED}
    * this parameter specifies the artifact type that is allowed to be a heading. This parameter must not be
    * <code>null</code> when <code>allowedOutlineTypes</code> is {@link AllowedOutlineTypes#RESTRICTED}.
    * @param mainContentAttributeTypeToken specifies the attribute that contains the main publishing content.
    * @param excludedArtifactTypeArtifactAcceptor an {@link ArtifactAcceptor} used to determine if an artifact is to be
    * excluded from the publish.
    * @param artifactAcceptor only root artifacts that satisfy the {@link ArtifactAcceptor} will be analyzed.
    * Hierarchical processing will be stopped at any artifact not satisfying the {@link ArtifactAcceptor}.
    * @return an {@link ArtifactAcceptor} that returns <code>true</code> when an artifact is eligible to be a heading.
    * @throws NullPointerException when <code>allowedOutlineTypes<code> is {@link AllowedOutlineTypes#RESTRICTED} and
    * <code>headingArtifactTypeToken</code> is <code>null</code>; or when <code>includeHeadings</code> is
    * {@link IncludeHeadings#ONLY_WITH_MAIN_CONTENT} and <code>mainContentAttributeTypeToken</code> is
    * <code>null</code>.
    */

   //@formatter:off
   public static @NonNull ArtifactAcceptor
      populateEmptyHeaders
         (
            @Nullable List<@Nullable PublishingArtifact> artifacts,
            @Nullable IncludeHeadings                    includeHeadings,
            @Nullable AllowedOutlineTypes                allowedOutlineTypes,
            @Nullable ArtifactTypeToken                  headingArtifactTypeToken,
            @Nullable AttributeTypeToken                 mainContentAttributeTypeToken,
            @Nullable ArtifactAcceptor                   excludedArtifactTypeArtifactAcceptor,
            @Nullable ArtifactAcceptor                   artifactAcceptor
         ) {
      //@formatter:on

      if ((artifacts == null) || artifacts.isEmpty()) {

         /*
          * Nothing to check
          */

         return ArtifactAcceptor.ok();
      }

      if ((includeHeadings == null) || (includeHeadings == IncludeHeadings.ALWAYS)) {
         return ArtifactAcceptor.ok();
      }

      //@formatter:off
      final var safeAllowedOutlineTypes =
         ( allowedOutlineTypes != null )
            ? allowedOutlineTypes
            : AllowedOutlineTypes.ANYTHING;
      //@formatter:on

      //@formatter:off
      final var safeExcludedArtifactTypeArtifactAcceptor =
         ( excludedArtifactTypeArtifactAcceptor != null )
            ? excludedArtifactTypeArtifactAcceptor
            : ArtifactAcceptor.ok();
      //@formatter:on

      //@formatter:off
      final var safeArtifactAcceptor =
         ( artifactAcceptor != null )
            ? artifactAcceptor
            : ArtifactAcceptor.ok();
      //@formatter:on

      //@formatter:off
      final var safeHeadingArtifactTypeToken =
         Conditions.require
            (
               headingArtifactTypeToken,
               Conditions.ValueType.PARAMETER,
               "headingArtifactTypeToken",
               "must not be null when \"allowedOutlineTypes\" is \"RESTRICTED\"",
               ( value ) -> ( safeAllowedOutlineTypes == AllowedOutlineTypes.RESTRICTED ) && ( value == null ),
               NullPointerException::new
            );
      //@formatter:on

      //@formatter:off
      final var safeMainContentAttributeTypeToken =
         Conditions.require
            (
               mainContentAttributeTypeToken,
               Conditions.ValueType.PARAMETER,
               "mainContentAttributeTypeToken",
               "must not be null when \"includeHeadings\" is \"ONLY_WITH_MAIN_CONTENT\".",
               ( value ) -> ( includeHeadings == IncludeHeadings.ONLY_WITH_MAIN_CONTENT ) && ( value == null ),
               NullPointerException::new
            );
      //@formatter:on

      final var emptyHeaders = new HashSet<ArtifactId>();

      for (final var artifact : artifacts) {

         if (artifact == null) {
            continue;
         }

         if (safeArtifactAcceptor.isOk(artifact)) {

            //@formatter:off
            WordRenderUtil.populateEmptyHeadersInt
               (
                  artifact,
                  emptyHeaders,
                  includeHeadings,
                  safeAllowedOutlineTypes,
                  safeHeadingArtifactTypeToken,
                  safeMainContentAttributeTypeToken,
                  safeExcludedArtifactTypeArtifactAcceptor,
                  safeArtifactAcceptor
               );
            //@formatter:on

         }
      }

      //@formatter:off
      return
         emptyHeaders.isEmpty()
            ? ArtifactAcceptor.ok()
            : new ArtifactAcceptor()
              {

                 @Override
                 public boolean isOk(ArtifactReadable artifact) {
                    final var artifactId = ArtifactId.create(artifact);
                    return !emptyHeaders.contains(artifactId);
                 }

                 @Override
                 public String toString() {
                    return
                       new Message()
                              .title( "Empty Headers Artifact Acceptor" )
                              .indentInc()
                              .segment( "Size", emptyHeaders.size() )
                              .segmentIndexed( "Empty Headers", emptyHeaders, Function.identity(), 20 )
                              .toString();
                 }
              };
      //@formatter:on
   }

   /**
    * Recursive method used to determine whether an artifact is an empty heading.
    *
    * @see {@link #populateEmptyHeaders}.
    * @param artifact the hierarchical tree artifact node being analyzed.
    * @param emptyHeaders when an artifact is determined to be a heading and empty, its {@link ArtifactId} is added to
    * this {@link Set}.
    * @param includeHeadings includeHeadings specifies the requirements for a heading to not be considered empty.
    * @param allowedOutlineTypes allowedOutlineTypes specifies what type of artifacts can be headings.
    * @param headingArtifactTypeToken when <code>allowedOutlineTypes</code> is {@link AllowedOutlineTypes#RESTRICTED}
    * this parameter specifies the artifact type that is allowed to be a heading.
    * @param mainContentAttributeTypeToken specifies the attribute that contains the main publishing content.
    * @param excludedArtifactTypeArtifactAcceptor an {@link ArtifactAcceptor} used to determine if an artifact is to be
    * excluded from the publish.
    * @param artifactAcceptor only root artifacts that satisfy the {@link ArtifactAcceptor} will be analyzed.
    * Hierarchical processing will be stopped at any artifact not satisfying the {@link ArtifactAcceptor}.
    * @return a {@link Triplet} with the following Boolean flags:
    * <dl>
    * <dt>First:</dt>
    * <dd>indicates the artifact is to be included in the publish.</dd>
    * <dt>Second:</dt>
    * <dd>indicates the artifact has a descendant that is not a heading.</dd>
    * <dt>Third:</dt>
    * <dd>indicates the artifact has a descendant with main content.</dd>
    * </dl>
    */

   //@formatter:off
   private static @NonNull Triplet<Boolean,Boolean,Boolean>
      populateEmptyHeadersInt
         (
            @NonNull  PublishingArtifact  artifact,
            @NonNull  Set<ArtifactId>     emptyHeaders,
            @NonNull  IncludeHeadings     includeHeadings,
            @NonNull  AllowedOutlineTypes allowedOutlineTypes,
            @Nullable ArtifactTypeToken   headingArtifactTypeToken,
            @Nullable AttributeTypeToken  mainContentAttributeTypeToken,
            @NonNull  ArtifactAcceptor    excludedArtifactTypeArtifactAcceptor,
            @NonNull  ArtifactAcceptor    artifactAcceptor
         ) {

      var artifactIncluded = false;
      var withNonHeading   = !AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY.isAllowed( artifact, headingArtifactTypeToken );
      var withMainContent  = artifact.hasAttributeContent( mainContentAttributeTypeToken );

      final var children =
         artifactAcceptor.isOk( artifact )
            ? artifact.getChildrenAsPublishingArtifacts()
            : List.<PublishingArtifact>of();

      if( children.isEmpty() ) {

         /*
          * At leaf of artifact tree
          */

         switch( includeHeadings ) {

            case ONLY_WITH_NON_HEADING_DESCENDANTS:

               if( withNonHeading ) {
                  artifactIncluded = withNonHeading = excludedArtifactTypeArtifactAcceptor.isOk(artifact);
               } else {
                  if( AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY.isAllowed(artifact, null)) {
                     emptyHeaders.add( ArtifactId.create( artifact ) );
                  }
               }

               break;

            case ONLY_WITH_MAIN_CONTENT:

               if( withMainContent ) {
                  artifactIncluded = withMainContent = excludedArtifactTypeArtifactAcceptor.isOk(artifact);
               } else {
                  if( AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY.isAllowed(artifact, null)) {
                     emptyHeaders.add( ArtifactId.create( artifact ) );
                  }
               }

               break;

            default:

               Conditions.invalidCase(includeHeadings, "includeHeadings", IllegalStateException::new );
         }

         return Triplet.createNonNullImmutable( artifactIncluded, withNonHeading, withMainContent );
      }

      for( final var child: children ) {

         if( child == null ) {
            continue;
         }

         var triplet =
            WordRenderUtil.populateEmptyHeadersInt
               (
                  child,
                  emptyHeaders,
                  includeHeadings,
                  allowedOutlineTypes,
                  headingArtifactTypeToken,
                  mainContentAttributeTypeToken,
                  excludedArtifactTypeArtifactAcceptor,
                  artifactAcceptor
               );

         artifactIncluded |= triplet.getFirst();
         withNonHeading   |= triplet.getSecond();
         withMainContent  |= triplet.getThird();

      }

      if( !artifactIncluded ) {

         switch( includeHeadings ) {

            case ONLY_WITH_NON_HEADING_DESCENDANTS:

               if( withNonHeading ) {
                  artifactIncluded = excludedArtifactTypeArtifactAcceptor.isOk(artifact);
               } else {
                  if( AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY.isAllowed(artifact, null)) {
                     emptyHeaders.add( ArtifactId.create( artifact ) );
                  }
               }

               break;

            case ONLY_WITH_MAIN_CONTENT:

               if( withMainContent ) {
                  artifactIncluded = excludedArtifactTypeArtifactAcceptor.isOk(artifact);
               } else {
                  if( AllowedOutlineTypes.HEADERS_AND_FOLDERS_ONLY.isAllowed(artifact, null)) {
                     emptyHeaders.add( ArtifactId.create( artifact ) );
                  }
               }

               break;

            default:

               Conditions.invalidCase(includeHeadings, "includeHeadings", IllegalStateException::new );
         }

      }

      return Triplet.createNonNullImmutable(artifactIncluded, withNonHeading, withMainContent);
   }
   //@formatter:on

   private static boolean isMainContent(boolean isFirst, String attributeOptionsName,
      AttributeTypeToken contentAttributeType) {

      switch (attributeOptionsName) {
         case "*":
            return isFirst;
         case "<format-content-attribute>":
            return true;
         default:
            return attributeOptionsName.equals(contentAttributeType.getName());
      }
   }

   //@formatter:off
   private static Optional<CharSequence>
      processMainContentAttribute
         (
                     FormatIndicator              formatIndicator,
            @NonNull List<AttributeOptions>       attributeOptionsList,
                     InternalAttributeProcessor   internalAttributeProcessor,
                     AttributeTypeTokenAcceptor   attributeTypeTokenAcceptor,
                     ArtifactReadable             artifact,
                     AttributeTypeToken           contentAttributeType
         ) {
      //@formatter:on

      final var safeAttributeOptionsList = Conditions.requireNonNull(attributeOptionsList, "attributeOptionsList");

      if (safeAttributeOptionsList.isEmpty()) {
         return Optional.empty();
      }

      boolean okToProcess = false;

      final var attributeOptionsIterator = safeAttributeOptionsList.iterator();
      var attributeOptions = attributeOptionsIterator.next();
      var attributeName = attributeOptions.getAttributeName();

      okToProcess = WordRenderUtil.isMainContent(true, attributeName, contentAttributeType);

      if (!okToProcess) {

         while (attributeOptionsIterator.hasNext()) {

            attributeOptions = attributeOptionsIterator.next();
            attributeName = attributeOptions.getAttributeName();

            okToProcess = WordRenderUtil.isMainContent(false, attributeName, contentAttributeType);

            if (okToProcess) {
               break;
            }
         }
      }

      //@formatter:off
      if(    !okToProcess
          || !artifact.isAttributeTypeValid( contentAttributeType )
          || !attributeTypeTokenAcceptor.isOk( contentAttributeType ) ) {
      //@formatter:on
         return Optional.empty();
      }

      final var stringBuilder = new StringBuilder(2048);
      final var publishingAppender = formatIndicator.createPublishingAppender(stringBuilder);

      internalAttributeProcessor.process(publishingAppender, attributeOptions, contentAttributeType, true);

      var result =
         (stringBuilder.length() > 0) ? Optional.<CharSequence> of(stringBuilder) : Optional.<CharSequence> empty();

      return result;
   }

   /**
    * Loops through each attribute element that is to be printed, if * (all attributes), it loops through every valid
    * attribute on that artifact. Also makes sure not to print the headingAttributeType if outlining is enabled.
    * Otherwise it only runs for the specific attribute element. In this default implementation the presentation type is
    * preview.
    *
    * @param formatIndicator the output format of the publish. This parameter is used to determine the attribute type to
    * process when the "AttrType" attribute option is set to "&lt;format-content-attribute&gt;".
    * @param attributeOptionsList a list of the {@link AttributeOptions} from the {@link PublishOptions} for the
    * publish.
    * @param attributeProcessor a callback method to render and attribute.
    * @param attributeTypeFunction a callback method to look up an {@link AttributeTypeToken} by an attribute name.
    * @param orderedAttributeTypeSupplier a supplier method to get a list of the attributes in rendering order for the
    * artifact.
    * @param artifact the artifact whose attributes are to be rendered.
    * @param headingAttributeType the {@link AttributeTypeToken} used to identify artifacts that are headings.
    * @param renderAllAttributes the publishing render all attributes flag.
    */

   //@formatter:off
   private static Optional<CharSequence>
      processAttributes
         (
                     FormatIndicator                  formatIndicator,
            @NonNull List<AttributeOptions>           attributeOptionsList,
                     InternalAttributeProcessor       internalAttributeProcessor,
                     AttributeTypeFunction            attributeTypeFunction,
                     OrderedAttributeTypeSupplier     orderedAttributeTypeSupplier,
                     AttributeTypeTokenAcceptor       attributeTypeTokenAcceptor,
                     ArtifactReadable                 artifact,
                     Optional<CharSequence>           mainContentOptional,
                     OutlineSectionResult             outlineSectionResult,
                     IncludeMainContentForHeadings    includeMainContentForHeadings,
                     AttributeTypeToken               contentAttributeType,
                     boolean                          renderAllAttributes
         ) {

      final var safeAttributeOptionsList = Conditions.requireNonNull( attributeOptionsList, "attributeOptionsList" );

      final var stringBuilder = new StringBuilder(2048);
      final var publishingAppender = formatIndicator.createPublishingAppender(stringBuilder);

      AttributeOptions attributeOptions;

      if(    ( safeAttributeOptionsList.size() == 1 )
          && ( "*".equals( ( attributeOptions = safeAttributeOptionsList.get(0) ).getAttributeName() ) ) ) {

         /*
          * RendererOption is set to process all attributes or all attributes were specified in the publishing
          * template renderer options.
          */

         final var attributeTypeTokens = orderedAttributeTypeSupplier.get();

         for (var attributeTypeToken : attributeTypeTokens ) {

            //@formatter:off
            if(    artifact.isAttributeTypeValid( attributeTypeToken )
                && attributeTypeTokenAcceptor.isOk( attributeTypeToken ) ) {

               if( attributeTypeToken.equals( contentAttributeType ) ) {
                  mainContentOptional.ifPresent( publishingAppender::append );
               } else {
                  internalAttributeProcessor.process( publishingAppender, attributeOptions, attributeTypeToken, true );
               }
            }
            //@formatter:on
         }

         return (stringBuilder.length() > 0) ? Optional.of(stringBuilder) : Optional.empty();
      }

      /**
       * Specific attributes were specified in the publishing template.
       */

      for (final var attributeOptions2 : safeAttributeOptionsList) {

         final var attributeName = attributeOptions2.getAttributeName();

         AttributeTypeToken attributeTypeToken;

         switch (attributeName) {
            case "*":
               continue;
            case "<format-content-attribute>":
               attributeTypeToken = formatIndicator.getContentAttributeTypeToken();
               break;
            default:
               attributeTypeToken = attributeTypeFunction.apply(attributeName);
         }

         //@formatter:off
         if(    artifact.isAttributeTypeValid( attributeTypeToken )
             && attributeTypeTokenAcceptor.isOk( attributeTypeToken ) ) {

            if( attributeTypeToken.equals( contentAttributeType ) ) {
               mainContentOptional.ifPresent( publishingAppender::append );
            } else {
               internalAttributeProcessor.process( publishingAppender, attributeOptions2, attributeTypeToken, true );
            }
         }
         //@formatter:on
      }

      return (stringBuilder.length() > 0) ? Optional.of(stringBuilder) : Optional.empty();

   }

   /**
    * Renders the metadata attributes.
    *
    * @param metadataOptionsArray an array of the {@link MetadataOptions} definitions for the metadata attributes to be
    * rendered.
    * @param applicabilityTokens a {@link Map} of the applicability tokens by applicability identifiers for rendering
    * the "Applicability" metadata attribute.
    * @param artifact the client or server artifact wrapped in an {@link ArtifactReadable}.
    * @param publishingAppender the {@link PublishingAppender} to render the attributes with.
    */

   private static void processMetadataOptions(FormatIndicator formatIndicator, MetadataOptions[] metadataOptionsArray,
      Map<ApplicabilityId, ApplicabilityToken> applicabilityTokens, ArtifactReadable artifact,
      PublishingAppender publishingAppender) {

      if (Objects.isNull(metadataOptionsArray)) {
         return;
      }

      //@formatter:off
      Arrays.asList( metadataOptionsArray )
         .forEach
            (
               (element) ->
               {

                   String name = element.getType();
                   String format = element.getFormat();
                   String label = element.getLabel();
                   String value;

                   switch( name )
                   {
                      case WordRenderUtil.APPLICABILITY:
                      {
                         ApplicabilityToken applicabilityToken;

                         value = artifact.getApplicability().isValid()
                                    ? Objects.nonNull( applicabilityToken = applicabilityTokens.get(artifact.getApplicability() ) )
                                         ? applicabilityToken.getName()
                                         : artifact.getApplicability().getIdString()
                                    : "unknown";
                      }
                      break;

                      case WordRenderUtil.ARTIFACT_TYPE:
                      {
                         value = artifact.getArtifactType().getName();
                      }
                      break;

                      case WordRenderUtil.ARTIFACT_ID:
                      {
                         value = artifact.getIdString();
                      }
                      break;

                      default:
                      {
                         value = "";
                      }
                   }

                   publishingAppender.startParagraph();
                   publishingAppender.append( WordCoreUtil.replaceRendererOptionToken( formatIndicator, label, format, name, value ) );
                   publishingAppender.endParagraph();
               }
            );
      //@formatter:on
   }

   /**
    * Determines if an artifact is not an outline heading.
    *
    * @param artifact the artifact to be tested.
    * @param allowedOutlineTypes specifies the type of artifacts that are eligible to be headings.
    * @param contentAttributeOnly when <code>true</code> all artifacts are not eligible to be headings.
    * @param headingArtifactTypeToken when <code>allowedOutlineTypes</code> is {@link AllowedOutlineTypes#RESTRICTED}
    * artifacts that are not of this type are not eligible to be headings.
    * @param invalidAttributesHandler this call back is invoked when the artifact contains invalid attributes for
    * publishing.
    * @return <code>true</code> when the artifact is an outline heading; otherwise, <code>false</code>.
    */

   //@formatter:off
   private static boolean
      outliningNotAHeading
         (
            @NonNull  PublishingArtifact           artifact,
            @NonNull  AllowedOutlineTypes          allowedOutlineTypes,
                      boolean                      contentAttributeOnly,
            @Nullable ArtifactTypeToken            headingArtifactTypeToken,
            @Nullable Consumer<PublishingArtifact> invalidAttributesHandler
         ) {

      if( contentAttributeOnly ) {
         return true;
      }

      /*
       * Not A Heading when the artifact is not an allowed type for outlining
       */

      final var okToOutline = allowedOutlineTypes.isAllowed(artifact, headingArtifactTypeToken);

      if (!okToOutline) {
         return true;
      }

      //@formatter:off
      final boolean containsExcludedAttributes =
            artifact.isAttributeTypeValid( CoreAttributeTypes.WholeWordContent )
         || artifact.isAttributeTypeValid( CoreAttributeTypes.NativeContent    );
      //@formatter:on

      if (containsExcludedAttributes) {
         if (invalidAttributesHandler != null) {
            invalidAttributesHandler.accept(artifact);
         }
         return true;
      }

      //@formatter:off
      final var publishInline =
         artifact.isAttributeTypeValid( CoreAttributeTypes.PublishInline )
            ? artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false)
            : false;
      //@formatter:on

      if (publishInline) {
         return true;
      }

      return false;
   }

   /**
    * Creates the publish heading text for an artifact.
    *
    * @param formatIndicator the publishing format to generate the heading text in.
    * @param artifact the {@link PublishingArtifact} to be rendered as a section heading.
    * @param headingAttributeTypeToken when non-<code>null</code> and the <code>artifact</code> has a single attribute
    * of the type <code>headingAttributeTypeToken</code> the heading text will be read from the <code>artifact</code>'s
    * <code>headingAttributeTypeToken</code> attribute; otherwise, an empty string will be used for the heading text.
    * @param headingFont when non-<code>null</code> the heading will be rendered with the specified font. The parameter
    * is not effective for all publishing formats.
    * @param headingTextFunction when non-<code>null</code> the heading text will be processed with this
    * {@link Function}.
    * @param includeBookmark specifies whether to insert a book mark into the heading.
    * @param overrideOutlineNumber when <code>true</code> the outlining number will be obtained according to
    * {@link #getOverrideSectionNumber}.
    * @param outlineNumber the {@link OutlineNumber} tracking object.
    * @param updateParagraphNumber when <code>true</code> the {@link BiConsumer} will be invoked with the artifact and
    * the determined outline number for the artifact.
    * @return a {@link CharSequence} with the rendered heading for the artiact.
    */
   //@formatter:off
   private static CharSequence
      outliningCreateHeading
         (
            @NonNull  FormatIndicator                             formatIndicator,
            @NonNull  PublishingArtifact                          artifact,
            @Nullable AttributeTypeToken                          headingAttributeTypeToken,
            @Nullable String                                      headingFont,
            @NonNull  IncludeBookmark                             includeBookmark,
                      boolean                                     overrideOutlineNumber,
            @NonNull  OutlineNumber                               outlineNumber,
            @Nullable BiConsumer<PublishingArtifact,CharSequence> updateParagraphNumber
         ) {
   //@formatter:off

      final var stringBuilder = new StringBuilder( 2048 );
      final var publishingAppender = formatIndicator.createPublishingAppender( stringBuilder );

      //@formatter:off
      CharSequence headingText =
         ( headingAttributeTypeToken != null ) && artifact.isAttributeTypeValid( headingAttributeTypeToken )
            ? artifact.getSoleAttributeAsString(headingAttributeTypeToken, Strings.EMPTY_STRING )
            : Strings.EMPTY_STRING;

      //@formatter:off
      final var paragraphNumberOptional =
            overrideOutlineNumber
               ? WordRenderUtil
                    .getOverrideSectionNumber( artifact, outlineNumber )
                    .map( outlineNumber::setOutlineNumber )
               : outlineNumber.getOutlineNumberString();

      final var paragraphNumber = paragraphNumberOptional.orElse( null );
      final var paragraphLevel = outlineNumber.getOutlineLevel();

      String[] bookmark = null;

      if( includeBookmark.isYes() ) {
         bookmark = WordCoreUtil.getWordMlBookmark( artifact.getId() );
         artifact.setBookmarked();
      }

      publishingAppender.startOutlineSubSection
         (
            bookmark,
            paragraphNumber,
            paragraphLevel,
            headingText,
            null,            /* outlineType */
            headingFont
         );
      //@formatter:on

      /*
       * Update Paragraph Numbers
       */

      if (updateParagraphNumber != null) {
         updateParagraphNumber.accept(artifact, paragraphNumberOptional.orElse(Strings.EMPTY_STRING));
      }

      return stringBuilder;

   }

   /**
    * When the <code>artifact</code> is eligible to be a section heading it is rendered as such according to the
    * publishing format specified by <code>formatIndicator</code>.
    *
    * @param formatIndicator the publishing format to generate the heading text in.
    * @param artifact the {@link PublishingArtifact} to be rendered as a section heading.
    * @param outlineNumber the {@link OutlineNumber} tracking object for the document being published. When
    * <code>overrideOutlineNumber</code> is <code>true</code> a new outline number will be obtained from the
    * <code>artifact</code>'s {@link CoreAttributeTypes#ParagraphNumber} attribute when present and set; or the
    * <code>artifact</code>'s hierarchical position when the attribute not present or unset; and the
    * <code>outlineNumber</code> will be updated with the new outline number.
    * @param headingArtifactTypeToken when <code>outlineOnlyHeadersFolders</code> is <code>true</code>,
    * <code>headingArtifactTypeToken</code> is non-<code>null</code>, and the <code>artifact</code> is of or derived
    * from the artifact type <code>headingArtifactTypeToken</code> this method will return <code>false</code> without
    * performing any actions.
    * @param headingAttributeTypeToken when non-<code>null</code> and the <code>artifact</code> has a single attribute
    * of the type <code>headingAttributeTypeToken</code> the heading text will be read from the <code>artifact</code>'s
    * <code>headingAttributeTypeToken</code> attribute; otherwise, an empty string will be used for the heading text.
    * @param headingTextFunction when non-<code>null</code> the heading text will be processed with this
    * {@link Function}.
    * @param headingFont when non-<code>null</code> the heading will be rendered with the specified font.
    * @param invalidAttributesHandler when non-<code>null</code> and the <code>artifact</code> has either of the
    * attributes {@link CoreAttributeTypes#WholeWordContent} or {@link CoreAttributeTypes#NativeContent} the
    * {@link Consumer} will be called with the invalid <code>artifact</code>.
    * @param updateParagraphNumber when non-<code>null</code> and the <code>artifact</code> is rendered, this
    * {@link BiConsumer} will be called with the <code>artifact</code> and the outline number the artifact was rendered
    * with.
    * @param publishArtifactAcceptor when non-<code>null</code>, the {@link ArtifactAcceptor} predicate will be used to
    * test if the <code>artifact</code> is eligible to be published; otherwise, the <code>artifact</code> will be
    * considered eligible. When the <code>artifact</code> is ineligible, this method will return <code>false</code>
    * without performing any actions.
    * @param contentAttributeOnly flag indicating that only the content attribute is being rendered for the publish.
    * When <code>contentAttributeOnly</code> is <code>true</code>, this method returns <code>false</code> without
    * performing any actions.
    * @param allowedOutlineTypes a member of the {@link AllowedOutlineTypes} enumeration indicating the artifact types
    * allowed for outlining. This method returns <code>false</code> without performing any actions when the
    * <code>artifact</code> is not of an allowed type.
    * @param overrideOutlineNumber When <code>overrideOutlineNumber</code> is <code>true</code>, the outline number will
    * be read from the <code>artifact</code>'s {@link CoreAttributeTypes#ParagraphNumber} attribute when the attribute
    * is present and set; otherwise, the outline number will be determined from <code>artifact</code>'s hierarchical
    * position. When <code>overrideOutlineNumber</code> is <code>false</code>, the current outline number will be
    * obtained from the document outline number tracking object <code>outlineNumber</code>.
    * @param includeBookmark specifies whether to insert a book mark into the heading.
    * @return When the <code>artifact</code> is a section heading an {@link Optional} containing a {@link CharSequence}
    * with the section heading rendered for the publishing format; otherwise, and empty {@link Optional}.
    * @throws NullPointerException when any of the parameters <code>formatIndicator</code>, <code>artifact</code>,
    * <code>outlineNumber</code>, <code>allowedOutlineTypes</code>, or <code>includeBookmar</code> are
    * <code>null</code>.
    */

   //@formatter:off
   private static OutlineSectionResult
      processOutlining
         (
            @NonNull  FormatIndicator                             formatIndicator,
            @NonNull  PublishingArtifact                          artifact,
            @NonNull  OutlineNumber                               outlineNumber,
            @Nullable ArtifactTypeToken                           headingArtifactTypeToken,
            @Nullable AttributeTypeToken                          headingAttributeTypeToken,
            @Nullable String                                      headingFont,
            @Nullable Consumer<PublishingArtifact>                invalidAttributesHandler,
            @Nullable BiConsumer<PublishingArtifact,CharSequence> updateParagraphNumber,
            @Nullable ArtifactAcceptor                            publishArtifactAcceptor,
                      boolean                                     contentAttributeOnly,
            @NonNull  AllowedOutlineTypes                         allowedOutlineTypes,
                      boolean                                     overrideOutlineNumber,
            @NonNull  IncludeBookmark                             includeBookmark
         ) {

      final var safeFormatIndicator = Conditions.requireNonNull( formatIndicator, "formatIndicator");
      final var safeArtifact = Conditions.requireNonNull(artifact,"artifact");
      final var safeOutlineNumber = Conditions.requireNonNull(outlineNumber,"outlineNumber");
      final var safeAllowedOutlineTypes = Conditions.requireNonNull(allowedOutlineTypes,"allowedOutlineTypes");
      final var safeIncludeBookmark = Conditions.requireNonNull(includeBookmark,"includeBookmark");

      /*
       * Exclusions
       */

      /*
       * Exclude non-applicable artifacts
       */

      //@formatter:off
      final var excluded =
         ( publishArtifactAcceptor == null )
            ? false
            : !publishArtifactAcceptor.isOk( artifact );
      //@formatter:on

      if (excluded) {
         return OutlineSectionResult.excluded();
      }

      /*
       * Not A Headings
       */

      //@formatter:off
      final var notAHeading =
         WordRenderUtil.outliningNotAHeading
            (
               safeArtifact,
               safeAllowedOutlineTypes,
               contentAttributeOnly,
               headingArtifactTypeToken,
               invalidAttributesHandler//,
               //publishArtifactAcceptor
            );
      //@formatter:on

      if (notAHeading) {
         return OutlineSectionResult.notAHeading();
      }

      /*
       * Create Heading
       */

      //@formatter:off
      final var headingText =
         WordRenderUtil.outliningCreateHeading
            (
               safeFormatIndicator,
               safeArtifact,
               headingAttributeTypeToken,
               headingFont,
               safeIncludeBookmark,
               overrideOutlineNumber,
               safeOutlineNumber,
               updateParagraphNumber
            );
      //@formatter:on

      return OutlineSectionResult.of(headingText);

   }

   /**
    * This is the fifth level in the main process of publishing. This is where any processing needed happens once it is
    * determined that the artifact will be included in the publish. In the default implementation, we just check
    * outlining and publishInLine to see whether or not to print the header and start the outlining. Then metadata and
    * attributes are processed. The reason this method returns a boolean is to say whether or not the MS Word section
    * was begun with a header.
    */

   //@formatter:off
   public static boolean
      renderArtifact
         (
            boolean                                     allAttributes,
            AllowedOutlineTypes                         allowedOutlineTypes,
            Map<ApplicabilityId, ApplicabilityToken>    applicabilityTokens,
            List<AttributeOptions>                      attributeOptionsList,
            AttributeProcessor                          attributeProcessor,
            PublishingArtifact                          artifact,
            ArtifactAcceptor                            artifactAcceptor,
            Consumer<PublishingArtifact>                artifactPostProcess,
            boolean                                     contentAttributeOnly,
            AttributeTypeToken                          contentAttributeType,
            DataRightContentBuilder                     dataRightContentBuilder,
            ArtifactAcceptor                            emptyFoldersArtifactAcceptor,
            ArtifactAcceptor                            excludedArtifactTypeArtifactAcceptor,
            FormatIndicator                             formatIndicator,
            ArtifactTypeToken                           headingArtifactTypeToken,
            AttributeTypeToken                          headingAttributeTypeToken,
            ArtifactAcceptor                            includeBookmarkArtifactAcceptor,
            IncludeHeadings                             includeHeadings,
            IncludeMainContentForHeadings               includeMainContentForHeadings,
            IncludeMetadataAttributes                   includeMetadataAttributes,
            Consumer<PublishingArtifact>                invalidAttributesHandler,
            MetadataOptions[]                           metadataOptionsArray,
            RelationTableOptions                        relationTableOptions,
            OrcsTokenService                            orcsTokenService,
            PresentationType                            presentationType,
            PublishingAppender                          publishingAppender,
            boolean                                     publishInline,
            OrderedAttributeTypeSupplier                orderedAttributeTypeSupplier,
            OutlineNumber                               outlineNumber,
            boolean                                     overrideOutlineNumber,
            BiConsumer<PublishingArtifact,CharSequence> updateParagraphNumber,
            ArtifactAcceptor                            wordRenderApplicabilityChecker
         ) {

      var outlineSectionResult =
         WordRenderUtil.processOutlining
            (
               formatIndicator,
               artifact,
               outlineNumber,
               headingArtifactTypeToken,
               headingAttributeTypeToken,
               Strings.EMPTY_STRING,
               invalidAttributesHandler,
               updateParagraphNumber,
               ArtifactAcceptor.and
                  (
                     emptyFoldersArtifactAcceptor,
                     wordRenderApplicabilityChecker,
                     excludedArtifactTypeArtifactAcceptor,
                     artifactAcceptor
                  ),
               contentAttributeOnly,
               allowedOutlineTypes,
               overrideOutlineNumber,
               includeBookmarkArtifactAcceptor.isOk(artifact)
                  ? IncludeBookmark.YES
                  : IncludeBookmark.NO
            );

      if (outlineSectionResult.isExcluded()) {
         return false;
      }

      /*
       * If a section heading was generated, it will contain the sub-section start tag.
       */

      outlineSectionResult
         .ifStarted
            (
               ( sectionHeading ) ->
               {
                  /*
                   * When the outline depth is above the maximum, serial sub-sections are generated instead of allowing further
                   * nesting of sub-sections.
                   */

                  if (outlineNumber.isAboveMaximumOutlingLevel()) {
                     publishingAppender.endSubSection();
                  }

                  publishingAppender.append( sectionHeading );
               }
            );


      final var footer =
         ( ( dataRightContentBuilder != null ) && formatIndicator.isWordMl() )
            ? dataRightContentBuilder.getContent( artifact, WordRenderUtil.getPageOrientation( artifact ) )
            : Strings.EMPTY_STRING;

      /*
       * Look for and process main content attribute first. It's presence may be used to determine if metadata attributes
       * should be included or not.
       */

      var mainContentOptional =
         !outlineSectionResult.isStarted() || includeMainContentForHeadings.isAlways()
            ? WordRenderUtil.processMainContentAttribute
               (
                  formatIndicator,
                  attributeOptionsList,
                  ( lPublishingAppender, lAttributeOptions, lAttributeType, lAllAttributes ) ->
                     attributeProcessor.process
                        (
                           artifact,
                           lPublishingAppender,
                           lAttributeOptions,
                           lAttributeType,
                           lAllAttributes,
                           presentationType,
                           publishInline,
                           footer,
                           !outlineSectionResult.isStarted() && includeBookmarkArtifactAcceptor.isOk(artifact)
                              ? IncludeBookmark.YES
                              : IncludeBookmark.NO
                        ),
                  ( attributeTypeToken ) ->
                         includeHeadings.isNever()
                     || !allowedOutlineTypes.isAllowed( artifact, headingArtifactTypeToken )
                     || !attributeTypeToken.equals( headingAttributeTypeToken ),
                  artifact,
                  contentAttributeType
               )
            : Optional.<CharSequence>empty();

      /*
       * Optionally process metadata attributes
       */

      boolean includeMetadata = false;

      switch( includeMetadataAttributes ) {

         case ALWAYS:
            includeMetadata = true;
            break;

         case NEVER:
            includeMetadata = false;
            break;

         case NOT_FOR_HEADINGS:
            includeMetadata = !outlineSectionResult.isStarted();
            break;

         case ONLY_WITH_MAIN_CONTENT:
            includeMetadata = mainContentOptional.isPresent();
            break;

         case ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD:
            includeMetadata = artifact.isOfType( CoreArtifactTypes.Requirement, CoreArtifactTypes.DesignMsWord );
            break;

         default:
            includeMetadata = false;
            break;
      }

      if( includeMetadata && !formatIndicator.isMarkdown()) {

         /*
          * Add metadata attributes to the Word ML output
          */

         WordRenderUtil.processMetadataOptions
            (
               formatIndicator,
               metadataOptionsArray,
               applicabilityTokens,
               artifact,
               publishingAppender
            );

      }

      /*
       * Process the selected artifacts in order. Main content is passed in so that it can be
       * inserted into the attributes at the selected position.
       */

      WordRenderUtil
         .processAttributes
            (
               formatIndicator,
               attributeOptionsList,
               ( lPublishingAppender, lAttributeOptions, lAttributeType, lAllAttributes ) ->
                  attributeProcessor.process
                     (
                        artifact,
                        lPublishingAppender,
                        lAttributeOptions,
                        lAttributeType,
                        lAllAttributes,
                        presentationType,
                        publishInline,
                        footer,
                        IncludeBookmark.NO
                     ),
               ( attributeName )      -> orcsTokenService.getAttributeType( attributeName ),
               orderedAttributeTypeSupplier,
               ( attributeTypeToken ) ->    includeHeadings.isNever()
                                         || !allowedOutlineTypes.isAllowed( artifact, headingArtifactTypeToken)
                                         || !attributeTypeToken.equals( headingAttributeTypeToken ),
               artifact,
               mainContentOptional,
               outlineSectionResult,
               includeMainContentForHeadings,
               contentAttributeType,
               allAttributes
            )
         .ifPresent( publishingAppender::append );

      if( includeMetadata && formatIndicator.isMarkdown()) {

         /*
          * Add metadata attributes to the Markdown output
          */

         WordRenderUtil.processMetadataOptions
            (
               formatIndicator,
               metadataOptionsArray,
               applicabilityTokens,
               artifact,
               publishingAppender
            );

      }

      /*
       * Add relation table(s) to the output
       */

      WordRenderUtil.processRelationTable
         (
            formatIndicator,
            relationTableOptions,
            artifact,
            publishingAppender,
            orcsTokenService
         );

      /*
       * When the first artifact in a section does not have word template content and a footer is present, the footer
       * will not have been appended to the output. Append the footer here.
       */

      if(    formatIndicator.isWordMl()
          && !artifact.hasAttributeContent( CoreAttributeTypes.WordTemplateContent )
          && Strings.isValidAndNonBlank( footer ) ) {

         publishingAppender.append(footer);
      }

      publishingAppender.endArtifact();

      if( artifactPostProcess != null ) {
         artifactPostProcess.accept(artifact);
      }

      return outlineSectionResult.isStarted();
   }

//@formatter:on

   /**
    * Processes a relation table by building and appending the table content based on the specified format. This method
    * determines the format of the relation table (HTML or Word) based on the provided {@code formatIndicator},
    * constructs the appropriate {@link RelationTableAppender}, and then uses the {@link RelationTableBuilder} to build
    * the table. The generated content is appended to the provided {@code publishingAppender}.
    *
    * @param formatIndicator the format indicator specifying whether to generate HTML or Word table content
    * @param relationTableOptions the options configuring the relation table, including artifact types, columns, and
    * relation type sides
    * @param artifact the publishing artifact associated with the relation table, used to retrieve related artifacts
    * @param publishingAppender the appender used to add the generated table content to the publishing output
    * @param orcsTokenService the service for retrieving relation types and attributes
    * @throws IllegalArgumentException if the {@code formatIndicator} specifies an unsupported format
    * @throws OseeCoreException if an error occurs during the processing of the relation table
    */
   public static void processRelationTable(FormatIndicator formatIndicator, RelationTableOptions relationTableOptions,
      PublishingArtifact artifact, PublishingAppender publishingAppender, OrcsTokenService orcsTokenService) {

      try {
         RelationTableAppender tableAppender;

         // Determine which appender to use based on the format indicator
         switch (formatIndicator) {
            case MARKDOWN:
               tableAppender = new HtmlRelationTableAppender();
               break;
            case WORD_ML:
               tableAppender = new WordRelationTableAppender();
               break;
            default:
               throw new IllegalArgumentException("Unsupported format: " + formatIndicator);
         }

         RelationTableBuilder builder =
            new RelationTableBuilder(relationTableOptions, artifact, orcsTokenService, tableAppender);
         builder.buildRelationTable();

         // Append the generated content to the publishing output
         publishingAppender.append(tableAppender.getTable());

      } catch (Exception e) {
         //@formatter:off
         throw new OseeCoreException(
            new Message()
              .title("WordRenderUtil::processRelationTable, failed to process relation table.")
              .indentInc()
              .segment("Publishing Artifact Name", artifact.getName())
              .segment("Publishing Artifact ID", artifact.getArtifactId())
              .segment("Publishing Relation Table Artifact Types", relationTableOptions.getRelationTableArtifactTypeNamesAndOrIds())
              .segment("Publishing Relation Table Relation Type Sides", relationTableOptions.getRelationTableRelationTypeSides())
              .segment("Publishing Relation Table Columns", relationTableOptions.getRelationTableColumns())
              .reasonFollows(e)
              .toString(),
           e
         );
         //@formatter:on
      }
   }

   /**
    * The default attribute render for non-WordML attributes. Attributes are formatted with the "label" and "format"
    * sub-templates specified for the attribute in the Publishing Template Renderer Options. When a sub-template is not
    * provided the attribute name or value is rendered as plain text in the Word ML. The
    * {@link CoreAttributeTypes#RelationOrder} attribute requires special processing and the
    * {@link RelationOrderFuntion} is used as a callback to render that attribute.
    *
    * @param attributeType the attribute to be rendered.
    * @param relationOrderFunction a {@link RelationOrderFunction} used to render the
    * {@link CoreAttributeTypes#RelationOrder} attribute.
    * @param artifactReadable the artifact's whose attribute is to be rendered.
    * @param publishingAppender the generated Word ML is written to the {@link PublishingAppender}.
    * @param label the label template from the Publishing Template Renderer Options for the attribute.
    * @param format the format template from the Publishing Template Renderer Options for the attribute.
    */

   public static void renderAttribute(FormatIndicator formatIndicator, AttributeTypeToken attributeType,
      RelationOrderFunction relationOrderFunction, ArtifactReadable artifactReadable,
      PublishingAppender publishingAppender, String label, String format) {

      var name = attributeType.getUnqualifiedName();

      if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {

         if (Objects.isNull(relationOrderFunction)) {

            /*
             * Just skip it, when a RelationOrderSupplier was not provided.
             */

            return;
         }

         /*
          * Render Relation Order
          */

         publishingAppender.startParagraph();
         publishingAppender.addRunWithTextEscape(name);
         publishingAppender.endParagraph();

         /*
          * Data will contain a sub-section with a table.
          */

         String data = relationOrderFunction.apply(artifactReadable);

         publishingAppender.append(data);

      } else {

         /*
          * Render Label: Value
          */

         var value = artifactReadable.getAttributeValuesAsString(attributeType);

         publishingAppender.startParagraph();
         publishingAppender.append(
            WordCoreUtil.replaceRendererOptionToken(formatIndicator, label, format, name, value));
         publishingAppender.endParagraph();
      }
   }

   //@formatter:off
   public static String
      renderWordAttribute
         (
            PublishingArtifact                 artifact,
            ArtifactId                         viewId,
            PublishingAppender                 publishingAppender,
            RendererMap                        rendererMap,
            PresentationType                   presentationType,
            String                             label,
            String                             footer,
            String                             desktopClientLoopbackUrl,
            IncludeBookmark                    includeBookmark,
            TransactionToken                   historicalArtifactTransactionToken,
            Set<String>                        unknownGuids,
            RenderWordTemplateContentOperation renderWordTemplateContentOperation,
            ExceptionHandler                   exceptionHandler
         )
   {
      if ( Objects.nonNull(publishingAppender ) && Strings.isValidAndNonBlank(label) ) {
         publishingAppender.addParagraph(label);
      }

      final var artifactId = ArtifactId.create( artifact );

      final var branchId = BranchId.valueOf( artifact.getBranch().getId() );
      //@formatter:off
      final var safeIncludeBookmark =
         ( includeBookmark != null ) && includeBookmark.isYes() && !artifact.isBookmarked()
            ? IncludeBookmark.YES
            : IncludeBookmark.NO;
      //@formatter:on

      WordTemplateContentData wtcData = new WordTemplateContentData();
      wtcData.setArtId(artifactId);
      wtcData.setBranch(branchId);
      wtcData.setViewId(viewId);
      wtcData.setFooter(footer);
      wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
      wtcData.setLinkType(rendererMap.getRendererOptionValue(RendererOption.LINK_TYPE));
      wtcData.setPresentationType(presentationType);
      wtcData.setTxId(historicalArtifactTransactionToken);
      wtcData.setDesktopClientLoopbackUrl(desktopClientLoopbackUrl);
      wtcData.setIncludeBookmark(safeIncludeBookmark);

      String wordMlContentDataAndFooter = "";

      try {

         var content = renderWordTemplateContentOperation.apply(wtcData);
         if (Objects.nonNull(content)) {
            wordMlContentDataAndFooter = content.getFirst();
            var contentUnknownGuids = content.getSecond();
            if (!contentUnknownGuids.isEmpty()) {
               unknownGuids.addAll(content.getSecond());
            }
         }
      } catch (Exception e) {

         if (Objects.nonNull(publishingAppender)) {

            publishingAppender.addParagraphNoEscape("Failed to parse content for artifact.");
            publishingAppender.startParagraph();
            publishingAppender.addRunWithTextEscape("Artifact: ", artifact.toStringWithId());
            publishingAppender.endParagraph();
            publishingAppender.startParagraph();
            publishingAppender.addRunWithTextEscape("Branch: ", branchId.toString());
            publishingAppender.endParagraph();
         }

         var renderException = new OseeCoreException(
            new Message().title("Failed to parse content for artifact.").indentInc().segment("Artifact",
               artifact.toStringWithId()).segment("Branch", branchId).reasonFollowsWithTrace(e).toString(),
            e);

         if (Objects.nonNull(exceptionHandler)) {
            exceptionHandler.accept(renderException);
         }
      }

      if (Objects.nonNull(publishingAppender)) {

         if (PresentationType.SPECIALIZED_EDIT.equals(presentationType)) {

            publishingAppender.addEditParagraphNoEscape(WordCoreUtil.getStartEditImage(artifact.getGuid()));

            if (Objects.nonNull(wordMlContentDataAndFooter)) {
               publishingAppender.append(wordMlContentDataAndFooter);
            }

            publishingAppender.addEditParagraphNoEscape(WordCoreUtil.getEndEditImage(artifact.getGuid()));

         } else {

            if (Objects.nonNull(wordMlContentDataAndFooter)) {

               publishingAppender.append(wordMlContentDataAndFooter);

               if (WordCoreUtil.containsLists(wordMlContentDataAndFooter)) {

                  publishingAppender.resetListValue();

               }

            } else {

               if (Objects.nonNull(footer)) {

                  publishingAppender.append(footer);

               }

            }

         }

      }

      return wordMlContentDataAndFooter;
   }
   //@formatter:on

   /**
    * Prepares a {@link PublishingTemplate} and a {@link PublishingAppender}.
    * <dl>
    * <dt>Publishing Template Setup</dt>
    * <dd>
    * <ul>
    * <li>If the publishing template format is Word ML, the page number tags for page 1 are removed.</li>
    * <li>If the publishing template format is Word ML, styles are updated to the initial outlining numbers for the
    * publish.</li></dd>
    * <dt>Publishing Appender Setup</dt>
    * <dd>
    * <li>Sets the initial list sequence numbers for the starting number of the publish.</li>
    * <li>Sets the maximum outlining level.</li>
    * </ul>
    * </dl>
    *
    * @param formatIndicator the format for the publish.
    * @param publishingTemplate the {@link PublishingTemplate} to prepare.
    * @param publishingAppender the {@link PublishingAppender} for the publish is initialized with the starting
    * paragraph number.
    * @param outlineNumber when not <code>null</code> or blank, is used as the starting paragraph number.
    */

   public static void setupPublishingTemplate(FormatIndicator formatIndicator, PublishingTemplate publishingTemplate,
      PublishingAppender publishingAppender, String outlineNumber, String outlineType) {

      if (formatIndicator.isWordMl()) {

         publishingTemplate.update(WordCoreUtil::cleanupPageNumberTypeStart1);

         //@formatter:off
          publishingTemplate.update
             (
               ( templateContent) -> WordCoreUtil.initializePublishingTemplateOutliningNumbers
                                        (
                                           outlineNumber,
                                           templateContent,
                                           outlineType
                                        )
             );
          //@formatter:on
      }

   }

}

/* EOF */
