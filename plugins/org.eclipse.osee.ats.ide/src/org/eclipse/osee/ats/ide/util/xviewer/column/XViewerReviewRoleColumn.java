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
package org.eclipse.osee.ats.ide.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class XViewerReviewRoleColumn extends XViewerValueColumn {

   private final AtsUser user;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public XViewerReviewRoleColumn copy() {
      return new XViewerReviewRoleColumn(getUser(), getId(), getName(), getWidth(), getAlign(), isShow(),
         getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   public XViewerReviewRoleColumn(AtsUser user) {
      super("ats.column.role", "Role", 75, XViewerAlign.Left, true, SortDataType.String, false, null);
      this.user = user;
   }

   public XViewerReviewRoleColumn(AtsUser user, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.user = user;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      try {
         if (element instanceof AbstractReviewArtifact) {
            return getRolesStr((AbstractReviewArtifact) element, user);
         }
         return "";
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

   private static String getRolesStr(AbstractReviewArtifact reviewArt, AtsUser user) {
      StringBuilder builder = new StringBuilder();
      IAtsPeerReviewRoleManager roleMgr = ((PeerToPeerReviewArtifact) reviewArt).getRoleManager();
      for (UserRole role : roleMgr.getUserRoles()) {
         if (role.getUserId().equals(user.getUserId())) {
            builder.append(role.getRole().name());
            builder.append(", ");
         }
      }

      return builder.toString().replaceFirst(", $", "");
   }

   public AtsUser getUser() {
      return user;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (user == null ? 0 : user.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      XViewerReviewRoleColumn other = (XViewerReviewRoleColumn) obj;
      if (user == null) {
         if (other.user != null) {
            return false;
         }
      } else if (!user.equals(other.user)) {
         return false;
      }
      return true;
   }

}
