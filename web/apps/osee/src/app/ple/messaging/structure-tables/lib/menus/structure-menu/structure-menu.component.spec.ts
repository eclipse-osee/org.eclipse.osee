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

import { StructureMenuComponent } from './structure-menu.component';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	CurrentStateServiceMock,
	structuresMock,
} from '@osee/messaging/shared/testing';

describe('StructureMenuComponent', () => {
	let component: StructureMenuComponent;
	let fixture: ComponentFixture<StructureMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [StructureMenuComponent],
			providers: [
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(StructureMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('menuData', {
			x: '0',
			y: '0',
			url: '',
			header: 'name',
			isInDiff: false,
			open: false,
			structure: structuresMock[0],
		});
		fixture.componentRef.setInput('isEditing', true);
		fixture.componentRef.setInput('breadCrumb', '');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
