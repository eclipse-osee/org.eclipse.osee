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
import org.eclipse.osee.ats.artifact.IFavoriteableArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.Favorites;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class FavoriteOperation extends WorkPageService {

   private final SMAManager smaMgr;
   private Action action;

   public FavoriteOperation(SMAManager smaMgr) {
      super(smaMgr);
      this.smaMgr = smaMgr;
   }

   @Override
   public Action createToolbarService() {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            (new Favorites(smaMgr.getSma())).toggleFavorite();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.FAVORITE));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Add as Favorite";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.operation.WorkPageService#refresh()
    */
   @Override
   public void refresh() {
      if (action != null) action.setToolTipText(((IFavoriteableArtifact) smaMgr.getSma()).amIFavorite() ? "Remove Favorite" : "Add Favorite");
   }

}
