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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.search.ui.text.RemoveAllEvent;

/**
 * @author Michael S. Rodgers
 */
public abstract class AbstractArtifactSearchResult implements ISearchResult {

   private ArrayList<Match> aMatches;
   private Map<Artifact, Match> artifacts;
   private ArrayList<ISearchResultListener> aListeners;
   private MatchEvent aMatchEvent;

   /**
    * Constructs a new <code>AbstractTextSearchResult</code>
    */
   protected AbstractArtifactSearchResult() {
      this.aMatches = new ArrayList<Match>();
      this.aListeners = new ArrayList<ISearchResultListener>();
      this.aMatchEvent = new MatchEvent(this);
      this.artifacts = new HashMap<Artifact, Match>();

   }

   /**
    * Returns an array with all matches reported against the given element.
    * 
    * @param element the element to report matches for
    * @return all matches reported for this element
    * @see Match#getElement()
    */
   public Match getMatch(Object element) {
      synchronized (aMatches) {
         return aMatches.get(aMatches.indexOf(element));
      }
   }

   /**
    * Adds a <code>Match</code> to this search result. This method does nothing if the match is already present.
    * <p>
    * Subclasses may extend this method.
    * </p>
    * 
    * @param match the match to add
    */
   public void addMatch(Match match) {
      boolean hasAdded = false;
      synchronized (aMatches) {
         hasAdded = doAddMatch(match);
      }
      if (hasAdded) fireChange(getSearchResultEvent(match, MatchEvent.ADDED));
   }

   /**
    * Adds a number of Matches to this search result. This method does nothing for matches that are already present.
    * <p>
    * Subclasses may extend this method.
    * </p>
    * 
    * @param matches the matches to add
    */
   public void addMatches(Match[] matches) {
      Collection<Match> reallyAdded = new ArrayList<Match>();
      synchronized (aMatches) {
         for (int i = 0; i < matches.length; i++) {
            if (doAddMatch(matches[i])) reallyAdded.add(matches[i]);
         }
      }
      if (!reallyAdded.isEmpty()) fireChange(getSearchResultEvent(reallyAdded, MatchEvent.ADDED));
   }

   private MatchEvent getSearchResultEvent(Match match, int eventKind) {
      return aMatchEvent;
   }

   private MatchEvent getSearchResultEvent(Collection<Match> matches, int eventKind) {
      return aMatchEvent;
   }

   private boolean doAddMatch(Match match) {
      aMatches.add(match);
      artifacts.put((Artifact) match.getElement(), match);
      return true;

   }

   /**
    * Removes all matches from this search result.
    * <p>
    * Subclasses may extend this method.
    * </p>
    */
   public void removeAll() {
      synchronized (aMatches) {
         doRemoveAll();
      }
      fireChange(new RemoveAllEvent(this));
   }

   private void doRemoveAll() {
      aMatches.clear();
   }

   /**
    * Removes the given match from this search result. This method has no effect if the match is not found.
    * <p>
    * Subclasses may extend this method.
    * </p>
    * 
    * @param match the match to remove
    */
   public void removeMatch(Match match) {
      boolean existed = false;
      synchronized (aMatches) {
         existed = doRemoveMatch(match);
      }
      if (existed) {
         artifacts.remove(match.getElement());
         fireChange(getSearchResultEvent(match, MatchEvent.REMOVED));
      }
   }

   /**
    * Removes the given matches from this search result. This method has no effect for matches that are not found
    * <p>
    * Subclasses may extend this method.
    * </p>
    * 
    * @param matches the matches to remove
    */
   public void removeMatches(Match[] matches) {
      Collection<Match> existing = new ArrayList<Match>();
      synchronized (aMatches) {
         for (int i = 0; i < matches.length; i++) {
            if (doRemoveMatch(matches[i])) existing.add(matches[i]); // no duplicate matches at this point
         }
      }
      if (!existing.isEmpty()) fireChange(getSearchResultEvent(existing, MatchEvent.REMOVED));
   }

   private boolean doRemoveMatch(Match match) {
      boolean existed = false;
      int matchIndex = aMatches.indexOf(match);

      if (matchIndex >= 0) {
         Match indexMatch = aMatches.get(matchIndex);
         if (indexMatch != null) {
            existed = aMatches.remove(match);
         }
      }
      return existed;
   }

   /**
    * {@inheritDoc}
    */
   public void addListener(ISearchResultListener l) {
      if (l == null) throw new IllegalArgumentException("Can not have a null listener");
      synchronized (aListeners) {
         aListeners.add(l);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void removeListener(ISearchResultListener l) {
      synchronized (aListeners) {
         aListeners.remove(l);
      }
   }

   /**
    * Send the given <code>SearchResultEvent</code> to all registered search result listeners.
    * 
    * @param e the event to be sent
    * @see ISearchResultListener
    */
   protected void fireChange(SearchResultEvent e) {
      ArrayList<ISearchResultListener> copiedListeners = new ArrayList<ISearchResultListener>();
      synchronized (aListeners) {
         copiedListeners.addAll(aListeners);
      }
      Iterator<?> listeners = copiedListeners.iterator();
      while (listeners.hasNext()) {
         ((ISearchResultListener) listeners.next()).searchResultChanged(e);
      }
   }

   /**
    * Returns the total number of matches contained in this search result.
    * 
    * @return total number of matches
    */
   public int getMatchCount() {
      return aMatches.size();
   }

   /**
    * Returns an array containing the set of all elements that matches are reported against in this search result.
    * 
    * @return the set of elements in this search result
    */
   public Object[] getElements() {
      synchronized (aMatches) {
         return aMatches.toArray();
      }
   }

   public List<Artifact> getArtifactResults() {
      synchronized (aMatches) {
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>(aMatches.size());
         for (Match match : aMatches) {
            artifacts.add((Artifact) match.getElement());
         }
         return artifacts;
      }
   }

   /**
    * Removes the children artifacts from the search
    * 
    * @param children
    */
   public void removeArtifacts(Collection<Artifact> children) {
      for (Artifact artifact : children) {
         Match match = artifacts.get(artifact);
         removeMatch(match);
         artifacts.remove(artifact);

         try {
            // remove all of its children
            removeArtifacts(artifact.getChildren());
         } catch (OseeCoreException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }
}
