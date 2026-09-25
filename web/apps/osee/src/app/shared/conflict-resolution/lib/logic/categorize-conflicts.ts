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
	/**
	 * New instances the user staged locally that are safe to add without
	 * prompting: the server did not add an instance of the same type while the
	 * user was staging, so there is nothing to reconcile. Each carries the
	 * transaction layer's new-instance sentinel (`id`/`gamma` of `-1`) so it can
	 * be committed as an `add` op unchanged. Staged adds that DO collide with a
	 * server-side add of the same type are surfaced as {@link conflicts} instead.
	 */
	stagedAddAttrs: attribute<string, ATTRIBUTETYPEID>[];
	/**
	 * Edits whose local value now equals the server value -- the user and another
	 * user independently set the same value, so there is nothing to save and
	 * nothing to decide. Surfaced (rather than silently dropped) so the dialog can
	 * account for the field with a "you both set the same value" note, matching the
	 * ring the editor still shows. Each carries the server attr merged with the
	 * (equal) local value. Never committed -- the server already holds this value.
	 */
	convergedAttrs: attribute<string, ATTRIBUTETYPEID>[];
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
 * A new attribute instance the user created locally while the artifact was
 * conflicted, awaiting categorization. `key` is a stable client identity for the
 * staged instance (the shared logic never interprets it, only echoes it back on
 * any resulting conflict so the caller can correlate). `attr` is the new instance
 * (new-instance sentinel `id`/`gamma` of `-1`).
 */
export type stagedAddInput = {
	key: string;
	attr: attribute<string, ATTRIBUTETYPEID>;
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
 *   -> conflict with `serverDeleted: true` (never silently auto-commit over a
 *   server-side deletion).
 * - Value conflict: server and local both diverge from base and differ from each
 *   other -> conflict with the server attr.
 *
 * This is intentionally side-effect free so it can be unit tested and shared by
 * every conflict-aware editor.
 *
 * @param baseAttrs   the attribute set the user started editing from
 * @param serverAttrs the freshly fetched server attribute set
 * @param pendingValues instance id -> unsaved local value
 * @param options key-matching options for pending value edits
 * @param stagedAdds new instances created locally while conflicted (see below)
 */
export function categorizeConflicts(
	baseAttrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	serverAttrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	pendingValues: ReadonlyMap<string, string>,
	options?: conflictKeyOptions,
	stagedAdds?: readonly stagedAddInput[]
): conflictCategorization {
	const keyOf = options?.keyOf ?? ((a) => a.id);
	const conflicts: attributeConflict[] = [];
	const autoSaveAttrs: attribute<string, ATTRIBUTETYPEID>[] = [];
	const stagedAddAttrs: attribute<string, ATTRIBUTETYPEID>[] = [];
	const convergedAttrs: attribute<string, ATTRIBUTETYPEID>[] = [];

	// Index base/server by the chosen key once, so matching is O(1) per pending
	// entry rather than a linear scan (avoids O(n*m) over larger attribute sets).
	const baseByKey = indexByKey(baseAttrs, keyOf);
	const serverByKey = indexByKey(serverAttrs, keyOf);

	// Keys that belong to locally-staged new instances. A staged add records a
	// pending value (so an in-place edit before resolving is captured) AND appears
	// in the merged base set, so without this guard the pending-values loop below
	// would mis-classify it -- typically as a "server deleted" conflict, since the
	// server has no instance under the client temp key -- while the staged-adds loop
	// ALSO reports it. Staged adds are handled exclusively by the staged-adds loop.
	const stagedKeys = new Set((stagedAdds ?? []).map((s) => s.key));

	for (const [key, localValue] of pendingValues.entries()) {
		// Staged adds are categorized only by the staged-adds pass below; skip here
		// so a single staged instance is never counted twice.
		if (stagedKeys.has(key)) {
			continue;
		}

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

		// Local and server converged on the same value -- nothing to save or decide,
		// but surface it so the dialog can account for the field (the editor still
		// shows a ring) with a "you both set the same value" note instead of an
		// unexplained flagged field with no dialog entry.
		if (serverValue === localValue) {
			convergedAttrs.push({ ...serverAttr, value: localValue });
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

	// Locally-staged new instances (created while the artifact was conflicted). A
	// staged add is safe to apply unless the server ALSO added an instance of the
	// same type in the meantime -- then the two additions must be reconciled, so we
	// surface a conflict letting the user keep one or both. A "server-side add" is a
	// server instance of the type whose id was NOT present at base; this identifies
	// the actual new instance regardless of the order the server returns instances
	// in (a count-plus-position scheme would depend on that order). Each server add
	// is consumed once, so multiple staged adds of the same type pair with distinct
	// server adds and any surplus staged add falls through as a safe add.
	if (stagedAdds && stagedAdds.length > 0) {
		const serverAddsByType = serverAddsNotAtBase(baseAttrs, serverAttrs);

		for (const { key, attr } of stagedAdds) {
			const serverAdded = serverAddsByType.get(attr.typeId)?.shift();

			if (serverAdded) {
				conflicts.push({
					baseAttr: attr,
					conflictKey: key,
					localValue: `${attr.value}`,
					serverAttr: serverAdded,
					serverDeleted: false,
					allowsMultiple: allowsMultiple(attr),
					stagedAdd: true,
				});
			} else {
				stagedAddAttrs.push(attr);
			}
		}
	}

	return { conflicts, autoSaveAttrs, stagedAddAttrs, convergedAttrs };
}

/**
 * Groups, per type id, the server instances whose ids were not present in the
 * base set -- i.e. instances another user added since the base snapshot. Used to
 * pair locally-staged adds against genuine server-side adds of the same type,
 * independent of the order the server returns instances in.
 */
function serverAddsNotAtBase(
	baseAttrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	serverAttrs: readonly attribute<string, ATTRIBUTETYPEID>[]
): Map<string, attribute<string, ATTRIBUTETYPEID>[]> {
	const baseIds = new Set(baseAttrs.map((a) => a.id));
	const byType = new Map<string, attribute<string, ATTRIBUTETYPEID>[]>();
	for (const attr of serverAttrs) {
		if (baseIds.has(attr.id)) {
			continue;
		}
		const list = byType.get(attr.typeId);
		if (list) {
			list.push(attr);
		} else {
			byType.set(attr.typeId, [attr]);
		}
	}
	return byType;
}

function indexByKey(
	attrs: readonly attribute<string, ATTRIBUTETYPEID>[],
	keyOf: (attr: attribute<string, ATTRIBUTETYPEID>) => string
): Map<string, attribute<string, ATTRIBUTETYPEID>> {
	const map = new Map<string, attribute<string, ATTRIBUTETYPEID>>();
	for (const attr of attrs) {
		// First-wins: keep the earliest instance for a key.
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
