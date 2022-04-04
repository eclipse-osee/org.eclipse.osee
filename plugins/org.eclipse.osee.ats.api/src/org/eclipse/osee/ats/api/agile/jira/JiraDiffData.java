/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.jira;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Stephen J. Molaro
 */
public class JiraDiffData {

   private XResultData results;
   private String programIncrement;
   private String teamId;
   private List<String> desiredAttributes;

   public JiraDiffData() {
      // for jax-rs
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public String getProgramIncrement() {
      return programIncrement;
   }

   public String getTeamId() {
      return teamId;
   }

   public List<String> getDesiredAttributes() {
      return desiredAttributes;
   }

   public void setProgramIncrement(String programIncrement) {
      this.programIncrement = programIncrement;
   }

   public void setTeamId(String teamId) {
      this.teamId = teamId;
   }

   public void setDesiredAttributes(List<String> desiredAttributes) {
      this.desiredAttributes = desiredAttributes;
   }

}
