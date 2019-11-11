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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import flexjson.JSONSerializer;

/**
 * Component Resource to return Components and Component related queries
 * 
 * @author Ajay Chandrahasan
 */

@Path("Components")
public class ComponentsResource extends AbstractConfigResource{
	
	public ComponentsResource(AtsApi atsApi, OrcsApi orcsApi) {
	   super(AtsArtifactTypes.ActionableItem, atsApi, orcsApi);
	}
	
  /**
   * This function gets team users for component
   * 
   * @param attributes String guid of the component
   * @return serialize String associated team users of given component
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("teamsuser")
  public String getAssociatedTeamsUsersForComponent(final String attributes) {
    try {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      if (attributes != null) {
        ArtifactReadable compArtifact =
            CommonUtil.getArtifactFromIdExcludingDeleted(attributes, CommonUtil.getCommonBranch(orcsApi), orcsApi);
        // to fetch only group users if group present in create task
        TransferableArtifactsContainer container = new TransferableArtifactsContainer();
        List<ITransferableArtifact> listOfUsers = new ArrayList<ITransferableArtifact>();
        boolean isConfigured = false;
        ResultSet<ArtifactReadable> listTeamArtifact =
            compArtifact.getRelated(AtsRelationTypes.TeamActionableItem_TeamDefinition);
        if (!isConfigured) {
          // Get the Team Artifact
          TransferableArtifact teamArtifact = null;
          for (ArtifactReadable artifactReadable : listTeamArtifact) {
            ArtifactReadable exactlyOne = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
                .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
            teamArtifact = new TransferableArtifact();
            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, teamArtifact);
            ResultSet<ArtifactReadable> relatedArtifact = exactlyOne.getRelated(AtsRelationTypes.TeamLead_Lead);
            ResultSet<ArtifactReadable> relatedArtifact1 = exactlyOne.getRelated(AtsRelationTypes.TeamMember_Member);
            for (ArtifactReadable teamLead : relatedArtifact) {
              TransferableArtifact ar = new TransferableArtifact();
              TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(teamLead, ar);
              ResultSet<? extends AttributeReadable<Object>> attributes2 =
                  teamLead.getAttributes(CoreAttributeTypes.UserId);
              for (AttributeReadable<Object> attributeReadable : attributes2) {
                List<String> l = new ArrayList<String>();
                l.add(attributeReadable.getValue().toString());
                ar.putAttributes(CoreAttributeTypes.UserId.toString(), l);
              }
              listOfUsers.add(ar);
            }
            for (ArtifactReadable teamMembers : relatedArtifact1) {
              TransferableArtifact ar = new TransferableArtifact();
              TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(teamMembers, ar);
              ResultSet<? extends AttributeReadable<Object>> attributes2 =
                  teamMembers.getAttributes(CoreAttributeTypes.UserId);
              for (AttributeReadable<Object> attributeReadable : attributes2) {
                List<String> l = new ArrayList<String>();
                l.add(attributeReadable.getValue().toString());
                ar.putAttributes(CoreAttributeTypes.UserId.toString(), l);
              }
              listOfUsers.add(ar);
            }
          }
          if (teamArtifact != null) {
            List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
            list.add(teamArtifact);
            container.setArtifactList(list);
          }
        }
        Object[] st = listOfUsers.toArray();
        for (Object s : st) {
          if (listOfUsers.indexOf(s) != listOfUsers.lastIndexOf(s)) {
            listOfUsers.remove(listOfUsers.lastIndexOf(s));
          }
        }
        container.addAll(listOfUsers);
        JSONSerializer serializer = new JSONSerializer();
        String serialize = serializer.deepSerialize(container);
        return serialize;
      }
    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  
  /**
   * Creates a new Component with all the attribute values sent from Client
   *
   * @param json String attribute values of Component artifact
   * @return serialize String new artifact of Component created with all the attribute values
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("createTest")
  public String createComponentTest(final String json) {
    try {
      Gson gson = new GsonBuilder()
          .registerTypeAdapter(ITransferableArtifact.class, new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
      ArtifactId childArtifact = null;
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      TransactionFactory txFactory = orcsApi.getTransactionFactory();
      TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
          UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
          "Add New Component");
      String uuid = AtsRelationTypes.TeamActionableItem_TeamDefinition.getGuid().toString();
      String type1 = AtsRelationTypes.TeamActionableItem_TeamDefinition.getRelationType().toString();
      String side = AtsRelationTypes.TeamActionableItem_TeamDefinition.getSide().toString();
      String key = "RelationTypeSide - uuid=[" + uuid + "] type=[" + type1 + "] side=[" + side + "]";
      List<ITransferableArtifact> relatedArtifacts = artifact.getRelatedArtifacts(key);
      for (ITransferableArtifact transferableArtifact : relatedArtifacts) {
        ResultSet<ArtifactReadable> result = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
            .andUuid(Long.valueOf(transferableArtifact.getUuid())).getResults();
        ArtifactReadable team = null;
        if ((result != null) && (result.size() == 1)) {
          team = result.getOneOrNull();
          team.getRelated(AtsRelationTypes.ProjectToTeamDefinition_Project).getOneOrNull();
        }
        childArtifact = tx.createArtifact(AtsArtifactTypes.ActionableItem, artifact.getName());
        ResultSet<ArtifactReadable> results =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andNameEquals("Actionable Items").getResults();
        tx.addChild(results.getExactlyOne(), childArtifact);
        Map<String, List<String>> attributes = artifact.getAttributes();
        Set<Entry<String, List<String>>> entrySet = attributes.entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
          String type = entry.getKey();
          String[] split = type.split(";");
          List<String> value = entry.getValue();
          for (String string : value) {
            if ((split.length == 2) && split[1].equals("Date")) {
              Date date = CommonUtil.getDate(string);
              tx.setSoleAttributeValue(childArtifact, AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"),
                  date);
            }
            else {
              tx.setSoleAttributeFromString(childArtifact,
                  AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), string);
            }
          }
        }
        tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Actionable, "true");
        if (team != null) {
          tx.relate(team, AtsRelationTypes.TeamActionableItem_ActionableItem, childArtifact);
        }
      }
      tx.commit();
      ResultSet<ArtifactReadable> result =
          orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
      ArtifactReadable readableArtifact = result.getExactlyOne();
      TransferableArtifact ar = new TransferableArtifact();
      ar.setName(readableArtifact.getName());
      TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(readableArtifact, ar);
      TransferableArtifactsContainer artifactsContainer = new TransferableArtifactsContainer();
      artifactsContainer.addAll(Arrays.asList((ITransferableArtifact) ar));
      JSONSerializer serializer = new JSONSerializer();
      String serialize = serializer.deepSerialize(artifactsContainer);
      return serialize;
    }
    catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
    return json;
  }
  
  
  /**
   * Rest method to update a Component artifact
   *
   * @param json String component attribute values to be updated
   * @return serialize String updated Component artifact
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updatePackage")
  public String updateComponentWeb(final String json) {
    try {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder()
          .registerTypeAdapter(ITransferableArtifact.class, new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
      String attribute = artifact.getUuid();
      TransactionFactory txFactory = orcsApi.getTransactionFactory();
      TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
          UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
          "Update Component Artifact");
      if (attribute != null) {
        ResultSet<ArtifactReadable> list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
            .andIsOfType(AtsArtifactTypes.ActionableItem).andUuid(Long.valueOf(attribute)).excludeDeleted().getResults();
        for (ArtifactReadable artifactReadable : list) {
          Map<String, List<String>> attributes = artifact.getAttributes();
          Set<Entry<String, List<String>>> entrySet = attributes.entrySet();
          for (Entry<String, List<String>> entry : entrySet) {
            String type = entry.getKey();
            String[] split = type.split(";");
            List<String> value = entry.getValue();
            for (String string : value) {
              if ((split.length == 2) && split[1].equals("Date")) {
                Date date = CommonUtil.getDate(string);
                tx.setSoleAttributeValue(artifactReadable,
                    AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), date);
              }
              else {
                tx.setSoleAttributeFromString(artifactReadable,
                    AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), string);
              }
            }
          }
          tx.commit();
          List<ITransferableArtifact> trans = new ArrayList<ITransferableArtifact>();
          TransferableArtifact ar = new TransferableArtifact();
          ar.setName(artifactReadable.getName());
          TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
          trans.add(ar);
          TransferableArtifactsContainer container = new TransferableArtifactsContainer();
          container.addAll(trans);
          JSONSerializer serializer = new JSONSerializer();
          String serialize = serializer.deepSerialize(container);
          return serialize;
        }
      }
    }
    catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
    catch (OseeCoreException e) {
      e.printStackTrace();
    }
    return json;
  }
}
