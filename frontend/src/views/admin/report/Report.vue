<template>
  <div class="admin-report" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>销售报表</h3>
          <div class="date-filter">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
            />
            <el-button type="primary" @click="loadAllReports">查询</el-button>
          </div>
        </div>
      </template>

      <!-- 销售汇总 -->
      <el-row :gutter="20" class="summary-cards">
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="card-content">
              <div class="card-label">订单总量</div>
              <div class="card-value">{{ summary.totalOrders || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="card-content">
              <div class="card-label">销售额</div>
              <div class="card-value">¥{{ (summary.totalSales || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="card-content">
              <div class="card-label">客单价</div>
              <div class="card-value">¥{{ (summary.avgOrderAmount || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="card-content">
              <div class="card-label">支付转化率</div>
              <div class="card-value">{{ ((summary.paymentRate || 0) * 100).toFixed(2) }}%</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 销售趋势图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>销售趋势</h4>
        </template>
        <div ref="trendChartRef" style="width: 100%; height: 400px;"></div>
      </el-card>

      <!-- 热销商品图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>热销商品 TOP 10</h4>
        </template>
        <div ref="topProductsChartRef" style="width: 100%; height: 400px;"></div>
      </el-card>

      <!-- 订单状态分布图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>订单状态分布</h4>
        </template>
        <div ref="statusChartRef" style="width: 100%; height: 400px;"></div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getSalesSummary,
  getSalesTrend,
  getTopProducts,
  getOrderStatusDistribution
} from '@/api/admin/report'

const loading = ref(false)
const dateRange = ref([])
const summary = reactive({
  totalOrders: 0,
  totalSales: 0,
  avgOrderAmount: 0,
  paymentRate: 0
})

const trendChartRef = ref(null)
const topProductsChartRef = ref(null)
const statusChartRef = ref(null)

let trendChart = null
let topProductsChart = null
let statusChart = null

function getDateParams() {
  const params = {}
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  return params
}

async function loadSummary() {
  try {
    const response = await getSalesSummary(getDateParams())
    if (response.code === 0) {
      Object.assign(summary, response.data)
    }
  } catch (error) {
    console.error('加载销售汇总失败:', error)
  }
}

async function loadTrendChart() {
  try {
    const response = await getSalesTrend(getDateParams())
    if (response.code === 0 && trendChartRef.value) {
      const data = response.data
      const dates = data.dataPoints.map(p => p.date)
      const sales = data.dataPoints.map(p => p.sales)
      const orders = data.dataPoints.map(p => p.orders)

      if (!trendChart) {
        trendChart = echarts.init(trendChartRef.value)
      }

      trendChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['销售额', '订单数']
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: [
          {
            type: 'value',
            name: '销售额（元）',
            position: 'left'
          },
          {
            type: 'value',
            name: '订单数',
            position: 'right'
          }
        ],
        series: [
          {
            name: '销售额',
            type: 'line',
            data: sales,
            yAxisIndex: 0
          },
          {
            name: '订单数',
            type: 'line',
            data: orders,
            yAxisIndex: 1
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载销售趋势失败:', error)
  }
}

async function loadTopProductsChart() {
  try {
    const response = await getTopProducts({ ...getDateParams(), limit: 10 })
    if (response.code === 0 && topProductsChartRef.value) {
      const data = response.data
      const names = data.map(p => p.productName)
      const sales = data.map(p => p.salesQuantity)

      if (!topProductsChart) {
        topProductsChart = echarts.init(topProductsChartRef.value)
      }

      topProductsChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '销售数量'
        },
        series: [
          {
            name: '销售数量',
            type: 'bar',
            data: sales
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载热销商品失败:', error)
  }
}

async function loadStatusChart() {
  try {
    const response = await getOrderStatusDistribution(getDateParams())
    if (response.code === 0 && statusChartRef.value) {
      const data = response.data
      const statusMap = {
        CREATED: '待支付',
        PAID: '待发货',
        SHIPPED: '已发货',
        COMPLETED: '已完成',
        CANCELLED: '已取消',
        REFUNDED: '已退款'
      }

      const chartData = data.statusCounts.map(item => ({
        name: statusMap[item.status] || item.status,
        value: item.count
      }))

      if (!statusChart) {
        statusChart = echarts.init(statusChartRef.value)
      }

      statusChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '订单状态',
            type: 'pie',
            radius: '50%',
            data: chartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载订单状态分布失败:', error)
  }
}

async function loadAllReports() {
  loading.value = true
  try {
    await Promise.all([
      loadSummary(),
      loadTrendChart(),
      loadTopProductsChart(),
      loadStatusChart()
    ])
  } finally {
    loading.value = false
  }
}

function handleDateChange() {
  loadAllReports()
}

onMounted(async () => {
  await loadAllReports()
  await nextTick()
  // 窗口大小改变时重新调整图表
  window.addEventListener('resize', () => {
    trendChart?.resize()
    topProductsChart?.resize()
    statusChart?.resize()
  })
})
</script>

<style scoped>
.admin-report {
  height: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-filter {
  display: flex;
  gap: 10px;
  align-items: center;
}

.summary-cards {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
}

.card-content {
  padding: 10px 0;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>
