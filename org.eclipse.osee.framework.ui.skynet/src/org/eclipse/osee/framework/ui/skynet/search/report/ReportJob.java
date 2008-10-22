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
package org.eclipse.osee.framework.ui.skynet.search.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.search.ui.text.Match;

/**
 * @author Ryan D. Brooks
 */
public abstract class ReportJob extends Job {
   private IStructuredSelection selection;

   /**
    * @param name
    */
   public ReportJob(String name) {
      super(name);
   }

   @Override
   protected final IStatus run(IProgressMonitor monitor) {
      try {
         List<Artifact> artifacts;
         if (selection == null) {
            artifacts = new ArrayList<Artifact>(0);
         } else {
            artifacts = new ArrayList<Artifact>(selection.size());
            Iterator<?> iter = selection.iterator();
            while (iter.hasNext()) {
               Object obj = iter.next();
               if (obj instanceof Match && ((Match) obj).getElement() instanceof Artifact) artifacts.add((Artifact) ((Match) obj).getElement());
            }
         }
         generateReport(artifacts, monitor);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
      return Status.OK_STATUS;
   }

   /**
    * All exceptoins that are thrown will be gracefully handled by the GUI
    * 
    * @param selectedArtifacts
    * @param monitor
    */
   public abstract void generateReport(List<Artifact> selectedArtifacts, IProgressMonitor monitor) throws Exception;

   /**
    * @param selection
    */
   public final void setSelection(IStructuredSelection selection) {
      this.selection = selection;
   }

   public final IStructuredSelection getSelection() {
      return selection;
   }
}