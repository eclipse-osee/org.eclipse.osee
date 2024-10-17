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
import {
	Directive,
	ElementRef,
	Input,
	OnChanges,
	Renderer2,
	SimpleChanges,
	inject,
} from '@angular/core';

@Directive({
	selector: '[oseeHighlightFilteredText]',
	standalone: true,
})
export class HighlightFilteredTextDirective implements OnChanges {
	private el = inject(ElementRef);
	private renderer = inject(Renderer2);

	@Input() searchTerms = '';
	@Input() text = '';
	@Input() classToApply = '';

	ngOnChanges(_changes: SimpleChanges): void {
		if (!this.classToApply && this.text) {
			//we are now assuming users of this directive are string interpolating their value when searchTerms is not present.
			return;
		} else if (this.text) {
			this.renderer.setProperty(
				this.el.nativeElement,
				'innerHTML',
				!this.searchTerms || !this.searchTerms.length
					? this.text
					: this.getFormattedText()
			);
		}
	}
	getFormattedText() {
		const re = new RegExp(`(${this.searchTerms})`, 'i');
		const returnValue = this.text
			?.toString()
			.replace('<', '&lt;')
			.replace('>', '&gt;')
			.replace(re, `<span class="${this.classToApply}">$1</span>`);
		return returnValue;
	}
}
