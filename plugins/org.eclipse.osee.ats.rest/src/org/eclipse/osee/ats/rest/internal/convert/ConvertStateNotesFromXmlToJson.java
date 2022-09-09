/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.rest.internal.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNote;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteType;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteXml;
import org.eclipse.osee.ats.api.workflow.note.AtsStateNoteXmlType;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * See description below
 *
 * @author Donald G Dunne
 */
public class ConvertStateNotesFromXmlToJson extends AbstractConvertGuidToId {

   public final static String LOG_ITEM_TAG = "Item";
   public final static String ATS_NOTE_TAG = "AtsNote";
   private XResultData rd;
   private boolean reportOnly;

   public ConvertStateNotesFromXmlToJson(Log logger, JdbcClient jdbcClient, OrcsApi orcsApi, AtsApi atsApi) {
      super(logger, jdbcClient, orcsApi, atsApi);
   }

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {
      this.rd = rd;
      this.reportOnly = reportOnly;
      if (reportOnly) {
         rd.log("REPORT ONLY - Changes not persisted\n");
      }
      IAtsChangeSet changes = atsApi.createChangeSet(getName());
      int numChanges = 0;
      // Uncomment to process specific worflows
      //      Collection<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(
      //         "10597232,10827167,10827897,10838579,10838691,10849611,10863883");
      Collection<IAtsWorkItem> workItems =
         atsApi.getQueryService().getWorkItemsAtrTypeExists(AtsAttributeTypes.StateNotes);
      rd.logf("Found %s attributes\n\n", workItems.size());
      StringBuilder sb = new StringBuilder();
      for (IAtsWorkItem workItem : workItems) {
         sb.append(workItem.getIdString());
         sb.append(",");
         Collection<IAttribute<Object>> attributes =
            atsApi.getAttributeResolver().getAttributes(workItem, AtsAttributeTypes.StateNotes);
         for (IAttribute<Object> stateNoteAttr : attributes) {
            String stateNoteVal = (String) stateNoteAttr.getValue();
            if (((String) stateNoteAttr.getValue()).startsWith("<")) {
               rd.logf("Need to convert xml state notes for %s value %s\n", workItem.getAtsId(), stateNoteVal);
               numChanges++;
               List<AtsStateNoteXml> xmlNotes = getNoteItems(workItem, stateNoteVal);
               convertToJsonAndStore(workItem, xmlNotes, stateNoteAttr, changes);
            }
         }
      }
      rd.log("\nWorkItems: " + sb.toString() + "\n");
      if (reportOnly) {
         rd.log("\n" + numChanges + " Need to be Changed");
      } else {
         rd.log("\n" + numChanges + " Changes Persisted");
         changes.executeIfNeeded();
      }
   }

   private List<AtsStateNote> convertToJsonAndStore(IAtsWorkItem workItem, List<AtsStateNoteXml> xmlNotes, IAttribute<Object> stateNoteAttr, IAtsChangeSet changes) {
      List<AtsStateNote> notes = new ArrayList<>();
      for (AtsStateNoteXml xNote : xmlNotes) {
         try {
            AtsStateNoteXmlType oldType = xNote.getType();
            AtsStateNoteType newType = AtsStateNoteType.Other;
            if (oldType == AtsStateNoteXmlType.Comment || oldType == AtsStateNoteXmlType.Question) {
               newType = AtsStateNoteType.Info;
            } else if (oldType == AtsStateNoteXmlType.Error) {
               newType = AtsStateNoteType.Problem;
            }

            AtsStateNote note = new AtsStateNote(newType.name(), xNote.getState(),
               String.valueOf(xNote.getDate().getTime()), xNote.getUser(), xNote.getMsg());
            if (reportOnly) {
               rd.logf("--- Needed Note: [%s]\n", note.toString());
            } else {
               String json = atsApi.jaxRsApi().toJson(note);
               changes.addAttribute(workItem, AtsAttributeTypes.StateNotes, json);
               rd.logf("--- Converted to Note: [%s]\n", note.toString());
            }
         } catch (Exception ex) {
            rd.errorf("Exception converting %s", ex.getLocalizedMessage());
         }
      }
      if (!reportOnly) {
         changes.deleteAttribute(workItem, stateNoteAttr);
      }
      return notes;
   }

   public List<AtsStateNoteXml> getNoteItems(IAtsWorkItem workItem, String stateNoteXml) {
      try {
         if (Strings.isValid(stateNoteXml)) {
            return fromXml(stateNoteXml, "id", atsApi);
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error extracting note");
      }
      return Collections.emptyList();
   }

   public static List<AtsStateNoteXml> fromXml(String xml, String atsId, AtsApi atsApi) {
      List<AtsStateNoteXml> logItems = new ArrayList<>();
      try {
         if (Strings.isValid(xml)) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               try {
                  UserToken user = atsApi.userService().getUserByUserId(element.getAttribute("userId"));
                  AtsStateNoteXml item =
                     new AtsStateNoteXml(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                        element.getAttribute("date"), user, element.getAttribute("msg"));
                  logItems.add(item);
               } catch (UserNotInDatabase ex) {
                  atsApi.getLogger().error(ex, "Error parsing notes for [%s]", atsId);
                  AtsStateNoteXml item =
                     new AtsStateNoteXml(element.getAttribute("type"), element.getAttribute("state"), // NOPMD by b0727536 on 9/29/10 8:52 AM
                        element.getAttribute("date"), SystemUser.OseeSystem, element.getAttribute("msg"));
                  logItems.add(item);
               }
            }
         }
      } catch (Exception ex) {
         atsApi.getLogger().error(ex, "Error reading AtsNote");
      }
      return logItems;
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("ConvertStateNotesFromXmlToJson (required conversion)\n\n");
      data.append("Necessary for upgrading from OSEE 0.65 to 0.66\n");
      data.append("Items not converted will remain but not show in ATS UI\n");
      data.append("- Converts ats.State Note attr that stores 1..n notes in single xml attr to " //
         + "attrs that stores one json note per attr.\n");
      data.append("NOTE: This operation can be run multiple times\n");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Convert ats.State Notes From Xml To Json";
   }

}
