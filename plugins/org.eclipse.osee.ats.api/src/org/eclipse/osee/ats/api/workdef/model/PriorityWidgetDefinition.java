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

import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class PriorityWidgetDefinition extends WidgetDefinition {

   public PriorityWidgetDefinition(Priorities... priorities) {
      this(false, priorities);
   }

   public PriorityWidgetDefinition(boolean isAttr, Priorities... priorities) {
      super("Priority", (isAttr ? "XHyperlinkPrioritySelectionDam" : "XHyperlinkPrioritySelection"));
      if (priorities != null) {
         addParameter("Priority", Collections.toString(";", Collections.asList(priorities)));
      } else {
         addParameter("Priority", Collections.toString(";", Priorities.DEFAULT_PRIORITIES));
      }
   }

   public LayoutItem andRequired() {
      set(WidgetOption.REQUIRED_FOR_TRANSITION);
      return this;
   }

}
