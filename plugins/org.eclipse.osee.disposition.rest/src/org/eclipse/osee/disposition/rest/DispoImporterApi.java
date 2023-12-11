/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.disposition.rest;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public interface DispoImporterApi {

   List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir, OperationReport report,
      Log logger);

}
