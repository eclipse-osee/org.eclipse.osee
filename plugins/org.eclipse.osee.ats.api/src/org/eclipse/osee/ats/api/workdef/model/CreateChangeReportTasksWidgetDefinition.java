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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XCreateChangeReportTasksArtWidget;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;

/**
 * @author Donald G. Dunne
 */
public class CreateChangeReportTasksWidgetDefinition extends WidgetDefinition {

   public CreateChangeReportTasksWidgetDefinition(String name, AtsTaskDefToken atsTaskDefToken) {
      super(name, XCreateChangeReportTasksArtWidget);
      addParameter(AtsTaskDefToken.ID, atsTaskDefToken);
   }

}
