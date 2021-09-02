<template>
  <div class="app-container">

    <!-- banner列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="数据正在加载......"
      border
      fit
      highlight-current-row>

      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="outTradeNo" label="订单编号" />
      <el-table-column prop="createTime" label="申请时间" width="155px"/>

      <el-table-column label="商品图片" width="130" align="center">
        <template slot-scope="scope">
          <img :src="scope.row.imgUrl" alt="scope.row.title" width="80px">
        </template>
      </el-table-column>
      <el-table-column label="商品" align="center">
        <template slot-scope="scope">
          {{ scope.row.skuName }} <br/>
          数量：{{ scope.row.refundNum }} <br/>
          退款金额：{{ scope.row.refundAmount }} <br/>
        </template>
      </el-table-column>
      <el-table-column label="商品" align="center">
        <template slot-scope="scope">
          {{ scope.row.refundReasonTypeString }} <br/>
          原因：{{ scope.row.refundReasonTxt }} <br/>
        </template>
      </el-table-column>
      <el-table-column label="退款类型" width="80px">
        <template slot-scope="scope">
          {{ scope.row.refundType == 1 ? '退款' : '退货' }}
        </template>
      </el-table-column>
      <el-table-column prop="refundStatusString" label="状态" width="80px"/>
      <el-table-column prop="approveRemark" label="审批备注" width="120px"/>
      <el-table-column prop="trackingNo" label="退货单号" width="80px"/>

      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="scope">
          <el-button v-if="scope.row.refundStatus == 'APPLY'" type="danger" @click="approvalShow(scope.row.id)" size="mini">审批</el-button>
          <el-button v-if="scope.row.refundStatus == 'DELEVERED'" type="danger" size="mini" @click="recieve(scope.row.id)">签收</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[5, 10, 20, 30, 40, 50, 100]"
      style="padding: 30px 0; text-align: center;"
      layout="sizes, prev, pager, next, jumper, ->, total, slot"
      @current-change="fetchData"
      @size-change="changeSize"
    />

    <el-dialog title="审批" :visible.sync="dialogVisible" width="490px">
      <el-form ref="approvalForm" :model="approvalForm" label-position="right" label-width="100px">
        <el-form-item label="是否通过">
          <el-radio-group v-model="approvalForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="-1">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注" prop="name">
          <el-input v-model="approvalForm.remark" :rows="3" type="textarea"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="approvalSubmit()">
          {{ submitBnt }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/order/orderRefundInfo'

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象

      dialogVisible: false,
      submitBnt: '确定',
      approvalForm: {
        id: 0,
        status: 1,
        remark: ''
      }
    }
  },

  // 生命周期函数：内存准备完毕，页面尚未渲染
  created() {
    console.log('list created......')
    this.fetchData()
  },

  // 生命周期函数：内存准备完毕，页面渲染成功
  mounted() {
    console.log('list mounted......')
  },

  methods: {

    // 当页码发生改变的时候
    changeSize(size) {
      console.log(size)
      this.limit = size
      this.fetchData(1)
    },

    // 加载banner列表数据
    fetchData(page = 1) {
      console.log('翻页。。。' + page)
      // 异步获取远程数据（ajax）
      this.page = page

      api.getPageList(this.page, this.limit, this.searchObj).then(
        response => {
          debugger
          this.list = response.data.records
          this.total = response.data.total

          // 数据加载并绑定成功
          this.listLoading = false
        }
      )
    },

    // 重置查询表单
    resetData() {
      console.log('重置查询表单')
      this.searchObj = {}
      this.fetchData()
    },

    approvalShow(id) {
      this.dialogVisible = true
      this.approvalForm.id = id
    },

    approvalSubmit() {
      this.submitBnt = '确定中...'
      api.approval(this.approvalForm).then(response => {
        this.dialogVisible = false
        this.$message.success('操作成功!')
        this.fetchData(this.page)
      }).catch(e => {
        this.dialogVisible = true
        this.submitBnt = '确定'
      })
    },

    recieve(id) {
      console.log(id)
      this.$confirm('确认该退货签收吗, 是否确认?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => { // promise
        // 点击确定，远程调用ajax
        return api.recieve(id)
      }).then((response) => {
        if (response.code) {
          this.$message({
            type: 'success',
            message: '签收成功!'
          })
        }
        this.fetchData(this.page)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消签收'
        })
      })
    }
  }
}
</script>

