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
import org.eclipse.osee.ats.task.ITaskEditorProvider;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class OpenNewAtsTaskEditor extends Action {

   private final IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler;

   public OpenNewAtsTaskEditor(IOpenNewAtsTaskEditorHandler openNewAtsTaskEditorHandler) {
      this.openNewAtsTaskEditorHandler = openNewAtsTaskEditorHandler;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
      setToolTipText("Open New ATS Task Editor");
   }

   public interface IOpenNewAtsTaskEditorHandler {
      public ITaskEditorProvider getTaskEditorProviderCopy() throws OseeCoreException;

      public CustomizeData getCustomizeDataCopy() throws OseeCoreException;
   }

   @Override
   public void run() {
      try {
         ITaskEditorProvider provider = openNewAtsTaskEditorHandler.getTaskEditorProviderCopy();
         provider.setCustomizeData(openNewAtsTaskEditorHandler.getCustomizeDataCopy());
         provider.setTableLoadOptions(TableLoadOption.NoUI);
         TaskEditor.open(provider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
