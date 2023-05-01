/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.osee.define.operations.publishing.PublishingUtils;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * A {@link PublishingTemplateCache} implementation for OSEE Artifact RendererTemplateWholeWord Publishing Templates.
 *
 * @author Loren K. Ashley
 */

class ArtifactPublishingTemplateCache extends AbstractPublishingTemplateCache {

   /**
    * Saves the single instance of the {@link PublishingTemplateCache} implementation.
    */

   private static ArtifactPublishingTemplateCache artifactPublishingTemplateCache = null;

   /**
    * Saves a handle to the {@link PublishingUtils} instance used to obtain OSEE Artifacts from the database.
    */

   private final PublishingUtils publishingUtils;

   /**
    * Creates a new empty instance of the {@link ArtifactPublishingTemplateCache}. The constructor is private to prevent
    * the instantiation of more than instance.
    *
    * @param logger a handle to the {@link Log} service.
    * @param orcsApi a handle to the {@link OrcsApi} service.
    */

   private ArtifactPublishingTemplateCache(Log logger, OrcsApi orcsApi) {
      super(logger);
      this.publishingUtils = new PublishingUtils(orcsApi);
   }

   /**
    * Factory method to get or create the single instance of the {@link ArtifactPublishingTemplateCache}. Creation of
    * the cache will not immediately load the Artifact Publishing Templates.
    *
    * @param logger a handle to the {@link Log} service.
    * @param orcsApi a handle to the {@link OrcsApi} service.
    * @return the single instance of the {@link PublsishingTemplateCache} implementation.
    */

   synchronized static PublishingTemplateCache create(Log logger, OrcsApi orcsApi) {
      //@formatter:off
      return
         Objects.nonNull( ArtifactPublishingTemplateCache.artifactPublishingTemplateCache )
            ? ArtifactPublishingTemplateCache.artifactPublishingTemplateCache
            : ( ArtifactPublishingTemplateCache.artifactPublishingTemplateCache = new ArtifactPublishingTemplateCache(logger, orcsApi) );
      //@formatter:on
   }

   /**
    * Factory method to create an {@link ArtifactPublishingTemplate} object from an {@link ArtifactReadable} containing
    * publishing template data.
    *
    * @param artifactReadable the Publishing Template {@link ArtifactReadable}.
    * @return on success an {@link Optional} containing the created {@link ArtifactPublishingTemplate}; otherwise, an
    * empty {@link Optional}.
    */

   Optional<ArtifactPublishingTemplate> createPublishingTemplate(ArtifactReadable artifactReadable) {
      try {
         return Optional.of(new ArtifactPublishingTemplate(artifactReadable));
      } catch (Exception e) {
         this.logger.error(e,
            "ArtifactPublishingTemplateCache::createPublishingTemplate, Invalid Publishing Template.");
         return Optional.empty();
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * Loads all RendererTemplateWholeWord Artifacts on the Common Branch and creates a new
    * {@link ArtifactPublishingTemplate} from each one.
    */

   @Override
   synchronized public List<PublishingTemplateInternal> loadTemplates() {

      //@formatter:off
      return
         this.publishingUtils
            .getArtifactReadablesByType
               (
                  new BranchSpecification( CoreBranches.COMMON ),
                  CoreArtifactTypes.RendererTemplateWholeWord
               )
            .orElseThrow
               (
                  () ->
                  {
                     var cause     = this.publishingUtils.getLastCause();
                     var exception = PublishingUtils.Cause.OK.equals( cause )
                                        ? null
                                        : this.publishingUtils.getLastError();

                     var message =
                        new Message()
                               .title( "ArtifactPublishingTemplateCache::loadTemplates, no Publishing Templates found." )
                               .indentInc()
                               .segment( "Query Result", cause )
                               .reasonFollowsIfPresent( exception )
                               .toString();

                     return new OseeCoreException( message, exception );
                  }
               )
            .stream()
            .map( this::createPublishingTemplate )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .collect( Collectors.toUnmodifiableList() );
         //@formatter:on
   }

}

/* EOF */
