/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.core.model;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IXViewerDynamicColumn {

   default public boolean performUI() {
      // for extensibility
      return true;
   }

   public String getId();

   default public boolean refreshColumnOnChange() {
      return false;
   }

   default public void addColumnsOnShow(Object xViewer, List<XViewerColumn> newXCols) {
      // for extensibility
   }

}
