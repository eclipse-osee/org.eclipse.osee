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

import java.util.Objects;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.git.GitOperations;
import org.eclipse.osee.define.operations.api.importing.ImportOperations;
import org.eclipse.osee.define.operations.api.publisher.PublisherOperations;
import org.eclipse.osee.define.operations.api.synchronization.SynchronizationOperations;
import org.eclipse.osee.define.operations.api.toggles.TogglesOperations;
import org.eclipse.osee.define.operations.api.traceability.TraceabilityOperations;
import org.eclipse.osee.define.operations.publisher.PublisherOperationsImpl;
import org.eclipse.osee.define.operations.publisher.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.synchronization.SynchronizationOperationsImpl;
import org.eclipse.osee.define.operations.toggles.TogglesOperationsImpl;
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

   private AtsApi atsApi;
   private EventAdmin eventAdmin;
   private GitOperations gitOperations;
   private ImportOperations importOperations;
   private Log logger;
   private OrcsApi orcsApi;
   private PublisherOperations publisherOperations;
   private SynchronizationOperations synchronizationOperations;
   private TogglesOperations togglesOperations;
   private TraceabilityOperations traceabilityOperations;

   @Override
   public ImportOperations getImportOperations() {
      return this.importOperations;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public PublisherOperations getPublisherOperations() {
      return this.publisherOperations;
   }

   @Override
   public SynchronizationOperations getSynchronizationOperations() {
      return this.synchronizationOperations;
   }

   @Override
   public TogglesOperations getTogglesOperations() {
      return this.togglesOperations;
   }

   @Override
   public TraceabilityOperations getTraceabilityOperations() {
      return this.traceabilityOperations;
   }

   @Override
   public GitOperations gitOperations() {
      return this.gitOperations;
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

      Objects.requireNonNull(this.atsApi);
      Objects.requireNonNull(this.eventAdmin);
      Objects.requireNonNull(this.logger);
      Objects.requireNonNull(this.orcsApi);

      PublishingPermissions.create(this.orcsApi);

      var jdbcService = this.orcsApi.getJdbcService();
      var systemProperties = this.orcsApi.getSystemProperties();

      this.gitOperations = new GitOperationsImpl(this.orcsApi, systemProperties);
      this.importOperations = new ImportOperationsImpl(this.orcsApi);

      this.publisherOperations = PublisherOperationsImpl.create(orcsApi, atsApi, logger, eventAdmin);

      this.synchronizationOperations = SynchronizationOperationsImpl.create(this.orcsApi);
      this.togglesOperations = TogglesOperationsImpl.create(jdbcService);
      this.traceabilityOperations = new TraceabilityOperationsImpl(this.orcsApi, this.gitOperations);

      this.atsApi = null;
      this.eventAdmin = null;
      this.logger = null;
      this.orcsApi = null;
   }

   public void stop() {

      this.atsApi = null;

      this.eventAdmin = null;

      this.gitOperations = null;

      this.importOperations = null;

      this.logger = null;

      this.orcsApi = null;

      PublisherOperationsImpl.free();
      this.publisherOperations = null;

      this.synchronizationOperations = null;

      this.togglesOperations = null;

      this.traceabilityOperations = null;
   }

}