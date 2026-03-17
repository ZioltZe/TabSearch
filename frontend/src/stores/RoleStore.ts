import { defineStore } from 'pinia'
import { useAuthStore } from '@/stores/AuthStore.ts';

export const useRoleStore = defineStore('role',() => {
  const userRoles: Ref<Array<string>> = ref([]);
  const auth = useAuthStore();

  function setRoles (roles: string[])
  {
    userRoles.value = roles;
  }

  function hasRole (role: string)
  {
    return auth.isLoggedIn ? userRoles.value.includes(role) : false;
  }

  return { userRoles, setRoles, hasRole };
})
