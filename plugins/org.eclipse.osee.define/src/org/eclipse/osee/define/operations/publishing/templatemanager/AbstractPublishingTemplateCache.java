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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.logger.Log;

/**
 * Provides a skeletal implementation of a {@link PublishingTemplateCache} to minimize the effort required to implement
 * the {@link PublishingTemplateCache} interface for a specific type of Publishing Template.
 *
 * @author Loren K. Ashley
 */

abstract class AbstractPublishingTemplateCache implements PublishingTemplateCache {

   /**
    * The primary map used for caching
    */

   protected RankMap<List<PublishingTemplateInternal>> keyMap;

   /**
    * Extensions of this class must populate this member with an unordered {@link List} of the Publishing Templates to
    * be held by the {@link AbstractPublishingTemplateClass}. <code>null</code> is a sentinel value used to indicate the
    * cache has not yet been loaded or has been deleted.
    */

   protected List<PublishingTemplateInternal> list;

   /**
    * Saves a handle to the {@link Log} service.
    */

   protected Log logger;

   /**
    * Creates a new empty {@link AbstractPublishingTemplateCache}.
    *
    * @param logger handle to the {@link Log} service.
    */

   AbstractPublishingTemplateCache(Log logger) {

      this.logger = logger;

      this.deleteCache();
   }

   /**
    * Extracts the secondary cache key for each primary cache key type from the Publishing Template and caches the
    * Publishing Template under all of the primary and secondary key combinations.
    *
    * @param publishingTemplateInternal the publishing template to be cached
    */

   private void addToKeyMap(PublishingTemplateInternal publishingTemplateInternal) {

      for (var primaryKey : PublishingTemplateCacheKey.values()) {

         var secondaryKeyIterable = new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
               return primaryKey.extractKey(publishingTemplateInternal);
            }

         };

         for (var secondaryKey : secondaryKeyIterable) {

            var publishingTemplateInternalListOptional = this.keyMap.get(primaryKey, secondaryKey);

            if (!publishingTemplateInternalListOptional.isPresent()) {

               var publishingTemplateInternalList = new ArrayList<PublishingTemplateInternal>();
               publishingTemplateInternalList.add(publishingTemplateInternal);
               this.keyMap.associate(publishingTemplateInternalList, primaryKey, secondaryKey);

            } else {

               publishingTemplateInternalListOptional.get().add(publishingTemplateInternal);

            }
         }
      }
   }

   /**
    * Sorts the Publishing Templates associated with a match criteria by the Publishing Template names.
    */

   private void sortMatchCritera() {

      //@formatter:off
      this.keyMap.stream( PublishingTemplateCacheKey.MATCH_CRITERIA )
         .filter( ( publishingTemplateInternalList ) -> publishingTemplateInternalList.size() > 1 )
         .forEach
            (
               ( publishingTemplateInternalList ) -> publishingTemplateInternalList.sort
                                                        (
                                                          new Comparator<PublishingTemplateInternal>() {

                                                             @Override
                                                             public int compare(PublishingTemplateInternal o1, PublishingTemplateInternal o2) {
                                                                return o1.getName().compareTo( o2.getName() );
                                                             }
                                                          }
                                                        )
            );
      //@formatter:on
   }

   /**
    * Logs a message for each for Match Criteria where more than one {@link PublishingTemplateInternal} matches.
    */

   private void logDuplicateMatchCritera() {

      //@formatter:off
      this.keyMap.streamEntries( PublishingTemplateCacheKey.MATCH_CRITERIA )
         .filter( ( entry ) -> entry.getValue().size() > 1 )
         .forEach
            (
               ( entry ) ->
               {
                  if( !this.logger.isInfoEnabled() ) {
                     return;
                  }

                  var matchCriteria = entry.getKey(1);
                  var publishingTemplateInternalList = entry.getValue();

                  var primaryTemplateName = publishingTemplateInternalList.get(0).getName();
                  var primaryTemplateIdentifier = publishingTemplateInternalList.get(0).getIdentifier();

                  var message =
                     new Message()
                            .blank()
                            .title( "PublishingTemplateProvider has detected a conflict with a Match Criteria." )
                            .indentInc()
                            .segment( "Match Criteria", matchCriteria )
                            .title( "The Publishing Template That Will Be Used" )
                            .indentInc()
                            .segment( "Name",       primaryTemplateName       )
                            .segment( "Identifier", primaryTemplateIdentifier )
                            .indentDec()
                            .segmentIndexedList( "Matching Publishing Templates", publishingTemplateInternalList )
                            ;

                  this.logger.infoNoFormat( null, message.toString() );
               }
            );
      //@formatter:on
   }

   /**
    * Loads and caches the Publishing Templates when the cache is not loaded. The specialization of this class is used
    * to load the Publishing Templates on to the member {@link #list} as {@link PublishingTemplateInternal}
    * implementations. Once the {@link PublishingTemplateInternal} implementations are loaded they are indexed on to the
    * members {@link #matchCriteriaMap} and {@link #keyMap}. This method is synchronized to prevent more that one thread
    * from loading the Publishing Templates.
    */

   synchronized private void cacheTemplates() {

      if (this.isCacheLoaded()) {
         return;
      }

      this.loadTemplates();

      @SuppressWarnings("unchecked")
      var keyMap = new RankHashMap<List<PublishingTemplateInternal>>("KeyMap", 2, 256, 0.75f,
         new Predicate[] {(o) -> o instanceof PublishingTemplateCacheKey, (o) -> o instanceof String});
      this.keyMap = keyMap;

      this.list.stream().forEach(this::addToKeyMap);

      this.sortMatchCritera();

      this.logDuplicateMatchCritera();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {
      this.list = null;
      this.keyMap = null;
      //      this.matchCriteriaMap = null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> findFirstTemplate(PublishingTemplateCacheKey key, String identifier) {

      this.loadCacheIfNeeded();

      if (Objects.isNull(key) || Objects.isNull(identifier)) {
         return Optional.empty();
      }

      //@formatter:off
      var firstTemplate =
         this.keyMap.get(key, identifier)
            .map( (templateList) -> templateList.size() > 0 ? templateList.get( 0 ) : null );
      //@formatter:on

      return firstTemplate;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> findFirstTemplateByMatchCriteria(List<String> matchCriteria) {

      this.loadCacheIfNeeded();

      //@formatter:off
      var firstTemplate =
         matchCriteria.stream()
            .map( ( matchCriterion ) -> this.keyMap.get( PublishingTemplateCacheKey.MATCH_CRITERIA, matchCriterion ) )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .map( ( publishingTemplateInternalList ) -> publishingTemplateInternalList.isEmpty() ? null : publishingTemplateInternalList.get(0) )
            ;
      //@formatter:on

      return firstTemplate;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public List<String> getPublishingTemplateSafeNames() {

      this.loadCacheIfNeeded();

      //@formatter:off
      var publishingTemplateSafeNames =
         this.list.stream()
            .map( PublishingTemplateInternal::getSafeName )
            .collect( Collectors.toUnmodifiableList() );
      //@formatter:on

      return publishingTemplateSafeNames;
   }

   /**
    * Predicate to determine if the cache has been loaded.
    *
    * @return <code>true</code> when the cache is loaded; otherwise, <code>false</code>.
    */

   protected boolean isCacheLoaded() {
      return Objects.nonNull(this.list);
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
    * Loads the templates to be held by the {@link AbstractPublishingTemplateCache} implementation and puts them on a
    * {@link List} saved with the member {@link #list}.
    */

   abstract protected void loadTemplates();

}

/* EOF */
