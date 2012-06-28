package org.eclipse.osee.framework.skynet.core.attribute.service;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 *
 */
public interface AttributeAdapter<T> {
   T adapt(Attribute<?> attribute, Identity<String> identity) throws OseeCoreException;

   Collection<IAttributeType> getSupportedTypes();
}