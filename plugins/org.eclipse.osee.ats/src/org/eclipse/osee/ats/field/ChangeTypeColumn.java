/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ChangeTypeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class ChangeTypeColumn extends XViewerAtsAttributeValueColumn {

   public static final IAttributeType ChangeTypeAttribute = new AtsAttributeTypes("AAMFEc+MwGHnPCv7HlgA",
      "Change Type", "Type of change.");
   public static ChangeTypeColumn instance = new ChangeTypeColumn();

   public static ChangeTypeColumn getInstance() {
      return instance;
   }

   private ChangeTypeColumn() {
      super(ChangeTypeAttribute, 22, SWT.CENTER, true, SortDataType.String, false);
   }

   public ChangeTypeColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ChangeTypeColumn copy() {
      return new ChangeTypeColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   public static boolean promptChangeType(AbstractWorkflowArtifact sma, boolean persist) {
      if (sma.isTeamWorkflow()) {
         return promptChangeType(Arrays.asList((TeamWorkFlowArtifact) sma), persist);
      }
      return false;
   }

   public static boolean promptChangeType(final Collection<? extends TeamWorkFlowArtifact> teams, boolean persist) {

      for (TeamWorkFlowArtifact team : teams) {
         if (team.isReleased() || team.isVersionLocked()) {
            AWorkbench.popup("ERROR",
               "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
            return false;
         }
      }
      final ChangeTypeDialog dialog = new ChangeTypeDialog(Displays.getActiveShell());
      try {
         if (teams.size() == 1) {
            ChangeType changeType = getChangeType(teams.iterator().next());
            if (changeType != null) {
               dialog.setSelected(changeType);
            }
         }
         if (dialog.open() == 0) {

            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Type");

            ChangeType newChangeType = dialog.getSelection();
            for (TeamWorkFlowArtifact team : teams) {
               ChangeType currChangeType = getChangeType(team);
               if (currChangeType != newChangeType) {
                  setChangeType(team, newChangeType);
                  team.saveSMA(transaction);
               }
            }
            transaction.execute();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // Only prompt change for sole attribute types
         if (AttributeTypeManager.getType(getAttributeType()).getMaxOccurrences() != 1) {
            return false;
         }
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
            boolean modified =
               promptChangeType(Arrays.asList((TeamWorkFlowArtifact) useArt), isPersistViewer(treeColumn));
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist priority via alt-left-click");
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

   public static String getChangeTypeStr(Artifact artifact) throws OseeCoreException {
      ChangeType changeType = getChangeType(artifact);
      if (changeType == ChangeType.None) {
         return "";
      }
      return changeType.name();
   }

   public static ChangeType getChangeType(Artifact artifact) throws OseeCoreException {
      return ChangeType.getChangeType(artifact.getSoleAttributeValue(ChangeTypeAttribute, ""));
   }

   public static void setChangeType(Artifact artifact, ChangeType changeType) throws OseeCoreException {
      artifact.setSoleAttributeValue(ChangeTypeAttribute, changeType.name());
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) {
      try {
         Artifact useArt = getParentTeamWorkflowOrArtifact(element);
         if (useArt != null) {
            ChangeType changeType = getChangeType(useArt);
            if (changeType != null) {
               return changeType.getImage();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   public static void resetChangeTypeOffChildren(ActionArtifact actionArt) throws OseeCoreException {
      ChangeType changeType = null;
      Collection<TeamWorkFlowArtifact> teamArts = actionArt.getTeamWorkFlowArtifacts();
      if (teamArts.size() == 1) {
         changeType = getChangeType(teamArts.iterator().next());
      } else {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (!team.isCancelled()) {
               if (changeType == null) {
                  changeType = getChangeType(team);
               } else if (changeType != getChangeType(team)) {
                  return;
               }
            }
         }
      }
      if (changeType != null && getChangeType(actionArt) != changeType) {
         setChangeType(actionArt, changeType);
      }
   }

}
