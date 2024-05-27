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

import { MatDialogModule } from '@angular/material/dialog';
import {
	enumerationUiServiceMock,
	typesUIServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { EnumerationUIService } from './enumeration-ui.service';
import { PlatformTypeActionsService } from './platform-type-actions.service';
import { TypesUIService } from './types-ui.service';
import { WarningDialogService } from './warning-dialog.service';

describe('PlatformTypeActionsService', () => {
	let service: PlatformTypeActionsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [MatDialogModule],
			providers: [
				{
					provide: WarningDialogService,
					useValue: warningDialogServiceMock,
				},
				{
					provide: EnumerationUIService,
					useValue: enumerationUiServiceMock,
				},
				{ provide: TypesUIService, useValue: typesUIServiceMock },
			],
		});
		service = TestBed.inject(PlatformTypeActionsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
