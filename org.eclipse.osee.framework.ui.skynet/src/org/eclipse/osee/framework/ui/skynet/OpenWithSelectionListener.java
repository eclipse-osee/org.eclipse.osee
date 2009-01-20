/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public class OpenWithSelectionListener extends SelectionAdapter {
   private Command command;
   private IHandlerService handlerService;

   public OpenWithSelectionListener(Command command) {
      super();
      this.command = command;
      this.handlerService =
            (IHandlerService) AWorkbench.getActivePage().getActivePart().getSite().getService(IHandlerService.class);
   }

   @Override
   public void widgetSelected(SelectionEvent e) {
      try {
         handlerService.executeCommand(command.getId(), null);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

}
