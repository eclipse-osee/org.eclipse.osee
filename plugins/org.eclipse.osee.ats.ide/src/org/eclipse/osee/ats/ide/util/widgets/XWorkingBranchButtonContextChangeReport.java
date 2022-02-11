/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Branden W. Phillips
 */
public class XWorkingBranchButtonContextChangeReport extends XWorkingBranchButtonAbstract {

   @Override
   protected void initButton(Button button) {
      button.setToolTipText("Generate Context Change Report");
      button.setImage(ImageManager.getImage(AtsImage.CONTEXT_CHANGE_REPORT));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            AtsApiService.get().getBranchServiceIde().generateContextChangeReport(getTeamArt());
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(
         !disableAll && (isWorkingBranchInWork() || isCommittedBranchExists()) && isWidgetAllowedInCurrentState());
   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return true;
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return null;
   }
}
