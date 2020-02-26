/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.define.rest.publishing.TemplatePublisherPreviewStreamingOutput;
import org.eclipse.osee.define.rest.publishing.TemplatePublisherStreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.LinkType;
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
         new TemplatePublisherStreamingOutput(publishingOptions, template, headArtifact, orcsApi, logger);

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }

   @Override
   public Response msWordTemplatePublishPreview(BranchId branch, ArtifactId template, ArtifactId headArtifact) {
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
         new TemplatePublisherPreviewStreamingOutput(publishingOptions, template, headArtifact, orcsApi, logger);

      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + fileName);
      return builder.build();
   }
}
