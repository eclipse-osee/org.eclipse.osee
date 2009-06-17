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
package org.eclipse.osee.ote.core.environment.interfaces;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;
import org.eclipse.osee.ote.core.environment.command.TestEnvironmentCommand;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;

public interface ITestEnvironmentListener {
	void onEnvironmentKilled(TestEnvironment env);
	void onCommandAdded(TestEnvironment env, TestEnvironmentCommand cmd);
	void onCommandBegan(TestEnvironment env, CommandDescription cmdDesc);
	void onCommandFinished(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status);
	void onTestServerCommandFinished(TestEnvironment env, ICommandHandle handle);
	void onCommandRemoved(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status);
	void onException(String message, Throwable t);
}
