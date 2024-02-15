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

import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

public interface XmlNamespaceSpecification extends ToMessage {

   String getPrefix();

   Optional<String> getUri();

   @Override
   default Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();
      //@formatter:off
      outMessage
         .indent( indent )
         .title( "XmlNamespaceSpecification" )
         .indentInc()
         .segment( "Class",  this.getClass().getName() )
         .segment( "URI",    this.getUri() )
         .segment( "Prefix", this.getPrefix() )
         .indentDec()
         ;
      //@formatter:on
      return outMessage;
   }

}
