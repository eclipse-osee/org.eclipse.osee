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
package org.eclipse.osee.ats.rest.internal.workitem.journal;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.framework.core.util.OseeInf;

/**
 * @author Donald G. Dunne
 */
public class JournalWebOperations {

   private final IAtsWorkItem workItem;
   private final AtsApi atsApi;
   private String page;
   private final AtsUser user;

   public JournalWebOperations(IAtsWorkItem workItem, AtsUser user, AtsApi atsApi) {
      this.workItem = workItem;
      this.user = user;
      this.atsApi = atsApi;
   }

   public String getHtml() {
      page = OseeInf.getResourceContents("templates/journal/journal.html", JournalWebOperations.class);
      JournalData jData = atsApi.getWorkItemService().getJournalData(workItem, new JournalData());
      String comments = jData.getResults().isErrors() ? jData.getResults().toString() : jData.getCurrentMsg();
      page = page.replaceFirst(JournalKey.PUT_COMMENTS_HERE.name(), comments);

      String url =
         String.format("<b><a target=\"_blank\" href=\"/ats/ui/action/%s\">View Action Details</a></b><br/><br/>",
            workItem.getAtsId());
      page = page.replaceFirst(JournalKey.PUT_ACTION_DETAILS_HERE.name(), url);

      handlePage();
      handleForm();

      return page;
   }

   private void handleForm() {
      String form = OseeInf.getResourceContents("templates/journal/journalForm.html", JournalWebOperations.class);
      page = page.replaceFirst(JournalKey.PUT_FORM_HERE.name(), form);
      page = page.replaceAll(JournalKey.PUT_ATS_ID_HERE.name(), workItem.getAtsId());
      page = page.replaceFirst(JournalKey.PUT_USER_NAME_HERE.name(), user.getName());
      page = page.replaceFirst(JournalKey.PUT_USER_AID_HERE.name(), user.getIdString());
   }

   private void handlePage() {
      page = page.replaceFirst(JournalKey.PUT_TITLE_HERE.name(), String.format("\"%s\" - %s - \"%s\"",
         workItem.getArtifactType().getName(), workItem.getAtsId(), workItem.getName()));
   }

}
