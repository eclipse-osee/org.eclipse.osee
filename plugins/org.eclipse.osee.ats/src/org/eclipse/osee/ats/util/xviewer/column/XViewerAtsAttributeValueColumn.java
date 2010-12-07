/*
 * Created on Oct 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Given ATS attribute, will return ; delimited values for AbstractWorkflowArtifact and unique rolled up values for
 * ActionArtifacts. This column will also provide default alt-left-click operation for any single value (max=1)
 * attribute.
 * 
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeValueColumn extends XViewerAtsAttributeColumn implements IAltLeftClickProvider, IMultiColumnEditProvider, IXViewerValueColumn {

   public XViewerAtsAttributeValueColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerAtsAttributeValueColumn(IAttributeType attributeType, String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   protected XViewerAtsAttributeValueColumn() {
      super();
   }

   /**
    * Returns parent team workflow, if AbstractWorkflowArtifact or Artifact, if artifact
    */
   protected Artifact getParentTeamWorkflowOrArtifact(Object element) throws OseeCoreException {
      Artifact useArt = null;
      if (element instanceof Artifact) {
         if (element instanceof AbstractWorkflowArtifact) {
            useArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         } else {
            useArt = (Artifact) element;
         }
      }
      return useArt;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return ((AbstractWorkflowArtifact) element).getAttributesToStringUnique(getAttributeType(), ";");
         }
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString("; ", strs);
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return null;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // Only prompt change for sole attribute types
         if (AttributeTypeManager.getMaxOccurrences(getAttributeType()) != 1) {
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
            boolean modified =
               PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) treeItem.getData(),
                  getAttributeType(), false, isMultiLineStringAttribute());
            if (modified && isPersistViewer()) {
               useArt.persist("persist attribute via alt-left-click");
            }
            if (modified) {
               ((XViewerColumn) treeColumn.getData()).getTreeViewer().update(useArt, null);
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
      Set<AbstractWorkflowArtifact> smas = new HashSet<AbstractWorkflowArtifact>();
      for (TreeItem item : treeItems) {
         Artifact art = (Artifact) item.getData();
         try {
            if (art instanceof AbstractWorkflowArtifact && art.isAttributeTypeValid(getAttributeType())) {
               smas.add((AbstractWorkflowArtifact) art);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      if (smas.isEmpty()) {
         AWorkbench.popup("Invalid Selection",
            String.format("No selected items valid for attribute [%s] editing", getAttributeType()));
         return;
      }
      PromptChangeUtil.promptChangeAttribute(smas, getAttributeType(), isPersistViewer(getXViewer()),
         isMultiLineStringAttribute());
      getXViewer().update(smas.toArray(), null);
   }

   /**
    * Set if promptChange should display a multi-lined dialog. Only valid for Textual attributes.
    */
   public boolean isMultiLineStringAttribute() {
      return false;
   }
}
