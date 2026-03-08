export interface OtpRequest {
  phone: string;
}

export interface OtpVerify {
  phone: string;
  code: string;
}

export interface AuthResponse {
  token: string; // Si tu utilises JWT
  parentName: string;
  phone: string;
}