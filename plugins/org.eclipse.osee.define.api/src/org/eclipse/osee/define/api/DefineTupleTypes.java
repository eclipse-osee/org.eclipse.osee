/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import static org.eclipse.osee.framework.core.enums.CoreTupleFamilyTypes.GitFamily;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Tuple4Type;

/**
 * @author Ryan D. Brooks
 */
public final class DefineTupleTypes {
   private static final ChangeType[] changeTypes = ChangeType.values();

   // repository, code unit, commitArtId, changeType
   public static final Tuple4Type<ArtifactId, ArtifactId, ArtifactId, ChangeType> GitCommitFile =
      Tuple4Type.valueOf(GitFamily, 11L, ArtifactId::valueOf, ArtifactId::valueOf, ArtifactId::valueOf,
         ordinal -> changeTypes[ordinal.intValue()]);

   // repository, code unit, latest commitArtId, latest baseline commitArtId
   public static final Tuple4Type<ArtifactId, ArtifactId, ArtifactId, ArtifactId> GitLatest = Tuple4Type.valueOf(
      GitFamily, 12L, ArtifactId::valueOf, ArtifactId::valueOf, ArtifactId::valueOf, ArtifactId::valueOf);

   private DefineTupleTypes() {
      // Constants
   }
}