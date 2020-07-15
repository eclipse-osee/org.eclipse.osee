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

/**
 * @author Angel Avila
 * @author David W. Miller
 */
public interface DefineApi {

   //Traceability
   TraceabilityOperations getTraceabilityOperations();

   //Rendering
   RenderOperations renderOperations();

   //MS Word
   MSWordOperations getMSWordOperations();

   //Data Rights
   DataRightsOperations getDataRightsOperations();

   //Import
   ImportOperations getImportOperations();

   //Report
   ReportOperations getReportOperations();

   ActivityLog getActivityLog();

   GitOperations gitOperations();

}