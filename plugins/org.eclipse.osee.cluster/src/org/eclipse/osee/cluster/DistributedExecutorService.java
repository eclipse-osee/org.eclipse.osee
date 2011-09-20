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
package org.eclipse.osee.cluster;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Roberto E. Escobar
 */
public interface DistributedExecutorService extends ExecutorService {

   <T> Future<T> submit(Callable<T> worker, Callback<T> callback);

   <T> Future<T> submitOnKeyOwner(Callable<T> worker, Callback<T> callback, Object key);

   <T> Future<T> submitOnMember(Callable<T> worker, Callback<T> callback, Member member);

   <T> Future<T> submitOnMembers(Callable<T> worker, Callback<T> callback, Set<Member> members);
}
