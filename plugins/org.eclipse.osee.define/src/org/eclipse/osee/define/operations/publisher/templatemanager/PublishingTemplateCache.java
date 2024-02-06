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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.operations.api.publisher.dataaccess.DataAccessOperations;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateScalarKey;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateVectorKey;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.logger.Log;

/**
 * A {@link PublishingTemplateCache} implementation for OSEE Artifact RendererTemplateWholeWord Publishing Templates.
 *
 * @author Loren K. Ashley
 */

class PublishingTemplateCache {

   /**
    * Saves the single instance of the {@link PublishingTemplateCache} implementation.
    */

   private static PublishingTemplateCache publishingTemplateCache = null;

   /**
    * Factory method to get or create the single instance of the {@link PublishingTemplateCache}. Creation of the cache
    * will not immediately load the Artifact Publishing Templates.
    *
    * @param logger a handle to the {@link Log} service.
    * @param dataAccessOperations a handle to the {@link DataAccessOperations} for database access.
    * @return the single instance of the {@link PublsishingTemplateCache} implementation.
    */

   synchronized static PublishingTemplateCache create(Log logger, DataAccessOperations dataAccessOperations) {
      //@formatter:off
      return
         Objects.isNull( PublishingTemplateCache.publishingTemplateCache )
            ? PublishingTemplateCache.publishingTemplateCache =
                 new PublishingTemplateCache
                        (
                           Objects.requireNonNull( logger ),
                           Objects.requireNonNull( dataAccessOperations )
                        )
            : PublishingTemplateCache.publishingTemplateCache;
      //@formatter:on
   }

   /**
    * Nulls the static reference to the {@link PublishingTemplateCache} instance so that it can be garbage collected.
    */

   synchronized static void free() {
      PublishingTemplateCache.publishingTemplateCache = null;
   }

   /**
    * Saves a handle to the {@link DataAccessOperations} instance used to obtain OSEE Artifacts from the database.
    */

   private final DataAccessOperations dataAccessOperations;

   /**
    * The primary map used for caching. <code>null</code> is a sentinel value used to indicate the cache has not yet
    * been loaded or has been deleted.
    */

   protected RankMap<List<PublishingTemplate>> keyMap;

   /**
    * Saves a list of the {@link PublishingTemplate} objects provided by the extension of this class. The list is saved
    * so that a list of Publishing Template Key Groups can be returned with out duplicates. The
    * {@link PublishingTemplate} object will be stored in the map under each of their primary and secondary key sets. So
    * streaming the map {@link #keyMap} values will result in many duplicates in the stream.
    */

   private List<PublishingTemplate> list;

   /**
    * Saves a handle to the {@link Log} service.
    */

   protected Log logger;

   /**
    * Creates a new empty instance of the {@link PublishingTemplateCache}. The constructor is private to prevent the
    * instantiation of more than instance.
    *
    * @param logger a handle to the {@link Log} service.
    * @param dataAccessOperations a handle to the {@link DataAccessOperations} for database access.
    */

   private PublishingTemplateCache(Log logger, DataAccessOperations dataAccessOperations) {
      this.logger = logger;
      this.dataAccessOperations = dataAccessOperations;
      this.deleteCache();
   }

   /**
    * Extracts the secondary cache key for each primary cache key type from the Publishing Template and caches the
    * Publishing Template under all of the primary and secondary key combinations.
    *
    * @param publishingTemplateInternal the publishing template to be cached
    */

   private void addToKeyMap(PublishingTemplate publishingTemplateInternal) {

      for (var primaryKey : PublishingTemplateKeyType.values()) {

         var secondaryKeyIterable = publishingTemplateInternal.getKeyIterable(primaryKey);

         for (var secondaryKey : secondaryKeyIterable) {

            //@formatter:off
            this.keyMap
               .get( primaryKey, secondaryKey )
               .ifPresentOrElse
                  (
                     ( publishingTemplateInternalList ) -> publishingTemplateInternalList.add( publishingTemplateInternal ),
                     () ->
                     {
                        var publishingTemplateInternalList = new ArrayList<PublishingTemplate>();
                        publishingTemplateInternalList.add(publishingTemplateInternal);
                        this.keyMap.associate(publishingTemplateInternalList, primaryKey, secondaryKey);
                     }
                  );
            //@formatter:on
         }
      }
   }

   /**
    * Loads and caches the Publishing Templates when the cache is not loaded. The specialization of this class is used
    * to load the Publishing Templates on to the member {@link #list} as {@link PublishingTemplate} implementations.
    * Once the {@link PublishingTemplate} implementations are loaded they are indexed on to the members
    * {@link #matchCriteriaMap} and {@link #keyMap}. This method is synchronized to prevent more that one thread from
    * loading the Publishing Templates.
    */

   synchronized private void cacheTemplates() {

      if (this.isCacheLoaded()) {
         return;
      }

      this.list = this.loadTemplates();

      @SuppressWarnings("unchecked")
      //@formatter:off
      var keyMap =
         new RankHashMap<List<PublishingTemplate>>
                (
                   "KeyMap",
                   2,
                   256,
                   0.75f,
                   new Predicate[]
                      {
                         (o) -> o instanceof PublishingTemplateKeyType,
                         (o) -> ( o instanceof PublishingTemplateScalarKey ) || ( o instanceof PublishingTemplateVectorKey )
                      }
                );
      //@formatter:on
      this.keyMap = keyMap;

      this.list.stream().forEach(this::addToKeyMap);

      this.sortMatchCritera();

      this.logDuplicates();
   }

   /**
    * Factory method to create an {@link PublishingTemplate} object from an {@link ArtifactReadable} containing
    * publishing template data.
    *
    * @param artifactReadable the Publishing Template {@link ArtifactReadable}.
    * @return on success an {@link Optional} containing the created {@link PublishingTemplate}; otherwise, an empty
    * {@link Optional}.
    */

   Optional<PublishingTemplate> createPublishingTemplate(@NonNull ArtifactReadable artifactReadable) {

      //@formatter:off
      var publishingTemplate =
         PublishingTemplate
            .create( artifactReadable )
            .getFirstIfPresentOthers( this.logger::error );
      //@formatter:on

      return Optional.ofNullable(publishingTemplate);
   }

   /**
    * Deletes the contents of the cache. After this method is invoked calls to any of the other interface methods will
    * cause the cache to be reloaded.
    */

   public void deleteCache() {
      this.keyMap = null;
      this.list = null;
   }

   /**
    * Gets a publishing template by a primary and secondary key pair. In the case where more than one publishing
    * template has the same key pair, the first publishing template found is returned. The following key pair types are
    * supported:
    * <dl>
    * <dt>Primary Key: &quot;NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template name.</dd>
    * <dt>Primary Key: &quot;SAFE_NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template safe name.</dd>
    * <dt>Primary Key: &quot;IDENTIFIER&quot;</dt>
    * <dd>Secondary Key: the publishing template identifier.</dd>
    * </dl>
    *
    * @param primaryKey the primary search key.
    * @param secondaryKey the secondary search key.
    * @return the first found {@link PublishingTemplate}.
    */

   public Optional<PublishingTemplate> findFirstTemplate(PublishingTemplateKeyType key, String identifier) {

      this.loadCacheIfNeeded();

      if (Objects.isNull(key) || Objects.isNull(identifier)) {
         return Optional.empty();
      }

      //@formatter:off
      var firstTemplate =
         this.keyMap
            .get
               (
                  key,
                  new PublishingTemplateScalarKey( identifier, key )
               )
            .map( (templateList) -> templateList.size() > 0 ? templateList.get( 0 ) : null );
      //@formatter:on

      return firstTemplate;
   }

   /**
    * The match criteria on the <code>matchCriteria</code> {@link List} are sequentially used to search the cache for a
    * a {@link PublishingTemplate} with a matching match criteria.
    *
    * @param matchCriteria a {@link List} of match criteria {@link String}s.
    * @return when found, an {@link Optional} containing the first found {@link PublishingTemplate}; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<PublishingTemplate> findFirstTemplateByMatchCriteria(List<String> matchCriteria) {

      this.loadCacheIfNeeded();

      //@formatter:off
      var firstTemplate =
         matchCriteria.stream()
            .map( ( matchCriterionString ) -> new PublishingTemplateScalarKey( matchCriterionString, PublishingTemplateKeyType.MATCH_CRITERIA ) )
            .map( ( matchCriterionKey ) -> this.keyMap.get( PublishingTemplateKeyType.MATCH_CRITERIA, matchCriterionKey ) )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .map( ( publishingTemplateInternalList ) -> publishingTemplateInternalList.isEmpty() ? null : publishingTemplateInternalList.get(0) )
            ;
      //@formatter:on

      return firstTemplate;
   }

   /**
    * Gets an unordered unmodifiable {@link List} of the {@link PublishingTemplateKeyGroups} for each publishing
    * template held by the cache.
    *
    * @return an unmodifiable {@link List} of the {@link PublishingTemplateKeyGroups} held by the cache.
    */

   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups() {

      this.loadCacheIfNeeded();

      //@formatter:off
      var publishingTemplateKeyGroupList =
         this.list.stream()
            .map( PublishingTemplate::getPublishingTemplateKeyGroup )
            .collect( Collectors.toUnmodifiableList() );
      //@formatter:on

      return new PublishingTemplateKeyGroups(publishingTemplateKeyGroupList);
   }

   /**
    * Predicate to determine if the cache has been loaded.
    *
    * @return <code>true</code> when the cache is loaded; otherwise, <code>false</code>.
    */

   protected boolean isCacheLoaded() {
      return Objects.nonNull(this.keyMap);
   }

   /**
    * Verifies the cache is loaded and loads the cache if it is not loaded.
    */

   protected void loadCacheIfNeeded() {
      if (!this.isCacheLoaded()) {
         this.cacheTemplates();
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * Loads all RendererTemplateWholeWord Artifacts on the Common Branch and creates a new {@link PublishingTemplate}
    * from each one.
    */

   synchronized public List<PublishingTemplate> loadTemplates() {

      //@formatter:off
      return
         this.dataAccessOperations
            .getArtifactReadablesByType
               (
                  new BranchSpecification( CoreBranches.COMMON ),
                  CoreArtifactTypes.RendererTemplateWholeWord
               )
            .orElseThrow
               (
                  ( exception ) ->
                  {
                     var cause   = exception.getPublishingUtilCause();

                     var message =
                        new Message()
                               .title( "PublishingTemplateCache::loadTemplates, no Publishing Templates found." )
                               .indentInc()
                               .segment( "Query Result", cause )
                               .reasonFollows( exception )
                               .toString();

                     return new OseeCoreException( message, exception );
                  }
               )
            .stream()
            .map( ( artifactReadable ) -> Conditions.applyWhenNonNull( artifactReadable, ( a ) -> this.createPublishingTemplate( a ) ) )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .collect( Collectors.toUnmodifiableList() );
         //@formatter:on
   }

   /**
    * Logs a message for each for Primary and Secondary key pair where more than one {@link PublishingTemplate} matches.
    */

   private void logDuplicates() {

      //@formatter:off
      this.keyMap.streamEntries( )
         .filter( ( entry ) -> entry.getValue().size() > 1 )
         .forEach
            (
               ( entry ) ->
               {
                  if( !this.logger.isInfoEnabled() ) {
                     return;
                  }

                  var primaryKey = (PublishingTemplateKeyType) entry.getKey(0);
                  var secondaryKey = entry.getKey(1);

                  var publishingTemplateInternalList = entry.getValue();

                  var primaryTemplateName = publishingTemplateInternalList.get(0).getName();
                  var primaryTemplateIdentifier = publishingTemplateInternalList.get(0).getIdentifier();

                  var message =
                     new Message()
                            .blank()
                            .title( "PublishingTemplateProvider has detected a conflict." )
                            .indentInc()
                            .segment( "Key Type", primaryKey )
                            .segment( "Publishing Template Key", secondaryKey )
                            .title( "The Publishing Template That Will Be Used" )
                            .indentInc()
                            .segment( "Name",       primaryTemplateName       )
                            .segment( "Identifier", primaryTemplateIdentifier )
                            .indentDec()
                            .title( "All Matching Templates")
                            .indentInc()
                            ;
                  publishingTemplateInternalList.forEach
                     (
                        ( publishingTemplate ) ->
                           message
                              .title( publishingTemplate.getName().toString() )
                              .indentInc()
                              .segment( "Identifier", publishingTemplate.getIdentifier() )
                              .segmentToMessage( "Match Criteria", publishingTemplate.getMatchCriteria() )
                              .indentDec()
                     );

                  this.logger.infoNoFormat( null, message.toString() );
               }
            );
      //@formatter:on
   }

   /**
    * Sorts the Publishing Templates associated with the same primary and secondary key pair by the Publishing Template
    * names.
    */

   private void sortMatchCritera() {

      //@formatter:off
      this.keyMap.stream()
         .filter( ( publishingTemplateInternalList ) -> publishingTemplateInternalList.size() > 1 )
         .forEach
            (
               ( publishingTemplateInternalList ) -> publishingTemplateInternalList.sort
                                                        (
                                                          new Comparator<PublishingTemplate>() {

                                                             @Override
                                                             public int compare(PublishingTemplate o1, PublishingTemplate o2) {
                                                                return o1.getName().compareTo( o2.getName() );
                                                             }
                                                          }
                                                        )
            );
         //@formatter:on
   }

}

/* EOF */
