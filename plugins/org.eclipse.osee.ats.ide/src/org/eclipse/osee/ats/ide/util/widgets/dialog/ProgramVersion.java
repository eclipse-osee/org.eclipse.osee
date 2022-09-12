/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class ProgramVersion {

   private final ArtifactToken programTok;
   private final IAtsVersion version;
   private final ArtifactToken progVerArt;

   public ProgramVersion(ArtifactToken programTok, IAtsVersion version, ArtifactToken progVerArt) {
      this.programTok = programTok;
      this.version = version;
      this.progVerArt = progVerArt;
   }

   public ArtifactToken getProgramTok() {
      return programTok;
   }

   public IAtsVersion getVersion() {
      return version;
   }

   @Override
   public String toString() {
      return String.format("[%s] - [%s]", programTok.getName(), version.getName());
   }

   public ArtifactToken getProgVerArt() {
      return progVerArt;
   }

}
