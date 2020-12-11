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

package org.eclipse.osee.framework.core.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class MailStatus {

   private String uuid;
   private String subject;
   private Date dateSent;
   private boolean verified;

   private final Set<String> fromAddress = new HashSet<>();
   private final Set<String> invalidAddress = new HashSet<>();
   private final Set<String> sentAddress = new HashSet<>();
   private final Set<String> unsentAddress = new HashSet<>();

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public void setDateSent(Date dateSent) {
      this.dateSent = dateSent;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getUuid() {
      return uuid;
   }

   public Date getDateSent() {
      return dateSent;
   }

   public String getSubject() {
      return subject;
   }

   public boolean isVerified() {
      return verified;
   }

   public void setVerified(boolean verified) {
      this.verified = verified;
   }

   @XmlElement
   public Set<String> getFromAddress() {
      return fromAddress;
   }

   @XmlElement
   public Set<String> getInvalidAddress() {
      return invalidAddress;
   }

   @XmlElement
   public Set<String> getSentAddress() {
      return sentAddress;
   }

   @XmlElement
   public Set<String> getUnsentAddress() {
      return unsentAddress;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (uuid == null ? 0 : uuid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      MailStatus other = (MailStatus) obj;
      if (uuid == null) {
         if (other.uuid != null) {
            return false;
         }
      } else if (!uuid.equals(other.uuid)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "MailStatus [uuid=" + uuid + ", subject=" + subject + ", dateSent=" + dateSent + ", verified=" + verified + ", fromAddress=" + fromAddress + ", invalidAddress=" + invalidAddress + ", sentAddress=" + sentAddress + ", unsentAddress=" + unsentAddress + "]";
   }
}