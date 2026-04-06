/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import { ArtifactHierarchyPathService } from './artifact-hierarchy-path.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import { UiService } from '@osee/shared/services';

describe('ArtifactHierarchyPathService', () => {
	let service: ArtifactHierarchyPathService;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(ArtifactHierarchyPathService);
		uiService = TestBed.inject(UiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('Test resets', () => {
		it('should initialize to empty string', () => {
			expect(service['_selectedArtifactId']()).toEqual('');
		});
		it('should reset when branch id changes', () => {
			expect(service['_selectedArtifactId']()).toEqual('');
			service['_selectedArtifactId'].set('abcd');
			expect(service['_selectedArtifactId']()).toEqual('abcd');
			uiService.idValue = '8';
			expect(service['_selectedArtifactId']()).toEqual('');
		});
		it('should reset when view id changes', () => {
			expect(service['_selectedArtifactId']()).toEqual('');
			service['_selectedArtifactId'].set('abcd');
			expect(service['_selectedArtifactId']()).toEqual('abcd');
			uiService.ViewId = '1';
			expect(service['_selectedArtifactId']()).toEqual('');
		});
		it('should reset when both branch id and view id changes', () => {
			expect(service['_selectedArtifactId']()).toEqual('');
			service['_selectedArtifactId'].set('abcd');
			expect(service['_selectedArtifactId']()).toEqual('abcd');
			uiService.idValue = '8';
			uiService.ViewId = '1';
			expect(service['_selectedArtifactId']()).toEqual('');
		});
	});
});
