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
package org.eclipse.osee.ote.ui.host.cmd;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;

/**
 * @author Roberto E. Escobar
 */
public class TreeBuilder {

   public static void buildTree(InputManager<TreeParent> inputManager, IHostTestEnvironment host, Map<ITestEnvironment, IRemoteCommandConsole> consoles) throws Exception {

      TreeParent treeParent = new TreeParent("Test Service");
      CategoryNode categoryNode = new CategoryNode("Consoles");
      treeParent.addChild(categoryNode);
      for (ITestEnvironment env : host.getRemoteEnvironments()) {
         Collection<OSEEPerson1_4> users = env.getUserList();
         categoryNode.addChild(new ConsoleNode(consoles.get(env), users.toArray(new OSEEPerson1_4[users.size()])));
      }
      inputManager.addNode(treeParent);
   }
}
