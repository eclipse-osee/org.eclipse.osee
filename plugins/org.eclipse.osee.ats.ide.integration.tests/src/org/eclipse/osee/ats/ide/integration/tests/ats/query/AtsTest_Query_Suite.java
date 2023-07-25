/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This test suite MUST be done first after all the DemoDbPopulateSuite so query tests are not corrupted by future tests
 *
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   AtsActionEndpointImplTest.class,
   AtsActionUiEndpointTest.class,
   AtsProductLineEndpointTest.class,
   AtsAttributeEndpointImplTest.class,
   AtsActionEndpointImplOptionsTest.class,
   AtsWorkItemFilterTest.class,
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
   TeamDefinitionQuickSearchTest.class, //
})
public class AtsTest_Query_Suite {
   // do nothing
}
