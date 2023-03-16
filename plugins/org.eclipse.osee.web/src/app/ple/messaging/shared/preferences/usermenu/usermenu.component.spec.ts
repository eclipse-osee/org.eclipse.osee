/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { of } from 'rxjs';

import { UsermenuComponent } from './usermenu.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	EditAuthService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	editAuthServiceMock,
	preferencesUiServiceMock,
} from '@osee/messaging/shared/testing';
import { UiService } from '@osee/shared/services';

describe('UsermenuComponent', () => {
	let component: UsermenuComponent;
	let fixture: ComponentFixture<UsermenuComponent>;
	let loader: HarnessLoader;
	let routeState: UiService;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatMenuModule,
				MatIconModule,
				MatDialogModule,
				MatTooltipModule,
				UsermenuComponent,
			],
			providers: [
				{ provide: EditAuthService, useValue: editAuthServiceMock },
				{
					provide: PreferencesUIService,
					useValue: preferencesUiServiceMock,
				},
			],
			declarations: [],
		}).compileComponents();
		routeState = TestBed.inject(UiService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(UsermenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should open settings dialog', async () => {
		routeState.idValue = '10';
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of({
				branchId: '10',
				allowedHeaders1: [],
				allowedHeaders2: [],
				allHeaders1: [],
				allHeaders2: [],
				editable: true,
				headers1Label: '',
				headers2Label: '',
				headersTableActive: false,
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		let spy = spyOn(component, 'openSettingsDialog').and.callThrough();
		await (
			await loader.getHarness(
				MatMenuItemHarness.with({ text: new RegExp('Settings') })
			)
		).click();
		expect(spy).toHaveBeenCalled();
	});
});
