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
package org.eclipse.osee.ats.ide.world;

import java.util.regex.Pattern;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class WorldAssigneeFilter extends ViewerFilter {

   Pattern p;

   public WorldAssigneeFilter() {
      p = Pattern.compile(AtsClientService.get().getUserService().getCurrentUser().getName());
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      try {
         if (element instanceof IAtsObject) {
            return p.matcher(AtsClientService.get().getColumnService().getColumnText(AtsColumnId.Assignees,
               (IAtsObject) element)).find();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return true;
   }

}
