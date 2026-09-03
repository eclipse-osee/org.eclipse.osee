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

import { ArtifactOperationsContextMenuComponent } from './artifact-operations-context-menu.component';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { artifactHierarchyPathServiceMock } from '../../../testing/artifact-hierarchy-path.service.mock';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../testing/artifact-explorer-http.service.mock';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import type { attribute } from '@osee/attributes/types';
import type { ATTRIBUTETYPEID } from '@osee/attributes/constants';

/** Accessor for the private groupAttributesByType helper (test-only). */
function groupAttributesByType(
	component: ArtifactOperationsContextMenuComponent,
	attributes: attribute<string, ATTRIBUTETYPEID>[]
): { typeId: string; value: string | string[] }[] {
	return (
		component as unknown as {
			groupAttributesByType: (
				a: attribute<string, ATTRIBUTETYPEID>[]
			) => { typeId: string; value: string | string[] }[];
		}
	).groupAttributesByType(attributes);
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

	describe('groupAttributesByType', () => {
		it('emits a scalar value for a single instance of a type', () => {
			const result = groupAttributesByType(component, [
				makeAttr('100', 'md'),
			]);
			expect(result).toEqual([{ typeId: '100', value: 'md' }]);
		});

		it('emits an array value for multiple instances of the same type', () => {
			const result = groupAttributesByType(component, [
				makeAttr('317', 'Unspecified'),
				makeAttr('317', 'Unlimited Rights'),
				makeAttr('317', 'Unspecified'),
			]);
			expect(result).toEqual([
				{
					typeId: '317',
					value: ['Unspecified', 'Unlimited Rights', 'Unspecified'],
				},
			]);
		});

		it('groups mixed types: scalars stay scalar, duplicates become arrays', () => {
			const result = groupAttributesByType(component, [
				makeAttr('100', 'md'),
				makeAttr('317', 'A'),
				makeAttr('317', 'B'),
			]);
			expect(result).toEqual([
				{ typeId: '100', value: 'md' },
				{ typeId: '317', value: ['A', 'B'] },
			]);
		});

		it('skips attributes with a null value', () => {
			const result = groupAttributesByType(component, [
				makeAttr('100', null),
				makeAttr('200', 'keep'),
			]);
			expect(result).toEqual([{ typeId: '200', value: 'keep' }]);
		});
	});
});
