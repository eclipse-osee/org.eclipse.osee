/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.search.widget.button;

import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.ide.world.search.pr.ProblemReportBuildMemoOps;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPushWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ValidateProblemReportsWidget {

   public static final String VALIDATE_PRS = "Validate PRs";
   private final WorldEditorParameterSearchItem searchItem;
   private final String memoName;

   public ValidateProblemReportsWidget(WorldEditorParameterSearchItem searchItem) {
      this(searchItem, VALIDATE_PRS);
   }

   public ValidateProblemReportsWidget(WorldEditorParameterSearchItem searchItem, String memoName) {
      this.searchItem = searchItem;
      this.memoName = memoName;
   }

   public void addWidget(XWidgetBuilderAts wba) {
      addWidget(wba, 0);
   }

   public void addWidget(XWidgetBuilderAts wba, int beginComposite) {
      wba.andXButtonPush(memoName);
      wba.andComposite(beginComposite);
   }

   public XButtonPushWidget getWidget() {
      return (XButtonPushWidget) searchItem.getxWidgets().get(VALIDATE_PRS);
   }

   public void set(AtsSearchData data) {
      // do nothing
   }

   public void setup(XWidget widget) {
      // do nothing
   }

   public void widgetCreated(WorldEditor worldEditor, XWidget widget, FormToolkit toolkit, Artifact art,
      XWidgetSwtRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (widget.getLabel().equals(VALIDATE_PRS)) {
         XButtonPushWidget button = (XButtonPushWidget) widget;
         button.getbutton().getParent().setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
               if (loadedArtifacts.isEmpty()) {
                  AWorkbench.popup(ProblemReportBuildMemoOps.NOTHING_LOADED);
                  return;
               }
               ProblemReportBuildMemoOps ops = getProblemReportBuildMemoOps(worldEditor, memoName);
               XResultData rd = new XResultData(VALIDATE_PRS);
               ops.validateLoaded(rd);
               XResultDataUI.report(rd, VALIDATE_PRS);
            }

         });
      }
   }

   protected ProblemReportBuildMemoOps getProblemReportBuildMemoOps(WorldEditor worldEditor, String buildMemo) {
      return new ProblemReportBuildMemoOps(worldEditor, buildMemo);
   }

}
