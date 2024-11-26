/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.agile.jira;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.graphics.Color;

/**
 * @author Donald G. Dunne
 */
public class JiraStateColumnUI extends AbstractJiraSyncColumnUI {

   public static JiraStateColumnUI instance = new JiraStateColumnUI();
   private static Map<String, Color> stateToColor;

   public static JiraStateColumnUI getInstance() {
      return instance;
   }

   private JiraStateColumnUI() {
      super(AtsColumnTokensDefault.JiraStateColumn);
      if (stateToColor == null) {
         stateToColor = new HashMap<>();
         stateToColor.put("To Do", FontManager.getDarkCyan());
         stateToColor.put("In Progress", FontManager.getDarkBlue());
         stateToColor.put("In Review", FontManager.getDarkYellow());
         stateToColor.put("Closed", FontManager.getDarkGreen());
      }
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public JiraStateColumnUI copy() {
      JiraStateColumnUI newXCol = new JiraStateColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      if (workItem.isTeamWorkflow()) {
         if (AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.Points,
            "").equals(AtsAttributeTypes.Points.Epic.getName())) {
            return "Epic";
         }
         IAgileSprint sprint = AtsApiService.get().getAgileService().getSprint((IAtsTeamWorkflow) workItem);
         if (sprint != null) {
            JiraSearch srch = search(workItem);
            if (srch.getRd().isErrors()) {
               return srch.getRd().toString();
            }
            if (srch.issues != null && !srch.issues.isEmpty()) {
               return srch.issues.iterator().next().getState();
            } else {
               return "Not in JIRA";
            }
         }
      }
      return "";
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      String text = getColumnText(element, xCol, columnIndex);
      Color col = stateToColor.get(text);
      if (col != null) {
         return col;
      }
      return super.getForeground(element, xCol, columnIndex);
   }

}
