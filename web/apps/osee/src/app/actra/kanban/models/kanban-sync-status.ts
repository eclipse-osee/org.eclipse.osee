export const kanbanSyncStatuses = ['synced', 'pending', 'error'] as const;

export type KanbanSyncStatus = (typeof kanbanSyncStatuses)[number];
