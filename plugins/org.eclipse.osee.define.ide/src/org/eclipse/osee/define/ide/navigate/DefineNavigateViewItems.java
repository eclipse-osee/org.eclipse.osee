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
package org.eclipse.osee.define.ide.navigate;

import java.util.logging.Level;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemOperation;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.CompareTwoStringsAction;
import org.eclipse.osee.framework.ui.skynet.artifact.MassEditDirtyArtifactOperation;

/**
 * @author Donald G. Dunne
 */
public class DefineNavigateViewItems implements IXNavigateCommonItem {

   @Override
   public String getSectionId() {
      return "Define";
   }

   @Override
   public void addUtilItems(XNavigateItem utilItems) {
      try {
         new XNavigateItemAction(utilItems, new CompareTwoStringsAction(), FrameworkImage.EDIT);
         new XNavigateItemAction(utilItems,
            new org.eclipse.osee.framework.ui.skynet.action.CompareTwoArtifactIdListsAction(), FrameworkImage.EDIT);
         new XNavigateItemOperation(utilItems, FrameworkImage.ARTIFACT_MASS_EDITOR, MassEditDirtyArtifactOperation.NAME,
            new MassEditDirtyArtifactOperation());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}