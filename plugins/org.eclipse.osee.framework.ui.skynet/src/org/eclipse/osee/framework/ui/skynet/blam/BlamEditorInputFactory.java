/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 *
 * @author Donald G. Dunne
 */
public class BlamEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.framework.ui.skynet.blam.BlamEditorInputFactory"; //$NON-NLS-1$
   private static final String BLAM_ID = "org.eclipse.osee.framework.ui.skynet.blam.id"; //$NON-NLS-1$;

   @Override
   public IAdaptable createElement(IMemento memento) {
      try {
         String blamId = memento.getString(BLAM_ID);
         if (Strings.isValid(blamId)) {
            for (AbstractBlam blam : BlamContributionManager.getBlamOperations()) {
               if (blam.getName().equals(blamId)) {
                  return new BlamEditorInput(blam);
               }

            }
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static void saveState(IMemento memento, BlamEditorInput input) {
      String id = input.getBlamOperation().getName();
      memento.putString(BLAM_ID, id);
   }

}
