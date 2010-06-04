/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import org.eclipse.osee.framework.branch.management.exchange.handler.IExportItem;

/**
 * @author Ryan D. Brooks
 */
public interface IOseeExchangeDataProvider {

   public boolean wasZipExtractionRequired();

   public File getExportedDataRoot();

   public File getFile(IExportItem item);

   public File getFile(String fileName);
}
