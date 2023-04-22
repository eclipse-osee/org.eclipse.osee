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

package org.eclipse.osee.define.rest.internal.wordupdate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.operations.publishing.PublishingUtils;
import org.eclipse.osee.define.operations.publishing.WordCoreUtilServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
   private final PublishingUtils publishingUtils;

   public WordTemplateContentRendererHandler(OrcsApi orcsApi, Log logger) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
      this.publishingUtils = new PublishingUtils(orcsApi);
   }

   public Pair<String, Set<String>> renderWordMLForArtifact(ArtifactReadable artifact, WordTemplateContentData wtcData) {

      String data =
         artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, DeletionFlag.EXCLUDE_DELETED, null);

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
            data = WordMlLinkHandler.link(this.queryFactory, wtcData.getLinkType(), artifact, data, wtcData.getTxId(),
               unknownGuids, wtcData.getPresentationType(), wtcData.getPermanentLinkUrl());
            data = WordCoreUtilServer.reassignBookMarkID(data).toString();
            data = WordCoreUtilServer.removeNewLines(data);

            return new Pair<>(data, unknownGuids);
         }

         return null;
      }

      //The artifact has WordTemplateContent data

      //Change the BinData Id so images do not get overridden by the other images
      data = WordCoreUtilServer.reassignBinDataID(data);

      if (wtcData.getArtIsChanged()) {
         data = WordCoreUtilServer.appendInlineChangeTag(data);
      }
      data = WordCoreUtilServer.removeNewLines(data);

      data = WordMlLinkHandler.link(this.queryFactory, wtcData.getLinkType(), artifact, data, wtcData.getTxId(),
         unknownGuids, wtcData.getPresentationType(), wtcData.getPermanentLinkUrl());
      data = WordCoreUtilServer.reassignBookMarkID(data).toString();

      // if no extra paragraphs have been added this will replace the normal footer
      var charSequenceData = WordCoreUtil.removeFootersAndNoDataRightsStatements(data);
      data = WordCoreUtil.replaceEmptySectionBreaksWithPageBreaks(charSequenceData).toString();

      if (wtcData.getIsEdit() && !data.contains("<w:tbl>")) {
         int lastIndex = data.lastIndexOf("<w:p wsp:rsidR=");

         if (lastIndex != -1) {
            // temp should equal <w:p wsp:rsidR ..</w:p> ...
            String temp = data.substring(lastIndex);
            temp = temp.replaceAll("<w:p\\s[^>]*>(<w:pPr><w:spacing[^>]*></w:spacing></w:pPr>)?</w:p>", "");
            data = data.substring(0, lastIndex) + temp;
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

         data = data.replaceAll(PL_STYLE_WITH_RETURN, "");
         data = data.replaceAll(PL_STYLE, "");
         data = data.replaceAll(PL_HIGHLIGHT, "");
         data = applicHandler.previewValidApplicabilityContent(data);
         data = data.replaceAll(EMPTY_PARAGRAPHS, "");
      }

      data = data.concat(wtcData.getFooter());

      if (!wtcData.getIsEdit()) {
         data = data.replaceAll(PGNUMTYPE_START_1, "");
      }

      return new Pair<>(data, unknownGuids);

   }

   public Pair<String, Set<String>> renderWordML(WordTemplateContentData wtcData) {
      //@formatter:off
      return
         ( wtcData.isTxIdValid()
              ? this.publishingUtils.getArtifactReadablePossiblyDeletedByIdentifierAndTransactionIdWithDeleteAttributes
                   (
                     wtcData.getBranch(),                      /* branchId      */
                     ArtifactId.valueOf( wtcData.getArtId() ), /* artifactId    */
                     wtcData.getTxId()                         /* transactionId */
                   )
              : this.publishingUtils.getArtifactReadablePossiblyDeletedByIdentifierWithDeletedAttributes
                   (
                     wtcData.getBranch(),                      /* branchId      */
                     ArtifactId.valueOf( wtcData.getArtId() )  /* artifactId    */
                   )
         )
         .map
            (
               //When optional value is present
               ( artifactReadable ) -> this.renderWordMLForArtifact( artifactReadable, wtcData )
            )
         .orElse
            (
               //When optional value is not present
               null
            );
      //@formatter:on
   }

}
