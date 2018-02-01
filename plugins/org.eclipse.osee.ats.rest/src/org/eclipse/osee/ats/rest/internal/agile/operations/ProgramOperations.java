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
package org.eclipse.osee.ats.rest.internal.agile.operations;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileProgram;
import org.eclipse.osee.ats.api.agile.atw.AtwNode;
import org.eclipse.osee.ats.api.agile.program.UiGridProgItem;
import org.eclipse.osee.ats.api.agile.program.UiGridProgram;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public class ProgramOperations {

   private final AtsApi atsApi;
   private static String IMG_BASE_PATH = "/ats/agileui/images/";

   public ProgramOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public AtwNode getAtwTree(IAgileProgram program) {
      ArtifactToken progArt = atsApi.getQueryService().getArtifact(program.getId());
      AtwNode progNode = handleArtifactAndChildren(progArt, null);
      progNode.setExpanded(true);
      return progNode;
   }

   private AtwNode handleArtifactAndChildren(ArtifactToken artifact, AtwNode parentNode) {
      AtwNode node = getNodeFromArt(artifact);
      if (parentNode != null) {
         parentNode.getChildren().add(node);
      }

      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(artifact)) {
         handleArtifactAndChildren(child, node);
      }
      return node;
   }

   private AtwNode getNodeFromArt(ArtifactToken artifact) {
      AtwNode node = new AtwNode();
      node.setName(artifact.getName());
      node.setId(artifact.getId().toString());
      node.setImage(getImage(artifact));
      return node;
   }

   public UiGridProgram getUiGridTree(IAgileProgram program) {
      ArtifactToken progArt = atsApi.getQueryService().getArtifact(program.getId());
      UiGridProgram progItem = new UiGridProgram();
      progItem.setId(program.getId());
      progItem.setName(program.getName());
      progItem.setItems(handleProgArtifactAndChildren(progArt, null, -1, new LinkedList<UiGridProgItem>()));
      return progItem;
   }

   private List<UiGridProgItem> handleProgArtifactAndChildren(ArtifactToken artifact, UiGridProgItem parentNode, int level, LinkedList<UiGridProgItem> items) {
      UiGridProgItem item = getProgItemFromArt(artifact);
      item.setTLevel(level);
      if (level >= 0) {
         items.add(item);
      }

      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(artifact)) {
         handleProgArtifactAndChildren(child, item, level + 1, items);
      }
      return items;
   }

   private UiGridProgItem getProgItemFromArt(ArtifactToken artifact) {
      UiGridProgItem item = new UiGridProgItem();
      item.setName(artifact.getName());
      item.setId(artifact.getId());
      item.setImage(getImage(artifact));
      item.setType(atsApi.getStoreService().getArtifactType(artifact).getName());
      return item;
   }

   private String getImage(ArtifactToken artifact) {
      if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileProgramBacklog)) {
         return IMG_BASE_PATH + "agileProgramBacklog.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileProgram)) {
         return IMG_BASE_PATH + "agileProgram.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileProgramBacklogItem)) {
         return IMG_BASE_PATH + "agileProgramBacklogItem.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileProgramFeature)) {
         return IMG_BASE_PATH + "agileProgramFeature.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileStory)) {
         return IMG_BASE_PATH + "agileStory.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileSprint)) {
         return IMG_BASE_PATH + "agileSprint.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileBacklog)) {
         return IMG_BASE_PATH + "agileBacklog.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileTeam)) {
         return IMG_BASE_PATH + "agileTeam.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.AgileFeatureGroup)) {
         return IMG_BASE_PATH + "agileFeatureGroup.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, CoreArtifactTypes.GeneralDocument)) {
         return IMG_BASE_PATH + "generalDoc.gif";
      } else if (atsApi.getStoreService().isOfType(artifact, CoreArtifactTypes.Folder)) {
         return IMG_BASE_PATH + "folder.gif";
      }
      return null;
   }

}
