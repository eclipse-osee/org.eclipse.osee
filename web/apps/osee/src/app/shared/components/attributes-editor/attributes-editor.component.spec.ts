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

	describe('canDelete (allowDelete on)', () => {
		const canDelete = (attr: attribute<string, ATTRIBUTETYPEID>) =>
			(
				component as unknown as {
					canDelete: (
						a: attribute<string, ATTRIBUTETYPEID>
					) => boolean;
				}
			).canDelete(attr);

		const makeAttr = (
			overrides: Partial<attribute<string, ATTRIBUTETYPEID>>
		): attribute<string, ATTRIBUTETYPEID> =>
			({
				name: 'Attr',
				value: '',
				typeId: '100' as ATTRIBUTETYPEID,
				id: '-1',
				gammaId: '-1',
				storeType: 'String',
				...overrides,
			}) as attribute<string, ATTRIBUTETYPEID>;

		beforeEach(() => {
			fixture.componentRef.setInput('allowDelete', true);
		});

		it('never allows deleting Name', () => {
			const name = makeAttr({ name: 'Name' });
			fixture.componentRef.setInput('attributes', [name]);
			fixture.detectChanges();
			expect(canDelete(name)).toBe(false);
		});

		it('allows deleting an optional attribute', () => {
			// No multiplicity / ANY -> optional.
			const optional = makeAttr({
				multiplicity: { id: '1', name: 'ANY' },
			});
			fixture.componentRef.setInput('attributes', [optional]);
			fixture.detectChanges();
			expect(canDelete(optional)).toBe(true);
		});

		it('does not allow deleting the only instance of a required type', () => {
			const required = makeAttr({
				multiplicity: { id: '4', name: 'AT_LEAST_ONE' },
			});
			fixture.componentRef.setInput('attributes', [required]);
			fixture.detectChanges();
			expect(canDelete(required)).toBe(false);
		});

		it('allows deleting extra instances of a required type', () => {
			const first = makeAttr({
				multiplicity: { id: '4', name: 'AT_LEAST_ONE' },
			});
			const second = makeAttr({
				multiplicity: { id: '4', name: 'AT_LEAST_ONE' },
			});
			fixture.componentRef.setInput('attributes', [first, second]);
			fixture.detectChanges();
			// Both instances are individually deletable while more than one exists.
			expect(canDelete(first)).toBe(true);
			expect(canDelete(second)).toBe(true);
		});

		it('reports nothing deletable when allowDelete is off', () => {
			fixture.componentRef.setInput('allowDelete', false);
			const optional = makeAttr({
				multiplicity: { id: '1', name: 'ANY' },
			});
			fixture.componentRef.setInput('attributes', [optional]);
			fixture.detectChanges();
			expect(canDelete(optional)).toBe(false);
		});
	});

	describe('isDuplicateValue', () => {
		const isDuplicateValue = (attr: attribute<string, ATTRIBUTETYPEID>) =>
			(
				component as unknown as {
					isDuplicateValue: (
						a: attribute<string, ATTRIBUTETYPEID>
					) => boolean;
				}
			).isDuplicateValue(attr);

		const dupAttr = (
			typeId: string,
			value: string
		): attribute<string, ATTRIBUTETYPEID> =>
			({
				name: 'Attr',
				value,
				typeId: typeId as ATTRIBUTETYPEID,
				id: '-1',
				gammaId: '-1',
				storeType: 'String',
			}) as attribute<string, ATTRIBUTETYPEID>;

		it('flags two same-type instances that share a value', () => {
			fixture.componentRef.setInput('allowDelete', true);
			const a = dupAttr('100', 'dup');
			const b = dupAttr('100', 'dup');
			fixture.componentRef.setInput('attributes', [a, b]);
			fixture.detectChanges();
			expect(isDuplicateValue(a)).toBe(true);
			expect(isDuplicateValue(b)).toBe(true);
		});

		it('does not flag same-type instances with distinct values', () => {
			fixture.componentRef.setInput('allowDelete', true);
			const a = dupAttr('100', 'one');
			const b = dupAttr('100', 'two');
			fixture.componentRef.setInput('attributes', [a, b]);
			fixture.detectChanges();
			expect(isDuplicateValue(a)).toBe(false);
			expect(isDuplicateValue(b)).toBe(false);
		});

		it('does not flag identical values across different types', () => {
			fixture.componentRef.setInput('allowDelete', true);
			const a = dupAttr('100', 'same');
			const b = dupAttr('200', 'same');
			fixture.componentRef.setInput('attributes', [a, b]);
			fixture.detectChanges();
			expect(isDuplicateValue(a)).toBe(false);
		});

		it('never flags when allowDelete is off', () => {
			fixture.componentRef.setInput('allowDelete', false);
			const a = dupAttr('100', 'dup');
			const b = dupAttr('100', 'dup');
			fixture.componentRef.setInput('attributes', [a, b]);
			fixture.detectChanges();
			expect(isDuplicateValue(a)).toBe(false);
		});
	});

	it('groups multiple instances of a type under a count header when allowDelete is on', async () => {
		fixture.componentRef.setInput('allowDelete', true);
		const first: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'md',
		};
		const second: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'txt',
		};
		fixture.componentRef.setInput('attributes', [first, second]);
		fixture.detectChanges();
		await fixture.whenStable();
		fixture.detectChanges();

		// The group header shows the attribute name and instance count.
		const header: HTMLElement = fixture.nativeElement.querySelector(
			'[data-testid="attribute-group-header"]'
		);
		expect(header).toBeTruthy();
		expect(header.textContent).toContain('Extension');
		expect(header.textContent).toContain('(2)');

		// Both instance fields render inside the group.
		const textareas: NodeListOf<HTMLTextAreaElement> =
			fixture.nativeElement.querySelectorAll('textarea[matInput]');
		expect(textareas.length).toBe(2);
		expect(textareas[0].value).toBe('md');
		expect(textareas[1].value).toBe('txt');
	});

	it('does not group (no count header) in the default flat layout', async () => {
		const first: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'md',
		};
		const second: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'txt',
		};
		fixture.componentRef.setInput('attributes', [first, second]);
		fixture.detectChanges();
		await fixture.whenStable();
		fixture.detectChanges();

		expect(
			fixture.nativeElement.querySelector(
				'[data-testid="attribute-group-header"]'
			)
		).toBeNull();
	});

	it('binds independent values for multiple instances of the same type', async () => {
		// Two instances of the same String type must render two independent
		// textareas (unique form-control names) so their values do not collide.
		const first: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'md',
		};
		const second: attribute<string, ATTRIBUTETYPEID> = {
			...stringAttributeWithDefault,
			value: 'txt',
		};
		fixture.componentRef.setInput('attributes', [first, second]);
		fixture.detectChanges();
		await fixture.whenStable();
		fixture.detectChanges();

		const textareas: NodeListOf<HTMLTextAreaElement> =
			fixture.nativeElement.querySelectorAll('textarea[matInput]');
		expect(textareas.length).toBe(2);
		expect(textareas[0].value).toBe('md');
		expect(textareas[1].value).toBe('txt');
	});
});
