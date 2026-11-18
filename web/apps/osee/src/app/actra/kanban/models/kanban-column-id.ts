export const kanbanColumnIds = [
	'open',
	'analyze',
	'implement',
	'review',
	'completed',
	'cancelled',
] as const;

export type KanbanColumnId = (typeof kanbanColumnIds)[number];
