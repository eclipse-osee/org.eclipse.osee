/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.orcs.search;

/**
 * @author Christopher Rebuck
 */

public final class ArtifactTable {

   public final ArtifactTableOptions tableOptions;
   public final String[][] data;

   public ArtifactTable(ArtifactTableOptions tableOptions, String[][] data) {
      this.tableOptions = tableOptions;
      this.data = data;

   }

}