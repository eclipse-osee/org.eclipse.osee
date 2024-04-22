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

import { PlatformTypeActionsService } from './platform-type-actions.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {
	warningDialogServiceMock,
	enumerationUiServiceMock,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { WarningDialogService } from './warning-dialog.service';
import { EnumerationUIService } from './enumeration-ui.service';
import { TypesService } from '../http/types.service';

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
				{ provide: TypesService, useValue: typesServiceMock },
			],
		});
		service = TestBed.inject(PlatformTypeActionsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
