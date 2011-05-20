/*
 * Created on May 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.IResultDataListener;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * Added to XResultData if desire results to be OseeLog
 * 
 * @author Donald G. Dunne
 */
public class XResultDataLoggerListener implements IResultDataListener {

   @Override
   public void log(final XResultData.Type type, final String str) {
      OseeLog.log(SkynetGuiPlugin.class, Level.parse(type.name().toUpperCase()), str);
   }

}
