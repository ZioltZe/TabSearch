<script setup lang="ts">
  import { SpotifyPlaylist, SpotifyPlaylists } from '@/models/spotify.ts';

  const pagePlaylist = ref(1);

  const emit = defineEmits<{
    (e: 'search-playlist', playlistId: string): void
    (e: 'on-update-page', value: number): void
  }>();

  const { loadingID, playlists, playlistPageLimit, fullPlaylists, isActive, page } = defineProps<{
    loadingID: string
    playlists: SpotifyPlaylist[]
    playlistPageLimit: number
    fullPlaylists: SpotifyPlaylists
    isActive: boolean
    page: number
  }>()

  function onUpdatePage (value: number)
  {
    emit('on-update-page', value)
  }

  function searchPlaylist (playlistId: string)
  {
    isSearching.value = true;
    emit('search-playlist', playlistId)
  }

  onMounted(()=>{
    pagePlaylist.value = page;
    isSearching.value = false;
  })

  const isSearching = ref(false);

</script>

<template>
  <v-card v-if="playlists.length" class="my-4">
    <div class="d-flex justify-space-between align-center">
      <v-card-title>Your Playlists</v-card-title>
      <v-pagination
        v-if="playlists"
        v-model="pagePlaylist"
        :disabled="isSearching"
        :length="Math.ceil(fullPlaylists.total/playlistPageLimit)"
        :total-visible="10"
        @update:model-value="onUpdatePage"
      />
    </div>
    <v-progress-linear v-if="isActive" :active="isActive" indeterminate />
    <v-sheet class="pt-2 pr-2" color="primary">
      <v-row no-gutters>
        <v-col v-for="playlist in playlists" :key="playlist.id" cols="4">
          <v-card class="pa-2 ml-2 mb-2" variant="flat">
            <v-row no-gutters>
              <v-col class="mr-2" cols="3">
                <v-img
                  v-if="playlist.images && playlist.images[0]?.url"
                  max-height="150"
                  max-width="150"
                  :src="playlist.images[0].url"
                />
              </v-col>
              <v-col class="d-flex flex-column justify-space-between">
                <v-card-title>{{ playlist.name }}</v-card-title>
                <v-card-subtitle v-if="playlist.description==''">No description.</v-card-subtitle>
                <v-card-subtitle v-if="playlist.description!=''">{{ playlist.description }}</v-card-subtitle>
                <v-spacer class="my-1" />
                <v-btn color="tertiary" :disabled="loadingID!=playlist.id && loadingID!='9999'" :loading="loadingID==playlist.id" @click="searchPlaylist(playlist.id)">
                  Search this playlist
                </v-btn>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
    </v-sheet>
  </v-card>
</template>

<style scoped>

</style>
