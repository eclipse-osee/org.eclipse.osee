/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldUISearchItem extends WorldSearchItem {

   public WorldUISearchItem(String name) {
      this(name, LoadView.WorldEditor, null);
   }

   public WorldUISearchItem(String name, AtsImage atsImage) {
      this(name, LoadView.WorldEditor, atsImage);
   }

   public WorldUISearchItem(String name, LoadView loadView, AtsImage atsImage) {
      super(name, loadView, atsImage);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem) {
      this(worldUISearchItem, null);
   }

   public WorldUISearchItem(WorldUISearchItem worldUISearchItem, AtsImage atsImage) {
      super(worldUISearchItem, atsImage);
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    */
   @Override
   public String getSelectedName(SearchType searchType) {
      return getName();
   }

   public abstract Collection<Artifact> performSearch(SearchType searchType);

   public Collection<Artifact> performSearchGetResults() {
      return performSearchGetResults(false, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi) {
      return performSearchGetResults(performUi, SearchType.Search);
   }

   public Collection<Artifact> performSearchGetResults(boolean performUi, final SearchType searchType) {
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

   public void performUI(SearchType searchType) {
      cancelled = false;
   }

}
