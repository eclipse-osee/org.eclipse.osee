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
package org.eclipse.osee.ats.api.workflow.transition;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class ValidResult {
   private final ValidType type;
   private final IAtsWidgetDefinition widgetDef;
   private final String details;
   public static ValidResult Valid = new ValidResult(ValidType.Valid, null, null);

   public ValidResult(ValidType type, IAtsWidgetDefinition widgetDef, String details) {
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

   public IAtsWidgetDefinition getWidgetDef() {
      return widgetDef;
   }

   public String getDetails() {
      return details;
   }
}
