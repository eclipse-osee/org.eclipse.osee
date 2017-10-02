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
import java.util.Collections;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldUISearchItem extends WorldSearchItem {

   public WorldUISearchItem(String name) {
      this(name, LoadView.WorldEditor, null);
   }

   public WorldUISearchItem(String name, KeyedImage oseeImage) {
      this(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldUISearchItem(String name, LoadView loadView, KeyedImage oseeImage) {
      super(name, loadView, oseeImage);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem) {
      this(worldUISearchItem, null);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem, KeyedImage oseeImage) {
      super(worldUISearchItem, oseeImage);
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    */
   @Override
   public String getSelectedName(SearchType searchType)  {
      return getName();
   }

   public abstract Collection<Artifact> performSearch(SearchType searchType) ;

   public Collection<Artifact> performSearchGetResults()  {
      return performSearchGetResults(false, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi)  {
      return performSearchGetResults(performUi, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi, final SearchType searchType)  {
      cancelled = false;
      if (performUi) {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  performUI(searchType);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });

      }
      if (cancelled) {
         return Collections.emptySet();
      }
      return performSearch(searchType);
   }

   public void performUI(SearchType searchType)  {
      cancelled = false;
   }

}
