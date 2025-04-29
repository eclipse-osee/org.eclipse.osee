/*******************************************************************************
 * Copyright (c) 2025 Boeing.
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

package org.eclipse.osee.ats.ide.workflow.pr.view;

import static org.eclipse.nebula.widgets.xviewer.core.model.SortDataType.String;
import static org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign.Left;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

public class ProblemrReportViewColumns {

   // @formatter:off

   public static XViewerColumn ArtifactTypeCol = new XViewerColumn("ams.pr.type", "Artifact Type", 250, Left, true, String, false, "");
   public static XViewerColumn AtsIdCol = new XViewerColumn("ams.pr.ats.id", "ATS ID", 70, Left, true, String, false, "");
   public static XViewerColumn NameCol = new XViewerColumn("ams.pr.name", "Title/Name", 200, Left, true, String, false, "");
   public static XViewerColumn ProgramCol = new XViewerColumn("ams.pr.program", "Program", 90, Left, true, String, false, "");
   public static XViewerColumn StateCol = new XViewerColumn("ams.pr.state", "State", 70, Left, true, String, false, "");
   public static XViewerColumn BIStatusCol = new XViewerColumn("ams.pr.bis.tatus", "Build Impact Status", 90, Left, true, String, false, "");
   public static XViewerColumn IdCol = new XViewerColumn("ams.pr.id", "ID", 60, Left, true, String, false, "");

   // @formatter:on

   private ProblemrReportViewColumns() {
   }

}
