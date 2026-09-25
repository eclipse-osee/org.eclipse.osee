/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 *
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	attributeConflict,
	resolvedConflict,
	conflictResolutionAction,
} from '../types/attribute-conflict.types';
import { mapResolutionsToOperations } from './map-resolutions-to-operations';

type attr = attribute<string, ATTRIBUTETYPEID>;

function makeAttr(id: string, value: string): attr {
	return {
		id,
		typeId: '1000',
		gammaId: '7',
		value,
		name: 'Description',
		storeType: 'String',
		multiplicity: { id: '1', name: 'exactly one' },
	} as unknown as attr;
}

function resolution(
	action: conflictResolutionAction,
	resolvedValues: string[],
	conflictOverride: Partial<attributeConflict> = {}
): resolvedConflict {
	const conflict: attributeConflict = {
		baseAttr: makeAttr('a1', 'base'),
		localValue: 'mine',
		serverAttr: makeAttr('a1', 'theirs'),
		serverDeleted: false,
		allowsMultiple: false,
		...conflictOverride,
	};
	return { conflict, action, resolvedValues };
}

describe('mapResolutionsToOperations', () => {
	it('take-theirs / accept-deletion produce no operations', () => {
		const ops = mapResolutionsToOperations([
			resolution('take-theirs', []),
			resolution('accept-deletion', [], {
				serverAttr: undefined,
				serverDeleted: true,
			}),
		]);
		expect(ops.set).toEqual([]);
		expect(ops.add).toEqual([]);
	});

	it('take-yours overwrites the server attribute with its fresh gamma', () => {
		const ops = mapResolutionsToOperations([
			resolution('take-yours', ['mine']),
		]);
		expect(ops.set).toHaveLength(1);
		expect(ops.set[0].value).toBe('mine');
		expect(ops.set[0].gammaId).toBe('7'); // server's gamma preserved for concurrency safety
		expect(ops.add).toEqual([]);
	});

	it('manual overwrites the server attribute with the manual value', () => {
		const ops = mapResolutionsToOperations([
			resolution('manual', ['merged value']),
		]);
		expect(ops.set).toHaveLength(1);
		expect(ops.set[0].value).toBe('merged value');
	});

	it('re-add creates a new instance from the base attr', () => {
		const ops = mapResolutionsToOperations([
			resolution('re-add', ['restored'], {
				serverAttr: undefined,
				serverDeleted: true,
			}),
		]);
		expect(ops.set).toEqual([]);
		expect(ops.add).toHaveLength(1);
		expect(ops.add[0].id).toBe('-1'); // sentinel: new/unpersisted
		expect(ops.add[0].gammaId).toBe('-1');
		expect(ops.add[0].value).toBe('restored');
	});

	it('take-both keeps the server value and adds the local value as a new instance', () => {
		const ops = mapResolutionsToOperations([
			resolution('take-both', ['theirs', 'mine'], {
				allowsMultiple: true,
			}),
		]);
		expect(ops.set).toEqual([]);
		expect(ops.add).toHaveLength(1);
		// resolvedValues[1] is the local value added as a second instance.
		expect(ops.add[0].value).toBe('mine');
		expect(ops.add[0].id).toBe('-1');
	});

	it('take-yours with no serverAttr produces nothing (defensive)', () => {
		const ops = mapResolutionsToOperations([
			resolution('take-yours', ['mine'], { serverAttr: undefined }),
		]);
		expect(ops.set).toEqual([]);
		expect(ops.add).toEqual([]);
	});

	it('aggregates a mix of actions', () => {
		const ops = mapResolutionsToOperations([
			resolution('take-yours', ['a']),
			resolution('take-theirs', []),
			resolution('re-add', ['b'], {
				serverAttr: undefined,
				serverDeleted: true,
			}),
		]);
		expect(ops.set).toHaveLength(1);
		expect(ops.add).toHaveLength(1);
	});
});
