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
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactExtractor implements IArtifactExtractor {

   private IArtifactExtractorDelegate delegate;

   protected AbstractArtifactExtractor() {
      this.delegate = null;
   }

   protected abstract void extractFromSource(URI source, RoughArtifactCollector collector) throws Exception;

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public boolean isDelegateRequired() {
      return false;
   }

   @Override
   public void setDelegate(IArtifactExtractorDelegate delegate) {
      this.delegate = delegate;
   }

   @Override
   public IArtifactExtractorDelegate getDelegate() {
      return delegate;
   }

   @Override
   public boolean hasDelegate() {
      return getDelegate() != null;
   }

   private void checkDelegate() throws OseeCoreException {
      if (isDelegateRequired() && !hasDelegate()) {
         throw new OseeStateException("Delegate is required but is null");
      }
   }

   public final void process(URI source, RoughArtifactCollector collector) throws Exception {
      checkDelegate();
      if (hasDelegate()) {
         getDelegate().initialize();
      }
      try {
         extractFromSource(source, collector);
         connectParentChildRelations(collector);
         connectCollectorParent(collector);
      } finally {
         if (hasDelegate()) {
            getDelegate().dispose();
         }
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