/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.navigate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * See rd.log description below
 *
 * @author Donald G. Dunne
 */
public class GenerateAtsConfigDatabaseViews extends GenerateDatabaseViews {

   public GenerateAtsConfigDatabaseViews() {
      super("Generate ATS Config Database View SQL");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData rd = new XResultData();

      rd.log("These sql statements will create the views necessary to easily query ATS Configs");

      // NOTE: Do NOT include double newlines in sql or it will not work in DBeaver until edit and save
      Map<String, ArtifactTypeToken> tableToArtType = new HashMap<>();
      tableToArtType.put("osee_atscfg_team_v", AtsArtifactTypes.TeamDefinition);
      tableToArtType.put("osee_atscfg_ai_v", AtsArtifactTypes.ActionableItem);
      tableToArtType.put("osee_atscfg_user_v", CoreArtifactTypes.User);
      tableToArtType.put("osee_atscfg_ver_v", AtsArtifactTypes.Version);

      rd.log("\n\n------------------ TO USE THE VIEWS CREATED BELOW-------------------------\n");

      for (Entry<String, ArtifactTypeToken> entry : tableToArtType.entrySet()) {
         rd.logf("select * from %s; \n", entry.getKey());
      }

      rd.log("\n\n------------------ TO CREATE THE VIEWS -------------------------\n");
      rd.log("-- Drop the ats_cfg views, then run the following in DBeaver to create the views\n");

      for (Entry<String, ArtifactTypeToken> entry : tableToArtType.entrySet()) {
         String tableName = entry.getKey();
         ArtifactTypeToken artType = entry.getValue();

         rd.logf("--------------------------------------------------------\n");
         rd.logf("-- Create %s View \n", tableName);
         rd.logf("--------------------------------------------------------\n");
         List<AttributeTypeToken> attrTypes = artType.getValidAttributeTypes();
         attrTypes.remove(AtsAttributeTypes.TestRunToSourceLocator);
         attrTypes.remove(AtsAttributeTypes.Description);
         attrTypes.remove(AtsAttributeTypes.GoalOrderVote);
         attrTypes.remove(AtsAttributeTypes.FullName);
         attrTypes.remove(CoreAttributeTypes.Description);
         attrTypes.remove(CoreAttributeTypes.ContentUrl);
         attrTypes.remove(CoreAttributeTypes.Annotation);
         attrTypes.remove(CoreAttributeTypes.RelationOrder);
         createTableViewSql(tableName, "", attrTypes, rd, "", artType);
         rd.addRaw(getPostSql());

         rd.logf("\n\ngrant select on %s to public;\n", tableName);
         rd.logf("create public synonym %s for %s;", tableName, tableName);

         rd.log("\n");
      }

      XResultDataUI.report(rd, getName());
   }

}
