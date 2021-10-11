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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
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
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * Team Resource to return Teams and Team related queries
 *
 * @author Ajay Chandrahasan
 */
@Path("Teams")
public class TeamsResource extends AbstractConfigResource {

   public TeamsResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.TeamDefinition, atsApi, orcsApi);
   }

   /**
    * This function is used to create a new Team artifact with all the attribute values and relations sent from Client
    *
    * @param json String which contains artifact, its attributes values and relation
    * @return serialize String with the created artifact
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("createTeamWeb")
   public String createTeamWeb(String json) {
      try {
         Gson gson = new Gson();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         ArtifactId childArtifact = null;
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         List<String> projectDetails = artifact.getAttributes("Project");
         List<String> teamMembers = artifact.getAttributes("TeamMembers");
         List<String> teamLeads = artifact.getAttributes("TeamLeads");
         ArtifactReadable project = null;

         for (String projectGuid : projectDetails) {
            project = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
               Long.valueOf(projectGuid)).getResults().getExactlyOne();
         }

         ArtifactReadable currentUser = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(currentUser.getId());
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON, userId, "Add New Team");
         childArtifact = tx.createArtifact(AtsArtifactTypes.TeamDefinition, artifact.getName());

         ResultSet<ArtifactReadable> results =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andNameEquals("Teams").getResults();
         tx.addChild(results.getExactlyOne(), childArtifact);
         tx.relate(project, AtsRelationTypes.ProjectToTeamDefinition_TeamDefinition, childArtifact);

         List<String> projectUsers = new ArrayList<String>();
         projectUsers.addAll(teamMembers);
         projectUsers.addAll(teamLeads);

         Object[] st = projectUsers.toArray();

         for (Object s : st) {
            if (projectUsers.indexOf(s) != projectUsers.lastIndexOf(s)) {
               projectUsers.remove(projectUsers.lastIndexOf(s));
            }
         }

         for (String projectUser : projectUsers) {
            ResultSet<ArtifactReadable> projectMember =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
                  Long.valueOf(projectUser)).getResults();
            tx.relate(project, AtsRelationTypes.ProjectToUser_Project, projectMember.getExactlyOne());
         }

         for (String team : teamMembers) {
            ResultSet<ArtifactReadable> teamMember =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(Long.valueOf(team)).getResults();
            tx.relate(childArtifact, AtsRelationTypes.TeamMember_Member, teamMember.getExactlyOne());
            tx.relate(project, CoreRelationTypes.Users_User, teamMember.getExactlyOne());
         }

         for (String teamLead : teamLeads) {
            ResultSet<ArtifactReadable> teamLeader =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(Long.valueOf(teamLead)).getResults();
            tx.relate(childArtifact, AtsRelationTypes.TeamLead_Lead, teamLeader.getExactlyOne());
            tx.relate(project, CoreRelationTypes.Users_User, teamLeader.getExactlyOne());

            ArtifactReadable atsAdmin =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.UserGroup).andId(
                  ArtifactId.valueOf(136750l)).getResults().getExactlyOne();
            ArtifactReadable oseeAdmin = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               CoreArtifactTypes.UserGroup).andNameEquals("OseeAdmin").getResults().getExactlyOne();
            tx.relate(atsAdmin, CoreRelationTypes.Users_User, teamLeader.getExactlyOne());
            tx.relate(oseeAdmin, CoreRelationTypes.Users_User, teamLeader.getExactlyOne());
         }

         List<String> attributes2 = artifact.getAttributes(AtsAttributeTypes.WorkflowDefinition.toString());

         if ((attributes2 != null) && (attributes2.size() > 0)) {
            tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.WorkflowDefinition, attributes2.get(0));
         }

         tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.TeamUsesVersions, "true");
         tx.commit();

         ResultSet<ArtifactReadable> result =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
         ArtifactReadable readableArtifact = result.getExactlyOne();
         TransferableArtifactsContainer artifactsContainer = new TransferableArtifactsContainer();
         TransferableArtifact ar = new TransferableArtifact();
         ar.setName(readableArtifact.getName());
         TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(readableArtifact, ar);
         artifactsContainer.addAll(Arrays.asList((ITransferableArtifact) ar));

         JSONSerializer serializer = new JSONSerializer();
         String serialize = serializer.deepSerialize(artifactsContainer);

         return serialize;
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to update a Team
    *
    * @param json String artifact with all to be updated attribute values
    * @return serialize String Updated Team artifact
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("updateTeamWeb")
   public String updateTeamWeb(String json) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         String serialize = null;
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         ArtifactReadable currentUser = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(currentUser.getId());
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx =
            txFactory.createTransaction(CommonUtil.getCommonBranch(orcsApi), userId, "Update Release Artifact");
         Map<String, List<String>> attributeMap;
         String attribute = artifact.getUuid();

         if (attribute != null) {
            ResultSet<ArtifactReadable> list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               AtsArtifactTypes.TeamDefinition).getResults();
            List<TransferableArtifact> listTras = new ArrayList<TransferableArtifact>();

            for (ArtifactReadable artifactReadable : list) {
               if (artifactReadable.getGuid().equals(attribute)) {
                  List<String> projectDetails = artifact.getAttributes("Project");
                  List<String> teamMembers = artifact.getAttributes("TeamMembers");
                  List<String> teamLeads = artifact.getAttributes("TeamLeads");
                  tx.unrelateFromAll(AtsRelationTypes.ProjectToTeamDefinition_Project, artifactReadable);
                  tx.unrelateFromAll(AtsRelationTypes.TeamMember_Team, artifactReadable);
                  tx.unrelateFromAll(AtsRelationTypes.TeamLead_Team, artifactReadable);

                  ResultSet<ArtifactReadable> relatedUserForProject = null;

                  for (String project : projectDetails) {
                     ResultSet<ArtifactReadable> projects =
                        orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(project).getResults();
                     tx.relate(projects.getExactlyOne(), AtsRelationTypes.ProjectToTeamDefinition_Project,
                        artifactReadable);
                     relatedUserForProject = projects.getExactlyOne().getRelated(CoreRelationTypes.Users_User);
                     tx.unrelateFromAll(AtsRelationTypes.ProjectToUser_Project, projects.getExactlyOne());

                     List<String> projectUsers = new ArrayList<String>();
                     projectUsers.addAll(teamMembers);
                     projectUsers.addAll(teamLeads);

                     Object[] st = projectUsers.toArray();

                     for (Object s : st) {
                        if (projectUsers.indexOf(s) != projectUsers.lastIndexOf(s)) {
                           projectUsers.remove(projectUsers.lastIndexOf(s));
                        }
                     }

                     for (String projectUser : projectUsers) {
                        ResultSet<ArtifactReadable> projectMember =
                           orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(projectUser).getResults();
                        tx.relate(projects.getExactlyOne(), AtsRelationTypes.ProjectToUser_Project,
                           projectMember.getExactlyOne());
                     }

                     List<ArtifactReadable> removeUserFromProject = new ArrayList<ArtifactReadable>();

                     for (ArtifactReadable relatedUser : relatedUserForProject) {
                        boolean isFound = false;

                        for (String teamMember : teamMembers) {
                           if (teamMember.equals(relatedUser.getGuid())) {
                              isFound = true;

                              break;
                           }
                        }

                        for (String teamLead : teamLeads) {
                           if (teamLead.equals(relatedUser.getGuid())) {
                              isFound = true;

                              break;
                           }
                        }

                        for (String teamLead : teamLeads) {
                           if (teamLead.equals(relatedUser.getGuid())) {
                              isFound = true;

                              break;
                           }
                        }

                        if (!isFound) {
                           removeUserFromProject.add(artifactReadable);
                        }
                     }

                     for (ArtifactReadable removeUser : removeUserFromProject) {
                        tx.unrelate(projects.getExactlyOne(), CoreRelationTypes.Users_User, removeUser);
                     }
                  }

                  for (String team : teamMembers) {
                     ResultSet<ArtifactReadable> teamMember =
                        orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(team).getResults();

                     for (ArtifactReadable artifactReadable2 : teamMember) {
                        tx.relate(artifactReadable, AtsRelationTypes.TeamMember_Team, artifactReadable2);
                     }
                  }

                  for (String teamLead : teamLeads) {
                     ResultSet<ArtifactReadable> teamLeader =
                        orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(teamLead).getResults();

                     for (ArtifactReadable artifactReadable2 : teamLeader) {
                        tx.relate(artifactReadable, AtsRelationTypes.TeamLead_Team, artifactReadable2);

                        ArtifactReadable atsAdmin =
                           orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                              CoreArtifactTypes.UserGroup).andId(
                                 ArtifactId.valueOf(136750l)).getResults().getExactlyOne();
                        ArtifactReadable oseeAdmin =
                           orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                              CoreArtifactTypes.UserGroup).andNameEquals("OseeAdmin").getResults().getExactlyOne();
                        boolean isOseeAdmin = false;
                        boolean isAtsAdmin = false;
                        ResultSet<ArtifactReadable> atsAdminList = atsAdmin.getRelated(CoreRelationTypes.Users_User);

                        for (ArtifactReadable artifactReadable3 : atsAdminList) {
                           if (artifactReadable3.getGuid().equals(artifactReadable2.getGuid())) {
                              isAtsAdmin = true;
                           }
                        }

                        ResultSet<ArtifactReadable> oseeAdminList = oseeAdmin.getRelated(CoreRelationTypes.Users_User);

                        for (ArtifactReadable artifactReadable3 : oseeAdminList) {
                           if (artifactReadable3.getGuid().equals(artifactReadable2.getGuid())) {
                              isOseeAdmin = true;
                           }
                        }

                        if (!isAtsAdmin) {
                           tx.relate(atsAdmin, CoreRelationTypes.Users_User, artifactReadable2);
                        }

                        if (!isOseeAdmin) {
                           tx.relate(oseeAdmin, CoreRelationTypes.Users_User, artifactReadable2);
                        }
                     }
                  }

                  List<String> attributes2 =
                     artifact.getAttributes(AtsAttributeTypes.WorkflowDefinition.getIdString());

                  if ((attributes2 != null) && (attributes2.size() > 0)) {
                     tx.setSoleAttributeFromString(artifactReadable, AtsAttributeTypes.WorkflowDefinition,
                        attributes2.get(0));
                  }

                  tx.setSoleAttributeFromString(artifactReadable, CoreAttributeTypes.Name, artifact.getName());
                  tx.commit();

                  ArtifactReadable ai = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(
                     attribute).excludeDeleted().getResults().getExactlyOne();
                  List<ITransferableArtifact> trans = new ArrayList<ITransferableArtifact>();
                  TransferableArtifact ar = new TransferableArtifact();
                  ar.setName(artifactReadable.getName());
                  TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
                  trans.add(ar);

                  TransferableArtifactsContainer container = new TransferableArtifactsContainer();
                  container.addAll(trans);

                  JSONSerializer serializer = new JSONSerializer();
                  serialize = serializer.deepSerialize(container);

                  break;
               }
            }
         }

         return serialize;
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to get all the teams associated to a project
    *
    * @param guidFromUi String guid of the project
    * @return serialize String teams related to project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("teamsForProject")
   public String getTeamsForProject(final String guidFromUi) throws OseeCoreException {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      String guid = guidFromUi;
      ArtifactReadable projectArtifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(guid, CoreBranches.COMMON, orcsApi);
      TransferableArtifactsContainer container = new TransferableArtifactsContainer();
      final ResultSet<ArtifactReadable> teamArtifacts =
         projectArtifact.getRelated(AtsRelationTypes.ProjectToTeamDefinition_TeamDefinition);
      List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

      if (teamArtifacts != null) {
         for (ArtifactReadable artifactReadable : teamArtifacts) {
            TransferableArtifact ar = new TransferableArtifact();
            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
            artifactReadable = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(
               artifactReadable.getGuid()).andIsOfType(AtsArtifactTypes.TeamDefinition).getResults().getExactlyOne();

            ResultSet<? extends AttributeReadable<Object>> attributes =
               artifactReadable.getAttributes(AtsAttributeTypes.WorkflowDefinition);

            if (attributes != null) {
               for (AttributeReadable<Object> attributeReadable : attributes) {
                  ar.putAttributes(AtsAttributeTypes.WorkflowDefinition.getName(),
                     Arrays.asList(attributeReadable.getValue().toString()));
               }
            }

            List<ITransferableArtifact> artMembers = new ArrayList<ITransferableArtifact>();
            List<ITransferableArtifact> artLeadList = new ArrayList<ITransferableArtifact>();
            ResultSet<ArtifactReadable> relatedLeads = artifactReadable.getRelated(AtsRelationTypes.TeamLead_Lead);

            for (ArtifactReadable artLeads : relatedLeads) {
               TransferableArtifact arChild = new TransferableArtifact();
               TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artLeads, arChild);

               AttributeReadable<Object> exactlyOne = artLeads.getAttributes(CoreAttributeTypes.UserId).getExactlyOne();
               arChild.putAttributes(CoreAttributeTypes.UserId.getName(),
                  Arrays.asList(exactlyOne.getValue().toString()));
               artLeadList.add(arChild);
            }

            ar.putRelations(AtsRelationTypes.TeamLead_Lead.getName(), artLeadList);

            ResultSet<ArtifactReadable> relatedMembs = artifactReadable.getRelated(AtsRelationTypes.TeamMember_Member);

            for (ArtifactReadable artMem : relatedMembs) {
               TransferableArtifact arChild = new TransferableArtifact();
               TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artMem, arChild);

               AttributeReadable<Object> exactlyOne = artMem.getAttributes(CoreAttributeTypes.UserId).getExactlyOne();
               arChild.putAttributes(CoreAttributeTypes.UserId.getName(),
                  Arrays.asList(exactlyOne.getValue().toString()));
               artMembers.add(arChild);
            }

            ar.putRelations(AtsRelationTypes.TeamMember_Member.getName(), artMembers);
            listTras.add(ar);
         }

         container.addAll(listTras);
      }

      JSONSerializer serializer = new JSONSerializer();
      String serialize = serializer.deepSerialize(container);

      return serialize;
   }
}
