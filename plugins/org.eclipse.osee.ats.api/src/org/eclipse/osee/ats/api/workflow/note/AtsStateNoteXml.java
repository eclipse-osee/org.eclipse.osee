/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.workflow.note;

import java.util.Date;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNoteXml {

   private Date date;
   private String state;
   private String msg;
   private UserToken user;
   private AtsStateNoteXmlType type;

   public AtsStateNoteXml(AtsStateNoteXmlType type, String state, String date, UserToken user, String msg) {
      Long l = Long.valueOf(date);
      this.date = new Date(l.longValue());
      this.state = Strings.intern(state);
      this.msg = msg;
      this.user = user;
      this.type = type;
   }

   public AtsStateNoteXml(String type, String state, String date, UserToken user, String msg) {
      this(AtsStateNoteXmlType.getType(type), state, date, user, msg);
   }

   public Date getDate() {
      return date;
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

   @Override
   public String toString() {
      return String.format("%s from %s%s on %s - %s", type, user.getName(), toStringState(),
         DateUtil.getMMDDYYHHMM(date), msg);
   }

   private String toStringState() {
      return state.isEmpty() ? "" : " for \"" + state + "\"";
   }

   public UserToken getUser() {
      return user;
   }

   public AtsStateNoteXmlType getType() {
      return type;
   }

   public String toHTML() {
      return toString().replaceFirst("^Note: ", "<b>Note:</b>");
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public void setUser(UserToken user) {
      this.user = user;
   }

   public void setType(AtsStateNoteXmlType type) {
      this.type = type;
   }
}