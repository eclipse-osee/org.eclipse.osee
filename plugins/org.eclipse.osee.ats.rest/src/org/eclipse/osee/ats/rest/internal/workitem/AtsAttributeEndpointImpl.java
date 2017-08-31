/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.attr.AtsAttributeEndpointApi;
import org.eclipse.osee.ats.api.workflow.attr.AtsAttributes;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumEntry;

/**
 * @author Donald G. Dunne
 */
@Path("attr")
public final class AtsAttributeEndpointImpl implements AtsAttributeEndpointApi {

   private final IAtsServices services;
   private final OrcsApi orcsApi;

   @Context
   private HttpHeaders httpHeaders;

   public AtsAttributeEndpointImpl(IAtsServices services, OrcsApi orcsApi) {
      this.services = services;
      this.orcsApi = orcsApi;
   }

   @Override
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsAttributes get() {
      AtsAttributes attrs = new AtsAttributes();
      for (AttributeKey key : AttributeKey.values()) {
         attrs.add(key.name(), key.name(),
            key.getUrl().equals("N/A") ? key.getUrl() : System.getProperty("OseeApplicationServer") + key.getUrl());
      }
      AttributeTypes attrTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      for (AttributeTypeToken attrType : attrTypes.getAll()) {
         if (attrTypes.isEnumerated(attrType)) {
            attrs.add(attrType.getIdString(), attrType.getName(),
               System.getProperty("OseeApplicationServer") + "/ats/attr/" + attrType.getIdString() + "/");
         }
      }
      return attrs;
   }

   @Path("{id}")
   @Override
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getValidValues(@PathParam("id") String id) {
      List<String> values = new LinkedList<>();
      if (id.equals(AttributeKey.Assignee.name()) || id.equals(AttributeKey.Originator.name())) {
         List<IAtsUser> active = services.getUserService().getUsers(Active.Active);
         for (IAtsUser user : active) {
            values.add(user.getName());
         }
      } else if (id.equals(AttributeKey.ColorTeam.name())) {
         getEnumValues(values, AtsAttributeTypes.ColorTeam.getId());
      } else if (id.equals(AttributeKey.IPT.name())) {
         getEnumValues(values, AtsAttributeTypes.IPT.getId());
      } else if (id.equals(AttributeKey.Priority.name())) {
         getEnumValues(values, AtsAttributeTypes.PriorityType.getId());
      } else if (Strings.isNumeric(id)) {
         getEnumValues(values, Long.valueOf(id));
      }
      if (!values.isEmpty()) {
         Collections.sort(values);
      }
      return values;
   }

   private void getEnumValues(List<String> values, Long id) {
      AttributeTypes attrTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      AttributeTypeToken attrType = attrTypes.get(id);
      if (attrTypes.isEnumerated(attrType)) {
         for (EnumEntry entry : attrTypes.getEnumType(attrType).values()) {
            values.add(entry.getName());
         }
      }
   }

}
