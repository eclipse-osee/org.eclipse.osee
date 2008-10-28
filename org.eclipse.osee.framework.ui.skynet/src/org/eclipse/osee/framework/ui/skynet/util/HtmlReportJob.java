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

package org.eclipse.osee.framework.ui.skynet.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class HtmlReportJob extends Job {
   private final CoreRelationEnumeration side;
   private boolean recurseChildren;
   private final String title;
   private final Collection<Artifact> artifacts;
   private Collection<String> onlyAttributeNames;
   private IProgressMonitor monitor;

   /**
    * @throws TransformerFactoryConfigurationError
    * @throws IOException
    * @throws TransformerConfigurationException
    */
   public HtmlReportJob(String title, Collection<Artifact> artifacts, CoreRelationEnumeration side) throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
      super(title);
      this.title = title;
      this.artifacts = artifacts;
      this.side = side;
   }

   public HtmlReportJob(String title, Artifact artifact, CoreRelationEnumeration side) throws TransformerConfigurationException, IOException, TransformerFactoryConfigurationError {
      this(title, new ArrayList<Artifact>(), side);
      artifacts.add(artifact);
   }

   public IStatus run(IProgressMonitor monitor) {
      this.monitor = monitor;

      monitor.beginTask("", IProgressMonitor.UNKNOWN);
      try {
         final String html = generateHtml(title);

         if (!monitor.isCanceled()) {
            Display.getDefault().asyncExec(new Runnable() {
               public void run() {

                  IFile workingFile;
                  try {
                     workingFile =
                           FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW).getFile(
                                 GUID.generateGuidStr() + ".html");
                     AIFile.writeToFile(workingFile, html);
                     Program.launch(workingFile.getLocation().toString());
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  }
               }
            });
         }
      } catch (OseeCoreException ex) {
         return new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
      monitor.done();
      return Status.OK_STATUS;
   }

   public String generateHtml(String title) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title));
      sb.append(AHTML.beginSimpleTable(0, 100));
      int x = 1;
      for (Artifact artifact : artifacts) {
         monitor.setTaskName(String.format("Processing %s/%s", x + "", artifacts.size() + ""));
         sb.append(AHTML.addSimpleTableRow(processArtifact(artifact, "" + x++, side, recurseChildren,
               onlyAttributeNames)));
      }
      sb.append(AHTML.endSimpleTable());
      return AHTML.titledPage(title, sb.toString());
   }

   public String processArtifact(Artifact art, String paraNum, CoreRelationEnumeration side, boolean recurseChildren, Collection<String> onlyAttributeNames) throws OseeCoreException {
      if (monitor.isCanceled()) {
         return "";
      }

      monitor.subTask(art.getDescriptiveName());
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginSimpleTable(0, 100));
      sb.append(AHTML.addSimpleTableRow(String.format("%s    %s", paraNum + "", AHTML.bold(art.getDescriptiveName()))));
      sb.append(AHTML.addSimpleTableRow(processAttributes(art, recurseChildren, onlyAttributeNames)));
      int x = 1;
      if (recurseChildren) for (Artifact child : art.getRelatedArtifacts(side)) {
         sb.append(AHTML.addSimpleTableRow(processArtifact(child, paraNum + "." + x++, side, recurseChildren,
               onlyAttributeNames)));
      }
      sb.append(AHTML.endSimpleTable());
      return sb.toString();
   }

   public String processAttributes(Artifact artifact, boolean recurseChildren, Collection<String> onlyAttributeNames) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(90));
      sb.append(AHTML.addRowSpanMultiColumnTable(RendererManager.renderToHtml(artifact), 2));
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

   public boolean isRecurseChildren() {
      return recurseChildren;
   }

   public void setRecurseChildren(boolean recurseChildren) {
      this.recurseChildren = recurseChildren;
   }

   public Collection<String> getOnlyAttributeNames() {
      return onlyAttributeNames;
   }

   public void setOnlyAttributeNames(Collection<String> onlyAttributeNames) {
      this.onlyAttributeNames = onlyAttributeNames;
   }

}
