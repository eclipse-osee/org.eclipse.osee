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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

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
   public static enum ShowActionType {
      ShowAction, ShowTeamWorkflow
   }

   public static enum RecurseType {
      RecurseChildren, None
   }

   public static enum ShowFinishedType {
      ShowAll, ShowFinished
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

   public abstract Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException, SQLException;

   public Collection<Artifact> performReSearch() throws SQLException, IllegalArgumentException {
      return EMPTY_SET;
   }

   public Collection<Artifact> performSearchGetResults() throws OseeCoreException, SQLException {
      return performSearchGetResults(false, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException, SQLException {
      return performSearchGetResults(false, searchType);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi) throws OseeCoreException, SQLException {
      return performSearchGetResults(performUi, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi, final SearchType searchType) throws OseeCoreException, SQLException {
      cancelled = false;
      if (performUi) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            public void run() {
               try {
                  performUI(searchType);
               } catch (Exception ex) {
                  OSEELog.logException(AtsPlugin.class, ex, true);
               }
            }
         }, true);

      }
      if (cancelled) return EMPTY_SET;
      return performSearch(searchType);
   }

   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
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
