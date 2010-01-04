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

import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldUISearchItem extends WorldSearchItem {

   public WorldUISearchItem(String name) {
      this(name, LoadView.WorldEditor, null);
   }

   public WorldUISearchItem(String name, OseeImage oseeImage) {
      this(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldUISearchItem(String name, LoadView loadView) {
      this(name, loadView, null);
   }

   public WorldUISearchItem(String name, LoadView loadView, OseeImage oseeImage) {
      super(name, loadView, oseeImage);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem) {
      this(worldUISearchItem, null);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem, OseeImage oseeImage) {
      super(worldUISearchItem, oseeImage);
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    */
   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return getName();
   }

   public abstract Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException;

   public Collection<Artifact> performReSearch() {
      return EMPTY_SET;
   }

   public Collection<Artifact> performSearchGetResults() throws OseeCoreException {
      return performSearchGetResults(false, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      return performSearchGetResults(false, searchType);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi) throws OseeCoreException {
      return performSearchGetResults(performUi, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi, final SearchType searchType) throws OseeCoreException {
      cancelled = false;
      if (performUi) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               try {
                  performUI(searchType);
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }, true);

      }
      if (cancelled) {
         return EMPTY_SET;
      }
      return performSearch(searchType);
   }

   public void performUI(SearchType searchType) throws OseeCoreException {
      cancelled = false;
   }

}
