package org.eclipse.osee.ats.core.workflow.transition;

import org.eclipse.osee.ats.core.workdef.WidgetDefinition;

public class ValidResult {
   private final ValidType type;
   private final WidgetDefinition widgetDef;
   private final String details;
   public static ValidResult Valid = new ValidResult(ValidType.Valid, null, null);

   public ValidResult(ValidType type, WidgetDefinition widgetDef, String details) {
      super();
      this.type = type;
      this.widgetDef = widgetDef;
      this.details = details;
   }

   public boolean isValid() {
      return type == ValidType.Valid;
   }

   public ValidType getType() {
      return type;
   }

   public WidgetDefinition getWidgetDef() {
      return widgetDef;
   }

   public String getDetails() {
      return details;
   }
}
