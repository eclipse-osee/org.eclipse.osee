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

/**
 * @author Donald G. Dunne
 */
public abstract class WorldParameterSearchItem extends WorldSearchItem implements IWorldEditorParameterProvider, IDynamicWidgetLayoutListener, IXWidgetOptionResolver {

   /**
    * @param name
    */
   public WorldParameterSearchItem(String name) {
      super(name);
   }

   /**
    * @param name
    * @param loadView
    */
   public WorldParameterSearchItem(String name, LoadView loadView) {
      super(name, loadView);
   }

   /**
    * @param worldSearchItem
    */
   public WorldParameterSearchItem(WorldSearchItem worldSearchItem) {
      super(worldSearchItem);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#copy()
    */
   @Override
   public WorldSearchItem copy() {
      return null;
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

}
