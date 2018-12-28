/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.ui.internal;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.orcs.script.dsl.ui.IOrcsObjectProvider;

/**
 * @author Roberto E. Escobar
 */
public class DefaultOrcsObjectProvider implements IOrcsObjectProvider {

   private static final String EXTENSION_POINT_ID = "org.eclipse.osee.orcs.script.dsl.ui.OrcsObjectProvider";
   private static final String EXTENSION_ELEMENT = "OrcsObjectProvider";
   private static final String CLASSNAME = "classname";

   private final ExtensionDefinedObjects<IOrcsObjectProvider> extensions =
      new ExtensionDefinedObjects<>(EXTENSION_POINT_ID, EXTENSION_ELEMENT, CLASSNAME, true);

   @Override
   public Iterable<? extends NamedIdBase> getBranches() {
      List<NamedIdBase> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getBranches());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedIdBase> getArtifactTypes() {
      List<NamedIdBase> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getArtifactTypes());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedIdBase> getAttributeTypes() {
      List<NamedIdBase> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getAttributeTypes());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedIdBase> getRelationTypes() {
      List<NamedIdBase> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getRelationTypes());
      }
      return toReturn;
   }

   private List<IOrcsObjectProvider> getProviders() {
      return extensions.getObjects();
   }
}