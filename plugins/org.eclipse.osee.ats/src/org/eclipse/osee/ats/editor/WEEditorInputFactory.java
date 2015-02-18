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
package org.eclipse.osee.ats.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 * 
 * @author Donald G. Dunne
 */
public class WEEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.ats.WEEditorInputFactory"; //$NON-NLS-1$
   public final static String ART_KEY = "org.eclipse.osee.ats.WEEditorInputFactory.artUuid"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.ats.WEEditorInputFactory.branchUuid"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.WEEditorInputFactory.title"; //$NON-NLS-1$

   public WEEditorInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      long branchUuid = 0;
      if (Strings.isValid(memento.getString(BRANCH_KEY))) {
         branchUuid = Long.valueOf(memento.getString(BRANCH_KEY));
      }
      Integer artUuid = memento.getInteger(ART_KEY);
      String title = memento.getString(TITLE);
      return new SMAEditorInput(branchUuid, artUuid == null ? 0 : artUuid, title);
   }

   public static void saveState(IMemento memento, SMAEditorInput input) {
      int artUuid = input.getArtUuid();
      long branchUuid = input.getBranchUuid();
      String title = input.getTitle();
      if (input.getArtifact() != null && !input.getArtifact().isDeleted()) {
         artUuid = input.getArtifact().getArtId();
         branchUuid = input.getArtifact().getBranchUuid();
         title = input.getName();
      }
      if (artUuid > 0 && branchUuid > 0 && Strings.isValid(title)) {
         memento.putString(BRANCH_KEY, String.valueOf(branchUuid));
         memento.putInteger(ART_KEY, artUuid);
         memento.putString(TITLE, title);
      }
   }

}
