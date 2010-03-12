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
package org.eclipse.osee.framework.jini.service.directory.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.directory.DirectoryPerson;

public interface IDirectory extends Remote {

   /**
    * @param bemsid
    * @return person object
    * @throws RemoteException
    */
   public DirectoryPerson getPerson(int bemsid) throws RemoteException;

   /**
    * @param dept
    * @return arraylist of people
    * @throws RemoteException
    */
   public ArrayList<String> getGroup(String[] dept) throws RemoteException;

}