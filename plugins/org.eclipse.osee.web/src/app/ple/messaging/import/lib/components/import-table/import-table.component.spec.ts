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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { messageToken } from 'src/app/ple/messaging/shared/types/messages';

import { ImportTableComponent } from './import-table.component';

describe('ImportTableComponent', () => {
	let component: ImportTableComponent<messageToken>;
	let fixture: ComponentFixture<ImportTableComponent<messageToken>>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ImportTableComponent, NoopAnimationsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(ImportTableComponent<messageToken>);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
