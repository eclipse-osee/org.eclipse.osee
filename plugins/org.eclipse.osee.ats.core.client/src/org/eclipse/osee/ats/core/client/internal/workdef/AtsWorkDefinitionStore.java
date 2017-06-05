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
package org.eclipse.osee.ats.core.client.internal.workdef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStore;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionStore implements IAtsWorkDefinitionStore {

   @Override
   public List<Pair<String, String>> getWorkDefinitionStrings() throws OseeCoreException {
      List<Pair<String, String>> nameToWorkDefStr = new ArrayList<>(15);
      for (Artifact workDefArt : ArtifactQuery.getArtifactListFromType(Arrays.asList(AtsArtifactTypes.WorkDefinition),
         AtsClientService.get().getAtsBranch(), DeletionFlag.EXCLUDE_DELETED)) {
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
      Artifact artifact = ArtifactQuery.getArtifactFromTypeAndNameNoException(AtsArtifactTypes.WorkDefinition, name,
         AtsClientService.get().getAtsBranch());
      return loadWorkDefinitionFromArtifact(artifact);
   }

   private String loadWorkDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      String modelText = null;
      if (artifact != null) {
         modelText = artifact.getAttributesToString(AtsAttributeTypes.DslSheet);
         ArtifactCache.deCache(artifact);
      }
      return modelText;
   }

   @Override
   public String loadRuleDefinitionString() throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(AtsArtifactToken.RuleDefinitions);
      return artifact.getSoleAttributeValueAsString(AtsAttributeTypes.DslSheet, "");
   };

}
