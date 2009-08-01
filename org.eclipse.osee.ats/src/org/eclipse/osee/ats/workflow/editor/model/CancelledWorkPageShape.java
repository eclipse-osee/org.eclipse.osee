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
package org.eclipse.osee.ats.workflow.editor.model;

import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class CancelledWorkPageShape extends WorkPageShape {

   /**
    * @param workPageDefinition
    */
   public CancelledWorkPageShape(WorkPageDefinition workPageDefinition) {
      super(workPageDefinition);
   }

   public CancelledWorkPageShape() {
      super(new WorkPageDefinition("Cancelled", "NEW", AtsCancelledWorkPageDefinition.ID));
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WorkPageShape) {
         try {
            return ((WorkPageShape) obj).isCancelledState();
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return super.equals(obj);
   }
}
