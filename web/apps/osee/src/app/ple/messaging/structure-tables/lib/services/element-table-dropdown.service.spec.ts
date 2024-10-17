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
import { MatDialogModule } from '@angular/material/dialog';
import {
	CurrentStructureService,
	PlatformTypeActionsService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { of } from 'rxjs';
import { ElementTableDropdownService } from './element-table-dropdown.service';

describe('ElementTableDropdownService', () => {
	let service: ElementTableDropdownService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [MatDialogModule],
			providers: [
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
				{
					provide: CurrentStructureService,
					useValue: CurrentStateServiceMock,
				},
				{
					provide: WarningDialogService,
					useValue: warningDialogServiceMock,
				},
				{
					provide: PlatformTypeActionsService,
					useValue: {
						openCopyEditDialog: of(),
						openEnumDialog(_id: string, _editMode: boolean) {
							return of();
						},
					},
				},
			],
		});
		service = TestBed.inject(ElementTableDropdownService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
