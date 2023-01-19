/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { GcDatatableComponent } from './gc-datatable.component';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { NoDataToDisplayComponent } from '../gc-datatable/no-data-to-display/no-data-to-display/no-data-to-display.component';
import { MatCardModule } from '@angular/material/card';

describe('GcDatatableComponent', () => {
	let component: GcDatatableComponent;
	let fixture: ComponentFixture<GcDatatableComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatCardModule,
				MatDialogModule,
				MatIconModule,
				MatToolbarModule,
				MatPaginatorModule,
				HttpClientTestingModule,
			],
			declarations: [GcDatatableComponent, NoDataToDisplayComponent],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						action: 'delete',
						object: { 'Artifact Id': '23456' },
					},
				},
				{ provide: MatDialogRef, useValue: {} },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		httpTestingController = TestBed.inject(HttpTestingController);
		fixture = TestBed.createComponent(GcDatatableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('if isAllSelected === true, masterToggle will set the selection array to an empty array', (done: DoneFn) => {
		component.rowsToBeSelectedVal.value.forEach((row) =>
			component.selection.select(row)
		);
		component.masterToggle();
		expect(component.selection.selected.length).toBe(0);
		done();
	});

	it('if isAllSelected === false, masterToggle will push all values of _isSelected and their lengths will be equal', (done: DoneFn) => {
		component.masterToggle();
		expect(
			component.selection.selected.length ===
				component.rowsToBeSelectedVal.value.length
		).toBe(true);
		done();
	});

	it('isAllSelected to return false', (done: DoneFn) => {
		const testObj1 = {
			name: 'testName',
			description: 'testDescription',
			url: 'testURL',
			type: 'testType',
			permissions: 'testPermission',
			icon: 'testIcon',
		};

		component.onElementToggled(testObj1);
		expect(component.isAllSelected()).toBeInstanceOf(Boolean);
		expect(component.isAllSelected()).toBe(false);
		done();
	});

	it('isAllSelected to return true', (done: DoneFn) => {
		component.rowsToBeSelectedVal.value.forEach((row) =>
			component.selection.select(row)
		);

		expect(component.isAllSelected()).toBeInstanceOf(Boolean);
		expect(component.isAllSelected()).toBe(true);
		done();
	});

	it('the argument passed to onElementToggle will be added to selection.selected array', (done: DoneFn) => {
		const testObj = {
			name: 'testName',
			description: 'testDescription',
			url: 'testURL',
			type: 'testType',
			permissions: 'testPermission',
			icon: 'testIcon',
		};

		component.onElementToggled(testObj);
		expect(component.selection.selected).toBeInstanceOf(Array);
		expect(component.selection.selected.length).toBe(1);
		expect(component.selection.selected[0]).toEqual(testObj);
		done();
	});

	it('hideRow when passed a rowObj should update hiddenRows array and return an array of the rowObjects that were passed as arguments', (done: DoneFn) => {
		const testRowObj1 = {
			name: 'test1Name',
			description: 'test1Description',
			url: 'test1URL',
			type: 'test1Type',
			permissions: 'test1Permission',
			icon: 'test1Icon',
		};
		const testRowObj2 = {
			name: 'test2Name',
			description: 'test2Description',
			url: 'test2URL',
			type: 'test2Type',
			permissions: 'test2Permission',
			icon: 'test2Icon',
		};

		component.hideRow(testRowObj1);
		component.hideRow(testRowObj2);

		component.hiddenRows.subscribe((val) => {
			expect(val).toBeInstanceOf(Array);
			expect(val).toEqual([testRowObj1, testRowObj2]);
			done();
		});
	});

	it('hideSelectedRows will update hiddenRows to include all rows that are present in selection.selected array', (done: DoneFn) => {
		const testRowObj1 = {
			name: 'test1Name',
			description: 'test1Description',
			url: 'test1URL',
			type: 'test1Type',
			permissions: 'test1Permission',
			icon: 'test1Icon',
		};
		const testRowObj2 = {
			name: 'test2Name',
			description: 'test2Description',
			url: 'test2URL',
			type: 'test2Type',
			permissions: 'test2Permission',
			icon: 'test2Icon',
		};

		component.onElementToggled(testRowObj1);
		component.onElementToggled(testRowObj2);

		component.hideSelectedRows();

		component.hiddenRows.subscribe((val) => {
			expect(val).toBeInstanceOf(Array);
			expect(val).toEqual([testRowObj1, testRowObj2]);
			done();
		});
	});

	it('showHiddenRows will clear the hiddenRows array', (done: DoneFn) => {
		const testRowObj1 = {
			name: 'test1Name',
			description: 'test1Description',
			url: 'test1URL',
			type: 'test1Type',
			permissions: 'test1Permission',
			icon: 'test1Icon',
		};
		const testRowObj2 = {
			name: 'test2Name',
			description: 'test2Description',
			url: 'test2URL',
			type: 'test2Type',
			permissions: 'test2Permission',
			icon: 'test2Icon',
		};

		component.hideRow(testRowObj1);
		component.hideRow(testRowObj2);
		component.showHiddenRows();

		component.hiddenRows.subscribe((val) => {
			expect(val).toBeInstanceOf(Array);
			expect(val).toEqual([]);
			done();
		});
	});
});
