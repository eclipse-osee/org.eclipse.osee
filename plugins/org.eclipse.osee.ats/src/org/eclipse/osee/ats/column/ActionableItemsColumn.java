/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class ActionableItemsColumn extends XViewerAtsAttributeValueColumn {

   public static ActionableItemsColumn instance = new ActionableItemsColumn();

   public static ActionableItemsColumn getInstance() {
      return instance;
   }

   private ActionableItemsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".actionableItems", AtsAttributeTypes.ActionableItem, 80, SWT.LEFT,
         true, SortDataType.String, false);
   }

   public ActionableItemsColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActionableItemsColumn copy() {
      return new ActionableItemsColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return getActionableItemsStr(element);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   public static String getActionableItemsStr(Object element) throws OseeCoreException {
      return Artifacts.commaArts(getActionableItems(element));
   }

   public static Collection<ActionableItemArtifact> getActionableItems(Object element) throws OseeCoreException {
      if (element instanceof ActionArtifact) {
         Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
         // Roll up if same for all children
         for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
            aias.addAll(team.getActionableItemsDam().getActionableItems());
         }
         return aias;
      } else if (element instanceof TeamWorkFlowArtifact) {
         return ((TeamWorkFlowArtifact) element).getActionableItemsDam().getActionableItems();
      } else if (element instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         return getActionableItems(teamArt);
      }
      return Collections.emptyList();
   }
}
