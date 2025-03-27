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
public class StatusCategory {
   public String self;
   public int id;
   public String key;
   public String colorName;
   public String name;

   @Override
   public String toString() {
      return "\n\nStatusCategory [\nself=" + self + ", \nid=" + id + ", \nkey=" + key + ", \ncolorName=" + colorName + ", \nname=" + name + "]\n";
   }

}
