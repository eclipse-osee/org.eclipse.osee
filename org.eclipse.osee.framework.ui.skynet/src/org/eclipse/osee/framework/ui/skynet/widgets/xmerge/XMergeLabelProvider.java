/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.sql.SQLException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XMergeLabelProvider implements ITableLabelProvider {
   public static enum ConflictState {
      UNTOUCHED(2, " "),
      REVERT(1, "Must Be Reverted"),
      MODIFIED(3, "Modified"),
      CHANGED(4, "Artifact Changed After Resolution"),
      RESOLVED(5, "Resolved"),
      INFORMATIONAL(6, "Informational");

      private final int value;
      private final String text;

      ConflictState(int value, String text) {
         this.value = value;
         this.text = text;
      }

      public static final int getValue(String text) {
         for (ConflictState state : values()) {
            if (state.text.equals(text)) {
               return state.value;
            }
         }
         return 0;
      }

      public final String getText() {
         return text;
      }

   };
   Font font = null;

   private final MergeXViewer mergeXViewer;

   private final static String SOURCE_IMAGE = "green_s.gif";
   private final static String DEST_IMAGE = "blue_d.gif";
   private final static String MERGE_IMAGE = "yellow_m.gif";
   private final static String START_WIZARD_IMAGE = "conflict.gif";
   private final static String MARKED_MERGED_IMAGE = "chkbox_enabled.gif";
   private final static String EDITED_IMAGE = "chkbox_disabled.gif";
   private final static String OUT_OF_DATE_IMAGE = "chkbox_enabled_conflicted.gif";
   private final static String NO_CONFLICT_IMAGE = "accept.gif";
   private final static String NOT_RESOLVABLE_IMAGE = "red_light.gif";
   private final static String INFORMATION_IMAGE = "issue.gif";

   public XMergeLabelProvider(MergeXViewer mergeXViewer) {
      super();
      this.mergeXViewer = mergeXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }

      XViewerColumn xCol = mergeXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         MergeColumn aCol = MergeColumn.getXColumn(xCol);
         try {
            return getColumnText(element, columnIndex, xCol, aCol);
         } catch (Exception ex) {
            XViewerCells.getCellExceptionString(ex);
         }
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param branch
    * @param xCol
    * @param aCol
    * @return column string
    * @throws SQLException
    */
   public String getColumnText(Object element, int columnIndex, XViewerColumn xCol, MergeColumn aCol) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      if (!xCol.isShow()) return AttributeConflict.NO_VALUE; // Since not shown, don't display
      try {
         if (element instanceof Conflict) {
            Conflict conflict = (Conflict) element;
            if (aCol == MergeColumn.Conflict_Resolved) {
               if (conflict.statusResolved()) return ConflictState.RESOLVED.getText();
               if (conflict.statusEdited()) return ConflictState.MODIFIED.getText();
               if (conflict.statusOutOfDate()) return ConflictState.CHANGED.getText();
               if (conflict.statusUntouched()) return ConflictState.UNTOUCHED.getText();
               if (conflict.statusNotResolvable()) return ConflictState.REVERT.getText();
               if (conflict.statusInformational()) return ConflictState.INFORMATIONAL.getText();
            } else if (aCol == MergeColumn.Artifact_Name) {
               return conflict.getArtifactName();
            } else if (aCol == MergeColumn.Change_Item) {
               return conflict.getChangeItem();
            } else if (aCol == MergeColumn.Source) {
               return conflict.getSourceDisplayData();
            } else if (aCol == MergeColumn.Destination) {
               return conflict.getDestDisplayData();
            } else if (aCol == MergeColumn.Merged) {
               return conflict.getMergeDisplayData();
            } else if (aCol == MergeColumn.Type) {
               return conflict.getArtifact().getArtifactTypeName();
            }

         }

      } catch (Exception ex) {
         OSEELog.logException(XMergeLabelProvider.class, ex, true);
      }
      return AttributeConflict.NO_VALUE;
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public MergeXViewer getTreeViewer() {
      return mergeXViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      XViewerColumn xCol = mergeXViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      MergeColumn dCol = MergeColumn.getXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display

      if (element instanceof Conflict) {
         try {
            Conflict conflict = (Conflict) element;
            if (dCol == MergeColumn.Artifact_Name) {
               return conflict.getArtifactImage();
            } else if (dCol == MergeColumn.Type) {
               return conflict.getArtifact().getImage();
            } else if (dCol == MergeColumn.Change_Item) {
               return conflict.getImage();
            } else if (dCol == MergeColumn.Source) {
               return SkynetGuiPlugin.getInstance().getImage(SOURCE_IMAGE);
            } else if (dCol == MergeColumn.Destination) {
               return SkynetGuiPlugin.getInstance().getImage(DEST_IMAGE);
            } else if (dCol == MergeColumn.Merged) {
               return getMergeImage(conflict);
            } else if (dCol == MergeColumn.Conflict_Resolved) {
               if (conflict.statusUntouched()) return null;
               if (conflict.statusEdited()) return SkynetGuiPlugin.getInstance().getImage(EDITED_IMAGE);
               if (conflict.statusResolved()) return SkynetGuiPlugin.getInstance().getImage(MARKED_MERGED_IMAGE);
               if (conflict.statusOutOfDate()) return SkynetGuiPlugin.getInstance().getImage(OUT_OF_DATE_IMAGE);
               if (conflict.statusNotResolvable()) return SkynetGuiPlugin.getInstance().getImage(NOT_RESOLVABLE_IMAGE);
               if (conflict.statusInformational()) return SkynetGuiPlugin.getInstance().getImage(INFORMATION_IMAGE);
            }
         } catch (Exception ex) {
            OSEELog.logException(XMergeLabelProvider.class, ex, true);
         }

      }

      return null;
   }

   public static Image getMergeImage(Conflict conflict) throws SQLException, OseeCoreException {
      if (conflict.statusInformational()) return null;
      if (conflict.statusNotResolvable()) return SkynetGuiPlugin.getInstance().getImage(START_WIZARD_IMAGE);
      if ((conflict.sourceEqualsDestination()) && (conflict.mergeEqualsSource())) return SkynetGuiPlugin.getInstance().getImage(
            NO_CONFLICT_IMAGE);
      if (conflict.statusUntouched()) return SkynetGuiPlugin.getInstance().getImage(START_WIZARD_IMAGE);
      if (conflict.mergeEqualsDestination()) return SkynetGuiPlugin.getInstance().getImage(DEST_IMAGE);
      if (conflict.mergeEqualsSource())
         return SkynetGuiPlugin.getInstance().getImage(SOURCE_IMAGE);
      else
         return SkynetGuiPlugin.getInstance().getImage(MERGE_IMAGE);
   }

}
