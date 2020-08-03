//package org.xm.secret.photo.album.ui;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatDialogFragment;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.*;
//import android.view.View.OnClickListener;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//;
//import com.android.absbase.helper.log.DLog;
//import com.android.absbase.utils.ToastUtils;
//import com.android.permissions.compat.PermissionManager;
//import com.photoeditor.R;
//import com.photoeditor.ad.AdProvider;
//import com.photoeditor.app.Base2Fragment;
//import com.photoeditor.app.PasswordConfig;
//import com.photoeditor.bean.*;
//import com.photoeditor.billing.BillingActivity;
//import com.photoeditor.event.ResourceOptionEvent;
//import com.photoeditor.function.collage.ui.CollageActivity;
//import com.photoeditor.function.gallery.ui.adapter.AlbumListAdapter;
//import com.photoeditor.function.gallery.ui.adapter.ListGridAdapter;
//import com.photoeditor.function.gallery.ui.listener.IGridItemClickListener;
//import com.photoeditor.function.gallery.utils.EncodeListListener;
//import com.photoeditor.function.gallery.utils.PrivateHelper;
//import com.photoeditor.function.gallery.utils.SharePreferenceUtil;
//import com.photoeditor.ui.ItemClickSupport;
//import com.photoeditor.ui.activity.ShowActivity;
//import com.photoeditor.ui.dialog.AlbumDialog;
//import com.photoeditor.ui.dialog.DialogHelper;
//import com.photoeditor.ui.flow.bean.AdItemBean;
//import com.photoeditor.utils.AsyncTask;
//import com.photoeditor.utils.FileUtil;
//import com.photoeditor.utils.MD5;
//import com.photoeditor.utils.ToastUtil;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.jetbrains.annotations.NotNull;
//
//import java.io.File;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//import static android.app.Activity.RESULT_OK;
//
///**
// * 私密相册的fragment
// */
//public class PrivateAlbumFragment extends Base2Fragment {
//
//    public static final int REQUEST_CODE_PRIVATE = 10001;
//    public static final String RESPONSE_DATA = "pick_data";
//    /*
//     * 用于存储图片的URL
//     */
//    private ArrayList<ThumbnailBean> mUris = new ArrayList<ThumbnailBean>();
//    //解码路径
////    ArrayList<String> decodePaths  = new ArrayList<>();
////    ArrayList<PrivateBean> decodeBeans  = new ArrayList<>();
////    ArrayList<ThumbnailBean> decodeThumb  = new ArrayList<>();
//
//
//    private ArrayList<Object> mDatas = new ArrayList<>();
//    private List<AdItemBean> mAdBeans = new ArrayList<>();
//
//    private GalleryActivity mActivity;
//
//    private View mContainerView;
//
//    //空的时候的界面
//    private View mEmptyGalleryView;
//
//    /**
//     * 分享相关
//     */
//    private View mPick;
////    private View mShade;
//    private View mPassWord;
//
//    private int mMode = MODE_FOLDER;
//
//    public final static int MODE_FOLDER = 2;
//
//    public final static int MODE_GALLERY = 3;
//
//    private DateBean mNowTime;
//
//    private final int COL_NUM = 3;
//    private ListGridAdapter mAdapter;
//    //图片列表
//    private ListView mListView;
//    private View mListPanel;
//
//    private LinearLayout mLlPwdContainer;
//
//    //加解密
//    private boolean isFinished = true;
//    private ArrayList<PrivateBean> encodeList = new ArrayList<>();
//
//    private View mContanerView;
//
//    private RecyclerView mRvAlbum;
//    private AlbumListAdapter mAlbumAdapter;
//    private List<ImageFolder> mAlbumData;
//    private ConcurrentHashMap<String, ImageFolder> mAlbumImageFolderMap = new ConcurrentHashMap<>();
//    //新建相册弹窗
//    private AlertDialog mAddAlbumDialog;
//    //修改相册名称弹窗
//    private AlertDialog mRenameAlbumDialog;
////    private ArrayList<ThumbnailBean> mLastSelectAlbum = new ArrayList<>();
//
//    private boolean mInitAlbumList = false;
//
//    public PrivateAlbumFragment() {
//
//    }
//
//    @SuppressLint("ValidFragment")
//    public PrivateAlbumFragment(ICheckedChangeListener listener, IOnFragmentChange dataListener) {
//        mActivityListener = listener;
//        mDataListener = dataListener;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mActivity = (GalleryActivity) getActivity();
//
//        loadBannerAd();
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        EventBus.getDefault().register(this);
//
//        if (mContanerView != null) {
//            ViewGroup parent = (ViewGroup) mContanerView.getParent();
//            if (parent != null) {
//                parent.removeView(mContanerView);
//            }
//        } else {
//            mContanerView = inflater.inflate(R.layout.fragment_secret_vault, container, false);
//        }
//
//        mLlPwdContainer = mContanerView.findViewById(R.id.id_pwd_keyboard_view_container);
////        if (mPwdHelper == null) {
////            mPwdHelper = new PasswordHelper(getMMainHandler(), new PasswordHelper.PasswordCallback() {
////                @Override
////                public void onPwdActionDown() {
////                    if (mActivity != null){
////                        //设置这个的原因是  防止按住密码滑动导致弹起事件接受不到
////                        mActivity.mViewPager.setCanScroll(false);
////                    }
////
////                }
////
////                @Override
////                public void onPwdActionUp() {
////                    if (mActivity != null){
////                        //设置这个的原因是  防止按住密码滑动导致弹起事件接受不到
////                        mActivity.mViewPager.setCanScroll(true);
////                    }
////                }
////
////                @Override
////                public void checkPwdRight() {
////                    showPasswordView(false);
////                    mActivity.showSelect(View.VISIBLE);
////                    PasswordConfig.INSTANCE.setSecretCheckPass(true);
////                    //initGalleryListView(true);
////                    initAlbumList();
////                    //getSecretVault();
////                }
////            });
////            mPwdHelper.init();
////        }
//
//        initView(mContanerView);
////        initBroadcast();
//        isCreated = true;
//        mMode = MODE_FOLDER;
//        setNeedRefresh(true);
//        if (mDataListener != null) {
//            mDataListener.onFragmentCreated(GalleryActivity.TYPE_FRAGMENT_PRIVATE_GALLERY);
//        }
//
////        if (mActivity.isPickMode()){
////            showPasswordView(false);
////            initAlbumList();
////        }else {
////            checkPassword();
////        }
//
//        if (PermissionManager.INSTANCE.getProxy().hasPermissions(App.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            if (!mInitAlbumList){
//                initAlbumList();
//            }
//        }
//
//        return mContanerView;
//    }
//
//    private void initView(View contanerView) {
//        mContainerView = contanerView;
////        mShade = contanerView.findViewById(R.id.shade);
//        mPick = contanerView.findViewById(R.id.iv_pick);
//        mPick.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void noDoubleClick(View v) {
//                PickGalleryActivity.startGalleryChangeCollageImages(getActivity(),REQUEST_CODE_PRIVATE);
//            }
//        });
//        mPick.setVisibility(View.GONE);
//
////        if(!mPwdHelper.isPass()){
////            showPasswordView(true);
////        }else{
//////            initGalleryListView(true);
////            initAlbumList();
////        }
//    }
//
//    /**
//     * 初始化相册列表
//     */
//    private void initAlbumList(){
//        if (mActivity == null || mContainerView == null){
//            return;
//        }
//        mActivity.showTopPanel();
//        View listPanel = null;
//        ViewStub vs = (ViewStub) mContainerView.findViewById(R.id.album_list_viewstub_id);
//        if (vs == null) {
//            listPanel = mContainerView.findViewById(R.id.album_list_layout_id);
//        } else {
//            listPanel = vs.inflate();
//        }
//        mRvAlbum = (RecyclerView) listPanel.findViewById(R.id.id_rv_album_list);
//        mRvAlbum.setLayoutManager(new GridLayoutManager(mActivity, 2));
//
//        AlbumListAdapter.Callback albumAdapterCallback = new AlbumListAdapter.Callback() {
//            @Override
//            public void onMoreClick(int position) {
//                mMorePosition = position;
//                if (mAlbumBottomDialog == null){
//                    mAlbumBottomDialog = new AlbumDialog(mAlbumBottomDialogCallback);
//                }
//
//                mAlbumBottomDialog.show(getFragmentManager(), PrivateAlbumFragment.class.getSimpleName());
//            }
//        };
//
//        mAlbumAdapter = new AlbumListAdapter(albumAdapterCallback);
//        mRvAlbum.setAdapter(mAlbumAdapter);
//
//        //监听
//        ItemClickSupport itemClickSupport = new ItemClickSupport(mRvAlbum, mAlbumAdapter);
//        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onPickItemClick(RecyclerView recyclerView, View view, final int position, long id) {
//                mAlbumPosition = position;
//                if (mAlbumData == null){
//                    return;
//                }
//                if (position == mAlbumData.size() - 1){
//                    //click last one -> add album
//                    if(mAlbumData.size() >= PasswordConfig.INSTANCE.getFolderSize2showSubs() && PasswordConfig.INSTANCE.getPurchaseSubSize() <= 0){
//                        BillingActivity.Companion.startActivity(getContext());
//                        return;
//                    }
//
//                    DialogHelper.Builder builder = new DialogHelper.Builder()
//                            .setTitleContent(App.context.getString(R.string.add_album))
//                            .isShowMessage(false)
//                            .isShowEditText(true)
//                            .setOkContent(App.context.getString(R.string.save))
//                            .setCancelContent(App.context.getString(R.string.cancel));
//
//                    mAddAlbumDialog = DialogHelper.INSTANCE.createEditTextDialog(mActivity, builder, new DialogHelper.EditDialogCallback() {
//                        @Override
//                        public void onOkClick(@NotNull AlertDialog dialog, @NotNull String etContent) {
//                            if (TextUtils.isEmpty(etContent)){
//                                ToastUtil.showToast(App.context.getString(R.string.tips_please_input_album_name));
//                                return;
//                            }
//
//                            String album = etContent;
//                            ImageFolder imageFolder = new ImageFolder();
//                            imageFolder.setName(album);
//                            imageFolder.setDir(PrivateHelper.Companion.getPATH_ALBUM() + File.separator + album);
//                            imageFolder.setData(new ArrayList<ThumbnailBean>());
//                            imageFolder.setCount(0);
//
//                            //创建相册
//                            createAlbum(etContent);
//
//                            //刷新数据
//                            if (mAlbumAdapter == null){
//                                return;
//                            }
//                            mAlbumAdapter.addData(imageFolder, mAlbumData.size() - 1);
//                            String albumPath = PrivateHelper.Companion.getPATH_ALBUM() + File.separator + album;
//                            mAlbumImageFolderMap.put(albumPath, imageFolder);
//
//                            //滚动到底部
//                            postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mRvAlbum.smoothScrollToPosition(mAlbumData.size() - 1);
//                                }
//                            }, 500);
//
//                            mAddAlbumDialog.dismiss();
//
//                        }
//
//                        @Override
//                        public void onCancelClick(@NotNull AlertDialog dialog) {
//
//                        }
//                    });
//                    mAddAlbumDialog.show();
//
//                }else {
//                    //点击相册
//                    if (mActivity == null){
//                        return;
//                    }
//
//                    mActivity.showBackBtn(true);
//
//                    ImageFolder imageFolder = mAlbumData.get(mAlbumPosition);
//                    if (imageFolder == null){
//                        return;
//                    }
//
//                    mActivity.setTitle(imageFolder.getName());
//                    initGalleryListView(true);
//                    mRvAlbum.setVisibility(View.GONE);
//                    handleResult(imageFolder.getData());
//                }
//            }
//        });
//
//        listPanel.setVisibility(View.VISIBLE);
//
//        getAlbumInfoListTask().executeOnExecutor(AsyncTask.DATABASE_THREAD_EXECUTOR);
//    }
//
//
//    /**
//     * 初始化图片列表，相册内容
//     */
//    private void initGalleryListView(boolean visible) {
//        if (visible) {
//            if (mListView == null) {
//                ViewStub vs = (ViewStub) mContainerView.findViewById(R.id.photo_list_viewstub_id);
//                if (vs == null) {
//                    mListPanel = mContainerView.findViewById(R.id.photo_list_layout_id);
//                } else {
//                    mListPanel = vs.inflate();
//                }
//                mListView = (ListView) mListPanel.findViewById(R.id.list);
//
//            }
//            mListPanel.setVisibility(View.VISIBLE);
//            if(mPick != null){
//                mPick.setVisibility(View.VISIBLE);
//            }
//
//        } else  {
//            if (mListPanel != null){
//                mListPanel.setVisibility(View.GONE);
//            }
//            if(mPick != null){
//                mPick.setVisibility(View.GONE);
//            }
//        }
//
//    }
//
//    /**
//     * 展示相册列表
//     */
//    private void showAlbumList(){
//        mActivity.setTitle(App.context.getString(R.string.app_name));
//        mActivity.showBackBtn(false);
//
//        showEmptyGalleryView(false);
//
//        if (mListPanel != null){
//            mListPanel.setVisibility(View.GONE);
//        }
//
//        if (mPick != null){
//            mPick.setVisibility(View.GONE);
//        }
//
//        if (mRvAlbum != null){
//            mRvAlbum.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /***
//     * 展示空数据时的view
//     * @param show
//     */
//    private void showEmptyGalleryView(boolean show) {
//        if (mEmptyGalleryView == null) {
//            ViewStub vs = (ViewStub) mContainerView.findViewById(R.id.empty_gallery_viewstub_id);
//            if (vs != null) {
//                mEmptyGalleryView = vs.inflate();
//            } else {
//                mEmptyGalleryView = mContainerView.findViewById(R.id.empty_gallery_layout_id);
//            }
//            if (mEmptyGalleryView != null) {
//                TextView title = (TextView) mEmptyGalleryView.findViewById(R.id.empty_title);
//                TextView content = (TextView) mEmptyGalleryView.findViewById(R.id.empty_content);
//                View bt = mEmptyGalleryView.findViewById(R.id.empty_bt);
//                title.setText(R.string.gallery_empty_title);
//                content.setText(R.string.album_select_secret_vault_gallery_empty_content);
//                bt.setVisibility(View.VISIBLE);
//                bt.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
////                        Intent intent = new Intent(mActivity, MainActivity.class);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                                | Intent.FLAG_ACTIVITY_SINGLE_TOP
////                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        intent.putExtra(IntentConstant.Extras.EXTRA_PAGE, MainActivity.PAGE_CAMERA);
////                        mActivity.startActivity(intent);
//                    }
//                });
//            }
//        }
//        if (mEmptyGalleryView != null) {
//            if (show) {
//                mEmptyGalleryView.setVisibility(View.VISIBLE);
//            } else {
//                mEmptyGalleryView.setVisibility(View.GONE);
//            }
//        }
//    }
//
//
//    /***
//     * 展示图片列表内容
//     * @param
//     * @return
//     */
//    private void handleResult(ArrayList<ThumbnailBean> result){
//        if (result == null || result.size() == 0 || mActivity == null) {
//            showEmptyGalleryView(true);
//            return;
//        }
//        showEmptyGalleryView(false);
//
//        mUris = result;
//
//        for (ThumbnailBean bean : result){
//            bean.setSourceType(ThumbnailBean.DECODE);
//        }
//        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>(5, 1f, false);
//        if (mNowTime == null) {
//            mNowTime = new DateBean();
//        }
//
//        mDatas = mActivity.uniformData(result, map, mNowTime, COL_NUM, mAdBeans);
//        if (mAdapter == null) {
//            mAdapter = new ListGridAdapter(mDatas, map, COL_NUM, mActivity, mNowTime.getDateTime());
//            mAdapter.setType(GalleryActivity.TYPE_FRAGMENT_PRIVATE_GALLERY);
//            mAdapter.setOnCheckedChangeListener(mActivityListener);
//        } else {
//            mAdapter.setData(mDatas, map, mNowTime.getDateTime());
//        }
//        mAdapter.setGridItemClickListener(new IGridItemClickListener() {
//            @Override
//            public void onPickItemClick(@NotNull ThumbnailBean bean, int position) {
//                if (mActivity.isPickMode()){
//                    Intent data = mActivity.getIntent();
//                    data.setData(bean.getUri());
//                    data.putExtra(CollageActivity.EXTRA_DECODE_DATA, bean);
//                    mActivity.setResult(Activity.RESULT_OK, data);
//                    mActivity.setNeedExitPageAd(false);
//                    mActivity.finish();
//                }else {
//                    ImageFolder imageFolder = mAlbumData.get(mAlbumPosition);
//                    String dir = imageFolder.getDir();
//                    ArrayList<ThumbnailBean> datas = imageFolder.getData();
//                    int index = datas.indexOf(bean);
//                    ShowActivity.Companion.startActivity(mActivity, dir, datas, index );
//                }
//
//            }
//        });
////        //长按监听
////        mAdapter.setDecodeBeanLongClickListener(new IDecodeBeanLongClickListener() {
////            @Override
////            public void onLongClick(final GalleryCellItem item, final ThumbnailBean bean, final int position) {
////                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
////                        .setMessage(R.string.set_album_cover)
////                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                            }
////                        })
////                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                String albumName = mAlbumData.get(mAlbumPosition).getName();
////                                String albumPath = PrivateHelper.Companion.getPATH_ALBUM() + File.separator + albumName;
////                                String coverPath = bean.getTempPath();
////                                SharePreferenceUtil.saveString(App.context, albumPath, coverPath);
////
////                                //刷新数据
////                                ImageFolder imageFolder = mAlbumImageFolderMap.get(albumPath);
////                                int selectedIndex = imageFolder.getData().indexOf(bean);
////                                imageFolder.setFirstImageBean(imageFolder.getData().get(selectedIndex));
////                                mAlbumImageFolderMap.put(albumPath, imageFolder);
////                                mAlbumData.set(mAlbumPosition, imageFolder);
////                                mAlbumAdapter.notifyDataSetChanged();
////                            }
////                        });
////                builder.show();
////            }
////        });
//        if (mListView != null) {
//            mListView.setAdapter(mAdapter);
//        }
//
//    }
//
//    /**
//     * 获取相册列表数据的异步任务
//     *
//     * @return
//     */
//    private AsyncTask<String, Integer, ArrayList<ImageFolder>> getAlbumInfoListTask() {
//        AsyncTask<String, Integer, ArrayList<ImageFolder>> albumTask = new AsyncTask<String, Integer, ArrayList<ImageFolder>>() {
//
//            @Override
//            protected ArrayList<ImageFolder> doInBackground(String... params) {
//
//                //遍历Album
//                File rootDir = new File( PrivateHelper.Companion.getPATH_ALBUM());
//                if (rootDir.exists() && rootDir.isDirectory()){
//                    String[] list = rootDir.list();
//                    if (list != null && list.length > 0) {
//                        mAlbumImageFolderMap.clear();
//                        File[] fileList = rootDir.listFiles();
//                        if (fileList == null){
//                            return null;
//                        }
//                        //遍历相册目录
//                        for (File albumDir: fileList){
//                            if (!albumDir.isDirectory()){
//                                continue;
//                            }
//
//                            String albumDirPath = albumDir.getAbsolutePath();
//                            ArrayList<ThumbnailBean> thumbnailBeans = new ArrayList<>();
//                            File[] files = albumDir.listFiles();
//                            ImageFolder imageFolder = new ImageFolder();
//
//                            if (files == null){
//                                continue;
//                            }
//
//                            //遍历目录内的链接文件
//                            for (File filepath: files) {
//                                String name = filepath.getName();
//                                File file = new File(PrivateHelper.Companion.getPATH_ENCODE_ORIGINAL(), name);
//
//                                if (!file.exists()){
//                                    continue;
//                                }
//
//                                Object obj = SharePreferenceUtil.getObjectFromShare(mActivity, name);
//                                if(obj instanceof LocalThumbnailBean){
//                                    LocalThumbnailBean thumb = (LocalThumbnailBean) obj;
//                                    ThumbnailBean thumbnailBean =  PrivateHelper.Companion.changeLocalThumbnailBean2ThumbnailBean(thumb);
//                                    if (!thumbnailBean.isInvalid()) {
//                                        thumbnailBeans.add(thumbnailBean);
//                                    }
//                                }
//
//                            }
//
//                            imageFolder.setDir(albumDirPath);
//                            imageFolder.setName(albumDir.getName());
//
//                            ArrayList<ThumbnailBean> sortThumbnailBeans  = new ArrayList<>(thumbnailBeans);
//                            Collections.sort(sortThumbnailBeans,new Comparator<ThumbnailBean>() {
//                                @Override
//                                public int compare(ThumbnailBean arg0, ThumbnailBean arg1) {
//                                    return Long.compare(arg1.getDate(), arg0.getDate());
//                                }
//                            });
//
//                            thumbnailBeans = sortThumbnailBeans;
//
//                            imageFolder.setData(thumbnailBeans);
//                            imageFolder.setDir(albumDirPath);
//                            imageFolder.setName(albumDir.getName());
//                            imageFolder.setCount(imageFolder.getData().size());
//                            mAlbumImageFolderMap.put(albumDirPath, imageFolder);
//                        }
//                    }
//                }
//
//                return new ArrayList<>(mAlbumImageFolderMap.values());
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<ImageFolder> result) {
//                if (mAlbumAdapter == null){
//                    return;
//                }
//
//                showEmptyGalleryView(false);
//
//                if (result == null){
//                    addEmptyAlbumData();
//                    return;
//                }
//
//                if (result.size() == 0){
//                    addEmptyAlbumData();
//                    return;
//                }
//
//                mAlbumData = result;
//                mAlbumAdapter.setData(mAlbumData);
//                mAlbumAdapter.addDataEnd(new ImageFolder());
//
//                mInitAlbumList = true;
//            }
//        };
//        return albumTask;
//    }
//
//    /***
//     * 删除相册异步任务
//     * @return
//     */
//    public AsyncTask<ImageFolder, Void, Boolean> getDeleteAlbumTask() {
//        AsyncTask<ImageFolder, Void, Boolean> albumTask = new AsyncTask<ImageFolder, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(ImageFolder... imageFolders) {
//                if (imageFolders == null || imageFolders.length == 0){
//                    return null;
//                }
//
//                ImageFolder imageFolder = imageFolders[0];
//                if (imageFolder == null){
//                    return null;
//                }
//
//                List<ThumbnailBean> thumbnailBeanList = imageFolder.getData();
//                if (thumbnailBeanList != null){
//                    if (thumbnailBeanList.size() > 0){
//                        //有内容，遍历删除加密文件，
//                        for (ThumbnailBean bean: thumbnailBeanList){
//                            //
//                            String fileNameMd5 = MD5.getMD5Str(bean.getPath());
//                            FileUtil.deleteFile(PrivateHelper.Companion.getPATH_ENCODE_ORIGINAL() + File.separator + fileNameMd5);
//                        }
//                    }
//
//                    //删除目录
//                    FileUtil.deleteFolder(imageFolder.getDir());
//                    return true;
//                }
//
//                return false;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean isSuccess) {
//                if (isSuccess){
//                    //已删除，刷新数据
//                    ToastUtils.INSTANCE.show(getString(R.string.delete_finish));
//
//                    if (mAlbumData == null || mAlbumAdapter == null){
//                        return;
//                    }
//
//                    mAlbumData.remove(mMorePosition);
//                    mAlbumAdapter.notifyDataSetChanged();
//
//                }else {
//                    //失败，不做处理
//                }
//                //弹窗消失
//                if (mAlbumBottomDialog != null){
//                    mAlbumBottomDialog.dismiss();
//                }
//            }
//        };
//
//        return albumTask;
//    }
//
////    private void showPasswordView(boolean visible) {
////        if (visible){
////            mActivity.hideTopPanel();
////        }else {
////            mActivity.showTopPanel();
////        }
////        if(visible){
////            if (mPassWord == null) {
////                mPassWord = mPwdHelper.getPasswordView();
////                mLlPwdContainer.addView(mPassWord);
////            }
////            mPassWord.setVisibility(View.VISIBLE);
////        }else if (mPassWord != null){
////            mPassWord.setVisibility(View.GONE);
////        }
////        if(mPick != null){
////            mPick.setVisibility(View.GONE);
////        }
////    }
////
////    /**
////     * 检查是否登陆
////     * @param form 检查出发事件的来源 true 代表来源监听home键
////     */
////    public void need2check(boolean form){
////        mPwdHelper.need2check();
////        showPasswordView(true);
////        if(mActivity != null){
////            if(form){
////                //如果当前页面不是Secret Vault 就不需要隐藏
////                if(mActivity.mTitle.getText().equals(getString(R.string.album_select_secret_vault))){
////                    mActivity.showSelect(View.GONE);
////                }
////            }else{
////                mActivity.showSelect(View.GONE);
////            }
////        }
////    }
//
//
//    @Override
//    public void onMoveClick(View v) {
//        mAdapter.setCheckStatus(false);
//    }
//
//    @Override
//    public boolean onBackClick(View v) {
//
//        if (mEmptyGalleryView != null && mEmptyGalleryView.getVisibility() == View.VISIBLE){
//            showAlbumList();
//            return true;
//        }
//
//        if (mListPanel != null && mListPanel.getVisibility() == View.VISIBLE){
//            showAlbumList();
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void onCancelClick(View v) {
//            if (mAdapter != null) {
//                if (mAdapter.isCheckStatus()) {
//                    mActivity.doCancel(mAdapter);
//                } else {
//                    mActivity.finish();
//                }
//            } else {
//                mActivity.finish();
//            }
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return false;
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (isAdded()) {
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
////                if (mAdapter != null && mAdapter.isCheckStatus()) {
////                    mActivity.doCancel(mAdapter);
////                    return true;
////                }
//
//                if (mRvAlbum != null && mRvAlbum.getVisibility() == View.VISIBLE){
//                    return false;
//                }
//
//                if (mEmptyGalleryView != null && mEmptyGalleryView.getVisibility() == View.VISIBLE){
//                    showAlbumList();
//                    return true;
//                }
//
//                if (mListPanel != null && mListPanel.getVisibility() == View.VISIBLE){
//                    showAlbumList();
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void onNewIntent(Intent intent) {
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == GalleryActivity.REQUEST_CODE_PREVIEW) {
//
//            if (data == null){
//                return;
//            }
//            int refresh = data.getIntExtra(/*PicturePreviewActivity.NEED_RESFREH*/"need_refresh", /*PicturePreviewActivity.REFRESH_NONE*/0);
//            if (refresh == /*PicturePreviewActivity.REFRESH_CURRENT*/1) {
//                setNeedRefresh(true);
//                refreshData();
//            } else if (refresh == /*PicturePreviewActivity.REFRESH_ALL*/5) {
//                setNeedRefresh(true);
//                refreshData();
//                if (mDataListener != null) {
//                    mDataListener.onDataChange(GalleryActivity.TYPE_FRAGMENT_PRIVATE_GALLERY);
//                }
//            } else if (refresh == /*PicturePreviewActivity.REFRESH_CURRENT_AND_OTHER_GALLERY*/4) {
//                setNeedRefresh(true);
//                refreshData();
//                if (mDataListener != null) {
//                    mDataListener.onDataChange(GalleryActivity.TYPE_FRAGMENT_PRIVATE_GALLERY);
//                }
//            }
//        }else if (requestCode == REQUEST_CODE_PRIVATE) {
//            if (resultCode==RESULT_OK && data != null) {
//                Bundle extras = data.getExtras();
//                ArrayList<ThumbnailBean> beanList = (ArrayList<ThumbnailBean>) extras.get(RESPONSE_DATA);
//                if (beanList == null){
//                    return;
//                }
//                for (int index = 0 ; index < beanList.size();index++){
//                    ThumbnailBean bean = beanList.get(index);
//                    bean.setSourceType(ThumbnailBean.DECODE);
//                    String nameMd5 = MD5.getMD5Str(bean.getPath());
//                    if (nameMd5 != null){
//                        bean.setTempPath(new File(PrivateHelper.Companion.getPATH_DECODE_TEMP(), nameMd5 ).getPath());
//                    }
//                }
//                //加密
//                if (mAlbumPosition != mAlbumData.size() - 1){
//                    String album = mAlbumData.get(mAlbumPosition).getName();
//                    String albumPath = PrivateHelper.Companion.getPATH_ALBUM() + File.separator + album;
//                    encodeData(albumPath, beanList);
//                }
//            }
//        }
//    }
//
//    /***
//     * 加密
//     * @param albumPath
//     * @param beans
//     */
//    private void encodeData(String albumPath, final ArrayList<ThumbnailBean> beans) {
//        encodeList.clear();
//        if(beans!=null&&beans.size()>0){
//            ArrayList<LocalThumbnailBean> localThumbnailBeans = PrivateHelper.Companion.changeThumbnailList2LocalThumbnaiList(beans);
//            for (LocalThumbnailBean bean : localThumbnailBeans){
//                if(SharePreferenceUtil.setObjectToShare(mActivity, MD5.getMD5Str(bean.getPath()),bean)){
//                    encodeList.add(new PrivateBean(new File(bean.getPath()).length(),bean.getDate(),bean.getPath(),albumPath));
//                }
//            }
//            PrivateHelper.Companion.encodeList(encodeList, new EncodeListListener() {
//                @Override
//                public void onStart() {
//                    mActivity.showVideoSavingAnim();
//                }
//
//                @Override
//                public void onSuccess(@NotNull final List<PrivateBean> success, @NotNull List<PrivateBean> errors) {
//                    mActivity.mSavingVideoAnimationView.endProgressSmooth(new Runnable() {
//                        @Override
//                        public void run() {
//                            mActivity.hideVideoSavingAnim();
//
//                            ImageFolder imageFolder = null;
//
//                            for (PrivateBean privateBean: success){
//                                String albumPath = privateBean.getAlbumPath();
//                                Object obj = SharePreferenceUtil.getObjectFromShare(mActivity, MD5.getMD5Str(privateBean.getOriginalPath()));
//                                if (!(obj instanceof LocalThumbnailBean)){
//                                    continue;
//                                }
//
//                                LocalThumbnailBean localThumbnailBean = (LocalThumbnailBean)obj;
//                                ThumbnailBean thumbnailBean =  PrivateHelper.Companion.changeLocalThumbnailBean2ThumbnailBean(localThumbnailBean);
//                                imageFolder = mAlbumImageFolderMap.get(albumPath);
//
//                                if (imageFolder == null){
//                                    continue;
//                                }
//
//                                ArrayList<ThumbnailBean> thumbnailBeans = imageFolder.getData();
//                                if (thumbnailBeans == null){
//                                    continue;
//                                }
//
//                                thumbnailBeans.add(thumbnailBean);
//                                //sort
//
//                                ArrayList<ThumbnailBean> sortThumbnailBeans  = new ArrayList<>(thumbnailBeans);
//                                Collections.sort(sortThumbnailBeans,new Comparator<ThumbnailBean>() {
//                                    @Override
//                                    public int compare(ThumbnailBean arg0, ThumbnailBean arg1) {
//                                        return Long.compare(arg1.getDate(), arg0.getDate());
//                                    }
//                                });
//
//                                thumbnailBeans = sortThumbnailBeans;
//
//
//
////                                imageFolder.getData().add(thumbnailBean);
////                                imageFolder.setCount(imageFolder.getData().size());
//
//                                imageFolder.setData(thumbnailBeans);
//                                handleResult(imageFolder.getData());
//
//
//                                int index = mAlbumData.indexOf(imageFolder);
//                                if (!mAlbumData.contains(imageFolder)){
//                                    mAlbumData.set(index, imageFolder);
//                                }
//                                mAlbumAdapter.notifyDataSetChanged();
//
//                            }
//
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailed(@NotNull List<PrivateBean> success,@NotNull final List<PrivateBean> errors) {
//                    mActivity.mSavingVideoAnimationView.endProgressSmooth(new Runnable() {
//                        @Override
//                        public void run() {
//                            mActivity.hideVideoSavingAnim();
////                            getSecretVault();
//                            ToastUtil.showToast(getString(R.string.album_select_secret_vault_encryption_failed));
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    @Override
//    public ListGridAdapter getAdapter() {
//        return mAdapter;
//    }
//
//    @Override
//    public void doOnStart() {
//        if (isAdded()) {
//            refreshData();
//        }
//
//        if (!mInitAlbumList){
//            initAlbumList();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        //需要刷新数据
//        if(PasswordConfig.INSTANCE.getSecretVaultNeedRefresh()){
////            getSecretVault();
//        }
//    }
//
//    public void reset(){
////        decodePaths.clear();
//        mUris.clear();
////        decodeBeans.clear();
//        mDatas.clear();
////        decodeThumb.clear();
//    }
//
////    /**
////     * 获取数据
////     */
////    public void getSecretVault(){
////        PasswordConfig.INSTANCE.setSecretVaultNeedRefresh(false);
////        reset();
////        File file = new File( PrivateHelper.Companion.getPATH_ENCODE_ORIGINAL());
////        if(file.exists() && decodeThumb.size() < file.listFiles().length){
////            for (File item : file.listFiles()){
////                PrivateBean privateBean = new PrivateBean();
////                if(privateBean.resolveHead(item.getPath())){
////                    if(!decodeBeans.contains(item.getPath())){
////                        decodePaths.add(item.getPath());
////                        decodeBeans.add(privateBean);
////                    }
////                    //从本地获取ThumbnailBean数据
////                    Object obj = SharePreferenceUtil.getObjectFromShare(mActivity, MD5.getMD5Str(privateBean.getOriginalPath()));
////                    if(obj instanceof LocalThumbnailBean){
////                        LocalThumbnailBean thumb = (LocalThumbnailBean) obj;
////                        ThumbnailBean thumbnailBean =  PrivateHelper.Companion.changeLocalThumbnailBean2ThumbnailBean(thumb);
////                        if(!decodeThumb.contains(thumbnailBean)){
////                            decodeThumb.add(thumbnailBean);
////                        }
////                    }
////                }
////            }
////        }
////        //展示
////        handleResult(decodeThumb);
////    }
//
//    @Override
//    public void setNeedRefresh(boolean flag) {
//        isNeedRefresh = flag;
//    }
//
//    @Override
//    public int getImageNum() {
//        return mUris.size();
//    }
//
//    private int mAlbumPosition = 0;
//
//
//    /***
//     * 底部弹窗
//     */
//    private AlbumDialog mAlbumBottomDialog;
//    private AlbumDialog.Callback mAlbumBottomDialogCallback = new AlbumDialog.Callback() {
//        @Override
//        public void onDeleteClick(@NotNull AppCompatDialogFragment dialog) {
//            if (mAlbumBottomDialog != null) {
//                mAlbumBottomDialog.dismissAllowingStateLoss();
//            }
//            //删除提示弹窗
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
//                    .setMessage(R.string.tips_dialog_delete_resource)
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            if (mAlbumBottomDialog != null){
//                                mAlbumBottomDialog.dismiss();
//                            }
//                        }
//                    })
//                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //启动一个Task删除， 遍历删除
//                            if (mAlbumData == null){
//                                return;
//                            }
//
//                            if (mMorePosition < 0 || mMorePosition >= mAlbumData.size()){
//                                return;
//                            }
//                            ImageFolder imageFolder = mAlbumData.get(mMorePosition);
//                            getDeleteAlbumTask().executeOnExecutor(AsyncTask.DATABASE_THREAD_EXECUTOR, imageFolder);
//                            dialog.dismiss();
//                        }
//                    });
//            builder.show();
//
//        }
//
//        @Override
//        public void onRenameClick(@NotNull AppCompatDialogFragment dialog) {
//            if (mAlbumBottomDialog != null) {
//                mAlbumBottomDialog.dismissAllowingStateLoss();
//            }
//
//            ImageFolder imageFolder = mAlbumData.get(mMorePosition);
//            if (imageFolder == null){
//                return;
//            }
//
//            String albumName = imageFolder.getName();
//
//            DialogHelper.Builder builder = new DialogHelper.Builder()
//                    .setTitleContent(App.context.getString(R.string.rename_album))
//                    .isShowMessage(false)
//                    .isShowEditText(true)
//                    .setOkContent(App.context.getString(R.string.save))
//                    .setCancelContent(App.context.getString(R.string.cancel))
//                    .setEditTextContent(albumName);
//
//            mRenameAlbumDialog = DialogHelper.INSTANCE.createEditTextDialog(mActivity, builder, new DialogHelper.EditDialogCallback() {
//                @Override
//                public void onOkClick(@NotNull AlertDialog dialog, @NotNull String etContent) {
//                    if (TextUtils.isEmpty(etContent)){
//                        ToastUtil.showToast(App.context.getString(R.string.tips_please_input_album_name));
//                        return;
//                    }
//
//                    String album = etContent;
//                    String albumPath = PrivateHelper.Companion.getPATH_ALBUM() + File.separator + album;
//
//                    //判断相册是是否重复
//                    if (FileUtil.isExistsFile(albumPath)){
//                        //已存在，
//                        ToastUtils.INSTANCE.show(App.context.getString(R.string.already_exist_album));
//                        return;
//                    }
//
//                    if (mAlbumData == null){
//                        return;
//                    }
//
//                    if (mMorePosition >= mAlbumData.size()){
//                        return;
//                    }
//
//                    ImageFolder imageFolder = mAlbumData.get(mMorePosition);
//                    if (imageFolder == null){
//                        return;
//                    }
//
//                    String albumDir = imageFolder.getDir();
//                    File albumDirFile = new File(albumDir);
//                    File albumDesDirFile = new File(albumPath);
//                    boolean renameOk = false;
//                    try {
//                        renameOk = albumDirFile.renameTo(albumDesDirFile);
//                    } catch (Exception e) {
//                        DLog.printStackTrace(e);
//                    }
//                    if (!renameOk) {
//                        ToastUtils.INSTANCE.show(App.context.getString(R.string.album_rename_failed));
//                        dialog.dismiss();
//                        if (mAlbumBottomDialog == null){
//                            mAlbumBottomDialog.dismiss();
//                        }
//                        return;
//                    }
//
//                    dialog.dismiss();
//                    if (mAlbumBottomDialog == null){
//                        mAlbumBottomDialog.dismiss();
//                    }
//
//                    mAlbumImageFolderMap.remove(albumDir);
//                    imageFolder.setDir(albumPath);
//                    imageFolder.setName(albumDesDirFile.getName());
//                    mAlbumImageFolderMap.put(albumPath, imageFolder);
//
//                    if (mAlbumAdapter != null){
//                        mAlbumAdapter.notifyDataSetChanged();
//                    }
//
//                }
//
//                @Override
//                public void onCancelClick(@NotNull AlertDialog dialog) {
//                    if (mAlbumBottomDialog == null){
//                        mAlbumBottomDialog.dismiss();
//                    }
//                }
//            });
//            mRenameAlbumDialog.show();
//        }
//    };
//
//    private int mMorePosition = 0;
//
//
//    /***
//     * 创建相册
//     * @param album 相册名
//     */
//    private void createAlbum(String album){
//        File albumFile = new File(PrivateHelper.Companion.getPATH_ALBUM() + File.separator + album);
//        if (!albumFile.exists()){
//            albumFile.mkdirs();
//        }
//    }
//
//    public void doCheckAll(boolean checked) {
//
//            if (mAdapter != null) {
//                mAdapter.checkAll(checked);
//            }
//    }
//
//    public void doCancel() {
//
//            if (mAdapter != null) {
//                mActivity.doCancel(mAdapter);
//            }
//    }
//
//    public int getMode() {
//        return mMode;
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser){
////            checkPassword();
//        }
//    }
//
////    private void checkPassword() {
////        if(mActivity!=null){
////            if(PasswordConfig.INSTANCE.getFirstOpenApp()|| PasswordConfig.INSTANCE.getSecretVaultPassword().isEmpty()){
////                PasswordConfig.INSTANCE.setFirstOpenApp(false);
////                //settingPassword();
////                showPasswordView(true);
////                mPwdHelper.settingPassword();
////                if(mActivity!=null){
////                    mActivity.showSelect(View.GONE);
////                }
////            }else {
////                if(!mPwdHelper.isPass()){
////                    need2check(false);
////                }else{
////                    mActivity.showSelect(View.VISIBLE);
////                }
////            }
////        }
////    }
//
////    private void initBroadcast() {
////        IntentFilter mFilter = new IntentFilter();
////        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//home键
////        mFilter.addAction(Intent.ACTION_SCREEN_ON);  //开屏
////        mFilter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏
////        mFilter.addAction(Intent.ACTION_USER_PRESENT);//解锁
////        if(mActivity!=null){
////            mActivity.registerReceiver(mHomeBroadcastReceiver,mFilter);
////        }
////    }
////    private BroadcastReceiver mHomeBroadcastReceiver = new BroadcastReceiver() {
////        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
////        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
////        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
////
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            String action = intent.getAction();
////            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
////                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
////                if (reason != null) {
////                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
////                        // 短按home键
////                        need2check(true);
////                        mActivity.checkPrivateAlbum();
////                        mActivity.secretVaultNeedHide = false;
////                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
////                        // 长按home键
////                        need2check(true);
////                        mActivity.checkPrivateAlbum();
////                        mActivity.secretVaultNeedHide = false;
////                    }
////                }
////            }
////            if (action.equals(Intent.ACTION_SCREEN_ON)) {
////                need2check(true);
////                mActivity.checkPrivateAlbum();
////                mActivity.secretVaultNeedHide = false;
////            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
////                need2check(true);
////                mActivity.checkPrivateAlbum();
////                mActivity.secretVaultNeedHide = false;
////            } else {
////                // 解锁
////            }
////        }
////    };
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//
//        destroyBannerAd();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReceiveRestoreResourceEvent(ResourceOptionEvent event){
//        if (event == null){
//            return;
//        }
//
//        if (event.getType() == ResourceOptionEvent.TYPE_RESTORE
//                || event.getType() == ResourceOptionEvent.TYPE_DELETE){
//            //刷新相册列表
//            if (mAlbumData == null){
//                return;
//            }
//
//            if (mAlbumPosition >= mAlbumData.size()){
//                return;
//            }
//
//            ImageFolder imageFolder = mAlbumData.get(mAlbumPosition);
//            if (imageFolder == null){
//                return;
//            }
//
////            //删除或恢复后处理封面逻辑
////            String albumPath = imageFolder.getDir();
////            String coverPath = SharePreferenceUtil.getString(App.context, albumPath, "");
////            ThumbnailBean thumbnailBean = imageFolder.getData().get(event.getPosition());
////            if (thumbnailBean != null){
////                if (coverPath.equals(thumbnailBean.getTempPath())){
////                    //删除的图片是封面,重置
////                    SharePreferenceUtil.saveString(App.context, albumPath, "");
////                }
////            }
//
//            //删除链接文件
//            ArrayList<ThumbnailBean> thumbnailBeanList = imageFolder.getData();
//            if (thumbnailBeanList == null){
//                return;
//            }
//
//            int position = event.getPosition();
//            if (position < 0 || position >= thumbnailBeanList.size()){
//                return;
//            }
//
//            ThumbnailBean thumbnailBean = thumbnailBeanList.get(position);
//            if (thumbnailBean == null){
//                return;
//            }
//
//            String nameMd5 = MD5.getMD5Str(thumbnailBean.getPath());
//            if (nameMd5 == null){
//                return;
//            }
//
//            File linkFile = new File(imageFolder.getDir(), nameMd5);
//            if (linkFile.exists()){
//                if (!linkFile.delete()){
//                    return;
//                }
//            }
//
//            thumbnailBeanList.remove(position);
//            imageFolder.setCount(thumbnailBeanList.size());
//
//            int index = mAlbumData.indexOf(imageFolder);
//            if (index == -1){
//                return;
//            }
//            mAlbumData.set(index, imageFolder);
//
//            //刷新图片列表
//            handleResult(thumbnailBeanList);
//
//            mAlbumAdapter.notifyDataSetChanged();
//
//        }else if (event.getType() == ResourceOptionEvent.TYPE_EDIT){
//            //
//            getAlbumInfoListTask().executeOnExecutor(AsyncTask.DATABASE_THREAD_EXECUTOR);
//            showAlbumList();
//        }
//
//
//    }
//
//    private void addEmptyAlbumData() {
//        mAlbumData = new ArrayList<>();
//        mAlbumData.add(new ImageFolder());
//        mAlbumAdapter.setData(mAlbumData);
//    }
//
//    private AdProvider mAdProvider;
//
//    private void loadBannerAd() {
////        if (mAdProvider != null && mAdProvider.hasAd()) {
////            showAd();
////            return;
////        }
////        Context context = App.context;
////        final AdRequest adRequest = new AdRequest();
////        adRequest.addBanner(context)
////                .addFacebookNativeBannerAd(context)
////                .addAdmobNativeAd(context)
////                .addMopubNativeAd(context)
////                .addApplovinNativeAd(context)
////                .addDisplayIONativeAd(context);
////
////        if (mAdProvider == null) {
////            mAdProvider = AdProvider.newProvider("private_album_banner", AdConstant.AdVirtualUnitID.PLACEMENTID_NATIVE_GALLERY_FLOW, 0);
////            mAdProvider.setOnAdListener(new AdProvider.OnAdListener() {
////                @Override
////                public void onAdLoadFinish(String cacheKey) {
////                    super.onAdLoadFinish(cacheKey);
////
////
////                    showAd();
////                }
////
////                @Override
////                public void onAdLoadFailure(String err) {
////                    super.onAdLoadFailure(err);
////                }
////
////                @Override
////                public void onAdClicked(String cacheKey) {
////                    super.onAdClicked(cacheKey);
////                }
////
////                @Override
////                public void onAdImpression(String cacheKey) {
////                    super.onAdImpression(cacheKey);
////                }
////
////                @Override
////                public void onAdClosed(String cacheKey) {
////                    super.onAdClosed(cacheKey);
////                }
////            });
////        }
////        mAdProvider.loadAd(adRequest);
//    }
//
//    private void destroyBannerAd() {
//        if (mAdBeans != null) {
//            for (AdItemBean adBean : mAdBeans) {
//                adBean.destory();
//            }
//            mAdBeans.clear();
//        }
//
//        if (mAdProvider != null) {
//            mAdProvider.destory();
//            mAdProvider = null;
//        }
//    }
//
//    private void showAd() {
////        if (mContanerView == null) {
////            return;
////        }
////        RelativeLayout adContent = mContanerView.findViewById(R.id.ad_content);
////        if (adContent == null) {
////            return;
////        }
////        String adCacheKey = mAdProvider.getAdCacheKey();
////        AdItemBean adItemBean = new AdItemBean(adCacheKey);
////        FlowAdView flowAdView = FlowAdView.newEntranceAdView(null);
////        flowAdView.setAdLayoutId(R.layout.sc_layout_banner_inapp);
////        flowAdView.setData(adItemBean);
////        flowAdView.setTitleLayoutVisibility(View.GONE);
////        adContent.removeAllViews();
////        adContent.addView(flowAdView);
//    }
//}
