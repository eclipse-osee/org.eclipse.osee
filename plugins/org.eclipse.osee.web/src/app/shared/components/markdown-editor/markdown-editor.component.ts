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
import {
	AfterViewInit,
	Component,
	ElementRef,
	OnDestroy,
	Renderer2,
	RendererStyleFlags2,
	ViewChild,
	computed,
	model,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MarkdownComponent } from 'ngx-markdown';
import { MatTooltip } from '@angular/material/tooltip';
import { scan } from 'rxjs';
import { MatMenu, MatMenuTrigger, MatMenuItem } from '@angular/material/menu';
import { mdExamples } from './markdown-editor-examples';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-markdown-editor',
	standalone: true,
	imports: [
		MatIcon,
		FormsModule,
		MatFormField,
		MatDivider,
		MatInputModule,
		MarkdownComponent,
		MatTooltip,
		MatMenu,
		MatMenuTrigger,
		MatMenuItem,
	],
	templateUrl: './markdown-editor.component.html',
})
export class MarkdownEditorComponent implements OnDestroy, AfterViewInit {
	mdContent = model.required<string>();
	_history = toObservable(this.mdContent).pipe(
		scan((acc, curr) => {
			if (acc.length === this.maxHistory()) {
				acc = acc.splice(1);
			}
			return [...acc, curr];
		}, [] as string[])
	);
	history = toSignal(this._history);
	redoHistory = signal([] as string[]);
	maxHistory = signal(100);
	mdExamples = mdExamples;

	constructor(private renderer2: Renderer2) {}

	addExampleToMdContent(markdownExample: string) {
		this.mdContent.set(this.mdContent() + '\n\n' + markdownExample);
	}

	// Undo/Redo

	undo() {
		const latestHistoryValue = this.history()?.pop();

		if (latestHistoryValue) {
			if (latestHistoryValue === this.mdContent()) {
				const nextValue = computed(() => this.history()?.pop())();

				if (nextValue) {
					this.updateRedoHistory(this.mdContent());
					this.mdContent.set(nextValue);
				}
			} else {
				this.updateRedoHistory(this.mdContent());
				this.mdContent.set(latestHistoryValue);
			}
		}
	}

	updateRedoHistory(latestHistoryValue: string) {
		if (
			this.redoHistory()[this.redoHistory().length - 1] !==
			latestHistoryValue
		) {
			this.redoHistory.update((curr) => [...curr, latestHistoryValue]);
		}
	}

	redo() {
		const latestRedoHistoryValue = this.redoHistory().pop();

		if (latestRedoHistoryValue) {
			if (latestRedoHistoryValue === this.mdContent()) {
				const nextValue = computed(() => this.history()?.pop())();

				if (nextValue) {
					this.mdContent.set(nextValue);
				}
			} else {
				this.mdContent.set(latestRedoHistoryValue);
			}
		}
	}

	// Mouse/Cursor tracking

	private unlistenSlideMouseDown!: () => void;
	private unlistenSlideMouseMoving!: () => void;
	private unlistenSlideMouseUp!: () => void;

	private oldCursorX!: number;
	private oldLeftWidth!: number;

	// Template element references

	@ViewChild('resizer') resizerRef!: ElementRef;
	@ViewChild('containerLeft') containerLeftRef!: ElementRef;
	@ViewChild('containerLeftTextarea')
	containerLeftTextareaRef!: ElementRef;
	@ViewChild('containerRight') containerRightRef!: ElementRef;
	@ViewChild('main') bodyRef!: ElementRef;

	ngAfterViewInit(): void {
		const resizerEl = this.resizerRef.nativeElement;

		const container__leftEL = this.containerLeftRef.nativeElement;
		const container__rightEL = this.containerRightRef.nativeElement;
		const bodyEl = this.bodyRef.nativeElement;

		this.unlistenSlideMouseDown = this.renderer2.listen(
			resizerEl,
			'mousedown',
			(event) => {
				this.oldCursorX = event.clientX;
				this.oldLeftWidth = container__leftEL.offsetWidth;

				this.unlistenSlideMouseMoving = this.renderer2.listen(
					'document',
					'mousemove',
					(event) => {
						const dx = event.clientX - this.oldCursorX;

						const newLeftWidth =
							((this.oldLeftWidth + dx) * 100) /
							resizerEl.parentNode.offsetWidth;

						this.renderer2.setStyle(
							container__leftEL,
							'width',
							newLeftWidth + '%',
							RendererStyleFlags2.Important
						);
					}
				);

				this.unlistenSlideMouseUp = this.renderer2.listen(
					'document',
					'mouseup',
					() => {
						this.renderer2.removeStyle(resizerEl, 'cursor');
						this.renderer2.removeStyle(bodyEl, 'cursor');

						this.renderer2.removeStyle(
							container__leftEL,
							'userSelect'
						);
						this.renderer2.removeStyle(
							container__leftEL,
							'pointerEvents'
						);

						this.renderer2.removeStyle(
							container__rightEL,
							'userSelect'
						);
						this.renderer2.removeStyle(
							container__rightEL,
							'pointerEvents'
						);

						this.unlistenSlideMouseMoving();
						this.unlistenSlideMouseUp();
					}
				);
			}
		);
	}

	ngOnDestroy(): void {
		this.unlistenSlideMouseDown();
	}
}
