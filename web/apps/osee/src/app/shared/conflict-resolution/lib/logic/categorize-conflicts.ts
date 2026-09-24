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
import { ATTRIBUTETYPEID, MULTIPLICITY_ID } from '@osee/attributes/constants';
import { attributeConflict } from '../types/attribute-conflict.types';

/**
 * The outcome of comparing pending local edits against freshly fetched server
 * state for a single entity (artifact/workflow).
 */
export type conflictCategorization = {
	/** Genuine conflicts that require the user to choose a resolution. */
	conflicts: attributeConflict[];
	/**
	 * Edits that are safe to save without prompting: the server did not touch
	 * these attributes, so the local value can be written against the server's
	 * current gamma. Each carries the server attr merged with the local value.
	 */
	autoSaveAttrs: attribute<string, ATTRIBUTETYPEID>[];
};

/**
 * How to match pending edits against base/server attributes. The pending map's
 * keys must be produced by the same {@link keyOf} used here.
 *
 * Default keys by attribute instance `id` (correct for editors that track live,
 * persisted instances). Editors whose edits are keyed by attribute `typeId`
 * (e.g. batched, type-definition-sourced forms where instances may not yet
 * exist) should pass `{ keyOf: (a) => a.typeId }`.
 */
export type conflictKeyOptions = {
	keyOf?: (attr: attribute<string, ATTRIBUTETYPEID>) => string;
};

/**
 * Pure comparison of pending local edits against the latest server state.
 *
 * For each pending edit (keyed by attribute instance id) it classifies the
 * attribute as converged, safe-to-save, a value conflict, or a delete conflict:
 *
 * - Converged: server value equals the local value -> nothing to do.
 * - Safe: server value equals the base value (server untouched) -> autoSaveAttrs.
 * - Delete conflict: the attribute is gone from the server while edited locally
 *   -> conflict with `serverDeleted: true` (this is the fix for the previously
 *   silent auto-commit when the server had deleted the attribute).
 * - Value conflict: server and local both diverge from base and differ from each
 *   other -> conflict with the server attr.
 *
 * This is intentionally side-effect free so it can be unit tested and shared by
 * every conflict-aware editor.
 *
 * @param baseAttrs   the attribute set the user started editing from
 * @param serverAttrs the freshly fetched server attribute set
 * @param pendingValues instance id -> unsaved local value
 */
export function categorizeConflicts(
	baseAttrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	serverAttrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	pendingValues: ReadonlyMap<string, string>,
	options?: conflictKeyOptions
): conflictCategorization {
	const keyOf = options?.keyOf ?? ((a) => a.id);
	const conflicts: attributeConflict[] = [];
	const autoSaveAttrs: attribute<string, ATTRIBUTETYPEID>[] = [];

	// Index base/server by the chosen key once, so matching is O(1) per pending
	// entry rather than a linear scan (avoids O(n*m) over larger attribute sets).
	const baseByKey = indexByKey(baseAttrs, keyOf);
	const serverByKey = indexByKey(serverAttrs, keyOf);

	for (const [key, localValue] of pendingValues.entries()) {
		const baseAttr = baseByKey.get(key);
		const serverAttr = serverByKey.get(key);

		// Without a base we cannot reason about divergence; skip defensively.
		if (!baseAttr) {
			continue;
		}

		// The server deleted the attribute while the user was editing it. Model it
		// as an explicit conflict so the user chooses re-add vs accept-deletion --
		// never silently discard or auto-commit.
		if (!serverAttr) {
			conflicts.push({
				baseAttr,
				localValue,
				serverAttr: undefined,
				serverDeleted: true,
				allowsMultiple: allowsMultiple(baseAttr),
			});
			continue;
		}

		const serverValue = serverAttr.value;

		// Local and server converged on the same value -- nothing to do.
		if (serverValue === localValue) {
			continue;
		}

		// Server did not touch this attribute; the user's edit is safe to save
		// using the server's current gamma (no real conflict).
		if (serverValue === baseAttr.value) {
			autoSaveAttrs.push({ ...serverAttr, value: localValue });
			continue;
		}

		// True value conflict: both sides changed this attribute to different values.
		conflicts.push({
			baseAttr,
			localValue,
			serverAttr,
			serverDeleted: false,
			allowsMultiple: allowsMultiple(serverAttr),
		});
	}

	return { conflicts, autoSaveAttrs };
}

function indexByKey(
	attrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	keyOf: (attr: attribute<string, ATTRIBUTETYPEID>) => string
): Map<string, attribute<string, ATTRIBUTETYPEID>> {
	const map = new Map<string, attribute<string, ATTRIBUTETYPEID>>();
	for (const attr of attrs) {
		// First-wins: keep the earliest instance for a key (matches prior find()).
		if (!map.has(keyOf(attr))) {
			map.set(keyOf(attr), attr);
		}
	}
	return map;
}

function allowsMultiple(attr: attribute<string, ATTRIBUTETYPEID>): boolean {
	const multiplicityId = attr.multiplicity?.id;
	return (
		multiplicityId === MULTIPLICITY_ID.ANY ||
		multiplicityId === MULTIPLICITY_ID.AT_LEAST_ONE
	);
}
