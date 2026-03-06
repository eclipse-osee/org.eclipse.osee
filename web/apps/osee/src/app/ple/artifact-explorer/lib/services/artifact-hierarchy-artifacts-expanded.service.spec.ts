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
import { TestBed } from '@angular/core/testing';

import { ArtifactHierarchyArtifactsExpandedService } from './artifact-hierarchy-artifacts-expanded.service';
import { UiService } from '@osee/shared/services';

describe('ArtifactHierarchyArtifactsOpenService', () => {
	let service: ArtifactHierarchyArtifactsExpandedService;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(ArtifactHierarchyArtifactsExpandedService);
		uiService = TestBed.inject(UiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('branch id interactions', () => {
		it('empty expanded artifacts with no id change', () => {
			expect(service['artifactsExpandedStructArray']()).toEqual([]);
		});
		it('empty expanded artifacts with id change', () => {
			uiService.idValue = '8';
			expect(service['artifactsExpandedStructArray']()).toEqual([]);
		});
		it('full expanded artifacts with id change', () => {
			service['artifactsExpandedStructArray'].set([
				{
					artifactId: '1',
					childArtifactIds: ['2'],
				},
			]);
			expect(service['artifactsExpandedStructArray']()).toEqual([
				{
					artifactId: '1',
					childArtifactIds: ['2'],
				},
			]);
			uiService.idValue = '8';
			expect(service['artifactsExpandedStructArray']()).toEqual([]);
		});
		it('full expanded artifacts with no id change', () => {
			service['artifactsExpandedStructArray'].set([
				{
					artifactId: '1',
					childArtifactIds: ['2'],
				},
			]);
			expect(service['artifactsExpandedStructArray']()).toEqual([
				{
					artifactId: '1',
					childArtifactIds: ['2'],
				},
			]);
		});
	});
});
