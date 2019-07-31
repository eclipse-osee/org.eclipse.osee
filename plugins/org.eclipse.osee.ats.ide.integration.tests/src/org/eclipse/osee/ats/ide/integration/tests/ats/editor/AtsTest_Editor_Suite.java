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
package org.eclipse.osee.ats.ide.integration.tests.ats.editor;

import org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem.AtsTest_Demo_StateItem_Suite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsTest_Demo_StateItem_Suite.class,
   WfeEditorAddSupportingArtifactsTest.class,
   WfeEditorAddSupportingFilesTest.class,
   WfePrintTest.class})
public class AtsTest_Editor_Suite {
   // do nothing
}
