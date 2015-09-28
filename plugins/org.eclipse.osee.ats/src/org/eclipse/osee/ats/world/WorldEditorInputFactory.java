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
   public final static String ART_UUIDS = "org.eclipse.osee.ats.WorldEditorInputFactory.artUuids"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.ats.WorldEditorInputFactory.branchUuid"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.WorldEditorInputFactory.title"; //$NON-NLS-1$

   public WorldEditorInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      long branchUuid = 0;
      List<Integer> artUuids = new ArrayList<>();
      String title = memento.getString(TITLE);
      try {
         if (Strings.isValid(memento.getString(BRANCH_KEY))) {
            branchUuid = Long.valueOf(memento.getString(BRANCH_KEY));
         }
         for (String artUuid : memento.getString(ART_UUIDS).split(",")) {
            artUuids.add(Integer.valueOf(artUuid));
         }
      } catch (Exception ex) {
         // do nothing
      }
      return new WorldEditorInput(new WorldEditorReloadProvider(title, branchUuid, artUuids));
   }

   public static void saveState(IMemento memento, WorldEditorInput input) {
      String title = input.getName();
      String artUuids = Collections.toString(",", input.getGuids());
      long branchUuid = input.getBranchUuid();

      if (Strings.isValid(artUuids) && branchUuid > 0 && Strings.isValid(title)) {
         memento.putString(BRANCH_KEY, String.valueOf(branchUuid));
         memento.putString(ART_UUIDS, artUuids);
         memento.putString(TITLE, title);
      }
   }

}
