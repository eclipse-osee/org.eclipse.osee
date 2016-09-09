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
   public final static String ART_UUID = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.uuid"; //$NON-NLS-1$
   public final static String BRANCH_KEY = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.branchUuid"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.framework.ui.skynet.ArtifactEditorInputFactory.title"; //$NON-NLS-1$

   public ArtifactEditorInputFactory() {
   }

   @Override
   public IAdaptable createElement(IMemento memento) {
      long branchUuid = 0;
      String title = memento.getString(TITLE);
      if (Strings.isValid(memento.getString(BRANCH_KEY))) {
         branchUuid = Long.valueOf(memento.getString(BRANCH_KEY));
      }
      Integer artUuid = null;
      String artUuidStr = memento.getString(ART_UUID);
      if (Strings.isValid(artUuidStr)) {
         artUuid = Integer.valueOf(artUuidStr);
      }
      return new ArtifactEditorInput(branchUuid, (artUuid == null ? null : Long.valueOf(artUuid)), title);
   }

   public static void saveState(IMemento memento, ArtifactEditorInput input) {
      String title = input.getName();
      Artifact artifact = input.getArtifact();
      if (artifact != null) {
         String artUuid = artifact.getUuid().toString();
         long branchUuid = artifact.getBranchId();
         if (Strings.isValid(artUuid) && branchUuid > 0 && Strings.isValid(title)) {
            memento.putString(BRANCH_KEY, String.valueOf(branchUuid));
            memento.putString(ART_UUID, artUuid);
            memento.putString(TITLE, title);
         }
      }
   }

}
