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
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.eclipse.osee.ats.api.query.IAtsOrcsScriptQuery;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsOrcsScriptQuery implements IAtsOrcsScriptQuery {

   IAtsServer atsServer;
   String data, query;

   AtsOrcsScriptQuery(String query, IAtsServer atsServer) {
      super();
      this.atsServer = atsServer;
      this.query = query;
   }

   @Override
   public String getResults() {
      StringWriter writer = new StringWriter();
      ScriptEngine engine = atsServer.getOrcsApi().getScriptEngine();
      ScriptContext context = new SimpleScriptContext();
      context.setWriter(writer);
      context.setErrorWriter(writer);
      context.setAttribute("output.debug", false, ScriptContext.ENGINE_SCOPE);
      try {
         engine.eval(query, context);
      } catch (ScriptException ex) {
         throw new OseeCoreException(ex, "Failed to execute script [%s]", query);
      }
      String result = writer.toString();
      return result;
   }

}
