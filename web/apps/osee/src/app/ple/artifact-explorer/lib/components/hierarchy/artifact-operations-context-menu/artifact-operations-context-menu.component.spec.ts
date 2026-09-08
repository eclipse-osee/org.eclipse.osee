/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { ArtifactOperationsContextMenuComponent } from './artifact-operations-context-menu.component';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { artifactHierarchyPathServiceMock } from '../../../testing/artifact-hierarchy-path.service.mock';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../testing/artifact-explorer-http.service.mock';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import type { attribute } from '@osee/attributes/types';
import type { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import type { createChildArtifactDialogData } from '../../../types/artifact-explorer';
import { operationTypeMock } from '../../../testing/artifact-explorer.data.mock';

type splitResult = {
	firstPerType: { typeId: string; value: string }[];
	extras: { typeId: string; value: string }[];
};

/** Accessor for the private splitAttributeInstances helper (test-only). */
function splitAttributeInstances(
	component: ArtifactOperationsContextMenuComponent,
	attributes: attribute<string, ATTRIBUTETYPEID>[]
): splitResult {
	return (
		component as unknown as {
			splitAttributeInstances: (
				a: attribute<string, ATTRIBUTETYPEID>[]
			) => splitResult;
		}
	).splitAttributeInstances(attributes);
}

/** Accessor for the private createArtifactTransaction helper (test-only). */
function createArtifactTransaction(
	component: ArtifactOperationsContextMenuComponent,
	branchId: string,
	data: createChildArtifactDialogData
) {
	return (
		component as unknown as {
			createArtifactTransaction: (
				b: string,
				d: createChildArtifactDialogData
			) => ReturnType<TransactionService['performMutation']>;
		}
	).createArtifactTransaction(branchId, data);
}

const makeAttr = (
	typeId: string,
	value: string | null
): attribute<string, ATTRIBUTETYPEID> =>
	({
		name: 'Attr',
		value,
		typeId: typeId as ATTRIBUTETYPEID,
		id: '-1',
		gammaId: '-1',
		storeType: 'String',
	}) as attribute<string, ATTRIBUTETYPEID>;

describe('ArtifactOperationsContextMenuComponent', () => {
	let component: ArtifactOperationsContextMenuComponent;
	let fixture: ComponentFixture<ArtifactOperationsContextMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ArtifactOperationsContextMenuComponent],
			providers: [
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: ArtifactHierarchyPathService,
					useValue: artifactHierarchyPathServiceMock,
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ArtifactOperationsContextMenuComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('splitAttributeInstances', () => {
		it('puts a single instance of a type in firstPerType with no extras', () => {
			const result = splitAttributeInstances(component, [
				makeAttr('100', 'md'),
			]);
			expect(result.firstPerType).toEqual([
				{ typeId: '100', value: 'md' },
			]);
			expect(result.extras).toEqual([]);
		});

		it('keeps the first same-type instance and routes the rest to extras', () => {
			const result = splitAttributeInstances(component, [
				makeAttr('317', 'Unspecified'),
				makeAttr('317', 'Unlimited Rights'),
				makeAttr('317', 'Unspecified'),
			]);
			expect(result.firstPerType).toEqual([
				{ typeId: '317', value: 'Unspecified' },
			]);
			expect(result.extras).toEqual([
				{ typeId: '317', value: 'Unlimited Rights' },
				{ typeId: '317', value: 'Unspecified' },
			]);
		});

		it('handles mixed types: first of each in firstPerType, duplicates in extras', () => {
			const result = splitAttributeInstances(component, [
				makeAttr('100', 'md'),
				makeAttr('317', 'A'),
				makeAttr('317', 'B'),
			]);
			expect(result.firstPerType).toEqual([
				{ typeId: '100', value: 'md' },
				{ typeId: '317', value: 'A' },
			]);
			expect(result.extras).toEqual([{ typeId: '317', value: 'B' }]);
		});

		it('skips attributes with a null value', () => {
			const result = splitAttributeInstances(component, [
				makeAttr('100', null),
				makeAttr('200', 'keep'),
			]);
			expect(result.firstPerType).toEqual([
				{ typeId: '200', value: 'keep' },
			]);
			expect(result.extras).toEqual([]);
		});

		it('keeps BOTH instances when the same value is selected twice (e.g. Qualification Method = Unspecified x2)', () => {
			const result = splitAttributeInstances(component, [
				makeAttr('317', 'Unspecified'),
				makeAttr('317', 'Unspecified'),
			]);
			// First goes with the create, the duplicate is added afterward —
			// so both persist rather than collapsing to one.
			expect(result.firstPerType).toEqual([
				{ typeId: '317', value: 'Unspecified' },
			]);
			expect(result.extras).toEqual([
				{ typeId: '317', value: 'Unspecified' },
			]);
			expect(result.firstPerType.length + result.extras.length).toBe(2);
		});
	});

	describe('createArtifactTransaction (two-step create + addAttributes)', () => {
		const QUAL = '317';
		const newArtifactId = '200256';

		const dataWith = (
			attributes: attribute<string, ATTRIBUTETYPEID>[]
		): createChildArtifactDialogData => ({
			name: 'New Requirement',
			artifactTypeId: '888',
			parentArtifactId: '200000',
			attributes,
			operationType: operationTypeMock,
		});

		beforeEach(() => {
			// Required inputs used by the post-create tap().
			fixture.componentRef.setInput('artifactId', '200000');
			fixture.componentRef.setInput('parentArtifactId', '200000');
			fixture.detectChanges();
		});

		afterEach(() => {
			// The spy wraps a shared mock object; restore so call counts don't
			// leak between tests.
			vi.restoreAllMocks();
		});

		it('creates once (no extra addAttributes call) when there are no duplicate-type instances', () => {
			const spy = vi
				.spyOn(transactionServiceMock, 'performMutation')
				.mockReturnValue(of(transactionResultMock));

			createArtifactTransaction(
				component,
				'5399376477749356789',
				dataWith([makeAttr(QUAL, 'Unspecified')])
			).subscribe();

			expect(spy).toHaveBeenCalledTimes(1);
			const createBody = spy.mock.calls[0][0];
			expect(createBody.createArtifacts?.[0].attributes).toEqual([
				{ typeId: QUAL, value: 'Unspecified' },
			]);
			expect(createBody.modifyArtifacts ?? []).toEqual([]);
		});

		it('fires a second addAttributes mutation carrying the extra same-value instances', () => {
			// First call (create) returns the new artifact id so step two fires.
			const createResult = {
				...transactionResultMock,
				results: {
					...transactionResultMock.results,
					ids: [newArtifactId],
				},
			};
			const spy = vi
				.spyOn(transactionServiceMock, 'performMutation')
				.mockReturnValueOnce(of(createResult))
				.mockReturnValue(of(transactionResultMock));

			createArtifactTransaction(
				component,
				'5399376477749356789',
				dataWith([
					makeAttr(QUAL, 'Unspecified'),
					makeAttr(QUAL, 'Unspecified'),
				])
			).subscribe();

			expect(spy).toHaveBeenCalledTimes(2);

			// Step 1: create with the first instance only.
			const createBody = spy.mock.calls[0][0];
			expect(createBody.createArtifacts?.[0].attributes).toEqual([
				{ typeId: QUAL, value: 'Unspecified' },
			]);

			// Step 2: add the remaining instance via the non-deduping add path,
			// targeting the newly created artifact id.
			const addBody = spy.mock.calls[1][0];
			expect(addBody.modifyArtifacts).toEqual([
				{
					id: newArtifactId,
					addAttributes: [{ typeId: QUAL, value: 'Unspecified' }],
				},
			]);
		});

		it('does NOT fire the second mutation when no artifact id comes back even if extras exist', () => {
			// ids stays [] (transactionResultMock default) -> no newArtifactId.
			const spy = vi
				.spyOn(transactionServiceMock, 'performMutation')
				.mockReturnValue(of(transactionResultMock));

			createArtifactTransaction(
				component,
				'5399376477749356789',
				dataWith([
					makeAttr(QUAL, 'Unspecified'),
					makeAttr(QUAL, 'Unspecified'),
				])
			).subscribe();

			expect(spy).toHaveBeenCalledTimes(1);
		});
	});
});
