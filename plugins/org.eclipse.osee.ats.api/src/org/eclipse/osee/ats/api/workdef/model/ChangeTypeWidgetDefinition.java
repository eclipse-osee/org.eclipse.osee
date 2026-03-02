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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXChangeTypeWidget;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeWidgetDefinition extends WidgetDefinition {

   public ChangeTypeWidgetDefinition() {
      this(java.util.Collections.emptyList());
   }

   public ChangeTypeWidgetDefinition(ChangeTypes... changeTypes) {
      this(Collections.asList(changeTypes));
   }

   public ChangeTypeWidgetDefinition(List<ChangeTypes> changeTypes) {
      super("Change Type", AtsAttributeTypes.ChangeType, XXChangeTypeWidget);
      if (!changeTypes.isEmpty()) {
         getWidData().setSelectable(OseeEnum.toStrings(changeTypes));
      }
   }

   public LayoutItem andRequired() {
      set(WidgetOption.RFT);
      return this;
   }

}
