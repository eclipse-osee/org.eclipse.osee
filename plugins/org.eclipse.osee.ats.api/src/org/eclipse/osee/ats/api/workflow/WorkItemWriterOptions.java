/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
