/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.event.send;

public interface OteEventMessageCallable<T,R> {

   void timeout(T transmitted);
   
   void call(T transmitted, R recieved, OteEventMessageFuture<?, ?> future);
   
}
