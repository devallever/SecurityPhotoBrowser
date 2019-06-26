package com.allever.security.photo.browser.util;

import android.opengl.GLES20;
import android.os.Environment;
import android.text.TextUtils;
import com.android.absbase.App;

import java.io.File;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import com.photoeditor.store.module.HomeOnlineManager;
//import com.android.absbase.App;
//import com.android.absbase.helper.log.DLog;
//import com.android.absbase.helper.log.RLog;
//import com.android.absbase.utils.DebugUtil;
//import com.photoeditor.R;
//import com.photoeditor.function.camera.CameraController;
//import com.photoeditor.function.camera.VideoQuality;
//import com.photoeditor.db.bean.BackgroundBean;
//import com.photoeditor.db.bean.BackgroundBeanDao;
//import com.photoeditor.db.bean.BackgroundCategoryBean;
//import com.photoeditor.db.bean.BackgroundCategoryBeanDao;
//import com.photoeditor.db.bean.CameraSizeBean;
//import com.photoeditor.db.bean.CameraSizeBeanDao;
//import com.photoeditor.db.DBHelper;
//import com.photoeditor.db.bean.ExtraBean;
//import com.photoeditor.db.bean.ExtraBeanDao;
//import com.photoeditor.db.bean.FilterBean;
//import com.photoeditor.db.bean.FilterBeanDao;
//import com.photoeditor.db.bean.FilterCategoryBean;
//import com.photoeditor.db.bean.FilterCategoryBeanDao;
//import com.photoeditor.function.filter.FilterConstant;
//import com.photoeditor.db.bean.MagazineBean;
//import com.photoeditor.db.bean.MagazineBeanDao;
//import com.photoeditor.db.bean.PipBean;
//import com.photoeditor.db.bean.PipBeanDao;
//import com.photoeditor.function.sticker.emoji.StickerConstant;
//import com.photoeditor.function.download.DownloadUtils;
//import com.photoeditor.store.module.BaseOnlineManager;
//import com.photoeditor.store.module.LocalPluginUtil;
//import com.photoeditor.store.module.StoreOnlineBean;
//import com.photoeditor.store.module.StoreOnlineManager;
//import com.photoeditor.ui.ResourceManager;
//import com.photoeditor.utils.PluginVersionUtil;
//import com.android.absbase.utils.FileUtils;
//import com.superpro.commercialize.CommercializeSDK;
//import org.greenrobot.greendao.query.Query;
//import java.io.FilenameFilter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;

//import com.photoeditor.function.filter.gpuimage.GPUImage;

//import static com.photoeditor.function.filter.FilterConstant.STATUS_UNUSE;
//import static com.photoeditor.function.filter.FilterConstant.STATUS_USE;
//import static com.photoeditor.function.filter.FilterConstant.TYPE_DOWNLOAD;
//import static com.photoeditor.function.filter.FilterConstant.UNLOCK;
//import static com.photoeditor.function.filter.ImageFilterTools.BUILDIN_FILTER_CATEGORY_ID_MAP;
//import static com.photoeditor.function.filter.ImageFilterTools.BUILDIN_FILTER_CATEGORY_NAME;
//import static com.photoeditor.function.filter.ImageFilterTools.BUILDIN_FILTER_IMAGE_URL;
//import static com.photoeditor.function.filter.ImageFilterTools.BUILDIN_FILTER_NAME;

/**
 */

public class DataManager {

    public static final String TAG = DataManager.class.getSimpleName();

//    public static final String TEMP_DIR = FileUtils.getExternalCacheDir(App.getContext(),
//            ".temp", true) + File.separator;
//
//    public static final String RESOURCE_DIR = FileUtils.getExternalCacheDir(App.getContext(),
//            ".normal_res", true) + File.separator;
//
//    public static final String CONFIG_DIR = FileUtils.getExternalCacheDir(App.getContext(),
//            ".config",true) + File.separator;
//
//    public static final String FONT_DIR = RESOURCE_DIR + "font" + File.separator;

    /*
    config -> 2245023265AE4CF87D02C8B6BA991139
    resource -> 96AB4E163F4EE03AAA4D1051AA51D204
    font -> 47A282DFE68A42D302E22C4920ED9B5E
    sticker -> 9173F7D7444D3161C4DEA50F35F5D15F
    filter -> B2C97AE425DD751B0E48A3ACAE79CF4A
    mixer -> 10B5E1E383FFF2F51F8A7C9FD67F2E03
    background -> D229BBF31EAEEBC7C88897732D8B932D
    tutorial -> 0575C8D592FB7B088226750ACEEC2B4E
    config.json -> 06B2D3B23DCE96E1619D2B53D6C947EC
    effects_info.json -> C2A6213D2C7F57DB3340E17BC71D5182
    temp -> 3D801AA532C1CEC3EE82D87A99FDF63F
    config -> 2245023265AE4CF87D02C8B6BA991139
    effect -> BF5B17FAC5C60D745A593B5920372235
    */

    //temp
    private static final String TEMP_MD5 = "3D801AA532C1CEC3EE82D87A99FDF63F";
    //resource
    private static final String RESOURCE_MD5 = "96AB4E163F4EE03AAA4D1051AA51D204";
    //config as path
    private static final String CONFIG_DIR_MD5 = "2245023265AE4CF87D02C8B6BA991139";
    //config as fileName
    private static final String CONFIG_NAME_MD5 = CONFIG_DIR_MD5;
//    //config.json
//    private static final String CONFIG_JSON_MD5 = "06B2D3B23DCE96E1619D2B53D6C947EC";
    //effect
    private static final String EFFECT_INFO_JSON_MD5 = "BF5B17FAC5C60D745A593B5920372235";
    //font
    private static final String FONT_MD5 = "47A282DFE68A42D302E22C4920ED9B5E";
    //sticker
    private static final String STICKER_MD5 = "9173F7D7444D3161C4DEA50F35F5D15F";
    //filter
    private static final String FILTER_MD5 = "B2C97AE425DD751B0E48A3ACAE79CF4A";
    //mixer
    private static final String MIXER_MD5 = "10B5E1E383FFF2F51F8A7C9FD67F2E03";
    //background
    private static final String BACKGROUND_MD5 = "D229BBF31EAEEBC7C88897732D8B932D";
    //tutorial
    private static final String TUTORIAL_MD5 = "0575C8D592FB7B088226750ACEEC2B4E";



    public static final String INTERNAL_ROOT_DIR = App.getContext().getFilesDir().getAbsolutePath();

    public static final String INTERNAL_RES_DIR = INTERNAL_ROOT_DIR + File.separator + RESOURCE_MD5;

    public static final String INTERNAL_FONT_DIR = INTERNAL_RES_DIR + File.separator + FONT_MD5;


    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "fantuan" + File.separator +  "photoeditor" + File.separator + "files";

    public static final String RESOURCE_DIR = ROOT_DIR + File.separator + RESOURCE_MD5;

    public static final String CONFIG_DIR = ROOT_DIR + File.separator + CONFIG_DIR_MD5;

    public static final String TEMP_DIR = ROOT_DIR + File.separator + TEMP_MD5;

    public static final String CONFIG_FILE_NAME = CONFIG_NAME_MD5;
    public static final String EFFECT_FILE_NAME = EFFECT_INFO_JSON_MD5;

    public static final String RES_FONT_DIR = RESOURCE_DIR + File.separator + FONT_MD5;
    public static final String RES_STICKER_DIR = RESOURCE_DIR + File.separator + STICKER_MD5;
    public static final String RES_BACKGROUND_DIR = RESOURCE_DIR + File.separator + BACKGROUND_MD5;
    public static final String RES_MIXER_DIR = RESOURCE_DIR + File.separator + MIXER_MD5;

    public static final String RES_TUTORIAL_DIR = RESOURCE_DIR + File.separator + TUTORIAL_MD5;


//    public static final String RESOURCE_DIR = FileUtils.getCacheDir(App.getContext(),
//            ".normal_res", true) + File.separator;

    /**
     * 数据是否已初始化
     */
    public boolean mIsDataInit = false;

    private static DataManager mInstance = null;

//    private List<Integer> mCameraIds;
//    private List<CameraSizeBean> mCameraSizes;
//    private List<FilterCategoryBean> mImageFilterCategoryList;

    /** 保存已下载资源 **/
    private List<String> mInstalledRes = new CopyOnWriteArrayList<>();

    public static DataManager getInstance() {
        if (mInstance == null) {
            synchronized (DataManager.class) {
                if (mInstance == null) {
                    mInstance = new DataManager();
                }
            }
        }
        return mInstance;
    }

    private DataManager() {
//        mCameraIds = new ArrayList<>();
//        mCameraSizes = new ArrayList<>();
    }

    public void initData() {
//        initFilterCategoryDB();
//        initFilterDB();
//        initStickerDB();
//        initMagazineDB();
//        initBackgroundDB();
//        SPDataManager.setInitDBDataSuccess(true);
//        initPipDB();
//        checkCameraData();
//        mCameraSizes = queryAllCameraSize(mCameraIds);
//        loadAllImageFilterMap();
        // do something in here

//        setDataInitSuccess(true);

//        if (DebugUtil.isDebuggable() || CommercializeSDK.getInstance().isBuyUser()) {
//            checkSdCard();
//        }
    }

//    private void checkSdCard() {
//        File rootDir = new File(Environment.getExternalStorageDirectory(), "."+App.getPackageName());
//        if (!rootDir.exists()) {
//            return;
//        }
//        File[] files = rootDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".zip");
//            }
//        });
//        for (File file : files) {
//            String absolutePath = file.getAbsolutePath();
//            String name = file.getName().split("\\.")[0];
//            String[] cols = name.split("_");
//            // filter_item_1010.zip
//            // filter_1040.zip
//            // sticker_1040.zip
//            if (cols.length >= 2) {
//                String typeStr = cols[0];
//                String id = cols[1];
//                String childType = cols.length > 2 ? cols[2] : "";
//                StoreOnlineBean beanByModuleId = HomeOnlineManager.getInstance().getBeanByModuleId(id);
//                if (beanByModuleId == null) {
//                    RLog.e(TAG, "test effects file error: no find id, " + absolutePath);
//                    continue;
//                }
//                final String packageName = beanByModuleId.getId();
//                int type = BaseOnlineManager.NORMAL_RESOURCE_FILE;
//                if (TextUtils.equals(BaseOnlineManager.STORE_FUNC_KEY_FILTER, typeStr)) {
//                    type = BaseOnlineManager.STORE_FUNC_TYPE_FILTER;
//                    if (TextUtils.equals("item", childType)) {
//                        FilterBean filterBean = StoreOnlineManager.toFilterBean(beanByModuleId, absolutePath);
//                        //下载完成， 添加到local滤镜里
//                        DataManager.getInstance().insertFilter(filterBean);
//                        DownloadUtils.sendDownloadFinishBroadcast(App.getContext(), packageName, type);
//                    } else {
//                        DownloadUtils.handleFilterCategory(absolutePath, beanByModuleId, false);
//                    }
//                } else if (TextUtils.equals(BaseOnlineManager.STORE_FUNC_KEY_STICKER, typeStr)) {
//                    type = BaseOnlineManager.STORE_FUNC_TYPE_STICKER;
//                    ExtraBean extraBean = StoreOnlineManager.toExtraBean(beanByModuleId, absolutePath);
//                    //下载完成， 添加到local滤镜里
//                    DataManager.getInstance().insertOrUpdateSticker(extraBean);
//                    DownloadUtils.sendDownloadFinishBroadcast(App.getContext(), packageName, type);
//                } else if (TextUtils.equals(BaseOnlineManager.STORE_FUNC_KEY_BACKGROUND, typeStr)) {
//                    type = BaseOnlineManager.STORE_FUNC_TYPE_BACKGROUND;
//                    DownloadUtils.handleFilterCategory(absolutePath, beanByModuleId, false);
//                } else if (TextUtils.equals(BaseOnlineManager.STORE_FUNC_KEY_TEMPLATE, typeStr)) {
//                    type = BaseOnlineManager.STORE_FUNC_TYPE_TEMPLATE;
//                    MagazineBean extraBean = StoreOnlineManager.toMagazineBean(beanByModuleId, absolutePath);
//                    //下载完成， 添加到local滤镜里
//                    extraBean.setLocalIndex(0);
//                    DataManager.getInstance().insertOrUpdateMagazine(extraBean);
//                    DownloadUtils.sendDownloadFinishBroadcast(App.getContext(), packageName, type);
//                } else/* if (TextUtils.equals(BaseOnlineManager.NORMAL_RESOURCE_FILE, typeStr))*/ {
//                    type = BaseOnlineManager.NORMAL_RESOURCE_FILE;
//                }
//
//            } else {
//                RLog.e(TAG, "file name error, eg:filter_10401_item or filter_1040" + name);
//                continue;
//            }
//        }
//    }

//    public void loadInstalledResData() {
//        List<FilterCategoryBean> filterCategory = getAllImageFilterCategory();
//        if (filterCategory != null) {
//            for (FilterCategoryBean bean : filterCategory) {
//                List<FilterBean> filterBeanList = loadFilterByCategoryId(bean.getCategoryId());
//                if (filterBeanList != null) {
//                    for (FilterBean filter : filterBeanList) {
//                        addInstalledRes(filter.getPackageName());
//                    }
//                }
//            }
//        }
//
//        ArrayList<ExtraBean> stickerBean = getAllUsableSticker();
//        if (stickerBean != null) {
//            for (ExtraBean sticker : stickerBean) {
//                addInstalledRes(sticker.getPkgName());
//            }
//        }
//
//        ArrayList<MagazineBean> magazinebean = getMagazineList(false);
//        if (magazinebean != null) {
//            for (MagazineBean magazine : magazinebean) {
//                addInstalledRes(magazine.getPackageName());
//            }
//        }
//
//        ArrayList<BackgroundBean> backgroundBeen = getAllBackgroundBean();
//        if (backgroundBeen != null) {
//            for (BackgroundBean background : backgroundBeen) {
//                addInstalledRes(background.getName());
//            }
//        }
//    }

    public void release() {
    }

    public boolean isDataInit() {
        return mIsDataInit;
    }

    public void setDataInitSuccess(boolean success) {
        mIsDataInit = success;
    }

//    private void loadAllImageFilterMap() {
//        if (mImageFilterCategoryList == null) {
//            getAllImageFilterCategory();
//        }
////        if (mImageFilterCategoryList != null) {
////            for (FilterCategoryBean categoryBean : mImageFilterCategoryList) {
////                loadFilterByCategoryId(categoryBean.getCategoryId());
////            }
////        }
//    }

    /**
     * 加载分类id下所有可用的滤镜
     *
     * @param categoryId
     * @return
     */
//    public List<FilterBean> loadFilterByCategoryId(String categoryId) {
//        ArrayList<FilterBean> ret;
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.CategoryId.eq(categoryId),
//                FilterBeanDao.Properties.Status.eq(STATUS_USE)).build();
//        ret = (ArrayList<FilterBean>) query.list();
//        return ret;
//    }

    /**
     * 加载分类id下所有可用的背景
     *
     * @param categoryId
     * @return
     */
//    public List<BackgroundBean> loadBackgroundByCategoryId(Long categoryId) {
//        BackgroundBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//        Query query = dao.queryBuilder().where(BackgroundBeanDao.Properties.CategoryId.eq(categoryId)).build();
//        return query.list();
//    }

//    public boolean isExistFilterCategory(String categoryId) {
//        boolean ret = false;
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        Query query = dao.queryBuilder().where(FilterCategoryBeanDao.Properties.CategoryId.eq(categoryId)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        return ret;
//    }

//    public void insertFilterCategory(FilterCategoryBean categoryBean) {
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        if (!isExistFilterCategory(categoryBean.getCategoryId())) {
//            categoryBean.setLocalIndex(0);
//            dao.insert(categoryBean);
//            mImageFilterCategoryList.add(0, categoryBean);
//        }
//    }

//    public void insertBackgroundCategory(BackgroundCategoryBean categoryBean) {
//        if (categoryBean == null) {
//            return;
//        }
//        BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        dao.insert(categoryBean);
//    }

//    public void insertBackground(BackgroundBean backgroundBean) {
//        if (backgroundBean == null) {
//            return;
//        }
//        addInstalledRes(backgroundBean.getName());
//        BackgroundBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//        dao.insert(backgroundBean);
//    }

    /**
     * 指定localIndex插入滤镜分类
     *
     * @param categoryBean
     * @param localIndex
     */
//    public void insertFilterCategory(FilterCategoryBean categoryBean, int localIndex) {
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        if (!isExistFilterCategory(categoryBean.getCategoryId())) {
//            int count = (int) dao.count();
//            if (localIndex < count) {
//                // 更新数据库中其他的分类locaIndex，不直接使用mImageFilterCategoryList更新，防止其他地方改动mImageFilterCategoryList
//                List<FilterCategoryBean> dbData = dao.loadAll();
//                for (FilterCategoryBean bean : dbData) {
//                    if (bean.getLocalIndex() >= localIndex) {
//                        bean.setLocalIndex(bean.getLocalIndex() + 1);
//                    }
//                }
//                dao.updateInTx(dbData);
//                // 更新内存中其他的分类locaIndex
//                for (FilterCategoryBean bean : mImageFilterCategoryList) {
//                    if (bean.getLocalIndex() >= localIndex) {
//                        bean.setLocalIndex(bean.getLocalIndex() + 1);
//                    }
//                }
//            } else {
//                localIndex = 0;
//            }
//            categoryBean.setLocalIndex(localIndex);
//            mImageFilterCategoryList.add(localIndex, categoryBean);
//            dao.insert(categoryBean);
//        }
//    }

//    public void updateFilterCategory(ArrayList<FilterCategoryBean> categoryList) {
//        try {
//            FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//            mImageFilterCategoryList.clear();
//            mImageFilterCategoryList.addAll(categoryList);
//            dao.updateInTx(categoryList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void updateBackgroundCategory(ArrayList<BackgroundCategoryBean> categoryList) {
//        try {
//            // 由于两张表是级联关系，调整顺序需要重新建立关联
//            BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//            BackgroundBeanDao beanDao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//            List<BackgroundBean> allChildBean = new ArrayList<>();
//            for (BackgroundCategoryBean backgroundCategoryBean : categoryList) {
//                allChildBean.addAll(backgroundCategoryBean.getBackgroundBeanList());
//            }
//            beanDao.deleteAll();
//            dao.deleteAll();
//            for (BackgroundCategoryBean backgroundCategoryBean : categoryList) {
//                Long categoryIdBefore = backgroundCategoryBean.getId();
//                Long selectedId = backgroundCategoryBean.getSelectedId();
//                backgroundCategoryBean.setId(null);
//                backgroundCategoryBean.setSelectedId(null);
//                dao.insert(backgroundCategoryBean);
//                backgroundCategoryBean.update();
//                for (BackgroundBean backgroundBean : allChildBean) {
//                    if (backgroundBean.getCategoryId().equals(categoryIdBefore)) {
//                        backgroundBean.setId(null);
//                        backgroundBean.setCategoryId(backgroundCategoryBean.getId());
//                        beanDao.insert(backgroundBean);
//                        beanDao.update(backgroundBean);
//                        if (selectedId != null && selectedId.equals(backgroundBean.getId())) {
//                            backgroundCategoryBean.setSelectedId(backgroundBean.getId());
//                            backgroundCategoryBean.update();
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void deleteFilterCategoryById(String categoryId) {
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        Query query = dao.queryBuilder().where(FilterCategoryBeanDao.Properties.CategoryId.eq(categoryId)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            dao.deleteInTx(list);
//        }
//        for (FilterCategoryBean bean : mImageFilterCategoryList) {
//            if (TextUtils.equals(bean.getCategoryId(), categoryId)) {
//                mImageFilterCategoryList.remove(bean);
//                break;
//            }
//        }
//        // 同时删除分类下滤镜
//        deleteFilterByCategoryId(categoryId);
//    }

//    public String getCategoryIdOfCategoryByPkgName(String pkgName) {
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        Query query = dao.queryBuilder().where(FilterCategoryBeanDao.Properties.PkgName.eq(pkgName)).build();
//        List list = query.list();
//        String categoryId = "";
//        if (list != null && !list.isEmpty()) {
//            categoryId = ((FilterCategoryBean) list.get(0)).getCategoryId();
//        }
//        return categoryId;
//    }

//    public FilterCategoryBean getFilterCategoryByCategoryId(String categoryId) {
//        for (FilterCategoryBean bean : mImageFilterCategoryList) {
//            if (TextUtils.equals(bean.getCategoryId(), categoryId)) {
//                return bean;
//            }
//        }
//        return null;
//    }


//    public List<FilterBean> getFilterBeansByCategoryId(String categoryId) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.CategoryId.eq(categoryId)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            return list;
//        }
//        return null;
//    }

//    public void deleteBackgroundCategoryById(Long categoryId) {
//        BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        Query query = dao.queryBuilder().where(BackgroundCategoryBeanDao.Properties.Id.eq(categoryId)).build();
//        List list = query.list();
//        if (list.size() > 0) {
//            BackgroundCategoryBean categoryBean = (BackgroundCategoryBean) list.get(0);
//            List<BackgroundBean> backgroundBeanList = categoryBean.getBackgroundBeanList();
//            if (backgroundBeanList != null && backgroundBeanList.size() > 0) {
//                BackgroundBeanDao backgroundBeanDao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//                for (BackgroundBean backgroundBean : backgroundBeanList) {
//                    delInstalledRes(backgroundBean.getName());
//                    backgroundBeanDao.delete(backgroundBean);
//                }
//            }
//            dao.delete(categoryBean);
//        }
//    }

//    private void updateFilterCategoryWhenDelFilter(String categoryId) {
//        // 分类下没有滤镜，删除该分类
//        List<FilterBean> list = loadFilterByCategoryId(categoryId);
//        if (list == null || list.size() == 0) {
//            for (FilterCategoryBean bean : mImageFilterCategoryList) {
//                if (TextUtils.equals(bean.getCategoryId(), categoryId)) {
//                    mImageFilterCategoryList.remove(bean);
//                    break;
//                }
//            }
//        }
//    }

    /**
     * 是否已经存在滤镜
     *
     * @param pkgName 滤镜包名
     * @return
     */
//    public boolean isExistFilter(String pkgName) {
//        boolean ret = false;
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.PackageName.eq(pkgName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        return ret;
//    }

    /**
     * 是否已经存在背景
     *
     * @param name 背景名称
     * @return
     */
//    public boolean isExistBackground(String name) {
//        boolean ret = false;
//        BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        Query query = dao.queryBuilder().where(BackgroundCategoryBeanDao.Properties.Name.eq(name)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        if (!ret) {
//            BackgroundBeanDao beanDao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//            Query beanQuery = beanDao.queryBuilder().where(BackgroundBeanDao.Properties.Name.eq(name)).build();
//            List beanList = beanQuery.list();
//            if (beanList != null && !beanList.isEmpty()) {
//                ret = true;
//            }
//        }
//        return ret;
//    }

    /**
     * @param filters
     */
//    public void insertFilters(ArrayList<FilterBean> filters) {
//        if (filters == null || filters.size() <= 0) {
//            return;
//        }
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Iterator<FilterBean> iterator = filters.iterator();
//        while (iterator.hasNext()) {
//            FilterBean bean = iterator.next();
//            if (isExistFilter(bean.getPackageName())) {
//                iterator.remove();
//            }
//        }
//        int dbCount = (int) dao.count();
//        if (filters != null && filters.size() > 0) {
//            for (int i = 0; i < filters.size(); ++i) {
//                FilterBean bean = filters.get(i);
//                addInstalledRes(bean.getPackageName());
//                bean.setLocalIndex(dbCount + i);
//            }
//        }
//        dao.insertInTx(filters);
//    }

    /**
     * 插入滤镜,新滤镜位置排最后
     * 注意：调用此方法插入滤镜前需要判断是否已插入新的滤镜分类
     *
     * @param filterBean
     */
//    public void insertFilter(FilterBean filterBean) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        if (!isExistFilter(filterBean.getPackageName())) {
//            addInstalledRes(filterBean.getPackageName());
//            // 新插入滤镜位置排最后
//            filterBean.setLocalIndex((int) dao.count());
//            dao.insert(filterBean);
//        }
//    }

    /**
     * local界面 ， 删除内置滤镜，其实是更新状态为不可用
     *
     * @param id
     */
//    public void deleteInternalFilterById(int id) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Id.eq(id)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            FilterBean bean = (FilterBean) list.get(0);
//            bean.setStatus(STATUS_UNUSE);
//            dao.update(bean);
//            updateFilterCategoryWhenDelFilter(bean.getCategoryId());
//        }
//    }

    /**
     * 删除分类下的滤镜
     *
     * @param categoryId
     */
//    public void deleteFilterByCategoryId(String categoryId) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.CategoryId.eq(categoryId)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            for (Object bean : list) {
//                delInstalledRes(((FilterBean)bean).getPackageName());
//            }
//            dao.deleteInTx(list);
//        }
//    }

    /**
     * 在filter列表点击下载内置滤镜， 把该滤镜状态改为可用
     * 注意：调用此方法插入滤镜前需要判断是否已插入新的滤镜分类
     *
     * @param name
     */
//    public void updateInternalFilterByName(String name) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Name.eq(name)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            FilterBean bean = (FilterBean) list.get(0);
//            bean.setStatus(FilterConstant.STATUS_USE);
//            dao.update(bean);
//        }
//    }

    /**
     * local界面 ， 删除下载滤镜
     *
     * @param id
     */
//    public void deleteLocalFilterById(int id) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        dao.deleteByKey((long) id);
//        // 更新滤镜分类
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Id.eq(id)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            FilterBean bean = (FilterBean) list.get(0);
//            updateFilterCategoryWhenDelFilter(bean.getCategoryId());
//        }
//    }

    /**
     * 根据包名删除滤镜
     *
     * @param packageNameList
     */
//    public void deleteFilterByPackageNames(ArrayList<String> packageNameList) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        if (packageNameList == null || packageNameList.size() < 1) {
//            //为空， 则删除所有下载的滤镜
//            Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Type.eq(TYPE_DOWNLOAD)).build();
//            List list = query.list();
//            if (list != null && !list.isEmpty()) {
//                dao.deleteInTx(list);
//                // 更新滤镜分类
//                for (Object bean : list) {
//                    updateFilterCategoryWhenDelFilter(((FilterBean) bean).getCategoryId());
//                }
//            }
//        } else {
//            ArrayList<FilterBean> deleteList = new ArrayList<>();
//            for (String pkgName : packageNameList) {
//                Query query = dao.queryBuilder().where(FilterBeanDao.Properties.PackageName.eq(pkgName)).build();
//                List list = query.list();
//                if (list != null && !list.isEmpty()) {
//                    deleteList.addAll(list);
//                }
//            }
//            if (deleteList.size() > 0) {
//                dao.deleteInTx(deleteList);
//                // 更新滤镜分类
//                for (FilterBean bean : deleteList) {
//                    updateFilterCategoryWhenDelFilter(bean.getCategoryId());
//                }
//            }
//        }
//    }

    /**
     * 上下拖动loca，改变顺序后，更新数据库状态。
     *
     * @param listNew
     */
//    public void updateNum(List<FilterBean> listNew) {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        dao.updateInTx(listNew);
//    }

    /**
     * 根据包名获取滤镜
     *
     * @return
     */
//    public FilterBean getLocalFilterByPackageName(String packageName) {
//        FilterBean ret = null;
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.PackageName.eq(packageName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            FilterBean bean = (FilterBean) list.get(0);
//            ret = bean;
//        }
//        return ret;
//    }

    /**
     * 查询所有滤镜 , 过滤掉more , 并且状态可用 , Local界面使用 , num 顺序排列
     *
     * @return
     */
//    public ArrayList<FilterBean> getLocalFilterListStatusUse() {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Status.eq(STATUS_USE)).orderAsc(FilterBeanDao.Properties.LocalIndex).build();
//        return (ArrayList<FilterBean>) query.list();
//    }

    /**
     * 查询所有滤镜，状态为可用，按分类排序
     *
     * @return
     */
//    public ArrayList<FilterBean> getLocalFilterListStatusUseByOrder() {
//        List<FilterCategoryBean> categorys = getAllImageFilterCategory();
//        ArrayList<FilterBean> ret = new ArrayList<>();
//        for (FilterCategoryBean category : categorys) {
//            ret.addAll(loadFilterByCategoryId(category.getCategoryId()));
//        }
//        return ret;
//    }

//    public ArrayList<BackgroundBean> getAllBackgroundBean() {
//        List<BackgroundCategoryBean> categoryBeanList = getAllBackgroundCategoryBean();
//        ArrayList<BackgroundBean> result = new ArrayList<>();
//        for (BackgroundCategoryBean categoryBean : categoryBeanList) {
//            result.addAll(categoryBean.getBackgroundBeanList());
//        }
//        return result;
//    }

//    public List<BackgroundCategoryBean> getAllBackgroundCategoryBean() {
//        BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        List<BackgroundCategoryBean> categoryBeanList = dao.queryBuilder()
//                .orderAsc(BackgroundCategoryBeanDao.Properties.LocalIndex)
//                .orderDesc(BackgroundCategoryBeanDao.Properties.Id).list();
//        // 重置选中状态，暂时不需要保存
//        for (BackgroundCategoryBean categoryBean : categoryBeanList) {
//            List<BackgroundBean> list = categoryBean.getBackgroundBeanList();
//            if (list != null && list.size() > 0) {
//                categoryBean.setSelectedId(list.get(0).getId());
//                categoryBean.update();
//            }
//        }
//        return categoryBeanList;
//    }

//    public BackgroundCategoryBean getBackgroundCategoryBean(String name) {
//        BackgroundCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        Query query = dao.queryBuilder().where(BackgroundCategoryBeanDao.Properties.Name.eq(name)).build();
//        BackgroundCategoryBean ret = null;
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = (BackgroundCategoryBean) list.get(0);
//        }
//        return ret;
//    }

    /**
     * 本地内置不可用(删除)状态
     *
     * @return
     */
//    public ArrayList<FilterBean> getLocalFilterListStatusNo() {
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        Query query = dao.queryBuilder().where(FilterBeanDao.Properties.Status.eq(STATUS_UNUSE),
//                FilterBeanDao.Properties.Type.eq(FilterConstant.TYPE_BUILDIN)).orderAsc(FilterBeanDao.Properties.LocalIndex).build();
//        return (ArrayList<FilterBean>) query.list();
//    }

//    private void initFilterDB() {
//        // 只在第一次启动时初始化
//        if (SPDataManager.isInitDBDataSuccess()) {
//            return;
//        }
//        FilterBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterBeanDao();
//        if (dao.count() > 0) {
//            return;
//        }
//        Context context = App.getContext();
//        Resources resources = context.getResources();
//        String[] pkgName = resources.getStringArray(R.array.filter_pkg_name);
//        ArrayList<FilterBean> list = new ArrayList<>(pkgName.length);
//        for (int i = 0; i < pkgName.length; ++i) {
//            FilterBean filterBean = new FilterBean(
//                    BUILDIN_FILTER_CATEGORY_ID_MAP.get(pkgName[i]),
//                    BUILDIN_FILTER_NAME.get(pkgName[i]),
//                    FilterConstant.TYPE_BUILDIN,
//                    FilterConstant.UNLOCK,
//                    0,
//                    FilterConstant.STATUS_USE,
//                    i,
//                    BUILDIN_FILTER_IMAGE_URL.get(pkgName[i]),
//                    pkgName[i],
//                    "", "", "");
//            list.add(filterBean);
//        }
//
//        dao.insertInTx(list);
//    }

//    public void initBackgroundDB() {
//        BackgroundBeanDao backgroundBeanDao = DBHelper.getInstance().getDaoSession().getBackgroundBeanDao();
//        BackgroundCategoryBeanDao categoryBeanDao = DBHelper.getInstance().getDaoSession().getBackgroundCategoryBeanDao();
//        if (backgroundBeanDao.count() > 0 || categoryBeanDao.count() > 0) {
//            return;
//        }
//        BackgroundCategoryBean backgroundCategoryBean = new BackgroundCategoryBean();
//        backgroundCategoryBean.setType(BackgroundCategoryBean.TYPE_LOCAL_COLOR);
//        backgroundCategoryBean.setColor(0xFFFFFFFF);
//        backgroundCategoryBean.setName("Color");
//        Long id = categoryBeanDao.insert(backgroundCategoryBean);
//        if (id <= 0) {
//            return;
//        }
//        backgroundCategoryBean.update();
//
//        int[] localColors = App.getContext().getResources().getIntArray(R.array.colors);
//
//        boolean selected = false;
//        for (int localColor : localColors) {
//            BackgroundBean backgroundBean = new BackgroundBean();
//            backgroundBean.setColor(localColor);
//            backgroundBean.setType(BackgroundBean.TYPE_LOCAL_COLOR);
//            backgroundBean.setCategoryId(backgroundCategoryBean.getId());
//            backgroundBeanDao.insert(backgroundBean);
//            backgroundBeanDao.update(backgroundBean);
//            if (!selected) {
//                selected = true;
//                backgroundCategoryBean.setSelectedId(backgroundBean.getId());
//                backgroundCategoryBean.update();
//            }
//        }
//    }

//    public List<FilterCategoryBean> getAllImageFilterCategory() {
//        if (mImageFilterCategoryList != null) {
//            // 拷贝列表，防止外部改变列表，导致异常
//            return (List<FilterCategoryBean>) ((ArrayList) mImageFilterCategoryList).clone();
//        }
//        List<FilterCategoryBean> ret;
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        ret = dao.queryBuilder().orderAsc(FilterCategoryBeanDao.Properties.LocalIndex).orderDesc(FilterCategoryBeanDao.Properties.Id).list();
//        mImageFilterCategoryList = (List<FilterCategoryBean>) ((ArrayList) ret).clone();
//        return ret;
//    }

//    private void initFilterCategoryDB() {
//        // 只在第一次启动时初始化
//        if (SPDataManager.isInitDBDataSuccess()) {
//            getAllImageFilterCategory();
//            return;
//        }
//        FilterCategoryBeanDao dao = DBHelper.getInstance().getDaoSession().getFilterCategoryBeanDao();
//        if (dao.count() > 0) {
//            return;
//        }
//        Context context = App.getContext();
//        Resources resources = context.getResources();
//        String[] categoryIds = resources.getStringArray(R.array.filter_category_id);
//        ArrayList<FilterCategoryBean> list = new ArrayList<>(categoryIds.length);
//        for (int i = 0; i < categoryIds.length; ++i) {
//            String name = BUILDIN_FILTER_CATEGORY_NAME.get(categoryIds[i]);
//            FilterCategoryBean bean = new FilterCategoryBean(null, categoryIds[i], name, FilterConstant.TYPE_BUILDIN,
//                    "", "", "", "", -1, "", "",
//                    "1", "", true, i);
//
//            list.add(bean);
//        }
//        mImageFilterCategoryList = list;
//
//        dao.insertInTx(list);
//    }


    /**
     * 是否已经存在画中画
     *
     * @param name 名称
     * @return
     */
//    public boolean isExistPip(String name) {
//        boolean ret = false;
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.Name.eq(name)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        return ret;
//    }

//    public void insertPip(PipBean bean) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        if (!isExistPip(bean.getName())) {
//            dao.insert(bean);
//        }
//    }

    /**
     * local界面 ， 删除内置画中画，其实是更新状态为不可用
     *
     * @param id
     */
//    public void deleteInternalPipById(int id) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.Id.eq(id)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            PipBean bean = (PipBean) list.get(0);
//            bean.setStatus(PipBean.STATUS_UNUSE);
//            dao.update(bean);
//        }
//    }

    /**
     * 在画中画列表点击下载内置画中画， 把该状态改为可用
     *
     * @param name
     */
//    public void updateInternalPipByName(String name) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.Name.eq(name)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            PipBean bean = (PipBean) list.get(0);
//            bean.setStatus(PipBean.STATUS_USE);
//            dao.update(bean);
//        }
//    }

    /**
     * local界面 ， 删除下载画中画
     *
     * @param id
     */
//    public void deleteLocalPipById(int id) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        dao.deleteByKey((long) id);
//    }

    /**
     * 根据包名删除画中画 (sdcard滤镜文件被删除， 点击)
     *
     * @param packageNameList
     */
//    public void deletePipByPackageNames(ArrayList<String> packageNameList) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        if (packageNameList == null || packageNameList.size() < 1) {
//            //为空， 则删除所有下载的滤镜
//            Query query = dao.queryBuilder().where(PipBeanDao.Properties.Type.eq(TYPE_DOWNLOAD)).build();
//            List list = query.list();
//            if (list != null && !list.isEmpty()) {
//                dao.deleteInTx(list);
//            }
//        } else {
//            ArrayList<PipBean> deleteList = new ArrayList<>();
//            for (String pkgName : packageNameList) {
//                Query query = dao.queryBuilder().where(PipBeanDao.Properties.PackageName.eq(pkgName)).build();
//                List list = query.list();
//                if (list != null && !list.isEmpty()) {
//                    deleteList.addAll(list);
//                }
//            }
//            if (deleteList.size() > 0) {
//                dao.deleteInTx(deleteList);
//            }
//        }
//    }

    /**
     * 上下拖动loca，改变顺序后，更新数据库状态。
     *
     * @param listNew
     */
//    public void updatePipNum(List<PipBean> listNew) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        dao.updateInTx(listNew);
//    }

    /**
     * 根据包名解锁
     *
     * @param packageName
     */
//    public void updateLockPipByPackageName(String packageName) {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.PackageName.eq(packageName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            PipBean bean = (PipBean) list.get(0);
//            bean.setUnLock(UNLOCK);
//            dao.update(bean);
//        }
//    }

    /**
     * 获取最大num ， 插入时使用
     *
     * @return
     */
//    public int getMaxPipNum() {
//        int ret = 0;
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        ret = (int) dao.count();
//        return ret;
//    }

    /**
     * 根据包名获取画中画
     *
     * @return
     */
//    public PipBean getPipByPackageName(String packageName) {
//        PipBean ret = null;
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.PackageName.eq(packageName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            PipBean bean = (PipBean) list.get(0);
//            ret = bean;
//        }
//        return ret;
//    }

    /**
     * 获取所有可用的画中画
     *
     * @return
     */
//    public ArrayList<PipBean> getLocalPipListAll() {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().orderAsc(PipBeanDao.Properties.LocalIndex).build();
//        return (ArrayList<PipBean>) query.list();
//    }

    /**
     * 查询所有画中画, 状态可用 , Local界面使用 , num 顺序排列
     *
     * @return
     */
//    public ArrayList<PipBean> getPipList() {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.Status.eq(STATUS_USE)).orderAsc(PipBeanDao.Properties.LocalIndex).build();
//        return (ArrayList<PipBean>) query.list();
//    }

    /**
     * 本地内置不可用(删除)状态
     *
     * @return
     */
//    public ArrayList<PipBean> getLocalPipListStatusNo() {
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        Query query = dao.queryBuilder().where(PipBeanDao.Properties.Status.eq(STATUS_UNUSE),
//                PipBeanDao.Properties.Type.eq(FilterConstant.TYPE_BUILDIN)).orderAsc(PipBeanDao.Properties.LocalIndex).build();
//        return (ArrayList<PipBean>) query.list();
//    }

//    private void initPipDB() {
//        // 只在第一次启动时初始化
//        if (SPDataManager.isInitDBDataSuccess()) {
//            return;
//        }
//        PipBeanDao dao = DBHelper.getInstance().getDaoSession().getPipBeanDao();
//        if (dao.count() > 0) {
//            return;
//        }
//        Context context = App.getContext();
//        Resources resources = context.getResources();
//        String[] imageUrl = resources.getStringArray(R.array.pip_image_url);
//        String[] name = resources.getStringArray(R.array.pip_name);
//        String[] pkgName = resources.getStringArray(R.array.pip_pkg_name);
//        ArrayList<PipBean> list = new ArrayList<>(pkgName.length);
//        for (int i = 0; i < pkgName.length; ++i) {
//            PipBean pipBean = new PipBean(name[i], FilterConstant.TYPE_BUILDIN,
//                    FilterConstant.UNLOCK, -1, FilterConstant.STATUS_USE, i, imageUrl[i],
//                    pkgName[i], "", "", "", "", "", "");
//            list.add(pipBean);
//        }
//        dao.insertInTx(list);
//    }

    public void setPreferenceSaveLocation(String location) {
        SPDataManager.setPhotoSaveLocation(location);
    }

    public String getPreferenceSaveLocation() {
        String location = SPDataManager.getPhotoSaveLocation();
        if (PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(location)) {//如果是5.0及以上  外置sd卡是可以读写的
            boolean hasPermission = ExtSdcardUtils.hasExtSdcardPermission();
            File f = new File(location);
            if (!hasPermission || !f.exists()) {//没有权限
                location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera";
                setPreferenceSaveLocation(location);
                return location;
            }
        } else {
            File f = new File(location);
            if (!f.exists() || !f.canRead() || !f.canWrite()) {//如果当前路径不存在 或者不能读  或者不能写 则设置为默认路径
                location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera";
                setPreferenceSaveLocation(location);
                return location;
            }
        }
        return location;
    }

    /**
     * 获取最大纹理，0为未初始化
     *
     * @return
     */
    public int getMaxTextureSize() {
        return SPDataManager.getMaxTextureSize();
    }

    /**
     * 初始化最大纹理，需要在GL线程执行
     */
    public void initMaxTextureSize() {
        int size = getMaxTextureSize();
        if (size == 0) {
            try {
                IntBuffer ib = IntBuffer.allocate(1);
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, ib);
                ib.position(0);
                size = ib.get();
            } catch (Throwable tr) {
            }
            if (size >= 2048) {
                SPDataManager.setMaxTextureSize(size);
            }
        }
    }

    /**
     * 是否有缓存摄像头尺寸数据
     *
     * @param cameraId
     * @return
     */
//    public synchronized boolean hasCameraSizes(int cameraId) {
//        return mCameraIds.contains(Integer.valueOf(cameraId));
//    }

//    /**
//     * 缓存摄像头尺寸数据
//     *
//     * @param cameraId
//     * @param pictureSizes
//     * @param videoSizes
//     */
//    public synchronized void addCameraSizes(int cameraId, List<CameraController.Size> pictureSizes, List<VideoQuality> videoSizes) {
//        if (!hasCameraSizes(cameraId) && pictureSizes != null && pictureSizes.size() > 0
//                && videoSizes != null && videoSizes.size() > 0) {
//            mCameraIds.add(cameraId);
//            List<CameraSizeBean> sizes = new ArrayList<>();
//            CameraSizeBean size = null;
//            for (CameraController.Size psize : pictureSizes) {
//                size = new CameraSizeBean(cameraId,
//                        CameraSizeBean.TYPE_PICTURE,
//                        psize.getWidth(), psize.getHeight(), null);
//                sizes.add(size);
//                mCameraSizes.add(size);
//            }
//            for (VideoQuality quality : videoSizes) {
//                size = new CameraSizeBean(cameraId,
//                        CameraSizeBean.TYPE_VIDEO,
//                        quality.mSize.getWidth(), quality.mSize.getHeight(), quality.mQuality);
//                sizes.add(size);
//                mCameraSizes.add(size);
//            }
//            insertCameraSize(sizes);
//        }
//    }

//    /**
//     * 获取缓存摄像头尺寸数据
//     *
//     * @param cameraId
//     * @param pictureSizes 用于填充拍照尺寸数据列表
//     * @param videoSizes   用于填充视频尺寸数据列表
//     */
//    public synchronized void getCameraSizes(int cameraId, List<CameraController.Size> pictureSizes, List<VideoQuality> videoSizes) {
//        if (hasCameraSizes(cameraId) && pictureSizes != null && videoSizes != null) {
//            pictureSizes.clear();
//            videoSizes.clear();
//            for (CameraSizeBean size : mCameraSizes) {
//                if (size.getCameraId() == cameraId) {
//                    if (size.getType() == CameraSizeBean.TYPE_PICTURE) {
//                        pictureSizes.add(new CameraController.Size(size.getWidth(), size.getHeight()));
//                    } else if (size.getType() == CameraSizeBean.TYPE_VIDEO) {
//                        videoSizes.add(new VideoQuality(size.getValue(),
//                                new CameraController.Size(size.getWidth(), size.getHeight())));
//                    }
//                }
//            }
//        }
//    }

    /**
     * 清理缓存
     */
//    public synchronized void clearCameraSizeData() {
//        mCameraIds.clear();
//        mCameraSizes.clear();
//        clearCameraSizeDataInner();
//    }

    /**
     * 如果有ROM升级则清除缓存数据
     */
//    public synchronized void checkCameraData() {
//        try {
//            if (!TextUtils.equals(Build.FINGERPRINT, SPDataManager.getCameraSizesFingerPrint())) {
//                clearCameraSizeData();
//                SPDataManager.setCameraSizesFingerPrint(Build.FINGERPRINT);
//            }
//        } catch (Throwable tr) {
//            DLog.e(TAG, "", tr);
//        }
//    }

//    private void clearCameraSizeDataInner() {
//        CameraSizeBeanDao dao = DBHelper.getInstance().getDaoSession().getCameraSizeBeanDao();
//        dao.deleteAll();
//    }

//    public synchronized void insertCameraSize(List<CameraSizeBean> sizes) {
//        CameraSizeBeanDao dao = DBHelper.getInstance().getDaoSession().getCameraSizeBeanDao();
//        dao.insertInTx(sizes);
//    }

//    public synchronized List<CameraSizeBean> queryAllCameraSize(List<Integer> cameraIds) {
//        List<CameraSizeBean> ret = new ArrayList<>();
//        CameraSizeBeanDao dao = DBHelper.getInstance().getDaoSession().getCameraSizeBeanDao();
//        for (int i : cameraIds) {
//            CameraSizeBean bean = dao.load((long) i);
//            if (bean != null) {
//                ret.add(bean);
//            }
//        }
//        return ret;
//    }

//    public void setPreferenceShowTiltshiftClickTips(boolean show) {
//        SPDataManager.setShowTiltshiftClickTips(show);
//    }

//    public boolean getPreferenceShowTiltshiftClickTips() {
//        return SPDataManager.getShowTiltshiftClickTips();
//    }

//    public void setShowTiltshiftClickTips(boolean show) {
//        GPUImage.setTiltShiftShowClickTips(show);
//    }

//    public void recordShareImageTool(String pkgName, String activityName) {
//        if (!TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(activityName) && !ShareImageTools.isDefaultTools(pkgName, activityName)) {
//            String tool1PkgName = getShareImageTool1PkgName();
//            String tool1ActivityName = getShareImageTool1ActivityName();
//            ;
//            if (!TextUtils.isEmpty(tool1PkgName) && !TextUtils.isEmpty(tool1ActivityName)) {
//                if (tool1PkgName.equals(pkgName) && tool1ActivityName.equals(activityName)) {
//                    //同一个工具不需要记录
//                } else {
//                    SPDataManager.setShareImageTool2PkgName(tool1PkgName);
//                    SPDataManager.setShareImageTool2ActivityName(tool1ActivityName);
//                    SPDataManager.setShareImageTool1PkgName(pkgName);
//                    SPDataManager.setShareImageTool1ActivityName(activityName);
//                }
//            } else {
//                SPDataManager.setShareImageTool1PkgName(pkgName);
//                SPDataManager.setShareImageTool1ActivityName(activityName);
//            }
//        }
//    }

    public String getShareImageTool1PkgName() {
        return SPDataManager.getShareImageTool1PkgName();
    }

    public String getShareImageTool1ActivityName() {
        return SPDataManager.getShareImageTool1ActivityName();
    }

    public String getShareImageTool2PkgName() {
        return SPDataManager.getShareImageTool2PkgName();
    }

    public String getShareImageTool2ActivityName() {
        return SPDataManager.getShareImageTool2ActivityName();
    }

//    public void recordShareVideoTool(String pkgName, String activityName) {
//        if (!TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(activityName) && !ShareImageTools.isDefaultTools(pkgName, activityName)) {
//            String tool1PkgName = getShareVideoTool1PkgName();
//            String tool1ActivityName = getShareVideoTool1ActivityName();
//            ;
//            if (!TextUtils.isEmpty(tool1PkgName) && !TextUtils.isEmpty(tool1ActivityName)) {
//                if (tool1PkgName.equals(pkgName) && tool1ActivityName.equals(activityName)) {
//                    //同一个工具不需要记录
//                } else {
//                    SPDataManager.setShareVideoTool2PkgName(tool1PkgName);
//                    SPDataManager.setShareVideoTool2ActivityName(tool1ActivityName);
//                    SPDataManager.setShareVideoTool1PkgName(pkgName);
//                    SPDataManager.setShareVideoTool1ActivityName(activityName);
//                }
//            } else {
//                SPDataManager.setShareVideoTool1PkgName(pkgName);
//                SPDataManager.setShareVideoTool1ActivityName(activityName);
//            }
//        }
//    }

    public String getShareVideoTool1PkgName() {
        return SPDataManager.getShareImageTool1PkgName();
    }

    public String getShareVideoTool1ActivityName() {
        return SPDataManager.getShareImageTool1ActivityName();
    }

    public String getShareVideoTool2PkgName() {
        return SPDataManager.getShareImageTool2PkgName();
    }

    public String getShareVideoTool2ActivityName() {
        return SPDataManager.getShareImageTool2ActivityName();
    }

//    public synchronized ArrayList<ExtraBean> getAllSticker() {
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        return (ArrayList<ExtraBean>) dao.queryBuilder()
//                .orderAsc(ExtraBeanDao.Properties.LocalIndex)
//                .orderDesc(ExtraBeanDao.Properties.Id).list();
//    }

//    public synchronized ArrayList<ExtraBean> getAllUsableSticker() {
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.Version.le(PluginVersionUtil.getStickerPluginVersionCode()))
//                .orderAsc(ExtraBeanDao.Properties.LocalIndex)
//                .orderDesc(ExtraBeanDao.Properties.Id).build();
//        return (ArrayList<ExtraBean>) query.list();
//    }

//    public ExtraBean getStickerByPkgName(String pkgName) {
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.PkgName.eq(pkgName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            return (ExtraBean) list.get(0);
//        }
//        return null;
//    }

//    public synchronized void deleteSticker(String pkgName) {
//        //如果不是内置的才需要删除
//        if (!pkgName.equals(StickerConstant.APK_EMOJI_PKG_NAME)) {
////            delInstalledRes(pkgName);
////            ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
////            Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.PkgName.eq(pkgName)).build();
////            List list = query.list();
////            if (list != null && !list.isEmpty()) {
////                dao.deleteInTx(list);
////            }
//        }
//    }

//    public synchronized void deleteSticker(String pkgName, int resType) {
//        if (!pkgName.equals(StickerConstant.APK_EMOJI_PKG_NAME)) {
//            delInstalledRes(pkgName);
//            ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//            Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.PkgName.eq(pkgName),
//                    ExtraBeanDao.Properties.ResType.eq(resType)).build();
//            List list = query.list();
//            if (list != null && !list.isEmpty()) {
//                dao.deleteInTx(list);
//            }
//        }
//    }

//    public synchronized void insertOrUpdateSticker(ExtraBean bean) {
//        if (bean == null) {
//            return;
//        }
//        addInstalledRes(bean.getPkgName());
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.PkgName.eq(bean.getPkgName())).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            dao.delete((ExtraBean) list.get(0));
//        }
//        dao.insert(bean);
//    }

//    public synchronized void replaceStickers(List<ExtraBean> allSticker) {
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        dao.deleteAll();
//        if (allSticker == null || allSticker.size() == 0) {
//            return;
//        }
//        dao.insertOrReplaceInTx();
//        dao.insertInTx(allSticker);
//    }

//    public boolean isExistSticker(String pkgName) {
//        boolean ret = false;
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        Query query = dao.queryBuilder().where(ExtraBeanDao.Properties.PkgName.eq(pkgName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        return ret;
//    }

//    private void initStickerDB() {
//        // 只在第一次启动时初始化
//        if (SPDataManager.isInitDBDataSuccess()) {
//            return;
//        }
//        ExtraBeanDao dao = DBHelper.getInstance().getDaoSession().getExtraBeanDao();
//        if (dao.count() > 0) {
//            return;
//        }
//        //内置sticker
//        ArrayList<ExtraBean> list = new ArrayList<>();
//        int i = 0;
//        for (String pkgName : LocalPluginUtil.INSIDE_STICKER_PKGNAME) {
//            if (LocalPluginUtil.copyInsideFileToExternalPath(pkgName)) {
//                ExtraBean extraBean = ExtraBean.create(pkgName,
//                        pkgName,
//                        ExtraBean.TYPE_OUTER_NOT_NEED_BUY,
//                        i++,
//                        false,
//                        1,
//                        ExtraBean.RES_TYPE_UNINSTALLED,
//                        LocalPluginUtil.generateLocalFilePath(LocalPluginUtil.STICKER_PLUGIN_FOLDER_PATH, pkgName),
//                        false);
//                list.add(extraBean);
//            }
//        }
//        dao.insertInTx(list);
//    }

//    private void initMagazineDB() {
        // 只在第一次启动时初始化
//        if (SPDataManager.isInitDBDataSuccess()) {
//            return;
//        }
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        if (dao.count() > 0) {
//            return;
//        }
//        ArrayList<MagazineBean> list = MagazinePluginUtil.getLocalMagazineList();
//        dao.insertInTx(list);
//    }

    /**
     * 根据图片个数查询所有杂志, 升序排列
     *
     * @return
     */
//    public ArrayList<MagazineBean> getMagazineList(int num, boolean supportVideoOnly) {
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        if (supportVideoOnly) {
//            Query query = dao.queryBuilder().where(MagazineBeanDao.Properties.SrcImageNum.eq(num),
//                    MagazineBeanDao.Properties.SupportVideo.eq(supportVideoOnly))
//                    .orderAsc(MagazineBeanDao.Properties.LocalIndex)
//                    .orderDesc(MagazineBeanDao.Properties.Id).build();
//            return (ArrayList<MagazineBean>) query.list();
//        } else {
//            Query query = dao.queryBuilder().where(MagazineBeanDao.Properties.SrcImageNum.eq(num))
//                    .orderAsc(MagazineBeanDao.Properties.LocalIndex)
//                    .orderDesc(MagazineBeanDao.Properties.Id).build();
//            return (ArrayList<MagazineBean>) query.list();
//        }
//    }

//    public ArrayList<MagazineBean> getMagazineList(boolean supportVideoOnly) {
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        if (supportVideoOnly) {
//            Query query = dao.queryBuilder().where(
//                    MagazineBeanDao.Properties.SupportVideo.eq(supportVideoOnly))
//                    .orderAsc(MagazineBeanDao.Properties.LocalIndex)
//                    .orderDesc(MagazineBeanDao.Properties.Id).build();
//            return (ArrayList<MagazineBean>) query.list();
//        } else {
//            Query query = dao.queryBuilder()
//                    .orderAsc(MagazineBeanDao.Properties.LocalIndex)
//                    .orderDesc(MagazineBeanDao.Properties.Id).build();
//            return (ArrayList<MagazineBean>) query.list();
//        }
//    }

//    public synchronized void insertOrUpdateMagazine(MagazineBean bean) {
//        if (bean == null) {
//            return;
//        }
//        addInstalledRes(bean.getPackageName());
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        Query query = dao.queryBuilder().where(MagazineBeanDao.Properties.PackageName.eq(bean.getPackageName())).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            dao.delete((MagazineBean) list.get(0));
//        }
//        dao.insert(bean);
//    }

//    public synchronized void replaceMagazine(List<MagazineBean> data) {
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        dao.deleteAll();
//        if (data == null || data.size() == 0) {
//            return;
//        }
//        dao.insertOrReplaceInTx();
//        dao.insertInTx(data);
//    }

//    public boolean isExistMagazine(String pkgName) {
//        boolean ret = false;
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        Query query = dao.queryBuilder().where(MagazineBeanDao.Properties.PackageName.eq(pkgName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            ret = true;
//        }
//        return ret;
//    }

//    public synchronized void deleteMagazine(String pkgName) {
//        delInstalledRes(pkgName);
//        MagazineBeanDao dao = DBHelper.getInstance().getDaoSession().getMagazineBeanDao();
//        Query query = dao.queryBuilder().where(MagazineBeanDao.Properties.PackageName.eq(pkgName)).build();
//        List list = query.list();
//        if (list != null && !list.isEmpty()) {
//            dao.deleteInTx(list);
//        }
//    }

//    public boolean isExistNormalResFile(String key) {
//        boolean ret = false;
//        String path = RESOURCE_DIR + key;
//        File file = new File(path);
//        if (file.exists()) {
//            ret = true;
//        }
//        return ret;
//    }

//    public String getNormalResFile(String key) {
//        String path = RESOURCE_DIR + key;
//        return path;
//    }

//    public boolean isInstalled(String pkgName) {
//        if (TextUtils.isEmpty(pkgName)) {
//            return false;
//        }
//        return mInstalledRes.contains(pkgName);
//    }

//    public void addInstalledRes(String pkgName) {
//        if (TextUtils.isEmpty(pkgName)) {
//            return;
//        }
//        if (!isInstalled(pkgName)) {
//            mInstalledRes.add(pkgName);
//        }
//    }

    public void delInstalledRes(String pkgName){
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        mInstalledRes.remove(pkgName);
    }
}
