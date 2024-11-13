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
public class Fields {
   public String summary;
   public String description;
   public Status status;
   public Status assignee;

   @Override
   public String toString() {
      return "Fields [summary=" + summary + ", description=" + description + ", status=" + status + ", assignee=" + assignee + "]";
   }

}
