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
package org.eclipse.osee.ats.ide.util.widgets.role;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class UserRoleXViewerFactory extends SkynetXViewerFactory {

   private final static String NAMESPACE = "UserRoleXViewer";

   public static XViewerColumn Role_Col =
      new XViewerColumn("osee.userRole.role", "Role", 100, XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn User_Col =
      new XViewerColumn("osee.userRole.user", "User", 150, XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Hours_Spent_Col = new XViewerColumn("osee.userRole.hoursSpent", "Hours Spent", 80,
      XViewerAlign.Left, true, SortDataType.Float, false, null);
   public static XViewerColumn Completed_Col = new XViewerColumn("osee.userRole.completed", "Completed", 80,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Num_Major_Col = new XViewerColumn("osee.userRole.major", "Major", 60,
      XViewerAlign.Center, true, SortDataType.Integer, false, null);
   public static XViewerColumn Num_Minor_Col = new XViewerColumn("osee.userRole.minor", "Minor", 60,
      XViewerAlign.Center, true, SortDataType.Integer, false, null);
   public static XViewerColumn Num_Issues_Col = new XViewerColumn("osee.userRole.issues", "Issues", 60,
      XViewerAlign.Center, true, SortDataType.Integer, false, null);

   public UserRoleXViewerFactory() {
      super(NAMESPACE, new OseeTreeReportAdapter("Table Report - User Roles"));
      registerColumns(Role_Col, User_Col, Hours_Spent_Col, Completed_Col, Num_Major_Col, Num_Minor_Col, Num_Issues_Col);
   }

}
