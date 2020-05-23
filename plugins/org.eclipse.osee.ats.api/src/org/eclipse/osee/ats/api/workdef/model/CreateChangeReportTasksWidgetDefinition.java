/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;

/**
 * @author Donald G. Dunne
 */
public class CreateChangeReportTasksWidgetDefinition extends WidgetDefinition {

   private final AtsTaskDefToken atsTaskDefToken;

   public CreateChangeReportTasksWidgetDefinition(String name, AtsTaskDefToken atsTaskDefToken) {
      super(name, "XCreateChangeReportTasksXButton");
      this.atsTaskDefToken = atsTaskDefToken;
      addParameter(AtsTaskDefToken.ID, atsTaskDefToken);
   }

   public AtsTaskDefToken getAtsTaskDefToken() {
      return atsTaskDefToken;
   }

}
