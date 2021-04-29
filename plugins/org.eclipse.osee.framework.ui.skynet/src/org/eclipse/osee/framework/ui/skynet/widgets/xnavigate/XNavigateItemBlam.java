/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {
   private final IBlamProvider blamProvider;

   public XNavigateItemBlam(XNavigateItem parent, AbstractBlam blamOperation) {
      this(parent, blamOperation, FrameworkImage.BLAM);
   }

   public XNavigateItemBlam(XNavigateItem parent, final AbstractBlam blamOperation, KeyedImage keyedImage) {
      super(parent, blamOperation.getName(), keyedImage);
      blamProvider = new IBlamProvider() {

         @Override
         public AbstractBlam getBlam() {
            return blamOperation;
         }
      };
   }

   public XNavigateItemBlam(XNavigateItem parent, final AbstractBlam blamOperation, OseeImage oseeImage) {
      super(parent, blamOperation.getName(), ImageManager.create(oseeImage));
      blamProvider = new IBlamProvider() {

         @Override
         public AbstractBlam getBlam() {
            return blamOperation;
         }
      };
   }

   public XNavigateItemBlam(XNavigateItem parent, IBlamProvider blamProvider, String name, OseeImage oseeImage) {
      super(parent, name, ImageManager.create(oseeImage));
      this.blamProvider = blamProvider;
   }

   public XNavigateItemBlam(XNavigateItem parent, IBlamProvider blamProvider, String name, KeyedImage keyedImage) {
      super(parent, name, keyedImage);
      this.blamProvider = blamProvider;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      // Need a new copy of the BLAM operation so widgets don't collide
      BlamEditor.edit(blamProvider.getBlam());
   }
}