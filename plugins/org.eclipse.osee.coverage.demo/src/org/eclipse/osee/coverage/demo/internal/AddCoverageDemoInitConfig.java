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
package org.eclipse.osee.coverage.demo.internal;

import org.eclipse.osee.coverage.demo.CoverageChoice;
import org.eclipse.osee.framework.database.init.DatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.DefaultDbInitTasks;
import org.eclipse.osee.framework.database.init.DefaultOseeTypeDefinitions;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IGroupSelector;

/**
 * @author Roberto E. Escobar
 */
public class AddCoverageDemoInitConfig implements IAddDbInitChoice {

   @Override
   public void addDbInitChoice(IGroupSelector groupSelection) {
      DatabaseInitConfiguration config = new DatabaseInitConfiguration();
      config.addTask(asLocalExtensionId("AddCommonBranchForCoverageClientDemo"));
      config.addTask("org.eclipse.osee.framework.access.provider.FrameworkAccessConfig");

      config.addTask(asLocalExtensionId("AddCoverageDemoUsers"));
      config.addTask(DefaultDbInitTasks.SIMPLE_TEMPLATE_PROVIDER.getExtensionId());
      config.addTask(asLocalExtensionId("AddCoverageBranch"));

      config.addOseeType(DefaultOseeTypeDefinitions.OSEE_BASE_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.DEFINE_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.ATS_TYPES);
      config.addOseeType(DefaultOseeTypeDefinitions.COVERAGE_TYPES);

      groupSelection.addChoice(CoverageChoice.OSEE_COVERAGE_DEMO, config);
   }

   private String asLocalExtensionId(String contributionId) {
      return String.format("org.eclipse.osee.coverage.demo.%s", contributionId);
   }
}
