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

package org.eclipse.osee.define.rest;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.DataRightsOperations;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.ImportOperations;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.RenderOperations;
import org.eclipse.osee.define.api.ReportOperations;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author Angel Avila
 * @author David W. Miller
 */
public class DefineApiImpl implements DefineApi {

   private OrcsApi orcsApi;
   private Log logger;
   private ActivityLog activityLog;
   private EventAdmin eventAdmin;
   private RenderOperations renderOperations;
   private MSWordOperations wordOperations;
   private DataRightsOperations dataRightsOperations;
   private TraceabilityOperations traceabilityOperations;
   private ImportOperations importOperations;
   private ReportOperations reportOperations;
   private GitOperations gitOperations;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void start() {
      renderOperations = new RenderOperationsImpl(orcsApi, logger, eventAdmin);
      wordOperations = new MSWordOperationsImpl(orcsApi, logger, eventAdmin);
      dataRightsOperations = new DataRightsOperationsImpl(orcsApi);
      gitOperations = new GitOperationsImpl(orcsApi, orcsApi.getSystemProperties());
      traceabilityOperations = new TraceabilityOperationsImpl(orcsApi, gitOperations);
      importOperations = new ImportOperationsImpl(orcsApi, activityLog);
      reportOperations = new ReportOperationsImpl(orcsApi, activityLog);
   }

   @Override
   public RenderOperations renderOperations() {
      return renderOperations;
   }

   @Override
   public MSWordOperations getMSWordOperations() {
      return wordOperations;
   }

   @Override
   public DataRightsOperations getDataRightsOperations() {
      return dataRightsOperations;
   }

   @Override
   public TraceabilityOperations getTraceabilityOperations() {
      return traceabilityOperations;
   }

   @Override
   public ImportOperations getImportOperations() {
      return importOperations;
   }

   @Override
   public ActivityLog getActivityLog() {
      return activityLog;
   }

   @Override
   public GitOperations gitOperations() {
      return gitOperations;
   }

   @Override
   public ReportOperations getReportOperations() {
      return reportOperations;
   }
}