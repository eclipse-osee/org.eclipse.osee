/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.search.engine.tagger;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.internal.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public class DefaultAttributeTaggerProvider implements IAttributeTaggerProvider {

   private String getValue(AttributeData attributeData) {
      String value = attributeData.getStringValue();
      return Strings.isValid(value) ? value.toLowerCase() : WordsUtil.EMPTY_STRING;
   }

   public void tagIt(AttributeData attributeData, ITagCollector tagCollector) throws Exception {
      TagProcessor.collectFromString(getValue(attributeData), tagCollector);
   }

   public boolean find(AttributeData attributeData, String value) {
      return getValue(attributeData).contains(value);
   }

}
