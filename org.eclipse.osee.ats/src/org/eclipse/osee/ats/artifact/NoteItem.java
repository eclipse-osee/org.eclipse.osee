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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.ats.NoteType;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

public class NoteItem {

   private Date date;
   private String state;
   private String msg;
   private User user;
   private NoteType type = NoteType.Other;

   public NoteItem() {
   }

   public NoteItem(NoteType type, String state, String date, User user, String msg) {
      Long l = new Long(date);
      this.date = new Date(l.longValue());
      this.state = state;
      this.msg = msg;
      this.user = user;
      this.type = type;
   }

   public NoteItem(String type, String state, String date, User user, String msg) {
      this(NoteType.getType(type), state, date, user, msg);
   }

   public Date getDate() {
      return date;
   }

   public String getDate(String pattern) {
      if (pattern != null) return (new SimpleDateFormat(pattern)).format(date);
      return date.toString();
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   public String toString() {
      return msg + " (" + type + ") by " + user + " on " + getDate(XDate.MMDDYYHHMM) + "\n";
   }

   public User getUser() {
      return user;
   }

   public NoteType getType() {
      return type;
   }

   public void setType(NoteType type) {
      this.type = type;
   }

   public String toHTML() {
      return "Note: \"" + type + "\" from \"" + user.getName() + "\"" + (state.equals("") ? "" : " for \"" + state + "\" state") + " on " + getDate(XDate.MMDDYYHHMM) + " - " + msg;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   /**
    * @param user the user to set
    */
   public void setUser(User user) {
      this.user = user;
   }

}
