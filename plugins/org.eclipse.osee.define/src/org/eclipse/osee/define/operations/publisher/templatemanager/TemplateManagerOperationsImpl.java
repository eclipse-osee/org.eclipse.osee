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

package org.eclipse.osee.define.operations.publisher.templatemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.eclipse.osee.define.operations.api.publisher.dataaccess.DataAccessOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Validation;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * An implementation of the {@link TemplateManagerOperations} interface.
 *
 * @author Loren K. Ashley
 */

public class TemplateManagerOperationsImpl implements TemplateManagerOperations {

   /**
    * Saves the single instance of the {@link TemplateManagerOperationsImpl}.
    */

   private static TemplateManagerOperationsImpl templateManagerOperationsImpl = null;

   /**
    * Gets or creates the single instance of the {@link TemplateManagerImpl} class.
    *
    * @param jdbcService the {@link JdbcService} handle.
    * @param logger the {@link Log} handle.
    * @param dataAccessOperations the {@link DataAccessOperations} handle.
    * @return the single {@link TemplateManagerImpl} object.
    * @throws NullPointerException when any of the following parameters is <code>null</code> and the single instance of
    * the {@link TemplateManagerImpl} has not yet been created:
    * <ul>
    * <li><code>jdbcService</code>,</li>
    * <li><code>logger</code>, or</li>
    * <li><code>dataAccessOperations</code>.</li>
    * </ul>
    */

   public synchronized static TemplateManagerOperationsImpl create(JdbcService jdbcService, Log logger, DataAccessOperations dataAccessOperations) {

      //@formatter:off
      return
         Objects.isNull( TemplateManagerOperationsImpl.templateManagerOperationsImpl )
            ? ( TemplateManagerOperationsImpl.templateManagerOperationsImpl =
                   new TemplateManagerOperationsImpl
                          (
                             Objects.requireNonNull(jdbcService),
                             Objects.requireNonNull(logger),
                             Objects.requireNonNull(dataAccessOperations)
                          )
              )
            : TemplateManagerOperationsImpl.templateManagerOperationsImpl;
      //@formatter:on
   }

   /**
    * Frees the {@link PublishingTemplateCache} and Nulls the static reference to the
    * {@link TemplateManagerOperationsImpl} instance so that it can be garbage collected.
    */

   public synchronized static void free() {
      PublishingTemplateCache.free();
      TemplateManagerOperationsImpl.templateManagerOperationsImpl = null;
   }

   /**
    * Saves the JDBC handle, used to get the value of the "no tags" toggle.
    */

   private final JdbcService jdbcService;

   /**
    * Regular expression patter used to extract the {@link PublishingTemplateProvider} key from a Publishing Template
    * Identifier.
    */

   private final Pattern keyPrefixPattern;

   /**
    * Saves a reference to the {@link PublishingTemplateCache}.
    */

   protected PublishingTemplateCache publishingTemplateCache;

   /**
    * Creates an object to process template manager REST calls.
    *
    * @param jdbcService the {@link JdbcService} handle.
    * @param logger the {@link Log} handle.
    * @param dataAccessOperations the {@link DataAccessOperations} handle.
    */

   private TemplateManagerOperationsImpl(JdbcService jdbcService, Log logger, DataAccessOperations dataAccessOperations) {

      this.jdbcService = jdbcService;

      this.keyPrefixPattern = Pattern.compile("(^[A-Z]+)-");

      this.publishingTemplateCache = PublishingTemplateCache.create(logger, dataAccessOperations);

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {
      this.publishingTemplateCache.deleteCache();
   }

   /**
    * Builds an ordered list of the expected Publishing Template match criteria from the
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
   public org.eclipse.osee.framework.core.publishing.PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest) {

      //@formatter:off
      Validation.require
         (
            publishingTemplateRequest,
            Validation.ValueType.PARAMETER,
            "TemplateManagerOperationsImpl",
            "getPublishingTemplate",
            "publishingTemplateRequest",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "publishing template request is valid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );
      //@formatter:on

      boolean isNoTags = Boolean.valueOf(OseeInfo.getValue(this.jdbcService.getClient(), "osee.publish.no.tags"));

      //@formatter:off
      var publishingTemplate =
         ( publishingTemplateRequest.isByOptions()
              ? this.publishingTemplateCache
                   .findFirstTemplate( PublishingTemplateKeyType.NAME, publishingTemplateRequest.getOption() )
                   .or
                      (
                         () -> this.publishingTemplateCache.findFirstTemplateByMatchCriteria
                                  (
                                     this.getPossibleTemplateNamesOrderedBySpecialization( publishingTemplateRequest, isNoTags )
                                  )
                      )
              : this.publishingTemplateCache
                   .findFirstTemplate( PublishingTemplateKeyType.IDENTIFIER, publishingTemplateRequest.getTemplateId() )
         )
         .map( PublishingTemplate::getBean )
         .orElse( org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL );
      //@formatter:on

      return publishingTemplate;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public org.eclipse.osee.framework.core.publishing.PublishingTemplate getPublishingTemplate(String primaryKey, String secondaryKey) {

      Message message = null;

      //@formatter:off
      message = Validation.requireNonNull( message, primaryKey,   "TemplateManagerOperationsImpl", "getPublishingTemplate", "primayKey"    );
      message = Validation.requireNonNull( message, secondaryKey, "TemplateManagerOperationsImpl", "getPublishingTemplate", "secondaryKey" );

      if (Objects.nonNull(message)) {
         var exceptionMessage = Validation.buildIllegalArgumentExceptionMessage( this.getClass().getSimpleName(), "getPublishingTemplate", message );
         throw new IllegalArgumentException( exceptionMessage );
      }
      //@formatter:on

      var publishingTemplateCacheKey = PublishingTemplateKeyType.valueOf(primaryKey);

      switch (publishingTemplateCacheKey) {
         case NAME:
         case SAFE_NAME: {
            //@formatter:off
            return
               this.publishingTemplateCache
                  .findFirstTemplate( publishingTemplateCacheKey, secondaryKey )
                  .map( PublishingTemplate::getBean )
                  .orElse( org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL );
            //@formatter:on
         }

         case IDENTIFIER: {
            var prefixMatcher = this.keyPrefixPattern.matcher(secondaryKey);

            if (!prefixMatcher.matches()) {
               return org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL;
            }

            var localIdentifier = secondaryKey.substring(prefixMatcher.end(0) + 2);

            //@formatter:off
            return
               this.publishingTemplateCache
                  .findFirstTemplate( publishingTemplateCacheKey, localIdentifier )
                  .map( PublishingTemplate::getBean )
                  .orElse( org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL );
            //@formatter:on
         }

         default:
            return org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL;
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups() {
      //@formatter:off
      var publishingTemplateKeyGroupList =
         new ArrayList<>
                (
                   this.publishingTemplateCache
                      .getPublishingTemplateKeyGroups()
                      .getPublishingTemplateKeyGroupList()
                );
      //@formatter:on

      Collections.sort(publishingTemplateKeyGroupList);

      var publishingTemplateSafeNames = new PublishingTemplateKeyGroups(publishingTemplateKeyGroupList);

      return publishingTemplateSafeNames;
   }

}

/* EOF */
