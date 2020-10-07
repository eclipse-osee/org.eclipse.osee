/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.structure.resources;

import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.enums.token.PriorityAttributeType.PriorityEnum;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * GeneralArtifact Resource to return enumeratyion values of attributes (task type and priorities)
 * 
 * @author Ajay Chandrahasan
 */

@Path("GeneralArtifact")
public class GeneralArtifactResource extends AbstractConfigResource {

   public GeneralArtifactResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.TeamWorkflow, atsApi, orcsApi);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("test")
   public String testSampleURLiCTeam() {

      return "inside test";

   }

   /**
    * This function gets enumeration values for a given attribute name
    * 
    * @param attrName String name of the attribute
    * @return serialize String enumeration values of the attribute
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("enumeration/values/test")
   public String getEnumerationValuesTest(final String attrName) {
      Set<String> valuesAsOrderedStringSet = new HashSet<>();
      try {
         if (AtsAttributeTypes.AgileChangeType.getId() == Long.parseLong(attrName)) {
            Collection<org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType.ChangeTypeEnum> enumValues =
               AtsAttributeTypes.AgileChangeType.getEnumValues();
            for (org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType.ChangeTypeEnum changeTypeEnum : enumValues) {
               valuesAsOrderedStringSet.add(changeTypeEnum.getName());
            }
         } else if (AtsAttributeTypes.Priority.getId() == Long.parseLong(attrName)) {
            Collection<PriorityEnum> enumValues = AtsAttributeTypes.Priority.getEnumValues();
            for (PriorityEnum changeTypeEnum : enumValues) {
               valuesAsOrderedStringSet.add(changeTypeEnum.getName());
            }
         }
         List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
         for (String string : valuesAsOrderedStringSet) {
            TransferableArtifact art = new TransferableArtifact();
            art.setName(string);
            list.add(art);
         }
         TransferableArtifactsContainer con = new TransferableArtifactsContainer();
         con.addAll(list);
         JSONSerializer serializer = new JSONSerializer();
         String serialize = serializer.deepSerialize(con);
         return serialize;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }
      return "";
   }
}
