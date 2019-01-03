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
package org.eclipse.osee.ats.ide.workdef.config;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class ImportAIsAndTeamDefinitionsItem extends XNavigateItemAction {

   public ImportAIsAndTeamDefinitionsItem(XNavigateItem parent) {
      super(parent, "Import AIs and Team Definition to DB", AtsImage.WORK_DEFINITION);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      EntryDialog dialog = new EntryDialog(getName(), "Enter DB type");
      if (dialog.open() == 0) {
         AtsWorkDefinitionSheetProviders.importAIsAndTeamsToDatabase(dialog.getEntry());
      }
   }

}
