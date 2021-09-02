<template>
  <div class="app-container">

    <el-form inline>
      <el-form-item label="订单号">
        <el-input type="text" width="100" placeholder="订单号" v-model="searchObj.outTradeNo" clearable/>
      </el-form-item>

      <el-form-item label="订单状态">
        <el-select v-model="searchObj.orderStatus" clearable placeholder="订单状态">
          <el-option value="UNPAID" label="未支付"/>
          <el-option value="PAID" label="已支付"/>
          <el-option value="WAITING_DELEVER" label="待发货"/>
          <el-option value="DELEVERED" label="已发货"/>
          <el-option value="CLOSED" label="已关闭"/>
          <el-option value="FINISHED" label="已完结"/>
          <el-option value="SPLIT" label="已拆分"/>
        </el-select>
      </el-form-item>

      <el-form-item label="下单时间">
        <el-date-picker
          v-model="times"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
        />
      </el-form-item>

       <el-form-item label="收货人">
        <el-input type="text" width="100" placeholder="收货人" v-model="searchObj.consignee" clearable/>
      </el-form-item>

      <el-form-item label="收件电话">
        <el-input type="text" width="100" placeholder="收件人电话" v-model="searchObj.consigneeTel" clearable/>
      </el-form-item>

      <el-form-item label="送货地址">
        <el-input type="text" width="150" placeholder="送货地址" v-model="searchObj.deliveryAddress" clearable/>
      </el-form-item>

      <el-button type="primary" icon="el-icon-search" @click="fetchData()">查询</el-button>
      <el-button type="default" @click="resetSearch">清空</el-button>
    </el-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="数据正在加载......"
      border
      fit
      highlight-current-row>

      <el-table-column
        type="index"
        label="序号"
        width="50"
        align="center">
      </el-table-column>

      <el-table-column prop="outTradeNo" label="订单号" width="220" />
      <el-table-column prop="totalAmount" label="支付金额(￥)" align="center" />
      <el-table-column prop="consignee" label="收货人" />
      <el-table-column prop="createTime" label="创建时间" width="180"/>
      <el-table-column prop="expireTime" label="失效时间" width="180"/>
      <el-table-column prop="orderStatusName" label="订单状态" width="80" />

      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <router-link :to="'/order/orderInfo/show/'+scope.row.id">
            <el-button type="primary" size="mini">查看</el-button>
          </router-link>
          <el-button v-if="scope.row.orderStatus == 'DELEVERED'" type="danger" size="mini" @click="recieve(scope.row.id)">签收</el-button>
        </template>
      </el-table-column>
    </el-table>

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
  </div>
</template>

<script>
import api from '@/api/order/orderInfo'

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {} // 查询表单对象
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

    recieve(id) {
      console.log(id)
      this.$confirm('确认该订单签收吗, 是否确认?', '提示', {
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
