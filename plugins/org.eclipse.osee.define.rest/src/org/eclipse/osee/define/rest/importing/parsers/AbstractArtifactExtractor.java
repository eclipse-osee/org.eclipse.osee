/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import java.net.URI;
import org.eclipse.define.api.importing.IArtifactExtractor;
import org.eclipse.define.api.importing.IArtifactExtractorDelegate;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactExtractor implements IArtifactExtractor {

   private static final IArtifactExtractorDelegate NULL_DELEGATE = new NullDelegate();

   private IArtifactExtractorDelegate delegate = NULL_DELEGATE;

   protected AbstractArtifactExtractor() {
      // Protect Constructor
   }

   protected abstract void extractFromSource(OrcsApi orcsApi, ActivityLog activityLog, URI source, RoughArtifactCollector collector) throws Exception;

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

   private void checkDelegate() {
      if (isDelegateRequired() && !hasDelegate()) {
         throw new OseeStateException("Delegate is required but is null delegate");
      }
   }

   @Override
   public final void process(OrcsApi orcsApi, ActivityLog activityLog, URI source, RoughArtifactCollector collector) throws Exception {
      checkDelegate();

      delegate.initialize();
      try {
         extractFromSource(orcsApi, activityLog, source, collector);
         connectParentChildRelations(collector);
         connectCollectorParent(collector);
      } finally {
         delegate.dispose();
      }
   }

   /**********************************************************
    * Called after the rough artifact is converted to a real artifact
    *
    * @param theArtifact The artifact that has been created
    * @param source The rough artifact source for the created artifact
    * @return true if the artifact has been modified
    */

   @Override
   public boolean artifactCreated(TransactionBuilder transaction, ArtifactId theArtifact, RoughArtifact source) {
      return false;
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
