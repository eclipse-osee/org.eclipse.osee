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
package org.eclipse.osee.ote.ui.testclassserver;

import java.io.File;
import java.net.BindException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osee.framework.core.client.CorePreferences;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.server.ClassServer;
import org.eclipse.osee.framework.plugin.core.server.PathResourceFinder;
import org.eclipse.osee.framework.ui.plugin.util.AJavaProject;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.ote.runtimemanager.UserLibResourceFinder;
import org.eclipse.osee.ote.ui.TestCoreGuiPlugin;

public class ClassServerInst {
   private ClassServer classServer;
   private String classServerPath;
   private PathResourceFinder pathResourceFinder;

   private static ClassServerInst instance = null;

   public static ClassServerInst getInstance() {
      if (instance == null) {
         instance = new ClassServerInst();
      }
      return instance;
   }

   /**
    * Creates a new ClassServer which will serve all projects currently in the workspace
    * 
    * @param testManager
    */
   private ClassServerInst() {
      try {
         InetAddress useHostAddress = CorePreferences.getDefaultInetAddress();
         classServer = new ClassServer(0, InetAddress.getLocalHost())//;
         {
            @Override
            protected void fileDownloaded(String fp, InetAddress addr) {
               System.out.println("ClassServerInst: File " + fp + " downloaded to " + addr);
            }
         };
         pathResourceFinder = new PathResourceFinder(new String[] {}, false);
         classServer.addResourceFinder(new UserLibResourceFinder());
         classServer.addResourceFinder(pathResourceFinder);
         classServer.start();

         classServerPath = "http://" + useHostAddress.getHostAddress() + ":" + classServer.getPort() + "/";

         Job job = new Job("Populating TM classserver with projects.") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  addAnyNewProjects();
               } catch (Throwable th) {
                  OseeLog.log(TestCoreGuiPlugin.class, Level.SEVERE, th.getMessage(), th);
               }
               return Status.OK_STATUS;
            }

         };
         job.schedule();
      } catch (BindException ex) {
         OseeLog.log(
               TestCoreGuiPlugin.class,
               Level.SEVERE,
               "Class Server not started.  Likely the IP address used is not local.  Set your IP address in the advanced page.",
               ex);
      } catch (Exception ex) {
         OseeLog.log(TestCoreGuiPlugin.class, Level.SEVERE, "Class Server not started.", ex);
      }
   }
   
   /**
    * Adds any newly created or checked out projects in the workspace to the ClassServer.
    */
   public void addAnyNewProjects() {
      // the ClassServer maintains a list and checks that any passed in projects are not already in
      // its list before adding new ones, so it is safe to simply pass the entire list of projects
      pathResourceFinder.addPaths(getAllProjects());
   }

   /**
    * @return the path to the class server, to be passed to the environment upon connection
    */
   public String getClassServerPath() {
      return classServerPath;
   }

   /**
    * Stops the class server. This should be called upon termination of the testManager
    */
   public void stopServer() {
      classServer.terminate();
   }

   private String[] getAllProjects() {
      ArrayList<String> list = new ArrayList<String>();

      IProject[] projects = AWorkspace.getProjects();
      for (IProject project : projects) {
         // If the project start with a '.', (i.e. a hidden project) do not include it in the class
         // server
         // This will keep .osee.data and others from being served
         if (!project.isOpen()) continue;

         IProjectDescription description;
         try {
            description = project.getDescription();
            if (!project.getName().startsWith(".") && description.hasNature("org.eclipse.jdt.core.javanature")) {
               List<File> fileList = AJavaProject.getJavaProjectProjectDependancies(JavaCore.create(project));
               for (File file : fileList)
                  list.add(file.getAbsolutePath());
            }
         } catch (CoreException ex) {
            ex.printStackTrace();
         }
      }

      return list.toArray(new String[list.size()]);
   }
}
