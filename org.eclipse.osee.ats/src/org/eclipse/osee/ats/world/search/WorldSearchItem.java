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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   protected static Set<Artifact> EMPTY_SET = new HashSet<Artifact>();
   protected boolean cancelled = false;
   private LoadView loadView;
   public static enum LoadView {
      WorldView, TaskEditor, None
   }
   public static enum SearchType {
      Search, ReSearch
   };
   public static enum ShowType {
      ShowAction, ShowTeamWorkflow
   }
   public static enum RecurseType {
      None, RecurseChildren
   }

   public WorldSearchItem(String name) {
      this(name, LoadView.WorldView);
   }

   public WorldSearchItem(String name, LoadView loadView) {
      super();
      this.name = name;
      this.loadView = loadView;
      this.cancelled = false;
   }

   public String getName() {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    * 
    * @param searchType TODO
    * @return selected name
    */
   public String getSelectedName(SearchType searchType) {
      return getName();
   }

   public abstract Collection<Artifact> performSearch(SearchType searchType) throws Exception;

   public Collection<Artifact> performReSearch() throws SQLException, IllegalArgumentException {
      return EMPTY_SET;
   }

   public Collection<Artifact> performSearchGetResults() throws Exception {
      return performSearchGetResults(false, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(SearchType searchType) throws Exception {
      return performSearchGetResults(false, searchType);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi) throws Exception {
      return performSearchGetResults(performUi, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi, final SearchType searchType) throws Exception {
      cancelled = false;
      if (performUi) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
               performUI(searchType);
            }
         }, true);

      }
      if (cancelled) return EMPTY_SET;
      return performSearch(searchType);
   }

   public void performUI(SearchType searchType) {
      cancelled = false;
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   /**
    * @return the loadView
    */
   public LoadView getLoadView() {
      return loadView;
   }

   /**
    * @param loadView the loadView to set
    */
   public void setLoadView(LoadView loadView) {
      this.loadView = loadView;
   }

   public String toString() {
      return getName();
   }
}
