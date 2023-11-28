/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.xml.publishing;

/**
 * Enumeration of the XML tag types open, close, and self closing.
 *
 * @author Loren K. Ashley
 */

public enum XmlTagType {

   /**
    * XML open tag.
    * <ul>
    * <li>&lt;tag&gt;</li>
    * </ul>
    */

   OPEN,

   /**
    * XML close tag.
    * <ul>
    * <li>&lt;tag/&gt;</li>
    * </ul>
    */

   CLOSE,

   /**
    * XML self closing tag.
    * <ul>
    * <li>&lt;/tag&gt;</li>
    * </ul>
    */

   SELF_CLOSING;
}

/* EOF */
