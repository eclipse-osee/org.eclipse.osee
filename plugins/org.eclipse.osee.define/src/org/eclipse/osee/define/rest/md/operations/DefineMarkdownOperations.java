/*
 * Created on Jun 1, 2023
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.rest.md.operations;

import org.eclipse.osee.define.api.md.DefineMarkdownImportData;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
public class DefineMarkdownOperations {

   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   public DefineMarkdownOperations(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
   }

   public DefineMarkdownImportData importMarkdown(DefineMarkdownImportData data) {
      data.getRd().log("Importing Markdown\n");
      if (data.getBranch().isValid()) {
         data.getRd().logf("Branch %s is VALID\n", data.getBranch());
      } else {
         data.getRd().errorf("Branch %s is invalid\n", data.getBranch());
      }
      return data;
   }

}
