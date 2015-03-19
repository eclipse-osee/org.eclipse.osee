/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesImportOperation extends AbstractOperation {
   private final TypesEndpoint typesEndpoint;
   private final IOseeCachingService cacheService;
   private final URI model;
   private final boolean refreshCaches;

   public OseeTypesImportOperation(TypesEndpoint typesEndpoint, IOseeCachingService cacheService, URI model, boolean refreshCaches) {
      super("Import Osee Types Model", Activator.PLUGIN_ID);
      this.typesEndpoint = typesEndpoint;
      this.cacheService = cacheService;
      this.model = model;
      this.refreshCaches = refreshCaches;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = model.toURL().openStream();
         Response response = typesEndpoint.setTypes(inputStream);
         if (Status.OK.getStatusCode() == response.getStatus()) {
            if (refreshCaches) {
               cacheService.reloadTypes();
            }
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      } finally {
         Lib.close(inputStream);
      }
   }
}
