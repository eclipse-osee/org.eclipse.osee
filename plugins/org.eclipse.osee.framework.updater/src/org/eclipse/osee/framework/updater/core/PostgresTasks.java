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
package org.eclipse.osee.framework.updater.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Andrew M. Finkbeiner
 */
public class PostgresTasks {//TODO move this to the correct project after the big structure refactor

   private static File postgresBinDir = new File("C:\\postgres\\bin");

   public static void launchPostGresInstaller(File installerDir) throws IOException, InterruptedException {
      WindowsShell shell = new WindowsShell(installerDir);
      shell.cmd("msiexec /i postgresql-8.2-int.msi /qr INTERNALLAUNCH=1 ADDLOCAL=server,psql,pgadmin SERVICEDOMAIN=\"%COMPUTERNAME%\" CREATESERVICEUSER=1 SERVICEPASSWORD=\"nEv.NifBi/BzeuUPFfdcng|RDWY|vD\" SUPERPASSWORD=\"postgres\" BASEDIR=\"c:\\postgres\" /l*v postsqlOSEELog.log");
      shell.close();
   }

   public static void launchDbInit(File initFile) throws IOException, InterruptedException {
      WindowsShell shell = new WindowsShell(postgresBinDir);
      shell.cmd("psql.exe -a -e -f \"" + initFile.getAbsolutePath() + "\" -U postgres");
      shell.close();
   }

   public static void launchDbInit(String args, File initFile, String user) throws IOException, InterruptedException {
      WindowsShell shell = new WindowsShell(postgresBinDir);
      shell.cmd("psql.exe " + args + " \"" + initFile.getAbsolutePath() + "\" -U " + user);
      shell.close();
   }

   public static void launchDbRestore(File backupFile) throws IOException, InterruptedException {
      WindowsShell shell = new WindowsShell(postgresBinDir);
      shell.cmd("pg_restore.exe -i -h localhost -p 5432 -U postgres -d \"OSEE\" -v \"" + backupFile.getAbsolutePath() + "\"");
      shell.close();
   }

   public static void writePasswordFile() throws FileNotFoundException {
      File postgresqlFolder =
            new File(
                  System.getProperty("user.home") + File.separator + "Application Data" + File.separator + "postgresql");
      postgresqlFolder.mkdirs();
      File passWordFile = new File(postgresqlFolder, "pgpass.conf");
      PrintWriter wos = new PrintWriter(passWordFile);
      wos.println("localhost:5432:*:postgres:postgres");
      wos.flush();
      wos.close();
   }
}
