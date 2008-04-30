/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin;

import org.eclipse.osee.framework.server.admin.conversion.DataConversion;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author b1528444
 */
public class AdminConsole implements CommandProvider {

   public void _convert(CommandInterpreter ci) {
      DataConversion.getInstance().convert(ci);
   }

   public void _convertstop(CommandInterpreter ci) {
      DataConversion.getInstance().convertStop(ci);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
    */
   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Server Admin Commands---\n");
      sb.append("        convert - converts some data\n");
      sb.append("        convertstop - stop the conversion\n");
      return sb.toString();
   }
}
