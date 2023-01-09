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

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplate;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateSafeNames;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.util.OsgiUtils;
import org.eclipse.osee.define.util.Validation;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

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
    * Saves the JDBC handle.
    */

   private final JdbcService jdbcService;

   /**
    * Regular expression patter used to extract the {@link PublishingTemplateProvider} key from a Publishing Template
    * Identifier.
    */

   private final Pattern keyPrefixPattern;

   /**
    * Saves the Log handle.
    */

   private final Log logger;

   /**
    * Saves the orcsApi handle.
    */

   private final OrcsApi orcsApi;

   /**
    * Saves a list of the {@link PublishingTemplateProvider}s.
    */

   private final List<PublishingTemplateProvider> publishingTemplateProviders;

   /**
    * Saves a map of the {@link PublishingTemplateProvider}s by their publishing template key prefix.
    */

   private final Map<String, PublishingTemplateProvider> publishingTemplateProvidersByKeyPrefix;

   /**
    * Creates an object to process template manager REST calls.
    *
    * @param jdbcService the {@link JdbcService} handle.
    * @param logger the {@link Log} handle.
    * @param orcsApi the {@link OrcsApi} handle.
    */

   private TemplateManagerOperationsImpl(JdbcService jdbcService, Log logger, OrcsApi orcsApi) {

      this.jdbcService = jdbcService;

      this.keyPrefixPattern = Pattern.compile("(^[A-Z]+)-");

      this.logger = logger;

      this.orcsApi = orcsApi;

      //@formatter:off
      var publishingTemplateProviderClassByKeyPrefixMap =
         OsgiUtils.findImplementations
            (
               "org/eclipse/osee/define/operations/publishing/templatemanager", /* Package path to search for classes. */
               IsPublishingTemplateProvider.class,                              /* Classes must have this Annotation class to be found. */
               "key",                                                           /* Annotation parameter (class method) to get the Publishing Template key from. */
               PublishingTemplateProvider.class                                 /* Classes must implement this interface to be found. */
            );
      //@formatter:on

      //@formatter:off
      this.publishingTemplateProvidersByKeyPrefix =
         publishingTemplateProviderClassByKeyPrefixMap.entrySet().stream()

            /*
             * Map String key and Class<PublishingTemplateProvider> value entries
             * into String key and PublishingTemplateProvider value entries.
             */

            .map( ( entry ) -> new AbstractMap.SimpleImmutableEntry<>( entry.getKey(), this.newPublishingTemplateProviderInstance( entry.getValue() ) ) )

            /*
             * Remove entries with a null PublishingTemplateProvider.
             */

            .filter( ( entry ) -> Objects.nonNull( entry.getValue() ) )

            /*
             * Gather the entries into a map.
             */

            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, Map.Entry::getValue ) );
      //@formatter:on

      this.publishingTemplateProviders =
         this.publishingTemplateProvidersByKeyPrefix.values().stream().collect(Collectors.toUnmodifiableList());
   }

   /**
    * Gets or creates the single instance of the {@link TemplateManagerImpl} class.
    *
    * @param jdbcService the {@link JdbcService} handle.
    * @param logger the {@link Log} handle.
    * @param orcsApi the {@link OrcsApi} handle.
    * @return the single {@link TemplateManagerImpl} object.
    * @throws NullPointerException when any of the following parameters is <code>null</code> and the single instance of
    * the {@link TemplateManagerImpl} has not yet been created:
    * <ul>
    * <li><code>jdbcService</code>,</li>
    * <li><code>logger</code>, or</li>
    * <li><code>orcsApi</code>.</li>
    * </ul>
    */

   public synchronized static TemplateManagerOperationsImpl create(JdbcService jdbcService, Log logger, OrcsApi orcsApi) {

      //@formatter:off
      return
         Objects.isNull( TemplateManagerOperationsImpl.templateManagerOperationsImpl )
            ? ( TemplateManagerOperationsImpl.templateManagerOperationsImpl =
                   new TemplateManagerOperationsImpl
                          (
                             Objects.requireNonNull(jdbcService),
                             Objects.requireNonNull(logger),
                             Objects.requireNonNull(orcsApi)
                          )
              )
            : TemplateManagerOperationsImpl.templateManagerOperationsImpl;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {
      this.publishingTemplateProviders.forEach(PublishingTemplateProvider::deleteCache);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( publishingTemplateRequest, "publishingTemplateRequest", message, "is invalid", (p) -> !p.isValid() );

      if (Objects.nonNull(message)) {
         var exceptionMessage = Validation.buildIllegalArgumentExceptionMessage( this.getClass().getSimpleName(), "getPublishingTemplate", message );
         throw new IllegalArgumentException( exceptionMessage );
      }
      //@formatter:on

      boolean isNoTags = Boolean.valueOf(OseeInfo.getValue(this.jdbcService.getClient(), "osee.publish.no.tags"));

      //@formatter:off
      var publishingTemplate =
         this.publishingTemplateProviders.stream()
            .max
               (
                  Comparator.<PublishingTemplateProvider,Integer>comparing
                     (
                       ( publishingTemplateProvider ) -> publishingTemplateProvider.getApplicabilityRating( publishingTemplateRequest ),
                       Integer::compare
                     )
               )
            .flatMap( ( publishingTemplateProvider ) -> publishingTemplateProvider.getTemplate( publishingTemplateRequest, isNoTags ) )
            .map( PublishingTemplateInternal::getBean )
            .orElse( PublishingTemplate.SENTINEL );
      //@formatter:on

      return publishingTemplate;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplate getPublishingTemplate(String primaryKey, String secondaryKey) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( primaryKey,   "primayKey",    message );
      message = Validation.verifyParameter( secondaryKey, "secondaryKey", message );

      if (Objects.nonNull(message)) {
         var exceptionMessage = Validation.buildIllegalArgumentExceptionMessage( this.getClass().getSimpleName(), "getPublishingTemplate", message );
         throw new IllegalArgumentException( exceptionMessage );
      }
      //@formatter:on

      var publishingTemplateCacheKey = PublishingTemplateCacheKey.valueOf(primaryKey);

      switch (publishingTemplateCacheKey) {
         case NAME:
         case SAFE_NAME: {
            //@formatter:off
            return
               this.publishingTemplateProviders.stream()
                  .map
                     (
                        ( publishingTemplateProvider ) -> publishingTemplateProvider.getTemplate( publishingTemplateCacheKey, secondaryKey )
                     )
                  .flatMap( Optional::stream )
                  .findFirst()
                  .map( PublishingTemplateInternal::getBean )
                  .orElse( PublishingTemplate.SENTINEL );
            //@formatter:on
         }

         case IDENTIFIER: {
            var prefixMatcher = this.keyPrefixPattern.matcher(secondaryKey);

            if (!prefixMatcher.matches()) {
               return PublishingTemplate.SENTINEL;
            }

            var prefix = prefixMatcher.group(0);
            var localIdentifier = secondaryKey.substring(prefixMatcher.end(0) + 2);

            var publishingTemplateProvider = this.publishingTemplateProvidersByKeyPrefix.get(prefix);

            if (Objects.isNull(publishingTemplateProvider)) {
               return PublishingTemplate.SENTINEL;
            }

            //@formatter:off
            return
               publishingTemplateProvider.getTemplate(publishingTemplateCacheKey, localIdentifier)
                  .map( PublishingTemplateInternal::getBean )
                  .orElse( PublishingTemplate.SENTINEL );
            //@formatter:on
         }

         default:
            return null;
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplateSafeNames getPublishingTemplateSafeNames() {
      //@formatter:off
      return
         new PublishingTemplateSafeNames
                (
                  this.publishingTemplateProviders.stream()
                     .map( PublishingTemplateProvider::getPublishingTemplateSafeNames )
                     .flatMap( List::stream )
                     .collect( Collectors.toList() )
                );
      //@formatter:on
   }

   /**
    * Creates a new instance of the {@link Class} that implements the {@link PublishingTemplateProvider} interface.
    *
    * @param publishingTemplateProviderClass the {@link Class} implementing the {@link PublishingTemplateProvider}
    * interface to be instantiated.
    * @return on success, a new implementation of the <code>publishingTemplateProviderClass</code>; otherwise,
    * <code>null</code>.
    */

   private PublishingTemplateProvider newPublishingTemplateProviderInstance(Class<? extends PublishingTemplateProvider> publishingTemplateProviderClass) {
      try {
         return publishingTemplateProviderClass.getConstructor(Log.class, OrcsApi.class).newInstance(this.logger,
            this.orcsApi);
      } catch (Exception e) {
         return null;
      }
   }

}

/* EOF */
