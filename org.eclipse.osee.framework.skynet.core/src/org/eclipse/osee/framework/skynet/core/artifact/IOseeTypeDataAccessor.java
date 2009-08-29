package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IOseeTypeDataAccessor {

   public void ensureArtifactTypePopulated() throws OseeCoreException;

   public void ensureTypeValidityPopulated() throws OseeCoreException;

   public void storeValidity(List<Object[]> datas) throws OseeCoreException;

   public void storeTypeInheritance(List<Object[]> datas) throws OseeCoreException;

   public void storeArtifactType(ArtifactType... artifactType) throws OseeCoreException;

   public ArtifactType createArtifactType(String guid, boolean isAbstract, String name) throws OseeCoreException;
}
