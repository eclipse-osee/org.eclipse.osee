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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict.ConflictType;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Theron Virgin
 */
public class MergeUtility {
   /*
    * This has all of the GUI prompts that help a user know what's going on
    * when they set a merge. 
    */
   public static final String CLEAR_PROMPT =
         "This attribute has had Merge changes made are you sure you want to overwrite them? All changes will be lost.";
   public static final String COMMITED_PROMPT =
         "You can not change the value for a conflict that has been marked resolved.  Change the conflict status if you wish to modify the value.";
   public static final String ARTIFACT_DELETED_PROMPT =
         "This Artifact has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Artifact will need to be reverted on the source branch";

   public static void clearValue(Conflict conflict, Shell shell, boolean prompt) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.clearValue();
      }
   }

   public static void setToDest(Conflict conflict, Shell shell, boolean prompt) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToDest();
      }
   }

   public static void setToSource(Conflict conflict, Shell shell, boolean prompt) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToSource();
      }
   }

   public static boolean okToOverwriteEditedValue(Conflict conflict, Shell shell, boolean prompt) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      boolean proceed = true;
      if (conflict.statusResolved()) {
         MessageDialog.openInformation(shell, "Attention", COMMITED_PROMPT);
         return false;
      }
      if (!(conflict.mergeEqualsDestination() || conflict.mergeEqualsSource() || conflict.statusUntouched()) && prompt) {
         proceed = MessageDialog.openConfirm(shell, "Confirm", CLEAR_PROMPT);
      }
      return proceed;
   }

   /*
    * This is not in the AttributeConflict because it relies on the renderer
    * that is in not in the skynet core package.
    */
   public static void showSourceDestCompareFile(AttributeConflict attrConflict) {
      if (attrConflict == null) return;
      if (attrConflict.getSourceDestDiffFile() == null) {
         try {
            IRenderer renderer =
                  RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, attrConflict.getDestArtifact());
            String diffFileName =
                  "Destination_Source_Changes_For_Artifact_" + attrConflict.getDestArtifact().getGuid() + ".xml";
            attrConflict.setSourceDestDiffFile(renderer.compare(attrConflict.getDestArtifact(),
                  attrConflict.getSourceArtifact(), "", null, diffFileName, false));
         } catch (Exception ex) {
            OSEELog.logException(MergeUtility.class, ex, false);
         }
      }
      showDiff(attrConflict, attrConflict.getSourceDestDiffFile());
   }

   public static void showSourceCompareFile(AttributeConflict attrConflict) {
      if (attrConflict == null) return;
      if (attrConflict.getSourceDiffFile() == null) {
         try {
            IRenderer renderer =
                  RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, attrConflict.getDestArtifact());
            String diffFileName = "Source_Branch_Changes_For_Artifact_" + attrConflict.getArtifact().getGuid() + ".xml";
            attrConflict.setSourceDiffFile(renderer.compare(getStartArtifact(attrConflict),
                  attrConflict.getSourceArtifact(), "", null, diffFileName, false));
         } catch (Exception ex) {
            OSEELog.logException(MergeUtility.class, ex, false);
         }
      }
      showDiff(attrConflict, attrConflict.getSourceDiffFile());
   }

   public static void showDestCompareFile(AttributeConflict attrConflict) {
      if (attrConflict == null) return;
      if (attrConflict.getDestDiffFile() == null) {
         try {
            IRenderer renderer =
                  RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, attrConflict.getDestArtifact());
            String diffFileName =
                  "Destination_Branch_Changes_For_Artifact_" + attrConflict.getArtifact().getGuid() + ".xml";
            attrConflict.setDestDiffFile(renderer.compare(getStartArtifact(attrConflict),
                  attrConflict.getDestArtifact(), "", null, diffFileName, false));
         } catch (Exception ex) {
            OSEELog.logException(MergeUtility.class, ex, false);
         }
      }
      showDiff(attrConflict, attrConflict.getDestDiffFile());
   }

   private static Artifact getStartArtifact(AttributeConflict attrConflict) {
      try {
         TransactionId id =
               TransactionIdManager.getInstance().getStartEndPoint(attrConflict.getSourceBranch()).getKey();
         return ArtifactPersistenceManager.getInstance().getArtifact(attrConflict.getArtifact().getGuid(), id);

      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
      return null;
   }

   private static void showDiff(AttributeConflict attrConflict, String diffFile) {
      try {
         IRenderer renderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, attrConflict.getDestArtifact());
         if (renderer instanceof FileSystemRenderer) {
            ((FileSystemRenderer) renderer).getAssociatedProgram(attrConflict.getArtifact()).execute(diffFile);
         }
      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
   }

   /**
    * @param conflict
    */
   public static boolean showArtifactDeletedConflict(Conflict conflict, Shell shell) {
      if (conflict.getConflictType().equals(ConflictType.ARTIFACT)) {
         MessageDialog dialog =
               new MessageDialog(shell, "Unresovable Conflict", null, ARTIFACT_DELETED_PROMPT, 3, new String[] {
                     "Revert Source Artifact", "Handle Later"}, 1);
         if (dialog.open() == 0) {
            try {
               ((ArtifactConflict) conflict).revertSourceArtifact();
               return true;
            } catch (Exception ex) {
               OSEELog.logException(MergeUtility.class, ex, false);
            }
         }
      }
      return false;
   }
}
