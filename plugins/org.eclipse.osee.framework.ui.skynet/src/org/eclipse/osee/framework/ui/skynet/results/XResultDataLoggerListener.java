/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.IResultDataListener;
import org.eclipse.osee.framework.core.util.result.XResultData;
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
