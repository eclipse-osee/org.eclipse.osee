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

package org.eclipse.osee.ats.api.workflow.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNote {

   @JsonSerialize(using = ToStringSerializer.class)
   private Long id;
   @JsonSerialize(using = ToStringSerializer.class)
   private Long date;
   private Date dateObj;
   private String state;
   private String msg;
   // Where userId is user account id (artifact id)
   @JsonSerialize(using = ToStringSerializer.class)
   private Long userId;
   private ArtifactToken userTok = ArtifactToken.SENTINEL;
   private String type;

   public AtsStateNote() {
      // for jax-rs
   }

   public AtsStateNote(AtsStateNoteType type, String state, String date, UserId user, String msg) {
      this.id = Lib.generateId();
      this.date = Long.valueOf(date);
      this.state = Strings.intern(state);
      this.msg = msg;
      this.userId = user.getId();
      this.type = type.getName();
   }

   public AtsStateNote(String type, String state, String date, UserId user, String msg) {
      this(AtsStateNoteType.valueOf(type), state, date, user, msg);
   }

   @JsonIgnore
   public Date getDateObj() {
      if (dateObj == null) {
         dateObj = new Date(date);
      }
      return dateObj;
   }

   public Long getDate() {
      return date;
   }

   public void setDate(Long date) {
      this.date = date;
   }

   public void setDateObj(Date date) {
      this.date = date.getTime();
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   @Override
   public String toString() {
      return String.format("%s from %s%s on %s - %s", type, getUserName(), toStringState(),
         DateUtil.getMMDDYYHHMM(getDateObj()), msg);
   }

   private String toStringState() {
      return state.isEmpty() ? "" : " for \"" + state + "\"";
   }

   public String getType() {
      return type;
   }

   @JsonIgnore
   public AtsStateNoteType getTypeEnum() {
      return AtsStateNoteType.valueOf(type);
   }

   public void setType(String type) {
      this.type = type;
   }

   public String toHTML() {
      return toString().replaceFirst("^Note: ", "<b>Note:</b>");
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public UserId getUser() {
      return UserId.valueOf(userId);
   }

   public void setUser(UserId user) {
      this.userId = user.getId();
   }

   @JsonIgnore
   public ArtifactToken getUserTok() {
      return userTok;
   }

   public void setUserTok(ArtifactToken userTok) {
      this.userTok = userTok;
   }

   @JsonIgnore
   public String getUserName() {
      return (userTok.isValid() ? userTok.getName() : userId.toString());
   }

}