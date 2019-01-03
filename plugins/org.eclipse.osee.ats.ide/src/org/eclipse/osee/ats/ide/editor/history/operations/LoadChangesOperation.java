/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.history.operations;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.editor.history.column.EventColumn;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;

/**
 * @author Donald G. Dunne
 */
public final class LoadChangesOperation extends AbstractOperation {

   private final Collection<Change> changes;
   private final Artifact workflowArtifact;

   public LoadChangesOperation(Artifact workflowArtifact, Collection<Change> changes) {
      super("Load History Viewer - Changes", Activator.PLUGIN_ID);
      this.workflowArtifact = workflowArtifact;
      this.changes = changes;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         changes.clear();
         for (Change change : ChangeManager.getChangesPerArtifact(workflowArtifact, null)) {
            // Only show changes with event text
            if (Strings.isValid(EventColumn.getInstance().getColumnText(change, null, 0))) {
               changes.add(change);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error loading History View - See Error Log", ex);
      }
   }
}