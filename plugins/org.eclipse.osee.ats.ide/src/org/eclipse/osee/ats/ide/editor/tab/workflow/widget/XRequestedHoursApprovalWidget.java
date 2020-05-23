/*********************************************************************
 * Copyright (c) 2019 Boeing
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
      super("Approve Requested Hours", "Sign or clear requesting hours.", AtsAttributeTypes.ApproveRequestedHoursDate,
         AtsAttributeTypes.ApproveRequestedHoursBy, AtsImage.CHECK_CLIPBOARD);
   }

   public XRequestedHoursApprovalWidget(boolean isRequired) {
      super("Approve Requested Hours", "Sign or clear requesting hours.", AtsAttributeTypes.ApproveRequestedHoursDate,
         AtsAttributeTypes.ApproveRequestedHoursBy, AtsImage.CHECK_CLIPBOARD, isRequired);

   }

   @Override
   public void refresh() {
      refreshLabel();
   }

}
