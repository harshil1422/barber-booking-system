export interface Review {
  id: string;
  bookingId: string;
  userId: string;
  shopId: string;
  rating: number;
  comment: string;
  isVerified: boolean;
  createdAt: string;
  reviewerName: string;    // joined from user profile
  reviewerInitials: string;
}
 
export interface MediaAsset {
  id: string;
  shopId: string;
  assetType: 'IMAGE' | 'VIDEO';
  cdnUrl: string;
  thumbnailUrl: string;
  mimeType: string;
}