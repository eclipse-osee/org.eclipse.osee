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

import { ArtifactHierarchyOptionsService } from './artifact-hierarchy-options.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ArtifactHierarchyOptionsService', () => {
	let service: ArtifactHierarchyOptionsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(ArtifactHierarchyOptionsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
