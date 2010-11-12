/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionLockedType;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TargetedVersionColumn extends XViewerAtsColumn implements IXViewerValueColumn, IAltLeftClickProvider, IMultiColumnEditProvider {

   public static TargetedVersionColumn instance = new TargetedVersionColumn();

   public static TargetedVersionColumn getInstance() {
      return instance;
   }

   private TargetedVersionColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".versionTarget", "Targeted Version", 40, SWT.LEFT, true,
         SortDataType.String, true, "Date this workflow transitioned to the Completed state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TargetedVersionColumn copy() {
      TargetedVersionColumn newXCol = new TargetedVersionColumn();
      copy(this, newXCol);
      return newXCol;
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
            boolean modified =
               promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) useArt),
                  AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
                  AtsUtil.isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked, true);
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

   public static boolean promptChangeVersion(AbstractWorkflowArtifact sma, VersionReleaseType versionReleaseType, VersionLockedType versionLockType, boolean persist) throws OseeCoreException {
      if (AtsUtil.isAtsAdmin() && !sma.isTeamWorkflow()) {
         AWorkbench.popup("ERROR ", "Cannot set version for: \n\n" + sma.getName());
         return false;
      }
      return promptChangeVersion(Arrays.asList((TeamWorkFlowArtifact) sma), versionReleaseType, versionLockType,
         persist);
   }

   public static boolean promptChangeVersion(final Collection<? extends TeamWorkFlowArtifact> smas, VersionReleaseType versionReleaseType, VersionLockedType versionLockType, final boolean persist) throws OseeCoreException {
      TeamDefinitionArtifact teamDefHoldingVersions = null;
      for (TeamWorkFlowArtifact teamArt : smas) {
         if (!teamArt.getTeamDefinition().isTeamUsesVersions()) {
            AWorkbench.popup("ERROR", "Team \"" + teamArt.getTeamDefinition().getName() + "\" doesn't use versions.");
            return false;
         }
         if (teamArt.isReleased() || teamArt.isVersionLocked()) {
            String error =
               "Team Workflow\n \"" + teamArt.getName() + "\"\n targeted version is locked or already released.";
            if (AtsUtil.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
               error + "\n\nOverride?")) {
               return false;
            } else if (!AtsUtil.isAtsAdmin()) {
               AWorkbench.popup("ERROR", error);
            }
         }
         if (teamDefHoldingVersions != null && teamDefHoldingVersions != teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions()) {
            AWorkbench.popup("ERROR", "Can't change version on Workflows that have different release version sets.");
            return false;
         }
         if (teamDefHoldingVersions == null) {
            teamDefHoldingVersions = teamArt.getTeamDefinition().getTeamDefinitionHoldingVersions();
         }
      }
      if (teamDefHoldingVersions == null) {
         AWorkbench.popup("ERROR", "No versions configured for impacted team(s).");
         return false;
      }
      final VersionListDialog vld =
         new VersionListDialog("Select Version", "Select Version", teamDefHoldingVersions.getVersionsArtifacts(
            versionReleaseType, versionLockType));
      if (smas.size() == 1 && smas.iterator().next().getTargetedVersion() != null) {
         Object[] objs = new Object[1];
         objs[0] = smas.iterator().next().getTargetedVersion();
         vld.setInitialSelections(objs);
      }
      int result = vld.open();
      if (result != 0) {
         return false;
      }
      Object obj = vld.getResult()[0];
      VersionArtifact newVersion = (VersionArtifact) obj;
      //now check selected version
      if (newVersion.isVersionLocked()) {
         String error = "Version \"" + newVersion.getFullDisplayName() + "\" is locked or already released.";
         if (AtsUtil.isAtsAdmin() && !MessageDialog.openConfirm(Displays.getActiveShell(), "Change Version",
            error + "\n\nOverride?")) {
            return false;
         } else if (!AtsUtil.isAtsAdmin()) {
            AWorkbench.popup("ERROR", error);
         }
      }

      for (TeamWorkFlowArtifact teamArt : smas) {
         teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
            java.util.Collections.singleton(newVersion));
      }
      if (persist) {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "ATS Prompt Change Version");
         for (TeamWorkFlowArtifact teamArt : smas) {
            teamArt.persist(transaction);
         }
         transaction.execute();
      }
      return true;
   }

   public static VersionArtifact getTargetedVersion(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            if (teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
               List<Artifact> verArts =
                  teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               if (verArts.size() > 1) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     "Multiple targeted versions for artifact " + teamArt.toStringWithId());
                  return (VersionArtifact) verArts.iterator().next();
               } else {
                  return (VersionArtifact) verArts.iterator().next();
               }
            }
         }
      }
      return null;
   }

   public static String getTargetedVersionStr(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            Collection<VersionArtifact> verArts =
               teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
                  VersionArtifact.class);
            if (verArts.isEmpty()) {
               return "";
            }
            if (verArts.size() > 1) {
               String errStr =
                  "Workflow " + teamArt.getHumanReadableId() + " targeted for multiple versions: " + Artifacts.commaArts(verArts);
               OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr, null);
               return XViewerCells.getCellExceptionString(errStr);
            }
            VersionArtifact verArt = verArts.iterator().next();
            if (!teamArt.isCompleted() && !teamArt.isCancelled() && verArt.getSoleAttributeValue(
               AtsAttributeTypes.Released, false)) {
               String errStr =
                  "Workflow " + teamArt.getHumanReadableId() + " targeted for released version, but not completed: " + verArt;
               if (!teamArt.isTargetedErrorLogged()) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, errStr, null);
                  teamArt.setTargetedErrorLogged(true);
               }
               return XViewerCells.getCellExceptionString(errStr);
            }
            return verArt.getName();
         }
      }
      return "";
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               String str = team.getTargetedVersionStr();
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(";", strs);

         } else {
            return getTargetedVersionStr(element);
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<TeamWorkFlowArtifact> smas = new HashSet<TeamWorkFlowArtifact>();
         List<Artifact> arts = new ArrayList<Artifact>();
         for (TreeItem item : treeItems) {
            Artifact art = (Artifact) item.getData();
            if (art instanceof TeamWorkFlowArtifact) {
               smas.add((TeamWorkFlowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeVersion(smas, AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
            AtsUtil.isAtsAdmin() ? VersionLockedType.Both : VersionLockedType.UnLocked, true);
         getXViewer().update(smas.toArray(), null);
         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
