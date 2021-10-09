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
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.DefaultXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.FrameworkXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

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
      for (IAtsWorkDefinition workDef : atsApi.getWorkDefinitionService().getAllWorkDefinitions()) {
         for (WidgetDefinition widgetDef : atsApi.getWorkDefinitionService().getWidgets(workDef)) {
            String xWidgetName = widgetDef.getXWidgetName();
            if (Strings.isValid(xWidgetName)) {
               XWidget widget = getWidget(xWidgetName);
               if (widget == null || widget.getLabel().contains("Unhandled XWidget")) {
                  rd.errorf("Widget not found for [%s] in WorkDef %s\n", xWidgetName, workDef.toStringWithId());
               }
            }
         }
      }
      return rd;
   }

   public XWidget getWidget(String xWidgetName) {
      SwtXWidgetRenderer dynamicXWidgetLayout = new SwtXWidgetRenderer(null, new DefaultXWidgetOptionResolver());
      XWidgetRendererItem dummyItem = new XWidgetRendererItem(dynamicXWidgetLayout, XOption.NONE);
      XWidget widget = FrameworkXWidgetProvider.getXWidget(dummyItem, xWidgetName, "IsInTest", null);
      return widget;
   }

}
