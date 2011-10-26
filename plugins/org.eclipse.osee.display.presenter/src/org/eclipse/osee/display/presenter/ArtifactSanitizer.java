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
import java.util.concurrent.Future;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John Misinco
 */
public class ArtifactSanitizer {

   private static final String SANITIZER_EXECUTOR_ID = "artifact.sanitizer";

   protected final List<String> notAllowed = new ArrayList<String>();

   private final ExecutorAdmin executorAdmin;
   private final ArtifactProvider provider;

   public ArtifactSanitizer(ExecutorAdmin executorAdmin, ArtifactProvider provider) {
      this.executorAdmin = executorAdmin;
      this.provider = provider;

      notAllowed.add("Technical Approaches");
      notAllowed.add("Technical Performance Parameters");
      notAllowed.add("Recent Imports");
      notAllowed.add("Test");
      notAllowed.add("Interface Requirements");
      notAllowed.add("Test Procedures");
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
      return allowed ? result : null;
   }

   public List<ReadableArtifact> sanitizeArtifacts(List<ReadableArtifact> arts) throws OseeCoreException {
      Iterator<ReadableArtifact> it = arts.iterator();
      while (it.hasNext()) {
         ReadableArtifact nextArtifact = it.next();
         if (sanitizeArtifact(nextArtifact) == null) {
            it.remove();
         }
      }
      return arts;
   }

   public List<Match<ReadableArtifact, ReadableAttribute<?>>> filter(List<Match<ReadableArtifact, ReadableAttribute<?>>> toSanitize) throws Exception {
      ExecutorService executor = executorAdmin.getExecutor(SANITIZER_EXECUTOR_ID);

      int numProcessors = Runtime.getRuntime().availableProcessors();
      int partitionSize = toSanitize.size() / numProcessors;
      int remainder = toSanitize.size() % numProcessors;

      int startIndex = 0;
      int endIndex = 0;

      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new LinkedList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      List<Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>>> futures =
         new LinkedList<Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>>>();

      for (int index = 0; index < numProcessors; index++) {
         startIndex = endIndex;
         endIndex = startIndex + partitionSize;
         if (index == 0) {
            endIndex += remainder;
         }
         List<Match<ReadableArtifact, ReadableAttribute<?>>> partialList = toSanitize.subList(startIndex, endIndex);
         ResultsCallable worker = new ResultsCallable(partialList);
         Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>> future = executor.submit(worker);
         futures.add(future);
      }
      for (Future<List<Match<ReadableArtifact, ReadableAttribute<?>>>> future : futures) {
         toReturn.addAll(future.get());
      }
      return toReturn;
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
