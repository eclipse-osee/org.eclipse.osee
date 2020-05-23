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

package org.eclipse.osee.orcs;

/**
 * @author Roberto E. Escobar
 */
public enum ImportOptions {
   USE_IDS_FROM_IMPORT_FILE,
   EXCLUDE_BASELINE_TXS,
   ALL_AS_ROOT_BRANCHES,
   CLEAN_BEFORE_IMPORT,
   MIN_TXS,
   MAX_TXS;
}
