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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.ui.skynet.renderer.RendererTestSuite;
import org.eclipse.osee.framework.ui.skynet.results.ResultsTestSuite;
import org.eclipse.osee.framework.ui.skynet.util.UtilTestSuite;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetsUtilTestSuite;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   RendererTestSuite.class,
   ResultsTestSuite.class,
   UtilTestSuite.class,
   XWidgetsUtilTestSuite.class,
   XMergeTestSuite.class})
/**
 * @author Roberto E. Escobar
 */
public class AllUiSkynetJunitTestSuite {
   // test provided above
}
