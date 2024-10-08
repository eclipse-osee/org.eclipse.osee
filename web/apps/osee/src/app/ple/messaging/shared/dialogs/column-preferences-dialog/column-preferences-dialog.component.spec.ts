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
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from '@osee/environments';

import { ColumnPreferencesDialogComponent } from './column-preferences-dialog.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import type {
	settingsDialogData,
	branchApplicability,
} from '@osee/messaging/shared/types';
import {
	defaultEditStructureProfile,
	defaultEditElementProfile,
	defaultViewStructureProfile,
	defaultViewElementProfile,
} from '@osee/messaging/shared/constants';

describe('ColumnPreferencesDialogComponent', () => {
	let component: ColumnPreferencesDialogComponent;
	let fixture: ComponentFixture<ColumnPreferencesDialogComponent>;
	const dialogData: settingsDialogData = {
		allowedHeaders1: ['name', 'description'],
		allHeaders1: ['name', 'description', 'applicability'],
		allowedHeaders2: ['name', 'description'],
		allHeaders2: ['name', 'description', 'applicability'],
		branchId: '10',
		editable: false,
		headers1Label: 'Headers1 Label',
		headers2Label: 'Headers2 Label',
		headersTableActive: true,
		wordWrap: false,
	};
	let httpTestingController: HttpTestingController;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatFormFieldModule,
				NoopAnimationsModule,
				MatListModule,
				FormsModule,
				MatButtonModule,
				MatTableModule,
				MatCheckboxModule,
				MatTooltipModule,
				ColumnPreferencesDialogComponent,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ColumnPreferencesDialogComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should call the backend to get whether a branch is editable', () => {
		const testData: branchApplicability = {
			associatedArtifactId: '-1',
			branch: {
				id: '-1',
				viewId: '-1',
				idIntValue: -1,
				name: '',
			},
			editable: true,
			features: [],
			groups: [],
			parentBranch: {
				id: '-1',
				viewId: '-1',
				idIntValue: -1,
				name: '',
			},
			views: [],
		};
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/applicui/branch/' + 10
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});
	it('should set default edit profiles', async () => {
		component.data.editable = true;
		await (
			await loader.getHarness(
				MatButtonHarness.with({ text: 'Reset to Defaults' })
			)
		).click();
		expect(component.data.allowedHeaders1).toEqual(
			defaultEditStructureProfile
		);
		expect(component.data.allowedHeaders2).toEqual(
			defaultEditElementProfile
		);
	});

	it('should set default view profiles', async () => {
		component.data.editable = false;
		await (
			await loader.getHarness(
				MatButtonHarness.with({ text: 'Reset to Defaults' })
			)
		).click();
		expect(component.data.allowedHeaders1).toEqual(
			defaultViewStructureProfile
		);
		expect(component.data.allowedHeaders2).toEqual(
			defaultViewElementProfile
		);
	});
});
