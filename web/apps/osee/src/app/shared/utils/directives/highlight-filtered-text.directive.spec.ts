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
import { ElementRef, Renderer2 } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { HighlightFilteredTextDirective } from './highlight-filtered-text.directive';
class MockElementRef implements ElementRef {
	nativeElement = {};
}

describe(`HighlightFilteredTextDirective`, () => {
	beforeEach(async () => {
		TestBed.configureTestingModule({
			imports: [HighlightFilteredTextDirective],
			providers: [
				{ provide: ElementRef, useValue: new MockElementRef() },
				Renderer2,
			],
		});
	});

	it('should create an instance', () => {
		TestBed.runInInjectionContext(() => {
			const directive = new HighlightFilteredTextDirective();
			expect(directive).toBeTruthy();
		});
	});
});
