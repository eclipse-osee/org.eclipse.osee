/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetFactory;

/**
 * @author Donald G. Dunne
 */
public class ValidateWorkDefXWidgetOperation {

   private final AtsApi atsApi;

   public ValidateWorkDefXWidgetOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {
      XResultData rd = new XResultData();
      for (WorkDefinition workDef : atsApi.getWorkDefinitionService().getAllWorkDefinitions()) {
         for (WidgetDefinition widgetDef : atsApi.getWorkDefinitionService().getWidgets(workDef)) {
            WidgetId widgetId = widgetDef.getWidgetId();
            if (widgetId.isValid()) {
               XWidget widget = XWidgetFactory.getInstance().createXWidget(widgetId);
               if (widget == null || widget.getLabel().contains("Unhandled XWidget")) {
                  rd.errorf("Widget not found for [%s] in WorkDef %s\n", widgetId, workDef.toStringWithId());
               }
            }
         }
      }
      return rd;
   }

}
