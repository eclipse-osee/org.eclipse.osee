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
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { MatTooltipModule } from '@angular/material/tooltip';
import { of } from 'rxjs';
import { editAuthServiceMock } from '../../../../shared/testing/edit-auth.service.mock';
import { CurrentStateServiceMock } from '../../../../shared/testing/current-structure.service.mock';
import { EditAuthService } from '../../../../shared/services/public/edit-auth-service.service';
import { CurrentStructureMultiService } from '../../../../shared/services/ui/current-structure-multi.service';
import { CurrentStructureService } from '../../../../shared/services/ui/current-structure.service';
import { preferencesUiServiceMock } from '../../../../shared/services/ui/preferences-ui-service.mock';
import { PreferencesUIService } from '../../../../shared/services/ui/preferences-ui.service';
import { MULTI_STRUCTURE_SERVICE } from '../../../../shared/tokens/injection/structure/multi';
import { STRUCTURE_SERVICE_TOKEN } from '../../../../shared/tokens/injection/structure/token';

import { UsermenuComponent } from './usermenu.component';

describe('UsermenuComponent', () => {
	let component: UsermenuComponent;
	let fixture: ComponentFixture<UsermenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatDialogModule, MatTooltipModule],
			providers: [
				{
					provide: EditAuthService,
					useValue: editAuthServiceMock,
				},
			],
		})
			.overrideComponent(UsermenuComponent, {
				set: {
					providers: [
						{
							provide: PreferencesUIService,
							useValue: preferencesUiServiceMock,
						},
						{
							provide: CurrentStructureService,
							useValue: CurrentStateServiceMock,
						},
						MULTI_STRUCTURE_SERVICE,
					],
				},
			})
			.compileComponents();
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
		const spy = spyOn(component, 'openSettingsDialog').and.callThrough();
		const button = await loader.getHarness(
			MatMenuItemHarness.with({ text: new RegExp('Settings') })
		);
		await button.click();
		expect(spy).toHaveBeenCalled();
	});
});
