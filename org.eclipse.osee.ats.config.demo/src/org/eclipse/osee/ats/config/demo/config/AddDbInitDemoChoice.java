/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.database.utility.GroupSelection;
import org.eclipse.osee.framework.database.utility.IAddDbInitChoice;

/**
 * @author Donald G. Dunne
 */
public class AddDbInitDemoChoice implements IAddDbInitChoice {

   /**
    * Add the ability to wipe an OSEE database and configure it for the ATS Demo Configuration which will showcase ATS
    * functionality.
    */
   public AddDbInitDemoChoice() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.database.utility.IAddDbInitChoice#addDbInitChoice()
    */
   public void addDbInitChoice(GroupSelection groupSelection) {
      addDemoDbInitChoice(groupSelection);
   }

   public void addDemoDbInitChoice(GroupSelection groupSelection) {
      List<String> dbInitTasks = new ArrayList<String>();

      dbInitTasks.add("org.eclipse.osee.ats.config.demo.AddCommonBranchForAtsDemo");
      dbInitTasks.add("org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTask");
      dbInitTasks.add("org.eclipse.osee.ats.AtsDatabaseConfig");
      dbInitTasks.add("org.eclipse.osee.ats.config.demo.AtsConfigDemoDatabaseConfig");
      dbInitTasks.add("org.eclipse.osee.framework.ui.skynet.TagCommonBranchArtifacts");

      groupSelection.addChoice("OSEE Demo Database", dbInitTasks, false);
   }

}
