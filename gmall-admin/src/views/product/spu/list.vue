<template>
  <div class="app-container">

    <!--三级下拉列表-->
    <CategorySelector v-show="!showSkuForm" @listenOnSelect="getSpuList" />

    <!--spu列表-->
    <div v-show="!showSpuForm && !showSkuForm">
      <div style="margin-bottom:10px;">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="addSpu()">添加SPU</el-button>
      </div>

      <el-table
        v-loading="spuListLoading"
        :data="spuList"
        element-loading-text="数据正在加载......"
        border
        fit
        highlight-current-row>
        <el-table-column align="center" label="序号" width="100">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="SPU ID" width="100">
          <template slot-scope="scope">
            {{ scope.row.id }}
          </template>
        </el-table-column>
        <el-table-column label="商品名称">
          <template slot-scope="scope">
            <span>{{ scope.row.spuName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="商品描述">
          <template slot-scope="scope">
            <span>{{ scope.row.description }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="addSku(scope.row.id, scope.row.spuName, scope.row.tmId)">添加SKU</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div v-show="!showSpuForm && !showSkuForm">
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

    <!--spu表单-->
    <SpuForm
      v-show="showSpuForm"
      ref="spuForm"
      :category-id="categoryId"
      @listenOnSave="onSpuSave()"
      @listenOnClose="onSpuClose()"/>

    <!--sku表单-->
    <SkuForm
      v-show="showSkuForm"
      ref="skuForm"
      :category-id="categoryId"
      :spu-id="selectedSpu.spuId"
      :spu-name="selectedSpu.spuName"
      @listenOnSave="onSkuSave()"
      @listenOnClose="onSkuClose()"/>

  </div>
</template>

<script>
import spu from '@/api/product/spu'
import CategorySelector from '@/views/components/CategorySelector'
import SpuForm from '@/views/product/components/SpuForm'
import SkuForm from '@/views/product/components/SkuForm'

export default {
  components: { CategorySelector, SpuForm, SkuForm },

  data() {
    return {
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数

      searchObj: {}, // 查询表单对象

      // spu所属分类
      // 属性所属分类
      category1Id: 0,
      category2Id: 0,
      category3Id: 0,
      categoryLevel: 1,

      // spu列表数据
      spuList: null,
      spuListLoading: false,

      // spu表单显示
      showSpuForm: false,

      // sku表单显示
      showSkuForm: false,

      // 选中的spu
      selectedSpu: {
        spuId: null,
        spuName: null
      }
    }
  },

  methods: {

    // 获取spu列表
    getSpuList(categoryId = 1, categoryLevel) {
      this.categoryLevel = categoryLevel
      if (categoryLevel === 1) {
        this.category1Id = categoryId
        this.category2Id = 0
        this.category3Id = 0
      }
      if (categoryLevel === 2) {
        this.category2Id = categoryId
        this.category3Id = 0
      }
      if (categoryLevel === 3) {
        this.category3Id = categoryId
      }
      this.searchObj.category3Id = categoryId
      // 查询数据
      this.fetchData(1)
    },

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

      spu.getPageList(this.page, this.limit, this.searchObj).then(
        response => {
          // debugger
          this.spuList = response.data.records

          this.total = response.data.total

          this.spuListLoading = false
        }
      )
    },

    // 选择三级分类确认
    confirmSelect() {
      if (!this.category3Id) {
        this.$alert('请选择三级分类', '提示', {
          confirmButtonText: '确定',
          type: 'warning'
        })
        return false
      }
      return true
    },

    // 添加spu
    addSpu() {
      if (!this.confirmSelect()) {
        return
      }

      // 初始化表单
      this.$refs.spuForm.init(this.category3Id)

      // 显示表单
      this.showSpuForm = true
    },

    // 保存spu后刷新列表
    onSpuSave() {
      // 刷新Spu列表
      this.getSpuList(this.category3Id)

      // 隐藏表单
      this.showSpuForm = false
    },

    // 关闭spu表单
    onSpuClose() {
      // 隐藏表单
      this.showSpuForm = false
    },

    // 添加sku
    addSku(spuId, spuName, tmId) {
      this.selectedSpu.spuId = spuId
      this.selectedSpu.spuName = spuName

      // 初始化表单
      this.$refs.skuForm.init(spuId, this.category1Id, this.category2Id, this.category3Id, tmId)

      // 显示表单
      this.showSkuForm = true
    },

    // 保存sku
    onSkuSave() {
      // 隐藏表单
      this.showSkuForm = false
    },

    // 关闭sku表单
    onSkuClose() {
      // 隐藏表单
      this.showSkuForm = false
    }

  }
}
</script>
