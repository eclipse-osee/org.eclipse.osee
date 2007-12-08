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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Creates a PriorityQueue of fixed size. Once the queue has become full, attempts to add elements will result in the
 * least (or smallest) element being removed from the queue.
 * 
 * @author David Diepenbrock
 */
public class FixedSizePriorityQueue<E> extends PriorityQueue<E> {

   private static final long serialVersionUID = 6408445028193363641L;

   private final int qSize;

   public FixedSizePriorityQueue(int size, Comparator<E> comparator) {
      super(size, comparator);
      this.qSize = size;
   }

   public FixedSizePriorityQueue(int size) {
      super(size);
      this.qSize = size;
   }

   public FixedSizePriorityQueue(int size, E initialElement) {
      this(size);
      this.add(initialElement);
   }

   @Override
   public boolean add(E o) {
      return this.offer(o);
   }

   @Override
   public boolean offer(E o) {
      if (size() >= qSize) this.poll();
      return super.offer(o);
   }

}
