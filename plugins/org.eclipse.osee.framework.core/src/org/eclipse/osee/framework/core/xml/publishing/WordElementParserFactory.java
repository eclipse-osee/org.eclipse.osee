/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.util.function.BiFunction;
import java.util.function.Function;
import org.w3c.dom.Element;

public class WordElementParserFactory<P extends AbstractElement, L extends AbstractElementList<? super P, ? super C>, C extends AbstractElement> {

   Function<P, L> listFactory;
   BiFunction<P, Element, C> childFactory;
   XmlTagSpecification childTag;

   WordElementParserFactory(Function<P, L> listFactory, BiFunction<P, Element, C> childFactory, XmlTagSpecification childTag) {
      this.listFactory = listFactory;
      this.childFactory = childFactory;
      this.childTag = childTag;
   }

   public Function<P, L> getListFactory() {
      return this.listFactory;
   }

   public BiFunction<P, Element, C> getChildFactory() {
      return this.childFactory;
   }

   public XmlTagSpecification getChildTag() {
      return this.childTag;
   }

}
