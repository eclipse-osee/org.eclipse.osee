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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken.ArtifactTokenImpl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * Specifies an Enumerated Artifact to containing dynamically configurable enumerations using an OseeTypeEnum. These
 * artifacts will be created and configured during dbInit with "values" specified
 *
 * @author Donald G. Dunne
 */
public class OseeTypeEnumArtifactToken extends ArtifactTokenImpl implements IUserGroupArtifactToken {

   public static final OseeTypeEnumArtifactToken SENTINEL = valueOf(ArtifactId.SENTINEL.getId(), "Unknown");
   public List<String> values = new ArrayList<>();

   public OseeTypeEnumArtifactToken(Long id, String name, String... values) {
      super(id, name, CoreBranches.COMMON, CoreArtifactTypes.OseeTypeEnum);
      java.util.Collections.addAll(this.values, values);
   }

   public static OseeTypeEnumArtifactToken valueOf(Long id, String name, String... values) {
      return new OseeTypeEnumArtifactToken(id, name, values);
   }

   public List<String> getValues() {
      return values;
   }

}