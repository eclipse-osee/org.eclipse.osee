/*********************************************************************
 * Copyright (c) 2026 Boeing
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

/**
 * The config schema version this frontend build supports.
 * The service loads all JSON attribute values from each DispatchConfig artifact,
 * parses each one, and uses the one whose `version` field matches this constant.
 *
 * Bump this when you introduce a breaking config schema change.
 */
export const DISPATCH_CONFIG_VERSION = '1';

/** Artifact type ID for DispatchConfig (CoreArtifactTypes.DispatchConfig). */
export const DISPATCH_CONFIG_ARTIFACT_TYPE_ID = '7226028762153318337';

/** Attribute type ID for Dispatch Config Json (CoreAttributeTypes.DispatchConfigJson). */
export const DISPATCH_CONFIG_JSON_ATTR_TYPE_ID = '6371428937946281743';
