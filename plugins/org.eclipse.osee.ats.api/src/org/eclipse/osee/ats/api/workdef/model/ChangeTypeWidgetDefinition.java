/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeWidgetDefinition extends WidgetDefinition {

   public ChangeTypeWidgetDefinition(ChangeTypes... changeTypes) {
      this(false, changeTypes);
   }

   public ChangeTypeWidgetDefinition(boolean isAttr, ChangeTypes... changeTypes) {
      super("Change Type", (isAttr ? "XHyperlinkChangeTypeSelectionDam" : "XHyperlinkChangeTypeSelection"));
      if (changeTypes != null) {
         addParameter("ChangeType", Collections.toString(";", Collections.asList(changeTypes)));
      } else {
         addParameter("ChangeType", Collections.toString(";", ChangeTypes.DEFAULT_CHANGE_TYPES));
      }
   }

   public LayoutItem andRequired() {
      set(WidgetOption.REQUIRED_FOR_TRANSITION);
      return this;
   }

}
