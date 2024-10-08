/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.ide.world.search.MyFavoritesSearchItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class MyFavoritesAction extends AbstractAtsAction {

   public MyFavoritesAction() {
      super("My Favorites");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      WorldEditor.open(new WorldEditorUISearchItemProvider(
         new MyFavoritesSearchItem("My Favorites", AtsApiService.get().getUserService().getCurrentUser()), null,
         TableLoadOption.None));
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.FAVORITE);
   }

}
