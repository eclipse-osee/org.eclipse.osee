/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.demo.internal;

import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.database.init.DatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.DefaultOseeTypeDefinitions;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IGroupSelector;

/**
 * @author Roberto E. Escobar
 */
public class AddClientDemoInitConfig implements IAddDbInitChoice {

   @Override
   public void addDbInitChoice(IGroupSelector groupSelection) {
      DatabaseInitConfiguration config = new DatabaseInitConfiguration();

      config.addTask(asLocalExtensionId("AddRequirementData"));

      config.addOseeType(DefaultOseeTypeDefinitions.OSEE_BASE_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.DEFINE_TYPES);
      config.addOseeType(asLocalExtensionId("OseeTypes_ClientDemo"));

      groupSelection.addChoice(DemoChoice.OSEE_CLIENT_DEMO.name(), config);
   }

   private String asLocalExtensionId(String contributionId) {
      return String.format("org.eclipse.osee.client.demo.%s", contributionId);
   }
}
