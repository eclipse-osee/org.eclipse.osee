/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.activity.api;

import java.sql.Timestamp;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Ryan D. Brooks
 */
@XmlRootElement
public class ActivityEntry extends ActivityEntryId {
   private Long parentId;
   private Long typeId;
   private Long accountId;
   private Long serverId;
   private Long clientId;
   private Timestamp startTime;
   private Long duration;
   private Integer status;
   private String messageArgs;

   public ActivityEntry(Long id) {
      super(id);
   }

   public Long getParentId() {
      return parentId;
   }

   public Long getTypeId() {
      return typeId;
   }

   public Long getAccountId() {
      return accountId;
   }

   public Long getServerId() {
      return serverId;
   }

   public Long getClientId() {
      return clientId;
   }

   public Timestamp getStartTime() {
      return startTime;
   }

   public Long getDuration() {
      return duration;
   }

   public Integer getStatus() {
      return status;
   }

   public String getMessageArgs() {
      return messageArgs;
   }

   public void setParentId(Long parentId) {
      this.parentId = parentId;
   }

   public void setTypeId(Long typeId) {
      this.typeId = typeId;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public void setServerId(Long serverId) {
      this.serverId = serverId;
   }

   public void setClientId(Long clientId) {
      this.clientId = clientId;
   }

   public void setStartTime(Timestamp startTime) {
      this.startTime = startTime;
   }

   public void setDuration(Long duration) {
      this.duration = duration;
   }

   public void setStatus(Integer status) {
      this.status = status;
   }

   public void setMessageArgs(String messageArgs) {
      this.messageArgs = messageArgs;
   }

   @Override
   public String toString() {
      return "ActivityEntry [" + getId() + ", parentId=" + parentId + ", typeId=" + typeId + ", accountId=" + accountId + ", serverId=" + serverId + ", clientId=" + clientId + ", startTime=" + startTime + ", duration=" + duration + ", status=" + status + ", messageArgs=" + messageArgs + "]";
   }

}
