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
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author Roberto E. Escobar
 */
public class TypesEndpointImpl implements TypesEndpoint {

   private final OrcsApi orcsApi;

   public TypesEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   private OrcsTypes getOrcsTypes() {
      return orcsApi.getOrcsTypes();
   }

   @Override
   public Response getTypes() {
      return Response.ok().entity(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            Callable<Void> op = getOrcsTypes().writeTypes(output);
            executeCallable(op);
         }
      }).build();
   }

   @Override
   public Response setTypes(final InputStream inputStream) {
      IResource resource = asResource("http.osee.model", inputStream);
      Callable<Void> op = getOrcsTypes().loadTypes(resource);
      executeCallable(op);
      getOrcsTypes().invalidateAll();
      return Response.ok().build();
   }

   @Override
   public Response invalidateCaches() {
      getOrcsTypes().invalidateAll();
      return Response.ok().build();
   }

   private IResource asResource(final String fileName, final InputStream inputStream) {
      byte[] bytes;
      try {
         String types = Lib.inputStreamToString(inputStream);
         bytes = types.getBytes("UTF-8");
      } catch (IOException ex1) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "Error parsing data");
      }
      return new ByteResource(fileName, bytes);
   }

   private static final class ByteResource implements IResource {

      private final String filename;
      private final byte[] bytes;

      public ByteResource(String filename, byte[] bytes) {
         super();
         this.filename = filename;
         this.bytes = bytes;
      }

      @Override
      public InputStream getContent() throws OseeCoreException {
         return new ByteArrayInputStream(bytes);
      }

      @Override
      public URI getLocation() {
         String modelName = filename;
         if (!modelName.endsWith(".osee")) {
            modelName += ".osee";
         }
         try {
            return new URI("osee:/" + modelName);
         } catch (URISyntaxException ex) {
            throw new OseeCoreException(ex, "Error creating URI for [%s]", modelName);
         }
      }

      @Override
      public String getName() {
         return filename;
      }

      @Override
      public boolean isCompressed() {
         return false;
      }
   }
}
