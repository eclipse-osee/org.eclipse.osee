/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin;

import java.io.File;
import org.eclipse.osee.framework.server.admin.conversion.DataConversion;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Andrew M. Finkbeiner
 */
public class AdminConsole implements CommandProvider {

   public void _convert(CommandInterpreter ci) {
      DataConversion.getInstance().convert(ci);
   }

   public void _convertstop(CommandInterpreter ci) {
      DataConversion.getInstance().convertStop(ci);
   }

   public void _configini(CommandInterpreter ci) {
      //      if (args.length < 2) {
      //         throw new IllegalArgumentException("We need a destination and at least one content");
      //      }
      //      File destination = new File(args[0]);
      //      if (destination.exists()) {
      //         throw new IllegalArgumentException("configuration folder already exists");
      //      }
      //      destination.mkdirs();
      //      FileOutputStream fos = new FileOutputStream(new File(destination, "config.ini"));
      StringBuilder sb = new StringBuilder();
      sb.append("eclipse.ignoreApp=true\n");
      sb.append("osgi.bundles= \\\n");

      String arg = ci.nextArgument();

      //      for (int i = 1; i < ci.length; i++) {
      File folder = new File(arg);
      File[] files = folder.listFiles();
      for (File f : files) {
         if (!f.isDirectory()) {
            sb.append(f.toURI());
            sb.append("@start, \\\n");
         }
      }
      //      }
      //      int index = sb.lastIndexOf(", \\\n");
      //      sb = sb.replace(index, index + 4, "");
      System.out.println(sb.toString());
      //      fos.write(sb.toString().getBytes());
      //      fos.close();
      //      System.out.println("Wrote configuration to: " + destination);
      //      System.out.println("java -jar org.eclipse.osgi_3.4.0.v20080326.jar -console -configuration " + destination.getAbsolutePath());
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
