/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.client.demo.internal;

import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.database.init.DatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IGroupSelector;

/**
 * @author Roberto E. Escobar
 */
public class AddClientDemoInitConfig implements IAddDbInitChoice {

   @Override
   public void addDbInitChoice(IGroupSelector groupSelection) {
      DatabaseInitConfiguration config = new DatabaseInitConfiguration();

      config.addTask("org.eclipse.osee.client.demo.AddRequirementData");
      groupSelection.addChoice(DemoChoice.OSEE_CLIENT_DEMO.name(), config);
   }
}