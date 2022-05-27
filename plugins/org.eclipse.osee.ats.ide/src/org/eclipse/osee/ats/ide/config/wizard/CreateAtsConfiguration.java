/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.config.wizard;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;

public class CreateAtsConfiguration extends XNavigateItem {

   public CreateAtsConfiguration() {
      super("Create ATS Configuration", FrameworkImage.GEAR, AtsNavigateViewItems.ATS_ADMIN);
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(AtsUserGroups.AtsAdmin);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      AtsConfigWizard wizard = new AtsConfigWizard();
      WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
      dialog.create();
      dialog.open();
   }

}
