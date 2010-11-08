/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class ReleaseDateColumn extends XViewerAtsAttributeValueColumn implements IMultiColumnEditProvider {

   public static final IAttributeType ReleaseDate = new AtsAttributeTypes("AAMFEc3+cGcMDOCdmdAA", "Release Date",
      "Date the changes were made available to the users.");
   public static ReleaseDateColumn instance = new ReleaseDateColumn();

   public static ReleaseDateColumn getInstance() {
      return instance;
   }

   private ReleaseDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".releaseDate", ReleaseDate, 80, SWT.LEFT, false, SortDataType.Date,
         true);
   }

   public ReleaseDateColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReleaseDateColumn copy() {
      return new ReleaseDateColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   public static boolean promptChangeReleaseDate(AbstractWorkflowArtifact sma) {
      if (sma.isReleased() || sma.isVersionLocked()) {
         AWorkbench.popup("ERROR", "Team Workflow\n \"" + sma.getName() + "\"\n version is locked or already released.");
         return false;
      }
      try {
         VersionArtifact verArt = sma.getTargetedVersion();
         if (verArt != null) {
            // prompt that this object is assigned to a version that is targeted
            // for release xxx - want to change?
            DateSelectionDialog diag =
               new DateSelectionDialog(
                  "Select Release Date Date",
                  "Warning: " + sma.getArtifactTypeName() + "'s release date is handled\n" + "by targeted for version \"" + verArt.getName() + "\"\n" + "changing the date here will change the\n" + "date for this entire release.\n\nSelect date to change.\n",
                  verArt.getReleaseDate());
            if (verArt.getReleaseDate() != null) {
               diag.setSelectedDate(verArt.getReleaseDate());
            }
            if (diag.open() == 0) {
               verArt.setSoleAttributeValue(ReleaseDate, diag.getSelectedDate());
               verArt.persist();
               return true;
            }
         } else {
            // prompt that current release is (get from attribute) - want to change?
            DateSelectionDialog diag =
               new DateSelectionDialog("Select Release Date", "Select Release Date",
                  ReleaseDateColumn.getReleaseDate(sma));
            if (diag.open() == 0) {
               sma.setSoleAttributeValue(ReleaseDate, diag.getSelectedDate());
               sma.persist();
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't save release date " + sma.getHumanReadableId(), ex);
      }
      return false;
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
            TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) useArt;
            if (team.isReleased() || team.isVersionLocked()) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
               return false;
            }

            boolean modified = promptChangeReleaseDate((TeamWorkFlowArtifact) useArt);
            XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist release date via alt-left-click");
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

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<TeamWorkFlowArtifact> smas = new HashSet<TeamWorkFlowArtifact>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
         if (art instanceof TeamWorkFlowArtifact) {
            smas.add((TeamWorkFlowArtifact) art);
         }
      }
      if (smas.size() == 0) {
         AWorkbench.popup("Must select Team Workflows");
         return;
      }
      PromptChangeUtil.promptChangeAttribute(smas, ReleaseDate, true, false);
      getXViewer().update(smas.toArray(), null);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               strs.add(getColumnText(team, column, columnIndex));
            }
            return Collections.toString(";", strs);

         } else if (element instanceof AbstractWorkflowArtifact) {
            Date date = getReleaseDate(element);
            if (date != null) {
               DateUtil.getMMDDYYHHMM(date);
            }
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public static String getReleaseDateStr(Object object) throws OseeCoreException {
      return DateUtil.getMMDDYYHHMM(getReleaseDate(object));
   }

   public static Date getReleaseDate(Object object) throws OseeCoreException {
      if (object instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) object;
         Collection<VersionArtifact> vers =
            teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, VersionArtifact.class);
         Date date = null;
         if (vers.isEmpty()) {
            date = teamArt.getSoleAttributeValue(ReleaseDate, null);
         } else {
            date = vers.iterator().next().getReleaseDate();
            if (date == null) {
               date = teamArt.getSoleAttributeValue(ReleaseDate, null);
            }
         }
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getReleaseDate(teamArt);
         }
      }
      return null;
   }
}
