<template>
  <div class="app-container">
    <el-form label-width="120px">
      <el-form-item label="标题">
        <el-input v-model="banner.title"/>
      </el-form-item>

      <el-form-item label="Banner">
        <el-upload
          :show-file-list="false"
          :on-success="handleImageUrlSuccess"
          :before-upload="beforeImageUrlUpload"
          :action="BASE_API+'/admin/product/fileUpload'"
          class="avatar-uploader">
          <img :src="banner.imageUrl" width="950px">
        </el-upload>
      </el-form-item>

      <el-form-item label="链接">
        <el-input v-model="banner.linkUrl"/>
      </el-form-item>

      <el-form-item label="排序">
        <el-input-number v-model="banner.sort" :min="0" controls-position="right"/>
      </el-form-item>

      <el-form-item>
        <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import bannerApi from '@/api/cms/banner'

const defaultForm = {
  title: '',
  imageUrl: 'https://guli-file-190513.oss-cn-beijing.aliyuncs.com/cover/default.gif',
  linkUrl: '',
  sort: 0
}

export default {
  data() {
    return {
      banner: defaultForm,
      saveBtnDisabled: false, // 保存按钮是否禁用
      BASE_API: process.env.BASE_API
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
      // debugger
      if (this.$route.params && this.$route.params.id) {
        const id = this.$route.params.id
        this.fetchDataById(id)
      } else {
        // 对象拓展运算符：拷贝对象，而不是赋值对象的引用
        this.banner = { ...defaultForm }
      }
    },

    saveOrUpdate() {
      this.saveBtnDisabled = true // 防止表单重复提交
      if (!this.banner.id) {
        this.saveData()
      } else {
        this.updateData()
      }
    },

    // 新增讲师
    saveData() {
      bannerApi.save(this.banner).then(response => {
        // debugger
        this.$message.success(response.message)
        this.$router.push({path: '/cms/banner/list'})
      })
    },

    // 根据id更新记录
    updateData() {
      bannerApi.updateById(this.banner).then(response => {
        this.$message.success(response.message)
        this.$router.push({path: '/cms/banner/list'})
      })
    },

    // 根据id查询记录
    fetchDataById(id) {
      bannerApi.getById(id).then(response => {
        // debugger
        this.banner = response.data
      })
    },

    // banner上传成功的回调
    handleImageUrlSuccess(response) {
      this.banner.imageUrl = response.data
    },

    // 上传之前的回调
    beforeImageUrlUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传封面图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传封面图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>
