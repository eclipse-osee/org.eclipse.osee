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
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MarkdownComponent } from 'ngx-markdown';
import {
	HelpTopicRegistryService,
} from '../help-topic-registry.service';
import { HelpSection } from '../help-drawer.service';

@Component({
	selector: 'osee-help-popup',
	imports: [MatIcon, MatIconButton, MatTooltip, MarkdownComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './help-popup.component.html',
	host: {
		class: 'tw-block tw-h-screen tw-overflow-hidden',
	},
})
export class HelpPopupComponent {
	private readonly route = inject(ActivatedRoute);
	private readonly registry = inject(HelpTopicRegistryService);
	private readonly destroyRef = inject(DestroyRef);

	private readonly contentEl =
		viewChild<ElementRef<HTMLDivElement>>('contentArea');

	private readonly topicId = toSignal(
		this.route.queryParamMap.pipe(map((params) => params.get('topic') ?? '')),
		{ initialValue: '' }
	);

	protected readonly activeTopic = computed(() => {
		return this.registry.getTopic(this.topicId());
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

	protected readonly activeSection = signal<string>('');

	protected highlightInParent(section: HelpSection): void {
		// Send message to opener window to highlight the element
		if (window.opener && !window.opener.closed) {
			window.opener.postMessage(
				{ type: 'osee-help-highlight', anchorId: section.anchorId },
				'*'
			);
		}
		this.scrollToSection(section);
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

	protected onMarkdownReady(): void {
		const container = this.contentEl()?.nativeElement;
		if (!container) {
			return;
		}

		const headings = container.querySelectorAll('h2');
		const sections = this.sections();

		headings.forEach((h2) => {
			const text = h2.textContent?.trim() ?? '';
			const matchingSection = sections.find(
				(s) => s.label.toLowerCase() === text.toLowerCase()
			);
			if (matchingSection) {
				h2.id = matchingSection.id;

				const showMeBtn = document.createElement('button');
				showMeBtn.className = 'osee-help-show-me-btn';
				showMeBtn.title = `Highlight "${matchingSection.label}" in the UI.`;
				showMeBtn.innerHTML =
					'<span class="material-icons" style="font-size: 14px; vertical-align: middle; font-feature-settings: \'liga\'; -webkit-font-smoothing: antialiased; text-rendering: optimizeLegibility; letter-spacing: normal; word-wrap: normal; white-space: nowrap; direction: ltr;">visibility</span> Show Me';
				showMeBtn.addEventListener('click', () => {
					this.highlightInParent(matchingSection);
				});
				h2.appendChild(showMeBtn);
			}
		});

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

export default HelpPopupComponent;
