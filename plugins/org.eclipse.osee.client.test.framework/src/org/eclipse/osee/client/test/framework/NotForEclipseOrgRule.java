/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.client.test.framework;

import java.util.Objects;
import java.util.regex.Pattern;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Rule to prevent tests from running on the Eclipse servers. Currently long running tests are failing due to a database
 * disconnection issue.
 * <p>
 * ToDo: Remove this class upon completion of TW22325.
 *
 * @author Loren K. Ashley
 */

public class NotForEclipseOrgRule implements TestRule {

   /**
    * Flag indicates if tests are OK to be run.
    */

   private static Boolean okToRun = null;

   /**
    * Creates a new {@link TestRule} and sets the {@link #okToRun} flag if it has not already been set.
    */

   public NotForEclipseOrgRule() {
      NotForEclipseOrgRule.setOkToRun();
   }

   @Override
   public Statement apply(Statement base, Description description) {
      //@formatter:off
      return
         new Statement() {

            @Override
            public void evaluate() throws Throwable {

               if( NotForEclipseOrgRule.okToRun ) {
                  base.evaluate();
               }

            }
         };
      //@formatter:on
   }

   /**
    * When the {@link NotForEclipseOrgRule#okToRun} has not been set, the flag will be set <code>true</code> when the
    * property "os.name" contains "windows"; otherwise, the flag will be set <code>false</code>.
    */

   private synchronized static void setOkToRun() {
      if (Objects.isNull(NotForEclipseOrgRule.okToRun)) {
         var osName = System.getProperty("os.name");
         var pattern = Pattern.compile("(?i:windows)");
         var matcher = pattern.matcher(osName);
         NotForEclipseOrgRule.okToRun = matcher.find();
      }
   }
}

/* EOF */