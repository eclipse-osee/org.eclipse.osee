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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;

/**
 * This class provides a skeletal implementation of the {@link PublishingTemplateProvider} interface, to minimize the
 * effort required to implement this interface.
 *
 * @author Loren K. Ashley
 */

abstract class AbstractPublishingTemplateProvider implements PublishingTemplateProvider {

   /**
    * Saves the Log handle.
    */

   @SuppressWarnings("unused")
   private final Log logger;

   /**
    * Saves a reference to the {@link PublishingTemplateCache}.
    */

   protected PublishingTemplateCache publishingTemplateCache;

   /**
    * Creates a new instance with an empty cache.
    *
    * @param logger a handle to the {@link Log} service.
    */

   public AbstractPublishingTemplateProvider(Log logger) {
      this.logger = logger;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {
      this.publishingTemplateCache.deleteCache();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> getTemplate(PublishingTemplateRequest publishingTemplateRequest, boolean isNoTags) {

      if (publishingTemplateRequest.isByOptions()) {
         //@formatter:off
         return
            this.publishingTemplateCache.findFirstTemplate( PublishingTemplateCacheKey.NAME, publishingTemplateRequest.getOption() ).or
               (
                  () -> this.publishingTemplateCache.findFirstTemplateByMatchCriteria
                           (
                              this.getPossibleTemplateNamesOrderedBySpecialization( publishingTemplateRequest, isNoTags )
                           )
               );
         //@formatter:on
      }

      return this.publishingTemplateCache.findFirstTemplate(PublishingTemplateCacheKey.IDENTIFIER,
         publishingTemplateRequest.getTemplateId());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> getTemplate(PublishingTemplateCacheKey primaryKey, String secondaryKey) {

      return this.publishingTemplateCache.findFirstTemplate(primaryKey, secondaryKey);
   }

   /**
    * Builds an ordered list of the expected Publishing Template match criteria form the
    * {@link PublishingTemplateRequest} as follows:
    * <dl>
    * <dt>When options "publishingArtifactTypeName" and "option" are present:</dt>
    * <dd>&lt;rendererId&gt; " " &lt;publishingArtifactTypeName&gt; " " &lt;presentationType&gt; " " &lt;option&gt; ["
    * NO TAGS" ]</dd>
    * <dt>When the option "publishingArtifactTypeName" is present:</dt>
    * <dd>&lt;rendererId&gt; " " &lt;publishingArtifactTypeName&gt; " " &lt;presentationType&gt; [" NO TAGS" ]</dd>
    * <dt>When the option "option" is present:</dt>
    * <dd>&lt;rendererId&gt; " " &lt;presentationType&gt; " " &lt;option&gt; [" NO TAGS" ]</dd>
    * <dt>Always included:</dt>
    * <dd>&lt;rendererId&gt; " " &lt;presentationType&gt; [" NO TAGS" ]</dd>
    * </ul>
    *
    * @param publishingTemplateRequest the {@link PublishingTemplateRequest} options.
    * @param isNoTags the OSEE server "osee.publish.no.tags" flag.
    * @return an ordered {@link List} of expected Publishing Template match criteria.
    * @throws OseeArgumentException when {@link PublishingTemplateRequest#getRendererId} or
    * {@link PublishingTemplateRequest#getPresentationType} is <code>null</code>.
    */

   private List<String> getPossibleTemplateNamesOrderedBySpecialization(PublishingTemplateRequest publishingTemplateRequest, boolean isNoTags) {

      var rendererId = publishingTemplateRequest.getRendererId();
      var presentationType = publishingTemplateRequest.getPresentationType();
      var publishArtifactTypeName = publishingTemplateRequest.getPublishArtifactTypeName();
      var option = publishingTemplateRequest.getOption();

      if (Objects.isNull(rendererId) || Objects.isNull(presentationType)) {

         //@formatter:off
         throw
            new OseeArgumentException
                   (
                      "Invalid renderer[%s] or presentationType[%s]",
                      rendererId == null ? "null" : rendererId,
                      presentationType == null ? "null" : presentationType
                   );
         //@formatter:on

      }

      List<String> list = new ArrayList<>();

      var isNoTagsString = isNoTags ? " NO TAGS" : "";

      if (Objects.nonNull(publishArtifactTypeName) && Objects.nonNull(option)) {
         list.add(rendererId + " " + publishArtifactTypeName + " " + presentationType + " " + option + isNoTagsString);
      }

      if (Objects.nonNull(publishArtifactTypeName)) {
         list.add(rendererId + " " + publishArtifactTypeName + " " + presentationType + isNoTagsString);
      }

      if (Objects.nonNull(option)) {
         list.add(rendererId + " " + presentationType + " " + option + isNoTagsString);
      }

      list.add(rendererId + " " + presentationType + isNoTagsString);

      return list;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   abstract public int getApplicabilityRating(PublishingTemplateRequest publishingTemplateRequest);

   /**
    * {@inheritDoc}
    */

   @Override
   public List<String> getPublishingTemplateSafeNames() {
      return this.publishingTemplateCache.getPublishingTemplateSafeNames();
   }

}

/* EOF */
