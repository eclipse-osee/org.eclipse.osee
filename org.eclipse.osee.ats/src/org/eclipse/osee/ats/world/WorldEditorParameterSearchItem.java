/*
 * Created on Nov 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorParameterSearchItem extends WorldSearchItem implements IWorldEditorParameterProvider, IDynamicWidgetLayoutListener, IXWidgetOptionResolver {

   private CustomizeData customizeData;
   private TableLoadOption[] tableLoadOptions;

   /**
    * @param name
    */
   public WorldEditorParameterSearchItem(String name) {
      super(name);
   }

   /**
    * @param name
    * @param loadView
    */
   public WorldEditorParameterSearchItem(String name, LoadView loadView) {
      super(name, loadView);
   }

   /**
    * @param worldSearchItem
    */
   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem) {
      super(worldSearchItem);
   }

   public abstract String getParameterXWidgetXml() throws OseeCoreException;

   public abstract Result isParameterSelectionValid() throws OseeCoreException;

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#run(org.eclipse.osee.ats.world.WorldEditor, org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType, boolean)
    */
   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorParameterProvider#getWidgetOptions(org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData)
    */
   @Override
   public String[] getWidgetOptions(DynamicXWidgetLayoutData widgetData) {
      return null;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * @return the customizeData
    */
   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   /**
    * @return the tableLoadOptions
    */
   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

}
