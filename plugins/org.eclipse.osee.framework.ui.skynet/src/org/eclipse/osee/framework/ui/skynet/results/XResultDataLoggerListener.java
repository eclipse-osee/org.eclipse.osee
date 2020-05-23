/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.result.IResultDataListener;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * Added to XResultData if desire results to be OseeLog
 * 
 * @author Donald G. Dunne
 */
public class XResultDataLoggerListener implements IResultDataListener {

   @Override
   public void log(final XResultData.Type type, final String str) {
      OseeLog.log(Activator.class, Level.parse(type.name().toUpperCase()), str);
   }

}
