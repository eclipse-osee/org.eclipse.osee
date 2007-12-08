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
package org.eclipse.osee.framework.ui.skynet.search.page.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchComposite;
import org.eclipse.osee.framework.ui.skynet.search.page.data.RelationTypeNode;
import org.eclipse.swt.widgets.Display;

public class GetArtifactTypesFromRelationJob extends Job {

   private ArtifactSearchComposite composite;
   private RelationTypeNode node;
   private boolean isChecked;

   public GetArtifactTypesFromRelationJob(String title, ArtifactSearchComposite composite, RelationTypeNode node, boolean isChecked) {
      super(title);
      this.composite = composite;
      this.node = node;
      this.isChecked = isChecked;
   }

   public static void scheduleJob(Job job) {
      job.setUser(true);
      job.setPriority(Job.SHORT);
      job.schedule();
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      if (node.getChildArtifactTypeNodes().size() == 0) {
         node.populateChildArtifactTypeNodes();
      }
      node.manageRelationDisplay(isChecked);
      Display.getCurrent().asyncExec(new Runnable() {
         public void run() {
            composite.getTreeWidget().getInputManager().inputChanged();
         }
      });
      return Status.OK_STATUS;
   }
}
