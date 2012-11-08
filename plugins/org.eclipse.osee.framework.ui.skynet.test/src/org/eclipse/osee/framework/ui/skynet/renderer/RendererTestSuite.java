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
package org.eclipse.osee.framework.ui.skynet.renderer;

import org.eclipse.osee.framework.ui.skynet.renderer.imageDetection.ImageDetectionTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   ImageDetectionTestSuite.class,
   PresentationTypeTest.class,
   RelationOrderRendererTest.class,
   RenderingUtilTest.class})
/**
 * @author Roberto E. Escobar
 */
public class RendererTestSuite {
   // test provided above
}
