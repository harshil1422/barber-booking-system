export interface UserProfile {
  id: string;
  authUserId: string;
  fullName: string;
  avatarUrl: string | null;
  phoneNumber: string;
  email: string;
  initials: string;   // computed on login, e.g. 'AK'
}