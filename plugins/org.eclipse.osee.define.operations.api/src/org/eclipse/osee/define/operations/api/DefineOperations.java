/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.define.operations.api;

import org.eclipse.osee.define.operations.api.git.GitOperations;
import org.eclipse.osee.define.operations.api.importing.ImportOperations;
import org.eclipse.osee.define.operations.api.publisher.PublisherOperations;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.operations.api.reports.ReportsOperations;
import org.eclipse.osee.define.operations.api.synchronization.SynchronizationOperations;
import org.eclipse.osee.define.operations.api.toggles.TogglesOperations;
import org.eclipse.osee.define.operations.api.traceability.TraceabilityOperations;

/**
 * @author Angel Avila
 * @author David W. Miller
 * @author Loren K. Ashley
 */

public interface DefineOperations {

   ImportOperations getImportOperations();

   /**
    * Gets an implementation of the {@link PublisherOperations} interface. The {@link PublisherOperations} interface is
    * used to access the following:
    * <ul>
    * <li>{@link DataRightsOperations} interface implementation,</li>
    * <li>{@link PublishingOperations} interface implementation, and</li>
    * <li>{@link TemplateManagerOperations} interface implementation.</li>
    * </ul>
    *
    * @return an implementation of the {@link PublisherOperations} interface.
    */

   PublisherOperations getPublisherOperations();

   ReportsOperations getReportsOperations();

   SynchronizationOperations getSynchronizationOperations();

   TogglesOperations getTogglesOperations();

   TraceabilityOperations getTraceabilityOperations();

   GitOperations gitOperations();

}

/* EOF */