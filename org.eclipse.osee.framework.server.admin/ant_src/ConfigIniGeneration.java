/*******************************************************************************
 * Copyright (c) 2004, 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created on May 27, 2008
 * 
 * @author Andrew M. Finkbeiner
 */
public class ConfigIniGeneration {
   public static void main(String[] args) {
      StringBuilder builder = new StringBuilder();
      builder.append("osgi.bundles= \\\n");
      builder.append("org.apache.commons.httpclient_3.1.0.v20080605-1935.jar@start, \\\n");
      builder.append("org.apache.commons.codec_1.3.0.v20080530-1600.jar@start, \\\n");
      builder.append("org.eclipse.equinox.app_1.1.0.v20080421-2006.jar@start, \\\n");
      builder.append("org.eclipse.equinox.registry_3.4.0.v20080516-0950.jar@start, \\\n");
      builder.append("org.eclipse.equinox.common_3.4.0.v20080421-2006.jar@start, \\\n");
      builder.append("org.eclipse.equinox.log_1.1.0.v20080303.jar@start, \\\n");
      builder.append("org.eclipse.equinox.ds_1.0.0.v20080310.jar@start, \\\n");
      builder.append("org.mortbay.jetty_5.1.11.v200803061811.jar@start, \\\n");
      builder.append("org.eclipse.osgi.services_3.1.200.v20071203.jar@start, \\\n");
      builder.append("org.eclipse.equinox.util_1.0.0.v20080303.jar@start, \\\n");
      builder.append("org.eclipse.equinox.http.servlet_1.0.100.v20080201.jar@start, \\\n");
      builder.append("org.eclipse.equinox.http.jetty_1.0.100.v20080303.jar@start, \\\n");
      builder.append("org.apache.commons.logging_1.0.4.v200803061811.jar@start, \\\n");
      builder.append("javax.servlet_2.4.0.v200803061910.jar@start, \\\n");
      File pluginDir = new File(args[0] + "/osee_server_bundles/plugins");
      File[] files = pluginDir.listFiles();
      for (File file : files) {
         builder.append("plugins/");
         builder.append(file.getName());
         builder.append("@start, \\\n");
      }
      builder.delete(builder.length() - 4, builder.length() - 1);
      builder.append("eclipse.ignoreApp=true\n");

      try {
         FileOutputStream fos = new FileOutputStream(args[0] + "/osee_server_bundles/configuration/config.ini");
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
