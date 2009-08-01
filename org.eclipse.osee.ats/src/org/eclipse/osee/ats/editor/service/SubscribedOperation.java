/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.service;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.ISubscribableArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.SubscribeManager;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SubscribedOperation extends WorkPageService {

   private Action action;

   public SubscribedOperation(SMAManager smaMgr) {
      super(smaMgr);
   }

   @Override
   public Action createToolbarService() {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            (new SubscribeManager(smaMgr.getSma())).toggleSubscribe();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.SUBSCRIBED));
      return action;
   }

   @Override
   public void refresh() {
      if (action != null) action.setToolTipText(((ISubscribableArtifact) smaMgr.getSma()).amISubscribed() ? "Remove Subscribed" : "Add Subscribed");
   }

   @Override
   public String getName() {
      return "Subscribe to Email Notifications";
   }
}
