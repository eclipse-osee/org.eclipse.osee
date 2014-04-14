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
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
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
   public final static String KEY = "org.eclipse.osee.ats.WEEditorInputFactory.guid"; //$NON-NLS-1$
   public final static String TITLE = "org.eclipse.osee.ats.WEEditorInputFactory.title"; //$NON-NLS-1$

   public WEEditorInputFactory() {
   }

   /*
    * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
    */
   @Override
   public IAdaptable createElement(IMemento memento) {
      String guid = memento.getString(KEY);
      String title = memento.getString(TITLE);
      if (Strings.isValid(guid) && Strings.isValid(title)) {
         return new SMAEditorInput(guid, title);
      }
      return null;
   }

   public static void saveState(IMemento memento, SMAEditorInput input) {
      String guid = null;
      String title = null;
      if (input.getArtifact() != null && !input.getArtifact().isDeleted()) {
         if (input.isReload()) {
            guid = input.getGuid();
            title = input.getTitle();
         } else {
            guid = input.getArtifact().getGuid();
            title = ((AbstractWorkflowArtifact) input.getArtifact()).getEditorTitle();
         }
      }
      memento.putString(KEY, guid);
      memento.putString(TITLE, title);
   }

}
