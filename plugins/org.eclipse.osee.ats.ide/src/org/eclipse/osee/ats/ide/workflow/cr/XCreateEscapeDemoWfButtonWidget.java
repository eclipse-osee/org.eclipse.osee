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
package org.eclipse.osee.ats.ide.workflow.cr;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XCreateEscapeDemoWfButtonWidget extends XAbstractCreateEscapeWfButtonArtWidget {

   public static WidgetId ID = WidgetIdAts.XCreateEscapeDemoWfButtonWidget;

   public XCreateEscapeDemoWfButtonWidget() {
      super(ID, "Create Demo Escape Analysis Workflow");
   }

   @Override
   public IAtsActionableItem getAi() {
      return atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_CR_AI);
   }

}
