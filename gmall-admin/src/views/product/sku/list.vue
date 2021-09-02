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

      <el-table-column prop="id" label="SKU ID" width="100"/>

      <el-table-column label="banner" width="320" align="center">
        <template slot-scope="scope">
          <div class="info">
            <div class="pic">
              <img :src="scope.row.skuDefaultImg" alt="scope.row.title" style="width: 50px;">
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="skuName" label="名称" />

      <el-table-column prop="price" label="价格" width="70"/>

      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button v-if="scope.row.isSale == 0" size="mini" icon="el-icon-edit" @click="onSale(scope.row.id)">上架</el-button>
          <el-button v-if="scope.row.isSale == 1" type="danger" size="mini" icon="el-icon-delete" @click="cancelSale(scope.row.id)">下架</el-button>
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
  </div>
</template>

<script>
import sku from '@/api/product/sku'

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象
      multipleSelection: [] // 批量选择中选择的记录列表
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

      sku.getPageList(this.page, this.limit).then(response => {
        this.list = response.data.records
        this.total = response.data.total

        // 数据加载并绑定成功
        this.listLoading = false
      })
    },

    onSale(skuId) {
      sku.onSale(skuId).then(response => {
        this.$message({
          type: 'success',
          message: '操作成功!'
        })
        this.fetchData(this.page)
      })
    },

    cancelSale(skuId) {
      sku.cancelSale(skuId).then(response => {
        this.$message({
          type: 'success',
          message: '操作成功!'
        })
        this.fetchData(this.page)
      })
    },

    // 重置查询表单
    resetData() {
      console.log('重置查询表单')
      this.searchObj = {}
      this.fetchData()
    }
  }
}
</script>

