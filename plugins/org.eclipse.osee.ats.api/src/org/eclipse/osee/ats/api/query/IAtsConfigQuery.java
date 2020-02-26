/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigQuery {

   IAtsConfigQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption);

   <T extends IAtsConfigObject> ResultSet<T> getConfigObjectResultSet();

   Collection<ArtifactId> getIds();

   <T extends IAtsConfigObject> Collection<T> getConfigObjects();

   <T extends IAtsConfigObject> Collection<T> getItems(Class<T> clazz);

   IAtsConfigQuery isOfType(ArtifactTypeToken... artifactType);

   IAtsConfigQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOptions);

   IAtsConfigQuery andId(ArtifactId id);

   IAtsConfigQuery andProgram(IAtsProgram program);

   IAtsConfigQuery andProgram(Long id);

   IAtsConfigQuery andWorkType(WorkType workType, WorkType... workTypes);

   IAtsConfigQuery andWorkType(Collection<WorkType> workTypes);

   IAtsConfigQuery andCsci(Collection<String> cscis);

   <T extends ArtifactToken> ResultSet<T> getArtifactResultSet();

   IAtsConfigQuery andName(String name);

   IAtsConfigQuery andTag(String... tags);

   IAtsConfigQuery andActive(boolean active);

   <T extends IAtsConfigObject> T getOneOrNull(Class<T> clazz);

   <T extends IAtsConfigObject> T getAtMostOneOrNull(Class<T> clazz);

   <T extends IAtsConfigObject> T getExactlyOne(Class<T> clazz);

   <T extends IAtsConfigObject> T getOneOrDefault(Class<T> clazz, T defaultValue);

   <T extends ArtifactToken> Collection<T> getArtifacts();

   IAtsConfigQuery isActive();

}
