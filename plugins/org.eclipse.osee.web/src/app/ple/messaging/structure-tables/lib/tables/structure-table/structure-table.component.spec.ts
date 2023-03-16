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
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	ActivatedRoute,
	ActivatedRouteSnapshot,
	RouterLink,
	ROUTES,
	UrlSegment,
} from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { EditStructureFieldComponentMock } from '../../fields/edit-structure-field/edit-structure-field.component.mock';
import { SubElementTableComponentMock } from '../sub-element-table/sub-element-table.component.mock';

import { StructureTableComponent } from './structure-table.component';
import { AsyncPipe, NgClass, NgFor, NgIf, NgStyle } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { TestScheduler } from 'rxjs/testing';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { EditElementFieldComponent } from '../../fields/edit-element-field/edit-element-field.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import type { structure } from '@osee/messaging/shared/types';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { TwoLayerAddButtonHarness } from '@osee/shared/components/testing';
import { TwoLayerAddButtonComponent } from '@osee/shared/components';
import {
	preferencesUiServiceMock,
	CurrentStateServiceMock,
	editAuthServiceMock,
	structuresMock,
	structuresMockWithChanges,
	ViewSelectorMockComponent,
	MessagingControlsMockComponent,
} from '@osee/messaging/shared/testing';
import { CdkVirtualForOf, ScrollingModule } from '@angular/cdk/scrolling';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import {
	PreferencesUIService,
	CurrentStructureMultiService,
	CurrentStructureService,
	EditAuthService,
} from '@osee/messaging/shared/services';
import { MULTI_STRUCTURE_SERVICE } from '@osee/messaging/shared/tokens';

describe('StructureTableComponent', () => {
	let component: StructureTableComponent;
	let fixture: ComponentFixture<StructureTableComponent>;
	let loader: HarnessLoader;
	let scheduler: TestScheduler;
	let dialog: MatDialog;
	const urlFromRoot =
		'ple/messaging/working/2780650236653788489/201282/messages/201297/201301/Test%20Message%203%20>%20test%20submessage%205/elements/diff';
	const urlSegmentsFromRoot = urlFromRoot
		.split('/')
		.map((fragment) => new UrlSegment(fragment, {}));

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				RouterTestingModule.withRoutes([
					{
						path: 'diffOpen',
						outlet: 'rightSideNav',
						component: StructureTableComponent,
					},
					{ path: 'diff', component: StructureTableComponent },
				]),
				NoopAnimationsModule,
			],
			providers: [
				{
					provide: PreferencesUIService,
					useValue: preferencesUiServiceMock,
				},
				{
					provide: CurrentStructureMultiService,
					useValue: CurrentStateServiceMock,
				},
				{
					provide: CurrentStructureService,
					useValue: CurrentStateServiceMock,
				},
				MULTI_STRUCTURE_SERVICE,
				{
					provide: MatDialog,
					useValue: {
						open() {
							return {
								afterClosed() {
									return of({
										id: '',
										name: '',
										structure: { id: '5' },
									});
								},
								close: null,
							};
						},
					},
				},
			],
		})
			.overrideComponent(StructureTableComponent, {
				set: {
					imports: [
						SubElementTableComponentMock,
						EditElementFieldComponent,
						EditStructureFieldComponentMock,
						ViewSelectorMockComponent,
						MockSingleDiffComponent,
						AddElementDialogComponent,
						HighlightFilteredTextDirective,
						NgIf,
						NgFor,
						NgClass,
						NgStyle,
						AsyncPipe,
						RouterLink,
						MatIconModule,
						TwoLayerAddButtonComponent,
						MessagingControlsMockComponent,
						MatFormFieldModule,
						FormsModule,
						MatInputModule,
						MatTableModule,
						MatTooltipModule,
						MatMenuModule,
						MatButtonModule,
						MatDialogModule,
						MatPaginatorModule,
						CdkVirtualForOf,
						ScrollingModule,
						RouterTestingModule,
					],
					providers: [
						{
							provide: ROUTES,
							multi: true,
							useValue: [
								{
									path: 'diffOpen',
									outlet: 'rightSideNav',
									component: StructureTableComponent,
								},
								{
									path: 'diff',
									component: StructureTableComponent,
								},
							],
						},
						{
							provide: EditAuthService,
							useValue: editAuthServiceMock,
						},
						{
							provide: ActivatedRoute,
							useValue: {
								fragment: of(''),
								snapshot: {
									pathFromRoot: [
										new ActivatedRouteSnapshot(),
									],
								},
							},
						},
						{
							provide: MatDialog,
							useValue: {
								open() {
									return {
										afterClosed() {
											return of({
												id: '',
												name: '',
												structure: { id: '5' },
											});
										},
										close: null,
									};
								},
							},
						},
						{
							provide: CurrentStructureMultiService,
							useValue: CurrentStateServiceMock,
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
		dialog = TestBed.inject(MatDialog);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StructureTableComponent);
		component = fixture.componentInstance;
		// component.messageData = new MatTableDataSource<structure>(
		// 	structuresMock
		// );
		component.hasFilter = true;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
	/**
	 * TBD: fix test, need to make a better mock :)
	 */
	// it('should remove element from expandedElement', () => {
	//   // component.expandedElement = [{ id:"1"} as structure, { id:"2"} as structure, { id:"3"} as structure, { id:"4"} as structure, { id:"5"} as structure, { id:"7"} as structure];
	//   scheduler.run(({ expectObservable }) => {
	//     component.expandRow({ id: "1" } as structure);
	//   })
	//   component.hideRow({ id:"4"} as structure);
	//   expect(component.rowIsExpanded("4")).toBeFalsy();
	// });

	// it('should add element from expandedElement', () => {
	//   // component.expandedElement = [{ id:"1"} as structure, { id:"2"} as structure, { id:"3"} as structure, { id:"4"} as structure, { id:"5"} as structure, { id:"7"} as structure];
	//   component.expandRow({ id:"9"} as structure);
	//   expect(component.rowIsExpanded("9")).toBeTruthy();
	// });

	it('should find a truncatedElement', () => {
		component.truncatedSections = ['hello', 'world'];
		let result = component.isTruncated('world');
		expect(result).toBeTruthy();
	});

	it('should not find a truncatedElement', () => {
		component.truncatedSections = ['hello', 'world'];
		let result = component.isTruncated('abcdef');
		expect(result).toBeFalsy();
	});

	it('should filter text', async () => {
		const form = await loader.getHarness(MatFormFieldHarness);
		const control = await form.getControl(MatInputHarness);
		await control?.setValue('Some text');
		expect(
			fixture.componentInstance.filter ===
				'Some text'.trim().toLowerCase()
		).toBeTruthy();
	});

	it('should expand row', () => {
		component.rowChange({ id: '1' } as structure, true);
		expect(component.rowIsExpanded('1')).toBeTruthy();
	});

	/**
	 * TBD: fix test, need to make a better mock :)
	 */
	// it('should open and close a sub table', async () => {
	//   const spy = spyOn(component, 'rowChange').and.callThrough();
	//   //test is bugged right now, don't know what the fix is, will do a raw test instead
	//   // const button = await loader.getHarness(MatButtonHarness.with({ text: 'V' }));
	//   // expect(await button.getText()).toEqual("V");
	//   // await button.click();
	//   // expect(await button.getText()).toEqual("V");
	//   // expect(spy).toHaveBeenCalled();
	//   // await button.click();
	//   // expect(await button.getText()).toEqual("V");
	//   //expect(spy).toHaveBeenCalled();
	//   component.rowChange({ id:"1"} as structure, true);
	//   expect(component.rowIsExpanded("1")).toBeTruthy();
	//   component.rowChange({ id:"1"} as structure, false);
	//   expect(component.expandedElement).toEqual([])
	// });

	it('should open add structure dialog', async () => {
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of({
				id: '10',
				name: 'New Structure',
				structure: {
					id: '10216532',
					name: 'New Structure',
					elements: [],
					description: '',
					interfaceMaxSimultaneity: '',
					interfaceMinSimultaneity: '',
					interfaceTaskFileType: 0,
					interfaceStructureCategory: '',
					numElements: '10',
					sizeInBytes: '10',
					bytesPerSecondMinimum: 10,
					bytesPerSecondMaximum: 10,
				},
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		const spy = spyOn(
			component,
			'openAddStructureDialog'
		).and.callThrough();
		const addmenu = await loader.getHarness(TwoLayerAddButtonHarness);
		await addmenu.toggleOpen();
		expect(addmenu.isOpen()).toBeTruthy();
		await addmenu.clickFirstOption();
		//const button = await loader.getHarness(MatButtonHarness.with({ selector:'#addStructure' }));
		//await button.click();
		expect(spy).toHaveBeenCalled();
	});

	/**
	 * TBD need better mock :)
	 */
	// it('should open add element dialog', async () => {
	//   let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({id:'10',name:'New Structure',structure:{id:'10216532',name:'New Structure',elements:[],description:'',interfaceMaxSimultaneity:'',interfaceMinSimultaneity:'',interfaceTaskFileType:0,interfaceStructureCategory:'',numElements:'10',sizeInBytes:'10',bytesPerSecondMinimum:10,bytesPerSecondMaximum:10}}), close: null });
	//   let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy)
	//   const spy = spyOn(component, 'openAddElementDialog').and.callThrough();
	//   component.rowChange({ id: "1", name: 'dummy element' } as structure, true);
	//   const addmenu = await loader.getHarness(TwoLayerAddButtonHarness);
	//   await addmenu.toggleOpen();
	//   expect(await (await addmenu.getNestedButtons()).length).toEqual(1);
	//   expect(addmenu.isOpen()).toBeTruthy();
	//   //await addmenu.clickItem({ text: "Add element to dummy element description" });
	//   //await addmenu.clickItem({ text: new RegExp("Add element") });
	//   await addmenu.clickItem();
	//   expect(spy).toHaveBeenCalled();
	// })

	describe('Menu Testing', () => {
		let mEvent: MouseEvent;
		beforeEach(() => {
			mEvent = document.createEvent('MouseEvent');
		});

		it('should open the menu and dismiss a description', async () => {
			component.openMenu(
				mEvent,
				structuresMock[0].id,
				structuresMock[0].name,
				structuresMock[0].description,
				structuresMock[0],
				'',
				'true'
			);
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
			let dialogSpy = spyOn(dialog, 'open').and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				TestBed.inject(CurrentStructureService),
				'partialUpdateStructure'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and edit a description', async () => {
			component.openMenu(
				mEvent,
				structuresMock[0].id,
				structuresMock[0].name,
				structuresMock[0].description,
				structuresMock[0],
				'',
				'true'
			);
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
			let dialogSpy = spyOn(dialog, 'open').and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				TestBed.inject(CurrentStructureService),
				'partialUpdateStructure'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the remove structure dialog', async () => {
			component.openMenu(
				mEvent,
				structuresMock[0].id,
				structuresMock[0].name,
				structuresMock[0].description,
				structuresMock[0],
				'',
				'true'
			);
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'removeStructureDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(dialog, 'open').and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				TestBed.inject(CurrentStructureService),
				'removeStructureFromSubmessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Remove structure from submessage'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});
		it('should open the delete structure dialog', async () => {
			component.openMenu(
				mEvent,
				structuresMock[0].id,
				structuresMock[0].name,
				structuresMock[0].description,
				structuresMock[0],
				'',
				'true'
			);
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'deleteStructureDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(dialog, 'open').and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
				TestBed.inject(CurrentStructureService),
				'deleteStructure'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Delete structure globally'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open a diff', async () => {
			component.openMenu(
				mEvent,
				structuresMockWithChanges.id,
				structuresMockWithChanges.name,
				structuresMockWithChanges.description,
				structuresMockWithChanges,
				structuresMockWithChanges.name,
				'true'
			);
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'viewDiff').and.callThrough();
			await menu.clickItem({ text: new RegExp('View Diff') });
			expect(spy).toHaveBeenCalled();
		});
		afterEach(() => {
			component.matMenuTrigger.closeMenu();
		});
	});
});
