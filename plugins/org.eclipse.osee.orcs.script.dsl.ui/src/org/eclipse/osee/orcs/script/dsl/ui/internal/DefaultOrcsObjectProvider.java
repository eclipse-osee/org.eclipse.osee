/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.ui.internal;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
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
   public Iterable<? extends NamedId> getBranches() {
      List<NamedId> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getBranches());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedId> getArtifactTypes() {
      List<NamedId> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getArtifactTypes());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedId> getAttributeTypes() {
      List<NamedId> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getAttributeTypes());
      }
      return toReturn;
   }

   @Override
   public Iterable<? extends NamedId> getRelationTypes() {
      List<NamedId> toReturn = new ArrayList<>();
      for (IOrcsObjectProvider provider : getProviders()) {
         Iterables.addAll(toReturn, provider.getRelationTypes());
      }
      return toReturn;
   }

   private List<IOrcsObjectProvider> getProviders() {
      return extensions.getObjects();
   }
}