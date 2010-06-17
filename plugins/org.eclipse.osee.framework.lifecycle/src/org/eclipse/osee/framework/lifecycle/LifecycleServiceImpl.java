package org.eclipse.osee.framework.lifecycle;

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
/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

public class LifecycleServiceImpl implements LifecycleService {

   private final HashCollection<AbstractLifecycleVisitor.Type<?>, LifecycleHandler> handlersByType =
         new HashCollection<AbstractLifecycleVisitor.Type<?>, LifecycleHandler>();

   public LifecycleServiceImpl() {
   }

   public Collection<AbstractLifecycleVisitor.Type<?>> getHandlerTypes() {
      return handlersByType.keySet();
   }

   @SuppressWarnings("unchecked")
   private <H> ArrayList<H> get(AbstractLifecycleVisitor.Type<H> type) {
      return (ArrayList<H>) handlersByType.getValues(type);
   }

   public <H extends LifecycleHandler> IStatus dispatch(IProgressMonitor monitor, AbstractLifecycleVisitor<H> visitor, String sourceId) {
      AbstractLifecycleVisitor.Type<H> type = visitor.getAssociatedType();
      IStatus status = null;
      Collection<H> handlers = get(type);
      if (handlers != null) {
         for (H handler : handlers) {
            status = visitor.dispatch(monitor, handler, sourceId);
            if (!status.isOK()) {
               break;
            }
         }
      }
      return status;
   }

   public int getHandlerCount(AbstractLifecycleVisitor.Type<?> type) {
      ArrayList<?> handlers = get(type);
      return handlers == null ? 0 : handlers.size();
   }

   public <H extends LifecycleHandler> void addHandler(AbstractLifecycleVisitor.Type<H> type, final H handler) throws OseeCoreException {
      Conditions.checkNotNull(type, "handler type");
      Conditions.checkNotNull(handler, "handler");
      handlersByType.put(type, handler);
   }

   public <H extends LifecycleHandler> void removeHandler(AbstractLifecycleVisitor.Type<H> type, final H handler) throws OseeCoreException {
      Conditions.checkNotNull(type, "handler type");
      Conditions.checkNotNull(handler, "handler");
      handlersByType.removeValue(type, handler);
   }
}
