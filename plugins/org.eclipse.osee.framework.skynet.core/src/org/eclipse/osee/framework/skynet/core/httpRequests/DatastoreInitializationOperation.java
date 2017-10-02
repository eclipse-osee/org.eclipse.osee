/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.httpRequests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreInitOptions;

/**
 * @author Roberto E. Escobar
 */
public final class DatastoreInitializationOperation extends AbstractOperation {

   private final String tableDataSpace;
   private final String indexDataSpace;
   private final boolean useSchemasSpecified;

   public DatastoreInitializationOperation(String tableDataSpace, String indexDataSpace, boolean useSchemasSpecified) {
      super("Datastore Initialization", Activator.PLUGIN_ID);
      this.tableDataSpace = tableDataSpace;
      this.indexDataSpace = indexDataSpace;
      this.useSchemasSpecified = useSchemasSpecified;
   }

   @Override
   protected void doWork(IProgressMonitor monitor)  {
      OseeClient oseeClient = ServiceUtil.getOseeClient();
      DatastoreEndpoint endPoint = oseeClient.getDatastoreEndpoint();

      DatastoreInitOptions options = new DatastoreInitOptions();
      options.setIndexDataSpace(indexDataSpace);
      options.setTableDataSpace(tableDataSpace);
      options.setUseFileSpecifiedSchemas(useSchemasSpecified);

      try {
         endPoint.initialize(options);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
