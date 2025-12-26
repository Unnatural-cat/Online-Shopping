import { ElMessage, ElMessageBox } from 'element-plus'

export function showSuccess(message) {
  ElMessage.success(message)
}

export function showError(message) {
  ElMessage.error(message)
}

export function showWarning(message) {
  ElMessage.warning(message)
}

export function showInfo(message) {
  ElMessage.info(message)
}

export function confirm(message, title = '提示') {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
}

