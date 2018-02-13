/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.workdef.IAtsWorkDefinitionSheetProvider;

/**
 * @author Donald G. Dunne
 */
public class DemoWorkDefinitionSheetProvider implements IAtsWorkDefinitionSheetProvider {

   @Override
   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<>();
      sheets.add(new WorkDefinitionSheet("WorkDef_Demo_AIs_And_Team_Definitions", Activator.PLUGIN_ID));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Code", Activator.PLUGIN_ID));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Req", Activator.PLUGIN_ID));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_Test", Activator.PLUGIN_ID));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Demo_SwDesign", Activator.PLUGIN_ID));
      return sheets;
   }
}
