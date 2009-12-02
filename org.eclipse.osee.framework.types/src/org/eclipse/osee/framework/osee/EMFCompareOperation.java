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
package org.eclipse.osee.framework.osee;

import java.util.Calendar;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.internal.InternalTypesActivator;

/**
 * @author Roberto E. Escobar
 */
public class EMFCompareOperation extends AbstractOperation {

   private final EObject ancestor;
   private final EObject modified;
   private final ComparisonResourceSnapshot comparisonSnapshot;

   public EMFCompareOperation(EObject ancestor, EObject modified, ComparisonResourceSnapshot comparisonSnapshot) {
      super("Compare", InternalTypesActivator.PLUGIN_ID);
      this.ancestor = ancestor;
      this.modified = modified;
      this.comparisonSnapshot = comparisonSnapshot;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      //      URI uri = URI.createURI("http://org.eclipse/osee/types/oseetypecache");
      //      final ResourceSet resourceSet1 = new ResourceSetImpl();
      //      Resource resource1 = resourceSet1.createResource(uri);
      //      resource1.getContents().add(baseModel);
      //
      //      final ResourceSet resourceSet2 = new ResourceSetImpl();
      //      Resource resource2 = resourceSet2.createResource(uri);
      //      resource2.getContents().add(changedModel);

      final MatchModel match = MatchService.doMatch(ancestor, modified, ancestor, null);
      monitor.worked(calculateWork(0.40));

      final DiffModel diff = DiffService.doDiff(match, true);
      monitor.worked(calculateWork(0.40));

      comparisonSnapshot.setDate(Calendar.getInstance().getTime());
      comparisonSnapshot.setMatch(match);
      comparisonSnapshot.setDiff(diff);
      monitor.worked(calculateWork(0.20));
   }
}
