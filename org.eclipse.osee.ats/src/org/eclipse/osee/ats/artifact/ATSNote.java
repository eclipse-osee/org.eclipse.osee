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
package org.eclipse.osee.ats.artifact;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class ATSNote {
   private WeakReference<Artifact> artifactRef;
   private boolean enabled = true;
   private static String ATS_NOTE_TAG = "AtsNote";
   private static String LOG_ITEM_TAG = "Item";

   public ATSNote(Artifact artifact) {
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   public void addNote(NoteType type, String state, String msg, User user) {
      addNote(type, state, msg, new Date(), user);
   }

   public void addNoteItem(NoteItem noteItem) {
      addNote(noteItem.getType(), noteItem.getState(), noteItem.getMsg(), noteItem.getDate(), noteItem.getUser());
   }

   public void addNote(NoteType type, String state, String msg, Date date, User user) {
      if (!enabled) return;
      NoteItem logItem = new NoteItem(type, state, date.getTime() + "", user, msg);
      List<NoteItem> logItems = getNoteItems();
      logItems.add(logItem);
      saveNoteItems(logItems);
   }

   public List<NoteItem> getNoteItems() {
      List<NoteItem> logItems = new ArrayList<NoteItem>();
      try {
         String xml = getArtifact().getSoleAttributeValue(ATSAttributes.STATE_NOTES_ATTRIBUTE.getStoreName(), "");
         if (!xml.equals("")) {
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(LOG_ITEM_TAG);
            for (int i = 0; i < nodes.getLength(); i++) {
               Element element = (Element) nodes.item(i);
               User user = UserManager.getUserByUserId(element.getAttribute("userId"));
               NoteItem item =
                     new NoteItem(element.getAttribute("type"), element.getAttribute("state"),
                           element.getAttribute("date"), user, element.getAttribute("msg"));
               logItems.add(item);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return logItems;
   }

   public void saveNoteItems(List<NoteItem> items) {
      try {
         Document doc = Jaxp.newDocument();
         Element rootElement = doc.createElement(ATS_NOTE_TAG);
         doc.appendChild(rootElement);
         for (NoteItem item : items) {
            Element element = doc.createElement(LOG_ITEM_TAG);
            element.setAttribute("type", item.getType().name());
            element.setAttribute("state", item.getState());
            element.setAttribute("date", item.getDate().getTime() + "");
            element.setAttribute("userId", item.getUser().getUserId());
            element.setAttribute("msg", item.getMsg());
            rootElement.appendChild(element);
         }
         getArtifact().setSoleAttributeValue(ATSAttributes.STATE_NOTES_ATTRIBUTE.getStoreName(),
               Jaxp.getDocumentXml(doc));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't create ats note document", ex);
      }
   }

   /**
    * Display Note Table; If state == null, only display non-state notes Otherwise, show only notes associated with
    * state
    * 
    * @param state
    */
   public String getTable(String state) {
      ArrayList<NoteItem> showNotes = new ArrayList<NoteItem>();
      List<NoteItem> noteItems = getNoteItems();
      try {
         if (!getArtifact().isAttributeTypeValid(ATSAttributes.STATE_NOTES_ATTRIBUTE.getStoreName())) {
            return "";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "";
      }

      for (NoteItem li : noteItems) {
         if (state == null) {
            if (li.getState().equals("")) showNotes.add(li);
         } else if ((state.equals("ALL")) || li.getState().equals(state)) {
            showNotes.add(li);
         }
      }
      if (showNotes.size() == 0) return "";
      StringBuilder builder = new StringBuilder();
      builder.append("<TABLE BORDER=\"1\" cellspacing=\"1\" cellpadding=\"3%\" width=\"100%\"><THEAD><TR><TH>Type</TH><TH>State</TH>" + "<TH>Message</TH><TH>User</TH><TH>Date</TH></THEAD></TR>");
      for (NoteItem note : showNotes) {
         User user = note.getUser();
         String name = "";
         if (user != null) {
            name = user.getName();
            if (name == null || name.equals("")) {
               name = user.getName();
            }
         }
         builder.append("<TR>");
         builder.append("<TD>" + note.getType() + "</TD>");
         builder.append("<TD>" + (note.getState().equals("") ? "," : note.getState()) + "</TD>");
         builder.append("<TD>" + (note.getMsg().equals("") ? "," : note.getMsg()) + "</TD>");

         if (user != null && user.isMe())
            builder.append("<TD bgcolor=\"#CCCCCC\">" + name + "</TD>");
         else
            builder.append("<TD>" + name + "</TD>");

         builder.append("<TD>" + (new SimpleDateFormat("MM/dd/yyyy h:mm a")).format(note.getDate()) + "</TD>");
         builder.append("</TR>");
      }
      builder.append("</TABLE>");
      return builder.toString();
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

}