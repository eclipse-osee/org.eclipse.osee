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
