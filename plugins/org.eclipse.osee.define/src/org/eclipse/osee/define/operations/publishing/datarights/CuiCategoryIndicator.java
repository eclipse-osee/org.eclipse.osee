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
 * An enumeration of the supported CUI Categories. Only add categories that are listed in CUI Registry which can be
 * found at <a href=
 * "https://www.archives.gov/cui/registry/category-list">https://www.archives.gov/cui/registry/category-list</a>.
 *
 * @author Loren K. Ashley
 */

public enum CuiCategoryIndicator {

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/controlled-technical-info.html">Controlled
    * Technical Information</a>
    */

   CTI,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/ucni-dod">Unclassified Controlled Nuclear
    * Information - Defense</a>
    */

   DCNI,

   /**
    * <a href=
    * "https://www.archives.gov/cui/registry/category-detail/dod-critical-infrastructure-security-information">DoD
    * Critical Infrastructure Security Information</a>
    */

   DCRIT,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/export-control.html">Export Controlled</a>
    */

   EXPT,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/export-controlled-research">Export Controlled
    * Research</a>
    */

   EXPTR,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/proprietary-manufacturer.html">Proprietary
    * Manufacturer</a>
    */

   MFC,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/naval-nuclear-propulsion-info">Naval Nuclear
    * Propulsion Information</a>
    */

   NNPI,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/proprietary-business-info.html">General Proprietary
    * Business Information</a>
    */

   PROPIN,

   /**
    * <a href="https://www.archives.gov/cui/registry/category-detail/procurement-acquisition.html">General Procurement
    * and Acquisition</a>
    */

   PROCURE;
}

/* EOF */
