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

import java.util.HashSet;
import org.eclipse.osee.display.api.search.SearchProgressProvider;
import org.eclipse.osee.display.api.search.SearchProgressListener;

/**
 * @author Shawn F. Cook
 */
public class SearchProgressListenerImpl implements SearchProgressProvider {

   private final HashSet<SearchProgressListener> listeners = new HashSet<SearchProgressListener>();

   @Override
   public void addListener(SearchProgressListener listener) {
      listeners.add(listener);
   }

   @Override
   public void removeListener(SearchProgressListener listener) {
      listeners.remove(listener);
   }

   protected void fireSearchInProgressEvent() {
      for (SearchProgressListener listener : listeners) {
         listener.searchInProgress();
      }
   }

   protected void fireSearchCancelledEvent() {
      for (SearchProgressListener listener : listeners) {
         listener.searchCancelled();
      }
   }

   protected void fireSearchCompletedEvent() {
      for (SearchProgressListener listener : listeners) {
         listener.searchCompleted();
      }
   }
}
