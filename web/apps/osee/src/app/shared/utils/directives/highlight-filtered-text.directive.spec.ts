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
import { Component, DebugElement, Renderer2, Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HighlightFilteredTextDirective } from './highlight-filtered-text.directive';
import { By } from '@angular/platform-browser';
@Component({
	selector: 'osee-my-test-component',
	template:
		'<div oseeHighlightFilteredText searchTerms="this" text="Hello World This is Text" classToApply="highlightTextClass">Hello World This is Text</div>',
	standalone: true,
	imports: [HighlightFilteredTextDirective],
})
class TestComponent {}

@Component({
	selector: 'osee-my-test-component-standalone',
	standalone: true,
	imports: [HighlightFilteredTextDirective],
	template:
		'<div oseeHighlightFilteredText searchTerms="this" text="Hello World This is Text" classToApply="highlightTextClass">Hello World This is Text</div>',
})
class StandaloneTestComponent {}

const componentsUnderTest = [
	{
		component: TestComponent,
		compilation: TestBed.configureTestingModule({
			imports: [HighlightFilteredTextDirective, TestComponent],
		}).compileComponents(),
	},
	{
		component: StandaloneTestComponent,
		compilation: TestBed.configureTestingModule({
			imports: [HighlightFilteredTextDirective, StandaloneTestComponent],
		}).compileComponents(),
	},
];
describe(`HighlightFilteredTextDirective ${componentsUnderTest[1].component.name}`, () => {
	let renderer: Renderer2;
	let inputEl: DebugElement;
	let component: any; //too big of a pain
	let fixture: ComponentFixture<any>;
	beforeEach(async () => {
		if (componentsUnderTest[1].compilation !== undefined) {
			await componentsUnderTest[1].compilation;
		}
		fixture = TestBed.createComponent(componentsUnderTest[1].component);
		fixture.detectChanges();
		inputEl = fixture.debugElement;
		component = fixture.componentInstance;
		renderer = fixture.componentRef.injector.get(
			Renderer2 as Type<Renderer2>
		);
	});

	it('should create an instance', () => {
		const directive = new HighlightFilteredTextDirective(inputEl, renderer);
		expect(directive).toBeTruthy();
	});

	it('should insert a span of type highlightTextClass', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		await fixture.whenRenderingDone();
		let nativeEl = inputEl;
		let spanDe = nativeEl.query(By.css('span'));
		let span = spanDe.nativeElement;
		expect(span.classList.contains('highlightTextClass')).toBeTruthy();
	});
});

describe('Should be available to non-standalone components', () => {
	let renderer: Renderer2;
	let inputEl: DebugElement;
	let component: any; //too big of a pain
	let fixture: ComponentFixture<any>;
	beforeEach(async () => {
		if (componentsUnderTest[0].compilation !== undefined) {
			await componentsUnderTest[0].compilation;
		}
		fixture = TestBed.createComponent(componentsUnderTest[1].component);
		fixture.detectChanges();
		inputEl = fixture.debugElement;
		component = fixture.componentInstance;
		renderer = fixture.componentRef.injector.get(
			Renderer2 as Type<Renderer2>
		);
	});

	it('should create an instance', () => {
		const directive = new HighlightFilteredTextDirective(inputEl, renderer);
		expect(directive).toBeTruthy();
	});
});
