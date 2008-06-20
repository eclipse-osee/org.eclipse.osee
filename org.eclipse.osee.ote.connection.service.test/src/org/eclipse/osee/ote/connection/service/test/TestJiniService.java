/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.connection.service.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author b1529404
 */
public class TestJiniService implements Remote {

   public void doSomething() throws RemoteException {
      System.out.println("doing something...");
   }
}
