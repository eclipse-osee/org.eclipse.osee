/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class OseeTreeReportAdapter implements IOseeTreeReportProvider {

   private final String title;

   public OseeTreeReportAdapter(String title) {
      this.title = title;
   }

   @Override
   public String getEditorTitle() {
      return title;
   }

   @Override
   public String getReportTitle() {
      return title;
   }

}
