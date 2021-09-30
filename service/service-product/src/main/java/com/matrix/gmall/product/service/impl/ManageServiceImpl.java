package com.matrix.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.model.product.*;
import com.matrix.gmall.product.mapper.*;
import com.matrix.gmall.product.service.ManageService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yihaosun
 * @Date: 2021/8/31 21:52
 */
@Service
public class ManageServiceImpl implements ManageService {
    /**
     * 服务层调用mapper层
     */
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public List<BaseCategory1> getBaseCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getBaseCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    @Override
    public List<BaseCategory3> getBaseCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id", category2Id));
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    /**
     * @param baseAttrInfo baseAttrInfo
     * @Transactional 多表操作DMl语句 增加这个注解
     * 1. 如果有异常会回滚！
     * 2. 如果当前代码块中出现了非运行时的异常也发生回滚！
     * 如果有问题的话 debug 的时候从controller开始
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 判断什么时候修改 什么时候新增 判断baseAttrInfo的Id是否为Null
        if (baseAttrInfo.getId() != null) {
            // 更新
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            // 新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // 不知道什么时候新增 什么时候修改？
        // 不知道什么时候 因此要先删除 再新增
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<BaseAttrValue>().eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(wrapper);

        // 关联到两张表 base_attr_info base_attr_value 插入操作DML 注意事务！！！
        // 获取到平台属性值的集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        // 将对应的Id和值插入到平台属性中去
        if (!CollectionUtils.isEmpty(attrValueList)) {
            attrValueList.forEach(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            });
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(Long attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if (baseAttrInfo != null) {
            List<BaseAttrValue> attrValueList = getAttrValueList(attrId);
            if (!CollectionUtils.isEmpty(attrValueList)) {
                baseAttrInfo.setAttrValueList(attrValueList);
            }
        }
        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> getSpuInfoList(SpuInfo spuInfo, Page<SpuInfo> spuInfoPage) {
        return spuInfoMapper.selectPage(spuInfoPage, new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id, spuInfo.getCategory3Id())
                .orderByDesc(SpuInfo::getId));
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 保存的内容
     * 1. spu_info
     * 2. spu_image
     * 3. spu_sale_attr
     * 4. spu_sale_attr_value
     * 多表的时候加上@Transactional()事务
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        // 获取图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            spuImageList.forEach(spuImage -> {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }
        // 获取当前的销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            spuSaleAttrList.forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                    spuSaleAttrValueList.forEach(spuSaleAttrValue -> {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    });
                }
            });
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 考虑都需要保存到那几张表里面去 sku_info sku_attr_value sku_sale_attr_vale sku_image
        skuInfoMapper.insert(skuInfo);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            skuAttrValueList.forEach(skuAttrValue -> {
                // 看看前端都少传了什么 然后在其他的方法把实体类补充完整
                if (skuInfo.getId() != null) {
                    skuAttrValue.setSkuId(skuInfo.getId());
                    skuAttrValueMapper.insert(skuAttrValue);
                }
            });
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            skuSaleAttrValueList.forEach(skuSaleAttrValue -> {
                if (!ObjectUtils.isEmpty(skuInfo.getSpuId()) && !ObjectUtils.isEmpty(skuInfo.getId())) {
                    skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                    skuSaleAttrValue.setSkuId(skuInfo.getId());
                    skuSaleAttrValueMapper.insert(skuSaleAttrValue);
                }
            });
        }

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            skuImageList.forEach(skuImage -> {
                if (!ObjectUtils.isEmpty(skuInfo.getId())) {
                    skuImage.setSkuId(skuInfo.getId());
                    skuImageMapper.insert(skuImage);
                }
            });
        }
    }

    @Override
    public IPage<SkuInfo> getSkuInfoList(Page<SkuInfo> skuInfoPage) {
        // limit 0, 10 表示从第一个开始到第十个位置的分页数据
        // limit 10, 10 表示从第一给开始到第二十个位置的分页数据
        // 前面的数据表示开始位置 后面的数据表示的是偏移量
        return skuInfoMapper.selectPage(skuInfoPage, new LambdaQueryWrapper<SkuInfo>().orderByDesc(SkuInfo::getId));
    }

    @Override
    public void onSale(Long skuId) {
        // 更新状态
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        // 更新状态
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 做分布式锁的目的是为了防止缓存击穿等单机锁的局限性
     * 所有锁的业务逻辑
     * if (true) {
     * // 读缓存
     * } else {
     * // 走数据库 并给缓存
     * }
     *
     * @param skuId skuId
     * @return SkuInfo
     */

    /**
     * ！！！读取缓存的数据 如何获取数据
     * String: 存储字符串 常量的
     * list: 存储队列 有先有后的
     * set: 存储没有重复的数据
     * Hash: 存储Java对象
     * zset: 存储没有重复的 而且可以做排序
     * 根据数据的存储特征选择相应的opsForxxx
     * <p>
     * redisTemplate.opsForHash(); 能 hset(key, field, value) field = 实体类的属性名！(但是太多了)
     * 优点是便于修改！hget(field, value);
     * 而我们做查询商品详情不需要进行修改操作
     * <p>
     * 综上, 我们为了方便直接存储字符串即可 不需要Hash
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) throws InterruptedException {
        return getSkuInfoRedisson(skuId);
    }

    /**
     * 使用 Redisson 做分布式锁
     * @param skuId skuId
     * @return SkuInfo
     */
    private SkuInfo getSkuInfoRedisson(Long skuId) {
        try {
            // 使用Redisson 做 分布式锁
            // 声明对象
            SkuInfo skuInfo = new SkuInfo();
            String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            if (Objects.isNull(skuInfo)) {
                // 定义锁的Key
                String skuLockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                RLock lock = redissonClient.getLock(skuLockKey);
                // 上锁 -> 使用Redisson
                boolean flag = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (flag) {
                    // 上锁成功 执行业务逻辑
                    try {
                        // 查询数据库并放入缓存
                        skuInfo = getInfoDB(skuId);
                        if (ObjectUtils.isEmpty(skuInfo)) {
                            // 直接放入一个空的对象就可以了  但是还要给这个对象放入一个过期时间10分钟 因为未来业务量上去的时候这个值早晚会被占用的
                            SkuInfo skuInfo1 = new SkuInfo();
                            redisTemplate.opsForValue().set(skuLockKey, skuInfo1, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                            return skuInfo1;
                        }
                        // skuInfo 不为空
                        redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT);
                        return skuInfo;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    try {
                        // 没得到锁 呆一会
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // 缓存有数据
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 用户 数据库 去兜底
        return getInfoDB(skuId);
    }

    /**
     * 通过redis的方式 做分布式锁
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    private SkuInfo getSkuInfoRedis(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        try {
            String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            // 正常返回Json字符串 但是我们在RedisConfig中将String Hash已经做了序列化处理了 所以可以直接返回
            // 注意Reids可能存在宕机的情况redisTemplate就无了 所以这块要try一下
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //  做了序列化处理 所以我们直接set就可以了
            // redisTemplate.opsForValue().set(skukey, skuId);
            if (ObjectUtils.isEmpty(skuInfo)) {
                // 走数据库 并给缓存
                // 但是不可以直接去查 一个请求倒是可以 万一一百万条数据的话直接干蹦了! 发生缓存击穿 这个Key一直失效 所以要加锁！
                /** 一. 先使用Redis上锁
                 *  1. 先定义一个key  Value使用UUID 以及 锁的超时时间为1s
                 *  2. 上锁
                 */
                String skuLockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                String uuid = UUID.randomUUID().toString();
                Boolean flag = redisTemplate.opsForValue().setIfAbsent(skuKey, uuid, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                if (flag) {
                    // 上锁成功！（获取到了锁）
                    skuInfo = getInfoDB(skuId);
                    // !!!但是要防止缓存穿透
                    if (ObjectUtils.isEmpty(skuInfo)) {
                        // 直接放入一个空的对象就可以了  但是还要给这个对象放入一个过期时间10分钟 因为未来业务量上去的时候这个值早晚会被占用的
                        SkuInfo skuInfo1 = new SkuInfo();
                        redisTemplate.opsForValue().set(skuLockKey, skuInfo1, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        return skuInfo1;
                    }
                    // 不为空 将数据放入缓存 并返回
                    redisTemplate.opsForValue().set(skuKey, skuInfo, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    // 别忘了解锁 使用Lua脚本释放锁
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    // 定义Lua脚本
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                            "then return redis.call('del', KEYS[1]) " +
                            "else return 0 end";
                    redisScript.setScriptText(script);
                    // 这个类型和 DefaultRedisScript<Long> 的类型中的泛型是一样的
                    redisScript.setResultType(Long.class);
                    redisTemplate.execute(redisScript, Arrays.asList(skuLockKey, uuid));
                    // 返回数据
                    return skuInfo;
                }
            } else {
                // 读缓存
                return skuInfo;
            }
        } catch (Exception e) {
            System.out.println("Redis 宕机了...");
            e.printStackTrace();
        }
        // 兜底方案 -> 用数据库兜底
        // 但是这种方法还是存在风险的 所以一定要快速的修复！或者让Redis使用高可用集群 引出了Redisson
        return getInfoDB(skuId);
    }

    // 根据SkuId查询数据库DB
    private SkuInfo getInfoDB(Long skuId) {
        // 以下相当于走数据库
        // TODO 如果skuId不存在的情况下 会出现空指针异常
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId, skuId));
        // 将SkuImageList集合赋值给SkuInfo对象
        // TODO 如果是空的话 null.setSkuImageList 会报空指针异常
        if (Objects.nonNull(skuInfo)) {
            skuInfo.setSkuImageList(skuImages);
        }
        return skuInfo;
    }

    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        // SELECT * FROM base_category_view WHERE id = 61
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (!ObjectUtils.isEmpty(skuInfo)) {
            return skuInfo.getPrice();
        }
        return new BigDecimal(0);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttListCheckBySku(skuId, spuId);
    }

    @Override
    public Map<String, Long> getSkuIdValueIdsMap(Long spuId) {
        Map hashMap = new HashMap<>();
        // 调用哪个mapper看调用了哪张表 调用了哪张表看想要的返回的数据是从哪张表里面来的
        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (!CollectionUtils.isEmpty(mapList)) {
            // 根据销售属性Ids 拿到 skuId
            mapList.forEach(map -> hashMap.put(map.get("value_ids"), map.get("sku_id")));
        }
        return hashMap;
    }
}