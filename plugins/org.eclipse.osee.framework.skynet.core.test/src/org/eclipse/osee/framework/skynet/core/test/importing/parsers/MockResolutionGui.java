/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.importing.parsers;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate.ContentType;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate.IConflictResolvingGui;

public final class MockResolutionGui implements IConflictResolvingGui {

   private ContentType resultingEnum;

   public MockResolutionGui() {
      this.resultingEnum = null;
   }

   public MockResolutionGui(ContentType resultingEnum) {
      this.resultingEnum = resultingEnum;
   }

   public void setMockUserAnswer(ContentType resultingEnum) {
      this.resultingEnum = resultingEnum;
   }

   @Override
   public ContentType determineContentType(Collection<String> paramList) {
      return resultingEnum;
   }
}
