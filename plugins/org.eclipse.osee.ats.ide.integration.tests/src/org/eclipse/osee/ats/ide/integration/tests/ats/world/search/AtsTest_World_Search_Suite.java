/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   WorldSearchItemTest.class,
   AtsQueryServiceImplTest.class,
   AtsConfigQueryImplTest.class,
   AtsQueryImplTest.class,
   MyFavoritesSearchItemTest.class,
   MyWorldSearchItemTest.class,
   MySubscribedSearchItemTest.class,
   NextVersionSearchItemTest.class,
   VersionTargetedForTeamSearchItemTest.class,
   ShowOpenWorkflowsByArtifactTypeTest.class,
   LegacyPcrIdQuickSearchTest.class,
   TeamDefinitionQuickSearchTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_World_Search_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      System.out.println("\n\nBegin " + AtsTest_World_Search_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_World_Search_Suite.class.getSimpleName());
   }
}
