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
import { StagedAttributeService } from './staged-attribute.service';

type attr = attribute<string, ATTRIBUTETYPEID>;

function newAttr(value: string, typeId = 'T1'): attr {
	return {
		id: '-1',
		gammaId: '-1',
		typeId,
		value,
		name: 'Description',
		storeType: 'String',
	} as unknown as attr;
}

describe('StagedAttributeService', () => {
	let service: StagedAttributeService;

	beforeEach(() => {
		service = new StagedAttributeService();
	});

	it('generates unique, stable client keys', () => {
		expect(service.nextKey()).toBe('TEMP-1');
		expect(service.nextKey()).toBe('TEMP-2');
		expect(service.nextKey()).toBe('TEMP-3');
	});

	it('stages instances and exposes them reactively and as a snapshot', () => {
		const k1 = service.nextKey();
		const k2 = service.nextKey();
		service.add(k1, newAttr('one'));
		service.add(k2, newAttr('two', 'T2'));

		expect(service.stagedAdds().map((s) => s.stagedKey)).toEqual([k1, k2]);
		expect(service.getAll().map((s) => s.attr.value)).toEqual([
			'one',
			'two',
		]);
		// The stored instance keeps the add sentinel so it commits as an add op.
		expect(service.getAll()[0].attr.id).toBe('-1');
	});

	it('preserves insertion order across the reactive and snapshot views', () => {
		const k1 = service.nextKey();
		const k2 = service.nextKey();
		service.add(k2, newAttr('second'));
		service.add(k1, newAttr('first'));
		// Insertion order (k2 then k1), not key order.
		expect(service.stagedAdds().map((s) => s.stagedKey)).toEqual([k2, k1]);
	});

	it('reports membership and removes a single staged instance', () => {
		const k1 = service.nextKey();
		const k2 = service.nextKey();
		service.add(k1, newAttr('one'));
		service.add(k2, newAttr('two'));

		expect(service.has(k1)).toBe(true);
		expect(service.has('TEMP-99')).toBe(false);

		service.remove(k1);
		expect(service.has(k1)).toBe(false);
		expect(service.stagedAdds().map((s) => s.stagedKey)).toEqual([k2]);
	});

	it('remove is a no-op for an unknown key (no needless signal churn)', () => {
		service.add(service.nextKey(), newAttr('one'));
		const before = service.stagedAdds();
		service.remove('TEMP-does-not-exist');
		expect(service.stagedAdds()).toBe(before);
	});

	it('clears all staged instances', () => {
		service.add(service.nextKey(), newAttr('one'));
		service.add(service.nextKey(), newAttr('two'));
		service.clear();
		expect(service.stagedAdds()).toEqual([]);
		expect(service.getAll()).toEqual([]);
	});

	it('clear is a no-op when nothing is staged (no needless signal churn)', () => {
		const before = service.stagedAdds();
		service.clear();
		// Same array reference: the underlying signal was not rewritten.
		expect(service.stagedAdds()).toBe(before);
	});
});
