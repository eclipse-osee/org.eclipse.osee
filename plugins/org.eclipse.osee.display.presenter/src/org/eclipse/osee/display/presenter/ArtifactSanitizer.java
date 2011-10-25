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
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John Misinco
 */
public class ArtifactSanitizer {

   protected final ArtifactProvider provider;

   public ArtifactSanitizer(ArtifactProvider provider) {
      this.provider = provider;
   }

   protected static final List<String> notAllowed = new ArrayList<String>();
   static {
      notAllowed.add("Technical Approaches");
      notAllowed.add("Technical Performance Parameters");
      notAllowed.add("Recent Imports");
      notAllowed.add("Test");
      notAllowed.add("Interface Requirements");
      notAllowed.add("Test Procedures");
   }

   public List<Match<ReadableArtifact, ReadableAttribute<?>>> sanitizeSearchResults(List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) {
      int numProcessors = Runtime.getRuntime().availableProcessors();
      int partitionSize = toSanitize.size() / numProcessors;
      int remainder = toSanitize.size() % numProcessors;
      ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
      int startIndex = 0;
      int endIndex = 0;
      List<ResultsCallable> workers = new LinkedList<ResultsCallable>();
      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new LinkedList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      for (int i = 0; i < numProcessors; i++) {
         startIndex = endIndex;
         endIndex = startIndex + partitionSize;
         if (i == 0) {
            endIndex += remainder;
         }
         ResultsCallable worker = new ResultsCallable(toSanitize.subList(startIndex, endIndex));
         workers.add(worker);
      }

      try {
         for (Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>> future : executor.invokeAll(workers)) {
            toReturn.addAll(future.get());
         }
      } catch (Exception ex) {
         //
      }

      return toReturn;

   }

   public ReadableArtifact sanitizeArtifact(ReadableArtifact result) throws OseeCoreException {
      boolean allowed = true;
      ReadableArtifact current = result;
      while (current != null) {
         if (notAllowed.contains(current.getName())) {
            allowed = false;
            break;
         }
         current = provider.getParent(current);
      }
      if (allowed) {
         return result;
      } else {
         return null;
      }
   }

   public List<ReadableArtifact> sanitizeArtifacts(List<ReadableArtifact> arts) throws OseeCoreException {
      Iterator<ReadableArtifact> it = arts.iterator();
      while (it.hasNext()) {
         if (sanitizeArtifact(it.next()) == null) {
            it.remove();
         }
      }
      return arts;
   }

   private class ResultsCallable implements Callable<List<Match<ReadableArtifact, ReadableAttribute<?>>>> {

      List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize;

      public ResultsCallable(List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) {
         this.toSanitize = toSanitize;
      }

      @Override
      public List<Match<ReadableArtifact, ReadableAttribute<?>>> call() throws Exception {
         Iterator<Match<ReadableArtifact, ReadableAttribute<?>>> it = toSanitize.iterator();
         while (it.hasNext()) {
            Match<ReadableArtifact, ReadableAttribute<?>> match = it.next();
            ReadableArtifact matchedArtifact = match.getItem();
            if (sanitizeArtifact(matchedArtifact) == null) {
               it.remove();
            }
         }
         return toSanitize;
      }
   }
}
