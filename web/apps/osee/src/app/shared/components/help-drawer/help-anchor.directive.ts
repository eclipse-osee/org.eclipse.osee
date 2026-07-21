/*********************************************************************
 * Copyright (c) 2026 Boeing
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
	effect,
	inject,
	input,
	DestroyRef,
	Renderer2,
} from '@angular/core';
import { HelpDrawerService } from './help-drawer.service';

/**
 * Directive that marks a UI element as a help anchor.
 * When the help drawer highlights this anchor ID, the element
 * receives a visual pulse animation to draw the user's attention.
 *
 * Usage:
 * ```html
 * <div oseeHelpAnchor="md-editor-toolbar">...</div>
 * ```
 */
@Directive({
	selector: '[oseeHelpAnchor]',
	standalone: true,
})
export class HelpAnchorDirective {
	/** The anchor ID that links this element to help content sections. */
	readonly oseeHelpAnchor = input.required<string>();

	private readonly el = inject(ElementRef);
	private readonly renderer = inject(Renderer2);
	private readonly helpDrawerService = inject(HelpDrawerService);
	private readonly destroyRef = inject(DestroyRef);

	private animationTimeout: ReturnType<typeof setTimeout> | null = null;

	constructor() {
		effect(() => {
			const highlighted = this.helpDrawerService.highlightedAnchor();
			const myAnchors = this.oseeHelpAnchor()
				.split(',')
				.map((s) => s.trim());

			if (highlighted !== '' && myAnchors.includes(highlighted)) {
				this.applyHighlight();
			} else {
				this.removeHighlight();
			}
		});

		this.destroyRef.onDestroy(() => {
			if (this.animationTimeout) {
				clearTimeout(this.animationTimeout);
			}
		});
	}

	private applyHighlight(): void {
		const el = this.el.nativeElement as HTMLElement;
		this.renderer.addClass(el, 'osee-help-highlight');

		// Auto-remove highlight after animation completes (3s animation)
		this.animationTimeout = setTimeout(() => {
			this.helpDrawerService.clearHighlight();
		}, 3200);

		// Scroll the element into view if not visible
		el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
	}

	private removeHighlight(): void {
		const el = this.el.nativeElement as HTMLElement;
		this.renderer.removeClass(el, 'osee-help-highlight');
		if (this.animationTimeout) {
			clearTimeout(this.animationTimeout);
			this.animationTimeout = null;
		}
	}
}
