/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * This is an example of how to implement and use the XAttachmentCombo.
 *
 * @author Donald G. Dunne
 */
public class XPeerChecklistAttachmentExampleCombo extends XAttachmentCombo {

   private static final String LABEL = "Select Peer Review Checklist to Attach";
   public static final Object WIDGET_ID = XPeerChecklistAttachmentExampleCombo.class.getSimpleName();
   private static String PEER_REVIEW_CHECKLIST_STATIC_ID = "7244494692772089382";

   public XPeerChecklistAttachmentExampleCombo() {
      super(LABEL, "DemoPeerReviewChecklist");
   }

   /**
    * Normally don't have to override this cause XAttachmentCombo gets its string and does what it needs. Because this
    * is a demo db, there is no common filesystem location, so we need to load files, place in known location (if not
    * already there) and hack the return string to have that filepath, then the regular code can do its work.
    */
   @Override
   protected String getFileListString() {
      String workspacePath = AWorkspace.getWorkspacePath();
      File workspaceFile = new File(workspacePath);
      if (!workspaceFile.exists()) {
         throw new OseeStateException("Temporary checklist location [%s] does not exist", workspacePath);
      }
      /**
       * Create a valid fileList. This would not need to be done as the string in atsApi.getConfigValue would be valid.
       */
      StringBuilder sb = new StringBuilder();
      for (String filename : Arrays.asList("Requirements_Checklist.xlsx", "Code_Checklist.txt",
         "Test_Checklist.docx")) {
         try {
            String checklistName = filename;
            checklistName = checklistName.replaceFirst("\\..*$", "");
            checklistName = checklistName.replaceFirst("_", " ");
            sb.append(checklistName);
            sb.append(";");
            File file =
               OseeInf.getResourceAsFile("demoPeerChecklists/" + filename, XPeerChecklistAttachmentExampleCombo.class);
            String filePath = file.getAbsolutePath();
            sb.append(filePath);
            sb.append("\n");
            System.err.println(filePath);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      // Add checklist stored in OSEE that were created as part of dbinit
      String dbFileList = AtsApiService.get().getConfigValue("DemoPeerReviewChecklist");
      for (String entry : dbFileList.split("\n")) {
         if (entry.contains("osee")) {
            sb.append(entry + "\n");
         }
      }
      String fileList = sb.toString();
      return fileList;
   }

   @Override
   public String getAttachmentStaticId() {
      return PEER_REVIEW_CHECKLIST_STATIC_ID;
   }
}
