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

package org.eclipse.osee.ats.ide.integration.tests.ats.world.search;

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
   // do nothing
}
