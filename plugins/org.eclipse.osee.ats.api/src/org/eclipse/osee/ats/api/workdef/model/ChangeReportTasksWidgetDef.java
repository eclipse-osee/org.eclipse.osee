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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXCreateChangeReportTasksWidget;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;

/**
 * @author Donald G. Dunne
 */
public class ChangeReportTasksWidgetDef extends WidgetDef {

   public ChangeReportTasksWidgetDef(AtsTaskDefToken atsTaskDefToken) {
      super(XXCreateChangeReportTasksWidget);
      addParameter(AtsTaskDefToken.ID, atsTaskDefToken);
   }

}
