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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.AttributeChangeWorker;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.IChangeWorker;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class ReplaceAttributeWithBaselineOperation extends AbstractOperation {

   private final Collection<Change> changes;

   public ReplaceAttributeWithBaselineOperation(Collection<Change> changes) {
      super("Replace Attribute With Baseline Operation", Activator.PLUGIN_ID);
      this.changes = changes;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!monitor.isCanceled() && Conditions.notNull(changes) && !changes.isEmpty()) {
         monitor.beginTask("Reverting attribute", changes.size());
         Set<Artifact> artifactHistory = new HashSet<>();

         Change firstChange = changes.iterator().next();
         SkynetTransaction transaction = TransactionManager.createTransaction(firstChange.getBranch(),
            ReplaceArtifactWithBaselineOperation.class.getSimpleName());

         for (Change change : changes) {
            monitor.subTask("Reverting: " + changes.toString());
            monitor.worked(1);
            Artifact artifact = ArtifactQuery.getArtifactFromId(change.getArtId(), change.getBranch());
            revertAttribute(artifact, change);
            artifactHistory.add(artifact);
            artifact.persist(transaction);
            monitor.done();
         }

         transaction.execute();

         for (Artifact artifact : artifactHistory) {
            artifact.reloadAttributesAndRelations();
         }

         artifactHistory.clear();

         monitor.done();
      }
   }

   private void revertAttribute(Artifact artifact, Change change) throws OseeStateException, OseeCoreException, SecurityException, IllegalArgumentException {
      Attribute<?> attribute = artifact.getAttributeById(change.getItemId().getId(), true);
      if (attribute != null && change.getItemId().getId().intValue() == attribute.getId()) {
         IChangeWorker worker = new AttributeChangeWorker(change, artifact);
         worker.revert();
      }
   }
}
