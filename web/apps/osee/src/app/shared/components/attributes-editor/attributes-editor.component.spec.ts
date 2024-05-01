/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { AttributesEditorComponent } from './attributes-editor.component';
import { MarkdownEditorComponent } from './../markdown-editor/markdown-editor.component';
import { mockAttribute } from '../../types/attribute';

describe('AttributesEditorComponent', () => {
	let component: AttributesEditorComponent;
	let fixture: ComponentFixture<AttributesEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [AttributesEditorComponent, MarkdownEditorComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(AttributesEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('attributes', [mockAttribute]);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
