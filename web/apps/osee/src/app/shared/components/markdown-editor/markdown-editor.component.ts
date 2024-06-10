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
	ChangeDetectionStrategy,
	Component,
	ElementRef,
	computed,
	input,
	model,
	signal,
	viewChild,
} from '@angular/core';
import {
	takeUntilDestroyed,
	toObservable,
	toSignal,
} from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { MarkdownComponent } from 'ngx-markdown';
import {
	combineLatest,
	fromEvent,
	map,
	of,
	scan,
	switchMap,
	takeUntil,
	throttleTime,
} from 'rxjs';
import { mdExamples } from './markdown-editor-examples';

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
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkdownEditorComponent {
	disabled = input(false);

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

	// Template element references

	private resizerRef = viewChild.required('resizer', { read: ElementRef });
	private resizerEl = computed<HTMLElement>(
		() => this.resizerRef().nativeElement
	);
	private _offsetWidth = computed(
		() => this.resizerEl().parentElement?.offsetWidth || 0
	);
	private containerLeftRef = viewChild.required('containerLeft', {
		read: ElementRef,
	});
	private container__leftEL = computed<HTMLElement>(
		() => this.containerLeftRef().nativeElement
	);

	private oldLeftWidth = computed(() => this.container__leftEL().offsetWidth);
	containerRightRef = viewChild.required('containerRight', {
		read: ElementRef,
	});
	bodyRef = viewChild.required('main', { read: ElementRef });
	bodyEl = computed<HTMLElement>(() => this.bodyRef().nativeElement);

	private _mouseDownOnResizeButton = toObservable(this.resizerEl).pipe(
		switchMap((el) => fromEvent<MouseEvent>(el, 'mousedown')),
		takeUntilDestroyed()
	);

	private _oldCursorX = this._mouseDownOnResizeButton.pipe(
		map((event) => event.x),
		takeUntilDestroyed()
	);

	private _mouseUp = fromEvent<MouseEvent>(document, 'mouseup').pipe(
		takeUntilDestroyed()
	);

	private _mouseMove = fromEvent<MouseEvent>(document, 'mousemove').pipe(
		takeUntilDestroyed()
	);

	private _mouseMoveX = this._mouseMove.pipe(
		map((event) => event.x),
		takeUntilDestroyed()
	);

	private _width = this._oldCursorX.pipe(
		switchMap((x) =>
			combineLatest([of(x), this._mouseMoveX]).pipe(
				throttleTime(16.67), //locking updates to 60Hz
				map(([oldX, newX]) => newX - oldX),
				map(
					(dx) =>
						((this.oldLeftWidth() + dx) * 100) / this._offsetWidth()
				),
				map((w) => 'width:' + w + '% !important'),
				takeUntil(this._mouseUp)
			)
		),
		takeUntilDestroyed()
	);

	protected width = toSignal(this._width, {
		initialValue: 'width:50% !important',
	});
}
