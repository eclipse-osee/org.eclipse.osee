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
package org.eclipse.osee.ats.rest.internal.workdef;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStore;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionStoreImpl implements IAtsWorkDefinitionStore {

   private static OrcsApi orcsApi;

   public static void setOrcsApi(OrcsApi orcsApi) {
      AtsWorkDefinitionStoreImpl.orcsApi = orcsApi;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(orcsApi, "OrcsApi");
      System.out.println("ATS - AtsWorkDefinitionStoreImpl started");
   }

   @Override
   public List<Pair<String, String>> getWorkDefinitionStrings() throws OseeCoreException {
      List<Pair<String, String>> nameToWorkDefStr = new ArrayList<>(15);
      for (ArtifactReadable workDefArt : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         AtsArtifactTypes.WorkDefinition).getResults()) {
         nameToWorkDefStr.add(
            new Pair<String, String>(workDefArt.getName(), loadWorkDefinitionFromArtifact(workDefArt)));
      }
      return nameToWorkDefStr;
   }

   @Override
   public String loadWorkDefinitionString(String workDefId) throws OseeCoreException {
      return loadWorkDefinitionFromArtifact(workDefId);
   }

   @Override
   public boolean isWorkDefinitionExists(String workDefId) throws OseeCoreException {
      return loadWorkDefinitionString(workDefId) != null;
   }

   private String loadWorkDefinitionFromArtifact(String name) throws OseeCoreException {
      ArtifactReadable artifact =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(AtsArtifactTypes.WorkDefinition).and(
            CoreAttributeTypes.Name, name, QueryOption.EXACT_MATCH_OPTIONS).getResults().getExactlyOne();
      return loadWorkDefinitionFromArtifact(artifact);
   }

   private String loadWorkDefinitionFromArtifact(ArtifactReadable artifact) throws OseeCoreException {
      String modelText = null;
      if (artifact != null) {
         modelText = artifact.getSoleAttributeAsString(AtsAttributeTypes.DslSheet);
      }
      return modelText;
   }

   @Override
   public String loadRuleDefinitionString() throws OseeCoreException {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
         AtsArtifactToken.RuleDefinitions).getResults().getOneOrNull();
      if (artifact != null) {
         return artifact.getSoleAttributeValue(AtsAttributeTypes.DslSheet, null);
      } else {
         return null;
      }
   };

}
