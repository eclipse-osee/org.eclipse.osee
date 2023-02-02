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
import { FilterService } from '../../services/datatable-services/filter/filter.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableFilterComponent } from './table-filter.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';

describe('TableFilterComponent', () => {
	let component: TableFilterComponent;
	let fixture: ComponentFixture<TableFilterComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientTestingModule,
				MatFormFieldModule,
				MatSelectModule,
				MatChipsModule,
				MatSelectModule,
			],
			declarations: [TableFilterComponent],
			providers: [FilterService],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TableFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
