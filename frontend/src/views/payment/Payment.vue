<template>
  <div class="payment" v-loading="loading">
    <div class="payment-container">
        <el-card>
          <el-result
            v-if="paymentStatus === 'success'"
            icon="success"
            title="支付成功"
            sub-title="您的订单已支付成功，我们将尽快为您发货"
          >
            <template #extra>
              <el-button type="primary" @click="$router.push(`/orders/${orderNo}`)">查看订单</el-button>
              <el-button @click="$router.push('/orders')">返回订单列表</el-button>
            </template>
          </el-result>

          <div v-else>
            <h2>订单支付</h2>
            <div class="order-info">
              <p>订单号：{{ orderNo }}</p>
              <p>支付金额：<span class="amount">¥{{ payAmount.toFixed(2) }}</span></p>
            </div>
            <div class="payment-methods">
              <el-radio-group v-model="paymentMethod">
                <el-radio label="MOCK">模拟支付</el-radio>
              </el-radio-group>
            </div>
            <div class="payment-actions">
              <el-button @click="$router.back()">取消</el-button>
              <el-button type="primary" size="large" @click="handlePay" :loading="paying">
                立即支付
              </el-button>
            </div>
          </div>
        </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail } from '@/api/order'
import { pay } from '@/api/payment'
import { showSuccess, showError } from '@/utils/message'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const paying = ref(false)
const orderNo = ref('')
const payAmount = ref(0)
const paymentMethod = ref('MOCK')
const paymentStatus = ref('pending')

async function loadOrder() {
  orderNo.value = route.params.orderNo
  if (!orderNo.value) {
    router.push('/orders')
    return
  }

  loading.value = true
  try {
    const response = await getOrderDetail(orderNo.value)
    if (response.code === 0) {
      const order = response.data
      if (order.status === 'PAID' || order.status === 'SHIPPED' || order.status === 'COMPLETED') {
        paymentStatus.value = 'success'
      }
      payAmount.value = order.payAmount || order.totalAmount || 0
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  paying.value = true
  try {
    const response = await pay(orderNo.value, {
      paymentMethod: paymentMethod.value
    })
    if (response.code === 0) {
      showSuccess('支付成功')
      paymentStatus.value = 'success'
      // 重新加载订单信息
      await loadOrder()
    }
  } catch (error) {
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.payment {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.payment-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.order-info {
  margin: 20px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.order-info p {
  margin: 10px 0;
  font-size: 16px;
}

.amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.payment-methods {
  margin: 30px 0;
}

.payment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}
</style>
