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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Theron Virgin
 */
public class MergeUtility {
   /*
    * This has all of the GUI prompts that help a user know what's going on when they set a merge.
    */
   public static final String CLEAR_PROMPT =
      "This attribute has had Merge changes made are you sure you want to overwrite them? All changes will be lost.";
   public static final String COMMITED_PROMPT =
      "You can not change the value for a conflict that has been marked resolved or has already been commited.  Change the conflict status if the source branch has not been commited and you wish to modify the value.";
   public static final String ARTIFACT_DELETED_PROMPT =
      "This Artifact has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Artifact will need to be reverted on the source branch.  \n\nReverting the artifact is irreversible and you will need to restart OSEE after reverting to see changes.";
   public static final String ATTRIBUTE_DELETED_PROMPT =
      "This Attribute has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Attribute will need to be reverted on the source branch.  \n\nReverting the attribute is irreversible and you will need to restart OSEE after reverting to see changes.";
   public static final String INFORMATIONAL_CONFLICT =
      "This Item has been %1$s on the Source Branch, but has been %2$s on the destination branch.  This conflict is informational only and will not prevent your from commiting, however when you commit the item will be DELETED.";
   public static final String OPEN_MERGE_DIALOG =
      "This will open a window that will allow in-document merging in Word.  You will need to right click on every difference and either accept or reject the change.  If you begin an in-document merge you will not be able to finalize the conflict until you resolve every change in the document.\n Computing a Merge will wipe out any merge changes you have made and start with a fresh diff of the two files.  If you want to only view the changes use the difference options.\n Change that touch the entire file are better handled using copy and paste. \n\nWARNING:  Word will occasionaly show incorrect changes especially when users have both modified the same block of text.  Check your final version.";

   public static void clearValue(Conflict conflict, Shell shell, boolean prompt) {
      if (conflict == null) {
         return;
      }
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.clearValue();
      }
   }

   public static void setToDest(Conflict conflict, Shell shell, boolean prompt, SkynetTransaction transaction) {
      if (conflict == null) {
         return;
      }
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToDest(transaction);
      }
   }

   public static void setToDest(Conflict conflict, Shell shell, boolean prompt) {
      if (conflict == null) {
         return;
      }
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToDest();
      }
   }

   public static void setToSource(Conflict conflict, Shell shell, boolean prompt, SkynetTransaction transaction) {
      if (conflict == null) {
         return;
      }
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToSource(transaction);
      }
   }

   public static void setToSource(Conflict conflict, Shell shell, boolean prompt) {
      if (conflict == null) {
         return;
      }
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToSource();
      }
   }

   public static boolean okToOverwriteEditedValue(Conflict conflict, Shell shell, boolean prompt) {
      boolean proceed = true;
      if (!conflict.getStatus().isEditable()) {
         if (shell != null) {
            MessageDialog.openInformation(shell, "Attention", COMMITED_PROMPT);
         } else {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Attention: " + COMMITED_PROMPT);
         }
         return false;
      }
      if (!(conflict.mergeEqualsDestination() || conflict.mergeEqualsSource() || conflict.getStatus().isUntouched()) && prompt) {
         proceed = MessageDialog.openConfirm(shell, "Confirm", CLEAR_PROMPT);
      }
      return proceed;
   }

   public static void showCompareFile(Artifact art1, Artifact art2, String filePrefix) {
      RendererManager.diffInJob(new ArtifactDelta(art1, art2), filePrefix);
   }

   public static boolean showInformationalConflict(Shell shell, Conflict conflict) {
      String srcDisplay = conflict.getSourceDisplayData();
      String destDisplay = conflict.getDestDisplayData();
      String msg = String.format(INFORMATIONAL_CONFLICT, srcDisplay, destDisplay);
      MessageDialog dialog = new MessageDialog(shell, "Informational Conflict", null, msg, 2, new String[] {"OK"}, 1);
      dialog.open();
      return false;
   }

   public static void launchMerge(final AttributeConflict attributeConflict, Shell shell) {
      try {
         if (attributeConflict.getAttribute() instanceof WordAttribute) {
            if (!attributeConflict.getStatus().isEditable()) {
               MessageDialog.openInformation(shell, "Attention", COMMITED_PROMPT);
               return;
            }
            String[] buttons;
            if (attributeConflict.mergeEqualsSource() || attributeConflict.mergeEqualsDestination() || attributeConflict.getStatus().isUntouched()) {
               buttons = new String[] {"Begin New Merge", "Show Help", "Cancel"};
            } else {
               buttons = new String[] {"Continue with last Merge", "Begin New Merge", "Show Help", "Cancel"};
            }

            MessageDialog dialog = new MessageDialog(Displays.getActiveShell().getShell(), "Merge Word Artifacts", null,
               OPEN_MERGE_DIALOG, 4, buttons, 2);
            int response = dialog.open();
            if (buttons.length == 3) {
               response++;
            }
            if (response == 2) {
               HelpUtil.displayHelp(OseeHelpContext.MERGE_MANAGER);
            } else if (response == 1) {
               Operations.executeAsJob(new ThreeWayWordMergeOperation(attributeConflict), true);
            } else if (response == 0) {
               RendererManager.openInJob(attributeConflict.getArtifact(), PresentationType.SPECIALIZED_EDIT);
               attributeConflict.markStatusToReflectEdit();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static Artifact getStartArtifact(Conflict conflict) {
      Artifact toReturn = null;
      try {
         if (Conditions.notNull(conflict.getSourceBranch())) {
            TransactionToken baseTransaction = BranchManager.getBaseTransaction(conflict.getSourceBranch());
            toReturn =
               ArtifactQuery.getHistoricalArtifactFromId(conflict.getArtifact(), baseTransaction, INCLUDE_DELETED);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

   public static Artifact getCommonAncestor(Conflict conflict) {
      Artifact toReturn = null;
      try {
         if (Conditions.notNull(conflict.getSourceBranch())) {
            TransactionToken commonTransaction =
               ConflictManagerInternal.findCommonTransaction(conflict.getSourceBranch(), conflict.getDestBranch());
            toReturn =
               ArtifactQuery.getHistoricalArtifactFromId(conflict.getArtifact(), commonTransaction, INCLUDE_DELETED);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return toReturn;
   }

}