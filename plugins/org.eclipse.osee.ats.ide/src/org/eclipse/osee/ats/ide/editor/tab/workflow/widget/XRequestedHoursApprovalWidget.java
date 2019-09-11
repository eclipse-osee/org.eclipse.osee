/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;

/**
 * @author Donald G. Dunne
 */
public class XRequestedHoursApprovalWidget extends XAbstractSignDateAndByButton {

   public static String ID = XRequestedHoursApprovalWidget.class.getSimpleName();

   public XRequestedHoursApprovalWidget() {
      super("Approve Requested Hours", "Manager approving the requesting hours.",
         AtsAttributeTypes.ApproveRequestedHoursDate, AtsAttributeTypes.ApproveRequestedHoursBy,
         AtsImage.CHECK_CLIPBOARD);
   }

   public XRequestedHoursApprovalWidget(boolean isRequired) {
      super("Approve Requested Hours", "Manager approving the requesting hours.",
         AtsAttributeTypes.ApproveRequestedHoursDate, AtsAttributeTypes.ApproveRequestedHoursBy,
         AtsImage.CHECK_CLIPBOARD, isRequired);

   }

   @Override
   public void refresh() {
      refreshLabel();
   }

}
