/*
 * Created on Jan 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.config.demo.internal.OseeAtsConfigDemoActivator;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workdef.IAtsWorkDefinitionSheetProvider;

public class DemoWorkDefinitionSheetProvider implements IAtsWorkDefinitionSheetProvider {

   @Override
   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<WorkDefinitionSheet>();
      sheets.add(new WorkDefinitionSheet("WorkDef_Demo_AIs_And_Team_Definitions", "",
         AtsWorkDefinitionSheetProviders.getSupportFile(OseeAtsConfigDemoActivator.PLUGIN_ID,
            "support/WorkDef_Demo_AIs_And_Team_Definitions.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Code", "demo.code",
         AtsWorkDefinitionSheetProviders.getSupportFile(OseeAtsConfigDemoActivator.PLUGIN_ID,
            "support/WorkDef_Team_Demo_Code.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Req", "demo.req",
         AtsWorkDefinitionSheetProviders.getSupportFile(OseeAtsConfigDemoActivator.PLUGIN_ID,
            "support/WorkDef_Team_Demo_Req.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Test", "demo.test",
         AtsWorkDefinitionSheetProviders.getSupportFile(OseeAtsConfigDemoActivator.PLUGIN_ID,
            "support/WorkDef_Team_Demo_Test.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_SwDesign", "demo.swdesign",
         AtsWorkDefinitionSheetProviders.getSupportFile(OseeAtsConfigDemoActivator.PLUGIN_ID,
            "support/WorkDef_Team_Demo_SwDesign.ats")));
      return sheets;
   }
}
