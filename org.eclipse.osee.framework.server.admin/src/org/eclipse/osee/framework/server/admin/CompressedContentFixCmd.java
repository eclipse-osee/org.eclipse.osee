/*
 * Created on May 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin;

import java.io.File;
import org.eclipse.osee.framework.server.admin.conversion.CompressedContentFix;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class CompressedContentFixCmd implements CommandProvider {

   public void _native_content_fix(CommandInterpreter ci) {
      CompressedContentFix.getInstance().execute(ci);
   }

   public void _native_content_fix_stop(CommandInterpreter ci) {
      CompressedContentFix.getInstance().executeStop(ci);
   }

   public void _configini(CommandInterpreter ci) {
      StringBuilder sb = new StringBuilder();
      sb.append("eclipse.ignoreApp=true\n");
      sb.append("osgi.bundles= \\\n");

      String arg = ci.nextArgument();

      File folder = new File(arg);
      File[] files = folder.listFiles();
      for (File f : files) {
         if (!f.isDirectory()) {
            sb.append(f.toURI());
            sb.append("@start, \\\n");
         }
      }
      System.out.println(sb.toString());
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
      sb.append("        native_content_fix - converts some data\n");
      sb.append("        native_content_fix_stop - stop the conversion\n");
      return sb.toString();
   }

}
