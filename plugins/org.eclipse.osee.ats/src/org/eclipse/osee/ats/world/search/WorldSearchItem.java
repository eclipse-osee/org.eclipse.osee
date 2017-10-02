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
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   protected static final Set<Artifact> EMPTY_SET = new HashSet<>();
   protected boolean cancelled = false;
   private LoadView loadView;
   private final KeyedImage oseeImage;
   public static enum LoadView {
      TaskEditor,
      WorldEditor,
      None
   }
   public static enum SearchType {
      Search,
      ReSearch
   };

   public WorldSearchItem(String name, LoadView loadView, boolean cancelled, KeyedImage oseeImage) {
      super();
      this.name = name;
      this.loadView = loadView;
      this.cancelled = cancelled;
      this.oseeImage = oseeImage;
   }

   public WorldSearchItem(String name, LoadView loadView, KeyedImage oseeImage) {
      this(name, loadView, false, oseeImage);
   }

   public WorldSearchItem(WorldSearchItem worldSearchItem, KeyedImage oseeImage) {
      this(worldSearchItem.name, worldSearchItem.loadView, worldSearchItem.cancelled, oseeImage);
   }

   public abstract WorldSearchItem copy() ;

   public String getName()  {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    */
   public String getSelectedName(SearchType searchType)  {
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
      return oseeImage == null ? null : ImageManager.getImage(oseeImage);
   }

   public KeyedImage getOseeImage() {
      return oseeImage;
   }

}
