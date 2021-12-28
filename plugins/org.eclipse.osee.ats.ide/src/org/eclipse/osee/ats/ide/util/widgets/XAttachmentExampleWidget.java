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
import org.eclipse.osee.ats.api.workflow.AtsAttachment;
import org.eclipse.osee.ats.api.workflow.AtsAttachments;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
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
public class XAttachmentExampleWidget extends XAttachmentWidget {

   private static final String LABEL = "Select Peer Review Checklist to Attach";
   private static String PEER_REVIEW_CHECKLIST_STATIC_ID = "7244494692772089382";
   public static String ATTACHMENT_EXAMPLE_KEY = "XAttachmentExampleWidget";

   public XAttachmentExampleWidget() {
      super(LABEL, "");
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
      AtsAttachments checklists = new AtsAttachments();
      for (String filename : Arrays.asList("Requirements_Checklist.xlsx", "Code_Checklist.txt",
         "Test_Checklist.docx")) {
         try {
            String checklistName = filename;
            checklistName = checklistName.replaceFirst("\\..*$", "");
            checklistName = checklistName.replaceFirst("_", " ");
            File file = OseeInf.getResourceAsFile("demoPeerChecklists/" + filename, XAttachmentExampleWidget.class);
            String filePath = file.getAbsolutePath();
            checklists.addAttachment(new AtsAttachment(checklistName, filePath, BranchToken.SENTINEL));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      // Get attachment stored in OSEE that were created as part of dbinit
      String dbFileList = AtsApiService.get().getConfigValue("PeerReviewChecklist");
      // If Attachments exists in both file and OSEE, merge them together to get single json string
      if (!dbFileList.isEmpty()) {
         dbFileList = dbFileList.substring(dbFileList.indexOf(": [ {") + 1, dbFileList.length() - 3);
      }
      dbFileList = dbFileList.replace("[", ",");
      String jsonFile = JsonUtil.toJson(checklists);
      int index = jsonFile.indexOf(jsonFile.charAt(jsonFile.length() - 3));
      StringBuilder sb = new StringBuilder(jsonFile);
      sb.insert(index, dbFileList);

      return sb.toString();
   }

   @Override
   public String getAttachmentStaticId() {
      return PEER_REVIEW_CHECKLIST_STATIC_ID;
   }
}
