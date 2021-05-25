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

/**
 * @author Donald G. Dunne
 */
public class XCreateEscapementDemoWfXButton extends XCreateEscapementWfXButton {

   public static final String WIDGET_ID = XCreateEscapementDemoWfXButton.class.getSimpleName();

   public XCreateEscapementDemoWfXButton() {
      super("Create Demo Escapment Analysis Workflow");
   }

   @Override
   public IAtsActionableItem getAi() {
      return atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_CR_AI);
   }

}
