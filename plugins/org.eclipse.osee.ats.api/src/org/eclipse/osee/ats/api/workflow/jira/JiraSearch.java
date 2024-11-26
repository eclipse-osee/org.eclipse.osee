/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.api.workflow.jira;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class JiraSearch {
   public String expand;
   public int startAt;
   public int maxResults;
   public int total;
   public ArrayList<Issue> issues;
   private XResultData rd = new XResultData();

   @Override
   public String toString() {
      return "JiraSearch [\nexpand=" + expand + ", \nstartAt=" + startAt + ", \nmaxResults=" + maxResults + ", \ntotal=" + total + ", \nissues=" + issues + "]\n";
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }
}
