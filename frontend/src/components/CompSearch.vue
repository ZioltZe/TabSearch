<script setup lang="ts">
  import {
    SpotifyPlaylist,
    SpotifyPlaylists,
    SpotifyProfile, SpotifyTokens,
    SpotifyTracks,
  } from '@/models/spotify.ts';
  import { useAuthStore } from '@/stores/AuthStore.ts';
  import axios from 'axios';
  import config from '../../config.ts';
  import { UserSpotifyProfile } from '@/models/User.ts';
  import { Tabs } from '@/models/songsterr.ts';
  import { useRoleStore } from '@/stores/RoleStore.ts';

  const router = useRouter();

  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');

  const tokens = ref<SpotifyTokens>(new SpotifyTokens());
  const tokensResponse = ref<SpotifyTokens>(new SpotifyTokens());

  const spotifyProfile = ref<SpotifyProfile>(new SpotifyProfile());
  const userSpotifyProfile = ref<UserSpotifyProfile>(new UserSpotifyProfile());

  const playlists = ref<SpotifyPlaylist[]>([]);
  const fullPlaylists = ref<SpotifyPlaylists>(new SpotifyPlaylists());

  const allTabs = ref<Tabs[]>([])

  const auth = useAuthStore();

  const tracks = ref<SpotifyTracks>(new SpotifyTracks());
  const songsterrTabs = ref<Tabs>(new Tabs());

  const playlistPageLimit = 12;
  const loadingID = ref<string>('9999');

  const inPlaylist = ref(false);

  const isActiveLoading = ref(false);

  const pagePlaylist = ref(1);

  const role = useRoleStore();

  async function fetchCode ()
  {
    const result = await axios.get(config['API_URL'] + '/spotify/auth/login');
    if (result && result.data)
    {
      document.location = result.data;
    }
    else { console.log(result.status); }
  }

  async function fetchToken ()
  {
    const result = await axios.get(config['API_URL'] + '/spotify/auth/callback?code=' + code);
    if (result && result.data)
    {
      tokens.value = result.data;
    }
    else { console.log(result.status); }
  }

  onMounted(async () =>
  {
    if (code)
    {
      await fetchToken();

      spotifyProfile.value = await fetchProfile(tokens.value.access_token);
      userSpotifyProfile.value = toUserSpotifyProfile(spotifyProfile.value, tokens.value);
      console.log('test profile: ' + userSpotifyProfile.value.spotifyId + ' - ' + userSpotifyProfile.value.id);

      fullPlaylists.value = await fetchPlaylists(tokens.value.access_token, playlistPageLimit, 0);
      playlists.value = fullPlaylists.value.items;

      if(auth.isLoggedIn)
      {
        await axios.post(config['API_URL'] + '/spotify/profile/'+ auth.userInfo?.email +'/connect', userSpotifyProfile.value, { headers: auth.authHeader() })
          .then()
          .catch(e => { console.log('error', e); })
      }

      await router.push('/PageSearch')
    }

    if(auth.isLoggedIn)
    {
      const result = await axios.get(config['API_URL'] + '/spotify/profile/' + auth.userInfo?.email, { headers: auth.authHeader() });
      if(result && result.data)
      {
        userSpotifyProfile.value = result.data;
        if(userSpotifyProfile.value.displayName != 'DEFAULT_DISPLAY_NAME')
        {
          await checkTokens();
          console.log('ERR: tokens: ' + userSpotifyProfile.value.accessToken + ' //// ' + userSpotifyProfile.value.refreshToken);
          fullPlaylists.value = await fetchPlaylists(userSpotifyProfile.value.accessToken, playlistPageLimit, 0);
          playlists.value = fullPlaylists.value.items;
        }
      }
      else { console.log(result.status) }
    }
  });

  async function checkTokens ()
  {
    console.log('-----CHECKING TOKENS-----')
    console.log('-----Current Tokens [access] - [refresh]' + userSpotifyProfile.value.accessToken + ' //// ' + userSpotifyProfile.value.refreshToken)
    const resultPlaylist = await fetch('https://api.spotify.com/v1/users/'+userSpotifyProfile.value.spotifyId+'/playlists',
                                       { method: 'GET', headers: { Authorization: `Bearer ${userSpotifyProfile.value.accessToken}` } });
    console.log('-----Result = ' + resultPlaylist.status)
    if(resultPlaylist.status === 401)
    {
      console.log('-------Refreshing Tokens')
      // Refresh token if access token is expired
      const refreshed = await axios.post(config['API_URL'] + '/spotify/auth/refresh?token=' + userSpotifyProfile.value.refreshToken);
      if (refreshed && refreshed.data)
      {
        console.log('-------Data:');
        console.log(refreshed.data);
        tokensResponse.value = refreshed.data;

        if(tokensResponse.value.access_token)
          userSpotifyProfile.value.accessToken = tokensResponse.value.access_token;
        if(tokensResponse.value.refresh_token)
          userSpotifyProfile.value.refreshToken = tokensResponse.value.refresh_token;

        console.log('-------Tokens:');
        console.log(tokens.value);
        await axios.put(config['API_URL'] + '/spotify/profile/' + auth.userInfo?.email
                          + '/updateTokens?access=' + userSpotifyProfile.value.accessToken
                          + '&refresh=' + userSpotifyProfile.value.refreshToken,
                        null , { headers: auth.authHeader() })
          .catch( e => console.log('error', e) )
        console.log('-------REFRESHED TOKENS!!');
      }
    }
    console.log('-----Final Tokens [access] - [refresh]' + userSpotifyProfile.value.accessToken + ' //// ' + userSpotifyProfile.value.refreshToken)
    console.log('-----CHECKED TOKENS-----')
  }

  async function searchPlaylist (id: string)
  {
    loadingID.value = id;

    const result = await fetch('https://api.spotify.com/v1/playlists/' + id + '/tracks',
                               { method: 'GET', headers: { Authorization: `Bearer ${userSpotifyProfile.value.accessToken}` } });
    tracks.value = await result.json()

    for (const track of tracks.value.items)
    {
      // fetch songsterr
      const keyString = (track.track.name + ' ' + track.track.artists[0].name)
      const encodedKey = encodeURIComponent(keyString);
      const tabs = await axios.get(config['API_URL'] + '/songsterr/search?key=' + encodedKey);
      if (tabs && tabs.data)
      {
        songsterrTabs.value = tabs.data;
        songsterrTabs.value.spotifyName = track.track.name;
        songsterrTabs.value.spotifyArtist = track.track.artists[0].name;
        songsterrTabs.value.almostMatchingCount = 0;
        songsterrTabs.value.matchingCount = 0;

        // FILTER TABS
        for (const record of songsterrTabs.value.records)
        {
          const normalizedNameTab = normalizeTab(record.title);
          const normalizedArtistTab = normalizeTab(record.artist);
          const normalizedNameTrack = normalizeTab(track.track.name);
          const normalizedArtistTrack = normalizeTab(track.track.artists[0].name);

          if (normalizedNameTab === normalizedNameTrack && normalizedArtistTab === normalizedArtistTrack)
          { record.priority = 1; songsterrTabs.value.matchingCount += 1; }
          else if (normalizedNameTab === normalizedNameTrack || normalizedArtistTab === normalizedArtistTrack)
          { record.priority = 2; songsterrTabs.value.almostMatchingCount +=1; }
          else if (normalizedNameTab.includes(normalizedNameTrack) && normalizedArtistTab.includes(normalizedArtistTrack))
          { record.priority = 3; songsterrTabs.value.almostMatchingCount +=1; }
          else if (normalizedNameTab.includes(normalizedNameTrack) || normalizedArtistTab.includes(normalizedArtistTrack))
          { record.priority = 4; songsterrTabs.value.almostMatchingCount +=1; }
          else
            record.priority = 5;
        }

        if(songsterrTabs.value.matchingCount + songsterrTabs.value.almostMatchingCount > 0)
        {
          songsterrTabs.value.records.sort((a, b) => (a.priority - b.priority));
          allTabs.value.push(songsterrTabs.value);

        }
      }
    }
    allTabs.value.sort((a, b) => (b.matchingCount - a.matchingCount));
    inPlaylist.value = true;
    loadingID.value = '9999';
  }

  async function onUpdatePage (value: number)
  {
    isActiveLoading.value = true;
    if(auth.isLoggedIn) await checkTokens();
    const token = auth.isLoggedIn ? userSpotifyProfile.value.accessToken : tokens.value.access_token;
    fullPlaylists.value = await fetchPlaylists(token, playlistPageLimit, (value-1)*playlistPageLimit);
    playlists.value = fullPlaylists.value.items;

    pagePlaylist.value = value;

    isActiveLoading.value = false;
  }

  function TestAccess ()
  {
    userSpotifyProfile.value.accessToken = 'abc';
  }

  function normalizeTab (input: string): string
  {
    if (!input) return '';

    return input
      .normalize('NFKC')
      .toLowerCase()
      .replace(/\s+/g, ' ')
      .trim();
  }

  async function redirectToSpotify ()
  {
    await fetchCode();
  }

  async function fetchProfile (code: string): Promise<SpotifyProfile>
  {
    const result = await fetch('https://api.spotify.com/v1/me',
                               { method: 'GET', headers: { Authorization: `Bearer ${code}` } });
    return await result.json();
  }

  async function fetchPlaylists (code: string, limit: number, offset: number): Promise<SpotifyPlaylists>
  {
    const result = await fetch('https://api.spotify.com/v1/users/'+userSpotifyProfile.value.spotifyId+'/playlists?limit='+limit+'&offset='+offset,
                               { method: 'GET', headers: { Authorization: `Bearer ${code}` } });
    return await result.json();
  }

  function toUserSpotifyProfile (profile: SpotifyProfile, tokens: SpotifyTokens)
  {
    const userSpotifyProfile = new UserSpotifyProfile();
    userSpotifyProfile.country = profile.country;
    userSpotifyProfile.displayName = profile.display_name;
    userSpotifyProfile.email = profile.email;
    userSpotifyProfile.spotifyId = profile.id;
    userSpotifyProfile.spotifyImage = profile.images[0];
    userSpotifyProfile.accessToken = tokens.access_token;
    userSpotifyProfile.refreshToken = tokens.refresh_token;
    return userSpotifyProfile;
  }


  function handleClearTabs ()
  {
    allTabs.value = [];
    inPlaylist.value = false;
  }

</script>

<template>
  <div v-if="userSpotifyProfile.id!=99 && userSpotifyProfile.spotifyId=='DEFAULT_SPOTIFY_ID'" class="d-flex flex-column align-center">
    <h3 class="mb-4">To search for tabs, we need you to connect with spotify!</h3>
    <v-btn @click="redirectToSpotify">
      Connect Spotify
    </v-btn>
  </div>

  <div v-else-if="userSpotifyProfile.spotifyId=='DEFAULT_SPOTIFY_ID'" class="d-flex flex-column align-center">
    <h3 class="mb-4">To search for tabs, we need you to connect with spotify or login!</h3>
    <v-btn @click="redirectToSpotify">
      Connect Spotify
    </v-btn>
  </div>

  <div v-if="!(userSpotifyProfile.displayName=='DEFAULT_DISPLAY_NAME')" class="ma-2">
    <div class="d-flex">
      <v-card class="pa-1 mr-4">
        <v-img
          v-if="userSpotifyProfile.spotifyImage?.url"
          :height="65"
          :src="userSpotifyProfile.spotifyImage.url"
          :width="65"
        />
      </v-card>
      <v-card class="d-flex pa-1 px-6 w-100">
        <div class="d-flex align-center justify-space-between w-100">
          <v-card-title class="ma-0 pa-0">Welcome {{ userSpotifyProfile.displayName }}</v-card-title>
          <v-card-subtitle>Spotify ID : {{ userSpotifyProfile.spotifyId }}</v-card-subtitle>
        </div>
      </v-card>
    </div>

    <div v-if="inPlaylist">
      <ResultTabs :all-tabs="allTabs" :in-playlist="inPlaylist" @clear="handleClearTabs" />
    </div>

    <div v-if="!inPlaylist">
      <ResultPlaylists
        :full-playlists="fullPlaylists"
        :is-active="isActiveLoading"
        :loading-i-d="loadingID"
        :page="pagePlaylist"
        :playlist-page-limit="playlistPageLimit"
        :playlists="playlists"
        @on-update-page="onUpdatePage"
        @search-playlist="searchPlaylist"
      />
    </div>

    <v-btn class="mt-4" @click="redirectToSpotify">
      Update Spotify
    </v-btn>
    <v-btn v-if="role.hasRole('ROLE_ADMIN')" class="mt-4 ml-4" @click="TestAccess">
      Change Access Token
    </v-btn>
  </div>
</template>

<style scoped>

</style>
