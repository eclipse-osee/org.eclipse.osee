/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionStoreService implements IAtsWorkDefinitionStringProvider {

   private final AtsApi atsApi;

   public AtsWorkDefinitionStoreService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      List<WorkDefData> results = new ArrayList<>();
      for (ArtifactToken workDefArt : atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(),
         AtsArtifactTypes.WorkDefinition)) {
         results.add(
            new WorkDefData(workDefArt.getId(), workDefArt.getName(), loadWorkDefinitionFromArtifact(workDefArt)));
      }
      return results;
   }

   /**
    * @return WorkDefData or null if not found
    */
   public WorkDefData loadWorkDefinitionString(String workDefName) {
      return loadWorkDefinitionFromArtifact(workDefName);
   }

   public boolean isWorkDefinitionExists(String workDefName) {
      return loadWorkDefinitionString(workDefName) != null;
   }

   /**
    * @return WorkDefData or null if not found
    */
   private WorkDefData loadWorkDefinitionFromArtifact(String name) {
      ArtifactToken artifact = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.WorkDefinition, name);
      if (artifact != null) {
         return new WorkDefData(artifact.getId(), artifact.getName(), loadWorkDefinitionFromArtifact(artifact));
      }
      return null;
   }

   private String loadWorkDefinitionFromArtifact(ArtifactToken artifact) {
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      String modelText = null;
      if (artifact != null) {
         modelText = atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.DslSheet, "");
      }
      return modelText;
   }

   public String loadRuleDefinitionString() {
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(AtsArtifactToken.RuleDefinitions);
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.DslSheet, "");
   }

   public WorkDefData loadWorkDefinitionString(Long id) {
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(id);
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      return new WorkDefData(artifact.getId(), artifact.getName(), loadWorkDefinitionFromArtifact(artifact));
   }

}
