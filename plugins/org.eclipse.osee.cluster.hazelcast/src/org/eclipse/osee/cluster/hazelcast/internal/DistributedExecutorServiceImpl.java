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
package org.eclipse.osee.cluster.hazelcast.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.osee.cluster.Callback;
import org.eclipse.osee.cluster.DistributedExecutorService;
import org.eclipse.osee.cluster.Member;
import com.hazelcast.core.DistributedTask;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author Roberto E. Escobar
 */
public class DistributedExecutorServiceImpl implements DistributedExecutorService {

   private final HazelcastInstance instance;

   public DistributedExecutorServiceImpl(HazelcastInstance instance) {
      this.instance = instance;
   }

   private ExecutorService getProxyObject() {
      return instance.getExecutorService();
   }

   @Override
   public void shutdown() {
      getProxyObject().shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return getProxyObject().shutdownNow();
   }

   @Override
   public boolean isShutdown() {
      return getProxyObject().isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return getProxyObject().isTerminated();
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return getProxyObject().awaitTermination(timeout, unit);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return getProxyObject().submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return getProxyObject().submit(task, result);
   }

   @Override
   public Future<?> submit(Runnable task) {
      return getProxyObject().submit(task);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return getProxyObject().invokeAll(tasks);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      return getProxyObject().invokeAll(tasks, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return getProxyObject().invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return getProxyObject().invokeAny(tasks, timeout, unit);
   }

   @Override
   public void execute(Runnable command) {
      getProxyObject().execute(command);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Future<T> submit(Callable<T> worker, Callback<T> callback) {
      DistributedTask<T> task = new DistributedTask<>(worker);
      addCallback(task, callback);
      return (Future<T>) getProxyObject().submit(task);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Future<T> submitOnKeyOwner(Callable<T> worker, Callback<T> callback, Object key) {
      DistributedTask<T> task = new DistributedTask<>(worker, key);
      addCallback(task, callback);
      return (Future<T>) getProxyObject().submit(task);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Future<T> submitOnMember(Callable<T> worker, Callback<T> callback, Member member) {
      MemberProxy memberProxy = (MemberProxy) member;
      DistributedTask<T> task = new DistributedTask<>(worker, memberProxy.getProxyObject());
      addCallback(task, callback);
      return (Future<T>) getProxyObject().submit(task);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> Future<T> submitOnMembers(Callable<T> worker, Callback<T> callback, Set<Member> members) {
      Set<com.hazelcast.core.Member> xMembers = new HashSet<>();
      for (Member member : members) {
         MemberProxy memberProxy = (MemberProxy) member;
         xMembers.add(memberProxy.getProxyObject());
      }
      DistributedTask<T> task = new DistributedTask<>(worker, xMembers);
      addCallback(task, callback);
      return (Future<T>) getProxyObject().submit(task);
   }

   private <T> void addCallback(final DistributedTask<T> task, final Callback<T> callback) {
      if (callback != null) {
         ExecutionCallback<T> execCallback = new ExecutionCallback<T>() {
            @Override
            public void done(Future<T> future) {
               callback.done(future);
            }
         };
         task.setExecutionCallback(execCallback);
      }
   }
}
