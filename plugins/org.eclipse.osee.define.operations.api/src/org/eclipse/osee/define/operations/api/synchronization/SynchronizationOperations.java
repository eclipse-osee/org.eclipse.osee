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

package org.eclipse.osee.define.operations.api.synchronization;

import java.io.InputStream;
import org.eclipse.osee.define.rest.api.synchronization.ExportRequest;
import org.eclipse.osee.define.rest.api.synchronization.ImportRequest;

/**
 * This interface defines the methods for importing and exporting synchronization artifacts.
 *
 * @author Loren K. Ashley
 */

public interface SynchronizationOperations {

   /**
    * Exports a Synchronization Artifact as specified by the {@link ExportRequest} parameter.
    *
    * @param exportRequest an {@link ExportRequest} object containing the export parameters.
    * @return an {@link InputStream} containing the generated Synchronization Artifact.
    */

   InputStream exporter(ExportRequest exportRequest);

   /**
    * Imports a Synchronization Artifact as specified by the {@link ImportRequest} parameter.
    *
    * @param importRequest an {@link ImportRequest} object containing the import parameters.
    * @param inputStream an {@link InputStream} containing the Synchronization Artifact to be imported.
    */

   void importer(ImportRequest importRequest, InputStream inputStream);

}

/* EOF */
