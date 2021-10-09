/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.workflow.transition;

import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;

/**
 * @author Donald G. Dunne
 */
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
