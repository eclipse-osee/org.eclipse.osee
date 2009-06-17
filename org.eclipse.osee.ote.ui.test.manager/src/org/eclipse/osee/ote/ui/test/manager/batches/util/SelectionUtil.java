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
package org.eclipse.osee.ote.ui.test.manager.batches.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Roberto E. Escobar
 */
public class SelectionUtil {
   public static IProject findSelectedProject(ISelection selection) {
      IProject currentProject = null;
      if (selection != null) {
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object obj = ss.getFirstElement();
            if (obj instanceof IProject) {
               currentProject = (IProject) obj;
            }
         }
      }
      return currentProject;
   }

   public static IJavaProject findSelectedJavaProject(ISelection selection) {
      IJavaProject currentProject = null;
      if (selection != null) {
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object obj = ss.getFirstElement();
            if (obj instanceof IJavaProject) {
               currentProject = (IJavaProject) obj;
            }
         }
      }
      return currentProject;
   }

   public static String getStatusMessages(Exception e) {
      String msg = e.getMessage();
      if (e instanceof CoreException) {
         CoreException ce = (CoreException) e;
         IStatus status = ce.getStatus();
         IStatus[] children = status.getChildren();
         for (int i = 0; i < children.length; i++)
            msg += "\n" + children[i].getMessage();
         System.err.println(msg);
         ce.printStackTrace(System.err);
      }
      return msg;
   }
}
