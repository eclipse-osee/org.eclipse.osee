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
package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.InputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.writer.config.OrcsWriterInputConfig;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterEndpointImpl implements OrcsWriterEndpoint {

   private final OrcsApi orcsApi;

   public OrcsWriterEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public Response getOrcsWriterInputDefault() {
      OrcsWriterCollectorGenerator generator = new OrcsWriterCollectorGenerator();
      OwCollector collector = generator.run(orcsApi);
      StreamingOutput streamingOutput = new OrcsWriterStreamingOutput(orcsApi, 0L, collector);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "OrcsWriterInput.xml");
      return builder.build();
   }

   @Override
   public Response getOrcsWriterInputDefaultJson() throws Exception {
      OrcsWriterCollectorGenerator generator = new OrcsWriterCollectorGenerator();
      OwCollector collector = generator.run(orcsApi);
      return Response.ok(collector).build();
   }

   @Override
   public Response getOrcsWriterInputFromConfig(OrcsWriterInputConfig config) {
      OrcsWriterCollectorGenerator generator = new OrcsWriterCollectorGenerator(config);
      OwCollector collector = generator.run(orcsApi);
      StreamingOutput streamingOutput = new OrcsWriterStreamingOutput(orcsApi, 0L, collector);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "OrcsWriterInput.xml");
      return builder.build();
   }

   @Override
   public Response getOrcsWriterValidate(OwCollector collector) {
      try {
         OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
         XResultData results = validator.run();
         if (results.isErrors()) {
            return Response.serverError().entity(results.toString()).build();
         }
         return Response.ok(results.toString()).build();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public Response getOrcsWriterPersist(OwCollector collector) {
      OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
      XResultData results = validator.run();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      OrcsCollectorWriter writer = new OrcsCollectorWriter(orcsApi, collector, results);
      writer.run();
      if (results.isErrors()) {
         return Response.notModified().entity(results.toString()).build();
      }
      return Response.ok().entity(results.toString()).build();
   }

   @Override
   public Response validateExcelInput(Attachment attachment) {
      InputStream stream = attachment.getObject(InputStream.class);

      XResultData results = new XResultData();
      OrcsWriterExcelReader reader = new OrcsWriterExcelReader(results);
      try {
         reader.run(stream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      OwCollector collector = reader.getCollector();
      OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
      results = validator.run();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      return Response.ok().entity(results.toString()).build();
   }

   @Override
   public Response persistExcelInput(Attachment attachment) {
      InputStream stream = attachment.getObject(InputStream.class);

      XResultData results = new XResultData();
      OrcsWriterExcelReader reader = new OrcsWriterExcelReader(results);
      try {
         reader.run(stream);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      OwCollector collector = reader.getCollector();

      OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
      results = validator.run();
      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      OrcsCollectorWriter writer = new OrcsCollectorWriter(orcsApi, collector, results);
      writer.run();
      if (results.isErrors()) {
         return Response.notModified().entity(results.toString()).build();
      }
      return Response.ok().entity(results.toString()).build();
   }

}
