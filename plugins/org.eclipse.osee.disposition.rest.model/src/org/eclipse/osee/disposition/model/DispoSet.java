/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.model;

import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Angel Avila
 */

public interface DispoSet extends Identifiable<String> {

   String getImportPath();

   List<Note> getNotesList();

   String getImportState();

   String getDispoType();

   OperationReport getOperationSummary();

   String getCiSet();

   String getRerunList();

   Date getTime();
}
