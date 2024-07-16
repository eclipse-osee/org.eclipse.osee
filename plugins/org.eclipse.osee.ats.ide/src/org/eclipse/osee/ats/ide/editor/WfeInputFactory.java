/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.ArtifactId;
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

   public final static String ID = "org.eclipse.osee.ats.ide.WEEditorInputFactory"; //$NON-NLS-1$
   public final static String ART_KEY = "org.eclipse.osee.ats.ide.WEEditorInputFactory.artId"; //$NON-NLS-1$
   public final static String ART_KEY_AS_LONG = "org.eclipse.osee.ats.ide.WEEditorInputFactory.artIdAsLong"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.ats.ide.WEEditorInputFactory.branchId"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.ide.WEEditorInputFactory.title"; //$NON-NLS-1$

   public WfeInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      String branchStr = memento.getString(BRANCH_KEY);
      BranchId branch = branchStr == null ? BranchId.SENTINEL : BranchId.valueOf(branchStr);

      ArtifactId artifactId;

      String artKeyAsLong = memento.getString(ART_KEY_AS_LONG);
      if (Strings.isValid(artKeyAsLong)) {
         artifactId = ArtifactId.valueOf(artKeyAsLong);
      } else {
         String artKeyAsInt = memento.getString(ART_KEY);
         artifactId = artKeyAsInt == null ? ArtifactId.SENTINEL : ArtifactId.valueOf(artKeyAsInt);
      }

      String title = memento.getString(TITLE);
      return new WfeInput(branch, artifactId, title);
   }

   public static void saveState(IMemento memento, WfeInput input) {
      ArtifactId artifactId = input.getArtId();
      BranchId branch = input.getBranchId();
      String title = input.getSavedTitle();
      if (input.getArtifact() != null && !input.getArtifact().isDeleted()) {
         artifactId = input.getArtifact();
         branch = input.getArtifact().getBranch();
         title = input.getName();
      }
      if (artifactId.isValid() && branch.isValid() && Strings.isValid(title)) {
         memento.putString(BRANCH_KEY, branch.getIdString());
         memento.putString(ART_KEY_AS_LONG, artifactId.getIdString());
         memento.putString(TITLE, title);
      }
   }
}