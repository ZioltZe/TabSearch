<script setup lang="ts">

  import { UserCredentials, UserNew, UserPass, UserSpotifyProfile } from '@/models/User.ts';
  import { useAuthStore } from '@/stores/AuthStore.ts';
  import axios from 'axios';
  import config from '../../config.ts';
  import type { LastTab } from '@/models/songsterr.ts';

  const form = ref(false)
  const email = ref('')
  const pass = ref('')
  const auth = useAuthStore();
  const success = ref(false);
  const error = ref(false);
  const snackText = ref('');

  let profileLoaded = false

  const profile = ref<UserSpotifyProfile>(new UserSpotifyProfile());

  const changePassword = ref(false);
  const removeAccount = ref(false);

  const lvTabs = ref<LastTab[]>();

  const userPass = ref<UserPass>(new UserPass('', '', ''));

  function onSubmit ()
  {
    if(!form.value) return;

    const user = new UserCredentials(email.value, pass.value);

    auth.login(user)
      .then(() => {
        snackText.value = 'Logged in successfully';
        success.value = true;
      })
      .catch(() => {
        snackText.value = 'Failed to login';
        error.value = true;
      })

    email.value = '';
    pass.value = '';
  }

  onMounted(async ()=>{
    watch(
      () => auth.isLoggedIn,
      async logged => {
        if (logged && !profileLoaded) {
          profileLoaded = true;
          console.log('User logged in, loading profile…')
          await getProfile();
          await getLastVisitedTabs();
        }
      },
      { immediate: true }
    )
  })

  async function getProfile ()
  {
    const result = await axios.get(config['API_URL'] + '/spotify/profile/' + auth.userInfo?.email, { headers: auth.authHeader() });
    if(result && result.data) {
      profile.value = result.data;
    }
  }

  async function getLastVisitedTabs ()
  {
    const result = await axios.get(config['API_URL'] + '/user/Tabs', { headers: auth.authHeader() });
    if (result && result.data)
      lvTabs.value = result.data;
  }

  function createHref (id: number, isTab: boolean)
  {
    const startUrl = 'https://www.songsterr.com/a/wsa/';
    const stringId = id.toString();

    if (!isTab)
      return startUrl + 'chords-s'+ stringId;
    else
      return startUrl + 'tab-s'+ stringId;

  }

  const oldPassString = ref('');
  const newPassString = ref('');
  const repeatPassString = ref('');
  const passWrong = ref(false);
  const resultPass = ref(false);

  async function updatePassword ()
  {
    userPass.value.newPassword = newPassString.value;
    userPass.value.oldPassword = oldPassString.value;
    userPass.value.repeatPassword = repeatPassString.value;

    const result = await axios.put(config['API_URL'] + '/user/changePass', userPass.value, { headers: auth.authHeader() });
    console.log(result);
    if (result)
    {
      resultPass.value = result.data;
      if (!resultPass.value) passWrong.value = true;
      else changePassword.value = false;
    }
    else console.log('ERR');

    newPassString.value = '';
    oldPassString.value = '';
    repeatPassString.value = '';
  }

  async function deleteUser ()
  {
    await axios.delete(config['API_URL'] + '/user/delete', { headers: auth.authHeader() })
      .then(()=>
      {
        console.log('user Deleted');
      })
      .catch(() => {
        console.log("ERR: Couldn't Remove User");
      })
    auth.logout();
    removeAccount.value = false;
  }

  const newUser = ref(false);
  const userWrong = ref(false);
  const userResultNew = ref(false);
  const newUserEmail = ref('');
  const newUserPass = ref('');
  const newUserRepeat = ref('');
  const newUserData = ref<UserNew>(new UserNew('', '', ''));

  async function createNewUser ()
  {
    newUserData.value.email = newUserEmail.value;
    newUserData.value.password = newUserPass.value;
    newUserData.value.repeatPassword = newUserRepeat.value;

    console.log(newUserData);
    const result = await axios.post(config['API_URL'] + '/user/newUser', newUserData.value)

    console.log(result);
    if(result)
    {
      userResultNew.value = result.data;
      if (!userResultNew.value) userWrong.value = true;
      else
      {
        newUser.value = false
        snackText.value = 'Singed Up Successfully!'
        success.value = true;
      }
    }
    else console.log('ERR');

    newUserData.value = new UserNew('', '', '');
    newUserEmail.value = '';
    newUserPass.value = '';
    newUserRepeat.value = '';
  }
</script>

<template>
  <v-snackbar v-model="success" color="success">{{ snackText }}</v-snackbar>
  <v-snackbar v-model="error" color="error">{{ snackText }}</v-snackbar>
  <v-dialog v-model="changePassword" width="1000">
    <v-card>
      <v-card-title>Change Password</v-card-title>
      <v-chip v-if="passWrong" class="mx-4 mt-4" color="red">Password couldn't be updated, try again.</v-chip>
      <div class="ma-4">
        <v-text-field v-model="oldPassString" label="Old Password" :type="'password'" />
        <v-text-field v-model="newPassString" label="New Password" :type="'password'" />
        <v-text-field v-model="repeatPassString" label="Repeat Password" :type="'password'" />
        <v-btn color="primary" @click="updatePassword">Change Password</v-btn>
      </div>
    </v-card>
  </v-dialog>

  <v-dialog v-model="newUser" width="1000">
    <v-card>
      <v-card-title>Sign Up</v-card-title>
      <v-chip v-if="userWrong" class="mx-4 mt-4" color="red">User couldn't be created, try again.</v-chip>
      <div class="ma-4">
        <v-text-field v-model="newUserEmail" label="Email" />
        <v-text-field v-model="newUserPass" label="Password" :type="'password'" />
        <v-text-field v-model="newUserRepeat" label="Repeat Password" :type="'password'" />
        <v-btn color="primary" @click="createNewUser">Create Account</v-btn>
      </div>
    </v-card>
  </v-dialog>

  <v-dialog v-model="removeAccount" width="750">
    <v-card>
      <v-card-title>Remove Account</v-card-title>
      <div class="mx-4 mb-4">
        <p>Thank you for using our services!</p>
        <p>You can remove your account here.</p>
        <p>Everything will be deleted from our databases, this includes:</p>
        <p class="font-italic"> - Your email and password</p>
        <p class="font-italic"> - Your Spotify Account Information</p>
        <p class="font-italic"> - Your last visited tabs</p>
        <div class="d-flex mt-4">
          <v-btn color="dark" @click="removeAccount = false">Cancel</v-btn>
          <v-spacer />
          <v-btn color="red" @click="deleteUser">Delete</v-btn>
        </div>
      </div>
    </v-card>
  </v-dialog>

  <v-container fluid>
    <v-row v-if="!auth.isLoggedIn">
      <v-col>
        <v-card class="pa-4" color="primary">
          <v-form v-model="form" @submit.prevent="onSubmit">
            <h3 class="mb-4">Login</h3>
            <v-text-field v-model="email" label="username" variant="outlined" />
            <v-text-field v-model="pass" label="password" :type="'password'" variant="outlined" />
            <v-btn :disabled="!form" type="submit">Login</v-btn>
          </v-form>
        </v-card>
      </v-col>
      <v-col>
        <v-card class="pa-4">
          <h3 class="mb-4">Sign Up</h3>
          <h4>Not signed up yet? Create a new account here.</h4>
          <p>This will allow you to save you spotify profile for future use without having to login every time.</p>
          <p>The things we save in our database:</p>
          <p> - Your Spotify Profile (username, id, email, profile picture, country)</p>
          <p> - Your user information (email, hashed password)</p>
          <p> - Your last 3 visited tabs</p>
          <p class="text-caption font-italic">* We don't share any of this information *</p>
          <v-btn class="mt-4" color="primary" @click="newUser = true">Sign Up</v-btn>
        </v-card>
      </v-col>
    </v-row>

    <div v-if="auth.isLoggedIn">
      <v-card>
        <v-card-title>Welcome {{ auth.userInfo?.email }}</v-card-title>
        <v-card class="ma-4" color="dark">
          <div v-if="profile.displayName != 'DEFAULT_DISPLAY_NAME'">
            <v-card-title class="d-flex align-center">
              <p>Spotify</p>
              <v-chip class="ml-4" color="green" size="small">Connected</v-chip>
            </v-card-title>
            <v-list class="ma-4">
              <v-list-group>
                <template #activator="{ props }">
                  <v-list-item v-bind="props" class="text-h6">Spotify Data</v-list-item>
                </template>

                <v-list-item>Name: <b>{{ profile.displayName }}</b></v-list-item>
                <v-list-item>ID: <b>{{ profile.spotifyId }}</b></v-list-item>
                <v-list-item>Email: <b>{{ profile.email }}</b></v-list-item>
                <v-list-item>Country: <b>{{ profile.country }}</b></v-list-item>
                <v-list-item>Profile Picture: <b>{{ profile.spotifyImage.url }}</b></v-list-item>

              </v-list-group>
              <p class="mx-4 text-caption font-italic">* We show all the information we keep in our database and we don't share anything. *</p>
            </v-list>

            <v-list v-if="lvTabs && lvTabs.length>0" class="ma-4">
              <v-list-item class="text-h6">Last Visited Tabs</v-list-item>
              <v-list v-for="(tab, index) in lvTabs" :key="index">
                <v-btn class="ml-4" color="tertiary" :href="createHref(tab.tabId, tab.tab)" target="_blank">{{ tab.songArtist }} - {{ tab.songName }}</v-btn>
              </v-list>
              <p class="mx-4 text-caption font-italic">* We only save 3 tabs in our database and we don't use it for anything. *</p>
            </v-list>

          </div>
          <div v-else>
            <v-btn class="ma-4" color="primary" to="/PageSearch">Connect Spotify</v-btn>
          </div>
        </v-card>

        <v-card class="ma-4" color="dark">
          <v-card-title>Edit Profile</v-card-title>
          <div class="d-flex align-center ma-4">
            <v-btn class="mr-4" color="primary" @click="changePassword=true">Change User Password</v-btn>
            <v-spacer />
            <v-btn class="ml-4" color="red" @click="removeAccount = true">Remove account</v-btn>
          </div>
        </v-card>
      </v-card>
      <v-btn class="mt-4" @click="auth.logout()">
        <v-icon>mdi-logout</v-icon>
      </v-btn>
    </div>

  </v-container>
</template>

<style scoped>

</style>
