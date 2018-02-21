/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.query;

import java.io.StringWriter;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.eclipse.osee.ats.api.query.IAtsOrcsScriptQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class AtsOrcsScriptQuery implements IAtsOrcsScriptQuery {
   private final OrcsApi orcsApi;
   private final String query;

   AtsOrcsScriptQuery(String query, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.query = query;
   }

   @Override
   public String getResults() {
      StringWriter writer = new StringWriter();
      ScriptContext context = new SimpleScriptContext();
      context.setWriter(writer);
      context.setErrorWriter(writer);
      context.setAttribute("output.debug", false, ScriptContext.ENGINE_SCOPE);
      try {
         orcsApi.getScriptEngine().eval(query, context);
      } catch (ScriptException ex) {
         throw new OseeCoreException(ex, "Failed to execute script [%s]", query);
      }
      String result = writer.toString();
      return result;
   }
}