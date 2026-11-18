import { KanbanBoard } from './kanban-board';
import { KanbanCard } from './kanban-card';
import { KanbanColumn } from './kanban-column';
import { kanbanColumnIds } from './kanban-column-id';
import { kanbanSyncStatuses } from './kanban-sync-status';

describe('Kanban models', () => {
	const card: KanbanCard = {
		id: 'card-1',
		title: 'Review the board layout',
		columnId: 'review',
		assignees: ['Ali Shaban'],
		syncStatus: 'error',
	};

	const column: KanbanColumn = {
		id: 'review',
		title: 'Review',
		cards: [card],
	};

	const board: KanbanBoard = {
		columns: [column],
	};

	it('should list the sync statuses', () => {
		expect(kanbanSyncStatuses).toEqual(['synced', 'pending', 'error']);
	});

	it('should list the six board columns', () => {
		expect(kanbanColumnIds).toEqual([
			'open',
			'analyze',
			'implement',
			'review',
			'completed',
			'cancelled',
		]);
	});

	it('should describe a card', () => {
		expect(card.columnId).toBe('review');
		expect(card.assignees).toEqual(['Ali Shaban']);
		expect(card.syncStatus).toBe('error');
	});

	it('should describe a column of cards', () => {
		expect(column.title).toBe('Review');
		expect(column.cards[0].id).toBe('card-1');
	});

	it('should describe a board of columns', () => {
		expect(board.columns).toHaveLength(1);
		expect(board.columns[0].id).toBe('review');
	});

	it('should keep an error card on the board', () => {
		const errorCard = board.columns[0].cards.find(
			(item) => item.syncStatus === 'error'
		);
		expect(errorCard?.title).toBe('Review the board layout');
	});
});
