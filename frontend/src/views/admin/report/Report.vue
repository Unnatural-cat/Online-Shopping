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
              <div class="card-value">¥{{ (summary.averageOrderValue || 0).toFixed(2) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="card-content">
              <div class="card-label">支付转化率</div>
              <div class="card-value">{{ (summary.conversionRate || 0).toFixed(2) }}%</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 销售趋势图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>销售趋势</h4>
        </template>
        <div ref="trendChartRef" style="width: 100%; height: 400px;" v-loading="loading"></div>
        <el-empty v-if="!loading && !trendChartData.length" description="暂无数据" style="padding: 40px 0;" />
      </el-card>

      <!-- 热销商品图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>热销商品 TOP 10</h4>
        </template>
        <div ref="topProductsChartRef" style="width: 100%; height: 400px;" v-loading="loading"></div>
        <el-empty v-if="!loading && !topProductsData.length" description="暂无数据" style="padding: 40px 0;" />
      </el-card>

      <!-- 订单状态分布图表 -->
      <el-card style="margin-top: 20px;">
        <template #header>
          <h4>订单状态分布</h4>
        </template>
        <div ref="statusChartRef" style="width: 100%; height: 400px;" v-loading="loading"></div>
        <el-empty v-if="!loading && !statusChartData.length" description="暂无数据" style="padding: 40px 0;" />
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
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
  averageOrderValue: 0,
  conversionRate: 0
})

const trendChartRef = ref(null)
const topProductsChartRef = ref(null)
const statusChartRef = ref(null)

const trendChartData = ref([])
const topProductsData = ref([])
const statusChartData = ref([])

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
    if (response.code === 0 && response.data) {
      Object.assign(summary, {
        totalOrders: response.data.totalOrders || 0,
        totalSales: response.data.totalSales || 0,
        averageOrderValue: response.data.averageOrderValue || 0,
        conversionRate: response.data.conversionRate || 0
      })
    }
  } catch (error) {
    console.error('加载销售汇总失败:', error)
    // 重置为默认值
    Object.assign(summary, {
      totalOrders: 0,
      totalSales: 0,
      averageOrderValue: 0,
      conversionRate: 0
    })
  }
}

async function loadTrendChart() {
  try {
    const response = await getSalesTrend(getDateParams())
    if (response.code === 0 && trendChartRef.value) {
      const data = response.data
      if (!data || !data.dataPoints || data.dataPoints.length === 0) {
        if (trendChart) {
          trendChart.clear()
        }
        return
      }
      const dates = data.dataPoints.map(p => p.timeLabel)
      const sales = data.dataPoints.map(p => Number(p.salesAmount || 0))
      const orders = data.dataPoints.map(p => Number(p.orderCount || 0))
      
      trendChartData.value = data.dataPoints

      if (!trendChart) {
        trendChart = echarts.init(trendChartRef.value)
      }

      trendChart.setOption({
        title: {
          show: dates.length === 0,
          text: '暂无数据',
          left: 'center',
          top: 'middle',
          textStyle: {
            color: '#909399',
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>'
            params.forEach(param => {
              if (param.seriesName === '销售额') {
                result += param.marker + param.seriesName + ': ¥' + param.value.toFixed(2) + '<br/>'
              } else {
                result += param.marker + param.seriesName + ': ' + param.value + '<br/>'
              }
            })
            return result
          }
        },
        legend: {
          data: ['销售额', '订单数'],
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          boundaryGap: false
        },
        yAxis: [
          {
            type: 'value',
            name: '销售额（元）',
            position: 'left',
            axisLabel: {
              formatter: function(value) {
                return value >= 10000 ? (value / 10000).toFixed(1) + '万' : value
              }
            }
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
            yAxisIndex: 0,
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                  { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                ]
              }
            }
          },
          {
            name: '订单数',
            type: 'line',
            data: orders,
            yAxisIndex: 1,
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
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
      let data = response.data
      if (!data || data.length === 0) {
        if (topProductsChart) {
          topProductsChart.clear()
        }
        return
      }
      
      // 前端去重：按商品ID去重，如果ID相同则合并数据
      const productMap = new Map()
      data.forEach(item => {
        const productId = item.productId
        if (productMap.has(productId)) {
          const existing = productMap.get(productId)
          existing.salesQuantity = (existing.salesQuantity || 0) + (item.salesQuantity || 0)
          existing.salesAmount = (existing.salesAmount || 0) + (item.salesAmount || 0)
          existing.orderCount = (existing.orderCount || 0) + (item.orderCount || 0)
        } else {
          productMap.set(productId, { ...item })
        }
      })
      
      // 转换为数组并按销售数量排序
      data = Array.from(productMap.values())
        .sort((a, b) => (b.salesQuantity || 0) - (a.salesQuantity || 0))
        .slice(0, 10)
      
      const names = data.map(p => p.productName || '未知商品')
      const sales = data.map(p => p.salesQuantity || 0)
      
      topProductsData.value = data

      if (!topProductsChart) {
        topProductsChart = echarts.init(topProductsChartRef.value)
      }

      topProductsChart.setOption({
        title: {
          show: names.length === 0,
          text: '暂无数据',
          left: 'center',
          top: 'middle',
          textStyle: {
            color: '#909399',
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            const param = params[0]
            return param.name + '<br/>' + param.marker + '销售数量: ' + param.value
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            rotate: 45,
            interval: 0
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
            data: sales,
            itemStyle: {
              color: function(params) {
                const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
                return colors[params.dataIndex % colors.length]
              }
            },
            label: {
              show: true,
              position: 'top'
            }
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
      if (!data || !data.distribution || data.distribution.length === 0) {
        if (statusChart) {
          statusChart.clear()
        }
        return
      }
      const statusMap = {
        CREATED: '待支付',
        PAID: '待发货',
        SHIPPED: '已发货',
        COMPLETED: '已完成',
        CANCELLED: '已取消',
        REFUNDED: '已退款'
      }

      const chartData = data.distribution
        .filter(item => item.count > 0) // 只显示有数据的状态
        .map(item => ({
        name: statusMap[item.status] || item.status,
        value: item.count
      }))
      
      statusChartData.value = chartData

      if (!statusChart) {
        statusChart = echarts.init(statusChartRef.value)
      }

      const statusColors = {
        '待支付': '#E6A23C',
        '待发货': '#409EFF',
        '已发货': '#67C23A',
        '已完成': '#67C23A',
        '已取消': '#909399',
        '已退款': '#F56C6C'
      }

      statusChart.setOption({
        title: {
          show: chartData.length === 0,
          text: '暂无数据',
          left: 'center',
          top: 'middle',
          textStyle: {
            color: '#909399',
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          top: 20,
          itemGap: 15,
          textStyle: {
            fontSize: 15,
            fontWeight: 'normal'
          },
          itemWidth: 16,
          itemHeight: 16,
          icon: 'circle'
        },
        series: [
          {
            name: '订单状态',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {c} ({d}%)'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: chartData.map(item => ({
              ...item,
              itemStyle: {
                color: statusColors[item.name] || '#909399'
            }
            }))
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

// 设置默认日期范围为最近30天
function initDefaultDateRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 30)
  dateRange.value = [
    start.toISOString().split('T')[0],
    end.toISOString().split('T')[0]
  ]
}

// 窗口大小改变时重新调整图表
function handleResize() {
  trendChart?.resize()
  topProductsChart?.resize()
  statusChart?.resize()
}

onMounted(async () => {
  initDefaultDateRange()
  await loadAllReports()
  await nextTick()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  trendChart?.dispose()
  topProductsChart?.dispose()
  statusChart?.dispose()
})
</script>

<style scoped>
.admin-report {
  height: 100%;
  width: 100%;
}

.admin-report :deep(.el-card) {
  width: 100%;
}

.admin-report :deep(.el-card__body) {
  padding: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-content h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
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
  transition: transform 0.2s, box-shadow 0.2s;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-content {
  padding: 20px 0;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

/* 图表容器样式 */
.admin-report :deep(.el-card) {
  margin-bottom: 20px;
}

.admin-report :deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
}

.admin-report :deep(.el-card__header h4) {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>
