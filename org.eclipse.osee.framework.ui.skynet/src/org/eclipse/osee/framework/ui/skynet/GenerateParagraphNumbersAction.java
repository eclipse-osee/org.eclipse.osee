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

package org.eclipse.osee.framework.ui.skynet;

import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author Ryan D. Brooks
 */
public class GenerateParagraphNumbersAction extends Action {
   private final TreeViewer treeViewer;

   public GenerateParagraphNumbersAction(TreeViewer treeViewer) {
      super("Generate Paragraph Numbers");
      this.treeViewer = treeViewer;
   }

   public void run() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      final Artifact selectedArtifact = (Artifact) selection.getFirstElement();

      Jobs.startJob(new Job("Generate Paragraph Numbers for " + selectedArtifact.getDescriptiveName()) {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            IStatus toReturn = Status.CANCEL_STATUS;
            // Give it a stubbed appendable since we just want paragraph numbers out of it
            // ;o)
            final WordMLProducer producer = new WordMLProducer(new Appendable() {

               public Appendable append(CharSequence csq) throws IOException {
                  return this;
               }

               public Appendable append(char c) throws IOException {
                  return this;
               }

               public Appendable append(CharSequence csq, int start, int end) throws IOException {
                  return this;
               }
            });

            AbstractSkynetTxTemplate artifactTx = new AbstractSkynetTxTemplate(selectedArtifact.getBranch()) {

               @Override
               protected void handleTxWork() throws OseeCoreException, SQLException {
                  try {
                     for (Artifact artifact : selectedArtifact.getChildren()) {
                        if (monitor.isCanceled()) {
                           throw new IllegalStateException("USER CANCELLED");
                        }
                        setParagraphNumber(producer, artifact, monitor);
                     }
                  } catch (Exception ex) {
                     throw new OseeCoreException(ex);
                  }
               }

            };

            monitor.beginTask("Rebuilding Paragraph Numbers", IProgressMonitor.UNKNOWN);
            try {
               artifactTx.execute();
               toReturn = Status.OK_STATUS;
            } catch (Exception ex) {
               if (ex.getMessage().contains("USER CANCELLED")) {
                  toReturn = Status.CANCEL_STATUS;
               } else {
                  toReturn =
                        new Status(Status.ERROR, SkynetGuiPlugin.PLUGIN_ID, Status.OK, ex.getLocalizedMessage(), ex);
               }
            } finally {
               monitor.done();
            }

            return toReturn;
         }

         private void setParagraphNumber(WordMLProducer producer, Artifact artifact, IProgressMonitor monitor) throws IOException, SQLException, MultipleAttributesExist {
            String paragraphNumber = producer.startOutlineSubSection("", "", null).toString();

            monitor.subTask(paragraphNumber + " " + artifact.getDescriptiveName());
            artifact.setSoleAttributeValue("Imported Paragraph Number", paragraphNumber);
            artifact.persistAttributes();
            monitor.worked(1);

            for (Artifact child : artifact.getChildren()) {
               if (monitor.isCanceled()) {
                  return;
               }
               setParagraphNumber(producer, child, monitor);
            }
            producer.endOutlineSubSection();
         }
      });
   }
}
