/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.access.PolicyTableColumns;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Marc Potter
 */
public class PolicyTableXViewerFactory extends SkynetXViewerFactory {

   public static final String NAMESPACE = "branchAccessViewer";

   public PolicyTableXViewerFactory() {
      super(NAMESPACE, new OseeTreeReportAdapter("Table Report - Policy View"));
      PolicyTableColumns[] columns = PolicyTableColumns.values();
      XViewerColumn[] xColumns = new XViewerColumn[columns.length];
      for (int i = 0; i < columns.length; i++) {
         xColumns[i] = columns[i].getXViewerColumn();
      }
      registerColumns(xColumns);
   }
}
