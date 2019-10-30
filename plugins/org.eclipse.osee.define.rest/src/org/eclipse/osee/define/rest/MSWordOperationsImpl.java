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

import java.util.Set;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.define.rest.operations.NestedTemplateStreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
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
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);
      return updateArt.updateArtifacts(data);
   }

   @Override
   public StreamingOutput publishWithNestedTemplates(BranchId branch, ArtifactId masterTemplate, ArtifactId slaveTemplate, ArtifactId headArtifact) {
      PublishingOptions publishingOptions = new PublishingOptions();
      //default options
      publishingOptions.branch = branch;
      publishingOptions.includeUuids = false;
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.updateParagraphNumbers = false;
      publishingOptions.excludeArtifactTypes = null;
      publishingOptions.excludeFolders = true;
      publishingOptions.recurse = true;
      publishingOptions.maintainOrder = true;
      publishingOptions.useTemplateOnce = true;
      publishingOptions.firstTime = true;
      publishingOptions.publishDiff = false;
      publishingOptions.view = ArtifactReadable.SENTINEL;
      publishingOptions.publishEmptyHeaders = false;
      publishingOptions.overrideDataRights = "";

      if (masterTemplate.getId().equals(-1L)) {
         masterTemplate = ArtifactId.SENTINEL;
      }
      if (slaveTemplate.getId().equals(-1L)) {
         slaveTemplate = ArtifactId.SENTINEL;
      }

      StreamingOutput streamingOutput = new NestedTemplateStreamingOutput(publishingOptions, masterTemplate,
         slaveTemplate, headArtifact, orcsApi, logger);

      return streamingOutput;
   }
}
