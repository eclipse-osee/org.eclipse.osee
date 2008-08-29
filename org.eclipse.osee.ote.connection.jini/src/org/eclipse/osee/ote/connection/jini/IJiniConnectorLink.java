package org.eclipse.osee.ote.connection.jini;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IJiniConnectorLink extends Remote {
    boolean ping() throws RemoteException;
}
