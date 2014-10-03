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
import java.util.Properties;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
@Path("script")
public class OrcsScriptResource {

   private final ScriptEngine engine;

   public OrcsScriptResource(ScriptEngine engine) {
      this.engine = engine;
   }

   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   public Response processScript(final @DefaultValue("") @FormParam("script") String script, // 
      final @DefaultValue("") @FormParam("parameters") String parameters, //
      final @DefaultValue("false") @FormParam("debug") boolean debug) {
      return Response.ok(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            Properties properties = parseParamaters(parameters);
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
               engine.eval(script, context);
            } catch (ScriptException ex) {
               throw new WebApplicationException(ex);
            }
         }

      }).build();
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
