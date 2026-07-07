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
	afterNextRender,
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	effect,
	ElementRef,
	inject,
	signal,
	viewChild,
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MarkdownComponent } from 'ngx-markdown';
import { HelpDrawerService, HelpSection } from './help-drawer.service';
import { HelpTopicRegistryService } from './help-topic-registry.service';

@Component({
	selector: 'osee-help-drawer',
	imports: [MatIconButton, MatIcon, MatTooltip, MarkdownComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './help-drawer.component.html',
	host: {
		'[class.tw-hidden]': '!helpDrawerService.isOpen()',
		class: 'tw-fixed tw-right-4 tw-top-4 tw-bottom-4 tw-block',
		style: 'z-index: 10001',
	},
})
export class HelpDrawerComponent {
	protected readonly helpDrawerService = inject(HelpDrawerService);
	private readonly registry = inject(HelpTopicRegistryService);
	private readonly destroyRef = inject(DestroyRef);
	private readonly hostEl = inject(ElementRef);

	private readonly contentEl =
		viewChild<ElementRef<HTMLDivElement>>('contentArea');

	constructor() {
		// Move element to document.body AFTER the CDK overlay container
		// so it renders above dialog backdrops.
		afterNextRender(() => {
			document.body.appendChild(this.hostEl.nativeElement);
		});

		// Re-append to body every time the drawer opens, ensuring it's
		// always the last child (after CDK overlay container).
		effect(() => {
			if (this.helpDrawerService.isOpen()) {
				const el = this.hostEl.nativeElement as HTMLElement;
				if (el.parentNode === document.body) {
					document.body.appendChild(el);
				}
			}
		});

		this.destroyRef.onDestroy(() => {
			const el = this.hostEl.nativeElement as HTMLElement;
			if (el.parentNode === document.body) {
				document.body.removeChild(el);
			}
		});
	}

	protected readonly activeTopic = computed(() => {
		const topicId = this.helpDrawerService.activeTopic();
		return this.registry.getTopic(topicId);
	});

	protected readonly markdownPath = computed(
		() => this.activeTopic()?.markdownPath ?? ''
	);

	protected readonly sections = computed(
		() => this.activeTopic()?.sections ?? []
	);

	protected readonly topicLabel = computed(
		() => this.activeTopic()?.label ?? 'Help'
	);

	/** Tracks which section is currently most visible in the scroll area. */
	protected readonly activeSection = signal<string>('');

	protected close(): void {
		this.helpDrawerService.close();
	}

	protected highlightSection(section: HelpSection): void {
		this.helpDrawerService.highlightAnchor(section.anchorId);
	}

	protected scrollToSection(section: HelpSection): void {
		const container = this.contentEl()?.nativeElement;
		if (!container) {
			return;
		}
		const heading = container.querySelector(
			`[id="${section.id}"]`
		) as HTMLElement | null;
		if (heading) {
			heading.scrollIntoView({ behavior: 'smooth', block: 'start' });
			this.activeSection.set(section.id);
		}
	}

	protected highlightAndScroll(section: HelpSection): void {
		this.highlightSection(section);
		this.scrollToSection(section);
	}

	/**
	 * Called when markdown finishes rendering.
	 * Injects "Show Me" buttons after each h2 heading
	 * and sets up the IntersectionObserver for active section tracking.
	 */
	protected onMarkdownReady(): void {
		const container = this.contentEl()?.nativeElement;
		if (!container) {
			return;
		}

		// Add IDs to h2 elements matching section IDs and inject "Show Me" buttons
		const headings = container.querySelectorAll('h2');
		const sections = this.sections();

		headings.forEach((h2) => {
			const text = h2.textContent?.trim() ?? '';
			const matchingSection = sections.find(
				(s) => s.label.toLowerCase() === text.toLowerCase()
			);
			if (matchingSection) {
				h2.id = matchingSection.id;

				// Create "Show Me" button inline with the heading
				const showMeBtn = document.createElement('button');
				showMeBtn.className =
					'osee-help-show-me-btn';
				showMeBtn.title = `Highlight "${matchingSection.label}" in the UI`;
				showMeBtn.innerHTML =
					'<span class="material-icons" style="font-size: 14px; vertical-align: middle; font-feature-settings: \'liga\'; -webkit-font-smoothing: antialiased; text-rendering: optimizeLegibility; letter-spacing: normal; word-wrap: normal; white-space: nowrap; direction: ltr;">visibility</span> Show Me';
				showMeBtn.addEventListener('click', () => {
					this.helpDrawerService.highlightAnchor(
						matchingSection.anchorId
					);
				});
				h2.appendChild(showMeBtn);
			}
		});

		// Set up IntersectionObserver for active section tracking
		this.setupScrollObserver(container, sections);
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

		const observer = new IntersectionObserver(
			(entries) => {
				for (const entry of entries) {
					if (entry.isIntersecting) {
						this.activeSection.set(
							(entry.target as HTMLElement).id
						);
						break;
					}
				}
			},
			{
				root: container,
				rootMargin: '-10% 0px -70% 0px',
				threshold: 0,
			}
		);

		headings.forEach((h) => observer.observe(h));

		this.observerCleanup = () => observer.disconnect();
		this.destroyRef.onDestroy(() => {
			if (this.observerCleanup) {
				this.observerCleanup();
			}
		});
	}
}
