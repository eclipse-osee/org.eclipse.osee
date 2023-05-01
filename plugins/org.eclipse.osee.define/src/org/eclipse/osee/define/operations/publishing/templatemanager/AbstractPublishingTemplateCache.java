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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateScalarKey;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateVectorKey;
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
    * The primary map used for caching. <code>null</code> is a sentinel value used to indicate the cache has not yet
    * been loaded or has been deleted.
    */

   protected RankMap<List<PublishingTemplateInternal>> keyMap;

   /**
    * Saves a list of the {@link PublishingTemplateInternal} objects provided by the extension of this class. The list
    * is saved so that a list of Publishing Template Key Groups can be returned with out duplicates. The
    * {@link PublishingTemplateInternal} object will be stored in the map under each of their primary and secondary key
    * sets. So streaming the map {@link #keyMap} values will result in many duplicates in the stream.
    */

   private List<PublishingTemplateInternal> list;

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
                        var publishingTemplateInternalList = new ArrayList<PublishingTemplateInternal>();
                        publishingTemplateInternalList.add(publishingTemplateInternal);
                        this.keyMap.associate(publishingTemplateInternalList, primaryKey, secondaryKey);
                     }
                  );
            //@formatter:on
         }
      }
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
    * Logs a message for each for Primary and Secondary key pair where more than one {@link PublishingTemplateInternal}
    * matches.
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

      this.list = this.loadTemplates();

      @SuppressWarnings("unchecked")
      //@formatter:off
      var keyMap =
         new RankHashMap<List<PublishingTemplateInternal>>
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
    * {@inheritDoc}
    */

   @Override
   public void deleteCache() {
      this.keyMap = null;
      this.list = null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> findFirstTemplate(PublishingTemplateKeyType key, String identifier) {

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
    * {@inheritDoc}
    */

   @Override
   public Optional<PublishingTemplateInternal> findFirstTemplateByMatchCriteria(List<String> matchCriteria) {

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
    * {@inheritDoc}
    */

   @Override
   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups() {

      this.loadCacheIfNeeded();

      //@formatter:off
      var publishingTemplateKeyGroupList =
         this.list.stream()
            .map( PublishingTemplateInternal::getPublishingTemplateKeyGroup )
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
    * Loads the templates to be held by the {@link AbstractPublishingTemplateCache} implementation and puts them on a
    * {@link List} saved with the member {@link #list}.
    */

   abstract protected List<PublishingTemplateInternal> loadTemplates();

}

/* EOF */
