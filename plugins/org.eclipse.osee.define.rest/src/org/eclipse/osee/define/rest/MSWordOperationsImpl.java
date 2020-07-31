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

package org.eclipse.osee.define.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUtilities;
import org.eclipse.osee.define.rest.publishing.SpecifiedTemplatePublisherStreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ryan D. Brooks
 */
public class MSWordOperationsImpl implements MSWordOperations {

   private final OrcsApi orcsApi;
   private final Log logger;
   private final EventAdmin eventAdmin;

   public MSWordOperationsImpl(OrcsApi orcsApi, Log logger, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);

      return wordRendererHandler.renderWordML(data);
   }

   @Override
   public String renderPlainText(BranchId branchId, String data) {
      String PL_STYLE_WITH_RETURN =
         "<w:rPr><w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>)).*?</w:rPr>";
      String PL_STYLE = "<w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>))";
      String PL_HIGHLIGHT =
         "<w:highlight w:val=\"light-gray\"></w:highlight><w:shd w:color=\"auto\" w:fill=\"BFBFBF\" w:val=\"clear\"></w:shd>";
      String EMPTY_PARAGRAPHS = "<w:r wsp:rsidRPr=\"\\d+\"><w:t></w:t></w:r>";
      Pattern REVIEW_COMMENT = Pattern.compile(
         "<aml:annotation[^>]*w:type=\"Word.Comment.Start\"/?>(</aml:annotation>)?[\\s\\S]+?<aml:annotation[^>]*w:type=\"Word.Comment.End\"/?>(</aml:annotation>)?[\\s\\S]+?</aml:annotation></w:r>");

      data = WordUtilities.reassignBinDataID(data);
      data = WordMlLinkHandler.renderPlainTextWithoutLinks(orcsApi.getQueryFactory(), branchId, data);
      data = WordUtilities.reassignBookMarkID(data);
      data = WordUtilities.removeNewLines(data);

      // if no extra paragraphs have been added this will replace the normal footer
      data = data.replaceAll(ReportConstants.ENTIRE_FTR_EXTRA_PARA, "");
      data = data.replaceAll(ReportConstants.ENTIRE_FTR, "");
      data = data.replaceAll(ReportConstants.EMPTY_SECTION_BREAK, ReportConstants.PAGE_BREAK);
      data = data.replaceAll(ReportConstants.NO_DATA_RIGHTS, "");

      if (!data.contains("<w:tbl>")) {
         int lastIndex = data.lastIndexOf("<w:p wsp:rsidR=");

         if (lastIndex != -1) {
            // temp should equal <w:p wsp:rsidR ..</w:p> ...
            String temp = data.substring(lastIndex);
            temp = temp.replaceAll("<w:p\\s[^>]*>(<w:pPr><w:spacing[^>]*></w:spacing></w:pPr>)?</w:p>", "");
            data = data.substring(0, lastIndex) + temp;
         }
      }

      data = data.replaceAll(PL_STYLE_WITH_RETURN, "");
      data = data.replaceAll(PL_STYLE, "");
      data = data.replaceAll(PL_HIGHLIGHT, "");
      data = data.replaceAll(EMPTY_PARAGRAPHS, "");
      Matcher commentMatch = REVIEW_COMMENT.matcher(data);
      if (commentMatch.find()) {
         data = data.replaceAll(ReportConstants.WORD_COMMENT_START, "");
         data = data.replaceAll(ReportConstants.WORD_COMMENT_END, "");
         data = data.replaceAll(ReportConstants.WORD_COMMENT, "");
      }
      return data;
   }

   @Override
   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data) {

      WordMLApplicabilityHandler wordHandler = new WordMLApplicabilityHandler(orcsApi, logger, branchId, viewId);
      return wordHandler.previewValidApplicabilityContent(data);
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);
      return updateArt.updateArtifacts(data);
   }

   @Override
   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact) {
      //Generate filename with the headArtifact name and current time
      String name = orcsApi.getQueryFactory().fromBranch(branch).andId(headArtifact).asArtifact().getName();
      SimpleDateFormat format = new SimpleDateFormat("MM-dd_HH-mm-ss");
      Date date = new Date(System.currentTimeMillis());
      String time = format.format(date);
      String fileName = name + "_" + time + ".xml";

      PublishingOptions publishingOptions = new PublishingOptions();
      publishingOptions.branch = branch;
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.excludeFolders = true;

      StreamingOutput streamingOutput =
         new SpecifiedTemplatePublisherStreamingOutput(publishingOptions, template, headArtifact, orcsApi, logger);

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }
}
