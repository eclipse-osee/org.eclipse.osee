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

package org.eclipse.osee.define.operations;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.ImportOperations;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.api.publishing.PublishingOperations;
import org.eclipse.osee.define.api.publishing.datarights.DataRightsOperations;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.api.synchronization.SynchronizationOperations;
import org.eclipse.osee.define.operations.publishing.PublishingOperationsImpl;
import org.eclipse.osee.define.operations.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publishing.datarights.DataRightsOperationsImpl;
import org.eclipse.osee.define.operations.publishing.templatemanager.TemplateManagerOperationsImpl;
import org.eclipse.osee.define.operations.synchronization.SynchronizationOperationsImpl;
import org.eclipse.osee.define.rest.GitOperationsImpl;
import org.eclipse.osee.define.rest.ImportOperationsImpl;
import org.eclipse.osee.define.rest.TraceabilityOperationsImpl;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author Angel Avila
 * @author David W. Miller
 */
public class DefineOperationsImpl implements DefineOperations {

   private ActivityLog activityLog;
   private AtsApi atsApi;
   private DataRightsOperations dataRightsOperations;
   private EventAdmin eventAdmin;
   private GitOperations gitOperations;
   private ImportOperations importOperations;
   private Log logger;
   private OrcsApi orcsApi;
   private PublishingOperations publishingOperations;
   private SynchronizationOperations synchronizationOperations;
   private TemplateManagerOperations templateManagerOperations;
   private TraceabilityOperations traceabilityOperations;

   public void setActivityLog(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void start() {

      PublishingPermissions.create(this.orcsApi);

      //@formatter:off
      this.dataRightsOperations      = DataRightsOperationsImpl.create(this.orcsApi);
      this.gitOperations             = new GitOperationsImpl(this.orcsApi, this.orcsApi.getSystemProperties());
      this.traceabilityOperations    = new TraceabilityOperationsImpl(this.orcsApi, this.gitOperations);
      this.importOperations          = new ImportOperationsImpl(this.orcsApi, this.activityLog);
      this.publishingOperations      = PublishingOperationsImpl.create(this,this.orcsApi, this.atsApi, this.logger, this.eventAdmin);
      this.synchronizationOperations = SynchronizationOperationsImpl.create(this.orcsApi);
      this.templateManagerOperations = TemplateManagerOperationsImpl.create(this.orcsApi.getJdbcService(), this.logger, this.orcsApi);
      //@formatter:on
   }

   @Override
   public ActivityLog getActivityLog() {
      return this.activityLog;
   }

   @Override
   public DataRightsOperations getDataRightsOperations() {
      return this.dataRightsOperations;
   }

   @Override
   public ImportOperations getImportOperations() {
      return this.importOperations;
   }

   @Override
   public PublishingOperations getPublishingOperations() {
      return this.publishingOperations;
   }

   @Override
   public SynchronizationOperations getSynchronizationOperations() {
      return this.synchronizationOperations;
   }

   @Override
   public TemplateManagerOperations getTemplateManagerOperations() {
      return this.templateManagerOperations;
   }

   @Override
   public TraceabilityOperations getTraceabilityOperations() {
      return this.traceabilityOperations;
   }

   @Override
   public GitOperations gitOperations() {
      return this.gitOperations;
   }

}