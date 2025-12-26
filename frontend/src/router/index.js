import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        redirect: '/products'
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
      }
    ]
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
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/admin/report/Report.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'Products' })
    return
  }
  
  // 如果已登录，访问登录/注册页时重定向到首页
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
    next({ name: 'Products' })
    return
  }
  
  next()
})

export default router

