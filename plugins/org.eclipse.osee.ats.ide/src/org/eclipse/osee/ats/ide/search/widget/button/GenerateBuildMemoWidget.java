/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonPushWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class GenerateBuildMemoWidget {

   public static final String GENERATE_BUILD_MEMO = "Generate Build Memo";
   private final WorldEditorParameterSearchItem searchItem;
   private final String memoName;

   public GenerateBuildMemoWidget(WorldEditorParameterSearchItem searchItem) {
      this(searchItem, GENERATE_BUILD_MEMO);
   }

   public GenerateBuildMemoWidget(WorldEditorParameterSearchItem searchItem, String memoName) {
      this.searchItem = searchItem;
      this.memoName = memoName;
   }

   public void addWidget(XWidgetBuilderAts wba) {
      addWidget(wba, 0);
   }

   public void addWidget(XWidgetBuilderAts wba, int beginComposite) {
      wba.andButtonPushWidget(GENERATE_BUILD_MEMO);
      wba.startComp(beginComposite);
   }

   public XXButtonPushWidget getWidget() {
      return (XXButtonPushWidget) searchItem.getxWidgets().get(memoName);
   }

   public void set(AtsSearchData data) {
      // do nothing
   }

   public void setup(XWidget widget) {
      // do nothing
   }

   public void widgetCreated(WorldEditor worldEditor, XWidget widget, FormToolkit toolkit, Artifact art,
      XWidgetSwtRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      if (widget.getLabel().equals(GENERATE_BUILD_MEMO)) {
         XXButtonPushWidget button = (XXButtonPushWidget) widget;
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               List<Artifact> loadedArtifacts = worldEditor.getWorldComposite().getLoadedArtifacts();
               if (loadedArtifacts.isEmpty()) {
                  AWorkbench.popup(ProblemReportBuildMemoOps.NOTHING_LOADED);
                  return;
               }
               ProblemReportBuildMemoOps ops = getProblemReportBuildMemoOps(worldEditor, memoName, BuildMemoType.Full);
               ops.generateAndOpen();
            }

         });
      }
   }

   protected ProblemReportBuildMemoOps getProblemReportBuildMemoOps(WorldEditor worldEditor, String buildMemo,
      BuildMemoType buildMemoType) {
      return new ProblemReportBuildMemoOps(worldEditor, buildMemo, BuildMemoType.Full);
   }

}
