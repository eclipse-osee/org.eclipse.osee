/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractSignDateAndByButton;

/**
 * @author Donald G. Dunne
 */
public class XReviewedWidget extends XAbstractSignDateAndByButton {

   public XReviewedWidget() {
      super(AtsAttributeTypes.ReviewedBy, AtsAttributeTypes.ReviewedByDate);
   }

   @Override
   public void handleSelection() {
      XResultData rd = checkReviewedBy(artifact);
      if (rd.isErrors()) {
         return;
      }
      super.handleSelection();
   }

   public static XResultData checkReviewedBy(ArtifactToken artifact) {
      XResultData rd = new XResultData();
      for (IAtsWorkItemHook wiHook : AtsApiService.get().getWorkItemService().getWorkItemHooks()) {
         wiHook.isModifiableAttribute(artifact, AtsAttributeTypes.ReviewedBy, rd);
         if (rd.isErrors()) {
            AWorkbench.popup("A current assignee can not signoff the estimate");
            return rd;
         }
      }
      return rd;
   }

}
