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
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CommandPaletteComponent } from './command-palette/command-palette/command-palette.component';
import { GcDatatableComponent } from './gc-datatable/gc-datatable.component';
import { NoDataToDisplayComponent } from './gc-datatable/no-data-to-display/no-data-to-display/no-data-to-display.component';
import { GridCommanderComponent } from './grid-commander.component';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('GridCommanderComponent', () => {
	let component: GridCommanderComponent;
	let fixture: ComponentFixture<GridCommanderComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				MatCardModule,
				MatDialogModule,
				MatCardModule,
				GridCommanderComponent,
				CommandPaletteComponent,
				GcDatatableComponent,
				NoDataToDisplayComponent,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				provideRouter([]),
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GridCommanderComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
