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
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerReviewRoleColumn extends XViewerValueColumn {

   private final User user;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   @Override
   public XViewerReviewRoleColumn copy() {
      return new XViewerReviewRoleColumn(getUser(), getId(), getName(), getWidth(), getAlign(), isShow(),
            getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   public XViewerReviewRoleColumn(User user) {
      super("ats.column.role", "Role", 75, SWT.LEFT, true, SortDataType.String, false, null);
      this.user = user;
   }

   public XViewerReviewRoleColumn(User user, String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.user = user;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      try {
         if (element instanceof ReviewSMArtifact) {
            return getRolesStr((ReviewSMArtifact) element, user);
         }
         return "";
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

   private static String getRolesStr(ReviewSMArtifact reviewArt, User user) throws OseeCoreException {
      String str = "";
      for (UserRole role : reviewArt.getUserRoleManager().getUserRoles()) {
         if (role.getUser().equals(user)) str += role.getRole().name() + ", ";
      }
      return str.replaceFirst(", $", "");
   }

   public User getUser() {
      return user;
   }

}
