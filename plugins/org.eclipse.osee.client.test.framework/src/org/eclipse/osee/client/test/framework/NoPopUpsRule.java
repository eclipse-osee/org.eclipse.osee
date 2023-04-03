/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Rule to prevent tests displaying dialog boxes that must be acknowledged by a user.
 *
 * @author Loren K. Ashley
 */

public class NoPopUpsRule implements TestRule {

   /**
    * Creates a new {@link TestRule} and sets the {@link #okToRun} flag if it has not already been set.
    */

   public NoPopUpsRule() {
   }

   @Override
   public Statement apply(Statement base, Description description) {
      //@formatter:off
      return
         new Statement() {

            @Override
            public void evaluate() throws Throwable {

               RenderingUtil.setPopupsAllowed( false );

               base.evaluate();

            }
         };
      //@formatter:on
   }

}

/* EOF */