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

import { ArtifactUiService } from './artifact-ui.service';
import { ArtifactService } from '../http/artifact.service';
import { artifactServiceMock } from '../http/artifact.service.mock';

describe('ArtifactUiService', () => {
	let service: ArtifactUiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: ArtifactService, useValue: artifactServiceMock },
			],
		});
		service = TestBed.inject(ArtifactUiService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
