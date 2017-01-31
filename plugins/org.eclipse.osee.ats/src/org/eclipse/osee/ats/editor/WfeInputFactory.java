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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 *
 * @author Donald G. Dunne
 */
public class WfeInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.ats.WEEditorInputFactory"; //$NON-NLS-1$
   public final static String ART_KEY = "org.eclipse.osee.ats.WEEditorInputFactory.artUuid"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.ats.WEEditorInputFactory.branchUuid"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.WEEditorInputFactory.title"; //$NON-NLS-1$

   public WfeInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      BranchId branch = BranchId.SENTINEL;
      if (Strings.isValid(memento.getString(BRANCH_KEY))) {
         branch = BranchId.valueOf(memento.getString(BRANCH_KEY));
      }
      Integer artUuid = memento.getInteger(ART_KEY);
      String title = memento.getString(TITLE);
      return new WfeInput(branch, artUuid == null ? 0 : artUuid, title);
   }

   public static void saveState(IMemento memento, WfeInput input) {
      int artUuid = input.getArtUuid();
      BranchId branch = input.getBranchId();
      String title = input.getTitle();
      if (input.getArtifact() != null && !input.getArtifact().isDeleted()) {
         artUuid = input.getArtifact().getArtId();
         branch = input.getArtifact().getBranch();
         title = input.getName();
      }
      if (artUuid > 0 && branch.isValid() && Strings.isValid(title)) {
         memento.putString(BRANCH_KEY, branch.getIdString());
         memento.putInteger(ART_KEY, artUuid);
         memento.putString(TITLE, title);
      }
   }

}
