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
import { CurrentMessageServiceMock } from '../../../mocks/services/CurrentMessageService.mock';
import { CurrentMessagesService } from '../../../services/current-messages.service';

import { UsermenuComponent } from './usermenu.component';

describe('UsermenuComponent', () => {
	let component: UsermenuComponent;
	let fixture: ComponentFixture<UsermenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatMenuModule, MatIconModule, MatDialogModule],
			providers: [
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
			],
			declarations: [UsermenuComponent],
		}).compileComponents();
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

	it('should open a settings dialog', async () => {
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
		let button = await loader.getHarness(
			MatMenuItemHarness.with({ text: new RegExp('Settings') })
		);
		await button.click();
		expect(spy).toHaveBeenCalled();
	});
});
