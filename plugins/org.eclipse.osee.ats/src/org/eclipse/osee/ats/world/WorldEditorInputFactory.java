/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 * 
 * @author Donald G. Dunne
 */
public class WorldEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.ats.WorldEditorInputFactory"; //$NON-NLS-1$
   public final static String KEY = "org.eclipse.osee.ats.WorldEditorInputFactory.guids"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.WorldEditorInputFactory.title"; //$NON-NLS-1$

   public WorldEditorInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      List<String> guids = new ArrayList<String>();
      for (String guid : memento.getString(KEY).split(",")) {
         guids.add(guid);
      }
      String title = memento.getString(TITLE);
      if (!guids.isEmpty() && Strings.isValid(title)) {
         return new WorldEditorInput(new WorldEditorReloadProvider(title, guids));
      }
      return null;
   }

   public static void saveState(IMemento memento, WorldEditorInput input) {
      String guid = null;
      String title = null;
      guid = Collections.toString(",", input.getGuids());
      title = input.getName();
      memento.putString(KEY, guid);
      memento.putString(TITLE, title);
   }

}
