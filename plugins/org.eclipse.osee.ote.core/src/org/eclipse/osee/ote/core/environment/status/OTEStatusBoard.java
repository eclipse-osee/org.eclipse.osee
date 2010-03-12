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
package org.eclipse.osee.ote.core.environment.status;

import java.util.List;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;
import org.eclipse.osee.ote.core.environment.command.TestEnvironmentCommand;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;

/**
 * @author Andrew M. Finkbeiner
 */
public interface OTEStatusBoard {
   public void addStatusListener(IServiceStatusListener listener);

   public void onCommandAdded(TestEnvironment env, TestEnvironmentCommand cmd);

   public void onCommandRemoved(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status);

   public void onException(String message, Throwable t);

   public void removeStatusListener(IServiceStatusListener listener);

   public void onCommandBegan(TestEnvironment env, CommandDescription cmdDesc);

   public void onCommandFinished(TestEnvironment env, CommandDescription cmdDesc, CommandEndedStatusEnum status);

   public void onTestPointUpdate(int pass, int fail, String testClassName);

   public void onEnvironmentKilled(TestEnvironment env);

   public void dispose();

   public void onTestServerCommandFinished(TestEnvironment env, ICommandHandle handle);

   public void onTestComplete(String className, String serverOutfilePath, String clientOutfilePath, CommandEndedStatusEnum status, List<IHealthStatus> healthStatus);

   public void onTestStart(String className);
}
