/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.agile;

import java.net.URL;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabel;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXOpenSprintBurnupButton extends XButton implements IArtifactWidget {

   protected IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = AbstractXOpenSprintBurnupButton.class.getSimpleName();
   private final String id;

   public AbstractXOpenSprintBurnupButton(String name, String id) {
      super(name);
      this.id = id;
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to run Open Report");
      addXModifiedListener(listener);
   }

   @Override
   public Artifact getArtifact() {
      return AtsClientService.get().getQueryServiceClient().getArtifact(sprint);
   }

   protected Artifact getAgileTeam() {
      return AtsClientService.get().getQueryServiceClient().getArtifact(
         AtsClientService.get().getRelationResolver().getRelatedOrNull(sprint,
            AtsRelationTypes.AgileTeamToSprint_AgileTeam));
   };

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      XHyperlinkLabel external = new XHyperlinkLabel("Open Externally", getUrl(), true);
      external.createWidgets(bComp, horizontalSpan);
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

   public void openInternally() {
      IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
      try {
         IWebBrowser browser = browserSupport.createBrowser(id + sprint.getIdString());
         String url = getUrl();
         browser.openURL(new URL(url));
      } catch (Exception ex) {
         OseeLog.log(AbstractXOpenSprintBurnupButton.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   abstract String getUrl();

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.AgileSprint)) {
         this.sprint = (IAgileSprint) artifact;
      }
   }

   @Override
   public boolean isEditable() {
      return editable;
   }

}
