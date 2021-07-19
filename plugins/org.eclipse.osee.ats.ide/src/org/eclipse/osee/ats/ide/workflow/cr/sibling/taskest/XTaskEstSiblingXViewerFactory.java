/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.workflow.cr.sibling.taskest;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.ide.workflow.cr.sibling.base.XSiblingXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstSiblingXViewerFactory extends XSiblingXViewerFactory {

   public final static String NAMESPACE = "TaskEstSiblingXViewer";
   public static XViewerColumn Related_Task_Col = new XViewerColumn("ats.related.task", "Related Estimating Task", 200,
      XViewerAlign.Left, true, SortDataType.String, false, "Show related Estimating Task workflow was created from.");

   public XTaskEstSiblingXViewerFactory() {
      super(NAMESPACE);
   }

   @Override
   protected void addPostColumns(List<XViewerColumn> cols) {
      cols.add(Related_Task_Col);
   }

}
