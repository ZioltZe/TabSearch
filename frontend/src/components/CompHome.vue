<script setup lang="ts">
  const tabString = ref('Tab found, Song matched exactly');
  const isExactTab = ref(true);
  const tabColor = ref('tertiary');
  const chordString = ref('Chords found, Possible song match, not guaranteed');
  const isExactChords = ref(false);
  const chordColor = ref('grey');

  function changeTab ()
  {
    isExactTab.value = ! isExactTab.value;
    tabString.value = isExactTab.value ? 'Tab found, Song matched exactly' : 'Tab found, Possible song match, not guaranteed';
    tabColor.value = isExactTab.value ? 'tertiary' : 'grey';
  }

  function changeChords ()
  {
    isExactChords.value = ! isExactChords.value;
    chordString.value = isExactChords.value ? 'Chords found, Song matched exactly' : 'Chords found, Possible song match, not guaranteed';
    chordColor.value = isExactChords.value ? 'tertiary' : 'grey';
  }
</script>

<template>
  <v-container class="fill-height d-flex flex-column align-center justify-center text-center" fluid>
    <v-img class="mb-4 mt-16" src="@/assets/plectrum-white1.png" width="200" />
    <h1 class="text-h2 font-weight-bold mb-4">TabSearch</h1>
    <v-img class="mb-4" src="@/assets/plectrum-white2.png" width="200" />

    <p class="text-subtitle-1 mb-16">
      Finding tabs made easy.
    </p>
    <p class="text-h5 mb-6 mx-4">
      Ever wondered what songs in your Spotify playlists had a tab online?
    </p>
    <v-btn
      class="mb-16"
      color="primary"
      size="large"
      to="/PageSearch"
      variant="flat"
    >Start searching for tabs</v-btn>

    <div class="mt-16 d-flex justify-start w-100">
      <v-card class="text-left">
        <v-card-title>1. Connect with Spotify & select a playlist</v-card-title>
        <v-img class="mx-4 mb-4" src="@/assets/Tabsearch_ResultPlaylist.png" width="1200" />
      </v-card>
    </div>


    <div class="mt-16 d-flex justify-end w-100">
      <v-card class="text-right">
        <v-card-title>2. Find your songs with tabs</v-card-title>
        <v-img class="mx-4 mb-4" src="@/assets/Tabsearch_ResultTabs.png" width="1200" />
      </v-card>
    </div>

    <div class="mt-16 d-flex justify-start w-100">
      <v-card class="text-left">
        <v-card-title>3. Choose any of the resulted tabs</v-card-title>
        <v-col class="ma-4">
          <v-row>
            <v-btn
              class="mb-2 mr-4"
              :color="tabColor"
              @click="changeTab"
            >
              TAB
            </v-btn>
            <h3>{{ tabString }}</h3>
          </v-row>
          <v-row>
            <v-btn
              class="mr-4"
              :color="chordColor"
              @click="changeChords"
            >
              CHORDS
            </v-btn>
            <h3>{{ chordString }}</h3>

          </v-row>
        </v-col>
      </v-card>
    </div>

    <div class="mt-16 d-flex justify-end w-100">
      <v-card class="text-right">
        <v-card-title>4. Play your songs in Songsterr</v-card-title>
        <v-img class="mx-4 mb-4" src="@/assets/Tabsearch_ResultSongsterr.png" width="1200" />
      </v-card>
    </div>

    <v-btn
      class="mt-16"
      color="primary"
      size="large"
      to="/PageSearch"
      variant="flat"
    >Search Your Playlists Here</v-btn>
  </v-container>
</template>

<style scoped>

</style>
