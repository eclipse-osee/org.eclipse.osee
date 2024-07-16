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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   protected static final Set<Artifact> EMPTY_SET = new HashSet<>();
   protected boolean cancelled = false;
   private LoadView loadView;
   protected final AtsImage atsImage;
   public static enum LoadView {
      TaskEditor,
      WorldEditor,
      None
   }
   public static enum SearchType {
      Search,
      ReSearch
   };

   public WorldSearchItem(String name, LoadView loadView, boolean cancelled, AtsImage atsImage) {
      super();
      this.name = name;
      this.loadView = loadView;
      this.cancelled = cancelled;
      this.atsImage = atsImage;
   }

   public WorldSearchItem(String name, LoadView loadView, AtsImage atsImage) {
      this(name, loadView, false, atsImage);
   }

   public WorldSearchItem(WorldSearchItem worldSearchItem, AtsImage atsImage) {
      this(worldSearchItem.name, worldSearchItem.loadView, worldSearchItem.cancelled, atsImage);
   }

   public abstract WorldSearchItem copy();

   public String getName() {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    */
   public String getSelectedName(SearchType searchType) {
      return getName();
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public LoadView getLoadView() {
      return loadView;
   }

   public void setLoadView(LoadView loadView) {
      this.loadView = loadView;
   }

   @Override
   public String toString() {
      try {
         return getName();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public Image getImage() {
      return atsImage == null ? null : ImageManager.getImage(atsImage);
   }

   public AtsImage getAtsImage() {
      return atsImage;
   }

}
