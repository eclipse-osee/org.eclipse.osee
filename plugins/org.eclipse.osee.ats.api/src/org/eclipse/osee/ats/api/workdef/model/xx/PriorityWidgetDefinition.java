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

package org.eclipse.osee.ats.api.workdef.model.xx;

import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class PriorityWidgetDefinition extends AttrWidgetDef {

   public PriorityWidgetDefinition() {
      this(java.util.Collections.emptyList());
   }

   public PriorityWidgetDefinition(Priorities... priorities) {
      this(Collections.asList(priorities));
   }

   public PriorityWidgetDefinition(List<Priorities> priorities) {
      super("Priority", AtsAttributeTypes.Priority, WidgetIdAts.XXPriorityWidget);
      if (!priorities.isEmpty()) {
         getWidData().setSelectable(OseeEnum.toStrings(priorities));
      }
      set(XOption.CLEAR);
      setOseeImage(AtsImage.PRIORITY);
   }

   public PriorityWidgetDefinition addPriorityUrl(String priorityUrl) {
      addParameter("DescUrl", priorityUrl);
      return this;
   }

   public LayoutItem andRequired() {
      set(XOption.REQUIRED);
      return this;
   }

}
