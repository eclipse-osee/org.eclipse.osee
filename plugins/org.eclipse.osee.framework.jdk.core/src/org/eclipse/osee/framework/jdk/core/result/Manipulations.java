/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.jdk.core.result;

/**
 * @author Donald G. Dunne
 */
public enum Manipulations {
   NONE, //
   GUID_CMD_HYPER,
   // Replace all GUID strings with hyperlinks; ATS=<guid> opens Action editor
   // ART=<guid> opens Artifact editor, BOTH=<guid> allows either
   SUCCESS_GREEN, // Make all "Error" strings red
   ERROR_RED, // Make all "Error" strings red
   WARNING_YELLOW, // Make all "Warning" strings yellow
   CONVERT_NEWLINES, // Convert all \n to <br>
   HTML_MANIPULATIONS, // Do all except converting newlines
   RAW_HTML, // Just display in simple html page
   ERROR_WARNING_HEADER, // Shows Errors: 4 Warnings: 23 count at top of page
   ALL,
   ERROR_WARNING_FROM_SEARCH // Performs search for error and warning count instead of using logError and logWarning counts
};
