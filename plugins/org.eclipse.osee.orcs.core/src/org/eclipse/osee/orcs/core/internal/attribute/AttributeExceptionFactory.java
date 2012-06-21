package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;

public interface AttributeExceptionFactory {

   MultipleAttributesExist createManyExistException(IAttributeType typeSearched, int count);

   AttributeDoesNotExist createDoesNotExistException(IAttributeType typeSearched);

}