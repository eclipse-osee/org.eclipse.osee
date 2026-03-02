/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXFoundInVersionWidget extends XAbstractXXVersionWidget {

   public static final WidgetId ID = WidgetIdAts.XXFoundInVersionWidget;

   public static final String LABEL = "Found-In Version";

   public XXFoundInVersionWidget() {
      super(ID, LABEL, AtsRelationTypes.TeamWorkflowToFoundInVersion_Version);
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      widData.add(XOption.SORTED);
   }

}
