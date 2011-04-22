package org.eclipse.osee.framework.ui.skynet.render.compare;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface CompareDataCollector {
   void onCompare(CompareData data) throws OseeCoreException;
}