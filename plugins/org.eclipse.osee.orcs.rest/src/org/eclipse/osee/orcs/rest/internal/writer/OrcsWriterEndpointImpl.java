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
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.writer.OrcsWriterResponse;
import org.eclipse.osee.orcs.rest.model.writer.config.OrcsWriterInputConfig;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwCollector;

/**
 * @author Donald G. Dunne
 */
public class OrcsWriterEndpointImpl implements OrcsWriterEndpoint {

   private static final String EXCEPTION_READING_INPUT_S = "Exception reading input\n\n%s";
   private static final String ERROR_READING_INPUT_S = "Exception reading input\n\n%s";
   private static final String ERROR_VALIDATING_INPUT_S = "Error validating input\n\n%s";
   private static final String EXCEPTION_VALIDATING_INPUT_S = "Exception validating input\n\n%s";
   private static final String EXCEPTION_WRITING_CHANGES_S = "Exception writing changes\n\n%s";
   private static final String ERROR_WRITING_CHANGES_S = "Error writing changes:\n\n%s";
   private final OrcsApi orcsApi;

   public OrcsWriterEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public Response getOrcsWriterInputDefault() {
      OrcsWriterCollectorGenerator generator = new OrcsWriterCollectorGenerator();
      OwCollector collector = generator.run(orcsApi);
      StreamingOutput streamingOutput = new OrcsWriterStreamingOutput(orcsApi, collector);
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
      StreamingOutput streamingOutput = new OrcsWriterStreamingOutput(orcsApi, collector);
      ResponseBuilder builder = Response.ok(streamingOutput);
      builder.header("Content-Disposition", "attachment; filename=" + "OrcsWriterInput.xml");
      return builder.build();
   }

   @Override
   public Response getOrcsWriterValidate(OwCollector collector) {
      // Setup
      OrcsWriterResponse response = new OrcsWriterResponse();
      response.setTitle("JSON Validator");
      XResultData results = null;

      // Validate
      try {
         OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
         results = validator.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_VALIDATING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_VALIDATING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Return Success
      response.setMessage("Success");
      return Response.ok(response).build();
   }

   @Override
   public Response getOrcsWriterPersist(OwCollector collector) {
      // Setup
      OrcsWriterResponse response = new OrcsWriterResponse();
      response.setTitle("JSON Executor");
      XResultData results = null;

      // Validate
      try {
         OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
         results = validator.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_VALIDATING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_VALIDATING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Write
      try {
         OrcsCollectorWriter writer = new OrcsCollectorWriter(orcsApi, collector, results);
         writer.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_WRITING_CHANGES_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_WRITING_CHANGES_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Return Success
      response.setMessage("Success");
      return Response.ok().entity(response).build();
   }

   @Override
   public Response validateExcelInput(Attachment attachment) {
      // Setup
      InputStream stream = attachment.getObject(InputStream.class);
      OrcsWriterResponse response = new OrcsWriterResponse();
      response.setTitle("Excel Validation");
      XResultData results = new XResultData();

      // Read Input
      OrcsWriterExcelReader reader = new OrcsWriterExcelReader(results);
      try {
         reader.run(stream);
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_READING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_READING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }
      OwCollector collector = reader.getCollector();

      // Validate
      try {
         OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
         results = validator.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_VALIDATING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_VALIDATING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Return Success
      response.setMessage("Success");
      return Response.ok().entity(response).build();
   }

   @Override
   public Response persistExcelInput(Attachment attachment) {
      // Setup
      InputStream stream = attachment.getObject(InputStream.class);
      XResultData results = new XResultData();
      OrcsWriterResponse response = new OrcsWriterResponse();
      response.setTitle("Excel Executor");

      // Read Input
      OrcsWriterExcelReader reader = new OrcsWriterExcelReader(results);
      try {
         reader.run(stream);
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_READING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_READING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }
      OwCollector collector = reader.getCollector();

      // Validate
      try {
         OrcsCollectorValidator validator = new OrcsCollectorValidator(orcsApi, collector);
         results = validator.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_VALIDATING_INPUT_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_VALIDATING_INPUT_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Write
      try {
         OrcsCollectorWriter writer = new OrcsCollectorWriter(orcsApi, collector, results);
         writer.run();
      } catch (Exception ex) {
         response.setMessage(String.format(EXCEPTION_WRITING_CHANGES_S, Lib.exceptionToString(ex)));
         return Response.serverError().entity(response).build();
      }
      if (results.isErrors()) {
         response.setMessage(String.format(ERROR_WRITING_CHANGES_S, results.toString()));
         return Response.serverError().entity(response).build();
      }

      // Return Success
      results.log("Success");
      response.setMessage(results.toString());
      return Response.ok().entity(response).build();
   }

}
