/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.io.StringWriter;
import java.util.Collection;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;

/**
 * @author Donald G. Dunne
 */
public class ActionFactoryOperations {

   private final AtsApi atsApi;

   public ActionFactoryOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public String getActionStateJson(Collection<IAtsWorkItem> workItems, JsonFactory jsonFactory) {
      try {
         JsonGenerator writer = null;
         StringWriter stringWriter = new StringWriter();
         writer = jsonFactory.createJsonGenerator(stringWriter);
         if (workItems.size() > 1) {
            writer.writeStartArray();
         }
         for (IAtsWorkItem workItem : workItems) {
            writer.writeStartObject();
            writer.writeStringField("id", workItem.getIdString());
            writer.writeStringField("atsId", workItem.getAtsId());
            writer.writeStringField("legacyId",
               atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.LegacyPcrId, ""));
            writer.writeStringField("stateType", workItem.getStateMgr().getStateType().name());
            writer.writeStringField("state", workItem.getStateMgr().getCurrentStateName());
            writer.writeEndObject();
         }
         if (workItems.size() > 1) {
            writer.writeEndArray();
         }
         writer.close();
         return stringWriter.toString();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
