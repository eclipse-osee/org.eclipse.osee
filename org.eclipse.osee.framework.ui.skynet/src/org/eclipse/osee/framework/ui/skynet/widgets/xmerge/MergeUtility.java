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
         "You can not change the value for a conflict that has been marked resolved or has already been commited.  Change the conflict status if the source branch has not been commited and you wish to modify the value.";
   public static final String ARTIFACT_DELETED_PROMPT =
         "This Artifact has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Artifact will need to be reverted on the source branch.  \n\nReverting the artifact is irreversible and you will need to restart OSEE after reverting to see changes.";
   public static final String INFORMATIONAL_CONFLICT =
         "This Artifact has been deleted on the Source Branch, but has been changed on the destination branch.  This conflict is informational only and will not prevent your from commiting, however when you commit it will delete the artifact on the destination branch.";

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
      try {
         IRenderer renderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, attrConflict.getDestArtifact());
         showDiff(attrConflict, renderer.compare(attrConflict.getDestArtifact(), attrConflict.getSourceArtifact(), "",
               null, null, false));
      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
   }

   public static void showSourceCompareFile(Conflict conflict) {
      if (conflict == null) return;
      try {
         IRenderer renderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, conflict.getDestArtifact());
         showDiff(conflict, renderer.compare(getStartArtifact(conflict), conflict.getSourceArtifact(), "", null, null,
               false));
      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
   }

   public static void showDestCompareFile(Conflict conflict) {
      if (conflict == null) return;
      try {
         IRenderer renderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, conflict.getDestArtifact());
         showDiff(conflict, renderer.compare(getStartArtifact(conflict), conflict.getDestArtifact(), "", null, null,
               false));
      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
   }

   private static Artifact getStartArtifact(Conflict conflict) {
      try {
         TransactionId id = TransactionIdManager.getInstance().getStartEndPoint(conflict.getSourceBranch()).getKey();
         return ArtifactPersistenceManager.getInstance().getArtifact(conflict.getArtifact().getGuid(), id);

      } catch (Exception ex) {
         OSEELog.logException(MergeUtility.class, ex, false);
      }
      return null;
   }

   private static void showDiff(Conflict conflict, String diffFile) {
      try {
         IRenderer renderer =
               RendererManager.getInstance().getBestRenderer(PresentationType.DIFF, conflict.getDestArtifact());
         if (renderer instanceof FileSystemRenderer) {
            ((FileSystemRenderer) renderer).getAssociatedProgram(conflict.getArtifact()).execute(diffFile);
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
}
