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
package org.eclipse.osee.orcs.core.internal.attribute.primitives;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.orcs.core.AttributeClassProvider;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class PrimitiveAttributeClassProvider implements AttributeClassProvider {

   @Override
   public List<Class<? extends Attribute<?>>> getClasses() {
      List<Class<? extends Attribute<?>>> clazzes = new ArrayList<>();

      clazzes.add(BooleanAttribute.class);
      clazzes.add(IntegerAttribute.class);
      clazzes.add(FloatingPointAttribute.class);
      clazzes.add(StringAttribute.class);
      clazzes.add(DateAttribute.class);
      clazzes.add(EnumeratedAttribute.class);
      clazzes.add(JavaObjectAttribute.class);
      clazzes.add(CompressedContentAttribute.class);
      //      primitiveAttributes.put("WordAttribute", WordAttribute.class);

      return clazzes;
   }
}
