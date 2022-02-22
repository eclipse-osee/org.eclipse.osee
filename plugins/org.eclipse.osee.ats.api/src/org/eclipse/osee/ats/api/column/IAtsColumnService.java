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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.AtsConfigurations;

/**
 * @author Donald G. Dunne
 */
public interface IAtsColumnService {

   String getColumnText(AtsColumnToken column, IAtsObject atsObject);

   String getColumnText(String id, IAtsObject atsObject);

   AtsColumn getColumn(AtsColumnToken column);

   void add(String id, AtsColumn column);

   AtsColumn getColumn(String id);

   String getColumnText(AtsConfigurations configurations, AtsColumnToken column, IAtsObject atsObject);

   String getColumnText(AtsConfigurations configurations, String id, IAtsObject atsObject);

   Collection<IAtsColumnProvider> getColumProviders();

}
