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
package org.eclipse.osee.mim.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Luciano T. Vaglienti
 */
public class StructurePath extends PLGenericDBObject {
   public static final StructurePath SENTINEL = new StructurePath();
   private final LinkedList<ResolvedStructurePath> paths = new LinkedList<ResolvedStructurePath>();
   public StructurePath(Long id, String name) {
      super(id, name);
   }

   public StructurePath() {
      super();
   }

   @Override
   @JsonIgnore
   public Long getId() {
      return super.getId();
   }

   public List<ResolvedStructurePath> getPaths() {
      return this.paths;
   }

   public void addPath(ResolvedStructurePath path) {
      this.paths.add(path);
   }

}
