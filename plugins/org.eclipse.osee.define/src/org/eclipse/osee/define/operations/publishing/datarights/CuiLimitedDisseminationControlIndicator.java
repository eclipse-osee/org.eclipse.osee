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

package org.eclipse.osee.define.operations.publishing.datarights;

/**
 * An enumeration of the CUI Limited Dissemination Controls. Only designating agencies can apply or approve limited
 * dissemination controls for CUI information. Authorized holders may apply limited dissemination controls as required
 * by or approved by the designating agency.
 *
 * @author Loren K. Ashley
 */

public enum CuiLimitedDisseminationControlIndicator {

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">No foreign dissemination</a>
    */

   NOFORN,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Federal employees only</a>
    */

   FED_ONLY,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Federal employees and contractors only</a>
    */

   FEDCON,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">No dissemination to contractors</a>
    */

   NOCON,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Dissemination list controlled</a>
    */

   DL_ONLY,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Releasable by information disclosure
    * official</a>
    */

   RELIDO,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Authorized for release to certain nationals
    * only</a>
    */

   REL_TO,

   /**
    * <a href="https://www.archives.gov/cui/registry/limited-dissemination">Display only</a>
    */

   DISPLAY_ONLY;

}

/* EOF */
