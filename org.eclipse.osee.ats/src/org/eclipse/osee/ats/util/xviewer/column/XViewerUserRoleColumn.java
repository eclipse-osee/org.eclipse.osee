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
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerUserRoleColumn extends XViewerValueColumn {

   private final User user;

   public XViewerUserRoleColumn(String name, XViewer viewer, int columnNum, User user) {
      super(viewer, "Role", "", 75, 75, SWT.LEFT);
      this.user = user;
      setOrderNum(columnNum);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column) throws OseeCoreException, SQLException {
      if (element instanceof ReviewSMArtifact) {
         String str = "";
         for (UserRole role : ((ReviewSMArtifact) element).getUserRoleManager().getUserRoles()) {
            if (role.getUser().equals(user)) str += role.getRole().name() + ", ";
         }
         return str.replaceFirst(", $", "");
      }
      return "";
   }

}
