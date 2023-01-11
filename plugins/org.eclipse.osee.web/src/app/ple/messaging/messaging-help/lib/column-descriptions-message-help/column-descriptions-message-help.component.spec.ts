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
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';

import { ColumnDescriptionsMessageHelpComponent } from './column-descriptions-message-help.component';

describe('ColumnDescriptionsMessageHelpComponent', () => {
	let component: ColumnDescriptionsMessageHelpComponent;
	let fixture: ComponentFixture<ColumnDescriptionsMessageHelpComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatTableModule,
				MatListModule,
				ColumnDescriptionsMessageHelpComponent,
			],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(
			ColumnDescriptionsMessageHelpComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
