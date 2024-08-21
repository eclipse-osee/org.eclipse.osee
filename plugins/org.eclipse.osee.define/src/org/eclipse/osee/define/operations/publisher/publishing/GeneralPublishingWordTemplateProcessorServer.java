/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.IncludeBookmark;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.core.publishing.WordRenderUtil;
import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * This subclass of the {@link WordTemplateProcessorServer} is the publishing process for server-side previews with
 * similar features to the client-side publishing preview.
 *
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class GeneralPublishingWordTemplateProcessorServer extends WordTemplateProcessorServer {

   /**
    * Creates a new instance for a publish.
    *
    * @param orcsApi handle to the {@link OrcsApi} used by super class.
    * @param atsApi handle to the {@link AtsApi} used by super class to access logging facilities.
    */

   public GeneralPublishingWordTemplateProcessorServer(OrcsApi orcsApi, AtsApi atsApi, DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations) {
      super(orcsApi, atsApi, dataAccessOperations, dataRightsOperations);
   }

   /**
    * {@inheritDoc}
    * <p>
    * When the flags {@link this#includeEmptyHeaders} and {@link this#recurseChildren} are set empty headers will be
    * excluded from the publish.
    * <p>
    * Data rights will be determined for the artifacts being published.
    * <p>
    * Then the super class {@link WordTemplateProcessorServer#processedArtifact} will be called once for each artifact
    * on the <code>artifacts</code> list. The method {@link WordTemplateProcessorServer#processedArtifact} may
    * optionally process all hierarchical descendants of the artifact passed to it.
    *
    * @param artifacts a list of the top level artifacts to be published.
    * @param wordMl all WordMl is written to this object.
    */

   //@formatter:off
   @Override
   protected void
      processArtifactSet
         (
            List<PublishingArtifact> artifacts,
            PublishingAppender       wordMl
         ) {

      this.emptyFoldersArtifactAcceptor =
         WordRenderUtil.populateEmptyHeaders
            (
               artifacts,
               this.includeHeadings,
               this.allowedOutlineTypes,
               this.headingArtifactTypeToken,
               this.contentAttributeType,
               this.excludedArtifactTypeArtifactAcceptor,
               ArtifactAcceptor.ok()
            );

      /**
       * Setup Data Rights for the publish.
       * <p>
       * DataRightsClassification override comes in as a publishing option string, compare string to all
       * DataRightsClassifications, if they match, set override variable to that classification. This override makes it
       * that the entire published document uses the same data rights footer, regardless of the attribute on artifacts.
       * <p>
       * Given the list of artifacts for the publish, this loops through and adds any recursive artifacts to also be
       * published (if specified through recurseChildren) and determines all of their data rights.
       */

      //@formatter:off
      WordRenderUtil
         .getDataRights
            (
               /*
                * Publish artifact to analyze for data rights
                */

               artifacts,

               /*
                * The publishing branch
                */

               this.branchSpecification.getBranchId(),

               /*
                * Recursion logic
                */

               this.recurseChildren,

               /*
                * Not Historical, false -> accept descendants of historical artifacts and historical descendants
                */

               false,

               /*
                * Data rights classification override
                */

               this.overrideClassification,

               /*
                * When recursing, this tester accepts or rejects descendant artifacts
                */

               ArtifactAcceptor.and
                  (
                     this.wordRenderApplicabilityChecker,
                     this.emptyFoldersArtifactAcceptor
                  ),

               /*
                * Client/Server calling of the Data Rights Manager is different.
                */

               this.dataRightsOperations::getDataRights

            )
         .ifPresent
            (
               ( datarightsContentBuilder ) ->
                  artifacts
                     .forEach
                        (
                           ( artifact ) -> this.processArtifact
                                              (
                                                 artifact,
                                                 wordMl,
                                                 ArtifactAcceptor.ok(),
                                                 datarightsContentBuilder,
                                                 PublishingArtifactLoader.CacheReadMode.LOAD_FROM_DATABASE,
                                                 IncludeBookmark.YES.getArtifactAcceptor(),
                                                 null   /* Artifact Post Processor */
                                              )
                        )
            );
   }
   //@formatter:on
}

/* EOF */