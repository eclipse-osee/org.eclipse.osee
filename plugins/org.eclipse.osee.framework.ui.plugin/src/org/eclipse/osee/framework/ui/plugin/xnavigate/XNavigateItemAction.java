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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * Used to perform a specific java action
 *
 * @author Donald G. Dunne
 */
public class XNavigateItemAction extends XNavigateItem {

   private final Action action;
   private boolean promptFirst = false;
   private Collection<IUserGroupArtifactToken> groups;

   public XNavigateItemAction(String name, XNavItemCat... xNavItemCat) {
      this(name, false, (KeyedImage) null, xNavItemCat);
   }

   public XNavigateItemAction(String name, KeyedImage oseeImage, XNavItemCat... xNavItemCat) {
      this(name, false, oseeImage, xNavItemCat);
   }

   public XNavigateItemAction(String name, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      this(name, false, ImageManager.create(oseeImage), xNavItemCat);
   }

   public XNavigateItemAction(String name, boolean promptFirst, KeyedImage oseeImage, XNavItemCat... xNavItemCat) {
      super(name, oseeImage, xNavItemCat);
      this.action = null;
      this.promptFirst = promptFirst;
   }

   public XNavigateItemAction(Action action, KeyedImage oseeImage, XNavItemCat... xNavItemCat) {
      this(action, oseeImage, false, Collections.emptyList(), xNavItemCat);
   }

   public XNavigateItemAction(Action action, KeyedImage oseeImage, Collection<IUserGroupArtifactToken> groups, XNavItemCat... xNavItemCat) {
      this(action, oseeImage, false, groups, xNavItemCat);
   }

   public XNavigateItemAction(Action action, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      this(action, ImageManager.create(oseeImage), false, Collections.emptyList(), xNavItemCat);
   }

   public XNavigateItemAction(Action action, OseeImage oseeImage, boolean promptFirst, XNavItemCat... xNavItemCat) {
      this(action, ImageManager.create(oseeImage), promptFirst, Collections.emptyList(), xNavItemCat);
   }

   public XNavigateItemAction(Action action, KeyedImage oseeImage, boolean promptFirst, XNavItemCat... xNavItemCat) {
      this(action, oseeImage, promptFirst, null, xNavItemCat);
   }

   public XNavigateItemAction(Action action, KeyedImage oseeImage, boolean promptFirst, Collection<IUserGroupArtifactToken> groups, XNavItemCat... xNavItemCat) {
      super(action.getText(), oseeImage, xNavItemCat);
      this.action = action;
      this.promptFirst = promptFirst;
      this.groups = groups;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (action != null) {
         if (promptFirst) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName() + "?")) {
                     action.run();
                  }
               }
            });
         } else if (action.getStyle() == IAction.AS_CHECK_BOX) {
            action.setChecked(!action.isChecked());
            if (action.isChecked()) {
               action.run();
            }
         } else {
            action.run();
         }
      }
   }

   public boolean isPromptFirst() {
      return promptFirst;
   }

   public void setPromptFirst(boolean promptFirst) {
      this.promptFirst = promptFirst;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      if (groups != null && !groups.isEmpty()) {
         return groups;
      }
      if (categories.contains(XNavItemCat.OSEE_ADMIN)) {
         return Arrays.asList(CoreUserGroups.OseeAdmin);
      }
      return Arrays.asList(CoreUserGroups.Everyone);
   }

}
