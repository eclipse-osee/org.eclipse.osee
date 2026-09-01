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
import type { DispatchConfig } from './dispatch.types';

/**
 * Normalizes a raw config object (any known version) into the latest
 * DispatchConfig shape.
 *
 * When a new version is introduced:
 * 1. Add a DispatchConfigVN type to dispatch.types.ts
 * 2. Add it to the AnyDispatchConfig union
 * 3. Add a case here that calls migrateVxToVy(raw)
 * 4. Update DispatchConfig alias to point to the new type
 * 5. Bump DISPATCH_CONFIG_VERSION in dispatch.constants.ts
 */
export function normalizeDispatchConfig(raw: unknown): DispatchConfig {
	const obj = raw as Record<string, unknown>;
	const version = String(obj['version'] ?? '1');

	switch (version) {
		case '1':
			return raw as DispatchConfig;
		default:
			console.warn(
				`[Dispatch] Unknown config version ${version}. Attempting to use as-is.`
			);
			return raw as DispatchConfig;
	}
}
