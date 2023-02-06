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
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubElementTableComponent } from './sub-element-table.component';
import {
	CurrentStructureMultiService,
	CurrentStructureService,
	STRUCTURE_SERVICE_TOKEN,
} from '@osee/messaging/shared';
import { MockSubElementTableComponent } from 'src/app/ple/messaging/structure-tables/lib/menus/testing/sub-element-table-dropdown.component.mock';
import {
	CurrentStateServiceMock,
	elementsMock,
} from '@osee/messaging/shared/testing';

describe('SubElementTableComponent', () => {
	let component: SubElementTableComponent;
	let fixture: ComponentFixture<SubElementTableComponent>;
	let loader: HarnessLoader;
	let service: CurrentStructureService;
	let expectedData = [
		{
			beginWord: 'BEGIN',
			endWord: 'END',
			BeginByte: '0',
			EndByte: '32',
			Sequence: 'Sequence',
			ElementName: 'name1',
			Units: 'N/A',
			MinValue: '0',
			MaxValue: '1',
			AlterableAfterCreationValid: false,
			Description: 'A description',
			EnumLiteralsDesc: 'Description of enum literals',
			Notes: 'Notes go here',
			DefaultValue: '0',
			isArray: false,
		},
		{
			beginWord: 'Hello',
			endWord: 'World',
			BeginByte: '0',
			EndByte: '32',
			Sequence: 'Sequence',
			ElementName: 'name2',
			Units: 'N/A',
			MinValue: '0',
			MaxValue: '1',
			AlterableAfterCreationValid: false,
			Description: 'A description',
			EnumLiteralsDesc: 'Description of enum literals',
			Notes: 'Notes go here',
			DefaultValue: '0',
			isArray: false,
		},
	];

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
			declarations: [],
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
		fixture = TestBed.createComponent(SubElementTableComponent);
		component = fixture.componentInstance;
		component.editMode = true;
		component.data = expectedData;
		component.dataSource = new MatTableDataSource(expectedData);
		component.dataSource.filter = 'name1';
		component.filter = 'element: name1';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		expect(component).toBeTruthy();
		expect(component.data === expectedData).toBeTruthy();
		expect(component.filter === 'element: name1').toBeTruthy();
		expect(component.dataSource.filter === 'name1').toBeTruthy();
	});
	it('should update filter on changes', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		component.filter = 'element: name2';
		component.ngOnChanges({
			data: new SimpleChange(expectedData, expectedData, false),
			filter: new SimpleChange('element: name1', 'element: name2', false),
		});
		await fixture.whenStable();
		expect(component.dataSource.filter === 'name2').toBeTruthy();
		expect(component).toBeTruthy();
	});

	/**
	 * Note these tests are disabled. If someone gets the time to re-implement them in
	 * @see {SubElementTableComponent} 's tests, feel free. :)
	 * Pretty painful to do the DI for the MatDialogRef, since standalone components have a different dependency injection style
	 */
	xdescribe('Menu Testing', () => {
		let mEvent: MouseEvent;
		beforeEach(() => {
			mEvent = document.createEvent('MouseEvent');
		});

		it('should open the menu and open the enum dialog', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'openEnumDialog').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			await menu.clickItem({
				text: new RegExp('Open Enumeration Details'),
			});
			expect(spy).toHaveBeenCalled();
		});

		it('should open the menu and dismiss a description', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				service,
				'partialUpdateElement'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and edit a description', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					original: 'abcdef',
					type: 'description',
					return: 'jkl',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				service,
				'partialUpdateElement'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and dismiss a notes popup', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'openNotesDialog').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				TestBed.inject(CurrentStructureMultiService),
				'partialUpdateElement'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Notes') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).not.toHaveBeenCalled();
		});

		it('should open the menu and edit a notes popup', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'openNotesDialog').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					original: 'abcdef',
					type: 'description',
					return: 'jkl',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				service,
				'partialUpdateElement'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Notes') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the remove element dialog', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'removeElement').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				service,
				'removeElementFromStructure'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Remove element from structure'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the delete element dialog', async () => {
			component.openGeneralMenu(mEvent, elementsMock[0], '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'deleteElement').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(service, 'deleteElement').and.callThrough();
			await menu.clickItem({
				text: new RegExp('Delete element globally'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		afterEach(() => {
			component.generalMenuTrigger.closeMenu();
		});
	});
});
