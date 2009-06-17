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
package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.TestException;

/**
 * @author Andrew M. Finkbeiner
 */
public class BasicEnv extends OseeTestThread {

   /**
    * @param name
    * @param env
    */
   public BasicEnv(String name, TestEnvironment env) {
      super(name, env);
   }

   /* (non-Javadoc)
    * @see osee.test.core.OseeTestThread#run()
    */
   protected void run() throws TestException {
      getEnvironment().run();         
   }

}
