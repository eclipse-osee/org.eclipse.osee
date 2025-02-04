/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Date;

public class TimelineScriptToken {

   private Date executedAt;
   private Integer pass;
   private Integer fail;
   private Boolean abort;
   private String teamName;

   public TimelineScriptToken() {
      // Needed for jax-rs serialization
   }

   public TimelineScriptToken(Date executedAt, Integer pass, Integer fail, Boolean abort) {
      this.setExecutedAt(executedAt);
      this.setPass(pass);
      this.setFail(fail);
      this.setAbort(abort);
      this.setTeamName("");
   }

   public Date getExecutedAt() {
      return executedAt;
   }

   public void setExecutedAt(Date executedAt) {
      this.executedAt = executedAt;
   }

   public Integer getPass() {
      return pass;
   }

   public void setPass(Integer pass) {
      this.pass = pass;
   }

   public Integer getFail() {
      return fail;
   }

   public void setFail(Integer fail) {
      this.fail = fail;
   }

   public Boolean getAbort() {
      return abort;
   }

   public void setAbort(Boolean abort) {
      this.abort = abort;
   }

   public String getTeamName() {
      return teamName;
   }

   public void setTeamName(String teamName) {
      this.teamName = teamName;
   }

}
