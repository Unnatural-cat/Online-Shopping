<template>
  <el-pagination
    v-model:current-page="currentPage"
    v-model:page-size="pageSize"
    :page-sizes="[10, 20, 50, 100]"
    :total="total"
    layout="total, sizes, prev, pager, next, jumper"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
  />
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 20
  }
})

const emit = defineEmits(['update:page', 'update:size', 'change'])

const currentPage = ref(props.page)
const pageSize = ref(props.size)

watch(() => props.page, (newVal) => {
  currentPage.value = newVal
})

watch(() => props.size, (newVal) => {
  pageSize.value = newVal
})

function handleSizeChange(val) {
  pageSize.value = val
  emit('update:size', val)
  emit('change', { page: currentPage.value, size: val })
}

function handleCurrentChange(val) {
  currentPage.value = val
  emit('update:page', val)
  emit('change', { page: val, size: pageSize.value })
}
</script>

<style scoped>
.el-pagination {
  margin-top: 20px;
  justify-content: center;
}
</style>

