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

import java.net.URL;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public abstract class XAbstractOpenSprintBurnupArtWidget extends XButtonWidget {

   protected IAgileSprint sprint;
   private final boolean editable = false;
   private final String id;

   public XAbstractOpenSprintBurnupArtWidget(WidgetId widgetId, String name, String id) {
      super(widgetId, name, AtsImage.REPORT);
      this.id = id;
      setToolTip("Click to run Open Report");
      addXModifiedListener(listener);
   }

   protected Artifact getAgileTeam() {
      return AtsApiService.get().getQueryServiceIde().getArtifact(
         AtsApiService.get().getRelationResolver().getRelatedOrNull(sprint,
            AtsRelationTypes.AgileTeamToSprint_AgileTeam));
   };

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      XHyperlinkLabelWidget external = new XHyperlinkLabelWidget("Open Externally", getUrl(), true);
      external.createWidgets(comp, horizontalSpan);
      external.getControl().setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
      external.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            openExternally();
         }

      });
   }

   public void openExternally() {
      String url = getUrl();
      Program.launch(url);
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

   @SuppressWarnings("deprecation")
   public void openInternally() {
      IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
      try {
         IWebBrowser browser = browserSupport.createBrowser(id + sprint.getIdString());
         String url = getUrl();
         browser.openURL(new URL(url));
      } catch (Exception ex) {
         OseeLog.log(XAbstractOpenSprintBurnupArtWidget.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   abstract String getUrl();

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = AtsApiService.get().getAgileService().getAgileSprint(artifact);
      }
   }

   @Override
   public boolean isEditable() {
      return editable;
   }

}
