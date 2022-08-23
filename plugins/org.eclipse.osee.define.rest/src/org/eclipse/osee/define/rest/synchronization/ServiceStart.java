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

package org.eclipse.osee.define.rest.synchronization;

import java.io.InputStream;
import java.util.Objects;
import org.eclipse.osee.define.api.synchronization.ExportRequest;
import org.eclipse.osee.define.api.synchronization.ImportRequest;
import org.eclipse.osee.define.api.synchronization.SynchronizationEndpoint;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * This class is an OSGI service component. The OSGI service component performs the OSGI service activation which
 * creates a single instance of the {@link SynchronizationEndpoint} used to process the HTTP requests for the
 * "synchronization" end point.
 *
 * @author Loren K. Ashley
 */

public class ServiceStart implements SynchronizationEndpoint {

   /**
    * Saves a reference to the Logger service for the bundle.
    */

   private Log logger;

   /**
    * Saves a reference to the Object Revisions Control System service for the bundle.
    */

   private OrcsApi orcsApi;

   /**
    * Saves an implementation of the {@link SynchronizationEndpoint} interface used to process the HTTP requests for the
    * Synchronization end point.
    */

   private SynchronizationEndpoint synchronizationEndpoint;

   /**
    * Creates a new combination OSGI service component object and {@link javax.ws.rs.core.Application} object.
    */

   public ServiceStart() {
      this.synchronizationEndpoint = null;
      this.orcsApi = null;
      this.logger = null;
   }

   /**
    * OSGI service binding method, this method is called before activation with the Object Revision Control System
    * service object.
    *
    * @param orcsApi the {@link OrcsApi} service object.
    */

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * OSGI service binding method, this method is called before activation with the logger service object.
    *
    * @param logger the {@link Log} service object.
    */

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   /**
    * The OSGI Component <code>activate</code> method, this method is called as the last step in the component
    * activation. This method creates an instance of the {@link SynchronizationEndpointImpl} that will be called to
    * process the REST API calls for the Synchronization end point.
    */

   public void start() {

      assert Objects.nonNull(this.orcsApi) && Objects.nonNull(this.logger);

      this.synchronizationEndpoint = SynchronizationEndpointImpl.create(this.orcsApi);

      logger.warn("Synchronization Artifact Service Started - %s", System.getProperty("OseeApplicationServer"));
   }

   /*
    * SynchronizationEndponit Interface Methods
    */

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream export(ExportRequest exportRequest) {
      return this.synchronizationEndpoint.export(exportRequest);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void importSynchronizationArtifact(ImportRequest importRequest, InputStream inputStream) {
      this.synchronizationEndpoint.importSynchronizationArtifact(importRequest, inputStream);
   }

}

/* EOF */
