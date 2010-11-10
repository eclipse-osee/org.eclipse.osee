/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class GoalOrderVoteColumn extends XViewerAtsAttributeValueColumn {

   public static GoalOrderVoteColumn instance = new GoalOrderVoteColumn();

   public static GoalOrderVoteColumn getInstance() {
      return instance;
   }

   public GoalOrderVoteColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".goalOrderVote", AtsAttributeTypes.GoalOrderVote, 40, SWT.LEFT,
         false, SortDataType.String, true);
   }

   public GoalOrderVoteColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public GoalOrderVoteColumn copy() {
      return new GoalOrderVoteColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getSoleAttributeValue(AtsAttributeTypes.GoalOrderVote, "");
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // Only prompt change for sole attribute types
         if (AttributeTypeManager.getType(getAttributeType()).getMaxOccurrences() != 1) {
            return false;
         }
         Artifact artifact = (Artifact) treeItem.getData();
         if (artifact.isAttributeTypeValid(getAttributeType())) {
            boolean modified =
               PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) treeItem.getData(),
                  getAttributeType(), false, isMultiLineStringAttribute());
            if (modified && isPersistViewer()) {
               artifact.persist("persist goal order vote via alt-left-click");
            }
            if (modified) {
               ((XViewerColumn) treeColumn.getData()).getTreeViewer().update(artifact, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

}
