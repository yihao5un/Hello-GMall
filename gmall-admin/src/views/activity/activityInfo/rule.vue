<template>
  <div class="app-container">

    <h4>活动信息</h4>
    <table class="table table-striped table-condenseda table-bordered" width="100%">
      <tbody>
      <tr>
        <th width="15%">活动标题</th>
        <td width="35%"><b style="font-size: 14px">{{ activityInfo.activityName }}</b></td>
        <th width="15%">活动时间</th>
        <td width="35%">{{ activityInfo.startTime }}至{{ activityInfo.endTime }}</td>
      </tr>
      <tr>
        <th>活动类型</th>
        <td>{{ activityInfo.activityTypeString }}</td>
        <th>创建时间</th>
        <td>{{ activityInfo.createTime }}</td>
      </tr>
      <tr>
        <th>活动描述</th>
        <td colspan="3">{{ activityInfo.activityDesc }}</td>
      </tr>
      </tbody>
    </table>

    <el-dialog title="添加规则" :visible.sync="dialogRuleVisible" width="490px">
      <el-form label-width="120px" >
        <el-form-item v-if="activityInfo.activityType == 'FULL_REDUCTION'" label="满减金额">
          <el-input v-model="activityRule.conditionAmount"/>
        </el-form-item>

        <el-form-item v-if="activityInfo.activityType == 'FULL_REDUCTION'" label="优惠金额">
          <el-input v-model="activityRule.benefitAmount"/>
        </el-form-item>

        <el-form-item v-if="activityInfo.activityType == 'FULL_DISCOUNT'" label="满减件数">
          <el-input v-model="activityRule.conditionNum"/>
        </el-form-item>

        <el-form-item v-if="activityInfo.activityType == 'FULL_DISCOUNT'" label="优惠折扣">
          <el-input v-model="activityRule.benefitDiscount"/>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="add">添加</el-button>
          <el-button type="" @click="dialogRuleVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <h4>
      规则列表&nbsp;&nbsp;&nbsp;
      <el-button type="" size="mini" @click="dialogRuleVisible = true">添加规则</el-button>
    </h4>
    <el-table
      v-loading="listLoading"
      :data="activityRuleList"
      border
      fit
      highlight-current-row>

      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column label="活动类型" >
        <template slot-scope="scope">
          {{ activityInfo.activityType == 'FULL_REDUCTION' ? '满减' : '满量打折' }}
        </template>
      </el-table-column>

      <el-table-column v-if="activityInfo.activityType == 'FULL_REDUCTION'" prop="conditionAmount" label="满减金额" />
      <el-table-column v-if="activityInfo.activityType == 'FULL_REDUCTION'" prop="benefitAmount" label="优惠金额" />

      <el-table-column v-if="activityInfo.activityType == 'FULL_DISCOUNT'" prop="conditionNum" label="满减量数" />
      <el-table-column v-if="activityInfo.activityType == 'FULL_DISCOUNT'" prop="benefitDiscount" label="优惠折扣" />

      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeDataById(scope.$index)"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="添加活动范围" :visible.sync="dialogRangVisible" width="490px">
      <div style="margin-top: 20px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item>
            <el-autocomplete
              v-model="keyword"
              :fetch-suggestions="querySearch"
              :trigger-on-focus="false"
              class="inline-input"
              placeholder="请输入关键字，选择活动商品"
              value-key="skuName"
              style="width: 400px;"
              @select="selectData"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="" @click="dialogRangVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
    <h4>
      活动范围列表&nbsp;&nbsp;&nbsp;
      <el-button type="" size="mini" @click="dialogRangVisible = true">添加活动范围</el-button>
    </h4>
    <el-table
      v-loading="listLoading"
      :data="skuInfoList"
      border
      fit
      highlight-current-row>

      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
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
          <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeSkuDataById(scope.$index)"/>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="添加活动范围" :visible.sync="dialogCouponVisible" width="490px" @close="dialogCouponVisible = false">
      <div style="margin-top: 20px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item>
            <el-autocomplete
              v-model="keyword"
              :fetch-suggestions="queryCouponSearch"
              :trigger-on-focus="false"
              class="inline-input"
              placeholder="请输入关键字，选择活动优惠券"
              value-key="couponName"
              style="width: 400px;"
              @select="selectCouponData"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="" @click="dialogCouponVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
    <h4>
      优惠券列表&nbsp;&nbsp;&nbsp;
      <el-button type="" size="mini" @click="dialogCouponVisible = true">绑定优惠券</el-button>
    </h4>
    <el-table
      v-loading="listLoading"
      :data="couponInfoList"
      border
      fit
      highlight-current-row>
      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="id" label="优惠券ID" width="100"/>
      <el-table-column prop="couponName" label="名称" />
      <el-table-column prop="conditionAmount" label="规则">
        <template slot-scope="scope">
          {{ scope.row.couponType == 'CASH' ? '减金额' + scope.row.benefitAmount + '元' :
          scope.row.couponType == 'DISCOUNT' ? '打' + scope.row.benefitDiscount + '折' :
          scope.row.couponType == 'FULL_REDUCTION' ? '减金额' + scope.row.benefitAmount + '元' : '满' + scope.row.conditionNum + '打' + scope.row.benefitDiscount + '折' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <router-link :to="'/activity/couponInfo/rule/'+scope.row.id">
            <el-button type="" size="mini">规则</el-button>
          </router-link>
          <el-button type="danger" size="mini" icon="el-icon-delete" @click="removeCouponDataById(scope.$index)"/>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 15px;">
      <el-form label-width="0px">
        <el-form-item>
          <el-button :disabled="saveBtnDisabled" type="primary" @click="save">保存</el-button>
          <el-button @click="back">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>

import api from '@/api/activity/activityInfo'
// import skuApi from '@/api/product/sku'
import couponApi from '@/api/activity/couponInfo'

const defaultForm = {
  activityId: '',
  conditionAmount: '',
  conditionNum: '',
  benefitAmount: '',
  benefitDiscount: '',
  benefitLevel: ''
}

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载

      activityRule: defaultForm,
      saveBtnDisabled: false,

      dialogRuleVisible: false,
      activityInfo: null,
      activityRuleList: [],

      dialogRangVisible: false,
      keyword: '',
      skuInfoList: [],

      dialogCouponVisible: false,
      couponInfoList: []
    }
  },

  // 监听器
  watch: {
    $route(to, from) {
      console.log('路由变化......')
      console.log(to)
      console.log(from)
      this.init()
    }
  },

  // 生命周期方法（在路由切换，组件不变的情况下不会被调用）
  created() {
    console.log('form created ......')
    this.init()
  },

  methods: {

    // 表单初始化
    init() {
      const activityId = this.$route.params.id
      this.fetchDataById(activityId)

      this.fetchRuleDataById(activityId)
    },

    // 新增
    add() {
      this.activityRuleList.push(
        {
          activityId: this.activityInfo.id,
          conditionAmount: this.activityRule.conditionAmount,
          conditionNum: this.activityRule.conditionNum,
          benefitAmount: this.activityRule.benefitAmount,
          benefitDiscount: this.activityRule.benefitDiscount
        }
      )

      this.activityRule = defaultForm
      this.dialogRuleVisible = false
    },

    save() {
      const skuList = []
      this.skuInfoList.forEach(function(item) {
        skuList.push({
          skuId: item.id
        })
      })
      const couponIdList = []
      this.couponInfoList.forEach(function(item) {
        couponIdList.push(item.id)
      })
      const ruleData = {
        activityId: this.activityInfo.id,
        activityRuleList: this.activityRuleList,
        activitySkuList: skuList,
        couponIdList: couponIdList
      }
      debugger
      api.saveActivityRule(ruleData).then(response => {
        this.$message.success(response.message)
        this.$router.push({ path: '/activity/activityInfo/list' })
      })
    },

    removeDataById(index) {
      this.activityRuleList.splice(index, 1)
    },

    back() {
      this.$router.push({ path: '/activity/activityInfo/list' })
    },

    fetchRuleDataById(id) {
      api.findActivityRuleList(id).then(response => {
        // debugger
        this.activityRuleList = response.data.activityRuleList || []
        this.skuInfoList = response.data.skuInfoList || []
        this.couponInfoList = response.data.couponInfoList || []

        this.listLoading = false
      })
    },

    // 根据id查询记录
    fetchDataById(id) {
      api.getById(id).then(response => {
        // debugger
        this.activityInfo = response.data
      })
    },

    // sku
    querySearch(queryString, cb) {
      console.log(queryString)
      console.log(cb)

      api.findSkuInfoByKeyword(queryString).then(response => {
        // 调用 callback 返回建议列表的数据
        cb(response.data)
      })
    },

    selectData(item) {
      this.skuInfoList.push(item)
      this.dialogRangVisible = false
    },

    removeSkuDataById(index) {
      this.skuInfoList.splice(index, 1)
    },

    // 优惠券
    queryCouponSearch(queryString, cb) {
      console.log(queryString)
      console.log(cb)

      couponApi.findCouponByKeyword(queryString).then(response => {
        // 调用 callback 返回建议列表的数据
        cb(response.data)
      })
    },

    selectCouponData(item) {
      this.couponInfoList.push(item)
      this.dialogCouponVisible = false
    },

    removeCouponDataById(index) {
      this.couponInfoList.splice(index, 1)
    }
  }
}
</script>
<style>
  .app-container h4 {
    color: #606266;
  }
</style>
