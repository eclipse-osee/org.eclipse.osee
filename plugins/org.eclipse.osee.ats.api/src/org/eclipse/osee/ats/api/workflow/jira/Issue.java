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

/**
 * @author Donald G. Dunne
 */
public class Issue {
   public String expand;
   public String id;
   public String self;
   public String key;
   public Fields fields;

   @Override
   public String toString() {
      return "\n\nIssue [\nexpand=" + expand + ", \nid=" + id + ", \nself=" + self + ", \nkey=" + key + ", \nfields=" + fields + "]\n";
   }

   public String getState() {
      return fields.status.name;
   }

   public String getAssigneeUserId() {
      return fields.assignee.name;
   }
}
