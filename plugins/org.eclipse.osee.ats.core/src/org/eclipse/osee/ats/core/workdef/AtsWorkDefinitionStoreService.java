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
import org.eclipse.osee.ats.api.IAtsServices;
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

   private final IAtsServices services;

   public AtsWorkDefinitionStoreService(IAtsServices services) {
      this.services = services;
   }

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      List<WorkDefData> results = new ArrayList<>();
      for (ArtifactToken workDefArt : services.getQueryService().getArtifacts(AtsArtifactTypes.WorkDefinition,
         services.getAtsBranch())) {
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
      ArtifactToken artifact = services.getArtifactByName(AtsArtifactTypes.WorkDefinition, name);
      if (artifact != null) {
         return new WorkDefData(artifact.getId(), artifact.getName(), loadWorkDefinitionFromArtifact(artifact));
      }
      return null;
   }

   private String loadWorkDefinitionFromArtifact(ArtifactToken artifact) {
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      String modelText = null;
      if (artifact != null) {
         modelText = services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.DslSheet, "");
      }
      return modelText;
   }

   public String loadRuleDefinitionString() {
      ArtifactToken artifact = services.getArtifact(AtsArtifactToken.RuleDefinitions);
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.DslSheet, "");
   }

   public WorkDefData loadWorkDefinitionString(Long id) {
      ArtifactToken artifact = services.getArtifact(id);
      Conditions.checkNotNull(artifact, "Work Definition artifact");
      return new WorkDefData(artifact.getId(), artifact.getName(), loadWorkDefinitionFromArtifact(artifact));
   }

}
