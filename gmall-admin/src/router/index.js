import Vue from 'vue'
import Router from 'vue-router'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
**/
export const constantRouterMap = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/404', component: () => import('@/views/404'), hidden: true },

  // 首页
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: 'Dashboard',
    children: [{
      path: 'dashboard',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '商城后台首页', icon: 'dashboard' }
    }]
  },

  // 基本信息管理
  {
    path: '/baseinfo',
    component: Layout,
    redirect: '/baseinfo/prop/list',
    name: 'BasesInfo',
    meta: { title: '基本信息管理', icon: 'table' },
    alwaysShow: true,
    children: [
      {
        path: 'prop/list',
        name: 'BasesInfoPropList',
        component: () => import('@/views/baseinfo/prop/list'),
        meta: { title: '平台属性列表' }
      },
      {
        path: 'trademark/list',
        name: 'trademark',
        component: () => import('@/views/baseinfo/trademark/list'),
        meta: { title: '品牌列表' }
      },
      {
        path: 'trademark/add',
        name: 'trademark',
        component: () => import('@/views/baseinfo/trademark/form'),
        meta: { title: '添加' },
        hidden: true
      },
      {
        path: 'trademark/edit/:id',
        name: 'EduTeacherEdit',
        component: () => import('@/views/baseinfo/trademark/form'),
        meta: { title: '编辑' },
        hidden: true
      }
    ]
  },

  // 商品信息管理
  {
    path: '/product',
    component: Layout,
    redirect: '/product/spu/list',
    name: 'Product',
    meta: { title: '商品信息管理', icon: 'shopping' },
    alwaysShow: true,
    children: [
      {
        path: 'spu/list',
        name: 'ProductSpuList',
        component: () => import('@/views/product/spu/list'),
        meta: { title: '商品属性SPU管理' }
      },
      {
        path: 'sku/list',
        name: 'ProductSkuList',
        component: () => import('@/views/product/sku/list'),
        meta: { title: '商品属性SKU管理' }
      }
    ]
  },

  // {
  //   path: '/user',
  //   component: Layout,
  //   redirect: '/user/userInfo/list',
  //   name: 'userInfo',
  //   meta: { title: '会员管理', icon: 'table' },
  //   alwaysShow: true,
  //   children: [
  //     {
  //       path: 'userInfo/list',
  //       name: '会员列表',
  //       component: () => import('@/views/user/userInfo/list'),
  //       meta: { title: '会员列表', icon: 'table' }
  //     }
  //   ]
  // },

  {
    path: '/activity',
    component: Layout,
    redirect: '/activity/activityInfo/list',
    name: 'activity',
    meta: { title: '活动信息管理', icon: 'shopping' },
    alwaysShow: true,
    children: [
      {
        path: 'activityInfo/list',
        name: 'ActivityInfoList',
        component: () => import('@/views/activity/activityInfo/list'),
        meta: { title: '活动列表' }
      },
      {
        path: 'activityInfo/add',
        name: 'ActivityInfoCreate',
        component: () => import('@/views/activity/activityInfo/form'),
        meta: { title: '添加' },
        hidden: true
      },
      {
        path: 'activityInfo/edit/:id',
        name: 'ActivityInfoEdit',
        component: () => import('@/views/activity/activityInfo/form'),
        meta: { title: '编辑', noCache: true },
        hidden: true
      },
      {
        path: 'activityInfo/rule/:id',
        name: 'ActivityInfoRule',
        component: () => import('@/views/activity/activityInfo/rule'),
        meta: { title: '规则', noCache: true },
        hidden: true
      },
      {
        path: 'couponInfo/list',
        name: 'CouponInfoList',
        component: () => import('@/views/activity/couponInfo/list'),
        meta: { title: '优惠券列表' }
      },
      {
        path: 'couponInfo/add',
        name: 'CouponInfoCreate',
        component: () => import('@/views/activity/couponInfo/form'),
        meta: { title: '添加' },
        hidden: true
      },
      {
        path: 'couponInfo/edit/:id',
        name: 'CouponInfoEdit',
        component: () => import('@/views/activity/couponInfo/form'),
        meta: { title: '编辑', noCache: true },
        hidden: true
      },
      {
        path: 'couponInfo/rule/:id',
        name: 'CouponInfoRule',
        component: () => import('@/views/activity/couponInfo/rule'),
        meta: { title: '规则', noCache: true },
        hidden: true
      }
    ]
  },

  // {
  //   path: '/order',
  //   component: Layout,
  //   redirect: '/order/orderInfo/list',
  //   name: 'OrderInfo',
  //   meta: { title: '订单管理', icon: 'table' },
  //   alwaysShow: true,
  //   children: [
  //     {
  //       path: 'orderInfo/list',
  //       name: '订单列表',
  //       component: () => import('@/views/order/orderInfo/list'),
  //       meta: { title: '订单列表' }
  //     },
  //     {
  //       path: 'orderInfo/show/:id',
  //       name: '查看',
  //       component: () => import('@/views/order/orderInfo/show'),
  //       meta: { title: '查看', noCache: true },
  //       hidden: true
  //     },
  //     {
  //       path: 'refundApply/list',
  //       name: '退款列表',
  //       component: () => import('@/views/order/refundApply/list'),
  //       meta: { title: '退款列表' }
  //     }
  //   ]
  // },

  // {
  //   path: '/cms',
  //   component: Layout,
  //   redirect: '/cms/banner/list',
  //   name: 'cms',
  //   meta: { title: 'cms管理', icon: 'table' },
  //   alwaysShow: true,
  //   children: [
  //     {
  //       path: 'banner/list',
  //       name: 'Bander列表',
  //       component: () => import('@/views/cms/banner/list'),
  //       meta: { title: 'Bander列表' }
  //     },
  //     {
  //       path: 'banner/add',
  //       name: 'Bander添加',
  //       component: () => import('@/views/cms/banner/form'),
  //       meta: { title: 'Bander添加' },
  //       hidden: true
  //     },
  //     {
  //       path: 'banner/update/:id',
  //       name: 'Bander修改',
  //       component: () => import('@/views/cms/banner/form'),
  //       meta: { title: 'Bander修改' },
  //       hidden: true
  //     }
  //  ]
  // },

  { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
