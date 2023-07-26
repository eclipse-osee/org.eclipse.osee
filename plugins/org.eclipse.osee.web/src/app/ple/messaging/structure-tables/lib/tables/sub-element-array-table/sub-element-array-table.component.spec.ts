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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubElementArrayTableComponent } from './sub-element-array-table.component';
import { MockSubElementTableComponent } from '../../menus/testing/sub-element-table-dropdown.component.mock';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { elementSearch4 } from '@osee/messaging/type-element-search/testing';

describe('SubElementTableComponent', () => {
	let component: SubElementArrayTableComponent;
	let fixture: ComponentFixture<SubElementArrayTableComponent>;
	let loader: HarnessLoader;
	let service: CurrentStructureService;
	let expectedData = elementSearch4[0];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CommonModule,
				MatIconModule,
				MatDialogModule,
				MatTableModule,
				MatTooltipModule,
				MatMenuModule,
				MatFormFieldModule,
				MatInputModule,
				FormsModule,
				NoopAnimationsModule,
				RouterTestingModule,
				HttpClientTestingModule,
				MockSubElementTableComponent,
			],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						paramMap: of(
							convertToParamMap({
								branchId: '10',
								branchType: 'working',
							})
						),
						fragment: of(null),
					},
				},
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
				{
					provide: CurrentStructureService,
					useValue: CurrentStateServiceMock,
				},
			],
		}).compileComponents();
		service = TestBed.inject(CurrentStructureService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementArrayTableComponent);
		component = fixture.componentInstance;
		component.editMode = true;
		component.element = expectedData;
		component.filter = 'element: name1';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		expect(component).toBeTruthy();
		expect(component.element === expectedData).toBeTruthy();
		expect(component.filter === 'element: name1').toBeTruthy();
	});
	it('should update filter on changes', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		component.filter = 'element: name2';
		await fixture.whenStable();
		expect(component).toBeTruthy();
	});

	/**
	 * Note these tests are disabled. If someone gets the time to re-implement them in
	 * @see {SubElementTableComponent} 's tests, feel free. :)
	 * Pretty painful to do the DI for the MatDialogRef, since standalone components have a different dependency injection style
	 */
	// xdescribe('Menu Testing', () => {
	// 	let mEvent: MouseEvent;
	// 	beforeEach(() => {
	// 		mEvent = document.createEvent('MouseEvent');
	// 	});

	// 	it('should open the menu and open the enum dialog', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'openEnumDialog').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		await menu.clickItem({
	// 			text: new RegExp('Open Enumeration Details'),
	// 		});
	// 		expect(spy).toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and dismiss a description', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(
	// 			component,
	// 			'openDescriptionDialog'
	// 		).and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			service,
	// 			'partialUpdateElement'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Description') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and edit a description', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(
	// 			component,
	// 			'openDescriptionDialog'
	// 		).and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of({
	// 				original: 'abcdef',
	// 				type: 'description',
	// 				return: 'jkl',
	// 			}),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			service,
	// 			'partialUpdateElement'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Description') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and dismiss a notes popup', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'openNotesDialog').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			TestBed.inject(CurrentStructureMultiService),
	// 			'partialUpdateElement'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Notes') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).not.toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and edit a notes popup', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'openNotesDialog').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of({
	// 				original: 'abcdef',
	// 				type: 'description',
	// 				return: 'jkl',
	// 			}),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			service,
	// 			'partialUpdateElement'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Notes') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the remove element dialog', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'removeElement').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			service,
	// 			'removeElementFromStructure'
	// 		).and.callThrough();
	// 		await menu.clickItem({
	// 			text: new RegExp('Remove element from structure'),
	// 		});
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the delete element dialog', async () => {
	// 		component.openGeneralMenu(mEvent, elementsMock[0], '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'deleteElement').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(service, 'deleteElement').and.callThrough();
	// 		await menu.clickItem({
	// 			text: new RegExp('Delete element globally'),
	// 		});
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	afterEach(() => {
	// 		component.generalMenuTrigger.closeMenu();
	// 	});
	// });
});
