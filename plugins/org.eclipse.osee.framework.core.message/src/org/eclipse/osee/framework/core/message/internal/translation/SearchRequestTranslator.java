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
package org.eclipse.osee.framework.core.message.internal.translation;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

public class SearchRequestTranslator implements ITranslator<SearchRequest> {

   @Override
   public SearchRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      return null;
   }

   @Override
   public PropertyStore convert(SearchRequest object) throws OseeCoreException {
      return null;
   }

}
