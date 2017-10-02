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
package org.eclipse.osee.framework.lifecycle;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public interface ILifecycleService {

   public <H extends LifecycleHandler> void addHandler(AbstractLifecycleVisitor.Type<H> type, final H handler) ;

   public <H extends LifecycleHandler> void removeHandler(AbstractLifecycleVisitor.Type<H> type, final H handler) ;

   public Collection<AbstractLifecycleVisitor.Type<?>> getHandlerTypes();

   public int getHandlerCount(AbstractLifecycleVisitor.Type<?> type);

   public <H extends LifecycleHandler> IStatus dispatch(IProgressMonitor monitor, AbstractLifecycleVisitor<H> accessPoint, String sourceId);
}
