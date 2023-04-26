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
 * Required statements can be placed on the title page, in the page header, and/or the page footer. Head and footer
 * statements can be required on all pages or only the pages containing data with the required indicator.
 *
 * @author Loren K. Ashley
 */

public enum RequiredIndicatorFrequencyIndicator {

   /**
    * A footer is required only on pages containing data with the required indicator.
    */

   FOOTER_CONTAINING,

   /**
    * A footer is required on every page of the document for the required indicator.
    */

   FOOTER_EVERY,

   /**
    * A header is required only on pages containing data with the required indicator.
    */

   HEADER_CONTAINING,

   /**
    * A header is required on every page of the document for the required indicator.
    */

   HEADER_EVERY,

   /**
    * A statement is required on the title page.
    */

   TITLE;
}

/* EOF */