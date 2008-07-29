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
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
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
import org.eclipse.swt.widgets.Display;
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
         "You can not change the value for a conflict that has been marked resolved or has already been commited.  Change the conflict status if the source branch has not been commited and you wish to modify the value.";
   public static final String ARTIFACT_DELETED_PROMPT =
         "This Artifact has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Artifact will need to be reverted on the source branch.  \n\nReverting the artifact is irreversible and you will need to restart OSEE after reverting to see changes.";
   public static final String INFORMATIONAL_CONFLICT =
         "This Artifact has been deleted on the Source Branch, but has been changed on the destination branch.  This conflict is informational only and will not prevent your from commiting, however when you commit it will delete the artifact on the destination branch.";
   public static final String OPEN_MERGE_DIALOG =
         "This will open a window that will allow side by side merging in Word.  You will need to right click on every difference and either accept or reject the change.  If you begin a side by side merge you will not be able to finalize the conflict until you resolve every change in the document.\n Computing a Merge will wipe out any merge changes you have made and start with a fresh diff of the two files.  If you want to only view the changes use the difference options.";

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
      if (!conflict.statusEditable()) {
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
   public static void showCompareFile(Artifact art1, Artifact art2, boolean editable) throws Exception {
      if (art1 == null || art2 == null) return;
      IRenderer renderer = RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, art1);
      showDiff(art1, renderer.compare(art1, art2, "", null, null, false, editable));
   }

   public static Artifact getStartArtifact(Conflict conflict) {
      try {
         if (conflict.getSourceBranch() == null) return null;
         TransactionId id = TransactionIdManager.getStartEndPoint(conflict.getSourceBranch()).getKey();
         return ArtifactPersistenceManager.getInstance().getArtifact(conflict.getArtifact().getGuid(), id);

      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
      return null;
   }

   private static void showDiff(Artifact artifact, String diffFile) {
      try {
         IRenderer renderer = RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, artifact);
         if (renderer instanceof FileSystemRenderer) {
            ((FileSystemRenderer) renderer).getAssociatedProgram(artifact).execute(diffFile);
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
               new MessageDialog(shell, "Unresovable Conflict", null, ARTIFACT_DELETED_PROMPT, 1, new String[] {
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

   public static boolean showInformationalConflict(Shell shell) {
      MessageDialog dialog =
            new MessageDialog(shell, "Informational Conflict", null, INFORMATIONAL_CONFLICT, 2, new String[] {"OK"}, 1);
      dialog.open();
      return false;
   }

   public static void launchMerge(AttributeConflict attributeConflict, Shell shell) {

      try {
         if (attributeConflict.getAttribute() instanceof WordAttribute) {
            if (!attributeConflict.statusEditable()) {
               MessageDialog.openInformation(shell, "Attention", COMMITED_PROMPT);
               return;
            }
            String[] buttons;
            if (attributeConflict.mergeEqualsSource() || attributeConflict.mergeEqualsDestination() || attributeConflict.statusUntouched()) {
               buttons = new String[] {"Cancel", "Begin New Merge"};
            } else {
               buttons = new String[] {"Cancel", "Begin New Merge", "Continue with last Merge"};
            }

            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell().getShell(), "Merge Word Artifacts", null,
                        OPEN_MERGE_DIALOG, 4, buttons, 2);
            int response = dialog.open();
            if (response == 1) {
               attributeConflict.setToSource();
               MergeUtility.showCompareFile(attributeConflict.getArtifact(), attributeConflict.getDestArtifact(), true);
               attributeConflict.markStatusToReflectEdit();
            } else if (response == 2) {
               RendererManager.getInstance().editInJob(attributeConflict.getArtifact(), "EDIT_ARTIFACT");
               attributeConflict.markStatusToReflectEdit();
            }

         }

      } catch (Exception ex) {
         OSEELog.logException(MergeView.class, ex, true);
      }

   }
}
