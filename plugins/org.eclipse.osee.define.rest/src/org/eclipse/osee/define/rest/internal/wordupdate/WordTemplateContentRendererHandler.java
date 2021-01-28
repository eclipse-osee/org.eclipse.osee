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
import java.util.Set;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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

   public WordTemplateContentRendererHandler(OrcsApi orcsApi, Log logger) {
      this.orcsApi = orcsApi;
      queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
   }

   public Pair<String, Set<String>> renderWordML(WordTemplateContentData wtcData) {
      TransactionId txId = wtcData.getTxId();
      if (txId == null || txId.isInvalid()) {
         txId = TransactionId.SENTINEL;
      }
      ArtifactReadable artifact = null;
      if (txId.equals(TransactionId.SENTINEL)) {
         artifact = queryFactory.fromBranch(wtcData.getBranch()).andId(ArtifactId.valueOf(
            wtcData.getArtId())).includeDeletedArtifacts().includeDeletedAttributes().getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
      } else {
         artifact = queryFactory.fromBranch(wtcData.getBranch()).fromTransaction(txId).andId(ArtifactId.valueOf(
            wtcData.getArtId())).includeDeletedArtifacts().includeDeletedAttributes().getResults().getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
      }

      if (artifact.isValid()) {
         Set<String> unknownGuids = new HashSet<>();

         String data =
            artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, DeletionFlag.EXCLUDE_DELETED, null);

         if (data == null && wtcData.getIsEdit()) {
            data = CoreArtifactTypes.MsWordTemplate.getAttributeDefault(CoreAttributeTypes.WordTemplateContent);
         }

         if (data != null) {
            //Change the BinData Id so images do not get overridden by the other images
            data = WordUtilities.reassignBinDataID(data);

            if (wtcData.getArtIsChanged()) {
               data = WordUtilities.appendInlineChangeTag(data);
            }
            data = WordUtilities.removeNewLines(data);

            LinkType link = wtcData.getLinkType() != null ? LinkType.valueOf(wtcData.getLinkType()) : null;
            data = WordMlLinkHandler.link(queryFactory, link, artifact, data, wtcData.getTxId(), unknownGuids,
               wtcData.getPresentationType(), wtcData.getPermanentLinkUrl());
            data = WordUtilities.reassignBookMarkID(data);

            // if no extra paragraphs have been added this will replace the normal footer
            data = data.replaceAll(ReportConstants.ENTIRE_FTR_EXTRA_PARA, "");
            data = data.replaceAll(ReportConstants.ENTIRE_FTR, "");
            data = data.replaceAll(ReportConstants.EMPTY_SECTION_BREAK, ReportConstants.PAGE_BREAK);
            data = data.replaceAll(ReportConstants.NO_DATA_RIGHTS, "");

            if (wtcData.getIsEdit() && !data.contains("<w:tbl>")) {
               int lastIndex = data.lastIndexOf("<w:p wsp:rsidR=");

               if (lastIndex != -1) {
                  // temp should equal <w:p wsp:rsidR ..</w:p> ...
                  String temp = data.substring(lastIndex);
                  temp = temp.replaceAll("<w:p\\s[^>]*>(<w:pPr><w:spacing[^>]*></w:spacing></w:pPr>)?</w:p>", "");
                  data = data.substring(0, lastIndex) + temp;
               }
            }

            if (!wtcData.getIsEdit() && (wtcData.getBranch().getViewId().notEqual(
               ArtifactId.SENTINEL) || isWtcViewIdValid(wtcData))) {

               if (applicHandler == null) {
                  this.applicHandler =
                     new WordMLApplicabilityHandler(orcsApi, logger, wtcData.getBranch(), wtcData.getViewId());
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
         } else if (artifact.isOfType(CoreArtifactTypes.HeadingMsWord)) {
            //If the artifact is an empty ms word header, still want to tag that header with a book mark so it can be linked to.
            //Non empty ms word headers are caught above correctly
            data = "";
            LinkType link = wtcData.getLinkType() != null ? LinkType.valueOf(wtcData.getLinkType()) : null;
            data = WordMlLinkHandler.link(queryFactory, link, artifact, data, wtcData.getTxId(), unknownGuids,
               wtcData.getPresentationType(), wtcData.getPermanentLinkUrl());
            data = WordUtilities.reassignBookMarkID(data);
            data = WordUtilities.removeNewLines(data);

            return new Pair<>(data, unknownGuids);
         }
      }

      return null;
   }

   private boolean isWtcViewIdValid(WordTemplateContentData wtcData) {
      return wtcData.getViewId() != null && wtcData.getViewId().notEqual(ArtifactId.SENTINEL);
   }
}
