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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
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
import org.eclipse.osee.define.rest.publishing.MSWordPreviewPublisher;
import org.eclipse.osee.define.rest.publishing.MSWordTemplatePublisher;
import org.eclipse.osee.define.rest.publishing.PublishingErrorLog;
import org.eclipse.osee.define.rest.publishing.PublishingSharedArtifactsFolder;
import org.eclipse.osee.define.rest.publishing.PublishingUtils;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author Ryan D. Brooks
 */
public class MSWordOperationsImpl implements MSWordOperations {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final Log logger;
   private final EventAdmin eventAdmin;

   public MSWordOperationsImpl(OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
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
   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {
      //Generate filename with the headArtifact name and current time
      String name = orcsApi.getQueryFactory().fromBranch(branch).andId(headArtifact).asArtifactToken().getName();
      SimpleDateFormat format = new SimpleDateFormat("MM-dd_HH-mm-ss");
      Date date = new Date(System.currentTimeMillis());
      String time = format.format(date);
      String fileName = name + "_" + time + ".xml";

      PublishingOptions publishingOptions = new PublishingOptions();
      publishingOptions.branch = branch;
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.excludeFolders = true;
      publishingOptions.view = view;

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream opStream) throws WebApplicationException {
            try (Writer writer = new OutputStreamWriter(opStream)) {
               MSWordTemplatePublisher publisher =
                  new MSWordTemplatePublisher(publishingOptions, writer, orcsApi, atsApi);
               publisher.publish(template, Arrays.asList(headArtifact));
               writer.close();
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      };

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public Response msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view) {
      //Generate filename with the headArtifact name and current time
      String name;
      if (artifacts.size() == 1) {
         name = orcsApi.getQueryFactory().fromBranch(branch).andId(artifacts.get(0)).asArtifactToken().getName();
      } else {
         name = String.format("%d_Artifacts_Preview", artifacts.size());
      }
      SimpleDateFormat format = new SimpleDateFormat("MM-dd_HH-mm-ss");
      Date date = new Date(System.currentTimeMillis());
      String time = format.format(date);
      String fileName = name + "_" + time + ".xml";

      PublishingOptions publishingOptions = new PublishingOptions();
      publishingOptions.branch = branch;
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.excludeFolders = false;
      publishingOptions.view = view;

      StreamingOutput streamingOutput = new StreamingOutput() {

         @Override
         public void write(OutputStream opStream) throws WebApplicationException {
            try (Writer writer = new OutputStreamWriter(opStream)) {
               MSWordPreviewPublisher publisher =
                  new MSWordPreviewPublisher(publishingOptions, writer, orcsApi, atsApi);
               publisher.publish(template, artifacts);
               writer.close();
            } catch (Exception ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      };

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   /**
    * {@inheritDoc}
    *
    * @throws BadRequestException {@inheritDoc}
    */

   @Override
   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue) {

      var publishingUtils = new PublishingUtils(this.orcsApi);
      var publishingErrorLog = new PublishingErrorLog();

      //@formatter:off
      var publishingSharedArtifactsFolder =
         ArtifactTypeToken.SENTINEL.equals( artifactType )
            ? PublishingSharedArtifactsFolder.create
                 (
                   publishingUtils,
                   publishingErrorLog,
                   BranchId.create( branch.getId(), view ),
                   "Shared Artifacts Folder",
                   ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                   attributeType
                 )
            : PublishingSharedArtifactsFolder.create
                    (
                      publishingUtils,
                      publishingErrorLog,
                      BranchId.create( branch.getId(), view ),
                      "Shared Artifacts Folder",
                      ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                      artifactType,
                      attributeType
                    );
      //@formatter:on

      var sharedArtifacts = publishingSharedArtifactsFolder.getSharedArtifacts(attributeValue);

      if (publishingErrorLog.size() > 0) {
         var message = new StringBuilder(1024);
         publishingErrorLog.publishErrorLog(message);
         throw new BadRequestException(message.toString(), Response.status(Response.Status.BAD_REQUEST).build());
      }

      @SuppressWarnings("unchecked")
      var sharedArtifactTokens = (List<ArtifactToken>) (Object) sharedArtifacts;

      return sharedArtifactTokens;
   }

}
