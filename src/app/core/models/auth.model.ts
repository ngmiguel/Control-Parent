export interface DemandeCodeRequest {
  telephone: string;
}

export interface VerificationCodeRequest {
  telephone: string;
  code: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  telephone: string;
  message: string;
}

export interface MessageResponse {
  message: string;
  success: boolean;
}