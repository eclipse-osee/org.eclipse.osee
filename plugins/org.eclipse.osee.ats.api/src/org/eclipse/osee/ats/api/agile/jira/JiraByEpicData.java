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

import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class JiraByEpicData {

   private XResultData results;
   private String tabDelimReport;

   public JiraByEpicData() {
      // for jax-rs
   }

   public XResultData getResults() {
      return results;
   }

   public void setResults(XResultData results) {
      this.results = results;
   }

   public String getTabDelimReport() {
      return tabDelimReport;
   }

   public void setTabDelimReport(String tabDelimReport) {
      this.tabDelimReport = tabDelimReport;
   }

}
