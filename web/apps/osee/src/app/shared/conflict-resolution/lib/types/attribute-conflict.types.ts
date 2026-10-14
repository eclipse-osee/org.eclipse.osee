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
import { Observable } from 'rxjs';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

/**
 * A single detected attribute conflict: the user has an unsaved local value that
 * diverges from the value (or existence) the server now reports.
 *
 * Two shapes are represented via {@link serverDeleted}:
 * - value conflict ({@link serverDeleted} false): both sides changed the attribute
 *   to different values. {@link serverAttr} carries the server's current value/gamma.
 * - delete conflict ({@link serverDeleted} true): the server removed the attribute
 *   while the user was editing it. {@link serverAttr} is undefined.
 */
export type attributeConflict = {
	/** The attribute as it existed before the local edit (base state). */
	baseAttr: attribute<string, ATTRIBUTETYPEID>;
	/** The user's unsaved local value. */
	localValue: string;
	/**
	 * Stable identity for this conflict, used to reconcile a re-categorized live
	 * snapshot against the open dialog's in-progress selections. Defaults to the
	 * base attribute instance id. Set explicitly for conflicts whose base carries
	 * the new-instance sentinel (`id` of `-1`) -- e.g. a staged add colliding with
	 * a server-side add of the same type -- so multiple such conflicts do not all
	 * key to `-1` and clobber one another.
	 */
	conflictKey?: string;
	/**
	 * The server's current value (from the remote change). Undefined when the
	 * server deleted the attribute ({@link serverDeleted} is then true).
	 */
	serverAttr?: attribute<string, ATTRIBUTETYPEID>;
	/** True when the server deleted this attribute while the user was editing it. */
	serverDeleted: boolean;
	/** Whether this attribute type allows multiple instances (enables "take both"). */
	allowsMultiple: boolean;
	/**
	 * True when this conflict is a locally-staged NEW instance colliding with a
	 * server-side add of the same type (rather than a divergent edit of an existing
	 * instance). Here {@link baseAttr} is the user's staged instance (new-instance
	 * sentinel id/gamma). Only "take server's" (discard the staged add) and, when
	 * {@link allowsMultiple}, "take both" (add the staged instance alongside the
	 * server's) are meaningful -- there is no existing instance to overwrite in
	 * place, so "take yours"/"manual" are not offered.
	 */
	stagedAdd?: boolean;
};

/**
 * The resolution action chosen by the user for a single conflict.
 *
 * `re-add` applies only to delete conflicts: it re-creates the attribute with the
 * user's local value. `accept-deletion` is the delete-conflict counterpart of
 * `take-theirs` (accept the server's removal, discard the local edit).
 */
export type conflictResolutionAction =
	| 'take-yours'
	| 'take-theirs'
	| 'take-both'
	| 'manual'
	| 're-add'
	| 'accept-deletion';

/**
 * A single resolved conflict with the chosen action and resulting value(s).
 */
export type resolvedConflict = {
	/** The original conflict entry. */
	conflict: attributeConflict;
	/** Which action the user chose. */
	action: conflictResolutionAction;
	/**
	 * The final value(s) to persist.
	 * - take-yours / manual / re-add: [chosenValue]
	 * - take-theirs / accept-deletion: [] (discard local; caller reloads server state)
	 * - take-both: [serverValue, localValue] (new instance for localValue)
	 */
	resolvedValues: string[];
};

/**
 * A changed attribute that requires no user decision: the server did not touch it,
 * so the user's edit will be saved as-is. Shown read-only in the dialog so the user
 * sees a complete account of what will be persisted (not just the true conflicts).
 */
export type autoResolvedChange = {
	/** Display name of the attribute. */
	name: string;
	/** The user's value that will be saved. */
	localValue: string;
};

/**
 * A re-categorized snapshot pushed to an OPEN dialog when the server state changes
 * again while the user is resolving (another user committed once more). Lets the
 * dialog update the shown server values, auto-resolved list, and fresh gammas in
 * place instead of applying against a stale snapshot.
 */
export type liveConflictUpdate = {
	/** The freshly re-categorized conflicts (with current server values + gammas). */
	conflicts: attributeConflict[];
	/** The freshly re-categorized non-conflicting edits. */
	autoResolved: autoResolvedChange[];
	/** The freshly re-categorized staged additions that will be added without a decision. */
	stagedAdds: autoResolvedChange[];
	/** The freshly re-categorized converged edits (same value set by both users). */
	converged: autoResolvedChange[];
};

/**
 * Data passed into the conflict resolution dialog.
 */
export type attributeConflictResolutionDialogData = {
	/** The conflicts to resolve (interactive). */
	conflicts: attributeConflict[];
	/**
	 * Changed attributes that will be saved without a decision (server untouched).
	 * Shown read-only so the dialog accounts for every changed field, matching the
	 * fields flagged in the editor. Empty when there are none.
	 */
	autoResolved: autoResolvedChange[];
	/**
	 * New instances the user staged while conflicted that will be added without a
	 * decision (the server did not add the same type). Shown read-only so the
	 * dialog accounts for staged additions too, not just edits. Empty when none.
	 */
	stagedAdds: autoResolvedChange[];
	/**
	 * Edits where the user and another user independently set the SAME value. There
	 * is nothing to save (the server already holds the value) and nothing to decide,
	 * but the field is shown read-only so the dialog accounts for the ring the
	 * editor still displays. Empty when none.
	 */
	converged: autoResolvedChange[];
	/** The owning entity name (artifact/workflow) for display. */
	entityName: string;
	/** Owning entity ID (needed by widgets like the markdown editor for uploads). */
	entityId: string;
	/**
	 * Optional live stream of re-categorized conflicts, emitted by the service when a
	 * further remote change lands while the dialog is open. When provided, the dialog
	 * updates its displayed server values/gammas in place and re-defaults any row whose
	 * server value changed (flagging it so the user re-decides). Absent = static dialog.
	 */
	liveUpdates$?: Observable<liveConflictUpdate>;
};

/**
 * Result returned from the conflict resolution dialog. Undefined if cancelled.
 */
export type attributeConflictResolutionDialogResult = {
	/** The resolution for each conflict. */
	resolutions: resolvedConflict[];
};
