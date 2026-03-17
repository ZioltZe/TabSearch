export class SpotifyProfile {
  id: string;
  country: string;
  display_name: string;
  email: string;
  images: Image[];

  constructor () {
    this.id = '';
    this.country = '';
    this.display_name = '';
    this.email = '';
    this.images = [];
  }
}

export class SpotifyTokens
{
  access_token: string;
  refresh_token: string;

  constructor () {
    this.access_token = '';
    this.refresh_token = '';
  }
}

export class Image {
  url: string;
  height: number;
  width: number;

  constructor ()
  {
    this.url = '';
    this.height = 30;
    this.width = 30;
  }
}

export class SpotifyPlaylist
{
  id : string;
  name : string;
  images: Image[];
  description: string;

  constructor ()
  {
    this.id = '';
    this.images = [];
    this.name = '';
    this.description = '';
  }
}

export class SpotifyPlaylists
{
  items: SpotifyPlaylist[];
  total: number;

  constructor () {
    this.items = [];
    this.total = 0;
  }
}

export class SpotifyArtist
{
  id: string;
  name: string;

  constructor () {
    this.id = '';
    this.name = '';
  }
}

export class SpotifyTrackObject
{
  id: string;
  name : string;
  artists: SpotifyArtist[];

  constructor () {
    this.id = '';
    this.name = '';
    this.artists = [];
  }
}

export class SpotifyTrack
{
  track: SpotifyTrackObject;

  constructor () {
    this.track = new SpotifyTrackObject();
  }
}

export class SpotifyTracks
{
  total: number;
  items: SpotifyTrack[];

  constructor () {
    this.total = 0;
    this.items = [];
  }
}
