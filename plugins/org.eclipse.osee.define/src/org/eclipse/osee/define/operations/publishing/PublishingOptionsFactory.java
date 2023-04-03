/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.publishing;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.osee.define.api.publishing.PublishingOptions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * A factory class for creating new {@link PublishingOption} objects with default values selected according to a key.
 *
 * @author Loren K. Ashley
 * @param <K> The enumeration class used to key the map of {@link PublishingOptions}.
 */

public class PublishingOptionsFactory<K extends Enum<K>> {

   /**
    * Save the map of default {@link PublishingOptions}.
    */

   private final Map<K, PublishingOptions> map;

   /**
    * Creates a new {@link PublishingOptionsFactory} with the provided map of default options.
    *
    * @param publishingOptionsMap the map of default {@link PublishingOptions}.
    */

   private PublishingOptionsFactory(Map<K, PublishingOptions> publishingOptionsMap) {
      this.map = publishingOptionsMap;
   }

   /**
    * Looks of the default {@link PublishingOptions} according to the <code>key</code> and creates a new
    * {@link PublishingOptions} object with the same values. If a set of defaults is not found with the provided
    * <code>key</code> a new unset {@link PublishingOptions} object is returned.
    *
    * @param key the key used to look up the {@link PublishingOptions} defaults.
    * @return a new {@link PublishingOptions} object.
    */

   private PublishingOptions create(K key) {
      //@formatter:off
      var parentPublishingOptions = this.map.get( key );

      var publishingOptions       = Objects.nonNull( parentPublishingOptions )
                                       ? (PublishingOptions) parentPublishingOptions.clone()
                                       : new PublishingOptions();
      return publishingOptions;
      //@formatter:on
   }

   /**
    * Creates a new {@link PublishingOptions} object with the defaults specified by <code>key</code> and the specified
    * <code>branchId</code> as follows:
    * <ul>
    * <li>When the parameter <code>branchId</code> is <code>null</code>, the default branch and view values will not be
    * overridden.</li>
    * <li>When the parameter <code>branchId</code> is {@link BranchId#SENTINEL}, the branch and view default values will
    * be overridden with {@link BranchId#SENTINEL} and {@link ArtifactId#SENTINEL}.</li>
    * <li>When the parameter <code>branchId</code> is valid, the default branch will be overridden.</li>
    * <li>When the parameter <code>branchId</code> is valid and contains a <code>null</code> view {@link ArtifactId};
    * the view {@link ArtifactId} will not be overridden.</li>
    * <li>When the parameter <code>branchId</code> is valid and contains a {@link ArtifactId#SENTINEL} view
    * {@link ArtifactId}; the view {@link ArtifactId} will be overridden with {@link ArtifactId#SENTINEL}.</li>
    * <li>When the parameter <code>branchId</code> is valid and contains a valid view {@link ArtifactId}, the view
    * {@link ArtifactId} will be overridden.</li>
    * </ul>
    *
    * @param key the key used to look up the {@link PublishingOptions} defaults.
    * @param branchId the {@link BranchId} to be set in the new {@link PublishingOptions} object.
    * @return a new {@link PublishingOptions} object.
    */

   public PublishingOptions create(K key, BranchId branchId) {

      var publishingOptions = this.create(key);

      if (Objects.isNull(branchId)) {
         return publishingOptions;
      }

      if (branchId.isInvalid()) {
         publishingOptions.branch = BranchId.SENTINEL;
         publishingOptions.view = ArtifactId.SENTINEL;
         return publishingOptions;
      }

      publishingOptions.branch = branchId;

      var viewArtifactId = branchId.getViewId();

      if (Objects.isNull(viewArtifactId)) {
         return publishingOptions;
      }

      if (viewArtifactId.isInvalid()) {
         publishingOptions.view = ArtifactId.SENTINEL;
      }

      publishingOptions.view = viewArtifactId;

      return publishingOptions;
      //@formatter:on
   }

   /**
    * Creates a new {@link PublishingOptions} object with the defaults specified by <code>key</code> and the specified
    * <code>branchId</code> and <code>viewArtifactId</code> as follows:
    * <ul>
    * <li>When the parameter <code>branchId</code> is <code>null</code>, the default branch and view values will not be
    * overridden.</li>
    * <li>When the parameter <code>branchId</code> is {@link BranchId#SENTINEL}, the branch and view default values will
    * be overridden with {@link BranchId#SENTINEL} and {@link ArtifactId#SENTINEL}.</li>
    * <li>When the parameter <code>branchId</code> is valid, the default branch will be overridden.</li>
    * <li>When the parameter <code>branchId</code> is valid and the parameter <code>viewArtifactId</code> is
    * <code>null</code>; the view {@link ArtifactId} will not be overridden.</li>
    * <li>When the parameter <code>branchId</code> is valid and the parameter <code>viewArtifactId</code> is
    * {@link ArtifactId#SENTINEL}; the view {@link ArtifactId} will be overridden with {@link ArtifactId#SENTINEL}.</li>
    * <li>When the parameter <code>branchId</code> is valid and the parameter <code>viewArtifactId</code> is valid, the
    * view {@link ArtifactId} will be overridden.</li>
    *
    * @implNote An assertion check is performed when assertions are enable to verify that when a view artifact
    * identifier is contained in the <code>branchId</code>, it is the same as the view artifact identifier specified by
    * <code>viewArtifactId</code>.
    * @param key the key used to look up the {@link PublishingOptions} defaults.
    * @param branchId the {@link BranchId} to be set in the new {@link PublishingOptions} object.
    * @param viewArtifactId the {@link ArtifactId} to be set in the {@link PublishingOptions} object as the view.
    * @return a new {@link PublishingOptions} object.
    */

   public PublishingOptions create(K key, BranchId branchId, ArtifactId viewArtifactId) {

      //@formatter:off
      assert      Objects.nonNull( branchId             )
               && Objects.nonNull( branchId.getViewId() )
               && Objects.nonNull( viewArtifactId       )
               && !ArtifactId.SENTINEL.equals( branchId.getViewId() )
               && !ArtifactId.SENTINEL.equals( viewArtifactId       )
                  ? !branchId.getViewId().equals( viewArtifactId )
                  : true
             : new Message()
                      .title( "PublishingOptionsFactory::create, The view identifier in the branch identifier is not consistent with the specified view identifier." )
                      .indentInc()
                      .segment( "Branch Identifier",                    branchId.getIdString()             )
                      .segment( "View Identifier In Branch Identifier", branchId.getViewId().getIdString() )
                      .segment( "View Identifer From Parameter",        viewArtifactId.getIdString()       )
                      .toString();
      //@formatter:on

      var publishingOptions = this.create(key);

      if (Objects.isNull(branchId)) {
         return publishingOptions;
      }

      if (branchId.isInvalid()) {
         publishingOptions.branch = BranchId.SENTINEL;
         publishingOptions.view = ArtifactId.SENTINEL;
         return publishingOptions;
      }

      publishingOptions.branch = branchId;

      if (Objects.isNull(viewArtifactId)) {
         return publishingOptions;
      }

      if (viewArtifactId.isInvalid()) {
         publishingOptions.view = ArtifactId.SENTINEL;
      }

      publishingOptions.view = viewArtifactId;

      return publishingOptions;
      //@formatter:on
   }

   /**
    * Creates a new {@link PublishingOptionsFactory} initialized with the default {@link PublishingOptions} provided by
    * the <code>enumToPublishingOptionsFunction</code> for each member of the key enumeration.
    *
    * <pre>
    * private enum Members {
    *    MEMBER_A( new PublishingOptions(...) ),
    *    MEMBER_B( new PublishingOptions(...) );
    *
    *    private PublishingOptions publishingOptions;
    *
    *    Members( PubilshingOptions publishingOptions ) {
    *       this.publishingOptions = publishingOptions;
    *    }
    *
    *    PublishingOptions getPublishingOptions() {
    *       return this.publishingOptions;
    *    }
    * }
    *
    * private PublishingOptionsFactory<Members> publishingOptionsFactory =
    *    PublishingOptionsFactory.ofEntries( Members.class, Members::getPublishingOptions );
    * </pre>
    *
    * @param <K> The enumeration class used to key the map of {@link PublishingOptions}.
    * @param enumerationKeyClass the enumeration class of the key.
    * @param enumToPublishingOptionsFunction a {@link Function} implementation the returns a {@link PublishingOptions}
    * object with default values for the provided key.
    * @return a {@link PublishingOptionsFactory} initialized with the provided default {@link PublishingOptions}.
    */

   public static <K extends Enum<K>> PublishingOptionsFactory<K> ofEntries(Class<K> enumerationKeyClass, Function<K, PublishingOptions> enumToPublishingOptionsFunction) {

      var map = new EnumMap<K, PublishingOptions>(enumerationKeyClass);

      //@formatter:off
      EnumSet
         .allOf( enumerationKeyClass )
         .forEach( ( key ) -> map.put( key, enumToPublishingOptionsFunction.apply( key ) ) )
         ;
      //@formatter:on

      var publishingOptionsFactory = new PublishingOptionsFactory<K>(map);

      return publishingOptionsFactory;
   }

}

/* EOF */