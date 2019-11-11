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
package org.eclipse.osee.icteam.web.rest.data.write;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.util.CommonConstants;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * This class is for populating the data to Transferable artifact from ArtifactReadable
 *
 * @author Ajay Chandrahasan
 */
public class TranferableArtifactLoader {

  /**
   * This function sets the values in the transferable artifact from artifact readable object but the Relations will not
   * be filled in this method
   *
   * @param readable ArtifactReadable Object holding the values
   * @param transArtifact is the TransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */
  public static void copyArtifactReadbleToTransferableArtifactWithoutRelations(final ArtifactReadable readable,
      final TransferableArtifact transArtifact)
      throws OseeCoreException {

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        if (null != attributeReadable.getValue()) {
          listTemp.add(attributeReadable.getValue().toString());
        }
        else {
          listTemp.add("");
        }
      }
      transArtifact.putAttributes(iAttributeType.toString(), listTemp);
    }

    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    transArtifact.putAttributes("guid", list2);
    transArtifact.setId(readable.getId());
    transArtifact.setArtifactType(readable.getArtifactType().toString());
    transArtifact.setName(readable.getName());
    transArtifact.setBranchGuid(readable.getBranch().getId());
  }

  /**
   * This function sets the values in the transferable artifact from artifact readable object
   *
   * @param readable ArtifactReadable Object holding the values
   * @param ar is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */
  public static void copyArtifactReadbleToTransferableArtifact(final ArtifactReadable readable,
      final ITransferableArtifact ar)
      throws OseeCoreException {

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        if (null != attributeReadable.getValue()) {
          listTemp.add(attributeReadable.getValue().toString());
        }
        else {
          listTemp.add("");
        }
      }
      ar.putAttributes(iAttributeType.toString(), listTemp);
    }

    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    ar.putAttributes("guid", list2);
    ar.setId(readable.getId());
    ar.setArtifactType(readable.getArtifactType().toString());
    ar.setName(readable.getName());
    ar.setBranchGuid(readable.getBranch().getId());


    Collection<RelationTypeToken> existingRelationTypes = readable.getExistingRelationTypes();
    try {
      for (RelationTypeToken iRelationType : existingRelationTypes) {
    	  RelationTypeSide createRelationTypeSide = RelationTypeSide.create(((RelationTypeToken) iRelationType),
    			  RelationSide.SIDE_B);
        ResultSet<ArtifactReadable> relatedArtifacts = readable.getRelated(createRelationTypeSide);
        if (relatedArtifacts.size() > 0) {
          fillRelations(relatedArtifacts, createRelationTypeSide, iRelationType, ar);
        }


        RelationTypeSide createRelationTypeSide1 = RelationTypeSide.create(((RelationTypeToken) iRelationType),
        		RelationSide.SIDE_A);
        ResultSet<ArtifactReadable> relatedArtifacts1 = readable.getRelated(createRelationTypeSide1);
        if (relatedArtifacts1.size() > 0) {
          fillRelations(relatedArtifacts1, createRelationTypeSide1, iRelationType, ar);
        }

      }
    }
    catch (Exception e) {
      OseeLog.log(org.eclipse.osee.icteam.web.rest.layer.Activator.class, Level.WARNING,
          " Exception while copying relations . Some relation will not be copied ");
    }
  }


  /**
   * This function sets the values in the transferable artifact from projectArtifact readable object
   *
   * @param readable ArtifactReadable Object holding the values
   * @param ar is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */
  public static void copyProjectArtifactReadbleToTransferableArtifact(final ArtifactReadable readable,
      final ITransferableArtifact ar)
      throws OseeCoreException {

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        if (null != attributeReadable.getValue()) {
          listTemp.add(attributeReadable.getValue().toString());
        }
        else {
          listTemp.add("");
        }
      }
      ar.putAttributes(iAttributeType.toString(), listTemp);
    }

    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    ar.putAttributes("guid", list2);
    ar.setId(readable.getId());
    ar.setArtifactType(readable.getArtifactType().toString());
    ar.setName(readable.getName());
    ar.setBranchGuid(readable.getBranch().getId());


    Collection<RelationTypeToken> existingRelationTypes = readable.getExistingRelationTypes();
    try {
      for (RelationTypeToken iRelationType : existingRelationTypes) {

        if (!((RelationTypeToken) iRelationType).getName().equals("ProjectToTeamWorkFlow")) {
        	RelationTypeSide createRelationTypeSide = RelationTypeSide.create(((RelationTypeToken) iRelationType),
        			RelationSide.SIDE_B);
          ResultSet<ArtifactReadable> relatedArtifacts = readable.getRelated(createRelationTypeSide);
          if (relatedArtifacts.size() > 0) {
            fillRelations(relatedArtifacts, createRelationTypeSide, iRelationType, ar);
          }


          RelationTypeSide createRelationTypeSide1 = RelationTypeSide.create(((RelationTypeToken) iRelationType),
        		  RelationSide.SIDE_A);
          ResultSet<ArtifactReadable> relatedArtifacts1 = readable.getRelated(createRelationTypeSide1);
          if (relatedArtifacts1.size() > 0) {
            fillRelations(relatedArtifacts1, createRelationTypeSide1, iRelationType, ar);
          }
        }
      }
    }
    catch (Exception e) {
            OseeLog.log(org.eclipse.osee.icteam.web.rest.layer.Activator.class, Level.WARNING,
          " Exception while copying relations . Some relation will not be copied ");
    }
  }


  /**
   * This function sets the relations in the ITransferableArtifact object
   *
   * @param relatedArtifacts object which holds the relations
   * @param side RelationTypeSide
   * @param relationType the key for the relation map, set in ITransferableArtifact object
   * @param ar ITransferableArtifact object in which the relations will be set from the relatedArtifacts object
   * @throws OseeCoreException
   */
  public static void fillRelations(final ResultSet<ArtifactReadable> relatedArtifacts, final RelationTypeSide side,
      final RelationTypeToken relationType, final ITransferableArtifact ar)
      throws OseeCoreException {
    List<ITransferableArtifact> list1 = new ArrayList<ITransferableArtifact>();
    for (ArtifactReadable artifactReadable : relatedArtifacts) {

      if (artifactReadable.getArtifactType().equals(AtsArtifactTypes.Version)) {
        ArtifactReadable user = OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(artifactReadable.getBranch())
            .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
        TransferableArtifact art = new TransferableArtifact();
        TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifactDup(user, art);
        list1.add(art);
      }
      else {
        ArtifactReadable user = OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(artifactReadable.getBranch())
            .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
        TransferableArtifact art = new TransferableArtifact();
        TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, art);
        list1.add(art);
      }
    }
    ar.putRelations(((RelationTypeToken) relationType).getName() + CommonConstants.RELATION_MAP_KEY_SEPARATOR +
        side.getSide().name(), list1);
  }

  /**
   * This function sets the values in the transferable artifact from Artifact readable object
   *
   * @param user ArtifactReadable Object holding the values
   * @param art is the ITransferableArtifact object in which the values will be set.
   */
  public static void copyArtifactReadbleToTransferableArtifactDup(final ArtifactReadable readable,
      final TransferableArtifact transArtifact) {

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        if (null != attributeReadable.getValue()) {
          listTemp.add(attributeReadable.getValue().toString());
        }
        else {
          listTemp.add("");
        }
      }
      transArtifact.putAttributes(iAttributeType.toString(), listTemp);
    }

    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    transArtifact.putAttributes("guid", list2);
    transArtifact.setId(readable.getId());
    transArtifact.setArtifactType(readable.getArtifactType().toString());
    transArtifact.setName(readable.getName());
    transArtifact.setBranchGuid(readable.getBranch().getId());


    Collection<RelationTypeToken> existingRelationTypes = readable.getExistingRelationTypes();
    try {
      for (RelationTypeToken iRelationType : existingRelationTypes) {
    	  RelationTypeSide createRelationTypeSide = RelationTypeSide.create(((RelationTypeToken) iRelationType),
    			  RelationSide.SIDE_B);
        ResultSet<ArtifactReadable> relatedArtifacts = readable.getRelated(createRelationTypeSide);
        if (relatedArtifacts.size() > 0) {
          fillRelationsdup(relatedArtifacts, createRelationTypeSide, iRelationType, transArtifact);
        }


        RelationTypeSide createRelationTypeSide1 = RelationTypeSide.create(((RelationTypeToken) iRelationType),
        		RelationSide.SIDE_A);
        ResultSet<ArtifactReadable> relatedArtifacts1 = readable.getRelated(createRelationTypeSide1);
        if (relatedArtifacts1.size() > 0) {
          fillRelationsdup(relatedArtifacts1, createRelationTypeSide1, iRelationType, transArtifact);
        }

      }
    }
    catch (Exception e) {
      OseeLog.log(org.eclipse.osee.icteam.web.rest.layer.Activator.class, Level.WARNING,
          " Exception while copying relations . Some relation will not be copied ");
    }
  }

  /**
   * This function sets the relations in the ITransferableArtifact object
   *
   * @param relatedArtifacts object which holds the relations
   * @param createRelationTypeSide RelationTypeSide
   * @param iRelationType the key for the relation map, set in ITransferableArtifact object
   * @param transArtifact TransferableArtifact object in which the relations will be set from the relatedArtifacts
   *          object
   */
  private static void fillRelationsdup(final ResultSet<ArtifactReadable> relatedArtifacts, final RelationTypeSide side,
      final RelationTypeToken iRelationType, final TransferableArtifact transArtifact) {
    List<ITransferableArtifact> list1 = new ArrayList<ITransferableArtifact>();
    for (ArtifactReadable artifactReadable : relatedArtifacts) {

      ArtifactReadable user = OseeCoreData.getOrcsApi().getQueryFactory().fromBranch(artifactReadable.getBranch())
          .andGuid(artifactReadable.getGuid()).getResults().getExactlyOne();
      TransferableArtifact art = new TransferableArtifact();
      TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(user, art);
      list1.add(art);
    }
    transArtifact.putRelations(((RelationTypeToken) iRelationType).getName() +
        CommonConstants.RELATION_MAP_KEY_SEPARATOR + side.getSide().name(), list1);
  }

  /**
   * This method will copy the basic general info to transferable artifact
   *
   * @param readable ArtifactReadable Object holding the values
   * @param rootArt is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */
  public static void copyBasicInfoToTransferableArtifact(final ArtifactReadable readable,
      final ITransferableArtifact rootArt)
      throws OseeCoreException {
    OrcsApi orcsApi = OseeCoreData.getOrcsApi();
    String name = readable.getName();
    List<String> list1 = new ArrayList<String>();
    list1.add(name);
    rootArt.setName(name);


    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    rootArt.setId(readable.getId());


    List<String> list3 = new ArrayList<String>();
    if (readable.getArtifactType().toString().equalsIgnoreCase("IC_Component")) {
      rootArt.setArtifactType("Component");
      list3.add("Component");
    }
    else {
      rootArt.setArtifactType(readable.getArtifactType().toString());
      list3.add(readable.getArtifactType().toString());
    }

    rootArt.putAttributes("", list3);
    rootArt.putAttributes(CoreAttributeTypes.Name.toString(), list1);
    rootArt.putAttributes("guid", list2);

    rootArt.setBranchGuid(readable.getBranch().getId());
  }

  /**
   * Method to copy all info to ITransferableArtifact Loader for Export SubTasks/Version Attribibutes
   *
   * @param readable ArtifactReadable Object holding the values
   * @param mainArt is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */

  public static void copyAllInfoToTransferableArtifact(final ArtifactReadable readable,
      final ITransferableArtifact mainArt)
      throws OseeCoreException {

    OrcsApi orcsApi = OseeCoreData.getOrcsApi();
    String name = readable.getName();
    List<String> list1 = new ArrayList<String>();
    list1.add(name);
    mainArt.setName(name);

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        listTemp.add(attributeReadable.getValue().toString());
      }
      mainArt.putAttributes(iAttributeType.toString(), listTemp);
    }

    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    mainArt.setId(readable.getId());


    List<String> list3 = new ArrayList<String>();
    mainArt.setArtifactType(readable.getArtifactType().toString());
    list3.add(readable.getArtifactType().toString());

    mainArt.putAttributes("", list3);
    mainArt.putAttributes(CoreAttributeTypes.Name.toString(), list1);
    mainArt.putAttributes("guid", list2);

    mainArt.setBranchGuid(readable.getBranch().getId());

  }

  /**
   * Method to copy all info to ITransferableArtifact. Loader for Export Task and SubTask Attributes for Excel Export
   *
   * @param readable ArtifactReadable Object holding the values
   * @param transArt is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */

  public static void copyTaskInfoToTransferableArtifact(final ArtifactReadable readable,
      final TransferableArtifact transArt)
      throws OseeCoreException {

    OrcsApi orcsApi = OseeCoreData.getOrcsApi();
    String name = readable.getName();
    List<String> list1 = new ArrayList<String>();
    list1.add(name);
    transArt.setName(name);

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        listTemp.add(attributeReadable.getValue().toString());
      }
      transArt.putAttributes(iAttributeType.toString(), listTemp);
    }


    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    transArt.setId(readable.getId());


    List<String> list3 = new ArrayList<String>();
    transArt.setArtifactType(readable.getArtifactType().toString());
    list3.add(readable.getArtifactType().toString());

    transArt.putAttributes("", list3);
    transArt.putAttributes(CoreAttributeTypes.Name.toString(), list1);
    transArt.putAttributes("guid", list2);

    ResultSet<ArtifactReadable> results = readable.getRelated(AtsRelationTypes.TeamWfToTask_Task);
    List<ITransferableArtifact> list11 = new ArrayList<ITransferableArtifact>();
    for (ArtifactReadable artifactReadable : results) {
      TransferableArtifact art = new TransferableArtifact();
      TranferableArtifactLoader.copyAllInfoToTransferableArtifact(artifactReadable, art);
      list11.add(art);
    }
    transArt.putRelations(AtsRelationTypes.TeamWfToTask_Task.getName(), list11);

    ResultSet<ArtifactReadable> related = readable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
    List<ITransferableArtifact> list12 = new ArrayList<ITransferableArtifact>();
    for (ArtifactReadable artifactReadable : related) {
      TransferableArtifact ar = new TransferableArtifact();
      TranferableArtifactLoader.copyAllInfoToTransferableArtifact(artifactReadable, ar);
      list12.add(ar);
    }
    transArt.putRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version.getName(), list12);

    transArt.setBranchGuid(readable.getBranch().getId());

  }

  /**
   * copy method which triggers copying the data into TransferableArtifact
   *
   * @param readable ArtifactReadable Object holding the values
   * @param transArt is the ITransferableArtifact object in which the values will be set.
   * @param executorService service to run a tread to copy
   * @return
   */
  public static ListenableFuture<?> copy(final ArtifactReadable readable, final TransferableArtifact transArt,
      final ListeningExecutorService executorService) {
    return executorService.submit(new Runnable() {

      @Override
      public void run() {
        copyBasicTaskInfoToTransferableArtifact(readable, transArt);
      }
    });
  }

  /**
   * This method copy the basic task info to TransferableArtifact
   * 
   * @param readable ArtifactReadable Object holding the values
   * @param transArt is the ITransferableArtifact object in which the values will be set.
   * @throws OseeCoreException
   */
  public static void copyBasicTaskInfoToTransferableArtifact(final ArtifactReadable readable,
      final TransferableArtifact transArt)
      throws OseeCoreException {

    OrcsApi orcsApi = OseeCoreData.getOrcsApi();
    String name = readable.getName();
    List<String> list1 = new ArrayList<String>();
    list1.add(name);
    transArt.setName(name);

    Collection<? extends AttributeTypeToken> existingAttributeTypes = readable.getExistingAttributeTypes();
    for (AttributeTypeToken iAttributeType : existingAttributeTypes) {
      ResultSet<? extends AttributeReadable<Object>> attributes = readable.getAttributes(iAttributeType);
      List<String> listTemp = new ArrayList<String>();
      for (AttributeReadable<Object> attributeReadable : attributes) {
        if (null != attributeReadable.getValue()) {
          listTemp.add(attributeReadable.getValue().toString());
        }
        else {
          listTemp.add("");
        }
      }
      transArt.putAttributes(iAttributeType.toString(), listTemp);
    }


    List<String> list2 = new ArrayList<String>();
    list2.add(readable.getGuid());
    transArt.setId(readable.getId());


    List<String> list3 = new ArrayList<String>();
    transArt.setArtifactType(readable.getArtifactType().toString());
    list3.add(readable.getArtifactType().toString());

    transArt.setLocalId(readable.getIdIntValue());
    transArt.putAttributes("", list3);
    transArt.putAttributes(CoreAttributeTypes.Name.toString(), list1);
    transArt.putAttributes("guid", list2);

    ResultSet<ArtifactReadable> results = readable.getRelated(AtsRelationTypes.TeamWfToTask_Task);
    List<ITransferableArtifact> list11 = new ArrayList<ITransferableArtifact>();
    for (ArtifactReadable artifactReadable : results) {
      TransferableArtifact art = new TransferableArtifact();
      TranferableArtifactLoader.copyArtifactReadbleToTransferableArtifact(artifactReadable, art);
      list11.add(art);
    }

    transArt.putRelations(AtsRelationTypes.TeamWfToTask_Task.getName(), list11);

    transArt.setBranchGuid(readable.getBranch().getId());

  }
}
