/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution; and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactImage;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactImages {

   private final static List<ArtifactImage> images = new LinkedList<>();
   public static ArtifactImage AGILE_TASK =
      AtsArtifactImages.construct(AtsArtifactTypes.TeamWorkflow, "workflow.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_SPRINT =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileSprint, "agileSprint.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_TEAM =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileTeam, "agileTeam.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_FEATURE_GROUP =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileFeatureGroup, "agileFeatureGroup.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_BACKLOG =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileBacklog, "agileBacklog.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_BACKLOG_ITEM = AtsArtifactImages.construct(
      AtsArtifactTypes.AgileProgramBacklogItem, "agileProgramBacklogItem.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileProgram, "agileProgram.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM_BACKLOG = AtsArtifactImages.construct(AtsArtifactTypes.AgileProgramBacklog,
      "agileProgramBacklog.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM_BACKLOG_ITEM = ArtifactImage.construct(
      AtsArtifactTypes.AgileProgramBacklogItem, "agileProgramBacklogItem.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_PROGRAM_FEATURE = AtsArtifactImages.construct(AtsArtifactTypes.AgileProgramFeature,
      "agileProgramFeature.gif", "/ats/agileui/images");
   public static ArtifactImage AGILE_STORY =
      AtsArtifactImages.construct(AtsArtifactTypes.AgileStory, "agileStory.gif", "/ats/agileui/images");

   protected static ArtifactImage construct(ArtifactTypeToken artifactType, String imageName, String baseUrl) {
      ArtifactImage image = ArtifactImage.construct(artifactType, imageName, baseUrl);
      images.add(image);
      return image;
   }

   public static List<ArtifactImage> getImages() {
      return images;
   }

}
