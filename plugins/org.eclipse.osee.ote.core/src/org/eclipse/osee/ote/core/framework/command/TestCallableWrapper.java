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
package org.eclipse.osee.ote.core.framework.command;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;

public class TestCallableWrapper implements Callable<ITestCommandResult> {

	private final ITestServerCommand cmd;
	private final TestEnvironment context;
	private final BaseCommandManager cmdManager;
	private final OTEStatusBoard statusBoard;
	
	public TestCallableWrapper( BaseCommandManager cmdManager, ITestServerCommand cmd, TestEnvironment context, OTEStatusBoard statusBoard) {
		this.cmd = cmd;
		this.context = context;
		this.cmdManager = cmdManager;
		this.statusBoard = statusBoard;
	}

	public ITestCommandResult call() throws Exception {
		ITestCommandResult result;
		try{
			context.setActiveUser(cmd.getUserSessionKey());
			result = cmd.execute(context, statusBoard);
			if(result.getThrowable() != null){
	         OseeLog.log(TestCallableWrapper.class, Level.SEVERE, result.getThrowable());
	      }
		} catch(Throwable ex){
			result = new TestCommandResult(TestCommandStatus.FAIL, ex);
			OseeLog.log(TestCallableWrapper.class, Level.SEVERE, ex);
		} finally {
			cmdManager.commandComplete(cmd, context);
		}
		return result;
	}

}
