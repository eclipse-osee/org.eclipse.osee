/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shawn F. Cook
 */
@Component(service = XWidget.class, immediate = true)
public class XWrkBranchButtonArtifactExplorerWidget extends XAbstractWrkBranchButtonWidget {

   public static final WidgetId ID = WidgetIdAts.XWrkBranchButtonArtifactExplorerWidget;

   public XWrkBranchButtonArtifactExplorerWidget() {
      super(ID);
   }

   @Override
   protected void initButton(Button button) {
      button.setToolTipText("Show Artifact Explorer");
      button.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EXPLORER));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            try {
               ArtifactExplorer.exploreBranch(getArtifact().getWorkingBranch());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(
         !disableAll && getWorkingBranch().isValid() && getStatus().isChangesPermitted() && isWidgetAllowedInCurrentState());
   }

   @Override
   protected boolean isWidgetAllowedInCurrentState() {
      return isWidgetInState(XWrkBranchButtonArtifactExplorerWidget.class.getSimpleName());
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return null;
   }

}
