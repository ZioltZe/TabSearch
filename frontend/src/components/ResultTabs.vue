<script setup lang="ts">
import {LastTab, Tabs} from '@/models/songsterr.ts';
  import axios from 'axios';
  import config from '../../config.ts';
  import { useAuthStore } from '@/stores/AuthStore';

  const auth = useAuthStore();
  const TAB_PAGE_LIMIT = 6;
  const pageTab = ref(1);

  const { allTabs } = defineProps<{
    allTabs: Tabs[]
  }>()

  const emit = defineEmits(['clear'])

  function clearTab ()
  {
    pageTab.value = 1;
    emit('clear')
  }

  function createHrefTab (id: number)
  {
    const startUrl = 'https://www.songsterr.com/a/wsa/';
    const stringId = id.toString();

    return startUrl + 'tab-s'+ stringId;
  }

  function createHrefChords (id: number)
  {
    const startUrl = 'https://www.songsterr.com/a/wsa/';
    const stringId = id.toString();

    return startUrl + 'chords-s'+ stringId;
  }

  async function saveTab (id: number, songName: string, songArtist: string, isTab: boolean)
  {
    const tab = ref<LastTab>(new LastTab());
    tab.value.tab = isTab;
    tab.value.songName = songName;
    tab.value.songArtist = songArtist;
    tab.value.tabId = id;

    if (auth.isLoggedIn)
      await addLastVisitedTabs(tab.value);
  }

  async function addLastVisitedTabs (tab: LastTab)
  {
    await axios.put(config['API_URL'] + '/user/AddTab', tab, { headers: auth.authHeader() })
      .then (() => { console.log('Added Tab') })
      .catch(() => { console.log('ERR: Adding Tab') })
  }

  function createHrefSearch (nameOrKey: string, artist?: string)
  {
    const startUrl = 'https://www.songsterr.com/?pattern=';
    if (artist)
      return startUrl + nameOrKey + ' ' + artist;
    return startUrl + nameOrKey;
  }

  function isLatinOnly (str: string): boolean {
    return /^[a-z.A-Z0-9\s]*$/.test(str);
  }

  const warningBox = ref(false);

  function checkSearch (name: string, artist: string)
  {
    const keyString= name + ' ' + artist;
    if (!isLatinOnly(keyString))
    {
      warningValue.value = keyString;
      copyClipText.value = 'Copy (' + warningValue.value + ')';
      copyClipColor.value = 'tertiary';
      copied.value = false;

      warningBox.value = true;
    }
    else
      window.open(createHrefSearch(name, artist), '_blank');
  }

  function visitSearch (keystring: string)
  {
    window.open(createHrefSearch(keystring), '_blank');
  }

  const warningValue = ref('');

  const copyToClipboard = async (text: string) => {
    try
    {
      await navigator.clipboard.writeText(text);
    }
    catch (error) { console.error('ERR: ', error); }

    copyClipText.value = 'Copied Text To Clipboard!';
    copyClipColor.value = 'correct';
    copied.value = true;
  };

  const copyClipColor = ref('');
  const copyClipText = ref('');
  const copied = ref(false);

</script>

<template>
  <v-dialog v-model="warningBox" max-width="650">
    <v-card class="pa-4">
      <div class="align-center d-flex justify-space-between">
        <v-icon icon="mdi-alert" />
        <v-card-title>  Warning</v-card-title>
        <v-spacer />
        <v-btn
          color="dark"
          icon="mdi-close"
          rounded="lg"
          size="small"
          @click="warningBox = false"
        />
      </div>
      <p>Non-latin character were detected in the name or artist of the song.</p>
      <p>There is a chance Songsterr redirects you to the homepage if we send you to the search results.</p>
      <p>What you can do is copy the song name and artist here and paste them on the search page to see all the results.</p>
      <div class="d-flex my-4 align-center justify-space-between">
        <v-btn
          class="w-auto mr-2"
          :color="copyClipColor"
          :disabled="copied"
          :text="copyClipText"
          @click="copyToClipboard(warningValue)"
        />
        <v-btn color="primary" text="Go To Songsterr Search Page" @click="visitSearch(warningValue)" />
      </div>
    </v-card>
  </v-dialog>


  <v-card class="pr-2 pb-2 mt-4" color="primary">
    <v-card class="ml-2 mt-2">
      <div class="d-flex justify-space-between align-center">
        <v-btn class="my-4 mx-4" color="primary" text="<" @click="clearTab()" />
        <v-card-title>Tabs Found</v-card-title>
        <v-spacer />
        <v-pagination
          v-if="allTabs"
          v-model="pageTab"
          class="mr-2"
          :length="Math.ceil(allTabs.length/TAB_PAGE_LIMIT)"
          :total-visible="10"
        />
      </div>
    </v-card>
    <v-row no-gutters>
      <v-col v-for="(songTab, index) in allTabs.slice((pageTab-1)*TAB_PAGE_LIMIT, pageTab*TAB_PAGE_LIMIT)" :key="index" cols="6">
        <v-card class="ml-2 mt-2 pa-4">
          <h3>Tabs for <strong>{{ songTab.spotifyName }}</strong></h3>
          <h4>by <strong>{{ songTab.spotifyArtist }}</strong></h4>
          <div v-for="tab in songTab.records.slice(0, 3)" :key="tab.songId" class="d-flex">
            <v-btn
              v-if="tab.hasPlayer && tab.priority<5"
              class="my-4 mr-4"
              :color="(tab.priority==1)?'tertiary':'grey'"
              :href="createHrefTab(tab.songId)"
              target="_blank"
              @click="saveTab(tab.songId, tab.title, tab.artist, true)"
            >
              TAB
            </v-btn>
            <v-btn
              v-if="tab.hasChords && tab.priority<5"
              class="my-4 mr-4"
              :color="(tab.priority==1)?'tertiary':'grey'"
              :href="createHrefChords(tab.songId)"
              target="_blank"
              @click="saveTab(tab.songId, tab.title, tab.artist, false)"
            >
              CHORDS
            </v-btn>
            <div v-if="tab.priority<5" class="text-truncate">
              <v-chip class="my-4" label variant="tonal"> <b>{{ tab.title }}</b>&nbsp;&nbsp;<em>by</em>&nbsp;&nbsp;{{ tab.artist }} </v-chip>
            </div>
          </div>
          <v-btn
            v-if="songTab.almostMatchingCount + songTab.matchingCount > 3"
            class="mt-4 w-100"
            color="primary"

            @click="checkSearch(songTab.spotifyName, songTab.spotifyArtist)"
          >
            See All
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>

</style>
