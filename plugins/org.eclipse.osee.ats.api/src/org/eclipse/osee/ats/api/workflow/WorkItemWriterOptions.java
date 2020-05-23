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

package org.eclipse.osee.ats.api.workflow;

/**
 * @author Donald G. Dunne
 */
public enum WorkItemWriterOptions {
   // write keys as long attribute ids instead of attribute name
   KeysAsIds,
   // write dates as long instead of human readable date format
   DatesAsLong,
   // write attributes out with gammaIds and attrIds
   ValuesWithIds,
   // include assignees and version as tokens
   WriteRelatedAsTokens
}
