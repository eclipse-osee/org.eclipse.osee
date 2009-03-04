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
package org.eclipse.osee.ats.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.export.AtsExportManager;
import org.eclipse.osee.ats.operation.CancelMultipleWorkflows;
import org.eclipse.osee.ats.operation.CompleteMultipleWorkflows;
import org.eclipse.osee.ats.operation.DuplicateWorkflowBlam;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldEditorItem extends AtsWorldEditorItemBase {

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsWorldEditorItem#getWorldEditorMenuItems(org.eclipse.osee.ats.world.IWorldEditorProvider, org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public List<? extends IAtsWorldEditorMenuItem> getWorldEditorMenuItems(IWorldEditorProvider worldEditorProvider, WorldEditor worldEditor) throws OseeCoreException {
      try {
         return Arrays.asList(new AtsExportManager(), new DuplicateWorkflowBlam(), new CompleteMultipleWorkflows(),
               new CancelMultipleWorkflows());
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }
}
