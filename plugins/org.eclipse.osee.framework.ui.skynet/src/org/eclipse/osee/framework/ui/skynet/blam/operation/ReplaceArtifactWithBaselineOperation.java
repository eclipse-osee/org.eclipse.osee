/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.IChangeWorker;
import org.eclipse.osee.framework.skynet.core.change.RelationChange;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class ReplaceArtifactWithBaselineOperation extends AbstractOperation {

   private final Collection<Change> changeReportChanges;
   private final Collection<Artifact> artifacts;

   public ReplaceArtifactWithBaselineOperation(Collection<Change> changeReportChanges, Collection<Artifact> artifacts) {
      super("Replace Artifact With Baseline Operation", Activator.PLUGIN_ID);
      this.changeReportChanges = changeReportChanges;
      this.artifacts = artifacts;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!monitor.isCanceled() && Conditions.notNull(changeReportChanges, artifacts)) {
         monitor.beginTask("Reverting artifact(s)", artifacts.size());
         if (!artifacts.isEmpty()) {
            Artifact firstArtifact = artifacts.iterator().next();
            SkynetTransaction transaction =
               TransactionManager.createTransaction(firstArtifact.getBranch(),
                  ReplaceArtifactWithBaselineOperation.class.getSimpleName());

            for (Artifact artifact : artifacts) {
               monitor.subTask("Reverting: " + artifact.getName());
               monitor.worked(1);
               Collection<Change> changes = getArtifactSpecificChanges(artifact.getArtId(), changeReportChanges);
               revertArtifact(artifact, changes);
               artifact.persist(transaction);
               monitor.done();
            }

            monitor.subTask(String.format("Persisting %s artifact(s)", artifacts.size()));
            transaction.execute();
            persistAndReloadArtifacts();

         }
         monitor.done();
      }
   }

   /**
    * Conditions of filter:
    * <ul>
    * <li>!ChangeItem.isSynthetic()</li>
    * <li>change is for the specific artifact</li>
    * <li>if change is of type Relation and is NOT on the A side but on B side of that relation</li>
    * </ul>
    * 
    * @return filtered changes based on the above conditions
    */
   private Collection<Change> getArtifactSpecificChanges(int artId, Collection<Change> changeReport) {
      Collection<Change> artifactChanges = new ArrayList<Change>(changeReport.size());
      for (Change change : changeReport) {
         if (!change.getChangeItem().isSynthetic()) {
            if (change.getArtId() == artId) {
               artifactChanges.add(change);
            } else if (change.getChangeType() == LoadChangeType.relation) {
               RelationChange relationChange = (RelationChange) change;
               if (relationChange.getBArtId() == artId) {
                  artifactChanges.add(relationChange);
               }
            }
         }
      }
      return artifactChanges;
   }

   private void persistAndReloadArtifacts() throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         artifact.reloadAttributesAndRelations();
      }
   }

   private void revertArtifact(Artifact artifact, Collection<Change> changes) throws OseeStateException, OseeCoreException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
	   for (Change change : changes) {
		   Class<? extends IChangeWorker> workerClass = change.getWorker();
		   Constructor<?> ctor = workerClass.getConstructor(Change.class, Artifact.class);
		   IChangeWorker worker = (IChangeWorker) ctor.newInstance(change, artifact);
		   worker.revert();
      }
   }
}
