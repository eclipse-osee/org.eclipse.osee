/*********************************************************************
 * Copyright (c) 2020, 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.AtsAttachment;
import org.eclipse.osee.ats.api.workflow.AtsAttachments;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.osgi.service.component.annotations.Component;

/**
 * This is an example of how to implement and use the XAttachmentCombo.
 *
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXAttachmentDemoWidget extends XXAbstractAttachmentArtWidget {

   public static final WidgetId ID = WidgetIdAts.XXAttachmentDemoWidget;
   private static final String LABEL = "Select Peer Review Checklist to Attach";
   private static String PEER_REVIEW_CHECKLIST_STATIC_ID = "7244494692772089382";
   public static String ATTACHMENT_EXAMPLE_KEY = "XAttachmentExampleWidget";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAttachmentDemoWidget() {
      super(ID, LABEL, "");
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

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
            File file = OseeInf.getResourceAsFile("demoPeerChecklists/" + filename, XXAttachmentDemoWidget.class);
            String filePath = file.getAbsolutePath();
            checklists.addAttachment(new AtsAttachment(checklistName, filePath, BranchToken.SENTINEL));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      // Get attachments stored in OSEE that were created as part of dbinit
      String dbFileList = AtsApiService.get().getConfigValue("PeerReviewChecklist");
      if (!dbFileList.isEmpty()) {
         try {
            AtsAttachments dbAttachments =
               AtsApiService.get().jaxRsApi().readValue(dbFileList, AtsAttachments.class);
            for (AtsAttachment att : dbAttachments.getAttachments()) {
               checklists.addAttachment(att);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Error parsing PeerReviewChecklist config", ex);
         }
      }

      return JsonUtil.toJson(checklists);
   }

   @Override
   public String getAttachmentStaticId() {
      return PEER_REVIEW_CHECKLIST_STATIC_ID;
   }

}
