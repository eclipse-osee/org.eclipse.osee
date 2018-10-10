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
package org.eclipse.osee.ats.agile;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
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
public class XOpenStoredSprintReportsButton extends XButton implements IArtifactWidget {

   protected IAgileSprint sprint;
   private final boolean editable = false;
   public static final String WIDGET_ID = XOpenStoredSprintReportsButton.class.getSimpleName();

   public XOpenStoredSprintReportsButton() {
      super("Open Snapshots of Stored Sprint Charts");
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
      boolean found = false;
      for (AgileReportType rpt : AgileReportType.values()) {
         ArtifactToken rptArt = AtsClientService.get().getRelationResolver().getChildNamedOrNull(sprint, rpt.name());
         if (rptArt != null) {
            found = true;
            RendererManager.open(AtsClientService.get().getQueryServiceClient().getArtifact(rptArt),
               PresentationType.PREVIEW);
         }
      }
      if (!found) {
         AWorkbench.popup("No Stored Reports Found");
      }
   }

   public void openInternally() {
      List<IResultsEditorTab> tabs = new LinkedList<>();
      for (AgileReportType rpt : AgileReportType.values()) {
         ArtifactToken rptArt = AtsClientService.get().getRelationResolver().getChildNamedOrNull(sprint, rpt.name());
         if (rptArt != null) {
            String html = AtsClientService.get().getAttributeResolver().getSoleAttributeValue(rptArt,
               CoreAttributeTypes.NativeContent, null);
            if (Strings.isValid(html)) {
               tabs.add(new ResultsEditorHtmlTab(rpt.name(), rpt.name(), AHTML.simplePage(html)));
            }
         }
      }
      if (!tabs.isEmpty()) {
         AWorkbench.popup("No Stored Reports Found");
      } else {
         ResultsEditor.open(new IResultsEditorProvider() {

            @Override
            public String getEditorName() {
               return "Stored Sprint Charts - " + sprint.getName();
            }

            @Override
            public List<IResultsEditorTab> getResultsEditorTabs() {
               return tabs;
            }
         });

      }
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
