import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created on May 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

/**
 * @author b1528444
 */
public class ConfigIniGeneration {
   public static void main(String[] args) {
      StringBuilder builder = new StringBuilder();
      builder.append("osgi.bundles= \\\n");
      builder.append("org.eclipse.equinox.log_1.1.0.v20080303.jar@start, \\\n");
      builder.append("org.eclipse.equinox.ds_1.0.0.v20080310.jar@start, \\\n");
      builder.append("org.mortbay.jetty_5.1.11.v200803061811.jar@start, \\\n");
      builder.append("org.eclipse.osgi.services_3.1.200.v20071203.jar@start, \\\n");
      builder.append("org.eclipse.equinox.util_1.0.0.v20080303.jar@start, \\\n");
      builder.append("org.eclipse.equinox.http.servlet_1.0.100.v20080201.jar@start, \\\n");
      builder.append("org.eclipse.equinox.http.jetty_1.0.100.v20080303.jar@start, \\\n");
      builder.append("org.apache.commons.logging_1.0.4.v200803061811.jar@start, \\\n");
      builder.append("javax.servlet_2.4.0.v200803061910.jar@start, \\\n");
      File pluginDir = new File(args[0] + "/osee_servers_bundles/plugins");
      File[] files = pluginDir.listFiles();
      for (File file : files) {
         builder.append("plugins/");
         builder.append(file.getName());
         builder.append("@start, \\\n");
      }
      builder.delete(builder.length() - 4, builder.length() - 1);
      builder.append("eclipse.ignoreApp=true\n");

      try {
         FileOutputStream fos = new FileOutputStream(args[0] + "/osee_servers_bundles/configuration/config.ini");
         System.out.println(new File("config.ini").getAbsolutePath());
         fos.write(builder.toString().getBytes());
         fos.flush();
         fos.close();
      } catch (FileNotFoundException ex) {
         ex.printStackTrace();
      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }
}
