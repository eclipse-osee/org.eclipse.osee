/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import java.sql.SQLException;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws OseeCoreException, SQLException {
      if (element instanceof ReviewSMArtifact) {
         return getRolesStr((ReviewSMArtifact) element, user);
      }
      return "";
   }

   private static String getRolesStr(ReviewSMArtifact reviewArt, User user) throws OseeCoreException, SQLException {
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
