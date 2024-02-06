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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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
    * A functional interface for a method to be called once it has been determined that an attribute will be rendered.
    */

   @FunctionalInterface
   public interface AttributeProcessor {

      /**
       * The {@link #process} method of the {@link AttributeProcessor} implementation that was passed to the
       * {@link WordRenderUtil#processAttributes} method is called when it is determined that an attribute will be
       * rendered.
       *
       * @param attributeOptions the {@link AttributeOptions} array element from the {@link PublishOptions} that
       * applies to the attribute to be rendered.
       * @param attributeType the type of attribute to be rendered.
       * @param allAttributes the render all attributes flag.
       */

      void process(AttributeOptions attributeOptions, AttributeTypeToken attributeType, boolean allAttributes);
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

   @FunctionalInterface
   public interface ExceptionHandler extends Consumer<Throwable> {
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
    * @param artifact the first artifact selected for the publish.
    * @param publishingTemplate the {@link PublishingTemplate} for the publish.
    * @return the starting paragraph number.
    */

   public static String getStartingParagraphNumber(ArtifactReadable artifact, PublishingTemplate publishingTemplate) {

      var startParagraphNumber = "1";

      //@formatter:off
      if(    Objects.isNull(publishingTemplate)
          || Objects.isNull(artifact)
          || publishingTemplate.test(WordCoreUtil::isNotArtifactPublishingTemplateInsertToken)
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

   /**
    * Loops through each attribute element that is to be printed, if * (all attributes), it loops through every valid
    * attribute on that artifact. Also makes sure not to print the headingAttributeType if outlining is enabled.
    * Otherwise it only runs for the specific attribute element. In this default implementation the presentation type is
    * preview.
    *
    * @param formatIndicator the output format of the publish. This parameter is used to determine the attribute type to
    * process when the "AttrType" attribute option is set to "&lt;format-content-attribute&gt;".
    * @param attributeOptionsArray a list of the {@link AttributeOptions} from the {@link PublishOptions} for the
    * publish.
    * @param attributeProcessor a callback method to render and attribute.
    * @param attributeTypeFunction a callback method to look up an {@link AttributeTypeToken} by an attribute name.
    * @param orderedAttributeTypeSupplier a supplier method to get a list of the attributes in rendering order for the
    * artifact.
    * @param artifact the artifact whose attributes are to be rendered.
    * @param headingAttributeType the {@link AttributeTypeToken} used to identify artifacts that are headings.
    * @param renderAllAttributes the publishing render all attributes flag.
    * @param outlining the outlining mode flag.
    */

   //@formatter:off
   public static void
      processAttributes
         (
            FormatIndicator formatIndicator,
            List<AttributeOptions> attributeOptionsArray,
            AttributeProcessor attributeProcessor,
            AttributeTypeFunction attributeTypeFunction,
            OrderedAttributeTypeSupplier orderedAttributeTypeSupplier,
            ArtifactReadable artifact,
            AttributeTypeToken headingAttributeType,
            boolean renderAllAttributes,
            boolean outlining
         ) {

      for (var attributeOptions : attributeOptionsArray) {

         var attributeName = attributeOptions.getAttributeName();

         if (renderAllAttributes || "*".equals(attributeName)) {

            /*
             * RendererOption is set to process all attributes or all attributes were specified in the publishing
             * template renderer options.
             */

            for (var attributeType : orderedAttributeTypeSupplier.get() ) {

               /*
                * When outlining and the attribute type is for a heading, skip it. The heading has already been
                * processed.
                */

               if (outlining && attributeType.equals( headingAttributeType )) {
                  continue;
               }

               //@formatter:off
               attributeProcessor.process
                  (
                     attributeOptions,
                     attributeType,
                     true
                  );
               //@formatter:on
            }

         } else {

            /**
             * Not processing all attributes and a specific attribute was specified in the publishing template renderer
             * options.
             * <p>
             * Since the publishing template explicitly specified the attribute it will be processed without regard to
             * the outlining setting or whether the attribute type is for a heading.
             */

            //@formatter:off
            var attributeType =
               "<format-content-attribute>".equals( attributeName )
                  ? formatIndicator.getMainContentAttributeTypeToken()
                  : attributeTypeFunction.apply(attributeName);
            //@formatter:on

            if (artifact.isAttributeTypeValid(attributeType)) {

               //@formatter:off
               attributeProcessor.process
                  (
                     attributeOptions,
                     attributeType,
                     false
                  );
               //@formatter:on
            }

         }

      }
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

   public static void processMetadataOptions(FormatIndicator formatIndicator, MetadataOptions[] metadataOptionsArray, Map<ApplicabilityId, ApplicabilityToken> applicabilityTokens, ArtifactReadable artifact, PublishingAppender publishingAppender) {

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

   public static void renderAttribute(FormatIndicator formatIndicator, AttributeTypeToken attributeType, RelationOrderFunction relationOrderFunction, ArtifactReadable artifactReadable, PublishingAppender publishingAppender, String label, String format) {

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
            ArtifactReadable                   artifact,
            ArtifactId                         viewId,
            PublishingAppender                 wordMl,
            RendererMap                        rendererMap,
            PresentationType                   presentationType,
            String                             label,
            String                             footer,
            String                             permanentLinkUrl,
            boolean                            artifactIsChanged,
            TransactionToken                   historicalArtifactTransactionToken,
            Set<String>                        unknownGuids,
            RenderWordTemplateContentOperation renderWordTemplateContentOperation,
            ExceptionHandler                   exceptionHandler
         )
   {
      if ( Objects.nonNull(wordMl ) && Strings.isValidAndNonBlank(label) ) {
         wordMl.addParagraph(label);
      }

      var artifactId = ArtifactId.create( artifact );
      var branchId = BranchId.valueOf( artifact.getBranch().getId() );

      WordTemplateContentData wtcData = new WordTemplateContentData();
      wtcData.setArtId( artifactId );
      wtcData.setBranch( branchId );
      wtcData.setViewId( viewId );
      wtcData.setFooter(presentationType != PresentationType.SPECIALIZED_EDIT ? footer : "");
      wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
      wtcData.setLinkType( rendererMap.getRendererOptionValue( RendererOption.LINK_TYPE ) );
      wtcData.setPresentationType(presentationType);
      wtcData.setTxId( historicalArtifactTransactionToken );
      wtcData.setPermanentLinkUrl(permanentLinkUrl);
      wtcData.setArtIsChanged( artifactIsChanged );

      String wordMlContentDataAndFooter = "";

      try {



         var content = renderWordTemplateContentOperation.apply( wtcData );
         if( Objects.nonNull( content ) ) {
            wordMlContentDataAndFooter = content.getFirst();
            var contentUnknownGuids = content.getSecond();
            if( !contentUnknownGuids.isEmpty() ) {
               unknownGuids.addAll( content.getSecond() );
            }
         }
      } catch( Exception e ) {

         if( Objects.nonNull( wordMl ) ) {

            wordMl.addParagraphNoEscape( "Failed to parse content for artifact." );
            wordMl.startParagraph();
            wordMl.addRunWithTextEscape( "Artifact: ", artifact.toStringWithId() );
            wordMl.endParagraph();
            wordMl.startParagraph();
            wordMl.addRunWithTextEscape( "Branch: ", branchId.toString() );
            wordMl.endParagraph();
         }

         var renderException =
            new OseeCoreException
                   (
                     new Message()
                            .title( "Failed to parse content for artifact." )
                            .indentInc()
                            .segment( "Artifact", artifact.toStringWithId() )
                            .segment( "Branch",   branchId  )
                            .reasonFollowsWithTrace( e )
                            .toString(),
                     e
                   );

         if( Objects.nonNull( exceptionHandler ) ) {
            exceptionHandler.accept( renderException );
         }
      }

      if( Objects.nonNull( wordMl ) ) {

         if( PresentationType.SPECIALIZED_EDIT.equals( presentationType ) ) {

            wordMl.addEditParagraphNoEscape(WordCoreUtil.getStartEditImage(artifact.getGuid()));

            if( Objects.nonNull( wordMlContentDataAndFooter ) ) {
               wordMl.append( wordMlContentDataAndFooter );
            }

            wordMl.addEditParagraphNoEscape(WordCoreUtil.getEndEditImage(artifact.getGuid()));

         } else {

            if( Objects.nonNull( wordMlContentDataAndFooter ) ) {

               wordMl.append( wordMlContentDataAndFooter );

               if( WordCoreUtil.containsLists( wordMlContentDataAndFooter) ) {

                  wordMl.resetListValue();

               }

            } else {

               if( Objects.nonNull( footer ) ) {

                  wordMl.append( footer );

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
    * @param artifact the artifact to be published.
    * @param publishingAppender the {@link PublishingAppender} for the publish is initialized with the starting
    * paragraph number.
    * @param outlineNumber when not <code>null</code> or blank, is used as the starting paragraph number.
    * @param maxOutline the maximum number of outlining levels allowed for the publish.
    */

   public static void setupPublishingTemplate(FormatIndicator formatIndicator, PublishingTemplate publishingTemplate, ArtifactReadable artifact, PublishingAppender publishingAppender, String outlineNumber, String outlineType, int maxOutline) {

      if (formatIndicator.isWordMl()) {
         publishingTemplate.update(WordCoreUtil::cleanupPageNumberTypeStart1);
      }

      //@formatter:off
      final var finalOutlineNumber =
         Strings.isInvalidOrBlank( outlineNumber )
            ? WordRenderUtil.getStartingParagraphNumber( artifact, publishingTemplate )
            : outlineNumber;
       //@formatter:on

      if (formatIndicator.isWordMl()) {
         //@formatter:off
          publishingTemplate.update
             (
               ( templateContent) -> WordCoreUtil.initializePublishingTemplateOutliningNumbers
                                        (
                                           finalOutlineNumber,
                                           templateContent,
                                           outlineType
                                        )
             );
          //@formatter:on
      }

      publishingAppender.setNextParagraphNumberTo(finalOutlineNumber);

      publishingAppender.setMaxOutlineLevel(maxOutline);
   }

}

/* EOF */
