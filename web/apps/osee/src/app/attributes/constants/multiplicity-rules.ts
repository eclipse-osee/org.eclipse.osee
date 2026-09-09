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
import { MULTIPLICITY_ID } from './multiplicity-id';

/**
 * Minimal structural shape needed to reason about an attribute's multiplicity.
 * Both the in-memory create-dialog attributes and the persisted
 * artifact-editor attributes satisfy this, so the deletion rules can be
 * shared. Kept structural (not importing the full `attribute` type) so this
 * module has no dependency on `@osee/attributes/types`.
 */
type MultiplicityAttribute = {
	name?: string;
	readonly typeId: string;
	multiplicity?: { id?: string };
};

/**
 * True when the attribute type requires at least one instance
 * (EXACTLY_ONE or AT_LEAST_ONE multiplicity).
 */
export function isRequiredMultiplicity(attr: MultiplicityAttribute): boolean {
	const id = attr.multiplicity?.id;
	return (
		id === MULTIPLICITY_ID.EXACTLY_ONE ||
		id === MULTIPLICITY_ID.AT_LEAST_ONE
	);
}

/**
 * Whether a single attribute instance may be deleted, shared by the create
 * dialog and the artifact editor so the rule stays consistent:
 *
 * - The Name attribute is never deletable.
 * - Optional types (ANY / ZERO_OR_ONE) are always deletable.
 * - Required types (EXACTLY_ONE / AT_LEAST_ONE) are deletable only when more
 *   than one instance of that type exists (the minimum count must remain).
 *
 * @param attr the instance being considered for deletion
 * @param allAttributes every attribute currently on the artifact/working set,
 *   used to count how many instances of the same type exist
 */
export function isAttributeInstanceDeletable(
	attr: MultiplicityAttribute,
	allAttributes: readonly MultiplicityAttribute[]
): boolean {
	if (attr.name?.toLowerCase() === 'name') {
		return false;
	}
	if (!isRequiredMultiplicity(attr)) {
		return true;
	}
	const instanceCount = allAttributes.filter(
		(a) => a.typeId === attr.typeId
	).length;
	return instanceCount > 1;
}
