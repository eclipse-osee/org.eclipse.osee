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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.ui.forms.IMessageManager;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXIntroducedInVersionWidget extends XAbstractXXVersionWidget {

   public static final WidgetId ID = WidgetIdAts.XXIntroducedInVersionWidget;

   public static final String LABEL = "Introduced-In Version";

   public XXIntroducedInVersionWidget() {
      super(ID, LABEL, AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version);
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
   }

   @Override
   public void refresh() {
      super.refresh();

      if (Widgets.isAccessible(labelHyperlink) && xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         if (teamWf != null && isRequiredEntry()) {
            if (getManagedForm() != null && !getManagedForm().getForm().isDisposed()) {
               IMessageManager messageManager = getManagedForm().getMessageManager();
               if (getSelected().isEmpty()) {
                  messageManager.addMessage(labelHyperlink, "Must Select " + getLabel(), null, IMessageProvider.ERROR,
                     labelHyperlink);
               } else {
                  messageManager.removeMessages(labelHyperlink);
               }
            }
         }
      }
   }

}
