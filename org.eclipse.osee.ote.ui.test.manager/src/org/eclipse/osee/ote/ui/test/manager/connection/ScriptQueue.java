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
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.util.List;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;

public abstract class ScriptQueue implements Runnable {

   private List<ScriptTask> scripts;
   private TestManagerEditor testManager;

   public ScriptQueue(List<ScriptTask> scripts, TestManagerEditor testManager) {
      super();
      this.scripts = scripts;
      this.testManager = testManager;
   }

   public abstract void run();

   protected List<ScriptTask> getScriptsToExecute() {
      return scripts;
   }

   protected TestManagerEditor getTestManagerEditor() {
      return testManager;
   }

   protected ScriptManager getScriptManager() {
	return testManager.getPageManager().getScriptPage().getScriptManager();
    }
}
