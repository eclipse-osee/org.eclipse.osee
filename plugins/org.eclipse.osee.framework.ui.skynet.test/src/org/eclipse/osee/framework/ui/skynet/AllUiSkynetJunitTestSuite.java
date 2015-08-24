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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTestSuite;
import org.eclipse.osee.framework.ui.skynet.renderer.RendererTestSuite;
import org.eclipse.osee.framework.ui.skynet.results.ResultsTestSuite;
import org.eclipse.osee.framework.ui.skynet.util.UtilTestSuite;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetsUtilTestSuite;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ArtifactTestSuite.class,
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
