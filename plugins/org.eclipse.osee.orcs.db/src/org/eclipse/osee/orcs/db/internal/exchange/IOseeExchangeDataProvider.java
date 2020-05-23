/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange;

import java.io.File;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.IExportItem;

/**
 * @author Ryan D. Brooks
 */
public interface IOseeExchangeDataProvider {

   public boolean wasZipExtractionRequired();

   public File getExportedDataRoot();

   public File getFile(IExportItem item);

   public File getFile(String fileName);

   public Log getLogger();

   public String getExchangeBasePath();
}
