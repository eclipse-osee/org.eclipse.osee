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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.icteam.common.clientserver.dependent.util.CommonConstants;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * Project Resource to return Projects and Project related queries
 *
 * @author Ajay Chandrahasan
 */
@Path("projects")
public class ProjectsResource extends AbstractConfigResource {

   public ProjectsResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.Project, atsApi, orcsApi);
   }

   /**
    * Rest method to get all the releases associated to a project
    *
    * @param attribute String projectGuid of the project
    * @return serialize String releases related to project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("releaseWeb")
   public String getAssociatedReleasesForProjectWeb(final String projectGuid) {
      String serialize = null;

      try {
         String attribute = projectGuid;

         if (attribute != null) {
            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();
            ResultSet<ArtifactReadable> releases = CommonUtil.getReleasesForProject(attribute);

            for (ArtifactReadable artifactReadable : releases) {
               TransferableArtifact ar = new TransferableArtifact();
               TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(artifactReadable, ar);
               listTras.add(ar);

               ResultSet<? extends AttributeReadable<Object>> attributes =
                  artifactReadable.getAttributes(AtsAttributeTypes.BaselineBranchGuid);

               if (attributes != null) {
                  for (AttributeReadable<Object> attributeReadable : attributes) {
                     String branchGuid = attributeReadable.getValue().toString();
                     Branch andGuid =
                        (Branch) OseeCoreData.getOrcsApi().getQueryFactory().branchQuery().getResultsAsId().getList().get(
                           Integer.valueOf(branchGuid));
                     ar.putAttributes("BaseLineBranchName", Arrays.asList(andGuid.getName()));
                  }
               }
            }

            TransferableArtifactsContainer container = new TransferableArtifactsContainer();
            container.addAll(listTras);

            JSONSerializer serializer = new JSONSerializer();
            serialize = serializer.deepSerialize(container);
         }

         return serialize;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to get all the components associated to a project
    *
    * @param attribute String guid of the project
    * @return serialize String components related to project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("componentforproject")
   public String getAssociatedComponentForProject(final String attribute) {
      String serialize = null;

      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();

         if (attribute != null) {
            ArtifactReadable project =
               CommonUtil.getArtifactFromIdExcludingDeleted(attribute, CommonUtil.getCommonBranch(orcsApi), orcsApi);
            ResultSet<ArtifactReadable> teamArtifacts =
               project.getRelated(AtsRelationTypes.ProjectToTeamDefinition_TeamDefinition);
            List<ArtifactReadable> listTeam = new ArrayList<ArtifactReadable>();

            for (ArtifactReadable teamArtifact : teamArtifacts) {
               listTeam.add(teamArtifact);
            }

            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

            if (!listTeam.isEmpty()) {
               ResultSet<ArtifactReadable> components =
                  orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                     AtsArtifactTypes.ActionableItem).andRelatedTo(AtsRelationTypes.TeamActionableItem_TeamDefinition,
                        listTeam).getResults();

               for (ArtifactReadable artifactReadable : components) {
                  TransferableArtifact ar = new TransferableArtifact();
                  TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(artifactReadable, ar);
                  listTras.add(ar);
               }
            }

            TransferableArtifactsContainer container = new TransferableArtifactsContainer();
            container.addAll(listTras);

            JSONSerializer serializer = new JSONSerializer();
            serialize = serializer.deepSerialize(container);
         }

         return serialize;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * This function is used to create a new Project artifact with all the attribute values sent from Client
    *
    * @param json String which contains artifact and its attributes values
    * @return json String with the created artifact,its attribute values if no duplicate project name and/or shortname
    * present or else Failure status is returned
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("createproject")
   public String createProject(final String json) {
      String serialize = null;

      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         ArtifactToken childArtifact = null;
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         ArtifactReadable currentUser = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(currentUser.getId());
         TransactionBuilder tx = txFactory.createTransaction(CoreBranches.COMMON, userId, "Add New Project");
         boolean status = true;
         String projectType = artifact.getArtifactType();
         ResultSet<ArtifactReadable> results = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.AgileProject).andNameEquals(artifact.getName()).getResults();
         ResultSet<ArtifactReadable> shortnameResults =
            orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               AtsArtifactTypes.AgileProject).getResults();
         List<String> shortName = artifact.getAttributes("ShortName");

         for (ArtifactReadable artifactReadable : shortnameResults) {
            if (!artifactReadable.getAttributes(AtsAttributeTypes.Shortname).isEmpty()) {
               if (artifactReadable.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString().equals(
                  shortName.get(0))) {
                  status = false;

                  break;
               }
            }
         }

         TransferableArtifactsContainer artifactsContainer = new TransferableArtifactsContainer();

         if (results.isEmpty() && status) {
            if (AtsArtifactTypes.AgileProject.getName().equals(projectType)) {
               childArtifact = tx.createArtifact(AtsArtifactTypes.AgileProject, artifact.getName());
            } else {
               childArtifact = tx.createArtifact(AtsArtifactTypes.Project, artifact.getName());
            }

            Map<String, List<String>> attributes = artifact.getAttributes();
            Set<Entry<String, List<String>>> entrySet = attributes.entrySet();

            for (Entry<String, List<String>> entry : entrySet) {
               String type = entry.getKey();
               List<String> value = entry.getValue();

               if (!type.equalsIgnoreCase("ShortName")) {
                  for (String string : value) {
                     tx.setSoleAttributeFromString(childArtifact,
                        AttributeTypeToken.valueOf(Long.parseLong(type), "Attribute"), string);
                  }
               } else {
                  tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Shortname, shortName.get(0));
               }
            }

            String uuid = CoreRelationTypes.Users_User.getGuid().toString();
            String type = CoreRelationTypes.Users_User.getRelationType().toString();
            String side = CoreRelationTypes.Users_User.getSide().toString();
            String key = "RelationTypeSide - uuid=[" + uuid + "] type=[" + type + "] side=[" + side + "]";
            List<ITransferableArtifact> createdUserList = artifact.getRelatedArtifacts(key);

            for (ITransferableArtifact user : createdUserList) {
               ResultSet<ArtifactReadable> list =
                  orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andGuid(
                     user.getUuid()).getResults();

               tx.relate(childArtifact, CoreRelationTypes.Users_User, list.getExactlyOne());
               tx.relate(childArtifact, AtsRelationTypes.ProjectToUser, list.getExactlyOne());
            }

            ResultSet<ArtifactReadable> result1 = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               CoreArtifactTypes.RootArtifact).getResults();
            tx.addChild(result1.getExactlyOne(), childArtifact);
            tx.setSoleAttributeFromString(childArtifact,
               AttributeTypeToken.valueOf(AtsAttributeTypes.TaskCountForProject.getId(), "Attribute"),
               String.valueOf(0));
            tx.commit();

            ResultSet<ArtifactReadable> result =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
            ArtifactReadable readableArtifact = result.getExactlyOne();
            TransferableArtifact ar = new TransferableArtifact();
            ar.putAttributes(CommonConstants.STATUS, Arrays.asList(CommonConstants.SUCCESS));
            ar.setName(readableArtifact.getName());
            TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(readableArtifact, ar);

            List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
            list.add(ar);
            artifactsContainer.setArtifactList(list);
         } else {
            TransferableArtifact ar = new TransferableArtifact();
            ar.putAttributes(CommonConstants.STATUS, Arrays.asList(CommonConstants.FAILURE));

            if (!status) {
               ar.putAttributes("Message", Arrays.asList("ShortName already exists!!!!"));
            }

            if (!results.isEmpty()) {
               ar.putAttributes("Message", Arrays.asList("Project Name already exists!!!!"));
            }

            if (!status && !results.isEmpty()) {
               ar.putAttributes("Message", Arrays.asList("Project Name and Short Name already exists!!!!"));
            }

            List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
            list.add(ar);
            artifactsContainer.setArtifactList(list);
         }

         JSONSerializer serializer = new JSONSerializer();
         serialize = serializer.deepSerialize(artifactsContainer);
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return serialize;
   }

   /**
    * Rest method to return specific projects for a given user uuid
    *
    * @param uuid String Uuid of a User
    * @return serialize String Projects of a user
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("userSpecific")
   public String getUseeSpecificProjects(final String uuid) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();

         if (uuid != null) {
            ResultSet<ArtifactReadable> userList =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andGuid(
                  uuid).getResults();
            ArtifactReadable loggedInUser = userList.getExactlyOne();
            ResultSet<ArtifactReadable> projects =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                  AtsArtifactTypes.Project).getResults();
            Set<TransferableArtifact> userProjectList = new HashSet();

            for (ArtifactReadable project : projects) {
               if (!project.getName().endsWith(CommonConstants.MANGLED_NAME)) {
                  ResultSet<ArtifactReadable> createdUsers = project.getRelated(CoreRelationTypes.Users_User);
                  boolean addProject = false;

                  for (ArtifactReadable createdUser : createdUsers) {
                     if (createdUser.equals(loggedInUser)) {
                        addProject = true;
                     }
                  }

                  ResultSet<ArtifactReadable> related = project.getRelated(AtsRelationTypes.ProjectToUser_User);

                  for (ArtifactReadable projectUser : related) {
                     if (projectUser.equals(loggedInUser)) {
                        addProject = true;
                     }
                  }

                  if (addProject) {
                     TransferableArtifact ar = new TransferableArtifact();
                     TranferableArtifactLoader.copyAllInfoToTransferableArtifact(project, ar);
                     userProjectList.add(ar);
                  }
               }
            }

            TransferableArtifactsContainer container = new TransferableArtifactsContainer();
            List<ITransferableArtifact> selectedProjects = new ArrayList<ITransferableArtifact>();
            selectedProjects.addAll(userProjectList);
            container.setArtifactList(selectedProjects);

            JSONSerializer serializer = new JSONSerializer();
            String serialize = serializer.deepSerialize(container);

            return serialize;
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to return a project for a given Uuid
    *
    * @param projectUuid String Uuid of a Project
    * @return serialize String Project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("uuidWeb")
   public String getProjectForUuidWeb(final String projectUuid) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         String attribute = projectUuid;

         if (attribute != null) {
            ResultSet<ArtifactReadable> list =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Project).andUuid(
                  Long.valueOf(attribute)).getResults();
            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

            for (ArtifactReadable artifactReadable : list) {
               if (artifactReadable.getIdString().equals(attribute)) {
                  TransferableArtifact ar = new TransferableArtifact();
                  TranferableArtifactLoader.copyProjectArtifactReadbleToTransferableArtifact(artifactReadable, ar);
                  listTras.add(ar);

                  TransferableArtifactsContainer container = new TransferableArtifactsContainer();
                  container.addAll(listTras);

                  JSONSerializer serializer = new JSONSerializer();
                  String serialize = serializer.deepSerialize(container);

                  return serialize;
               }
            }
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }
}
