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
package org.eclipse.osee.ats.search.internal.query;

import java.io.StringWriter;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.query.IAtsOrcsScriptQuery;
import org.eclipse.osee.ats.util.IAtsClient;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class AtsOrcsScriptQuery implements IAtsOrcsScriptQuery {

   AtsOrcsScriptQuery(String query, String data, IAtsClient atsClient) {
      super();
      this.data = data;
      this.atsClient = atsClient;
      this.query = query;
   }

   IAtsClient atsClient;
   String data, query;

   @Override
   public String getResults() {
      StringWriter writer = new StringWriter();
      OseeClient oseeClient = atsClient.getOseeClient();
      oseeClient.executeScript(String.format(query, data), new Properties(), false, MediaType.APPLICATION_JSON_TYPE,
         writer);
      return writer.toString();
   }

}
