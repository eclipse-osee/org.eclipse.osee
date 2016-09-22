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
package org.eclipse.osee.framework.core.data;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface IRelationType extends Named, Id {

   public static IRelationType valueOf(long id, String name) {
      final class RelationTypeImpl extends NamedId implements IRelationType {

         public RelationTypeImpl(Long txId, String name) {
            super(txId, name);
         }
      }
      return new RelationTypeImpl(id, name);
   }
}
