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
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	ElementRef,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MarkdownComponent } from 'ngx-markdown';
import { HelpSection } from '../help-drawer.service';

@Component({
	selector: 'osee-help-popup',
	imports: [MatIcon, MatTooltip, MarkdownComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './help-popup.component.html',
	styleUrl: './help-popup.component.sass',
	host: {
		class: 'tw-block tw-h-screen tw-overflow-hidden',
	},
})
export class HelpPopupComponent {
	private readonly route = inject(ActivatedRoute);
	private readonly destroyRef = inject(DestroyRef);

	private readonly contentEl =
		viewChild<ElementRef<HTMLDivElement>>('contentArea');

	private readonly topicId = toSignal(
		this.route.queryParamMap.pipe(
			map((params) => params.get('topic') ?? '')
		),
		{ initialValue: '' }
	);

	protected readonly markdownPath = computed(() => {
		const id = this.topicId();
		return id ? `assets/help/${id}/overview.md` : '';
	});

	protected readonly topicLabel = computed(() => {
		const id = this.topicId();
		return id
			.split('-')
			.map((w) => w.charAt(0).toUpperCase() + w.slice(1))
			.join(' ');
	});

	/** Sections discovered from rendered markdown h2 headings (used for navigation chips). */
	protected readonly sections = signal<HelpSection[]>([]);

	protected readonly activeSection = signal<string>('');

	protected highlightInParent(section: HelpSection): void {
		if (window.opener && !window.opener.closed) {
			window.opener.postMessage(
				{ type: 'osee-help-highlight', anchorId: section.anchorId },
				window.location.origin
			);
		}
		this.scrollToSection(section);
	}

	/** When true, the scroll handler won't update activeSection. */
	private scrollLocked = false;

	protected scrollToSection(section: HelpSection): void {
		const container = this.contentEl()?.nativeElement;
		if (!container) {
			return;
		}
		const heading = container.querySelector(
			`[id="${section.id}"]`
		) as HTMLElement | null;
		if (!heading) {
			return;
		}

		// Proportional scrollspy maps scroll% to heading positions.
		// To make this heading the active one, we need to scroll to the
		// position where scrollPercent >= headingPercent.
		// headingPercent = heading.offsetTop / scrollHeight
		// scrollPercent = scrollTop / (scrollHeight - clientHeight)
		// Solve: scrollTop = headingPercent * (scrollHeight - clientHeight)
		const scrollHeight = container.scrollHeight;
		const maxScroll = scrollHeight - container.clientHeight;
		const headingPercent = heading.offsetTop / scrollHeight;
		// Subtract a small amount so the heading lands slightly above
		// the activation threshold, showing more content below it.
		const targetScroll = Math.max(
			0,
			Math.min((headingPercent - 0.02) * maxScroll, maxScroll)
		);

		this.scrollLocked = true;
		this.activeSection.set(section.id);
		container.scrollTo({ top: targetScroll, behavior: 'smooth' });
		setTimeout(() => {
			this.scrollLocked = false;
		}, 800);
	}

	/**
	 * Called when markdown finishes rendering.
	 * Discovers sections from all headings (h2–h6) for "Show Me" buttons,
	 * uses h2 headings for navigation chips, and requests anchor IDs
	 * from the opener window.
	 */
	protected onMarkdownReady(): void {
		const container = this.contentEl()?.nativeElement;
		if (!container) {
			return;
		}

		// Discover all headings for "Show Me" button injection
		const allHeadings = container.querySelectorAll('h2, h3, h4, h5, h6');
		const discovered: HelpSection[] = [];

		allHeadings.forEach((heading) => {
			const text = heading.textContent?.trim() ?? '';
			const id = text
				.toLowerCase()
				.replace(/[^a-z0-9]+/g, '-')
				.replace(/^-|-$/g, '');
			heading.id = id;
			discovered.push({ id, label: text, anchorId: '' });
		});

		// Only h2 headings become navigation chips
		const chipSections = discovered.filter((d) => {
			const el = container.querySelector(`[id="${d.id}"]`);
			return el?.tagName === 'H2';
		});

		// Request anchor IDs from the opener (parent) window
		if (window.opener && !window.opener.closed) {
			window.opener.postMessage(
				{ type: 'osee-help-request-sections', topicId: this.topicId() },
				window.location.origin
			);
		}

		// Listen for section data from the parent
		const onMessage = (event: MessageEvent) => {
			if (event.data?.type === 'osee-help-sections-response') {
				const parentSections: HelpSection[] = event.data.sections ?? [];
				// Merge anchor IDs from parent into all discovered headings
				const merged = discovered.map((d) => {
					const match = parentSections.find(
						(p) => p.label.toLowerCase() === d.label.toLowerCase()
					);
					return match ? { ...d, anchorId: match.anchorId } : d;
				});
				// Only h2 headings become navigation chips
				const chips = merged.filter((d) => {
					const el = container.querySelector(`[id="${d.id}"]`);
					return el?.tagName === 'H2';
				});
				this.sections.set(chips);
				this.injectShowMeButtons(container, merged);
				this.setupScrollObserver(container, chips);
				window.removeEventListener('message', onMessage);
			}
		};
		window.addEventListener('message', onMessage);

		// Fallback: if no response in 500ms, use sections without anchor IDs
		setTimeout(() => {
			if (this.sections().length === 0) {
				this.sections.set(chipSections);
				this.setupScrollObserver(container, chipSections);
				window.removeEventListener('message', onMessage);
			}
		}, 500);
	}

	private injectShowMeButtons(
		container: HTMLElement,
		sections: HelpSection[]
	): void {
		sections.forEach((section) => {
			if (!section.anchorId) {
				return;
			}
			const heading = container.querySelector(`[id="${section.id}"]`);
			if (!heading) {
				return;
			}
			const showMeBtn = document.createElement('button');
			showMeBtn.className = 'osee-help-show-me-btn';
			showMeBtn.title = `Highlight "${section.label}" in the UI.`;
			showMeBtn.innerHTML =
				'<span class="material-icons" style="font-size: 14px; vertical-align: middle; font-feature-settings: \'liga\'; -webkit-font-smoothing: antialiased; text-rendering: optimizeLegibility; letter-spacing: normal; word-wrap: normal; white-space: nowrap; direction: ltr;">visibility</span> Show Me';
			showMeBtn.addEventListener('click', () => {
				this.highlightInParent(section);
			});
			heading.appendChild(showMeBtn);
		});
	}

	private observerCleanup: (() => void) | null = null;

	private setupScrollObserver(
		container: HTMLElement,
		sections: readonly HelpSection[]
	): void {
		if (this.observerCleanup) {
			this.observerCleanup();
		}

		const headings = sections
			.map((s) => container.querySelector(`[id="${s.id}"]`))
			.filter((el): el is HTMLElement => el !== null);

		if (headings.length === 0) {
			return;
		}

		// Proportional scrollspy: maps scroll position as a percentage of
		// total scrollable distance to heading positions as a percentage
		// of total document height. Ensures all sections including the
		// last can become active.
		const onScroll = () => {
			if (this.scrollLocked) {
				return;
			}

			const scrollTop = container.scrollTop;
			const scrollHeight =
				container.scrollHeight - container.clientHeight;

			if (scrollHeight <= 0) {
				this.activeSection.set(headings[0].id);
				return;
			}

			const scrollPercent = scrollTop / scrollHeight;
			const docHeight = container.scrollHeight;
			let activeId = headings[0].id;

			for (const h of headings) {
				const headingPercent = h.offsetTop / docHeight;
				if (scrollPercent >= headingPercent - 0.03) {
					activeId = h.id;
				}
			}

			this.activeSection.set(activeId);
		};

		container.addEventListener('scroll', onScroll, { passive: true });
		onScroll();

		this.observerCleanup = () =>
			container.removeEventListener('scroll', onScroll);
		this.destroyRef.onDestroy(() => {
			if (this.observerCleanup) {
				this.observerCleanup();
			}
		});
	}
}

export default HelpPopupComponent;
