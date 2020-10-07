/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver;

import javax.xml.bind.annotation.XmlElement;

public class LdapUserDetailsWrapper {

   String diaplayName;
   String userId;
   String mail;

   /**
    * @return userId, if current user id is present
    */
   @XmlElement
   public String getUserId() {
      return this.userId;
   }

   /**
    * Method to set userId
    */
   public void setUserId(final String userId) {
      this.userId = userId;
   }

   /**
    * @return mail
    */
   @XmlElement
   public String getMail() {
      return this.mail;
   }

   /**
    * Method to set Mail
    */
   public void setMail(final String mail) {
      this.mail = mail;
   }

   /**
    * @return display name
    */
   @XmlElement
   public String getDisplayName() {
      return this.diaplayName;
   }

   /**
    * Method to set display name
    */
   public void setDisplayName(final String diaplayName) {
      this.diaplayName = diaplayName;
   }

}
