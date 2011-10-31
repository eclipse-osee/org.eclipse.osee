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
package org.eclipse.osee.display.presenter.internal;

import java.util.Collection;
import org.eclipse.osee.display.presenter.ArtifactFilter;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Collections.Filter;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class FilteredMatchCallable extends CancellableCallable<Collection<Match<ReadableArtifact, ReadableAttribute<?>>>> implements Filter<Match<ReadableArtifact, ReadableAttribute<?>>> {

   private final Collection<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize;
   private final ArtifactFilter sanitizer;

   public FilteredMatchCallable(ArtifactFilter sanitizer, Collection<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) {
      this.sanitizer = sanitizer;
      this.toSanitize = toSanitize;
   }

   @Override
   public Collection<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
      Collections.filter(toSanitize, this);
      return toSanitize;
   }

   @Override
   public boolean accept(Match<ReadableArtifact, ReadableAttribute<?>> item) throws Exception {
      checkForCancelled();
      return sanitizer.accept(item.getItem());
   }
}