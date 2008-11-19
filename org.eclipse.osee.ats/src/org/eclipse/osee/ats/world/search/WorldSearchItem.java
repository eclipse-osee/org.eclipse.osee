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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   protected static Set<Artifact> EMPTY_SET = new HashSet<Artifact>();
   protected boolean cancelled = false;
   private LoadView loadView;
   public static enum LoadView {
      TaskEditor, WorldEditor, None
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
      this(name, LoadView.WorldEditor);
   }

   public WorldSearchItem(String name, LoadView loadView) {
      super();
      this.name = name;
      this.loadView = loadView;
      this.cancelled = false;
   }

   public WorldSearchItem(WorldSearchItem worldSearchItem) {
      this.name = worldSearchItem.name;
      this.cancelled = worldSearchItem.cancelled;
      this.loadView = worldSearchItem.loadView;
   }

   public abstract WorldSearchItem copy();

   public String getName() throws OseeCoreException {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    * 
    * @param searchType TODO
    * @return selected name
    * @throws OseeCoreException
    */
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return getName();
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

   @Override
   public String toString() {
      try {
         return getName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
