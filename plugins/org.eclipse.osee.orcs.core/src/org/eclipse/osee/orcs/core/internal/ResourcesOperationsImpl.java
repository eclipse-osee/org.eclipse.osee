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

package org.eclipse.osee.orcs.core.internal;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.orcs.ResourcesOperations;

/**
 * Implementation of the Resources Operations service.
 *
 * @author Loren K. Ashley
 */

public class ResourcesOperationsImpl implements ResourcesOperations {

   /**
    * The URL sub-path for downloading publishing results.
    */

   private static final String publishingDownloadUrlSubPath = "/orcs/resources/publish/?path=";

   /**
    * The sub-directory in the server's data directory that contains publishing results.
    */

   private static final String publishingSubDirectory = "publish";

   /**
    * Saves the single instance of the {@link ResourcesOperationsImpl}.
    */

   private static volatile ResourcesOperationsImpl resourcesOperationsImpl = null;

   /**
    * Gets or creates the single instance of the {@link ResourcesOperationsImpl} class.
    *
    * @return the single instance of the {@link ResourcesOperationsImpl} class.
    */

   public synchronized static WeakReference<ResourcesOperations> create() {
      //@formatter:off
      return
         Objects.isNull( ResourcesOperationsImpl.resourcesOperationsImpl )
            ? new WeakReference<>( ResourcesOperationsImpl.resourcesOperationsImpl = new ResourcesOperationsImpl() )
            : new WeakReference<>( ResourcesOperationsImpl.resourcesOperationsImpl );
      //@formatter:on
   }

   /**
    * Sets the statically saved instance of the {@link ResourcesOperationsImpl} class to <code>null</code>.
    */

   public synchronized static void free() {
      ResourcesOperationsImpl.resourcesOperationsImpl = null;
   }

   /**
    * Saves the path to the server's data publishing directory.
    */

   private final @NonNull String publishingDirectory;

   /**
    * Saves an {@link Optional} with the download URL for publishing files. When the server's root URL cannot be
    * determined the {@link Optional} will be empty.
    */

   private final @NonNull Optional<String> publishingDownloadUrl;

   /**
    * Creates the sole instance of the {@link ResourcesOperationsImpl} class. The constructor determines the publishing
    * directory path and the publishing file download URL.
    */

   private ResourcesOperationsImpl() {

      //@formatter:off
      this.publishingDirectory =
         OseeServerProperties
            .getOseeApplicationServerData()
            .map( ( dataPath ) -> dataPath + File.separator + ResourcesOperationsImpl.publishingSubDirectory )
            .orElse( ResourcesOperationsImpl.publishingSubDirectory );

      this.publishingDownloadUrl =
         OseeServerProperties
            .getOseeApplicationServer()
            .map( ( urlRoot ) -> urlRoot + ResourcesOperationsImpl.publishingDownloadUrlSubPath );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getPublishingDirectory() {
      return this.publishingDirectory;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull Optional<String> getPublishingDownloadUrl() {
      return this.publishingDownloadUrl;
   }

}

/* EOF */
