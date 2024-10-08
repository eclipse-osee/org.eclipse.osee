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
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   public synchronized static TemplateManagerOperationsImpl create(JdbcService jdbcService, Log logger,
      DataAccessOperations dataAccessOperations, OrcsTokenService orcsTokenService) {

      //@formatter:off
      return
         Objects.isNull( TemplateManagerOperationsImpl.templateManagerOperationsImpl )
            ? ( TemplateManagerOperationsImpl.templateManagerOperationsImpl =
                   new TemplateManagerOperationsImpl
                          (
                             Objects.requireNonNull(jdbcService),
                             Objects.requireNonNull(logger),
                             Objects.requireNonNull(dataAccessOperations),
                             Objects.requireNonNull(orcsTokenService)
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

   private TemplateManagerOperationsImpl(JdbcService jdbcService, Log logger, DataAccessOperations dataAccessOperations, OrcsTokenService orcsTokenService) {

      this.jdbcService = jdbcService;

      this.publishingTemplateCache = PublishingTemplateCache.create(logger, dataAccessOperations, orcsTokenService);
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

   private List<String> getPossibleTemplateNamesOrderedBySpecialization(
      PublishingTemplateRequest publishingTemplateRequest, boolean isNoTags) {

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
   public org.eclipse.osee.framework.core.publishing.PublishingTemplate getPublishingTemplate(
      PublishingTemplateRequest publishingTemplateRequest) {

      //@formatter:off
      Conditions.require
         (
            publishingTemplateRequest,
            ValueType.PARAMETER,
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
      var publishingTemplateBean =
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
                   .findFirstTemplate
                      (
                         PublishingTemplateKeyType.IDENTIFIER,
                         publishingTemplateRequest.getTemplateId()
                      )
         )
         .map
            (
               ( publishingTemplate ) -> publishingTemplate.getBean
                                            (
                                               publishingTemplateRequest.getFormatIndicator()
                                            )
            )
         .orElse( org.eclipse.osee.framework.core.publishing.PublishingTemplate.SENTINEL );
      //@formatter:on

      return publishingTemplateBean;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getPublishingTemplateStatus(PublishingTemplateRequest publishingTemplateRequest) {

      //@formatter:off
      Conditions.require
         (
            publishingTemplateRequest,
            ValueType.PARAMETER,
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
      return
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
                   .findFirstTemplate
                      (
                         PublishingTemplateKeyType.IDENTIFIER,
                         publishingTemplateRequest.getTemplateId()
                      )
         )
         .map( PublishingTemplate::getStatus )
         .orElse( "NOT FOUND" );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups(@NonNull String filterBySafeName) {
      //@formatter:off
      var publishingTemplateKeyGroupList =
         new ArrayList<>
                (
                   this.publishingTemplateCache
                      .getPublishingTemplateKeyGroups()
                      .getPublishingTemplateKeyGroupList()
                );

      Collections.sort(publishingTemplateKeyGroupList);

      var publishingTemplateSafeNames = new PublishingTemplateKeyGroups(publishingTemplateKeyGroupList);

      var filteredList =
         Strings.isValidAndNonBlank(filterBySafeName)
            ? new PublishingTemplateKeyGroups
               (
                  publishingTemplateSafeNames
                     .getPublishingTemplateKeyGroupList()
                     .stream()
                     .filter( key -> Strings.containsIgnoreCase(key.getSafeName().getKey(), filterBySafeName))
                     .collect(Collectors.toList())
               )
            : publishingTemplateSafeNames;

      return filteredList;
      //@formatter:on
   }

}

/* EOF */
