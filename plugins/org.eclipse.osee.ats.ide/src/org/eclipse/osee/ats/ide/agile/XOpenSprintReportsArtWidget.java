/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.agile;

import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XOpenSprintReportsArtWidget extends XButtonWidget {

   protected IAgileSprint sprint;
   public static final WidgetId ID = WidgetIdAts.XOpenSprintReportsArtWidget;

   public XOpenSprintReportsArtWidget() {
      super(ID, "Open Current Sprint Reports");
      setOseeImage(AtsImage.REPORT);
      setToolTip("Click to run Open Reports");
      addXModifiedListener(listener);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      XHyperlinkLabelWidget external = new XHyperlinkLabelWidget("Open Externally");
      external.setAddDefaultListener(false);
      external.createWidgets(comp, horizontalSpan);
      external.getControl().setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
      external.getControl().addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            openExternally();
         }
      });
   }

   XModifiedListener listener = new XModifiedListener() {
      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         try {
            openInternally();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

   };

   public void openExternally() {
      XOpenSprintBurndownArtWidget down = new XOpenSprintBurndownArtWidget();
      down.setArtifact(getArtifact());
      down.openExternally();

      XOpenSprintBurnupArtWidget up = new XOpenSprintBurnupArtWidget();
      up.setArtifact(getArtifact());
      up.openExternally();

      XOpenSprintSummaryArtWidget sum = new XOpenSprintSummaryArtWidget();
      sum.setArtifact(getArtifact());
      sum.openExternally();

      XOpenSprintDataTableArtWidget data = new XOpenSprintDataTableArtWidget();
      data.setArtifact(getArtifact());
      data.openExternally();
   }

   public void openInternally() {
      XOpenSprintBurndownArtWidget down = new XOpenSprintBurndownArtWidget();
      down.setArtifact(getArtifact());
      down.openInternally();

      XOpenSprintBurnupArtWidget up = new XOpenSprintBurnupArtWidget();
      up.setArtifact(getArtifact());
      up.openInternally();

      XOpenSprintSummaryArtWidget sum = new XOpenSprintSummaryArtWidget();
      sum.setArtifact(getArtifact());
      sum.openInternally();

      XOpenSprintDataTableArtWidget data = new XOpenSprintDataTableArtWidget();
      data.setArtifact(getArtifact());
      data.openInternally();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getArtifact().isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = AtsApiService.get().getAgileService().getAgileSprint(artifact);
      }
   }

}
