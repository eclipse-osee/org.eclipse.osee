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
public class Status {
   public String self;
   public String description;
   public String iconUrl;
   public String name;
   public String id;
   public StatusCategory statusCategory;

   @Override
   public String toString() {
      return "\n\nStatus [\nself=" + self + ", \ndescription=" + description + ", \niconUrl=" + iconUrl + ", \nname=" + name + ", \nid=" + id + ", \nstatusCategory=" + statusCategory + "]\n";
   }

}
