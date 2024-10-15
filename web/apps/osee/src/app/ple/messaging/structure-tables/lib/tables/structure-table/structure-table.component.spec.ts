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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
	ActivatedRoute,
	ActivatedRouteSnapshot,
	RouterLink,
	provideRouter,
} from '@angular/router';
import { of } from 'rxjs';
import { SubElementTableComponentMock } from '../sub-element-table/sub-element-table.component.mock';

import { CdkVirtualForOf, ScrollingModule } from '@angular/cdk/scrolling';
import { AsyncPipe, NgClass, NgFor, NgIf, NgStyle } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MockCurrentViewSelectorComponent } from '@osee/shared/components/testing';
import { MatPaginatorModule } from '@angular/material/paginator';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MockPersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown/testing';
import { MockPersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input/testing';
import {
	CurrentStructureMultiService,
	CurrentStructureService,
	EditAuthService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	MessagingControlsMockComponent,
	editAuthServiceMock,
	preferencesUiServiceMock,
} from '@osee/messaging/shared/testing';
import { MULTI_STRUCTURE_SERVICE } from '@osee/messaging/shared/tokens';
import { MockPersistedStructureCategoryDropdownComponent } from '@osee/messaging/structure-category/persisted-structure-category-dropdown/testing';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { AddElementDialogComponent } from '../../dialogs/add-element-dialog/add-element-dialog.component';
import { StructureTableNoEditFieldComponent } from '../../fields/structure-table-no-edit-field/structure-table-no-edit-field.component';
import { StructureMenuComponent } from '../../menus/structure-menu/structure-menu.component';
import { StructureImpactsValidatorDirective } from '../../structure-impacts-validator.directive';
import { StructureTableComponent } from './structure-table.component';

describe('StructureTableComponent', () => {
	let component: StructureTableComponent;
	let fixture: ComponentFixture<StructureTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatDialogModule],
			providers: [
				provideNoopAnimations(),
				provideRouter([
					{
						path: 'diffOpen',
						outlet: 'rightSideNav',
						component: StructureTableComponent,
					},
					{
						path: 'diff',
						component: StructureTableComponent,
					},
				]),
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
						MockCurrentViewSelectorComponent,
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
						MockPersistedApplicabilityDropdownComponent,
						MockPersistedStringAttributeInputComponent,
						MockPersistedStructureCategoryDropdownComponent,
						StructureMenuComponent,
						StructureTableNoEditFieldComponent,
						FormsModule,
						StructureImpactsValidatorDirective,
					],
					providers: [
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
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StructureTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
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
});
