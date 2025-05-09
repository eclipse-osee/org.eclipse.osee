/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Morgan E. Cook
 */
public class WordTemplateContentRendererHandler {

   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   public static final String PL_STYLE_WITH_RETURN =
      "<w:rPr><w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>)).*?</w:rPr>";
   public static final String PL_STYLE = "<w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>))";
   public static final String PL_HIGHLIGHT =
      "<w:highlight w:val=\"light-gray\"></w:highlight><w:shd w:color=\"auto\" w:fill=\"BFBFBF\" w:val=\"clear\"></w:shd>";
   public static final String EMPTY_PARAGRAPHS = "<w:r wsp:rsidRPr=\"\\d+\"><w:t></w:t></w:r>";
   public static final String EXTRA_SPACES = "<w:r><w:t> </w:t></w:r>";

   private final OrcsApi orcsApi;
   private final Log logger;

   private WordMLApplicabilityHandler applicHandler;
   private final QueryFactory queryFactory;
   private final DataAccessOperations dataAccessOperations;

   public WordTemplateContentRendererHandler(OrcsApi orcsApi, DataAccessOperations dataAccessOperations, Log logger) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
      this.dataAccessOperations = dataAccessOperations;
   }

   public Pair<String, Set<String>> renderWordMLForArtifact(ArtifactReadable artifact,
      WordTemplateContentData wtcData) {

      //@formatter:off
      CharSequence data =
         artifact.isDeleted()
            ? artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, DeletionFlag.INCLUDE_DELETED, null )
            : artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, DeletionFlag.EXCLUDE_DELETED, null );
      //@formatter:on

      if (Objects.isNull(data) && wtcData.getIsEdit()) {
         data = CoreArtifactTypes.MsWordTemplate.getAttributeDefault(CoreAttributeTypes.WordTemplateContent);
      }

      Set<String> unknownGuids = new HashSet<>();

      if (Objects.isNull(data)) {

         //The artifact does not have any WordTemplateContent data.

         if (artifact.isOfType(CoreArtifactTypes.HeadingMsWord)) {

            /*
             * The artifact is an empty HeadingMSWord (header). Tag the artifact with a book mark so it can be linked
             * to.
             */

            data = "";
            //@formatter:off
            data =
               WordMlLinkHandler.link
                  (
                     this.queryFactory,
                     wtcData.getLinkType(),
                     artifact,
                     data.toString(),
                     wtcData.getTxId(),
                     unknownGuids,
                     wtcData.getPresentationType(),
                     wtcData.getDesktopClientLoopbackUrl(),
                     wtcData.getIncludeBookmark()
                  );
            //@formatter:on

            return new Pair<>(data.toString(), unknownGuids);
         }

         return null;
      }

      //The artifact has WordTemplateContent data

      //Change the BinData Id so images do not get overridden by the other images
      data = WordCoreUtil.replaceBinaryDataIdentifiers(data);

      if (wtcData.getArtIsChanged()) {
         data = WordCoreUtil.appendInlineChangeTag(data);
      }

      try {
         //@formatter:off
         data =
            WordMlLinkHandler.link
               (
                  this.queryFactory,
                  wtcData.getLinkType(),
                  artifact,
                  data.toString(),
                  wtcData.getTxId(),
                  unknownGuids,
                  wtcData.getPresentationType(),
                  wtcData.getDesktopClientLoopbackUrl(),
                  wtcData.getIncludeBookmark()
               );
         //@formatter:on
      } catch (Exception e) {
         //If link processing fails, continue on
         //ToDo: add message to the publishing log
      }

      // if no extra paragraphs have been added this will replace the normal footer
      data = WordCoreUtil.removeFootersAndNoDataRightsStatements(data);
      data = WordCoreUtil.removeProofErrors(data);
      data = WordCoreUtil.replaceEmptySectionBreaksWithPageBreaks(data).toString();

      var dataString = data.toString();

      if (wtcData.getIsEdit() && !dataString.contains("<w:tbl>")) {
         int lastIndex = dataString.lastIndexOf("<w:p wsp:rsidR=");

         if (lastIndex != -1) {
            // temp should equal <w:p wsp:rsidR ..</w:p> ...
            String temp = dataString.substring(lastIndex);
            temp = temp.replaceAll("<w:p\\s[^>]*>(<w:pPr><w:spacing[^>]*></w:spacing></w:pPr>)?</w:p>", "");
            dataString = dataString.substring(0, lastIndex) + temp;
         }
      }

      //@formatter:off
      if(    !wtcData.getIsEdit()
          && (    wtcData.getBranch().getViewId().notEqual( ArtifactId.SENTINEL )
               || wtcData.isViewIdValid() ) )
      //@formatter:on
      {

         if (applicHandler == null) {
            this.applicHandler =
               new WordMLApplicabilityHandler(this.orcsApi, this.logger, wtcData.getBranch(), wtcData.getViewId());
         }

         dataString = dataString.replaceAll(PL_STYLE_WITH_RETURN, "");
         dataString = dataString.replaceAll(PL_STYLE, "");
         dataString = dataString.replaceAll(PL_HIGHLIGHT, "");
         dataString = applicHandler.previewValidApplicabilityContent(dataString);
         dataString = dataString.replaceAll(EMPTY_PARAGRAPHS, "");
      }

      if (!wtcData.getIsEdit()) {
         dataString = dataString.concat(wtcData.getFooter()); // editable content should not have footer appended to the end
         dataString = dataString.replaceAll(PGNUMTYPE_START_1, "");
      }

      return new Pair<>(dataString, unknownGuids);

   }

   public Pair<String, Set<String>> renderWordML(WordTemplateContentData wtcData) {
      //@formatter:off
      return
         this.dataAccessOperations.getArtifactReadables
            (
               new BranchSpecification( wtcData.getBranch(), wtcData.getViewId() ),
               List.of( ArtifactId.valueOf( wtcData.getArtId() ) ),
               List.of(),
               Strings.EMPTY_STRING,
               ArtifactTypeToken.SENTINEL,
               wtcData.isTxIdValid() ? wtcData.getTxId() : TransactionId.SENTINEL,
               IncludeDeleted.YES
            )
         .filterValue( Predicate.not( List::isEmpty ) )
         .mapValue
            (
               //When optional value is present
               ( artifactReadables ) -> this.renderWordMLForArtifact( artifactReadables.get(0), wtcData )
            )
         .orElseGet
            (
               //When optional value is not present
               (Pair<String,Set<String>>) null
            );
      //@formatter:on
   }

}
