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
package org.eclipse.osee.icteam.common.clientserver.dependent.datamodel;

import java.util.Date;

/**
 * This class is for Comments.
 *
 * @author Ajay Chandrahasan
 */
public class CommentArtifact {

   private String msg;

   /**
    * gets message
    *
    * @return
    */
   public String getMsg() {
      return this.msg;
   }

   /**
    * sets message
    *
    * @param msg
    */
   public void setMsg(final String msg) {
      this.msg = msg;
   }

   /**
    * gets User
    * 
    * @return
    */
   public String getUser() {
      return this.user;
   }

   /**
    * set user
    *
    * @param user
    */
   public void setUser(final String user) {
      this.user = user;
   }

   /**
    * gets date
    *
    * @return
    */
   public Date getDate() {
      return this.date;
   }

   /**
    * sets date
    *
    * @param date
    */
   public void setDate(final Date date) {
      this.date = date;
   }

   private String user;
   private Date date;
}
