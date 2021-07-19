/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.taskest;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.workflow.task.mini.MiniTaskXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstXViewerFactory extends MiniTaskXViewerFactory {

   public final static String NAMESPACE = "TaskEstXViewer";
   public static XViewerColumn Check_Col = new XViewerColumn("ats.taskest.check", "Select", 53, XViewerAlign.Left, true,
      SortDataType.String, false, "Check and plus to create canned tasks.  Add task to create manual tasks.");
   public static XViewerColumn Attachments_Col = new XViewerColumn("ats.taskest.attachments", "Attachments", 20,
      XViewerAlign.Left, true, SortDataType.String, false, "Shows number of attachments.  Double-click to open task.");
   public static XViewerColumn Related_Wf_Col = new XViewerColumn("ats.taskest.related.wf", "Related Workflow", 200,
      XViewerAlign.Left, true, SortDataType.String, false, "Show related Team Workflow, if created");

   public XTaskEstXViewerFactory() {
      super(NAMESPACE);
   }

   @Override
   protected void addPreColumns(List<XViewerColumn> cols) {
      cols.add(Check_Col);
      cols.add(Attachments_Col);
   }

   @Override
   protected void addPostColumns(List<XViewerColumn> cols) {
      cols.add(Related_Wf_Col);
   }

}
