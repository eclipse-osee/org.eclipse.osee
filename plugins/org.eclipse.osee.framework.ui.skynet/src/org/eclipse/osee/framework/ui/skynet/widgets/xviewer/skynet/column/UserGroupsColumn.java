/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Jeremy A. Midvidy
 */
public class UserGroupsColumn extends XViewerValueColumn {

   public static final String FRAMEWORK_USER_GROUPS = "framework.userGroups";

   public UserGroupsColumn(boolean show) {
      super(FRAMEWORK_USER_GROUPS, "User Groups", 100, XViewerAlign.Left, show, SortDataType.String, false,
         "Displays all of the groups this user belongs to.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public UserGroupsColumn copy() {
      UserGroupsColumn newXCol = new UserGroupsColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof User) {
            Collection<Artifact> userGroups = ((User) element).getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
            if (userGroups.isEmpty()) {
               return "";
            }
            String strUserGroupsOut = "";
            for (Artifact usr : userGroups) {
               strUserGroupsOut += usr.toStringWithId() + ", ";
            }
            return strUserGroupsOut.substring(0, strUserGroupsOut.length() - 2);
         } else {
            return "";
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

}
