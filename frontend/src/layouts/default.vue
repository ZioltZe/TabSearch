<template>
  <v-app-bar>
    <v-app-bar-nav-icon class="ml-2" rounded="lg" to="/">
      <v-img src="@/assets/plectrum-white.png" width="20" />
    </v-app-bar-nav-icon>
    <v-app-bar-title><b>Tab Search</b></v-app-bar-title>
    <div class="mx-2">
      <v-btn class="mr-2" text to="/">Home</v-btn>
      <v-btn class="mr-2" text to="/PageSearch">Search</v-btn>
      <v-btn v-if="!auth.isLoggedIn" class="mr-2" text to="/PageLogin">Login</v-btn>
      <v-btn
        v-if="auth.isLoggedIn"
        class="mr-2"
        icon="mdi-account"
        rounded="lg"
        size="small"
        to="/PageLogin"
      />
    </div>
  </v-app-bar>

  <v-main>
    <router-view />
  </v-main>

  <AppFooter />
</template>

<script lang="ts" setup>
  import { useAuthStore } from '@/stores/AuthStore.ts';

  const auth = useAuthStore()
</script>
