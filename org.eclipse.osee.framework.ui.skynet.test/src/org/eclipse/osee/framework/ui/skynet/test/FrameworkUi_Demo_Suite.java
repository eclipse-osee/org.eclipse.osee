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
package org.eclipse.osee.framework.ui.skynet.test;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.ui.skynet.test.cases.InterArtifactDropTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.PreviewAndMultiPreviewTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.RelationOrderRendererTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.ViewWordChangeAndDiffTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.WordEditTest;
import org.eclipse.osee.framework.ui.skynet.test.cases.WordTrackedChangesTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {RelationOrderRendererTest.class, InterArtifactDropTest.class, WordEditTest.class,
      WordTrackedChangesTest.class, PreviewAndMultiPreviewTest.class, ViewWordChangeAndDiffTest.class})
/**
 * @author Donald G. Dunne
 */
public class FrameworkUi_Demo_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Demo Application Server must be running.",
            ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }
}
