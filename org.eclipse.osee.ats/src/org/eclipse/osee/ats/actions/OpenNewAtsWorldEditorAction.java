/*
 * Created on Sep 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsWorldEditorAction extends Action {

   private final IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler;

   public OpenNewAtsWorldEditorAction(IOpenNewAtsWorldEditorHandler openNewAtsWorldEditorHandler) {
      this.openNewAtsWorldEditorHandler = openNewAtsWorldEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.GLOBE));
      setToolTipText("Open in ATS World Editor");
   }

   public interface IOpenNewAtsWorldEditorHandler {
      public IWorldEditorProvider getWorldEditorProviderCopy() throws OseeCoreException;

      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;
   }

   @Override
   public void run() {
      try {
         IWorldEditorProvider provider = openNewAtsWorldEditorHandler.getWorldEditorProviderCopy();
         provider.setCustomizeData(openNewAtsWorldEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         WorldEditor.open(provider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
