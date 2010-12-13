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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.net.URI;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.OperationReporter;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactExtractor implements IArtifactExtractor {

   private static final IArtifactExtractorDelegate NULL_DELEGATE = new NullDelegate();

   private IArtifactExtractorDelegate delegate = NULL_DELEGATE;

   protected AbstractArtifactExtractor() {
      // Protect Constructor
   }

   protected abstract void extractFromSource(OperationReporter reporter, URI source, RoughArtifactCollector collector) throws Exception;

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public boolean isDelegateRequired() {
      return false;
   }

   @Override
   public final void setDelegate(IArtifactExtractorDelegate delegate) {
      this.delegate = delegate != null ? delegate : NULL_DELEGATE;
   }

   @Override
   public final IArtifactExtractorDelegate getDelegate() {
      return delegate;
   }

   @Override
   public final boolean hasDelegate() {
      return delegate instanceof NullDelegate ? false : true;
   }

   private void checkDelegate() throws OseeCoreException {
      if (isDelegateRequired() && !hasDelegate()) {
         throw new OseeStateException("Delegate is required but is null delegate");
      }
   }

   @Override
   public final void process(OperationReporter reporter, URI source, RoughArtifactCollector collector) throws Exception {
      checkDelegate();

      delegate.initialize();
      try {
         extractFromSource(reporter, source, collector);
         connectParentChildRelations(collector);
         connectCollectorParent(collector);
      } finally {
         delegate.dispose();
      }
   }

   private void connectCollectorParent(RoughArtifactCollector collector) {
      RoughArtifact parent = collector.getParentRoughArtifact();
      if (parent != null) {
         for (RoughArtifact roughArtifact : collector.getRoughArtifacts()) {
            if (!roughArtifact.hasParent()) {
               parent.addChild(roughArtifact);
            }
         }
      }
   }

   private void connectParentChildRelations(RoughArtifactCollector collector) {
      for (RoughArtifact roughArtifact : collector.getRoughArtifacts()) {
         if (roughArtifact.hasHierarchicalRelation()) {
            connectParentChildRelationsFor(collector, roughArtifact);
         }
      }
   }

   private void connectParentChildRelationsFor(RoughArtifactCollector collector, RoughArtifact parent) {
      // find all children and then save them in order
      for (RoughArtifact otherRoughArtifact : collector.getRoughArtifacts()) {
         if (!parent.equals(otherRoughArtifact)) { // don't compare to self
            if (parent.isChild(otherRoughArtifact)) {
               parent.addChild(otherRoughArtifact);
            }
         }
      }
   }
}