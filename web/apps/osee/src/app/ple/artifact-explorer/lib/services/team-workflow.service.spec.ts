/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { TeamWorkflowService } from './team-workflow.service';
import { ArtifactUiService } from '@osee/shared/services';
import { artifactUiServiceMock } from '@osee/shared/testing';

describe('TeamWorkflowService', () => {
	let service: TeamWorkflowService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: ArtifactUiService, useValue: artifactUiServiceMock },
			],
		});
		service = TestBed.inject(TeamWorkflowService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
