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
import { Injectable } from '@angular/core';

/**
 * Tracks unsaved (pending) attribute values for a single editor, keyed by
 * attribute instance id. Snapshotted during conflict detection so the newest
 * local value for each edited attribute is available to compare against server
 * state.
 *
 * Not provided in root: provide it at the editor component level so each open
 * editor has its own isolated set of pending values.
 */
@Injectable()
export class PendingAttributeValuesService {
	private readonly pendingValues = new Map<string, string>();

	/** Records a pending (unsaved) value for an attribute instance. */
	set(attrId: string, value: string) {
		this.pendingValues.set(attrId, value);
	}

	/** Retrieves the pending value for an attribute, or undefined if clean. */
	get(attrId: string): string | undefined {
		return this.pendingValues.get(attrId);
	}

	/** Removes a tracked pending value (e.g., after save or revert). */
	remove(attrId: string) {
		this.pendingValues.delete(attrId);
	}

	/** Returns all pending values as a Map (for snapshotting during conflict detection). */
	getAll(): ReadonlyMap<string, string> {
		return this.pendingValues;
	}

	/** Clears all tracked pending values. */
	clear() {
		this.pendingValues.clear();
	}

	/** Whether any pending values exist. */
	hasPending(): boolean {
		return this.pendingValues.size > 0;
	}
}
