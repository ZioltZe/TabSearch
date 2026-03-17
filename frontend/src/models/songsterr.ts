export class Tabs
{
  spotifyName: string;
  spotifyArtist: string;
  matchingCount: number;
  almostMatchingCount: number;
  records: Tab[];

  constructor () {
    this.matchingCount = 0;
    this.almostMatchingCount = 0;
    this.records = [];
    this.spotifyArtist = '';
    this.spotifyName = '';
  }
}

export class Tab
{
  priority: number;
  songId: number;
  artist: string;
  title: string;
  hasChords: boolean;
  hasPlayer: boolean;

  constructor () {
    this.priority = 5;
    this.songId = 0;
    this.artist = '';
    this.title = '';
    this.hasChords = false;
    this.hasPlayer = false;
  }
}

export class LastTab
{
  songName: string;
  songArtist: string;
  tab: boolean;
  tabId: number;

  constructor () {
    this.songName = '';
    this.songArtist = '';
    this.tab = true;
    this.tabId = -2;
  }
}
