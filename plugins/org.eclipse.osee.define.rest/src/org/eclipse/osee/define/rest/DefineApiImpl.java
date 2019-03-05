/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.DataRightsOperations;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.ImportOperations;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.SystemPreferences;
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
   private MSWordOperations wordOperations;
   private DataRightsOperations dataRightsOperations;
   private TraceabilityOperations traceabilityOperations;
   private ImportOperations importOperations;
   private GitOperations gitOperations;
   private SystemPreferences systemPrefs;

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

   public void setSystemPreferences(SystemPreferences systemPrefs) {
      this.systemPrefs = systemPrefs;
   }

   public void start() {
      wordOperations = new MSWordOperationsImpl(orcsApi, logger, eventAdmin);
      dataRightsOperations = new DataRightsOperationsImpl(orcsApi);
      gitOperations = new GitOperationsImpl(orcsApi, systemPrefs);
      traceabilityOperations = new TraceabilityOperationsImpl(orcsApi);
      importOperations = new ImportOperationsImpl(orcsApi, activityLog);
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
}