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

import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.enums.ConflictType;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.VbaWordDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

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
   public static final String ATTRIBUTE_DELETED_PROMPT =
         "This Attribute has been changed on the source branch, but has been deleted on the destination branch.  In order to commit this branch and resolve this conflict the Attribute will need to be reverted on the source branch.  \n\nReverting the attribute is irreversible and you will need to restart OSEE after reverting to see changes.";
   public static final String INFORMATIONAL_CONFLICT =
         "This Item has been deleted on the Source Branch, but has been changed on the destination branch.  This conflict is informational only and will not prevent your from commiting, however when you commit it will delete the item on the destination branch.";
   public static final String OPEN_MERGE_DIALOG =
         "This will open a window that will allow in-document merging in Word.  You will need to right click on every difference and either accept or reject the change.  If you begin an in-document merge you will not be able to finalize the conflict until you resolve every change in the document.\n Computing a Merge will wipe out any merge changes you have made and start with a fresh diff of the two files.  If you want to only view the changes use the difference options.\n Change that touch the entire file are better handled using copy and paste. \n\nWARNING:  Word will occasionaly show incorrect changes especially when users have both modified the same block of text.  Check your final version.";

   private static final Pattern authorPattern =
         Pattern.compile("aml:author=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern rsidRootPattern =
         Pattern.compile("\\</wsp:rsids\\>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsids =
         Pattern.compile("wsp:rsidR=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRPR =
         Pattern.compile("wsp:rsidRPr=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidP =
         Pattern.compile("wsp:rsidP=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern findSetRsidRDefault =
         Pattern.compile("wsp:rsidRDefault=\".*?\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern amlTerminatingDefault =
         Pattern.compile("aml:id[^\\>]*?/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Merge"));

   public static void clearValue(Conflict conflict, Shell shell, boolean prompt) throws MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.clearValue();
      }
   }

   public static void setToDest(Conflict conflict, Shell shell, boolean prompt) throws MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToDest();
      }
   }

   public static void setToSource(Conflict conflict, Shell shell, boolean prompt) throws MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
      if (conflict == null) return;
      if (okToOverwriteEditedValue(conflict, shell, prompt)) {
         conflict.setToSource();
      }
   }

   public static boolean okToOverwriteEditedValue(Conflict conflict, Shell shell, boolean prompt) throws MultipleArtifactsExist, ArtifactDoesNotExist, Exception {
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
   public static String showCompareFile(Artifact art1, Artifact art2, String fileName) throws Exception {
      if (art1 == null || art2 == null) return " ";
      return RendererManager.diff(art1, art2, true, new VariableMap("fileName", fileName));
   }

   /*
    * This is not in the AttributeConflict because it relies on the renderer
    * that is in not in the skynet core package.
    */
   public static String CreateMergeDiffFile(Artifact art1, Artifact art2, String fileName) throws Exception {
      if (art1 == null || art2 == null) return " ";
      return RendererManager.merge(art1, art2, fileName, false);
   }

   /*
    * This is not in the AttributeConflict because it relies on the renderer
    * that is in not in the skynet core package.
    */
   public static void mergeEditableDiffFiles(Artifact art1, String art1FileName, String art2FileName, String fileName, boolean show, boolean editable) throws Exception {
      if (art1 == null) return;
      RendererManager.merge(art1, null, AIFile.constructIFile(art1FileName), AIFile.constructIFile(art2FileName),
            fileName.substring(fileName.lastIndexOf('\\') + 1), show);
   }

   public static Artifact getStartArtifact(Conflict conflict) {
      try {
         if (conflict.getSourceBranch() == null) return null;
         TransactionId id = TransactionIdManager.getStartEndPoint(conflict.getSourceBranch()).getKey();
         return ArtifactPersistenceManager.getInstance().getArtifact(conflict.getArtifact().getGuid(), id);

      } catch (Exception ex) {
         OseeLog.log(MergeUtility.class, Level.SEVERE, ex);
      }
      return null;
   }

   /**
    * @param conflict
    */
   public static boolean showDeletedConflict(Conflict conflict, Shell shell) {
      if (conflict.getConflictType().equals(ConflictType.ARTIFACT)) {
         return showArtifactDeletedConflict(conflict, shell);
      } else if (conflict.getConflictType().equals(ConflictType.ATTRIBUTE)) {
         return showAttributeDeletedConflict(conflict, shell);
      }
      return false;
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
               OseeLog.log(MergeUtility.class, Level.SEVERE, ex);
            }
         }
      }
      return false;
   }

   /**
    * @param conflict
    */
   public static boolean showAttributeDeletedConflict(Conflict conflict, Shell shell) {
      if (conflict.getConflictType().equals(ConflictType.ATTRIBUTE)) {
         MessageDialog dialog =
               new MessageDialog(shell, "Unresovable Conflict", null, ATTRIBUTE_DELETED_PROMPT, 1, new String[] {
                     "Revert Source Attribute", "Handle Later"}, 1);
         if (dialog.open() == 0) {
            try {
               ((AttributeConflict) conflict).revertSourceAttribute();
               return true;
            } catch (Exception ex) {
               OseeLog.log(MergeUtility.class, Level.SEVERE, ex);
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

   public static void launchMerge(final AttributeConflict attributeConflict, Shell shell) {

      try {
         if (attributeConflict.getAttribute() instanceof WordAttribute) {
            if (!attributeConflict.statusEditable()) {
               MessageDialog.openInformation(shell, "Attention", COMMITED_PROMPT);
               return;
            }
            String[] buttons;
            if (attributeConflict.mergeEqualsSource() || attributeConflict.mergeEqualsDestination() || attributeConflict.statusUntouched()) {
               buttons = new String[] {"Begin New Merge", "Show Help", "Cancel"};
            } else {
               buttons = new String[] {"Continue with last Merge", "Begin New Merge", "Show Help", "Cancel"};
            }

            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell().getShell(), "Merge Word Artifacts", null,
                        OPEN_MERGE_DIALOG, 4, buttons, 2);
            int response = dialog.open();
            if (buttons.length == 3) {
               response++;
            }
            if (response == 2) {
               PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(
                     "/org.eclipse.osee.framework.ui.skynet/reference/Merge_Manager.html");
            } else if (response == 1) {

               Job job = new Job("Generate 3 Way Merge") {

                  @Override
                  protected IStatus run(final IProgressMonitor monitor) {
                     try {
                        int gamma = attributeConflict.getAttribute().getGammaId();
                        monitor.beginTask("Generate 3 Way Merge", 100);
                        VbaWordDiffGenerator generator = new VbaWordDiffGenerator();
                        generator.initialize(false, false);
                        monitor.worked(5);
                        String sourceChangeFile =
                              MergeUtility.CreateMergeDiffFile(getStartArtifact(attributeConflict),
                                    attributeConflict.getSourceArtifact(), null);
                        monitor.worked(15);
                        String destChangeFile =
                              MergeUtility.CreateMergeDiffFile(getStartArtifact(attributeConflict),
                                    attributeConflict.getDestArtifact(), null);
                        monitor.worked(15);
                        changeAuthorinWord("Source", sourceChangeFile, 2, 12345678, 55555555);
                        changeAuthorinWord("Destination", destChangeFile, 2, 56781234, 55555555);
                        monitor.worked(15);
                        MergeUtility.mergeEditableDiffFiles(
                              attributeConflict.getArtifact(),
                              sourceChangeFile,
                              destChangeFile,
                              "Source_Dest_Merge_" + attributeConflict.getArtifact().getSafeName() + "(" + attributeConflict.getArtifact().getGuid() + ")" + (new Date()).toString().replaceAll(
                                    ":", ";") + ".xml", false, true);

                        monitor.worked(40);
                        attributeConflict.markStatusToReflectEdit();

                        while (gamma == attributeConflict.getAttribute().getGammaId()) {
                           Thread.sleep(500);
                        }
                        monitor.done();
                        RendererManager.editInJob(attributeConflict.getArtifact());

                     } catch (Exception ex) {
                        OSEELog.logException(MergeView.class, ex, true);
                     }
                     monitor.done();
                     return Status.OK_STATUS;
                  }
               };

               Jobs.startJob(job);

            } else if (response == 0) {
               RendererManager.editInJob(attributeConflict.getArtifact());
               attributeConflict.markStatusToReflectEdit();
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(MergeView.class, ex, true);
      }
   }

   protected static void changeAuthorinWord(String newAuthor, String fileName, int revisionNumber, int rsidNumber, int baselineRsid) throws Exception {
      String fileValue = AFile.readFile(fileName);

      Matcher m = authorPattern.matcher(fileValue);
      while (m.find()) {
         String name = m.group();
         fileValue = fileValue.replace(name, "aml:author=\"" + newAuthor + "\"");
      }

      m = findSetRsids.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidR=\"" + baselineRsid + "\"");
      }
      m = findSetRsidRPR.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidRPr=\"" + baselineRsid + "\"");
      }
      m = findSetRsidP.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidP=\"" + baselineRsid + "\"");
      }
      m = findSetRsidRDefault.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "wsp:rsidRDefault=\"" + baselineRsid + "\"");
      }

      StringBuilder builder = new StringBuilder();
      String[] pieces = fileValue.split("</?aml:annotation");
      int position = 0;
      for (String string : pieces) {
         if (position != 0) {
            if (position % 2 == 1) {
               if (string.contains("w:type=\"Word.Insertion")) {
                  m = findSetRsids.matcher(string);
                  while (m.find()) {
                     String rev = m.group();
                     string = string.replace(rev, "wsp:rsidR=\"" + rsidNumber + "\"");
                  }
                  m = findSetRsidP.matcher(string);
                  while (m.find()) {
                     String rev = m.group();
                     string = string.replace(rev, "wsp:rsidP=\"" + rsidNumber + "\"");
                  }
                  m = findSetRsidRPR.matcher(string);
                  while (m.find()) {
                     String rev = m.group();
                     string = string.replace(rev, "wsp:rsidRPr=\"" + rsidNumber + "\"");
                  }
               }
               builder.append("<aml:annotation");
            } else {
               if (!amlTerminatingDefault.matcher(string).find()) {
                  builder.append("</aml:annotation");
               } else {
                  builder.append("<aml:annotation");
               }
            }

         }
         builder.append(string);
         if (!amlTerminatingDefault.matcher(string).find()) {
            position++;
         }
      }

      fileValue = builder.toString();

      m = rsidRootPattern.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "<wsp:rsid wsp:val=\"" + rsidNumber + "\"/></wsp:rsids>");
      }
      m = rsidRootPattern.matcher(fileValue);
      while (m.find()) {
         String rev = m.group();
         fileValue = fileValue.replace(rev, "<wsp:rsid wsp:val=\"" + baselineRsid + "\"/></wsp:rsids>");
      }

      AFile.writeFile(fileName, fileValue);
   }
}
