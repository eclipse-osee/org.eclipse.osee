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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ImmediateErrorStateMatcher } from '@osee/shared/matchers';
import { ShowOnDirtyErrorStateMatcher } from '@angular/material/core';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

const stringAttributeWithDefault: attribute<string, ATTRIBUTETYPEID> = {
	name: 'Extension',
	value: 'md',
	typeId: '1152921504606847064' as ATTRIBUTETYPEID,
	id: '-1',
	gammaId: '-1',
	storeType: 'String',
};

describe('AttributesEditorComponent', () => {
	let component: AttributesEditorComponent;
	let fixture: ComponentFixture<AttributesEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [AttributesEditorComponent, MarkdownEditorComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(AttributesEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('attributes', [mockAttribute]);
		fixture.componentRef.setInput('editable', true);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should prepopulate a string field with its provided default value', async () => {
		fixture.componentRef.setInput('attributes', [
			stringAttributeWithDefault,
		]);
		fixture.detectChanges();
		// Let ngModel flush the model value into the DOM.
		await fixture.whenStable();
		fixture.detectChanges();

		const textarea: HTMLTextAreaElement =
			fixture.nativeElement.querySelector('textarea[matInput]');
		expect(textarea).toBeTruthy();
		expect(textarea.value).toBe('md');
	});

	it('should use the standard (dirty) matcher by default', () => {
		const matcher = (
			component as unknown as {
				errorMatcher: () => unknown;
			}
		).errorMatcher();
		expect(matcher).toBeInstanceOf(ShowOnDirtyErrorStateMatcher);
	});

	it('should use the immediate matcher when highlightRequiredImmediately is true', () => {
		fixture.componentRef.setInput('highlightRequiredImmediately', true);
		fixture.detectChanges();

		const matcher = (
			component as unknown as {
				errorMatcher: () => unknown;
			}
		).errorMatcher();
		expect(matcher).toBeInstanceOf(ImmediateErrorStateMatcher);
		// Sanity: it is not the default (dirty) matcher.
		expect(matcher).not.toBeInstanceOf(ShowOnDirtyErrorStateMatcher);
	});
});
