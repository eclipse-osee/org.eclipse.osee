/*
 * Created on Nov 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

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
