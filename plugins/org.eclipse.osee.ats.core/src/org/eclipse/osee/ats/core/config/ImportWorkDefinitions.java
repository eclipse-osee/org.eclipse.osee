/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.tx.IAtsWorkDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class ImportWorkDefinitions {

   private final AtsApi atsApi;
   private final List<IAtsWorkDefinitionArtifactToken> workDefs = Arrays.asList(AtsArtifactToken.WorkDef_Goal,
      AtsArtifactToken.WorkDef_Review_Decision, AtsArtifactToken.WorkDef_Review_PeerToPeer,
      AtsArtifactToken.WorkDef_Sprint, AtsArtifactToken.WorkDef_Task_Default, AtsArtifactToken.WorkDef_Team_Default,
      AtsArtifactToken.WorkDef_Team_Simple);
   private final Class<?> clazz;

   public ImportWorkDefinitions(AtsApi atsApi, Class<?> clazz) {
      this.atsApi = atsApi;
      this.clazz = clazz;
   }

   public ImportWorkDefinitions(AtsApi atsApi) {
      this(atsApi, ImportWorkDefinitions.class);
   }

   public XResultData importDefaultSheets() {
      return importWorkDefinitionSheets(workDefs.toArray(new IAtsWorkDefinitionArtifactToken[workDefs.size()]));
   }

   public XResultData importWorkDefinitionSheets(IAtsWorkDefinitionArtifactToken... workDefs) {
      XResultData results = new XResultData();
      try {
         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
         for (IAtsWorkDefinitionArtifactToken workDefTok : workDefs) {
            ArtifactToken workDefArt =
               changes.createArtifact(AtsArtifactTypes.WorkDefinition, workDefTok.getName(), workDefTok.getId());
            String workDefStr = OseeInf.getResourceContents("atsConfig/" + workDefArt.getName() + ".ats", clazz);
            changes.setSoleAttributeValue(workDefArt, AtsAttributeTypes.DslSheet, workDefStr);
            changes.addChild(AtsArtifactToken.WorkDefinitionsFolder, workDefArt);
            results.logf("Imported Work Def [%s}", workDefArt.getName());
         }
         changes.execute();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

}
