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

package org.eclipse.osee.define.api;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.publishing.PublishingOperations;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.api.synchronization.SynchronizationOperations;

/**
 * @author Angel Avila
 * @author David W. Miller
 */
public interface DefineOperations {

   ActivityLog getActivityLog();

   DataRightsOperations getDataRightsOperations();

   ImportOperations getImportOperations();

   PublishingOperations getPublishingOperations();

   SynchronizationOperations getSynchronizationOperations();

   TemplateManagerOperations getTemplateManagerOperations();

   TraceabilityOperations getTraceabilityOperations();

   GitOperations gitOperations();

}

/* EOF */