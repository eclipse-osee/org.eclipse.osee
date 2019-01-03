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
package org.eclipse.osee.ats.ide.integration.tests.ats.health;

import static org.junit.Assert.fail;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.support.test.util.TestUtil;
import org.eclipse.swt.program.Program;

/**
 * This test runs the validate ats database check against whatever database is run against.
 *
 * @author Donald G. Dunne
 */
public class AtsValidateAtsDatabaseTest {

   @org.junit.Test
   public void testValidateAtsDatabase() throws Exception {
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      ValidateAtsDatabase validateAtsDatabase = new ValidateAtsDatabase(null);
      XResultData rd = new XResultData();
      validateAtsDatabase.setFixAttributeValues(false);
      validateAtsDatabase.runIt(null, rd);
      String html = XResultDataUI.getReport(rd, "").getManipulatedHtml();
      Matcher m = Pattern.compile("Error:.*$").matcher(html);
      while (m.find()) {
         File file = OseeData.writeToFile("ValidateAtsDatabaseTest.html", html);
         Program.launch(file.getAbsolutePath());
         fail("Note: Failure html opened in browser.  " + m.group());
      }

      TestUtil.severeLoggingEnd(monitorLog);
   }

}
