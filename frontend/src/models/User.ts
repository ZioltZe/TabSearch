import { jwtDecode } from 'jwt-decode';
import { Image } from '@/models/spotify.ts';

export class UserSpotifyProfile
{
  id: number;
  country: string;
  displayName: string;
  email: string;
  spotifyId: string;
  spotifyImage: Image;
  accessToken: string;
  refreshToken: string;

  constructor () {
    this.id = 99;
    this.country = 'DEFAULT_COUNTRY';
    this.displayName = 'DEFAULT_DISPLAY_NAME';
    this.email = 'DEFAULT_EMAIL';
    this.spotifyId = 'DEFAULT_SPOTIFY_ID';
    this.spotifyImage = new Image();
    this.accessToken = 'DEFAULT_TOKEN';
    this.refreshToken = 'DEFAULT_TOKEN';
  }
}

export class UserCredentials {
  email: string;
  password: string;

  constructor (email: string, password: string) {
    this.email = email;
    this.password = password;
  }
}

export class UserNew
{
  email: string;
  password: string;
  repeatPassword: string;

  constructor (email: string, password: string, repeatPassword: string)
  {
    this.email = email;
    this.password = password;
    this.repeatPassword = repeatPassword;
  }
}

export class UserPass
{
  oldPassword: string;
  newPassword: string;
  repeatPassword: string;

  constructor (oldPassword: string, newPassword: string, repeatPassword: string) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.repeatPassword = repeatPassword;
  }
}

export class JWTUser{
  email: string;
  roles: string[];
  exp: number;
  iat: number;
  iss: string;
  sub: string;
  constructor (email: string, roles: string[], exp: number, iat: number, iss: string, sub: string) {
    this.email = email;
    this.roles = roles;
    this.exp = exp;
    this.iat = iat;
    this.iss = iss;
    this.sub = sub;
  }
}

export class JWTToken {
  token: string;

  constructor (token: string) {
    this.token = token;
  }
  decode (): JWTUser {
    return jwtDecode(this.token);
  }
}
