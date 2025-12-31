import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/products'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/views/product/ProductList.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/ProductDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/cart/Cart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/order/OrderList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders/:orderNo',
    name: 'OrderDetail',
    component: () => import('@/views/order/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders/statistics',
    name: 'OrderStatistics',
    component: () => import('@/views/order/OrderStatistics.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/order/Checkout.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/payment/:orderNo',
    name: 'Payment',
    component: () => import('@/views/payment/Payment.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/addresses',
    name: 'Addresses',
    component: () => import('@/views/user/AddressList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/notification/NotificationList.vue'),
    meta: { requiresAuth: true }
  },
  // 管理端路由
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/products'
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/product/ProductList.vue')
      },
      {
        path: 'products/create',
        name: 'AdminProductCreate',
        component: () => import('@/views/admin/product/ProductForm.vue')
      },
      {
        path: 'products/:id/edit',
        name: 'AdminProductEdit',
        component: () => import('@/views/admin/product/ProductForm.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/order/OrderList.vue')
      },
      {
        path: 'orders/:orderNo',
        name: 'AdminOrderDetail',
        component: () => import('@/views/admin/order/OrderDetail.vue')
      },
      {
        path: 'orders/statistics',
        name: 'AdminOrderStatistics',
        component: () => import('@/views/admin/order/OrderStatistics.vue')
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/admin/report/Report.vue')
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: () => import('@/views/notification/NotificationList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 如果已登录但用户信息未加载，先加载用户信息（刷新页面时）
  if (userStore.isLoggedIn && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (error) {
      // 如果获取用户信息失败，清除token并跳转到登录页
      console.error('获取用户信息失败:', error)
      if (error.response?.status === 401 || error.response?.status === 403) {
        userStore.logout()
        next({ name: 'Login', query: { redirect: to.fullPath } })
        return
      }
    }
  }
  
  // 如果访问根路径，根据用户角色重定向
  if (to.path === '/') {
    if (userStore.isLoggedIn && userStore.isAdmin) {
      next({ path: '/admin/products' })
      return
    } else {
      next({ path: '/products' })
      return
    }
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    // 如果用户信息还未加载，等待加载完成
    if (userStore.isLoggedIn && !userStore.userInfo) {
      try {
        await userStore.fetchUserInfo()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        userStore.logout()
        next({ name: 'Login', query: { redirect: to.fullPath } })
        return
      }
    }
    
    // 检查管理员权限
    if (!userStore.isAdmin) {
      // 如果不是管理员，重定向到用户端首页
      next({ name: 'Products' })
      return
    }
  }
  
  // 如果已登录，访问登录/注册页时根据角色重定向
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
    if (userStore.isAdmin) {
      next({ path: '/admin/products' })
    } else {
      next({ name: 'Products' })
    }
    return
  }
  
  // 如果管理员访问需要认证的用户端路由，重定向到管理端
  // 允许访问公开页面（商品列表、商品详情等）
  if (userStore.isLoggedIn && userStore.isAdmin && 
      to.meta.requiresAuth && 
      !to.path.startsWith('/admin') && 
      to.path !== '/login' && to.path !== '/register') {
    next({ path: '/admin/products' })
    return
  }
  
  next()
})

export default router

