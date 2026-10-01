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
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { resolvedConflict } from '../types/attribute-conflict.types';

/**
 * The concrete attribute operations a set of resolved conflicts translate into:
 * - `set`: existing attribute instances to overwrite (carry the server's fresh
 *   gamma so the write is concurrency-safe).
 * - `add`: new attribute instances to create (sentinel id/gamma of `-1`).
 */
export type resolutionOperations = {
	set: attribute<string, ATTRIBUTETYPEID>[];
	add: attribute<string, ATTRIBUTETYPEID>[];
};

/**
 * Pure translation of the dialog's resolution choices into attribute set/add
 * operations. Single source of truth so every conflict-aware editor applies
 * resolutions identically:
 *
 * - `take-theirs` / `accept-deletion`: discard the local edit (nothing to write;
 *   the caller reloads server state).
 * - `take-yours` / `manual`: overwrite the existing server attribute using its
 *   fresh gamma.
 * - `re-add`: the server deleted the attribute; re-create it with the user's value.
 * - `take-both`: keep the server value and add the local value as a new instance.
 *
 * Value-conflict actions require `serverAttr`; delete-conflict `re-add` uses
 * `baseAttr` for the attribute metadata.
 */
export function mapResolutionsToOperations(
	resolutions: readonly resolvedConflict[]
): resolutionOperations {
	const set: attribute<string, ATTRIBUTETYPEID>[] = [];
	const add: attribute<string, ATTRIBUTETYPEID>[] = [];

	for (const resolution of resolutions) {
		const { serverAttr, baseAttr } = resolution.conflict;

		switch (resolution.action) {
			case 'take-theirs':
			case 'accept-deletion':
				// Discard local edit -- server state stays authoritative.
				break;

			case 'take-yours':
			case 'manual':
				if (serverAttr) {
					set.push({
						...serverAttr,
						value: resolution.resolvedValues[0],
					});
				}
				break;

			case 're-add':
				add.push(newInstance(baseAttr, resolution.resolvedValues[0]));
				break;

			case 'take-both':
				if (serverAttr) {
					add.push(
						newInstance(serverAttr, resolution.resolvedValues[1])
					);
				}
				break;
		}
	}

	return { set, add };
}

/** Builds a new (unpersisted) attribute instance from a template attr and value. */
function newInstance(
	template: attribute<string, ATTRIBUTETYPEID>,
	value: string
): attribute<string, ATTRIBUTETYPEID> {
	return {
		id: '-1' as const,
		typeId: template.typeId,
		gammaId: '-1' as const,
		value,
		name: template.name,
		storeType: template.storeType,
		multiplicity: template.multiplicity,
	};
}
