export interface AppNotification {
  id: string;
  userId: string;
  type: string;
  title: string;
  body: string;
  channel: string;
  isRead: boolean;
  createdAt: string;
}