/*
 * Created on Oct 10, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.server;

import java.rmi.RemoteException;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;

/**
 * @author Ken J. Aguilar
 */
public class RemoteShell extends ConsoleShell implements IRemoteCommandConsole{

   private static final long serialVersionUID = -4931966494670170915L;
   private final StringBuffer buffer = new StringBuffer(32000);
   
   public synchronized String doCommand(String line) throws RemoteException {
      buffer.setLength(0);
      buffer.append('>').append(line).append('\n');
      try {
         parseAndExecuteCmd(line);
      } catch (Throwable t) {
         printStackTrace(t);
      }
      return buffer.toString();
   }

   public RemoteShell(ICommandManager manager) {
      super(manager);
   }
   
   public void println(String string) {
      buffer.append(string).append('\n');
   }

   public void print(String string) {
      buffer.append(string);
   }

   public void println() {
      buffer.append('\n');
   }
}
