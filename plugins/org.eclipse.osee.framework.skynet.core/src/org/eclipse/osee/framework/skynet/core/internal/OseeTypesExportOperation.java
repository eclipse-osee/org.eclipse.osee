/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.internal;

import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesExportOperation extends AbstractOperation {

   private final TypesEndpoint typesEndpoint;
   private final OutputStream outputStream;

   public OseeTypesExportOperation(TypesEndpoint typesEndpoint, OutputStream outputStream) {
      super("Export Osee Types Model", Activator.PLUGIN_ID);
      this.typesEndpoint = typesEndpoint;
      this.outputStream = outputStream;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Conditions.checkNotNull(outputStream, "outputStream");
      try {
         Response response = typesEndpoint.getTypes();
         if (Status.OK.getStatusCode() == response.getStatus()) {
            InputStream inputStream = response.readEntity(InputStream.class);
            try {
               Lib.inputStreamToOutputStream(inputStream, outputStream);
            } finally {
               Lib.close(inputStream);
            }
         }
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }
}
