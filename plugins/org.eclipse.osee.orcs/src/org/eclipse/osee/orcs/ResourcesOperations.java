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

package org.eclipse.osee.orcs;

import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Interface for the resources operations service.
 *
 * @author Loren K. Ashley
 */

public interface ResourcesOperations {

   /**
    * Gets the path to the server's data directory.
    *
    * @return the server's data directory path.
    */

   @NonNull
   String getPublishingDirectory();

   /**
    * Gets the URL for downloading a published document from the server's data directory path. The returned URL string
    * only needs to have the desired file name appended to it.
    *
    * @return when the server's root URL is available an {@link Optional} with the URL for downloading a published
    * document; otherwise, an empty {@link Optional}.
    */

   @NonNull
   Optional<String> getPublishingDownloadUrl();

}

/* EOF */
