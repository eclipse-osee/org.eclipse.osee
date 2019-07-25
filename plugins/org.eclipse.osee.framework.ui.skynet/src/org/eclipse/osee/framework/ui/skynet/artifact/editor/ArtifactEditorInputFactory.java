/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * The factory which is capable of recreating class file editor inputs stored in a memento.
 *
 * @author Donald G. Dunne
 */
public class ArtifactEditorInputFactory implements IElementFactory {

   public final static String ID = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory"; //$NON-NLS-1$
   private final static String ART_KEY = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.uuid"; //$NON-NLS-1$
   private final static String ART_KEY_AS_LONG = "org.eclipse.osee.ats.WEEditorInputFactory.artIdAsLong"; //$NON-NLS-1$
   private final static String BRANCH_KEY =
      "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.branchUuid"; //$NON-NLS-1$
   private final static String TITLE = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.title"; //$NON-NLS-1$

   public ArtifactEditorInputFactory() {
   }

   @Override
   public IAdaptable createElement(IMemento memento) {
      String title = memento.getString(TITLE);
      String branchStr = memento.getString(BRANCH_KEY);
      BranchId branch = branchStr == null ? BranchId.SENTINEL : BranchId.valueOf(branchStr);

      ArtifactId artifactId = ArtifactId.SENTINEL;
      String artKeyAsLong = memento.getString(ART_KEY_AS_LONG);
      if (Strings.isNumeric(artKeyAsLong)) {
         artifactId = ArtifactId.valueOf(artKeyAsLong);
      } else {
         String artKeyAsInt = memento.getString(ART_KEY);
         if (Strings.isNumeric(artKeyAsInt)) {
            artifactId = ArtifactId.valueOf(artKeyAsInt);
         }
      }
      return new ArtifactEditorInput(branch, artifactId, title);
   }

   public static void saveState(IMemento memento, ArtifactEditorInput input) {
      String title = input.getName();
      Artifact artifact = input.getArtifact();
      if (artifact != null) {
         String artUuid = artifact.getIdString();
         BranchId branchId = artifact.getBranch();
         if (Strings.isValid(artUuid) && branchId.isValid() && Strings.isValid(title)) {
            memento.putString(BRANCH_KEY, branchId.getIdString());
            memento.putString(ART_KEY, artUuid);
            memento.putString(TITLE, title);
         }
      }
   }
}