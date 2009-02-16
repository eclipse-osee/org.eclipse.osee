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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XMergeLabelProvider extends XViewerLabelProvider {
   public static enum ConflictState {
      UNTOUCHED(2, " "),
      REVERT(1, "Must Be Reverted"),
      MODIFIED(3, "Modified"),
      CHANGED(4, "Artifact Changed After Resolution"),
      RESOLVED(5, "Resolved"),
      INFORMATIONAL(6, "Informational"),
      COMMITTED(7, "Committed"),
      CHANGED_EDIT(8, "Artifact Changed"),
      MERGE_SUCCESS(9, "Previous Merge Applied Successfully"),
      MERGE_CAUTION(10, "Previous Merge applied with destination differences");

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
   private final static String OUT_OF_DATE_IMAGE = "chkbox_red.gif";
   private final static String OUT_OF_DATE_COMMITTED_IMAGE = "chkbox_enabled_conflicted.gif";
   private final static String NO_CONFLICT_IMAGE = "accept.gif";
   private final static String NOT_RESOLVABLE_IMAGE = "red_light.gif";
   private final static String INFORMATION_IMAGE = "issue.gif";
   private final static String MERGE_SUCCESS_IMAGE = "icon_success.gif";
   private final static String MERGE_CAUTION_IMAGE = "icon_warning.gif";

   public XMergeLabelProvider(MergeXViewer mergeXViewer) {
      super(mergeXViewer);
      this.mergeXViewer = mergeXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException {
      if (element instanceof Conflict) {
         Conflict conflict = (Conflict) element;
         if (aCol.equals(MergeXViewerFactory.Conflict_Resolved)) {
            if (conflict.statusResolved()) return ConflictState.RESOLVED.getText();
            if (conflict.statusEdited()) return ConflictState.MODIFIED.getText();
            if (conflict.statusOutOfDate()) return ConflictState.CHANGED_EDIT.getText();
            if (conflict.statusOutOfDateCommitted()) return ConflictState.CHANGED.getText();
            if (conflict.statusUntouched()) return ConflictState.UNTOUCHED.getText();
            if (conflict.statusNotResolvable()) return ConflictState.REVERT.getText();
            if (conflict.statusInformational()) return ConflictState.INFORMATIONAL.getText();
            if (conflict.statusCommitted()) return ConflictState.COMMITTED.getText();
            if (conflict.statusPreviousMergeAppliedSuccess()) return ConflictState.MERGE_SUCCESS.getText();
            if (conflict.statusPreviousMergeAppliedCaution()) return ConflictState.MERGE_CAUTION.getText();
         } else if (aCol.equals(MergeXViewerFactory.Artifact_Name)) {
            return conflict.getArtifactName();
         } else if (aCol.equals(MergeXViewerFactory.Change_Item)) {
            return conflict.getChangeItem();
         } else if (aCol.equals(MergeXViewerFactory.Source)) {
            return conflict.getSourceDisplayData();
         } else if (aCol.equals(MergeXViewerFactory.Destination)) {
            return conflict.getDestDisplayData();
         } else if (aCol.equals(MergeXViewerFactory.Merged)) {
            return conflict.getMergeDisplayData();
         } else if (aCol.equals(MergeXViewerFactory.Type)) {
            return conflict.getArtifact().getArtifactTypeName();
         } else if (aCol.equals(MergeXViewerFactory.Art_Id)) {
            return String.valueOf(conflict.getArtifact().getArtId());
         }
      }
      return "unhandled column";
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

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) throws OseeCoreException {
      if (element instanceof Conflict) {
         try {
            Conflict conflict = (Conflict) element;
            if (dCol.equals(MergeXViewerFactory.Artifact_Name)) {
               return conflict.getArtifactImage();
            } else if (dCol.equals(MergeXViewerFactory.Type)) {
               return conflict.getArtifact().getImage();
            } else if (dCol.equals(MergeXViewerFactory.Change_Item)) {
               return conflict.getImage();
            } else if (dCol.equals(MergeXViewerFactory.Source)) {
               return SkynetGuiPlugin.getInstance().getImage(SOURCE_IMAGE);
            } else if (dCol.equals(MergeXViewerFactory.Destination)) {
               return SkynetGuiPlugin.getInstance().getImage(DEST_IMAGE);
            } else if (dCol.equals(MergeXViewerFactory.Merged)) {
               return getMergeImage(conflict);
            } else if (dCol.equals(MergeXViewerFactory.Conflict_Resolved)) {
               if (conflict.statusUntouched()) return null;
               if (conflict.statusEdited()) return SkynetGuiPlugin.getInstance().getImage(EDITED_IMAGE);
               if (conflict.statusResolved() || conflict.statusCommitted()) return SkynetGuiPlugin.getInstance().getImage(
                     MARKED_MERGED_IMAGE);
               if (conflict.statusOutOfDate()) return SkynetGuiPlugin.getInstance().getImage(OUT_OF_DATE_IMAGE);
               if (conflict.statusOutOfDateCommitted()) return SkynetGuiPlugin.getInstance().getImage(
                     OUT_OF_DATE_COMMITTED_IMAGE);
               if (conflict.statusPreviousMergeAppliedSuccess()) return SkynetGuiPlugin.getInstance().getImage(
                     MERGE_SUCCESS_IMAGE);
               if (conflict.statusPreviousMergeAppliedCaution()) return SkynetGuiPlugin.getInstance().getImage(
                     MERGE_CAUTION_IMAGE);
               if (conflict.statusNotResolvable()) return SkynetGuiPlugin.getInstance().getImage(NOT_RESOLVABLE_IMAGE);
               if (conflict.statusInformational()) return SkynetGuiPlugin.getInstance().getImage(INFORMATION_IMAGE);
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   public static Image getMergeImage(Conflict conflict) throws OseeCoreException {
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
