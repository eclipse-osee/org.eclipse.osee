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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
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
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * Release Resource to return Releases and Release related queries
 *
 * @author Ajay Chandrahasan
 */
@Path("Releases")
public class ReleasesResource extends AbstractConfigResource {

   public ReleasesResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.Version, atsApi, orcsApi);
   }

   /**
    * This function is used to get a Sprint by Uuid
    *
    * @param sprintUuid String Uuid of Sprint
    * @return returnJson String Sprint artifact
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("getSprintByUuid")
   public String getSprintByUuid(final String sprintUuid) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(sprintUuid, TransferableArtifact.class);

         if (artifact.getUuid() != null) {
            ResultSet<ArtifactReadable> list =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Version).andUuid(
                  Long.valueOf(artifact.getUuid())).getResults();
            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();

            for (ArtifactReadable artifactReadable : list) {
               TransferableArtifact ar = new TransferableArtifact();
               TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, ar);
               ar.putAttributes(AtsAttributeTypes.Released.getName(),
                  Arrays.asList(artifactReadable.getSoleAttributeAsString(AtsAttributeTypes.Released)));
               listTras.add(ar);

               TransferableArtifactsContainer container = new TransferableArtifactsContainer();
               container.addAll(listTras);

               String returnJson = gson.toJson(container);

               return returnJson;
            }
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * This function is used to create a new Release artifact (Milestone or Sprint) with all the attribute values sent
    * from Client
    *
    * @param json String which contains artifact and its attributes values
    * @return json String with the created artifact,its attribute values if no duplicate release present or else Failure
    * status is returned
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("CreateReleaseWeb")
   public String createReleaseWeb(final String json) {
      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         boolean status = true;
         boolean isMileStone = false;
         ArtifactId childArtifact = null;
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         ArtifactReadable artifactReadable1 = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(artifactReadable1.getId());
         TransactionBuilder tx =
            txFactory.createTransaction(CommonUtil.getCommonBranch(orcsApi), userId, "Add New Release");
         String parentGuid = artifact.getParentGuid();
         ArtifactReadable parentRelease = null;
         ResultSet<ArtifactReadable> projectReadable = null;

         if (parentGuid != null) {
            parentRelease = orcsApi.getQueryFactory().fromBranch(CommonUtil.getCommonBranch()).andUuid(
               Long.valueOf(parentGuid)).getResults().getOneOrNull();

            List<ArtifactReadable> sprintList = parentRelease.getChildren();

            for (ArtifactReadable sprint : sprintList) {
               if (sprint.getName().equalsIgnoreCase(artifact.getName())) {
                  status = false;
                  isMileStone = false;

                  break;
               }
            }
         } else {
            String uuid = AtsRelationTypes.ProjectToVersion_Project.getGuid().toString();
            String type1 = AtsRelationTypes.ProjectToVersion_Project.getRelationType().toString();
            String side = AtsRelationTypes.ProjectToVersion_Project.getSide().toString();
            String key = "RelationTypeSide - uuid=[" + uuid + "] type=[" + type1 + "] side=[" + side + "]";
            ITransferableArtifact project = null;
            if (artifact.getRelatedArtifacts(key) != null) {
               project = artifact.getRelatedArtifacts(key).get(0);
            }
            if (project != null) {
               projectReadable = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
                  Long.valueOf(project.getUuid())).getResults();

               ResultSet<ArtifactReadable> mileStoneList =
                  projectReadable.getExactlyOne().getRelated(AtsRelationTypes.ProjectToVersion_Version);

               for (ArtifactReadable mileStone : mileStoneList) {
                  if (mileStone.getName().equalsIgnoreCase(artifact.getName())) {
                     isMileStone = true;
                     status = false;

                     break;
                  }
               }
            }
         }

         if (status) {
            childArtifact = tx.createArtifact(AtsArtifactTypes.Version, artifact.getName());

            List<String> attributes = artifact.getAttributes(AtsAttributeTypes.ReleaseDate.toString());
            List<String> startDateAttribute = artifact.getAttributes(AtsAttributeTypes.StartDate.toString());

            if ((attributes != null) && (attributes.size() > 0)) {
               SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
               Date date = null;

               try {
                  date = formatter.parse(attributes.get(0));
               } catch (ParseException e) {
                  e.printStackTrace();
               }

               tx.setSoleAttributeValue(childArtifact, AtsAttributeTypes.ReleaseDate, date);

               Date startDate = null;
               try {
                  startDate = formatter.parse(startDateAttribute.get(0));
               } catch (ParseException e) {
                  e.printStackTrace();
               }

               tx.setSoleAttributeValue(childArtifact, AtsAttributeTypes.StartDate, startDate);
            }

            List<String> baselineBranchGuid = artifact.getAttributes(AtsAttributeTypes.BaselineBranchGuid.toString());

            if ((baselineBranchGuid != null) && (baselineBranchGuid.size() > 0)) {
               tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.BaselineBranchGuid,
                  baselineBranchGuid.get(0));
            }

            List<String> description = artifact.getAttributes(AtsAttributeTypes.Description.toString());

            if ((description != null) && (description.size() > 0)) {
               tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Description, description.get(0));
            }

            tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.Released, "false");
            tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.AllowCreateBranch, "true");
            tx.setSoleAttributeFromString(childArtifact, AtsAttributeTypes.AllowCommitBranch, "true");

            if (parentGuid != null) {
               tx.relate(parentRelease, CoreRelationTypes.DefaultHierarchical_Child, childArtifact);
            } else {
               // Relation to Project and Team should be only for MileStone and
               // Not Sprint
               if ((projectReadable != null) && (projectReadable.size() == 1)) {
                  tx.relate(projectReadable.getExactlyOne(), AtsRelationTypes.ProjectToVersion_Project, childArtifact);
               }

               String uuid = AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition.getGuid().toString();
               String type1 = AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition.getRelationType().toString();
               String side = AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition.getSide().toString();
               String key = "RelationTypeSide - uuid=[" + uuid + "] type=[" + type1 + "] side=[" + side + "]";
               List<ITransferableArtifact> relatedArtifacts = artifact.getRelatedArtifacts(key);

               for (ITransferableArtifact transferableArtifact : relatedArtifacts) {
                  ResultSet<ArtifactReadable> result =
                     orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andUuid(
                        Long.valueOf(transferableArtifact.getUuid())).getResults();

                  if ((result != null) && (result.size() == 1)) {
                     tx.relate(result.getExactlyOne(), AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition,
                        childArtifact);
                  }
               }
            }

            tx.commit();
         }

         TransferableArtifact ar = new TransferableArtifact();

         if (status) {
            ar.putAttributes(CommonConstants.STATUS, Arrays.asList(CommonConstants.SUCCESS));

            ResultSet<ArtifactReadable> result =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(childArtifact).getResults();
            ArtifactReadable readableArtifact = result.getExactlyOne();
            ar.setName(readableArtifact.getName());
            TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(readableArtifact, ar);
         }

         if (!status) {
            ar.putAttributes(CommonConstants.STATUS, Arrays.asList(CommonConstants.FAILURE));

            if (isMileStone) {
               ar.putAttributes("Message", Arrays.asList("Milestone name already exists!!!!"));
            } else {
               ar.putAttributes("Message", Arrays.asList("Sprint name already exists in this Milestone!!!!"));
            }
         }

         TransferableArtifactsContainer container = new TransferableArtifactsContainer();
         container.addAll(Arrays.asList((ITransferableArtifact) ar));

         String json1 = gson.toJson(container);

         return json1;
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to update a release
    *
    * @param json String artifact with all to be updated attribute values
    * @return serialize String Updated Release artifact
    */
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("updateReleaseWeb")
   public String updateReleaseWeb(final String json) {
      try {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         String serialize = null;
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         ArtifactReadable artifactReadable1 = CommonUtil.getCurrentUser(orcsApi, artifact.getCurrentLoggedInUser());
         UserId userId = UserId.valueOf(artifactReadable1.getId());
         TransactionBuilder tx =
            txFactory.createTransaction(CommonUtil.getCommonBranch(orcsApi), userId, "Update Release Artifact");
         String attribute = artifact.getUuid();

         if (attribute != null) {
            ResultSet<ArtifactReadable> list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
               AtsArtifactTypes.Version).getResults();

            for (ArtifactReadable artifactReadable : list) {
               if (artifactReadable.getIdString().equals(attribute)) {
                  List<String> attributes = artifact.getAttributes(AtsAttributeTypes.ReleaseDate.toString());
                  List<String> startDateAttribute = artifact.getAttributes(AtsAttributeTypes.StartDate.toString());

                  if ((attributes != null) && (attributes.size() > 0)) {
                     SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                     Date date = null;

                     try {
                        date = formatter.parse(attributes.get(0));
                     } catch (ParseException e) {
                        e.printStackTrace();
                     }

                     tx.setSoleAttributeValue(artifactReadable, AtsAttributeTypes.ReleaseDate, date);

                     Date startDate = null;
                     try {
                        startDate = formatter.parse(startDateAttribute.get(0));
                     } catch (ParseException e) {
                        e.printStackTrace();
                     }

                     tx.setSoleAttributeValue(artifactReadable, AtsAttributeTypes.StartDate, startDate);
                  }

                  List<String> baselineBranchGuid =
                     artifact.getAttributes(AtsAttributeTypes.BaselineBranchGuid.toString());

                  if ((baselineBranchGuid != null) && (baselineBranchGuid.size() > 0)) {
                     tx.setSoleAttributeFromString(artifactReadable, AtsAttributeTypes.BaselineBranchGuid,
                        baselineBranchGuid.get(0));
                  }

                  List<String> description = artifact.getAttributes(AtsAttributeTypes.Description.toString());

                  if ((description != null) && (description.size() > 0)) {
                     tx.setSoleAttributeFromString(artifactReadable, AtsAttributeTypes.Description, description.get(0));
                  }

                  List<String> name = artifact.getAttributes(CoreAttributeTypes.Name.toString());

                  if ((name != null) && (name.size() > 0)) {
                     tx.setSoleAttributeFromString(artifactReadable, CoreAttributeTypes.Name, name.get(0));
                  }

                  List<String> released = artifact.getAttributes(AtsAttributeTypes.Released.toString());

                  if ((released != null) && (released.size() > 0)) {
                     tx.setSoleAttributeFromString(artifactReadable, AtsAttributeTypes.Released, released.get(0));
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
                  serialize = serializer.deepSerialize(container);

                  break;
               }
            }

            return serialize;
         }
      } catch (JsonSyntaxException e) {
         e.printStackTrace();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to mark a release (close a release)
    *
    * @param json String uuid of a release
    * @return json1 String release is marked as released if no associated tasks
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("releaseReleaseWeb")
   public String releaseReleaseWeb(final String json) {
      String CanRelease = "true";
      OrcsApi orcsApi = OseeCoreData.getOrcsApi();
      String serialize = null;
      Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
         new InterfaceAdapter<TransferableArtifact>()).create();
      TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
      String attributes = artifact.getUuid();

      if (attributes != null) {
         try {
            ResultSet<ArtifactReadable> list =
               orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.Version).andUuid(
                  Long.valueOf(attributes)).getResults();
            ArtifactReadable version = list.getOneOrNull();

            if (version != null) {
               ResultSet<ArtifactReadable> teamWorkFlows =
                  orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
                     AtsArtifactTypes.TeamWorkflow).andRelatedTo(
                        AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow, version).getResults();
               ResultSet<ArtifactReadable> relatedTeamWF =
                  version.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow);

               if ((relatedTeamWF != null) && (relatedTeamWF.size() > 0)) {
                  for (ArtifactReadable teamWorkFlow : relatedTeamWF) {
                     String state = teamWorkFlow.getSoleAttributeAsString(AtsAttributeTypes.CurrentState);

                     if (!(state.contains("Cancelled") || state.contains("Completed"))) {
                        CanRelease = "false";

                        break;
                     }
                  }
               }
            }
         } catch (OseeCoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      serialize = "";

      if (CanRelease.equals("true")) {
         serialize = updateReleaseWeb(json);
      }

      TransferableArtifact reportArt = new TransferableArtifact();
      reportArt.putAttributes("isReleasable", Arrays.asList(CanRelease));

      TransferableArtifactsContainer container = new TransferableArtifactsContainer();
      List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
      list.add(reportArt);
      container.setArtifactList(list);

      String json1 = gson.toJson(container);

      return json1;
   }

   /**
    * Rest method to get all the close releases associated to a project
    *
    * @param projectGuid String guid of the project
    * @return json String close releases related to project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("closeReleaseWeb")
   public String getAssociatedsCloseReleasesForProjectWeb(final String projectGuid) {
      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         String attribute = projectGuid;

         if (attribute != null) {
            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();
            ResultSet<ArtifactReadable> mileStones = CommonUtil.getReleasesForProject(attribute);

            for (ArtifactReadable artifactReadable : mileStones) {
               ResultSet<ArtifactReadable> relatedSprints =
                  artifactReadable.getRelated(CoreRelationTypes.DefaultHierarchical_Child);

               if ((relatedSprints != null) && (relatedSprints.size() > 0)) {
                  for (ArtifactReadable artifactReadable2 : relatedSprints) {
                     ResultSet<? extends AttributeReadable<Object>> releasedAttribute =
                        artifactReadable2.getAttributes(AtsAttributeTypes.Released);

                     if ((releasedAttribute != null) && (releasedAttribute.size() > 0)) {
                        for (AttributeReadable<Object> attributeReadable : releasedAttribute) {
                           boolean releasedAttributeString = (Boolean) attributeReadable.getValue();

                           if (releasedAttributeString == true) {
                              TransferableArtifact ar = new TransferableArtifact();
                              TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable2, ar);

                              String displayName = artifactReadable.getName() + "-->" + artifactReadable2.getName();
                              ar.putAttributes("displayName", Arrays.asList(displayName));
                              listTras.add(ar);
                           }
                        }
                     }
                  }
               }
            }

            TransferableArtifactsContainer container = new TransferableArtifactsContainer();
            container.addAll(listTras);

            String json = gson.toJson(container);

            return json;
         }

         return null;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Rest method to get all the open releases associated to a project
    *
    * @param projectGuid String guid of the project
    * @return json String open releases related to project
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("openReleaseWeb")
   public String getAssociatedOpenReleasesForProjectWeb(final String projectGuid) {
      try {
         Gson gson = new GsonBuilder().registerTypeAdapter(ITransferableArtifact.class,
            new InterfaceAdapter<TransferableArtifact>()).create();
         String attribute = projectGuid;

         if (attribute != null) {
            List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();
            ResultSet<ArtifactReadable> mileStones = CommonUtil.getReleasesForProject(attribute);

            for (ArtifactReadable artifactReadable : mileStones) {
               ResultSet<ArtifactReadable> relatedSprints =
                  artifactReadable.getRelated(CoreRelationTypes.DefaultHierarchical_Child);

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

                              String displayName = artifactReadable.getName() + "-->" + artifactReadable2.getName();
                              ar.putAttributes("displayName", Arrays.asList(displayName));
                              listTras.add(ar);
                           }
                        }
                     }
                  }
               }
            }

            TransferableArtifactsContainer container = new TransferableArtifactsContainer();
            container.addAll(listTras);

            String json = gson.toJson(container);

            return json;
         }

         return null;
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }
}
