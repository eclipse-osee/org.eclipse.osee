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
package org.eclipse.osee.orcs.rest.internal;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Properties;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.OrcsScriptEndpoint;

/**
 * @author Roberto E. Escobar
 */
@Path("script")
public class OrcsScriptEndpointImpl implements OrcsScriptEndpoint {

   private final ScriptEngine engine;

   public OrcsScriptEndpointImpl(ScriptEngine engine) {
      this.engine = engine;
   }

   @Override
   public Response getScriptResult(HttpHeaders httpHeaders, String script, String parameters, String filename, boolean debug) {
      return evaluateScript(httpHeaders, script, parameters, filename, debug);
   }

   @Override
   public Response postScript(HttpHeaders httpHeaders, String script, String parameters, String filename, boolean debug) {
      return evaluateScript(httpHeaders, script, parameters, filename, debug);
   }

   private Response evaluateScript(final @Context HttpHeaders httpHeaders, final String script, final String parameters, final String filename, final boolean debug) {
      final boolean isJsonOutput = isJsonMediaType(httpHeaders.getAcceptableMediaTypes());
      final Properties properties = parseParamaters(parameters);

      StreamingOutput output = new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            Writer writer = null;
            try {
               writer = new OutputStreamWriter(output);
               ScriptContext context = new SimpleScriptContext();
               context.setWriter(writer);
               context.setErrorWriter(writer);
               context.setAttribute("output.debug", debug, ScriptContext.ENGINE_SCOPE);
               for (String key : properties.stringPropertyNames()) {
                  context.setAttribute(key, properties.getProperty(key), ScriptContext.ENGINE_SCOPE);
               }
               if (!isJsonOutput) {
                  context.setAttribute("output.mime-type", "excel-xml", ScriptContext.ENGINE_SCOPE);
               }
               engine.eval(script, context);
            } catch (ScriptException ex) {
               throw new WebApplicationException(ex);
            }
         }

      };

      ResponseBuilder builder = Response.ok(output);
      if (isJsonOutput) {
         builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
      } else {
         builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
      }

      if (Strings.isValid(filename)) {
         StringBuilder fileBuilder = new StringBuilder("attachment; filename=");
         fileBuilder.append(Lib.removeExtension(filename));
         fileBuilder.append("_");
         fileBuilder.append(Lib.getDateTimeString());

         String extension = Lib.getExtension(filename);
         if (!Strings.isValid(extension)) {
            fileBuilder.append(".xml");
         } else {
            fileBuilder.append(extension);
         }
         builder.header("Content-Disposition", fileBuilder.toString());
      }
      return builder.build();
   }

   private boolean isJsonMediaType(List<MediaType> acceptableMediaTypes) {
      boolean result = true;
      for (MediaType type : acceptableMediaTypes) {
         if (MediaType.APPLICATION_XML_TYPE.equals(type)) {
            result = false;
            break;
         }
      }
      return result;
   }

   private Properties parseParamaters(String parameters) {
      Properties properties = new Properties();
      if (Strings.isValid(parameters)) {
         Reader reader = null;
         try {
            reader = new StringReader(parameters);
            properties.load(reader);
         } catch (IOException ex) {
            throw new WebApplicationException(ex, BAD_REQUEST);
         } finally {
            Lib.close(reader);
         }
      }
      return properties;
   }

}
