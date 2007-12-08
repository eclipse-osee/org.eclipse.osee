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
package org.eclipse.osee.framework.jini.service.test.interfaces;

import java.rmi.RemoteException;
import java.util.List;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.jini.util.IRemotePrintTarget;

public interface INabitService extends IService {

   void runBashCommands(String[] cmds) throws RemoteException;

   String getServiceName() throws RemoteException;

   List<BuildTargetPair> getBuildTargetPairInfo() throws RemoteException;

   void connectToMachine(String username, String password, String ip, IRemotePrintTarget callback) throws RemoteException;
}
