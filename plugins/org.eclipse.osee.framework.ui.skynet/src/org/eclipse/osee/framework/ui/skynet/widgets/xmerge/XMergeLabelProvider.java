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
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
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

   public XMergeLabelProvider(MergeXViewer mergeXViewer) {
      super(mergeXViewer);
      this.mergeXViewer = mergeXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) {
      if (element instanceof Conflict) {
         Conflict conflict = (Conflict) element;
         if (aCol.equals(MergeXViewerFactory.Conflict_Resolved)) {
            ConflictStatus status = conflict.getStatus();
            if (status.isResolved()) {
               return ConflictState.RESOLVED.getText();
            }
            if (status.isEdited()) {
               return ConflictState.MODIFIED.getText();
            }
            if (status.isOutOfDate()) {
               return ConflictState.CHANGED_EDIT.getText();
            }
            if (status.isOutOfDateCommitted()) {
               return ConflictState.CHANGED.getText();
            }
            if (status.isUntouched()) {
               return ConflictState.UNTOUCHED.getText();
            }
            if (status.isInformational()) {
               return ConflictState.INFORMATIONAL.getText();
            }
            if (status.isCommitted()) {
               return ConflictState.COMMITTED.getText();
            }
            if (status.isPreviousMergeSuccessfullyApplied()) {
               return ConflictState.MERGE_SUCCESS.getText();
            }
            if (status.isPreviousMergeAppliedWithCaution()) {
               return ConflictState.MERGE_CAUTION.getText();
            }
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
            return conflict.getArtifact().getIdString();
         }
      }
      return "unhandled column";
   }

   @Override
   public void dispose() {
      if (font != null) {
         font.dispose();
      }
      font = null;
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   public MergeXViewer getTreeViewer() {
      return mergeXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn dCol, int columnIndex) {
      if (element instanceof Conflict) {
         try {
            Conflict conflict = (Conflict) element;
            if (dCol.equals(MergeXViewerFactory.Artifact_Name)) {
               return ArtifactImageManager.getImage(conflict.getArtifact());
            } else if (dCol.equals(MergeXViewerFactory.Type)) {
               return ArtifactImageManager.getImage(conflict.getArtifact());
            } else if (dCol.equals(MergeXViewerFactory.Change_Item)) {
               return ArtifactImageManager.getConflictImage(conflict);
            } else if (dCol.equals(MergeXViewerFactory.Source)) {
               return ImageManager.getImage(FrameworkImage.MERGE_SOURCE);
            } else if (dCol.equals(MergeXViewerFactory.Destination)) {
               return ImageManager.getImage(FrameworkImage.MERGE_DEST);
            } else if (dCol.equals(MergeXViewerFactory.Merged)) {
               return getMergeImage(conflict);
            } else if (dCol.equals(MergeXViewerFactory.Conflict_Resolved)) {
               ConflictStatus status = conflict.getStatus();
               if (status.isUntouched()) {
                  return null;
               }
               if (status.isEdited()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_EDITED);
               }
               if (status.isResolved() || status.isCommitted()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_MARKED);
               }
               if (status.isOutOfDate()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_OUT_OF_DATE);
               }
               if (status.isOutOfDateCommitted()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_OUT_OF_DATE_COMMITTED);
               }
               if (status.isPreviousMergeSuccessfullyApplied()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_SUCCESS);
               }
               if (status.isPreviousMergeAppliedWithCaution()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_CAUTION);
               }
               if (status.isInformational()) {
                  return ImageManager.getImage(FrameworkImage.MERGE_INFO);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return null;
   }

   public static Image getMergeImage(Conflict conflict) {
      ConflictStatus status = conflict.getStatus();
      if (status.isInformational()) {
         return null;
      }
      if (conflict.sourceEqualsDestination() && conflict.mergeEqualsSource()) {
         return ImageManager.getImage(FrameworkImage.MERGE_NO_CONFLICT);
      }
      if (status.isUntouched()) {
         return ImageManager.getImage(FrameworkImage.MERGE_START);
      }
      if (conflict.mergeEqualsDestination()) {
         return ImageManager.getImage(FrameworkImage.MERGE_DEST);
      }
      if (conflict.mergeEqualsSource()) {
         return ImageManager.getImage(FrameworkImage.MERGE_SOURCE);
      } else {
         return ImageManager.getImage(FrameworkImage.MERGE_YELLOW_M);
      }
   }

}
