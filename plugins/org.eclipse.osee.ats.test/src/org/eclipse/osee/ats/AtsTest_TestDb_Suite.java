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
package org.eclipse.osee.ats;

import org.eclipse.osee.ats.artifact.log.AtsLogTest;
import org.eclipse.osee.ats.artifact.log.LogItemTest;
import org.eclipse.osee.ats.artifact.note.AtsNoteTest;
import org.eclipse.osee.ats.artifact.note.NoteItemTest;
import org.eclipse.osee.ats.config.AtsBranchConfigurationTest;
import org.eclipse.osee.ats.util.AtsImageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   LogItemTest.class,
   AtsLogTest.class,
   NoteItemTest.class,
   AtsNoteTest.class,
   AtsImageTest.class,
   AtsBranchConfigurationTest.class})
/**
 * @author Donald G. Dunne
 */
public class AtsTest_TestDb_Suite {
   // test provided above
}
