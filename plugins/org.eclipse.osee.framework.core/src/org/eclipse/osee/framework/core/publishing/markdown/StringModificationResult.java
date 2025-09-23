/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.core.publishing.markdown;

import java.util.List;

public class StringModificationResult {
   private final String modifiedString;
   private final List<String> changes;

   public StringModificationResult(String modifiedString, List<String> changes) {
      this.modifiedString = modifiedString;
      this.changes = changes;
   }

   public String getModifiedString() {
      return modifiedString;
   }

   public List<String> getChanges() {
      return changes;
   }
}
