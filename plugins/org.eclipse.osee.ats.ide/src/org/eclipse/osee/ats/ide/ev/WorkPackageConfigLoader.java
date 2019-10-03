/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.ev;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageConfigLoader {

   public Map<String, Artifact> workPackageGuidToWorkPackageArt;
   public Map<Artifact, Artifact> workPackageArtToActivityArt;
   public Map<Artifact, Artifact> activityArtToInsertionArt;
   public Map<Artifact, Artifact> insertionArtToProgramArt;
   public Map<Artifact, Artifact> programArtToCountryArt;
   public Map<String, WorkPackageData> workPackageGuidToWorkPackageData;

   public void load() {
      workPackageGuidToWorkPackageArt = new HashMap<>();
      workPackageArtToActivityArt = new HashMap<>();
      activityArtToInsertionArt = new HashMap<>();
      insertionArtToProgramArt = new HashMap<>();
      programArtToCountryArt = new HashMap<>();
      workPackageGuidToWorkPackageData = new HashMap<>();

      loadWorkPackageConfiguration();

      for (Artifact workPackageArt : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.WorkPackage,
         AtsClientService.get().getAtsBranch())) {

         workPackageGuidToWorkPackageArt.put(workPackageArt.getGuid(), workPackageArt);

         WorkPackageData data = new WorkPackageData(workPackageArt.getName(), workPackageArt.getId());

         Artifact insertionActivityArt = workPackageArtToActivityArt.get(workPackageArt);
         if (insertionActivityArt != null) {
            workPackageArtToActivityArt.put(workPackageArt, insertionActivityArt);
            data.setInsertionActivityName(insertionActivityArt.getName());

            Artifact insertionArt = activityArtToInsertionArt.get(insertionActivityArt);
            insertionActivityArt.getRelatedArtifactOrNull(AtsRelationTypes.InsertionToInsertionActivity_Insertion);
            if (insertionArt != null) {
               data.setInsertionName(insertionArt.getName());

               Artifact programArt = insertionArtToProgramArt.get(insertionArt);
               if (programArt != null) {
                  data.setProgramName(programArt.getName());

                  Artifact countryArt = programArtToCountryArt.get(programArt);
                  if (countryArt != null) {
                     data.setCountryName(countryArt.getName());
                  }
               }
            }
         }

         data.setColorTeam(workPackageArt.getSoleAttributeValueAsString(AtsAttributeTypes.ColorTeam, ""));
         data.setWorkPackageProgram(
            workPackageArt.getSoleAttributeValueAsString(AtsAttributeTypes.WorkPackageProgram, ""));
         data.setWorkPackageIdStr(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.WorkPackageId, ""));
         data.setActivityId(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.ActivityId, ""));
         data.setWorkPackageIpt(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.IPT, ""));
         data.setWorkPackageType(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.WorkPackageType, ""));
         data.setWorkPackageNotes(workPackageArt.getSoleAttributeValue(CoreAttributeTypes.Notes, ""));
         data.setWorkPackageAnnotation(workPackageArt.getSoleAttributeValue(CoreAttributeTypes.Annotation, ""));
         data.setTeamNames(
            Collections.toString("; ", workPackageArt.getRelatedArtifacts(AtsRelationTypes.WorkPackage_AtsTeamDefOrAi)));
         data.setWorkPackageActive(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
         data.setWorkPackageStartDate(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.StartDate, null));
         data.setWorkPackageEndDate(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.EndDate, null));
         data.setWorkPackagePercentComplete(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0));
         data.setWorkPackagePointsNumeric(workPackageArt.getSoleAttributeValue(AtsAttributeTypes.PointsNumeric, 0.0));

         workPackageGuidToWorkPackageData.put(workPackageArt.getGuid(), data);
      }
   }

   private void loadWorkPackageConfiguration() {
      for (Artifact workPackageArt : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.WorkPackage,
         AtsClientService.get().getAtsBranch())) {

         Artifact insertionActivityArt =
            workPackageArt.getRelatedArtifactOrNull(AtsRelationTypes.InsertionActivityToWorkPackage_InsertionActivity);
         if (insertionActivityArt != null) {
            workPackageArtToActivityArt.put(workPackageArt, insertionActivityArt);

            Artifact insertionArt =
               insertionActivityArt.getRelatedArtifactOrNull(AtsRelationTypes.InsertionToInsertionActivity_Insertion);
            if (insertionArt != null) {
               activityArtToInsertionArt.put(insertionActivityArt, insertionArt);

               Artifact programArt = insertionArt.getRelatedArtifactOrNull(AtsRelationTypes.ProgramToInsertion_Program);
               if (programArt != null) {
                  insertionArtToProgramArt.put(insertionArt, programArt);

                  Artifact countryArt = programArt.getRelatedArtifactOrNull(AtsRelationTypes.CountryToProgram_Country);
                  if (countryArt != null) {
                     programArtToCountryArt.put(programArt, countryArt);
                  }
               }
            }
         }

      }
   }

   public Collection<WorkPackageData> getWorkPackageDatas() {
      return workPackageGuidToWorkPackageData.values();
   }

   public WorkPackageData getWorkPackageData(String workPackageGuid) {
      return workPackageGuidToWorkPackageData.get(workPackageGuid);
   }
}
