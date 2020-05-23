/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.column;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.AtsConfigurations;

/**
 * @author Donald G. Dunne
 */
public interface IAtsColumnService {

   String getColumnText(IAtsColumnId columnId, IAtsObject atsObject);

   String getColumnText(String id, IAtsObject atsObject);

   IAtsColumn getColumn(IAtsColumnId columnId);

   void add(String id, IAtsColumn column);

   IAtsColumn getColumn(String id);

   String getColumnText(AtsConfigurations configurations, IAtsColumnId column, IAtsObject atsObject);

   String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject);

}
