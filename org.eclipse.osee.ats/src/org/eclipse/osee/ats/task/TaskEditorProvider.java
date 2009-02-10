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
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public abstract class TaskEditorProvider implements ITaskEditorProvider {

   protected TableLoadOption[] tableLoadOptions;
   protected CustomizeData customizeData;

   public TaskEditorProvider(CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      this.customizeData = customizeData;
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * @return the customizeData
    */
   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTableLoadOptions()
    */
   @Override
   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
      return Collections.getAggregate(tableLoadOptions);
   }

   /**
    * @param tableLoadOptions the tableLoadOptions to set
    */
   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * @param customizeData the customizeData to set
    */
   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

}
