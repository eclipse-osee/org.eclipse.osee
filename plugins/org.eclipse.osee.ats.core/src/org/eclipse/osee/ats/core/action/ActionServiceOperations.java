/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.action;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.StringWriter;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ActionServiceOperations {

   private final AtsApi atsApi;

   public ActionServiceOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public String getActionStateJson(Collection<IAtsWorkItem> workItems, JsonFactory jsonFactory) {
      try {
         JsonGenerator writer = null;
         StringWriter stringWriter = new StringWriter();
         writer = jsonFactory.createGenerator(stringWriter);
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
            writer.writeStringField("state", atsApi.getWorkItemService().getCurrentStateName(workItem));
            writer.writeEndObject();
         }
         if (workItems.size() > 1) {
            writer.writeEndArray();
         }
         writer.close();
         return stringWriter.toString();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}