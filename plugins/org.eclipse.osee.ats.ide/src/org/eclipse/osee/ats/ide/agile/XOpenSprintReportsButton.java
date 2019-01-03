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

import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
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
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XOpenSprintReportsButton extends XButton implements IArtifactWidget {

   protected IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = XOpenSprintReportsButton.class.getSimpleName();

   public XOpenSprintReportsButton() {
      super("Open Current Sprint Reports");
      setImage(ImageManager.getImage(AtsImage.REPORT));
      setToolTip("Click to run Open Reports");
      addXModifiedListener(listener);
   }

   @Override
   public Artifact getArtifact() {
      return AtsClientService.get().getQueryServiceClient().getArtifact(sprint);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      XHyperlinkLabel external = new XHyperlinkLabel("Open Externally");
      external.setAddDefaultListener(false);
      external.createWidgets(bComp, horizontalSpan);
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
      XOpenSprintBurndownButton down = new XOpenSprintBurndownButton();
      down.setArtifact(getArtifact());
      down.openExternally();

      XOpenSprintBurnupButton up = new XOpenSprintBurnupButton();
      up.setArtifact(getArtifact());
      up.openExternally();

      XOpenSprintSummaryButton sum = new XOpenSprintSummaryButton();
      sum.setArtifact(getArtifact());
      sum.openExternally();

      XOpenSprintDataTableButton data = new XOpenSprintDataTableButton();
      data.setArtifact(getArtifact());
      data.openExternally();
   }

   public void openInternally() {
      XOpenSprintBurndownButton down = new XOpenSprintBurndownButton();
      down.setArtifact(getArtifact());
      down.openInternally();

      XOpenSprintBurnupButton up = new XOpenSprintBurnupButton();
      up.setArtifact(getArtifact());
      up.openInternally();

      XOpenSprintSummaryButton sum = new XOpenSprintSummaryButton();
      sum.setArtifact(getArtifact());
      sum.openInternally();

      XOpenSprintDataTableButton data = new XOpenSprintDataTableButton();
      data.setArtifact(getArtifact());
      data.openInternally();
   }

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
