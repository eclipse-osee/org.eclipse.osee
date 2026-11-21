import {
	CdkDrag,
	CdkDragDrop,
	CdkDropList,
	CdkDropListGroup,
} from '@angular/cdk/drag-drop';
import { httpResource } from '@angular/common/http';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	inject,
	input,
	linkedSignal,
	signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
	MatCard,
	MatCardContent,
	MatCardHeader,
	MatCardTitle,
} from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { apiURL } from '@osee/environments';
import { ConfirmDialogComponent } from '@osee/shared/dialogs';
import { workDefinition } from '@osee/shared/types/configuration-management';
import { KanbanWorkflow } from '../../models/kanban-workflow';

@Component({
	selector: 'osee-kanban-board',
	imports: [
		CdkDrag,
		CdkDropList,
		CdkDropListGroup,
		MatCard,
		MatCardContent,
		MatCardHeader,
		MatCardTitle,
	],
	templateUrl: './board.component.html',
	styleUrl: './board.component.css',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BoardComponent {
	private readonly dialog = inject(MatDialog);
	private readonly destroyRef = inject(DestroyRef);

	readonly teamDefinitionId = input('');
	readonly workflows = input<readonly KanbanWorkflow[]>([]);

	protected readonly draggedFromColumn = signal<number | null>(null);

	private readonly movedCards = linkedSignal({
		source: this.teamDefinitionId,
		computation: () => new Map<string, string>(),
	});

	protected readonly definition = httpResource<workDefinition>(() => {
		const id = this.teamDefinitionId();
		return id
			? apiURL + '/ats/workdef/teamdef/' + encodeURIComponent(id)
			: undefined;
	});

	protected readonly columns = computed(() => {
		const states = this.definition.hasValue()
			? (this.definition.value()?.states ?? [])
			: [];
		const cardsByState = new Map(
			states.map(({ name }) => [name, [] as KanbanWorkflow[]])
		);
		const movedCards = this.movedCards();
		for (const workflow of this.workflows()) {
			const state = movedCards.get(workflow.id) ?? workflow.state;
			const cards =
				cardsByState.get(state) ??
				cardsByState.get(state.split(/ \((?:Blocked|Hold)\)/, 1)[0]);
			cards?.push(workflow);
		}
		return [...cardsByState].map(([name, cards]) => ({
			id: name,
			title: name,
			cards,
		}));
	});

	protected readonly canEnterColumn = (
		drag: CdkDrag<KanbanWorkflow>,
		target: CdkDropList<string>
	): boolean => {
		const columns = this.columns();
		const currentIndex = columns.findIndex((column) =>
			column.cards.some((card) => card.id === drag.data.id)
		);
		const targetIndex = columns.findIndex(
			(column) => column.id === target.data
		);
		return currentIndex >= 0 && targetIndex > currentIndex;
	};

	protected moveCard(
		event: CdkDragDrop<string, string, KanbanWorkflow>
	): void {
		if (
			!event.isPointerOverContainer ||
			!this.canEnterColumn(event.item, event.container)
		) {
			return;
		}

		const dialogId = 'confirm-kanban-card-move';
		if (this.dialog.getDialogById(dialogId)) {
			return;
		}

		const teamDefinitionId = this.teamDefinitionId();
		const { id, title } = event.item.data;
		const targetState = event.container.data;
		this.dialog
			.open<
				ConfirmDialogComponent,
				{ title: string; text: string },
				boolean
			>(ConfirmDialogComponent, {
				id: dialogId,
				autoFocus: 'first-tabbable',
				data: {
					title: 'Confirm card move',
					text: `Are you sure you want to move “${title}” to “${targetState}”?`,
				},
			})
			.afterClosed()
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((confirmed) => {
				if (
					confirmed === true &&
					this.teamDefinitionId() === teamDefinitionId &&
					this.canEnterColumn(event.item, event.container)
				) {
					this.movedCards.update((moves) =>
						new Map(moves).set(id, targetState)
					);
				}
			});
	}
}
