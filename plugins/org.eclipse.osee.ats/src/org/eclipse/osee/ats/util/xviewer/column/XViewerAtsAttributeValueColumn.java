/*
 * Created on Oct 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.field.IPersistAltLeftClickProvider;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Given ATS attribute, will return ; delimited values for AbstractWorkflowArtifact and unique rolled up values for
 * ActionArtifacts. This column will also provide default alt-left-click operation for any single value (max=1)
 * attribute.
 * 
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeValueColumn extends XViewerAtsAttributeColumn implements IAltLeftClickProvider, IXViewerValueColumn {

   public XViewerAtsAttributeValueColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   public XViewerAtsAttributeValueColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(atsAttribute, width, align, show, sortDataType, multiColumnEditable);
   }

   public XViewerAtsAttributeValueColumn(String id, IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(id, attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) {
      return null;
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
               strs.add(getColumnText(team, column, columnIndex));
            }
            return Collections.toString(";", strs);
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return null;
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   @Override
   public StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
   }

   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) {
      return null;
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
            if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
               boolean modified =
                  PromptChangeUtil.promptChangeAttribute((AbstractWorkflowArtifact) treeItem.getData(),
                     getAttributeType(), false, true);
               XViewer xViewer = ((XViewerColumn) treeColumn.getData()).getTreeViewer();
               if (modified && isPersistViewer(xViewer)) {
                  useArt.persist("persist attribute via alt-left-click");
               }
               if (modified) {
                  xViewer.update(useArt, null);
                  return true;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   private boolean isPersistViewer(XViewer xViewer) {
      return xViewer != null && //
      xViewer instanceof IPersistAltLeftClickProvider //
         && ((IPersistAltLeftClickProvider) xViewer).isAltLeftClickPersist();
   }

}
