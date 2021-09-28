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
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.enums.token.AgileChangeTypeAttributeType.AgileChangeTypeEnum;
import org.eclipse.osee.ats.api.data.enums.token.PriorityAttributeType.PriorityEnum;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.CommentArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.CustomizedTeamWorkFlowArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TeamWorkFlowArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.icteam.common.clientserver.util.CommentItem;
import org.eclipse.osee.icteam.common.clientserver.util.CommentType;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.mail.notifier.ICTeamMailNotifier;
import org.eclipse.osee.icteam.web.mail.notifier.ICTeamNotifyType;
import org.eclipse.osee.icteam.web.rest.data.write.CustomizedTeamWorkFlowArtifactLoader;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.icteam.web.rest.layer.util.UserUtility;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * Teamworkflow Resource to return Teamworkflow and Teamworkflow related queries
 *
 * @author Ajay Chandrahasan
 */
@Path("tasks")
public class TeamWorkflowResource extends AbstractConfigResource {

   public TeamWorkflowResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.TeamWorkflow, atsApi, orcsApi);
   }

   /**
    * Method to get all TeamWorkFlow for a current user and given sprint
    *
    * @param artifact : artifact.getGuid --> User GUID artifact.getParentGuid() --> Sprint/Release GUID
    * @return This method will return the teamworkflow resource for a given sprint and user. Team workflow resources
    * will also have next States for transition from dashboard
    * @throws OseeCoreException
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("tasksforuserAndSprint")
   public String getTeamWorkflowForCurrentUserAndSprint(final String currentUser) throws OseeCoreException {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(currentUser, TransferableArtifact.class);

      if (artifact == null) {
         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         JSONSerializer serializer = new JSONSerializer();
         String json = serializer.deepSerialize(container);

         return json;
      }

      String sprintGuid = artifact.getParentGuid();
      String userGuid = artifact.getUuid();
      List<String> filterList = artifact.getAttributes("filter");

      if ((filterList == null) || (filterList.size() == 0)) {
         filterList.add("Working");
      }

      TransferableArtifactsContainer container = new TransferableArtifactsContainer();
      List<ITransferableArtifact> artifacts = new ArrayList<ITransferableArtifact>();
      ArtifactReadable sprintArtifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(sprintGuid, CoreBranches.COMMON, orcsApi);
      ResultSet<ArtifactReadable> tasks = null;

      for (String filter : filterList) {
         QueryBuilder query =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.TeamWorkflow).and(
               AtsAttributeTypes.CurrentStateType, filter, QueryOption.EXACT_MATCH_OPTIONS).andRelatedTo(
                  AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, sprintArtifact);

         if (filter.equals("Working")) {
            if ((userGuid != null) && (userGuid.length() > 0)) {
               ArtifactReadable userArtifact =
                  CommonUtil.getArtifactFromIdExcludingDeleted(userGuid, CoreBranches.COMMON, orcsApi);
               query = query.and(AtsAttributeTypes.CurrentState,
                  userArtifact.getSoleAttributeAsString(CoreAttributeTypes.UserId), QueryOption.TOKEN_DELIMITER__ANY);
            }

            try {
               tasks = query.getResults();
            } catch (OseeCoreException e) {
               e.printStackTrace();
            }
         } else if (filter.equals("Completed") || filter.equals("Cancelled")) {
            try {
               List<ArtifactReadable> listCompCan = new ArrayList<ArtifactReadable>();
               ResultSet<ArtifactReadable> tempList = query.getResults();

               if ((userGuid != null) && (userGuid.length() > 0)) {
                  ArtifactReadable userArtifact =
                     CommonUtil.getArtifactFromIdExcludingDeleted(userGuid, CoreBranches.COMMON, orcsApi);
                  String userId = userArtifact.getSoleAttributeAsString(CoreAttributeTypes.UserId);

                  for (ArtifactReadable artifactReadable : tempList) {
                     if (filter.equals("Completed")) {
                        ResultSet<? extends AttributeReadable<Object>> attributes =
                           artifactReadable.getAttributes(AtsAttributeTypes.CompletedBy);

                        if ((attributes != null) && (attributes.size() > 0)) {
                           for (AttributeReadable<Object> attributeReadable : attributes) {
                              String completedByUserID = (String) attributeReadable.getValue();

                              if (completedByUserID.equals(userId)) {
                                 listCompCan.add(artifactReadable);
                              }
                           }
                        }
                     } else if (filter.equals("Cancelled")) {
                        ResultSet<? extends AttributeReadable<Object>> attributes =
                           artifactReadable.getAttributes(AtsAttributeTypes.CancelledBy);

                        if ((attributes != null) && (attributes.size() > 0)) {
                           for (AttributeReadable<Object> attributeReadable : attributes) {
                              String completedByUserID = (String) attributeReadable.getValue();

                              if (completedByUserID.equals(userId)) {
                                 listCompCan.add(artifactReadable);
                              }
                           }
                        }
                     }
                  }

                  tasks = ResultSets.newResultSet(listCompCan);
               } else {
                  tasks = tempList;
               }
            } catch (OseeCoreException e) {
               e.printStackTrace();
            }
         }

         ArtifactReadable mileStoneArtifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Child, sprintArtifact).getResults().getExactlyOne();
         ArtifactReadable projectArtifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andRelatedTo(
            AtsRelationTypes.ProjectToVersion_Version, mileStoneArtifact).getResults().getExactlyOne();
         List<ITransferableArtifact> usersFromTeam = getTeamUsers(projectArtifact);

         if ((tasks != null) && (tasks.size() > 0)) {
            for (ArtifactReadable task : tasks) {
               IAtsWorkItem workItem = OseeCoreData.getAtsServer().getWorkItemService().getWorkItem(task);
               List<IAtsStateDefinition> toStates = workItem.getStateDefinition().getToStates();
               ArrayList<String> states = new ArrayList<String>();

               for (Object element : toStates) {
                  IAtsStateDefinition iAtsStateDefinition = (IAtsStateDefinition) element;
                  states.add(iAtsStateDefinition.getName());
               }

               String shortname =
                  task.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getAttributes(
                     AtsAttributeTypes.Shortname).getExactlyOne().toString();
               String workPackage = task.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
               String taskId = shortname + "-" + workPackage;
               TransferableArtifact ar = new TransferableArtifact();
               TranferableArtifactLoader.copyBasicTaskInfoToTransferableArtifact(task, ar);
               ar.putAttributes("states", states);
               /**
                * The below method getUsersFromGroupWeb is commented because, Group concept is removed from ICTeam tool.
                * But if we have to get groups into ICteam, then we have to use the method again to get the assignee for
                * dropdowm
                */
               ar.putRelations("AssigneeForCombo", usersFromTeam);
               ar.putAttributes("TaskId", Arrays.asList(taskId));
               artifacts.add(ar);
            }
         }
      }

      container.addAll(artifacts);

      String json = gson.toJson(container);

      return json;
   }

   /**
    * Method to get Team users of a Project
    *
    * @param projectArtifact ArtifactReadable Project artifact to get Team users (Team Members and Team Leads)
    * @return assigneeList List<ITransferableArtifact> List of all Team Users of a Project
    */
   private List<ITransferableArtifact> getTeamUsers(ArtifactReadable projectArtifact) {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      ResultSet<ArtifactReadable> teams = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
         AtsArtifactTypes.TeamDefinition).andRelatedTo(AtsRelationTypes.ProjectToTeamDefinition_Project,
            projectArtifact).getResults();
      List<ArtifactReadable> userArtifactList = new ArrayList<ArtifactReadable>();

      if ((teams != null) && (teams.size() > 0)) {
         for (ArtifactReadable team : teams) {
            ResultSet<ArtifactReadable> artifacts = team.getRelated(AtsRelationTypes.TeamLead_Lead);

            for (ArtifactReadable userArtifact : artifacts) {
               userArtifactList.add(userArtifact);
            }

            ResultSet<ArtifactReadable> related = team.getRelated(AtsRelationTypes.TeamMember_Member);

            for (ArtifactReadable userArtifact : related) {
               userArtifactList.add(userArtifact);
            }
         }
      }

      List<ITransferableArtifact> assigneeList = new ArrayList<ITransferableArtifact>();

      for (ArtifactReadable user : userArtifactList) {
         TransferableArtifact userTemp = new TransferableArtifact();
         TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, userTemp);
         userTemp.putAttributes(CoreAttributeTypes.UserId.toString(),
            Arrays.asList(user.getAttributes(CoreAttributeTypes.UserId).getExactlyOne().toString()));
         assigneeList.add(userTemp);
      }

      return assigneeList;
   }

   /**
    * Method to get Teamworkflows of a Project
    *
    * @param projectArtifact {@linkplain ArtifactReadable} Project artifact to get related Teamworkflows
    * @return artifactReadables List<ArtifactReadable> List of all Teamworkflows realted to Project
    */
   private List<ArtifactReadable> getTeamworkFlowArtifacts(final ArtifactReadable projectArtifact) {
      List<ArtifactReadable> artifactReadables = new ArrayList<ArtifactReadable>();

      try {
         final ResultSet<ArtifactReadable> teamWorkflowArtifacts =
            projectArtifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_TeamWorkflow);

         for (ArtifactReadable artifactReadable : teamWorkflowArtifacts) {
            artifactReadables.add(artifactReadable);
         }

         return artifactReadables;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Method called locally to update Teamworkflow attributes and relation values sent from Client
    *
    * @param artifact {@linkplain ITransferableArtifact} with values to be updated
    */
   private void updateTeamWorkFlow(final ITransferableArtifact artifact) {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      TransactionFactory txFactory = orcsApi.getTransactionFactory();
      TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
         UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
         "Update Team Artifact");
      String guid = artifact.getUuid();
      ArtifactReadable teamwfartifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(guid, CoreBranches.COMMON, orcsApi);
      Date estimatedDate = null;
      String createdUserId = "";
      List<String> assignees = new ArrayList<String>();
      boolean sendMailToAssignee = false;
      String changes = "";
      Map<String, List<String>> attributes = artifact.getAttributes();
      Set<Entry<String, List<String>>> entrySet = attributes.entrySet();
      Map<String, Pair<String, String>> linkedHashMap = new LinkedHashMap<>();
      String timeSpent = "";
      String estimatedTime = "";
      String currentDate = CommonUtil.getCurrentDate();

      for (Entry<String, List<String>> entry : entrySet) {
         String type = entry.getKey();
         String[] split = type.split(";");
         List<String> value = entry.getValue();

         for (String string : value) {
            if ((split.length == 2) && split[1].equals("Date")) {
               if (string.equals("NIL")) {
                  tx.deleteSoleAttribute(teamwfartifact,
                     AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"));
               } else {
                  Date date = CommonUtil.getDate(string);

                  if (date != null) {
                     tx.setSoleAttributeValue(teamwfartifact,
                        AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), date);

                     if (Long.parseLong(AtsAttributeTypes.EstimatedCompletionDate.getId().toString()) == Long.parseLong(
                        split[0])) {
                        estimatedDate = date;
                     }
                  }
               }
            } else if ((split.length == 2) && split[1].equals("Project")) {
               updateProjectAttributes(teamwfartifact, tx, split[0], string);
            } else {
               if (split[0].matches(".*[1-9].*")) {
                  if (Long.parseLong(AtsAttributeTypes.CurrentState.getId().toString()) == Long.parseLong(split[0])) {
                     if (!(string.contains("Cancelled") || string.contains("Completed"))) {
                        List<ITransferableArtifact> assignees2 = CommonUtil.getAssignees(string);
                        List<String> users = new ArrayList<String>();

                        for (ITransferableArtifact transferableArtifact : assignees2) {
                           users.add(transferableArtifact.getAttributes(CoreAttributeTypes.UserId.toString()).get(0));
                        }

                        assignees.addAll(users);

                        String createdBy = teamwfartifact.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);

                        if (createdBy != null) {
                           createdUserId = createdBy;
                        }

                        if (estimatedDate == null) {
                           AttributeReadable<Object> estDate = teamwfartifact.getAttributes(
                              AtsAttributeTypes.EstimatedCompletionDate).getAtMostOneOrNull();

                           if (estDate != null) {
                              if (estDate.getValue() != null) {
                                 String estdate = estDate.getValue().toString();

                                 if (estdate != null) {
                                    Date date = CommonUtil.getDate(estdate);
                                    estimatedDate = date;
                                 }
                              }
                           }
                        }

                        sendMailToAssignee = true;
                     }
                  }

                  if (Long.parseLong(AtsAttributeTypes.Rank.getId().toString()) == Long.parseLong(split[0])) {
                  }

                  if (split[0].equalsIgnoreCase("1152921504606847192")) {
                     if (string.contains("Completed")) {
                        if (!(teamwfartifact.getAttributes(
                           AtsAttributeTypes.CurrentStateType).getExactlyOne().toString().equalsIgnoreCase(
                              "Completed"))) {
                           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                           Date completedDate = null;
                           try {
                              completedDate = simpleDateFormat.parse(currentDate);
                           } catch (ParseException e) {
                              e.printStackTrace();
                           }
                           tx.setSoleAttributeValue(teamwfartifact, AtsAttributeTypes.CompletedDate, completedDate);
                           tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.CurrentStateType,
                              "Completed");
                        }
                     }

                     if (string.contains("Cancelled")) {
                        tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.CurrentStateType, "Cancelled");
                     }

                     if (string.contains("InProgress")) {
                        tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.CurrentStateType, "Working");
                     }

                     tx.setSoleAttributeFromString(teamwfartifact,
                        AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), string);
                  } else {
                     if (string.trim().length() > 0) {
                        tx.setSoleAttributeFromString(teamwfartifact,
                           AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), string);
                     }
                     //Get timeSpent and estimatedTime
                     if (split[0].equalsIgnoreCase("1152921504606847212")) {
                        timeSpent = string;
                     }
                     if (split[0].equalsIgnoreCase("1152921504606847182")) {
                        estimatedTime = string;
                     }
                  }
               } else if (split[0].equalsIgnoreCase("tag.Information")) {
                  tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.Information, string);
               } else if (split[0].equalsIgnoreCase("req.Condition")) {
                  tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.Condition, string);
               }
            }
         }
      }

      if (!estimatedTime.isEmpty()) {
         String existingBurndownData = teamwfartifact.getAttributeValuesAsString(AtsAttributeTypes.BurnDownData);
         Gson gson = new Gson();
         LinkedHashMap<String, Pair<String, String>> existingLinkedHashMap =
            gson.fromJson(existingBurndownData, LinkedHashMap.class);
         Pair<String, String> pair = new Pair<>(estimatedTime, timeSpent);
         linkedHashMap.putAll(existingLinkedHashMap);
         linkedHashMap.put(currentDate, pair);
         JSONSerializer jsonSerializer = new JSONSerializer();
         String burnDownData = jsonSerializer.deepSerialize(linkedHashMap);
         tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.BurnDownData, burnDownData);
      }

      if (artifact.getAttributes("Product_Backlog") != null) {
         tx.unrelateFromAll(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow, teamwfartifact);

         try {
            tx.commit();
         } catch (OseeCoreException e) {
            e.printStackTrace();
         }
      } else {
         List<ITransferableArtifact> list =
            artifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.toString());

         if ((list != null) && (list.size() > 0)) {
            List<ArtifactReadable> updatedReleases = new ArrayList<ArtifactReadable>();
            tx.unrelateFromAll(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow, teamwfartifact);

            try {
               tx.commit();
            } catch (OseeCoreException e) {
               e.printStackTrace();
            }

            ArtifactReadable wfArtifact =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(guid).getResults().getExactlyOne();
            Set<TransferableArtifact> relatedVersions = new HashSet<TransferableArtifact>();
            relatedVersions.addAll((Collection<? extends TransferableArtifact>) list);

            if (!updatedReleases.isEmpty() || !relatedVersions.isEmpty()) {
               if (!relatedVersions.isEmpty()) {
                  for (TransferableArtifact transferableArtifact : relatedVersions) {
                     ArtifactReadable releaseArtifact =
                        orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(
                           transferableArtifact.getUuid()).getResults().getExactlyOne();

                     if (!updatedReleases.contains(releaseArtifact)) {
                        updatedReleases.add(releaseArtifact);
                     }
                  }
               }

               for (ArtifactReadable artifactReadable : updatedReleases) {
                  tx.relate(wfArtifact, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, artifactReadable);
               }
            }
         }
      }

      List<ITransferableArtifact> relatedSuperCedes =
         artifact.getRelatedArtifacts(CoreRelationTypes.Supercedes_Supercedes.toString());

      if (relatedSuperCedes != null) {
         for (ITransferableArtifact transferableArtifact : relatedSuperCedes) {
            ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(tmWfArtifact, CoreRelationTypes.Supercedes_Supercedes, teamwfartifact);
         }
      }

      List<ITransferableArtifact> relatedSuperCeded =
         artifact.getRelatedArtifacts(CoreRelationTypes.Supercedes_SupercededBy.toString());

      if (relatedSuperCeded != null) {
         for (ITransferableArtifact transferableArtifact : relatedSuperCeded) {
            ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(teamwfartifact, CoreRelationTypes.Supercedes_SupercededBy, tmWfArtifact);
         }
      }

      List<ITransferableArtifact> relatedAgileTask = artifact.getRelatedArtifacts("agileTaskLink");

      if (relatedAgileTask != null) {
         for (ITransferableArtifact transferableArtifact : relatedAgileTask) {
            ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(tmWfArtifact, AtsRelationTypes.AgileTaskLink_TeamWorkflowA, teamwfartifact);
         }
      }

      List<ITransferableArtifact> relatedComponents =
         artifact.getRelatedArtifacts(AtsRelationTypes.ActionableItemWorkFlow_ActionableItem.toString());

      if (relatedComponents != null) {
         tx.unrelateFromAll(AtsRelationTypes.ActionableItemWorkFlow_TeamWorkflow, teamwfartifact);

         for (ITransferableArtifact transferableArtifact : relatedComponents) {
            ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(tmWfArtifact, AtsRelationTypes.ActionableItemWorkFlow_ActionableItem, teamwfartifact);
         }
      }

      try {
         tx.commit();
      } catch (Exception e) {
         e.printStackTrace();
      }

      teamwfartifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(teamwfartifact.getIdString(), CoreBranches.COMMON, orcsApi);

      String url = artifact.getAttributes("URLInfo").get(0);
      String projectGuid =
         teamwfartifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getGuid();
      String rapLink = url + "/icteam-web/#/dashboard/" + projectGuid + "/" + teamwfartifact.getGuid();

      if (sendMailToAssignee) {
         changes = changes + "\n" + String.format("You have been set as assignee to the task \"%s\"",
            teamwfartifact.getName());
         sendMail(orcsApi, createdUserId, assignees, teamwfartifact, estimatedDate, ICTeamNotifyType.Updated, changes,
            rapLink);
      }
   }

   /**
    * Method called locally to update Project attributes
    *
    * @param teamwfartifact {@linkplain ArtifactReadable}
    * @param tx {@link TransactionBuilder}
    * @param attrGuid {@link String}
    * @param value {@link String}
    */
   private void updateProjectAttributes(final ArtifactReadable teamwfartifact, final TransactionBuilder tx, final String attrGuid, final String value) {
      ArtifactReadable activeProject =
         teamwfartifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getAtMostOneOrNull();
      tx.setSoleAttributeFromString(activeProject, AttributeTypeToken.valueOf(Long.parseLong(attrGuid), "Attribute"),
         value);
   }

   /**
    * Send mail notifications to created user and assignees of a task when a task is created and task status is updated
    *
    * @param orcsApi {@link OrcsApi}
    * @param createdUserId {@link String}
    * @param assignees List<String>
    * @param teamwfartifact {@link ArtifactReadable}
    * @param estimatedDate {@link Date}
    * @param notify {@link ICTeamNotifyType}
    * @param changes {@link String}
    * @param link {@link String}
    */
   public static void sendMail(final OrcsApi orcsApi, final String createdUserId, final List<String> assignees, final ArtifactReadable teamwfartifact, final Date estimatedDate, final ICTeamNotifyType notify, final String changes, final String link) {
      List<String> mailIds = new ArrayList<String>();
      String createdUserMailId = getMailIdByUserId(orcsApi, createdUserId);

      if (createdUserMailId != null) {
         mailIds.add(createdUserMailId);
      }

      for (String assignee : assignees) {
         if (assignee.length() > 0) {
            String assigneeMailId = getMailIdByUserId(orcsApi, assignee);

            if (assigneeMailId != null) {
               mailIds.add(assigneeMailId);
            }
         }
      }

      ICTeamMailNotifier.notify(orcsApi, teamwfartifact, assignees, mailIds, changes, link, notify);
   }

   /**
    * To create a new task and aiding single transaction for creation
    *
    * @param tx {@link TransactionBuilder}
    * @param artifact {@link TransferableArtifact}
    * @param orcsApi {@link OrcsApi}
    * @param estimatedDate {@link Date}
    * @param notify {@link String}
    * @param createdUserId {@link String}
    * @param assignees List<String>
    * @param productBacklog {@link String}
    * @return childArtifact {@link ArtifactId}
    */
   public static ArtifactId createNewTask(final TransactionBuilder tx, final TransferableArtifact artifact, final OrcsApi orcsApi, Date estimatedDate, String notify, String createdUserId, final List<String> assignees, String productBacklog) {
      String artName = "";

      if (artifact.getName() == null) {
         List<String> artNameList = artifact.getAttributes(CoreAttributeTypes.Name.getId().toString());

         if ((artNameList != null) && !artNameList.isEmpty()) {
            artName = artNameList.get(0);
         }
      } else {
         artName = artifact.getName();
      }

      ArtifactId childArtifact = tx.createArtifact(AtsArtifactTypes.TeamWorkflow, artName);
      ArtifactId childAction = tx.createArtifact(AtsArtifactTypes.Action, artName);
      tx.setSoleAttributeFromString(childAction, AtsAttributeTypes.Description, "des");
      tx.setSoleAttributeFromString(childAction, AtsAttributeTypes.ChangeType, "Problem");
      tx.setSoleAttributeFromString(childAction, AtsAttributeTypes.Priority, "2");

      Map<String, List<String>> attributes = artifact.getAttributes();
      Set<Entry<String, List<String>>> entrySet = attributes.entrySet();

      for (Entry<String, List<String>> entry : entrySet) {
         String type = entry.getKey();
         String[] split = type.split(";");
         List<String> value = entry.getValue();
         Map<String, Pair<String, String>> linkedHashMap = new LinkedHashMap<>();

         for (String string : value) {
            if ((split.length == 2) && split[1].equals("Date")) {
               Date date = CommonUtil.getDate(string);
               Pair<String, String> pair = new Pair<>("0", "0");
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               if (date != null) {
                  String formattedDate = simpleDateFormat.format(date);
                  linkedHashMap.put(formattedDate, pair);
               }
               JSONSerializer jsonSerializer = new JSONSerializer();
               String burnDownData = jsonSerializer.deepSerialize(linkedHashMap);
               tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.BurnDownData, burnDownData);
               tx.setSoleAttributeValue(childArtifact,
                  AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), date);
               tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.WorkflowDefinition, "WorkDef_ICTeam");

               if (Long.parseLong(AtsAttributeTypes.EstimatedCompletionDate.getId().toString()) == Long.parseLong(
                  split[0])) {
                  estimatedDate = date;
               }
            } else {
               if (!split[0].equals("raplink") && !split[0].equals("Product_Backlog") && !split[0].equals(
                  "ArtifactGuid") && !split[0].equals(
                     "ArtifactBranchGuid") && !split[0].equals("CreateTaskFromLinkGuid")) {
                  tx.setSoleAttributeFromString(childArtifact,
                     AttributeTypeToken.valueOf(Long.parseLong(split[0]), "Attribute"), string);

                  if (Long.parseLong(AtsAttributeTypes.CurrentState.getId().toString()) == Long.parseLong(split[0])) {
                     notify = string.substring(0, string.indexOf(";"));

                     String assigneesStr = string.substring(string.indexOf("<") + 1, string.lastIndexOf(">"));
                     String[] _assignees =
                        ((assigneesStr != null) && !assigneesStr.isEmpty()) ? assigneesStr.split("><") : new String[0];
                     assignees.addAll(Arrays.asList(_assignees));
                  } else if (Long.parseLong(AtsAttributeTypes.CreatedBy.getId().toString()) == Long.parseLong(
                     split[0])) {
                     createdUserId = string;
                  }
               }
            }
         }
      }

      /*
       * @author sue6kor To provide unique id for tasks for proj specific pagination
       */
      String uuid = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getGuid().toString();
      String type = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getRelationType().toString();
      String side = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getSide().toString();
      String key = "RelationTypeSide - uuid=[" + uuid + "] type=[" + type + "] side=[" + side + "]";
      List<ITransferableArtifact> projArtifactList = artifact.getRelatedArtifacts(key);

      if ((projArtifactList != null) && !projArtifactList.isEmpty()) {
         ITransferableArtifact projArt = projArtifactList.get(0);
         String projectGUID = projArt.getUuid();
         ResultSet<ArtifactReadable> listCommon =
            orcsApi.getQueryFactory().fromBranch(BranchId.valueOf(projArt.getBranchGuid())).andUuid(
               Long.valueOf(projectGUID)).getResults();
         ArtifactReadable project = listCommon.getOneOrNull();
         String taskIDCountForProject = project.getSoleAttributeAsString(AtsAttributeTypes.TaskCountForProject);

         if (taskIDCountForProject != null) {
            int taskCount = Integer.parseInt(taskIDCountForProject);
            taskCount++;

            String taskCountAttr = setAndVerifyRange(taskCount);
            tx.setSoleAttributeFromString(childArtifact, AttributeTypeToken.valueOf(
               Long.parseLong(AtsAttributeTypes.WorkPackage.getId().toString()), "Attribute"), taskCountAttr);

            if (project.getArtifactType().equals(AtsArtifactTypes.AgileProject)) {
               tx.setSoleAttributeFromString(childArtifact,
                  AttributeTypeToken.valueOf(Long.parseLong(AtsAttributeTypes.Rank.getId().toString()), "Attribute"),
                  taskCountAttr);
            }

            ResultSet<ArtifactReadable> list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               AtsArtifactTypes.WorkDefinition).getResults();
            tx.setSoleAttributeFromString(project,
               AttributeTypeToken.valueOf(AtsAttributeTypes.TaskCountForProject.getId(), "Attribute"),
               String.valueOf(taskCount));
         }
      }

      if (productBacklog == null) {
         String uuid1 = AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getGuid().toString();
         String type1 = AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getRelationType().toString();
         String side1 = AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getSide().toString();
         String key1 = "RelationTypeSide - uuid=[" + uuid1 + "] type=[" + type1 + "] side=[" + side1 + "]";
         List<ITransferableArtifact> relatedVersions = artifact.getRelatedArtifacts(key1);

         if (relatedVersions != null) {
            for (ITransferableArtifact transferableArtifact : relatedVersions) {
               ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(
                  transferableArtifact.getUuid(), CoreBranches.COMMON, orcsApi);
               tx.relate(childArtifact, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, tmWfArtifact);
            }
         }
      }

      List<ITransferableArtifact> relatedComponents =
         artifact.getRelatedArtifacts(AtsRelationTypes.ActionableItemWorkFlow_ActionableItem.toString());

      if (relatedComponents != null) {
         for (ITransferableArtifact transferableArtifact : relatedComponents) {
            ArtifactReadable projectArt = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(projectArt, AtsRelationTypes.ActionableItemWorkFlow_ActionableItem, childArtifact);
         }
      }

      String uuid2 = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getGuid().toString();
      String type2 = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getRelationType().toString();
      String side2 = AtsRelationTypes.ProjectToTeamWorkFlow_Project.getSide().toString();
      String key2 = "RelationTypeSide - uuid=[" + uuid2 + "] type=[" + type2 + "] side=[" + side2 + "]";
      List<ITransferableArtifact> relatedproject = artifact.getRelatedArtifacts(key2);

      if (relatedproject != null) {
         for (ITransferableArtifact transferableArtifact : relatedproject) {
            ArtifactReadable tmWfArtifact = CommonUtil.getArtifactFromIdExcludingDeleted(transferableArtifact.getUuid(),
               CoreBranches.COMMON, orcsApi);
            tx.relate(tmWfArtifact, AtsRelationTypes.ProjectToTeamWorkFlow_Project, childArtifact);
         }
      }

      tx.relate(childAction, AtsRelationTypes.ActionToWorkflow_TeamWorkflow, childArtifact);

      return childArtifact;
   }

   /**
    * Returns Email Id for a given user Id
    *
    * @param orcsApi {@link OrcsApi}
    * @param userId {@link String}
    * @return {@link String}
    */
   private static String getMailIdByUserId(final OrcsApi orcsApi, final String userId) {
      try {
         ArtifactReadable artifactReadable = UserUtility.getUserById(orcsApi, userId);
         ResultSet<? extends AttributeReadable<Object>> attrList = artifactReadable.getAttributes();

         for (AttributeReadable<Object> attributeReadable : attrList) {
            if (attributeReadable.getValue().toString().contains("@")) {
               return attributeReadable.getValue().toString();
            }
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * To get next available states for a task
    *
    * @param str {@link String} Uuid od the task
    * @return json {@link String} next state list
    * @throws OseeCoreException
    */
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   @Path("nextStates")
   public String getNextStates(final String str) throws OseeCoreException {
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact transferableArtifact = gson.fromJson(str, TransferableArtifact.class);
      String guid = transferableArtifact.getUuid();
      ArrayList<String> states = getNextStateList(guid);
      TransferableArtifact artifact = new TransferableArtifact();
      artifact.putAttributes("states", states);

      TransferableArtifactsContainer conTemp = new TransferableArtifactsContainer();
      conTemp.setArtifactList(Arrays.asList((ITransferableArtifact) artifact));

      JSONSerializer serializer = new JSONSerializer();
      String json = serializer.deepSerialize(conTemp);

      return json;
   }

   /**
    * Updates a task state
    *
    * @param str {@link String} details of the task for state transition
    * @return json {@link String} next available states for transition
    * @throws OseeCoreException
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("transitionWeb")
   public String transitionStateWeb(final String str) throws OseeCoreException {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact transferableArtifact = gson.fromJson(str, TransferableArtifact.class);
      String url = transferableArtifact.getAttributes("URLInfo").get(0);
      String guid = transferableArtifact.getUuid();
      ResultSet<ArtifactReadable> rootArtifact =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(Long.valueOf(guid)).getResults();
      boolean isCommit = false;
      ArtifactReadable parentArtifact = rootArtifact.getExactlyOne();
      TransactionFactory txFactory1 = orcsApi.getTransactionFactory();
      TransactionBuilder tx1 = txFactory1.createTransaction(CoreBranches.COMMON,
         UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, transferableArtifact.getCurrentLoggedInUser()).getId()),
         "Update Task Status");
      AttributeReadable<Object> currentStateR =
         parentArtifact.getAttributes(AtsAttributeTypes.CurrentState).getAtMostOneOrNull();
      String currentStateStr = (String) currentStateR.getValue();
      String[] split = currentStateStr.split(";");
      String currentState = split[0];
      List<ITransferableArtifact> assignees = new ArrayList<ITransferableArtifact>();

      if (!currentState.equals("Completed") && !currentState.equals("Cancelled")) {
         assignees = CommonUtil.getBasicAssigneesInfo(currentStateStr);
      }

      if (currentState.equals("Completed")) {
         assignees.add(CommonUtil.getBasicCompletedByUser(parentArtifact));
      }

      if (currentState.equals("Cancelled")) {
         assignees.add(CommonUtil.getBasicCancelledBy(parentArtifact));
      }

      String toState = transferableArtifact.getAttributes("toState").get(0);
      Conditions.checkNotNull(toState, "toState");

      String reason = transferableArtifact.getAttributes("reason").get(0);
      Conditions.checkNotNull(reason, "reason");

      String asUserId = transferableArtifact.getAttributes("asUserId").get(0);
      Conditions.checkNotNull(asUserId, "asUserId");

      List<String> toStateUsers = transferableArtifact.getAttributes("toStateUserId");
      Conditions.checkNotNull(toStateUsers, "toStateUserId");

      String projectGuid = transferableArtifact.getParentGuid();
      Conditions.checkNotNull(projectGuid, "projectGuid");

      List<AtsUser> assigneesList = new ArrayList<AtsUser>();

      for (String userId : toStateUsers) {
         AtsUser toStateUser = AtsCoreUsers.getAtsCoreUserByUserId(userId);

         if (toStateUser != null) {
            assigneesList.add(toStateUser);
         }
      }

      ArtifactReadable projectArtifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
         Long.valueOf(projectGuid)).getResults().getAtMostOneOrNull();
      String cancelComment = "";
      List<String> cancelCommentAttr =
         transferableArtifact.getAttributes(AtsAttributeTypes.StateNotes.getId().toString());

      if ((cancelCommentAttr != null) && (cancelCommentAttr.size() > 0)) {
         cancelComment = cancelCommentAttr.get(0);
      }

      List<String> attributes = transferableArtifact.getAttributes("isAdmin");
      String admin = "false";

      if (!attributes.isEmpty()) {
         admin = transferableArtifact.getAttributes("isAdmin").get(0);
      }

      ArtifactReadable action = null;

      if (guid.matches("[0-9]+")) {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifact(Long.valueOf(guid));
      } else {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifactByGuid(guid);
      }

      if (toState.equalsIgnoreCase("Completed")) {
         if (!(parentArtifact.getAttributes(
            AtsAttributeTypes.CurrentStateType).getExactlyOne().toString().equalsIgnoreCase("Completed"))) {
            tx1.setSoleAttributeFromString(parentArtifact, AtsAttributeTypes.CurrentStateType, "Completed");
            isCommit = true;
         }
      }

      if (toState.equalsIgnoreCase("Cancelled")) {
         if (!(parentArtifact.getAttributes(
            AtsAttributeTypes.CurrentStateType).getExactlyOne().toString().equalsIgnoreCase("Cancelled"))) {
            tx1.setSoleAttributeFromString(parentArtifact, AtsAttributeTypes.CurrentStateType, "Cancelled");
            isCommit = true;
         }
      }

      if (toState.equalsIgnoreCase("InProgress")) {
         if (!(parentArtifact.getAttributes(
            AtsAttributeTypes.CurrentStateType).getExactlyOne().toString().equalsIgnoreCase("Working"))) {
            tx1.setSoleAttributeFromString(parentArtifact, AtsAttributeTypes.CurrentStateType, "Working");
            isCommit = true;
         }
      }

      if (isCommit) {
         tx1.commit();
      }

      CustomizedTeamWorkFlowArtifact transArtifact = new CustomizedTeamWorkFlowArtifact();
      CustomizedTeamWorkFlowArtifactLoader.copyArtifactReadbleToTransferableArtifactWithoutRelation(action,
         transArtifact);

      IAtsWorkItem workItem = OseeCoreData.getAtsServer().getWorkItemService().getWorkItem(action);
      AtsUser asAtsUser = OseeCoreData.getAtsServer().getUserService().getUserByUserId(asUserId);
      IAtsChangeSet changes =
         OseeCoreData.getAtsServer().getStoreService().createAtsChangeSet("transition Action", asAtsUser);
      TransitionHelper helper = null;

      if (admin.equals("true")) {
         helper = new TransitionHelper("Transition " + guid, Collections.singleton(workItem), toState, assigneesList,
            reason, changes, OseeCoreData.getAtsServer(), TransitionOption.OverrideWorkingBranchCheck);
      } else {
         helper = new TransitionHelper("Transition " + guid, Collections.singleton(workItem), toState, assigneesList,
            reason, changes, OseeCoreData.getAtsServer(), TransitionOption.None);
      }

      helper.setTransitionUser(asAtsUser);

      TransitionManager mgr = new TransitionManager(helper);
      TransitionResults results = mgr.handleAll();
      TransferableArtifact artifact = new TransferableArtifact();

      if (!results.isEmpty() || changes.isEmpty()) {
         String nextStates = getNextStates(str);
         JSONDeserializer<TransferableArtifactsContainer> deserializer1 =
            new JSONDeserializer<TransferableArtifactsContainer>();
         TransferableArtifactsContainer transCont = deserializer1.deserialize(nextStates);
         ITransferableArtifact transferableArtifact2 = transCont.getArtifactList().get(0);
         transferableArtifact2.putAttributes("status", Arrays.asList("failed"));
         transferableArtifact2.putAttributes("message", Arrays.asList(results.toString()));

         String json = gson.toJson(transCont);

         return json;
      }

      if (!changes.isEmpty()) {
         changes.execute();
      }

      if (guid.matches("[0-9]+")) {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifact(Long.valueOf(guid));
      } else {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifactByGuid(guid);
      }

      ResultSet<ArtifactReadable> result =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(action).getResults();
      ArtifactReadable exactlyOne = result.getExactlyOne();
      CustomizedTeamWorkFlowArtifactLoader.copyArtifactReadbleToTransferableArtifactWithoutRelation(exactlyOne,
         transArtifact);

      if (toState.equals("Completed") || toState.equals("Cancelled")) {
         if (toState.equals("Completed") && !(assignees.isEmpty())) {
            String completeChanges = String.format("Task \"%s\" completed by %s on %s", transArtifact.getName(),
               ICTeamMailNotifier.getUserNameByUserId(asUserId), DateUtil.getMMDDYY(new Date()));
            String rapServer = transferableArtifact.getAttributes("raplink").get(0);
            String gUid = transArtifact.getGuid();
            String artifactType = transArtifact.getArtifactType();
            String project =
               action.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getGuid();
            String rapLink = url + "/icteam-web/#/dashboard/" + project + "/" + transArtifact.getGuid();
            List<String> assigneeMailIds = new ArrayList<String>();
            List<String> userIdList = new ArrayList<String>();

            for (ITransferableArtifact user : assignees) {
               String userId = user.getAttributes(CoreAttributeTypes.UserId.getName()).get(0);
               String assigneeMailId = getMailIdByUserId(orcsApi, userId);

               if (assigneeMailId != null) {
                  assigneeMailIds.add(assigneeMailId);
                  userIdList.add(userId);
               }
            }

            ICTeamMailNotifier.notify(orcsApi, exactlyOne, userIdList, assigneeMailIds, completeChanges, rapLink,
               ICTeamNotifyType.Completed);
         }

         /*
          * @author sue6kor ICT-00007 Mail trigger for cancelled task
          */

         /*
          * mbh9kor commented as no successor
          */
         if (toState.equals("Cancelled") && !(assignees.isEmpty())) {
            String createdBy = exactlyOne.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);
            String createdUserId = "";

            if (createdBy != null) {
               createdUserId = createdBy;
            }

            String completeChanges =
               String.format("Task \"%s\" cancelled by %s due to reason: %s on %s", transArtifact.getName(),
                  ICTeamMailNotifier.getUserNameByUserId(asUserId), reason, DateUtil.getMMDDYY(new Date()));
            String project =
               action.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getGuid();
            String rapLink = url + "/icteam-web/#/dashboard/" + project + "/" + transArtifact.getGuid();
            List<String> assigneeMailIds = new ArrayList<String>();
            List<String> userIdList = new ArrayList<String>();
            List<ITransferableArtifact> assignee = transArtifact.getAssignee();

            for (ITransferableArtifact transferableArtifact2 : assignee) {
               String userId = transferableArtifact2.getAttributes(CoreAttributeTypes.UserId.getName()).get(0);
               String assigneeMailId = getMailIdByUserId(orcsApi, userId);

               if (assigneeMailId != null) {
                  assigneeMailIds.add(assigneeMailId);
                  userIdList.add(userId);
               }
            }

            ICTeamMailNotifier.notify(orcsApi, exactlyOne, userIdList, assigneeMailIds, completeChanges, rapLink,
               ICTeamNotifyType.Cancelled);
         }
      }

      if (!toState.equals("Cancelled") && !toState.equals("Completed") && !(assignees.isEmpty())) {
         String createdBy = exactlyOne.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);
         String createdUserId = "";

         if (createdBy != null) {
            createdUserId = createdBy;
         }

         String completeChanges = String.format("Task \"%s\" state changed by %s  on %s", transArtifact.getName(),
            ICTeamMailNotifier.getUserNameByUserId(asUserId), DateUtil.getMMDDYY(new Date()));
         String project = action.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getGuid();
         String rapLink = url + "/icteam-web/#/dashboard/" + project + "/" + transArtifact.getGuid();
         List<String> assigneeMailIds = new ArrayList<String>();
         List<String> userIdList = new ArrayList<String>();
         List<ITransferableArtifact> assignee = transArtifact.getAssignee();

         for (ITransferableArtifact transferableArtifact2 : assignee) {
            String userId = transferableArtifact2.getAttributes(CoreAttributeTypes.UserId.getName()).get(0);
            String assigneeMailId = getMailIdByUserId(orcsApi, userId);

            if (assigneeMailId != null) {
               assigneeMailIds.add(assigneeMailId);
               userIdList.add(userId);
            }
         }

         ICTeamMailNotifier.notify(orcsApi, exactlyOne, userIdList, assigneeMailIds, completeChanges, rapLink,
            ICTeamNotifyType.Updated);
      }

      ArtifactReadable exactlyOne1 = null;

      if (guid.matches("[0-9]+")) {
         exactlyOne1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
            Long.valueOf(guid)).getResults().getExactlyOne();
      } else {
         exactlyOne1 =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(guid).getResults().getExactlyOne();
      }

      if ((cancelComment != null) && (cancelComment.length() > 0)) {
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
            UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
            "Update Team Artifact");
         tx.setSoleAttributeFromString(exactlyOne1, AtsAttributeTypes.StateNotes, cancelComment);
         tx.commit();
      }

      List<String> userIdStringList = new ArrayList<String>();
      List<ArtifactReadable> userArtifactList = new ArrayList<ArtifactReadable>();

      if (projectArtifact != null) {
         AttributeReadable<Object> currentStateR1 =
            exactlyOne1.getAttributes(AtsAttributeTypes.CurrentState).getAtMostOneOrNull();
         String currentStateStr1 = (String) currentStateR1.getValue();
         String[] split1 = currentStateStr1.split(";");
         String currentState1 = split1[0];
         AttributeReadable<Object> currentStateType =
            exactlyOne1.getAttributes(AtsAttributeTypes.CurrentStateType).getAtMostOneOrNull();
         String currentStateTypeStr = (String) currentStateType.getValue();

         ResultSet<ArtifactReadable> teams = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andRelatedTo(AtsRelationTypes.ProjectToTeamDefinition_Project,
               projectArtifact).getResults();

         if ((teams != null) && (teams.size() > 0)) {
            for (ArtifactReadable team : teams) {
               ResultSet<ArtifactReadable> artifacts = team.getRelated(AtsRelationTypes.TeamLead_Lead);

               for (ArtifactReadable userArtifact : artifacts) {
                  AttributeReadable<Object> userId =
                     userArtifact.getAttributes(CoreAttributeTypes.UserId).getAtMostOneOrNull();
                  userIdStringList.add((String) userId.getValue());
                  userArtifactList.add(userArtifact);
               }

               ResultSet<ArtifactReadable> related = team.getRelated(AtsRelationTypes.TeamMember_Member);

               for (ArtifactReadable userArtifact : related) {
                  AttributeReadable<Object> userId =
                     userArtifact.getAttributes(CoreAttributeTypes.UserId).getAtMostOneOrNull();
                  userIdStringList.add((String) userId.getValue());
                  userArtifactList.add(userArtifact);
               }
            }
         }

         ResultSet<ArtifactReadable> teams1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andRelatedTo(AtsRelationTypes.ProjectToTeamDefinition_Project,
               projectArtifact).getResults();

         if ((teams1 != null) && (teams1.size() > 0)) {
            for (ArtifactReadable team : teams1) {
               ResultSet<ArtifactReadable> artifacts = team.getRelated(AtsRelationTypes.TeamLead_Lead);

               for (ArtifactReadable userArtifact : artifacts) {
                  AttributeReadable<Object> userId =
                     userArtifact.getAttributes(CoreAttributeTypes.UserId).getAtMostOneOrNull();
                  userIdStringList.add((String) userId.getValue());
                  userArtifactList.add(userArtifact);
               }

               ResultSet<ArtifactReadable> related = team.getRelated(AtsRelationTypes.TeamMember_Member);

               for (ArtifactReadable userArtifact : related) {
                  AttributeReadable<Object> userId =
                     userArtifact.getAttributes(CoreAttributeTypes.UserId).getAtMostOneOrNull();
                  userIdStringList.add((String) userId.getValue());
                  userArtifactList.add(userArtifact);
               }
            }
         }

         if (userIdStringList.size() > 0) {
            TransactionFactory txFactory = orcsApi.getTransactionFactory();
            TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
               UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
               "Update Team Artifact");
            String userString = "";

            for (ITransferableArtifact assignee : assignees) {
               userIdStringList.add(assignee.getUserId().toString());
               userString = userString + "<" + assignee.getUserId().toString() + ">";
            }

            Object[] st = userIdStringList.toArray();

            for (Object s : st) {
               if (userIdStringList.indexOf(s) != userIdStringList.lastIndexOf(s)) {
                  userIdStringList.remove(userIdStringList.lastIndexOf(s));
               }
            }

            currentState1 = currentState1 + ";" + userString + ";;";

            if (!currentStateStr1.equalsIgnoreCase(currentState1)) {
               tx.setSoleAttributeFromString(exactlyOne1, AtsAttributeTypes.CurrentState, currentState1);
               tx.commit();
            }
         }
      }

      if (guid.matches("[0-9]+")) {
         exactlyOne1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
            Long.valueOf(guid)).getResults().getExactlyOne();
      } else {
         exactlyOne1 =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(guid).getResults().getExactlyOne();
      }

      List<ITransferableArtifact> assigneeList = new ArrayList<ITransferableArtifact>();

      for (ArtifactReadable user : userArtifactList) {
         TransferableArtifact userTemp = new TransferableArtifact();
         TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, userTemp);
         userTemp.putAttributes(CoreAttributeTypes.UserId.toString(),
            Arrays.asList(user.getAttributes(CoreAttributeTypes.UserId).getExactlyOne().toString()));
         assigneeList.add(userTemp);
      }

      for (ITransferableArtifact assignee : assignees) {
         assigneeList.add(assignee);
      }

      Object[] st = assigneeList.toArray();

      for (Object s : st) {
         if (assigneeList.indexOf(s) != assigneeList.lastIndexOf(s)) {
            assigneeList.remove(assigneeList.lastIndexOf(s));
         }
      }

      TransferableArtifact art = new TransferableArtifact();
      art.setArtifactType("State");

      ArrayList<String> states = getNextStateList(guid);
      art.putAttributes("states", states);
      assigneeList.add(art);

      TransferableArtifactsContainer conTemp = new TransferableArtifactsContainer();
      conTemp.setArtifactList(assigneeList);

      String json = gson.toJson(conTemp);

      return json;
   }

   /**
    * Updates comments for a task
    *
    * @param json {@link String} comments for updation
    * @return json {@link String} Teamworkflow Artifact with updated comment
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("updateCommentForTWWeb")
   public String updateCommentforTeamWorkFlow(String json) {
      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
            UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
            "Update TeamWorkflow Comment Artifact");
         String guid = artifact.getUuid();
         ResultSet<ArtifactReadable> commentArtifact =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(guid).getResults();
         String uuid = commentArtifact.getExactlyOne().getIdString();
         ArtifactReadable teamwfartifact =
            CommonUtil.getArtifactFromIdExcludingDeleted(uuid, CoreBranches.COMMON, orcsApi);
         List<String> userIDList = artifact.getAttributes("userID");
         String[] split = userIDList.get(0).split(";");
         List<String> commentList = artifact.getAttributes("comment");
         List<String> currentState = artifact.getAttributes("currentState");
         CommentItem noteItem = new CommentItem(CommentType.Comment.name(), currentState.get(0),
            String.valueOf(new Date().getTime()), split[0], commentList.get(0));
         String xml = "";
         ResultSet<? extends AttributeReadable<Object>> attributes =
            teamwfartifact.getAttributes(AtsAttributeTypes.StateNotes);

         if ((attributes != null) && (attributes.size() > 0)) {
            AttributeReadable<Object> exactlyOne = attributes.getExactlyOne();
            String value = (String) exactlyOne.getValue();
            List<CommentItem> fromXml = CommentItem.fromXml(value, "");
            fromXml.add(noteItem);
            xml = CommentItem.toXml(fromXml);
         } else {
            xml = CommentItem.toXml(Arrays.asList(noteItem));
         }
         if (xml != null) {
            if (xml.length() > 0) {
               tx.setSoleAttributeFromString(teamwfartifact, AtsAttributeTypes.StateNotes, xml);
               tx.commit();
            }
         }
         teamwfartifact = CommonUtil.getArtifactFromIdExcludingDeleted(uuid, CoreBranches.COMMON, orcsApi);

         List<TeamWorkFlowArtifact> listTras = new ArrayList<TeamWorkFlowArtifact>();
         CustomizedTeamWorkFlowArtifact ar = new CustomizedTeamWorkFlowArtifact();
         List<String> attributes2 = artifact.getAttributes("isResponse");

         if ((attributes2 != null) && (attributes2.size() > 0)) {
            String isResponse = attributes2.get(0);

            if (isResponse.equals("true")) {
               List<CommentArtifact> listComment = new ArrayList<CommentArtifact>();
               ResultSet<? extends AttributeReadable<Object>> commentAttributes =
                  teamwfartifact.getAttributes(AtsAttributeTypes.StateNotes);

               if ((commentAttributes != null) && (commentAttributes.size() > 0)) {
                  for (AttributeReadable<Object> comment : commentAttributes) {
                     String commentStr = (String) comment.getValue();
                     List<CommentItem> fromXml = CommentItem.fromXml(commentStr, "");

                     for (CommentItem commentItem : fromXml) {
                        CommentArtifact arti = new CommentArtifact();
                        arti.setMsg(commentItem.getMsg());
                        arti.setDate(commentItem.getDate());

                        ArtifactReadable userFromGivenUserId = CommonUtil.getUserFromGivenUserId(commentItem.getUser());
                        AttributeReadable<Object> exactlyOne =
                           userFromGivenUserId.getAttributes(CoreAttributeTypes.Name).getExactlyOne();
                        arti.setUser((String) exactlyOne.getValue());
                        listComment.add(arti);
                     }
                  }

                  ar.setCommentArtifactList(listComment);
               }
            }
         }

         json = gson.toJson(ar);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json;
   }

   /**
    * Updates a teamworkflow artifact with attributes and relation sent from client
    *
    * @param json attributes and relation values to be updated for a task
    * @return json1 updated teamworkflow artifact with next available states
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("taskupdateweb")
   public String updateTeamWfWeb(String json) {
      String json1 = null;

      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         updateTeamWorkFlow(artifact);

         CustomizedTeamWorkFlowArtifact teamWorkFlowArtifact = getUpdatedTeamWorkFlowForWeb(artifact);
         List<String> attributes = artifact.getAttributes("toState");

         if ((attributes != null) && (attributes.size() > 0)) {
            String toState = attributes.get(0);
            String currentState = teamWorkFlowArtifact.getCurrentState();

            if (!currentState.equals(toState)) {
               transitionStateWeb(json);

               ArrayList<String> nextStateList = getNextStateList(artifact.getUuid());
               teamWorkFlowArtifact.putAttributes("states", nextStateList);
            }
         }

         json1 = gson.toJson(teamWorkFlowArtifact);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Method called locally to update a teamworkflow artifact
    *
    * @param artifact {@link TransferableArtifact} to update the values for artifact
    * @return ar {@link CustomizedTeamWorkFlowArtifact} updated teamworkflow artifact
    */
   private CustomizedTeamWorkFlowArtifact getUpdatedTeamWorkFlowForWeb(TransferableArtifact artifact) {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      ResultSet<ArtifactReadable> result = null;

      if (artifact.getUuid().matches("[0-9]+")) {
         result = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
            Long.valueOf(artifact.getUuid())).getResults();
      } else {
         result = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andGuid(artifact.getUuid()).getResults();
      }

      ArtifactReadable exactlyOne = result.getExactlyOne();
      List<TeamWorkFlowArtifact> listTras = new ArrayList<TeamWorkFlowArtifact>();
      CustomizedTeamWorkFlowArtifact ar = new CustomizedTeamWorkFlowArtifact();
      ar.setName(exactlyOne.getName());
      CustomizedTeamWorkFlowArtifactLoader.copyArtifactReadbleToTransferableArtifactWithoutRelation(exactlyOne, ar);

      return ar;
   }

   /**
    * Fetches Task details for a given task Uuid
    *
    * @param task {@link String} with task Uuid
    * @return json {@link String} Task details for a given Uuid
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getTaskDetailsWeb")
   public String getTaskDetailsByGuidWeb(String task) {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(task, TransferableArtifact.class);
      String guid = artifact.getUuid();
      ArtifactReadable teamwfartifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(guid, CoreBranches.COMMON, orcsApi);
      List<ITransferableArtifact> arTempListVersion = new ArrayList<ITransferableArtifact>();
      List<ITransferableArtifact> arTempListCompoennts = new ArrayList<ITransferableArtifact>();
      String json = "";

      if (teamwfartifact != null) {
         ArtifactReadable projectArtifact =
            teamwfartifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne();
         AttributeReadable<Object> shortname =
            projectArtifact.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne();
         shortname.getValue();

         ArtifactReadable teamartifact =
            projectArtifact.getRelated(AtsRelationTypes.ProjectToTeamDefinition_TeamDefinition).getExactlyOne();

         if (teamartifact != null) {
            ResultSet<ArtifactReadable> relatedMileStones =
               teamartifact.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version);

            for (ArtifactReadable mileStone : relatedMileStones) {
               ResultSet<ArtifactReadable> relatedSprints =
                  mileStone.getRelated(CoreRelationTypes.DefaultHierarchical_Child);

               if ((relatedSprints != null) && (relatedSprints.size() > 0)) {
                  for (ArtifactReadable artifactReadable2 : relatedSprints) {
                     ResultSet<? extends AttributeReadable<Object>> releasedAttribute =
                        artifactReadable2.getAttributes(AtsAttributeTypes.Released);

                     if ((releasedAttribute != null) && (releasedAttribute.size() > 0)) {
                        for (AttributeReadable<Object> attributeReadable : releasedAttribute) {
                           boolean releasedAttributeString = (Boolean) attributeReadable.getValue();

                           if (releasedAttributeString == false) {
                              TransferableArtifact ar = new TransferableArtifact();
                              TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable2, ar);

                              String displayName = mileStone.getName() + "-->" + artifactReadable2.getName();
                              ar.putAttributes("displayName", Arrays.asList(displayName));
                              arTempListVersion.add(ar);
                           }
                        }
                     }
                  }
               }
            }

            ResultSet<ArtifactReadable> relatedPackages =
               teamartifact.getRelated(AtsRelationTypes.TeamActionableItem_ActionableItem);

            if ((relatedPackages != null) && (relatedPackages.size() > 0)) {
               for (ArtifactReadable artifactReadable : relatedPackages) {
                  TransferableArtifact arTemp1 = new TransferableArtifact();
                  TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, arTemp1);
                  arTempListCompoennts.add(arTemp1);
               }
            }
         }

         List<CommentArtifact> listComment = new ArrayList<CommentArtifact>();
         ResultSet<? extends AttributeReadable<Object>> commentAttributes =
            teamwfartifact.getAttributes(AtsAttributeTypes.StateNotes);

         if ((commentAttributes != null) && (commentAttributes.size() > 0)) {
            for (AttributeReadable<Object> comment : commentAttributes) {
               String commentStr = (String) comment.getValue();
               List<CommentItem> fromXml = CommentItem.fromXml(commentStr, "");

               for (CommentItem commentItem : fromXml) {
                  CommentArtifact arti = new CommentArtifact();
                  arti.setMsg(commentItem.getMsg());
                  arti.setDate(commentItem.getDate());

                  ArtifactReadable userFromGivenUserId = CommonUtil.getUserFromGivenUserId(commentItem.getUser());
                  AttributeReadable<Object> exactlyOne =
                     userFromGivenUserId.getAttributes(CoreAttributeTypes.Name).getExactlyOne();
                  arti.setUser((String) exactlyOne.getValue());
                  listComment.add(arti);
               }
            }
         }

         List<String> changeTypeList = new ArrayList<String>();
         List<String> priorityTypeList = new ArrayList<String>();
         Set<String> valuesAsOrderedStringSetChangeType = new HashSet<>();
         for (AgileChangeTypeEnum changeTypeEnum : AtsAttributeTypes.AgileChangeType.getEnumValues()) {
            valuesAsOrderedStringSetChangeType.add(changeTypeEnum.getName());
         }

         changeTypeList.addAll(valuesAsOrderedStringSetChangeType);

         Set<String> valuesAsOrderedStringSetPriority = new HashSet<>();

         Collection<PriorityEnum> enumValues2 = AtsAttributeTypes.Priority.getEnumValues();
         for (PriorityEnum changeTypeEnum : enumValues2) {
            valuesAsOrderedStringSetPriority.add(changeTypeEnum.getName());
         }
         priorityTypeList.addAll(valuesAsOrderedStringSetPriority);

         ArrayList<String> nextStateList = getNextStateList(guid);
         CustomizedTeamWorkFlowArtifact ar = new CustomizedTeamWorkFlowArtifact();
         ar.putAttributes("ChangeTypeList", changeTypeList);
         ar.putAttributes("priorityList", priorityTypeList);
         ar.putAttributes("states", nextStateList);
         ar.setCommentArtifactList(listComment);
         ar.setListVersionsDropDown(arTempListVersion);
         ar.setListCompoenntsDropDown(arTempListCompoennts);
         ar.putAttributes(AtsAttributeTypes.Shortname.toString(), Arrays.asList(shortname.toString()));
         ar.putAttributes("WorkPackage",
            Arrays.asList(teamwfartifact.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString()));

         List<ITransferableArtifact> listReview = new ArrayList<ITransferableArtifact>();
         ResultSet<ArtifactReadable> relatedReview =
            teamwfartifact.getRelated(AtsRelationTypes.TeamWorkflowToReview_Review);

         if ((relatedReview != null) && (relatedReview.size() > 0)) {
            for (ArtifactReadable artifactReadable : relatedReview) {
               TransferableArtifact arReview = new TransferableArtifact();
               TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, arReview);
               listReview.add(arReview);
            }
         }

         ar.putRelations(AtsRelationTypes.TeamWorkflowToReview_Review.getName(), listReview);

         ResultSet<? extends AttributeReadable<Object>> taginformationAttributes =
            teamwfartifact.getAttributes(AtsAttributeTypes.Information);

         if ((taginformationAttributes != null) && (taginformationAttributes.size() > 0)) {
            AttributeReadable<Object> exactlyOne = taginformationAttributes.getExactlyOne();
            String string = exactlyOne.getValue().toString();
            ar.putAttributes(AtsAttributeTypes.Information.getName(), Arrays.asList(string));
         } else {
            ar.putAttributes(AtsAttributeTypes.Information.getName(), Arrays.asList(""));
         }

         ResultSet<? extends AttributeReadable<Object>> reqConditionAttributes =
            teamwfartifact.getAttributes(AtsAttributeTypes.Condition);

         if ((reqConditionAttributes != null) && (reqConditionAttributes.size() > 0)) {
            AttributeReadable<Object> exactlyOne = reqConditionAttributes.getExactlyOne();
            String string = exactlyOne.getValue().toString();
            ar.putAttributes(AtsAttributeTypes.Condition.getName(), Arrays.asList(string));
         } else {
            ar.putAttributes(AtsAttributeTypes.Condition.getName(), Arrays.asList(""));
         }

         CustomizedTeamWorkFlowArtifactLoader.copyArtifactReadbleToTransferableArtifactWithoutRelation(teamwfartifact,
            ar);
         json = gson.toJson(ar);
      }

      return json;
   }

   /**
    * Fetch next state list for a task
    *
    * @param uuid {@link String} task uuid
    * @return states {@linkArrayList<String>}
    */
   private ArrayList<String> getNextStateList(String uuid) {
      ArtifactReadable action = null;

      if (uuid.matches("[0-9]+")) {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifact(Long.valueOf(uuid));
      } else {
         action = (ArtifactReadable) OseeCoreData.getAtsServer().getQueryService().getArtifactByGuid(uuid);
      }

      IAtsWorkItem workItem = OseeCoreData.getAtsServer().getWorkItemService().getWorkItem(action);
      List<IAtsStateDefinition> toStates = workItem.getStateDefinition().getToStates();
      ArrayList<String> states = new ArrayList<String>();

      for (Object element : toStates) {
         IAtsStateDefinition iAtsStateDefinition = (IAtsStateDefinition) element;
         states.add(iAtsStateDefinition.getName());
      }

      return states;
   }

   /**
    * Gets the all the backlog tasks for a given project Uuid
    *
    * @param json {@link String} uuid of the project
    * @return json {@link String} list of backlog tasks with Assignes and TaskId
    * @throws OseeCoreException
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("tasksforBackLog")
   public String getTeamWorkflowForProjectBackLog(String json) throws OseeCoreException {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
      String guid = artifact.getUuid();
      ArtifactReadable projectArtifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(guid, CoreBranches.COMMON, orcsApi);
      String shortName = projectArtifact.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString();
      TransferableArtifactsContainer container = new TransferableArtifactsContainer();
      List<ArtifactReadable> teamworkFlowArtifacts = getTeamworkFlowArtifacts(projectArtifact);
      List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

      if (teamworkFlowArtifacts != null) {
         for (ArtifactReadable artifactReadable : teamworkFlowArtifacts) {
            ResultSet<ArtifactReadable> relatedSprints =
               artifactReadable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);

            if (relatedSprints.size() == 0) {
               String workPackageId =
                  artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
               String taskId = shortName + "-" + workPackageId;
               TransferableArtifact ar = new TransferableArtifact();
               ArrayList<String> states = getNextStateList(artifactReadable.getGuid());
               TranferableArtifactLoader.copyBasicTaskInfoToTransferableArtifact(artifactReadable, ar);
               ar.putAttributes("states", states);
               ar.putAttributes("TaskId", Arrays.asList(taskId));

               List<ITransferableArtifact> usersFromGroupWeb =
                  getAssigneesForCombo(projectArtifact.getIdString(), ar.getUuid());
               ar.putRelations("AssigneeForCombo", usersFromGroupWeb);
               listTras.add(ar);
            }
         }

         container.addAll(listTras);
      }

      JSONSerializer serializer = new JSONSerializer();
      json = serializer.deepSerialize(container);

      return json;
   }

   /**
    * Get task details for given Guid
    *
    * @param task {@link String} Guid of the task
    * @return json {@link String} Task details with attributes and relation
    * @throws OseeCoreException
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("taskforGuidsWeb")
   public String getTeamWorkFlowFromGuidWeb(final String task) throws OseeCoreException {
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(task, TransferableArtifact.class);
      ArtifactReadable teamwfartifact =
         CommonUtil.getArtifactFromIdExcludingDeleted(artifact.guid, CoreBranches.COMMON, orcsApi);
      List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

      if (teamwfartifact != null) {
         TransferableArtifact ar = new TransferableArtifact();
         ar.setBranchGuid(teamwfartifact.getBranch().getId());
         TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(teamwfartifact, ar);
         listTras.add(ar);
      }

      String json = "";
      TransferableArtifactsContainer container = new TransferableArtifactsContainer();
      container.addAll(listTras);

      JSONSerializer serializer = new JSONSerializer();
      json = serializer.deepSerialize(container);
      System.out.println(json);

      return json;
   }

   /**
    * Creates a new task with attribute values sent from client
    *
    * @param json {@link String} details of a new task
    * @return json1 {@link String} newly created task
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("createTask")
   public String createTask(final String json) {
      String json1 = "";

      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String url = artifact.geturlinfo();
         List<String> attributes = artifact.getAttributes("Product_Backlog");
         String productBacklog = null;

         if ((attributes != null) && (attributes.size() > 0) && !attributes.isEmpty()) {
            productBacklog = attributes.get(0);
         }

         List<String> workspaceArtifact = artifact.getAttributes("ArtifactGuid");
         String artifactGuid = null;

         if ((workspaceArtifact != null) && (workspaceArtifact.size() > 0) && !workspaceArtifact.isEmpty()) {
            artifactGuid = workspaceArtifact.get(0);
         }

         List<String> workspaceBranch = artifact.getAttributes("ArtifactBranchGuid");
         String artifactBranchGuid = null;

         if ((workspaceBranch != null) && (workspaceBranch.size() > 0) && !workspaceBranch.isEmpty()) {
            artifactBranchGuid = workspaceBranch.get(0);
         }

         List<String> linkTaskInTaskPage = artifact.getAttributes("CreateTaskFromLinkGuid");
         String linkTaskGuid = null;

         if ((linkTaskInTaskPage != null) && (linkTaskInTaskPage.size() > 0) && !linkTaskInTaskPage.isEmpty()) {
            linkTaskGuid = linkTaskInTaskPage.get(0);
         }

         ArtifactId childArtifact = null;
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
            UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
            "Add New task");
         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         Date estimatedDate = null;
         String notify = "";
         String createdUserId = artifact.getAttributesOrElse(AtsAttributeTypes.CreatedBy.getId().toString()).get(0);
         List<String> assignees = new ArrayList<String>();
         List<String> listTemp = new ArrayList<String>();
         listTemp.add(new Date().toString());
         artifact.putAttributes(AtsAttributeTypes.EstimatedCompletionDate.getId().toString() + ";Date", listTemp);

         List<String> listTemp1 = new ArrayList<String>();
         listTemp1.add(new Date().toString());
         artifact.putAttributes(AtsAttributeTypes.CreatedDate.getId().toString() + ";Date", listTemp1);
         childArtifact =
            createNewTask(tx, artifact, orcsApi, estimatedDate, notify, createdUserId, assignees, productBacklog);
         childArtifact.getUuid();
         tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.CurrentStateType, "Working");
         tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Information, "");
         tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Condition, "");
         tx.commit();

         if (artifactBranchGuid != null) {
            BranchToken branchNew = orcsApi.getQueryFactory().branchQuery().getResultsAsId().getList().get(
               Integer.valueOf(artifactBranchGuid));
            TransactionBuilder workspaceTx = txFactory.createTransaction(branchNew,
               UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
               "Create Task from Artifact");
            ArtifactReadable wokrspaceArtifactReadableList =
               orcsApi.getQueryFactory().fromBranch(branchNew).andGuid(artifactGuid).getResults().getExactlyOne();
            ResultSet<? extends AttributeReadable<Object>> attributes2 =
               wokrspaceArtifactReadableList.getAttributes(AtsAttributeTypes.GUID);
            String taskGuid = ";";

            if ((attributes2.size() > 0) && !attributes2.isEmpty()) {
               taskGuid = attributes2.getExactlyOne().getValue().toString() + taskGuid;
               taskGuid = taskGuid + childArtifact.getId();
               workspaceTx.setSoleAttributeFromString(wokrspaceArtifactReadableList, AtsAttributeTypes.GUID, taskGuid);
            } else {
               workspaceTx.setSoleAttributeFromString(wokrspaceArtifactReadableList, AtsAttributeTypes.GUID,
                  childArtifact.getIdString());
            }

            workspaceTx.commit();
         }

         if (linkTaskGuid != null) {
            ResultSet<ArtifactReadable> taskOneList =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                  AtsArtifactTypes.TeamWorkflow).andGuid(linkTaskGuid).getResults();
            ArtifactReadable taskOne = taskOneList.getExactlyOne();
            ResultSet<ArtifactReadable> taskTwoList =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                  AtsArtifactTypes.TeamWorkflow).andUuid(childArtifact.getId()).getResults();
            ArtifactReadable taskTwo = taskTwoList.getExactlyOne();
            ArtifactReadable currentUser = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
            UserId userId = UserId.valueOf(currentUser.getId());
            TransactionBuilder linkTaskTx =
               txFactory.createTransaction(CoreBranches.COMMON, userId, "Link Tasks From Task Page");
            linkTaskTx.relate(taskOne, AtsRelationTypes.TaskLink_From, taskTwo);
            linkTaskTx.commit();
         }

         List<TeamWorkFlowArtifact> listTras = new ArrayList<TeamWorkFlowArtifact>();

         ResultSet<ArtifactReadable> result =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
         ArtifactReadable readableArtifact = result.getExactlyOne();
         CustomizedTeamWorkFlowArtifact ar = new CustomizedTeamWorkFlowArtifact();
         CustomizedTeamWorkFlowArtifactLoader.copyArtifactReadbleToTransferableArtifactWithoutRelation(readableArtifact,
            ar);

         String projectGuid =
            readableArtifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getGuid();
         System.out.println(projectGuid);

         String changes = String.format("New Task \"%s\" is created by %s on %s", readableArtifact.getName(),
            ICTeamMailNotifier.getUserNameByUserId(createdUserId), DateUtil.getMMDDYY(new Date()));
         String guid = readableArtifact.getGuid();
         String rapLink = url + "/icteam-web/#/dashboard/" + projectGuid + "/" + guid;
         System.out.println(rapLink);
         sendMail(orcsApi, createdUserId, assignees, readableArtifact, estimatedDate, ICTeamNotifyType.Created, changes,
            rapLink);
         json1 = gson.toJson(ar);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Method called locally to get all the Team Members and leaders of a task
    *
    * @param projectGuid {@link String} Guid of the project to fecth the assigness
    * @param guid {@link String} Guid of the task to fecth the current state string
    * @return assigneeList {@link List<ITransferableArtifact>} List of assigness for a task
    */
   public List<ITransferableArtifact> getAssigneesForCombo(String projectGuid, String guid) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         ArtifactReadable teamWFArtifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
            Long.valueOf(guid)).getResults().getExactlyOne();
         ArtifactReadable projectArtifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
            Long.valueOf(projectGuid)).getResults().getExactlyOne();
         AttributeReadable<Object> currentStateR =
            teamWFArtifact.getAttributes(AtsAttributeTypes.CurrentState).getAtMostOneOrNull();
         String currentStateStr = (String) currentStateR.getValue();
         String[] split = currentStateStr.split(";");
         String currentState = split[0];
         List<ITransferableArtifact> assignees = new ArrayList<ITransferableArtifact>();

         if (!currentState.equals("Completed") && !currentState.equals("Cancelled")) {
            assignees = CommonUtil.getBasicAssigneesInfo(currentStateStr);
         }

         if (currentState.equals("Completed")) {
            assignees.add(CommonUtil.getBasicCompletedByUser(teamWFArtifact));
         }

         if (currentState.equals("Cancelled")) {
            assignees.add(CommonUtil.getBasicCancelledBy(teamWFArtifact));
         }

         List<ArtifactReadable> userArtifactList = new ArrayList<ArtifactReadable>();
         ResultSet<ArtifactReadable> teams = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andRelatedTo(AtsRelationTypes.ProjectToTeamDefinition_Project,
               projectArtifact).getResults();

         if ((teams != null) && (teams.size() > 0)) {
            for (ArtifactReadable team : teams) {
               ResultSet<ArtifactReadable> artifacts = team.getRelated(AtsRelationTypes.TeamLead_Lead);

               for (ArtifactReadable userArtifact : artifacts) {
                  userArtifactList.add(userArtifact);
               }

               ResultSet<ArtifactReadable> related = team.getRelated(AtsRelationTypes.TeamMember_Member);

               for (ArtifactReadable userArtifact : related) {
                  userArtifactList.add(userArtifact);
               }
            }
         }

         ResultSet<ArtifactReadable> teams1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andRelatedTo(AtsRelationTypes.ProjectToTeamDefinition_Project,
               projectArtifact).getResults();

         if ((teams1 != null) && (teams1.size() > 0)) {
            for (ArtifactReadable team : teams1) {
               ResultSet<ArtifactReadable> artifacts = team.getRelated(AtsRelationTypes.TeamLead_Lead);

               for (ArtifactReadable userArtifact : artifacts) {
                  userArtifactList.add(userArtifact);
               }

               ResultSet<ArtifactReadable> related = team.getRelated(AtsRelationTypes.TeamMember_Member);

               for (ArtifactReadable userArtifact : related) {
                  userArtifactList.add(userArtifact);
               }
            }
         }

         List<ITransferableArtifact> assigneeList = new ArrayList<ITransferableArtifact>();

         for (ArtifactReadable user : userArtifactList) {
            TransferableArtifact userTemp = new TransferableArtifact();
            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, userTemp);
            userTemp.putAttributes(CoreAttributeTypes.UserId.toString(),
               Arrays.asList(user.getAttributes(CoreAttributeTypes.UserId).getExactlyOne().toString()));
            assigneeList.add(userTemp);
         }

         for (ITransferableArtifact assignee : assignees) {
            assigneeList.add(assignee);
         }

         Object[] st = assigneeList.toArray();

         for (Object s : st) {
            if (assigneeList.indexOf(s) != assigneeList.lastIndexOf(s)) {
               assigneeList.remove(assigneeList.lastIndexOf(s));
            }
         }

         return assigneeList;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Link two tasks
    *
    * @param json {@link String} Details of two tasks to be linked
    * @return json1 {@link String} Linked tasks
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("linkTasks")
   public String linkTasks(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getUuid();
         List<ITransferableArtifact> artifacts = new ArrayList<ITransferableArtifact>();
         List<String> idList = artifact.getAttributes("TaskId");
         List<String> nameList = artifact.getAttributes("TaskName");
         ResultSet<ArtifactReadable> result = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andUuid(Long.valueOf(taskGuid)).getResults();
         ArtifactReadable taskOne = result.getExactlyOne();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
            UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
            "Link Tasks");
         QueryBuilder query =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.TeamWorkflow);
         ResultSet<ArtifactReadable> results = query.getResults();

         for (ArtifactReadable artifactReadable : results) {
            if (artifactReadable.getGuid() != taskGuid) {
               TransferableArtifact trans = new TransferableArtifact();
               ResultSet<ArtifactReadable> related =
                  artifactReadable.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project);

               if ((related.size() > 0) && !related.isEmpty()) {
                  ArtifactReadable project = related.getExactlyOne();
                  ResultSet<? extends AttributeReadable<Object>> attributes =
                     artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage);

                  if ((attributes.size() > 0) && !attributes.isEmpty()) {
                     String taskId = attributes.getExactlyOne().toString();
                     taskId =
                        project.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString() + "-" + taskId;

                     String taskName = artifactReadable.getName();

                     if (idList.get(0).toString().equalsIgnoreCase(
                        taskId) && nameList.get(0).toString().equalsIgnoreCase(taskName)) {
                        tx.relate(taskOne, AtsRelationTypes.TaskLink_From, artifactReadable);
                     }

                     artifacts.add(trans);
                  }
               }
            }
         }

         tx.commit();

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         container.addAll(artifacts);
         json1 = gson.toJson(container);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Gets the tasks details for linking
    *
    * @param json {@link String}
    * @return json1 {@link String}
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("taskInfoForLinking")
   public String getTaskInfoForLinking(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getUuid();
         List<ITransferableArtifact> artifacts = new ArrayList<ITransferableArtifact>();
         List<String> filterList = artifact.getAttributes("filter");
         List<String> projectFilterList = artifact.getAttributes("project");
         boolean addTask = true;
         String projectFilter = "";
         String typeFilter = "";

         for (String filter : projectFilterList) {
            String[] splited = filter.split("\\s+");

            if (splited.length > 1) {
               filter = splited[1];
               projectFilter = filter;
            } else {
               projectFilter = filter;
            }
         }

         for (String filter : filterList) {
            String[] splited = filter.split("\\s+");

            if (splited.length > 1) {
               filter = splited[1];
               typeFilter = filter;
            } else {
               typeFilter = filter;
            }
         }

         ResultSet<ArtifactReadable> projectReadable =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               AtsArtifactTypes.AgileProject).andNameEquals(projectFilter).getResults();
         ResultSet<ArtifactReadable> taskList = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andRelatedTo(AtsRelationTypes.ProjectToTeamWorkFlow_Project,
               projectReadable.getExactlyOne()).getResults();

         for (ArtifactReadable artifactReadable : taskList) {
            if (!(artifactReadable.getGuid().equals(taskGuid))) {
               addTask = true;

               if (typeFilter.equalsIgnoreCase(
                  artifactReadable.getAttributes(AtsAttributeTypes.ChangeType).getExactlyOne().toString())) {
                  TransferableArtifact trans = new TransferableArtifact();
                  ResultSet<ArtifactReadable> taskLink = artifactReadable.getRelated(AtsRelationTypes.TaskLink_To);
                  ResultSet<ArtifactReadable> linktask = artifactReadable.getRelated(AtsRelationTypes.TaskLink_From);

                  if (!taskLink.isEmpty() && (taskLink.size() > 0)) {
                     for (ArtifactReadable artifactReadable2 : taskLink) {
                        if (artifactReadable2.getGuid().equals(taskGuid)) {
                           addTask = false;
                        }
                     }
                  }

                  if (!linktask.isEmpty() && (linktask.size() > 0)) {
                     for (ArtifactReadable artifactReadable2 : linktask) {
                        if (artifactReadable2.getGuid().equals(taskGuid)) {
                           addTask = false;
                        }
                     }
                  } else {
                     String taskId =
                        artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
                     taskId = projectReadable.getExactlyOne().getAttributes(
                        AtsAttributeTypes.Shortname).getExactlyOne().toString() + "-" + taskId;

                     String taskName = artifactReadable.getName();
                     trans.putAttributes("TaskId", Arrays.asList(taskId));
                     trans.putAttributes("TaskName", Arrays.asList(taskName));
                     artifacts.add(trans);
                  }

                  if (addTask) {
                     String taskId =
                        artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
                     taskId = projectReadable.getExactlyOne().getAttributes(
                        AtsAttributeTypes.Shortname).getExactlyOne().toString() + "-" + taskId;

                     String taskName = artifactReadable.getName();
                     trans.putAttributes("TaskId", Arrays.asList(taskId));
                     trans.putAttributes("TaskName", Arrays.asList(taskName));
                     artifacts.add(trans);
                  }
               }
            }
         }

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         Object[] st = artifacts.toArray();

         for (Object s : st) {
            if (artifacts.indexOf(s) != artifacts.lastIndexOf(s)) {
               artifacts.remove(artifacts.lastIndexOf(s));
            }
         }

         container.addAll(artifacts);
         json1 = gson.toJson(container);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Gest the projects of tasks to be linked
    *
    * @param json {@link String}
    * @return json1 {@link String}
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getAllProjectsForTaskLinking")
   public String getAllProjectsForTaskLinking(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getUuid();
         Set<TransferableArtifact> userProjectList = new HashSet();
         ResultSet<ArtifactReadable> projects = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.AgileProject).getResults();

         for (ArtifactReadable project : projects) {
            TransferableArtifact ar = new TransferableArtifact();
            TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(project, ar);
            userProjectList.add(ar);
         }

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         List<ITransferableArtifact> selectedProjects = new ArrayList<ITransferableArtifact>();
         selectedProjects.addAll(userProjectList);
         container.setArtifactList(selectedProjects);
         json1 = gson.toJson(container);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Get the project details with given related Task
    *
    * @param json {@link String} contains the uuid of the task
    * @return json1 {@link String} contains the project name related to the given task
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getselectedProject")
   public String getselectedProject(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getUuid();
         ResultSet<ArtifactReadable> task = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andUuid(Long.valueOf(taskGuid)).getResults();
         String projectName =
            task.getExactlyOne().getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne().getName();
         TransferableArtifact trans = new TransferableArtifact();
         trans.putAttributes("Project", Arrays.asList(projectName));

         List<ITransferableArtifact> artifacts = new ArrayList<ITransferableArtifact>();
         artifacts.add(trans);

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         container.addAll(artifacts);
         json1 = gson.toJson(container);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Fetches all the linked tasks to a given task Uuid
    *
    * @param json {@link String} contains the uuid of the task to fetch its linked tasks
    * @return json1 {@link String} All the tasks that are liked to to a given task
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getAllTasksLinked")
   public String getAllTasksLinked(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getUuid();
         ResultSet<ArtifactReadable> task = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andGuid(taskGuid).getResults();
         List<TeamWorkFlowArtifact> listTras = new ArrayList<TeamWorkFlowArtifact>();

         for (ArtifactReadable artifactReadable : task) {
            ResultSet<ArtifactReadable> related = artifactReadable.getRelated(AtsRelationTypes.TaskLink_From);

            for (ArtifactReadable artifactReadable2 : related) {
               TeamWorkFlowArtifact ar = new TeamWorkFlowArtifact();
               TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(artifactReadable2, ar);

               ArtifactReadable project =
                  artifactReadable2.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne();
               String shortName = project.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString();
               String workpackage =
                  artifactReadable2.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
               String taskId = shortName + "-" + workpackage;
               ar.putAttributes("TaskId", Arrays.asList(taskId));
               listTras.add(ar);
            }

            ResultSet<ArtifactReadable> related2 = artifactReadable.getRelated(AtsRelationTypes.TaskLink_To);

            for (ArtifactReadable artifactReadable2 : related2) {
               TeamWorkFlowArtifact ar = new TeamWorkFlowArtifact();
               TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(artifactReadable2, ar);

               ArtifactReadable project =
                  artifactReadable2.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne();
               String shortName = project.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString();
               String workpackage =
                  artifactReadable2.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
               String taskId = shortName + "-" + workpackage;
               ar.putAttributes("TaskId", Arrays.asList(taskId));
               listTras.add(ar);
            }
         }

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         Object[] st = listTras.toArray();

         for (Object s : st) {
            if (listTras.indexOf(s) != listTras.lastIndexOf(s)) {
               listTras.remove(listTras.lastIndexOf(s));
            }
         }

         container.setListTeamWorkFlow(listTras);
         json1 = gson.toJson(container);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Used to de-link the linked tasks
    *
    * @param json {@link String} linked tasks Uuid
    * @return json1 {@link String}
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("deleteLink")
   public String deleteLink(String json) {
      String json1 = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         String taskGuid = artifact.getParentGuid();
         String secondTaskGuid = artifact.getUuid();
         ResultSet<ArtifactReadable> task1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andUuid(Long.valueOf(taskGuid)).getResults();
         ResultSet<ArtifactReadable> task2 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).andUuid(Long.valueOf(secondTaskGuid)).getResults();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON,
            UserId.valueOf(CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser()).getId()),
            "Unrelate Tasks");
         tx.unrelate(task1.getExactlyOne(), AtsRelationTypes.TaskLink_To, task2.getExactlyOne());
         tx.unrelate(task1.getExactlyOne(), AtsRelationTypes.TaskLink_From, task2.getExactlyOne());
         tx.commit();
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return json1;
   }

   /**
    * Limit range of ids from 00001 to 99999
    *
    * @param runningNumber {@link Integer}
    * @return taskCountAttr {@link String}
    * @throws OseeCoreException
    */
   private static String setAndVerifyRange(final int runningNumber) throws OseeCoreException {
      if (runningNumber > 99999) {
         throw new OseeCoreException("Sequence value exceeded 99999");
      }

      String taskCountAttr = String.valueOf(runningNumber);

      return taskCountAttr;
   }
}
