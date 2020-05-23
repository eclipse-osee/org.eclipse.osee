/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.tab.members.IMemberProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SetCollectorOrderAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final IMemberProvider memberProvider;

   public SetCollectorOrderAction(IMemberProvider memberProvider, CollectorArtifact goalArt, ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(String.format("Set %s Order", memberProvider.getCollectorName()));
      this.memberProvider = memberProvider;
      this.selectedAtsArtifacts = selectedAtsArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(memberProvider.getImageKey());
   }

   @Override
   public void run() {
      try {
         memberProvider.promptChangeOrder(memberProvider.getArtifact(),
            this.selectedAtsArtifacts.getSelectedAtsArtifacts());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
