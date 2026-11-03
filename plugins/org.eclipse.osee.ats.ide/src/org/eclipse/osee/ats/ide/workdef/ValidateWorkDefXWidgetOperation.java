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
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetService;

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
         for (WidgetDef widgetDef : atsApi.getWorkDefinitionService().getWidgets(workDef)) {
            WidgetId widgetId = widgetDef.getWidgetId();
            if (widgetId.isValid()) {
               XWidget widget = XWidgetService.get().createXWidget(widgetId);
               if (widget == null || widget.getLabel().contains("Unhandled XWidget")) {
                  rd.errorf("Widget not found for [%s] in WorkDef %s\n", widgetId, workDef.toStringWithId());
               } else {
                  validateAttributeBackedWidget(rd, workDef, widgetDef, widget);
               }
            }
         }
      }
      return rd;
   }

   /**
    * Client-side complement to the server-side build-time check in WorkDefBuilder. The server can only see the
    * WidgetDef's declared attribute type; it cannot see that the client XWidget is attribute-backed. When a
    * REQUIRED/RFC widget is declared as a plain WidgetDef (no attribute type) but its instantiated XWidget carries a
    * valid attribute type, server-side transition validation will never enforce it. The fix is to declare it with
    * AttrWidgetDef so the def carries the attribute type the server can validate against.
    */
   private void validateAttributeBackedWidget(XResultData rd, WorkDefinition workDef, WidgetDef widgetDef,
      XWidget widget) {
      boolean requiredOrRfc = widgetDef.is(XOption.REQUIRED) || widgetDef.is(XOption.RFC);
      boolean defHasAttrType = widgetDef.getAttributeType().isValid();
      boolean widgetHasAttrType = widget.getAttributeType().isValid();
      if (isUnenforceableAttributeBackedWidget(requiredOrRfc, defHasAttrType, widgetHasAttrType)) {
         rd.errorf(
            "Widget [%s] (widgetId [%s]) is %s and its XWidget is backed by attribute type [%s], but it is declared "
               + "as a plain WidgetDef with no attribute type; server-side transition validation will never enforce "
               + "it. Declare it with AttrWidgetDef so the definition carries the attribute type. WorkDef %s\n",
            widgetDef.getName(), widgetDef.getWidgetId().getName(),
            widgetDef.is(XOption.REQUIRED) ? "REQUIRED" : "RFC", widget.getAttributeType().getName(),
            workDef.toStringWithId());
      }
   }

   /**
    * Pure decision for the attribute-backed check, extracted so it can be unit tested without XWidgetService/OSGi.
    * Fires only when a REQUIRED/RFC widget is declared as a plain WidgetDef (no attribute type on the def) yet its
    * instantiated XWidget is attribute-backed. This is deliberately narrow: a non-attribute widget (widgetHasAttrType
    * == false) never fires, so widgets that validate themselves in the UI or via domain transition logic (defect
    * viewer, applicability) are not false-positived.
    *
    * @param requiredOrRfc widgetDef.is(REQUIRED) || widgetDef.is(RFC)
    * @param defHasAttrType whether the WidgetDef itself carries a valid attribute type (i.e. is an AttrWidgetDef)
    * @param widgetHasAttrType whether the instantiated XWidget carries a valid attribute type
    */
   public static boolean isUnenforceableAttributeBackedWidget(boolean requiredOrRfc, boolean defHasAttrType,
      boolean widgetHasAttrType) {
      return requiredOrRfc && !defHasAttrType && widgetHasAttrType;
   }

}
