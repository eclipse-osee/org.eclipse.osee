/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.demo.config;

import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.database.init.DatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.DefaultOseeTypeDefinitions;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IGroupSelector;

/**
 * * Add the ability to wipe an OSEE database and configure it for the ATS Demo Configuration which will showcase ATS
 * functionality.
 *
 * @author Donald G. Dunne
 */
public class AddDbInitDemoChoice implements IAddDbInitChoice {

   @Override
   public void addDbInitChoice(IGroupSelector groupSelection) {
      DatabaseInitConfiguration config = new DatabaseInitConfiguration();
      System.setProperty("user.name", DemoUsers.Joe_Smith.getLoginIds().iterator().next());
      config.addTask("org.eclipse.osee.ats.ide.AtsDatabaseConfigClient");
      config.addTask("org.eclipse.osee.ats.ide.demo.DemoDatabaseConfigClient");
      config.addTask("org.eclipse.osee.ats.ide.demo.DemoWebDatabaseConfig");

      config.addOseeType(DefaultOseeTypeDefinitions.OSEE_BASE_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.DEFINE_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.ATS_TYPES);
      config.addOseeType("org.eclipse.osee.ats.ide.demo.OseeTypes_Demo");
      groupSelection.addChoice(DemoChoice.ATS_CLIENT_DEMO, config);
   }
}
