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

import org.eclipse.osee.define.api.DataRightsOperations;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.MSWordOperations;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * @author Angel Avila
 */
public class DefineApiImpl implements DefineApi {

   private OrcsApi orcsApi;
   private Log logger;
   private EventAdmin eventAdmin;

   private MSWordOperations wordOperations;
   private DataRightsOperations dataRightsOperations;
   private TraceabilityOperations traceabilityOperations;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void start() {
      wordOperations = new MSWordOperationsImpl(orcsApi, logger, eventAdmin);
      dataRightsOperations = new DataRightsOperationsImpl(orcsApi);
      traceabilityOperations = new TraceabilityOperationsImpl(orcsApi);
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
}
