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
package org.eclipse.osee.ats.client.integration.tests.ats.health;

import static org.junit.Assert.fail;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.health.ValidateAtsDatabase;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.support.test.util.TestUtil;

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
      Matcher m = Pattern.compile("Error:.*$").matcher(XResultDataUI.getReport(rd, "").getManipulatedHtml());
      while (m.find()) {
         fail(m.group());
      }

      TestUtil.severeLoggingEnd(monitorLog);
   }

}
