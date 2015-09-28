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

import java.rmi.server.ExportException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.framework.thread.OteThreadManager;

public class BaseCommandManager implements ICommandManager {

   private final ExecutorService commands;
   private final ExecutorService commandResponse;
   private final Map<ITestServerCommand, Future<ITestCommandResult>> cmdMap;

   public BaseCommandManager() {
      OteThreadManager threadManager = OteThreadManager.getInstance();
      commands = Executors.newSingleThreadExecutor(threadManager.createNewFactory("ote.command"));
      commandResponse = Executors.newSingleThreadExecutor(threadManager.createNewFactory("ote.command.response"));
      cmdMap = new ConcurrentHashMap<>();
   }

   @Override
   public ICommandHandle addCommand(ITestServerCommand cmd, TestEnvironment context) throws ExportException {
      OTEStatusBoard statusBoard = ServiceUtility.getService(OTEStatusBoard.class);
      OTESessionManager sessionManager = ServiceUtility.getService(OTESessionManager.class);
      
      Future<ITestCommandResult> result =
         commands.submit(new TestCallableWrapper(this, cmd, context,statusBoard, sessionManager));
      cmdMap.put(cmd, result);
      return cmd.createCommandHandle(result, context);
   }

   public void commandComplete(ITestServerCommand cmd, TestEnvironment context) {
      commandResponse.submit(new TestCommandComplete(context, cmd, cmdMap.get(cmd)));
   }

}
