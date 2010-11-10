/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class OriginatorColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static OriginatorColumn instance = new OriginatorColumn();

   public static OriginatorColumn getInstance() {
      return instance;
   }

   private OriginatorColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".originator", "Originator", 80, SWT.LEFT, false,
         SortDataType.String, true, null);
   }

   public OriginatorColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OriginatorColumn copy() {
      return new OriginatorColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = (Artifact) treeItem.getData();
            if (useArt instanceof ActionArtifact) {
               if (((ActionArtifact) useArt).getTeamWorkFlowArtifacts().size() == 1) {
                  useArt = ((ActionArtifact) useArt).getTeamWorkFlowArtifacts().iterator().next();
               } else {
                  return false;
               }
            }
            if (!(useArt instanceof TeamWorkFlowArtifact)) {
               return false;
            }
            boolean modified = promptChangeOriginator(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer());
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist goals via alt-left-click");
            }
            if (modified) {
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   public static boolean promptChangeOriginator(AbstractWorkflowArtifact sma, boolean persist) throws OseeCoreException {
      return promptChangeOriginator(Arrays.asList(sma), persist);
   }

   public static boolean promptChangeOriginator(final Collection<? extends AbstractWorkflowArtifact> smas, boolean persist) throws OseeCoreException {
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select New Originator", Active.Active);
      int result = ld.open();
      if (result == 0) {
         User selectedUser = ld.getSelection();

         SkynetTransaction transaction = null;
         if (persist) {
            transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Originator");
         }

         for (AbstractWorkflowArtifact sma : smas) {
            sma.setOriginator(selectedUser);
            if (persist) {
               sma.persist(transaction);
            }
         }
         if (persist) {
            transaction.execute();
         }
         return true;
      }
      return false;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return ((AbstractWorkflowArtifact) element).getOriginatorStr();
         }
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               strs.add(team.getOriginatorStr());
            }
            return Collections.toString("; ", strs);
         }
         return "";
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> smas = new HashSet<AbstractWorkflowArtifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof AbstractWorkflowArtifact) {
               smas.add((AbstractWorkflowArtifact) art);
            }
         }
         promptChangeOriginator(smas, true);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
